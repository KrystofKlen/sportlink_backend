package com.sportlink.sportlink.transfer;

import com.sportlink.sportlink.utils.DTO_Adapter;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class TransferService {

    private final I_TransferRepository i_TransferRepository;
    private final DTO_Adapter adapter;

    public Page<DTO_Transfer> getUsersTransfers(int page, int size, Long userId) {
        Pageable pageable = PageRequest.of(page, size);
        return i_TransferRepository.findAllByUserId(pageable, userId).map(adapter::getDTO_Transfer);
    }
}
