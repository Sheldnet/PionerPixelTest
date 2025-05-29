package org.example.pionerpixeltest.api.dto;

import lombok.*;
import java.time.Instant;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorResponce {
    private Instant timestamp;
    private int status;
    private String message;

}
