package com.sportlink.sportlink.transfer;

import com.sportlink.sportlink.utils.DTO_Adapter;

public class TransferService {

    private final I_TransferRepository transferRepository;
    private final DTO_Adapter adapter;

    public TransferService(I_TransferRepository transferRepository, DTO_Adapter adapter) {
        this.transferRepository = transferRepository;
        this.adapter = adapter;
    }

    public DTO_Transfer save(Transfer transfer) {
        return adapter.getDTO_Transfer( transferRepository.saveTransfer(transfer) );
    }
}
