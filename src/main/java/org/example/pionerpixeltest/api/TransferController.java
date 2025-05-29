package org.example.pionerpixeltest.api;

import lombok.RequiredArgsConstructor;
import org.example.pionerpixeltest.api.dto.TransferRequest;
import org.example.pionerpixeltest.service.TransferService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/transfer")
public class TransferController {
    private final TransferService transferService;

    @PostMapping("/transfer")
    public ResponseEntity<Void> transfer(@AuthenticationPrincipal User principal,
                                         @RequestBody TransferRequest req) {
        Long fromId = Long.parseLong(String.valueOf(principal.getUsername()));
        transferService.transfer(fromId, req.getToUserId(), req.getAmount());
        return ResponseEntity.noContent().build();
    }


}
