package org.example.pionerpixeltest.service.iml;

import lombok.RequiredArgsConstructor;
import org.example.pionerpixeltest.api.dto.RegisterRequest;
import org.example.pionerpixeltest.api.dto.UserDto;
import org.example.pionerpixeltest.api.mapper.UserMapper;
import org.example.pionerpixeltest.dao.AccountRepository;
import org.example.pionerpixeltest.dao.EmailDataRepository;
import org.example.pionerpixeltest.dao.PhoneDataRepository;
import org.example.pionerpixeltest.dao.UserRepository;
import org.example.pionerpixeltest.entity.Account;
import org.example.pionerpixeltest.entity.EmailData;
import org.example.pionerpixeltest.entity.PhoneData;
import org.example.pionerpixeltest.entity.User;
import org.example.pionerpixeltest.service.AccountService;
import org.example.pionerpixeltest.service.UserFilter;
import org.example.pionerpixeltest.service.UserService;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = "users")
public class UserServiceImpl implements UserService {

    private final UserRepository userRepo;
    private final EmailDataRepository emailRepo;
    private final PhoneDataRepository phoneRepo;
    private final UserMapper mapper;
    private final PasswordEncoder passwordEncoder;
    private final AccountService accountService;
    private final AccountRepository accountRepo;

    @Override
    @Cacheable(key = "'filter:' + #filter.cacheKey() + ':' + #pageable.pageNumber")
    @Transactional(readOnly = true)
    public Page<UserDto> search(UserFilter filter, Pageable pageable) {

        Page<User> page = userRepo.findAll((root, q, cb) -> {
            var p = cb.conjunction();

            if (filter.dateOfBirthAfter() != null) {
                p = cb.and(p,
                        cb.greaterThan(root.get("dateOfBirth"),
                                filter.dateOfBirthAfter()));
            }
            if (filter.namePrefix() != null) {
                p = cb.and(p,
                        cb.like(cb.lower(root.get("name")),
                                filter.namePrefix().toLowerCase() + "%"));
            }
            if (filter.phoneExact() != null) {
                p = cb.and(p,
                        cb.equal(root.join("phones").get("phone"),
                                filter.phoneExact()));
            }
            if (filter.emailExact() != null) {
                p = cb.and(p,
                        cb.equal(root.join("emails").get("email"),
                                filter.emailExact().toLowerCase()));
            }
            return p;
        }, pageable);

        return page.map(mapper::toDto);
    }

    @Override
    @Cacheable(key = "'id:' + #id")
    @Transactional(readOnly = true)
    public UserDto getById(Long id) {
        return userRepo.findById(id)
                .map(mapper::toDto)
                .orElseThrow(() -> new NoSuchElementException("Пользователь не найден"));
    }


    @Override
    @Caching(evict = { @CacheEvict(key = "'id:' + #userId"),
            @CacheEvict(key = "'filter:*'", allEntries = true) })
    @Transactional
    public void updateEmails(Long userId, Set<String> newEmails,
                             UserDetails me) {

        checkSelfEdit(userId, me);

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("Пользователь не найден"));

        Set<String> existing = user.getEmails().stream()
                .map(EmailData::getEmail)
                .collect(Collectors.toSet());

        newEmails.stream()
                .filter(e -> !existing.contains(e))
                .forEach(e -> addEmail(userId, e));

        existing.stream()
                .filter(e -> !newEmails.contains(e))
                .forEach(e -> {
                    EmailData entity = emailRepo.findByEmailIgnoreCase(e)
                            .orElseThrow();
                    deleteEmail(userId, entity.getId());
                });
    }


    @Override
    @Caching(evict = { @CacheEvict(key = "'id:' + #userId"),
            @CacheEvict(key = "'filter:*'", allEntries = true) })
    @Transactional
    public void updatePhones(Long userId, Set<String> newPhones,
                             UserDetails me) {

        checkSelfEdit(userId, me);

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("Пользователь не найден"));

        Set<String> existing = user.getPhones().stream()
                .map(PhoneData::getPhone)
                .collect(Collectors.toSet());

        newPhones.stream()
                .filter(p -> !existing.contains(p))
                .forEach(p -> addPhone(userId, p));

        existing.stream()
                .filter(p -> !newPhones.contains(p))
                .forEach(p -> {
                    PhoneData entity = phoneRepo.findByPhone(p).orElseThrow();
                    deletePhone(userId, entity.getId());
                });
    }

    @Override
    @Transactional
    public void register(RegisterRequest rq) {

        if (emailRepo.existsByEmailIgnoreCaseIn(rq.getEmails()))
            throw new IllegalArgumentException("E-mail уже занят");
        if (phoneRepo.existsByPhoneIn(rq.getPhones()))
            throw new IllegalArgumentException("Телефон уже занят");

        User user = new User();
        user.setName(rq.getName());
        user.setPassword(passwordEncoder.encode(rq.getPassword()));
        user.setDateOfBirth(rq.getDateOfBirth());

        Set<EmailData> emails = rq.getEmails().stream()
                .map(e -> { EmailData ed = new EmailData();
                    ed.setEmail(e.toLowerCase()); ed.setUser(user); return ed; })
                .collect(Collectors.toSet());

        Set<PhoneData> phones = rq.getPhones().stream()
                .map(p -> { PhoneData pd = new PhoneData();
                    pd.setPhone(p); pd.setUser(user); return pd; })
                .collect(Collectors.toSet());

        user.setEmails(emails);
        user.setPhones(phones);

        Account acc = new Account();
        acc.setUser(user);
        acc.setBalance(rq.getInitialBalance());
        user.setAccount(acc);

        userRepo.save(user);

        accountService.registerAccount(acc);
    }

    private static void checkSelfEdit(Long userId, UserDetails me) {
        Long meId = Long.parseLong(me.getUsername());
        if (!meId.equals(userId)) {
            throw new IllegalStateException("Нельзя менять данные другого пользователя");
        }
    }

    @Caching(evict = { @CacheEvict(key = "'id:' + #userId"),
            @CacheEvict(key = "'filter:*'", allEntries = true) })
    @Transactional
    public void addEmail(Long userId, String email) {
        if (emailRepo.existsByEmailIgnoreCase(email)) {
            throw new IllegalArgumentException("E-mail уже занят");
        }
        User user = userRepo.getReferenceById(userId);
        EmailData data = new EmailData();
        data.setEmail(email.toLowerCase());
        data.setUser(user);
        emailRepo.save(data);
    }

    @Caching(evict = { @CacheEvict(key = "'id:' + #userId"),
            @CacheEvict(key = "'filter:*'", allEntries = true) })
    @Transactional
    public void deleteEmail(Long userId, Long emailId) {
        EmailData data = emailRepo.findById(emailId)
                .orElseThrow(() -> new NoSuchElementException("E-mail не найден"));
        if (!data.getUser().getId().equals(userId)) {
            throw new IllegalStateException("Чужие e-mail’ы удалять нельзя");
        }
        if (data.getUser().getEmails().size() == 1) {
            throw new IllegalStateException("Должен остаться минимум один e-mail");
        }
        emailRepo.delete(data);
    }

    @Caching(evict = { @CacheEvict(key = "'id:' + #userId"),
            @CacheEvict(key = "'filter:*'", allEntries = true) })
    @Transactional
    public void addPhone(Long userId, String phone) {
        if (phoneRepo.existsByPhone(phone)) {
            throw new IllegalArgumentException("Телефон уже занят");
        }
        User user = userRepo.getReferenceById(userId);
        PhoneData p = new PhoneData();
        p.setPhone(phone);
        p.setUser(user);
        phoneRepo.save(p);
    }

    @Caching(evict = { @CacheEvict(key = "'id:' + #userId"),
            @CacheEvict(key = "'filter:*'", allEntries = true) })
    @Transactional
    public void deletePhone(Long userId, Long phoneId) {
        PhoneData p = phoneRepo.findById(phoneId)
                .orElseThrow(() -> new NoSuchElementException("Телефон не найден"));
        if (!p.getUser().getId().equals(userId)) {
            throw new IllegalStateException("Чужие телефоны удалять нельзя");
        }
        if (p.getUser().getPhones().size() == 1) {
            throw new IllegalStateException("Должен остаться минимум один телефон");
        }
        phoneRepo.delete(p);
    }

}
