package com.sportlink.sportlink.codes;

import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class H2_CodesRepository implements I_CodesRepository{

    private final JPA_CodesRepository jpaRepository;


    public H2_CodesRepository(JPA_CodesRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public CodeData save(CodeData data) {
        return jpaRepository.save(data);
    }

    @Override
    public CodeData update(CodeData data) {
        return jpaRepository.save(data);
    }

    @Override
    public Optional<CodeData> findByCode(String qrCode) {
        return jpaRepository.findByQrCode(qrCode);
    }

    @Override
    public void delete(Long id) {
        jpaRepository.deleteById(id);
    }
}
