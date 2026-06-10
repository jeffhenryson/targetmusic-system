package com.targetmusic.core.domain.exception.os;

import com.targetmusic.core.domain.model.os.StatusOS;

public class TransicaoStatusInvalidaException extends RuntimeException {

    public TransicaoStatusInvalidaException(StatusOS de, StatusOS para) {
        super("Transição inválida: %s → %s".formatted(de, para));
    }
}
