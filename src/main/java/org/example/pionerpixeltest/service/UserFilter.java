package org.example.pionerpixeltest.service;

import java.time.LocalDate;

public record UserFilter(
        LocalDate dateOfBirthAfter,
        String    namePrefix,
        String    emailExact,
        String    phoneExact) {

    public String cacheKey() {
        return  (dateOfBirthAfter != null ? "dob>" + dateOfBirthAfter : "") + "|" +
                (namePrefix       != null ? "n:" + namePrefix         : "") + "|" +
                (emailExact       != null ? "e:" + emailExact         : "") + "|" +
                (phoneExact       != null ? "p:" + phoneExact         : "");
    }
}
