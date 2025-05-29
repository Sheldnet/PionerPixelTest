package org.example.pionerpixeltest.api.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransferRequest {
    private Long toUserId;
    private BigDecimal amount;

}
