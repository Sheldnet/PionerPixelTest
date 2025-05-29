package org.example.pionerpixeltest.api.dto;

import lombok.*;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PhoneUpdateRequest {
    private Set<String> phones;

}
