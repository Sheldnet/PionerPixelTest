package org.example.pionerpixeltest.api.mapper;

import org.example.pionerpixeltest.api.dto.UserDto;
import org.example.pionerpixeltest.entity.EmailData;
import org.example.pionerpixeltest.entity.PhoneData;
import org.example.pionerpixeltest.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Set;
import java.util.stream.Collectors;


@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(source = "account.balance", target = "balance")
    @Mapping(target = "emails", expression = "java(mapEmails(user.getEmails()))")
    @Mapping(target = "phones", expression = "java(mapPhones(user.getPhones()))")
    UserDto toDto(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "account", ignore = true)
    @Mapping(target = "emails", expression = "java(toEmailEntities(dto.getEmails()))")
    @Mapping(target = "phones", expression = "java(toPhoneEntities(dto.getPhones()))")
    User toEntity(UserDto dto);

    default Set<String> mapEmails(Set<EmailData> emails) {
        return emails == null ? null : emails.stream()
                .map(EmailData::getEmail)
                .collect(Collectors.toSet());
    }

    default Set<String> mapPhones(Set<PhoneData> phones) {
        return phones == null ? null : phones.stream()
                .map(PhoneData::getPhone)
                .collect(Collectors.toSet());
    }

    default Set<EmailData> toEmailEntities(Set<String> emails) {
        return emails == null ? null : emails.stream().map(email -> {
            EmailData e = new EmailData();
            e.setEmail(email);
            return e;
        }).collect(Collectors.toSet());
    }

    default Set<PhoneData> toPhoneEntities(Set<String> phones) {
        return phones == null ? null : phones.stream().map(phone -> {
            PhoneData p = new PhoneData();
            p.setPhone(phone);
            return p;
        }).collect(Collectors.toSet());
    }

}
