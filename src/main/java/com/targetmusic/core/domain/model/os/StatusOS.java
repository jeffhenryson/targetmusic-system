package com.targetmusic.core.domain.model.os;

public enum StatusOS {
    RECEBIDO,
    EM_ANALISE,
    AGUARDANDO_APROVACAO,
    EM_MANUTENCAO,
    AGUARDANDO_PECA,
    PRONTO,
    ENTREGUE,
    CANCELADO;

    public static boolean transicaoValida(StatusOS de, StatusOS para) {
        return switch (de) {
            case RECEBIDO             -> para == EM_ANALISE || para == CANCELADO;
            case EM_ANALISE           -> para == AGUARDANDO_APROVACAO || para == CANCELADO;
            case AGUARDANDO_APROVACAO -> para == EM_MANUTENCAO || para == CANCELADO;
            case EM_MANUTENCAO        -> para == AGUARDANDO_PECA || para == PRONTO || para == CANCELADO;
            case AGUARDANDO_PECA      -> para == EM_MANUTENCAO || para == CANCELADO;
            case PRONTO               -> para == ENTREGUE || para == EM_MANUTENCAO;
            case ENTREGUE, CANCELADO  -> false;
        };
    }
}
