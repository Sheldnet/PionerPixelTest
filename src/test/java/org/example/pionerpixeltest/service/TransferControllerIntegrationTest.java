package org.example.pionerpixeltest.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.pionerpixeltest.api.dto.TransferRequest;
import org.example.pionerpixeltest.dao.AccountRepository;
import org.example.pionerpixeltest.dao.EmailDataRepository;
import org.example.pionerpixeltest.dao.PhoneDataRepository;
import org.example.pionerpixeltest.dao.UserRepository;
import org.example.pionerpixeltest.entity.Account;
import org.example.pionerpixeltest.entity.EmailData;
import org.example.pionerpixeltest.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
class TransferControllerIntegrationTest {

    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("test-db")
            .withUsername("user")
            .withPassword("pass");

    static {
        postgres.start();
    }

    @DynamicPropertySource
    static void overrideProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private EmailDataRepository emailDataRepository;
    @Autowired
    private PhoneDataRepository phoneDataRepository;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AccountRepository accountRepository;

    private Long fromUserId;
    private Long toUserId;


    @BeforeEach
    void initSecurityContext() {
        org.springframework.security.core.userdetails.User springUser =
                new org.springframework.security.core.userdetails.User(
                        "1", "pass", List.of(new SimpleGrantedAuthority("ROLE_USER")));
        TestingAuthenticationToken auth = new TestingAuthenticationToken(springUser, null, springUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @BeforeEach
    void initData() {
        emailDataRepository.deleteAll();
        phoneDataRepository.deleteAll();
        accountRepository.deleteAll();
        userRepository.deleteAll();

        User user1 = new User();
        user1.setName("Alice");
        user1.setPassword("pass");
        user1.setDateOfBirth(LocalDate.of(1990, 1, 1));

        EmailData email1 = new EmailData();
        email1.setEmail("123");

        user1.setEmails(new HashSet<>());
        user1.getEmails().add(email1);
        email1.setUser(user1);

        User user2 = new User();
        user2.setName("Bob");
        user2.setPassword("pass");
        user2.setDateOfBirth(LocalDate.of(1985, 6, 15));

        EmailData email2 = new EmailData();
        email2.setEmail("321");

        user2.setEmails(new HashSet<>());
        user2.getEmails().add(email2);
        email2.setUser(user2);

        user1 = userRepository.save(user1);
        user2 = userRepository.save(user2);

        accountRepository.save(new Account(null, user1, new BigDecimal("300")));
        accountRepository.save(new Account(null, user2, new BigDecimal("100")));

        fromUserId = user1.getId();
        toUserId = user2.getId();
    }

    @Test
    void shouldTransferMoney() throws Exception {
        TransferRequest request = TransferRequest.builder()
                .toUserId(toUserId)
                .amount(new BigDecimal("50"))
                .build();

        mockMvc.perform(post("/api/transfer/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());

        assertBalances();
    }

    private void assertBalances() {
        BigDecimal fromBalance = accountRepository.findByUserId(fromUserId).orElseThrow().getBalance();
        BigDecimal toBalance   = accountRepository.findByUserId(toUserId).orElseThrow().getBalance();

        assertThat(fromBalance).isEqualByComparingTo("250");
        assertThat(toBalance).isEqualByComparingTo("150");
    }

}

