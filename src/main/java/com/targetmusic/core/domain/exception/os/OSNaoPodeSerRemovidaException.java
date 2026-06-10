package com.targetmusic.core.domain.exception.os;

import com.targetmusic.core.domain.model.os.StatusOS;

public class OSNaoPodeSerRemovidaException extends RuntimeException {

    public OSNaoPodeSerRemovidaException(Long id, StatusOS status) {
        super("OS id=%d não pode ser removida no status %s".formatted(id, status));
    }
}
