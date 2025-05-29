package org.example.pionerpixeltest.api.dto;

import lombok.*;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailUpdateRequest {
    private Set<String> emails;

}
