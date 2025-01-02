package com.sportlink.sportlink.codes;

import java.util.Optional;

public interface I_CodesRepository {
    CodeData save(CodeData data);
    CodeData update(CodeData data);
    Optional<CodeData> findByCode(String qrCode);
    void delete(Long id);
}
