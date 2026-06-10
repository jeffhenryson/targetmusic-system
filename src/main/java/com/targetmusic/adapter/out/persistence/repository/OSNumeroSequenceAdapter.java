package com.targetmusic.adapter.out.persistence.repository;

import com.targetmusic.core.ports.out.os.OSNumeroSequencePort;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Component
public class OSNumeroSequenceAdapter implements OSNumeroSequencePort {

    private final EntityManager em;

    public OSNumeroSequenceAdapter(EntityManager em) {
        this.em = em;
    }

    @Override
    @Transactional
    public String nextNumero() {
        Long seq = (Long) em.createNativeQuery("SELECT nextval('os_numero_seq')").getSingleResult();
        int year = LocalDate.now().getYear();
        return "OS-%d-%04d".formatted(year, seq);
    }
}
