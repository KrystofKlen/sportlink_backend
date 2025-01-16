package com.sportlink.sportlink.transfer;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/transfer")
public class TransferController {

    private final TransferService transferService;

    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }

    @GetMapping("/users-transfers")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Page<DTO_Transfer>> getUsersTransfers(@RequestParam(defaultValue = "0") int page,
                                                                @RequestParam(defaultValue = "10") int size) {
        Long uderAccountId = 1L;
        Page<DTO_Transfer> result = transferService.getUsersTransfers(page, size, uderAccountId);
        return ResponseEntity.ok(result);
    }
}
