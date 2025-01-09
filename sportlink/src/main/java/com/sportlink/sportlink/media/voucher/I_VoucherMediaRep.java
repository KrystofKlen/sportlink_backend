package com.sportlink.sportlink.media.voucher;

import java.util.Optional;

public interface I_VoucherMediaRep {
    Optional<VoucherMedia> findById(Long id);

    VoucherMedia save(VoucherMedia voucherMedia);
}
