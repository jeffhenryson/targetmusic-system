# 02 — Modelo de Domínio — Target Music

Entidades do negócio em `core/domain/model/` (pacote `com.targetmusic`).

---

## TipoInstrumento (enum)

```java
enum TipoInstrumento {
    GUITARRA, VIOLAO, BAIXO, CONTRABAIXO,
    TECLADO, PIANO,
    BATERIA, PERCUSSAO,
    SOPRO, CORDA,
    OUTRO
}
```

---

## StatusOS (enum)

```java
enum StatusOS {
    RECEBIDO,              // instrumento entrou na oficina
    EM_ANALISE,            // técnico está diagnosticando
    AGUARDANDO_APROVACAO,  // orçamento enviado, aguardando resposta do cliente
    EM_MANUTENCAO,         // orçamento aprovado, reparo em andamento
    AGUARDANDO_PECA,       // aguardando peça para continuar
    PRONTO,                // reparo concluído, aguardando retirada
    ENTREGUE,              // cliente retirou o instrumento
    CANCELADO              // OS encerrada sem conclusão do reparo
}
```

---

## Cliente

```java
class Cliente {
    Long id
    String nome
    String telefone
    String email           // nullable
    String cpf             // nullable
    String endereco        // nullable
    String observacoes     // nullable
    Long userId            // nullable — vinculado a User com ROLE_CLIENTE
    Instant createdAt

    static Cliente of(nome, telefone)
    static Cliente fromPersisted(id, nome, telefone, email, cpf, endereco, observacoes, userId, createdAt)
}
```

---

## Instrumento

```java
class Instrumento {
    Long id
    TipoInstrumento tipo
    String marca
    String modelo
    String numeroDeSerie   // nullable
    String cor             // nullable
    String descricao       // nullable — condição, detalhes, acessórios
    Long clienteId
    Instant createdAt

    static Instrumento of(tipo, marca, modelo, clienteId)
    static Instrumento fromPersisted(id, tipo, marca, modelo, numeroDeSerie, cor, descricao, clienteId, createdAt)
}
```

---

## OrdemDeServico

```java
class OrdemDeServico {
    Long id
    String numero              // gerado: "OS-YYYY-NNNN" com sequência global (ex: OS-2026-0001)
    StatusOS status
    Long instrumentoId
    Long clienteId
    String atendenteUsername   // quem abriu a OS
    Set<String> tecnicosUsernames  // técnicos atribuídos — múltiplos, gerenciados via os_tecnicos
    String descricaoProblema   // relatado pelo cliente no recebimento
    String laudoTecnico        // nullable — diagnóstico técnico
    BigDecimal valorOrcamento  // nullable — valor proposto antes da aprovação
    BigDecimal valorFinal      // nullable — valor cobrado na entrega
    LocalDate prazoEstimado    // nullable
    Instant dataRecebimento
    Instant dataEntrega        // nullable — preenchido ao mudar para ENTREGUE
    String observacoes         // nullable
    Instant createdAt
    Instant updatedAt

    static OrdemDeServico abrir(numero, instrumentoId, clienteId, atendenteUsername, descricaoProblema)
    static OrdemDeServico fromPersisted(...)

    // operações de domínio
    void adicionarTecnico(tecnicoUsername)
    void removerTecnico(tecnicoUsername)
    void atualizarLaudo(laudoTecnico)
    void definirOrcamento(valor)
    void definirPrazo(prazoEstimado)
    void mudarStatus(novoStatus)   // valida via StatusOS.transicaoValida() → lança TransicaoStatusInvalidaException
    void registrarEntrega()        // dataEntrega = now, status = ENTREGUE
    void definirValorFinal(valor)
}
```

---

## Peca

```java
class Peca {
    Long id
    String nome
    String descricao           // nullable
    int quantidadeEstoque      // nunca negativo — lança EstoqueInsuficienteException se tentar
    BigDecimal precoUnitario
    boolean ativo              // false = desativada (soft delete — OS existentes mantêm referência)
    Instant createdAt
    Instant updatedAt

    static Peca criar(nome, precoUnitario)
    static Peca fromPersisted(...)

    void darEntrada(quantidade)       // quantidadeEstoque += quantidade
    void darSaida(quantidade)         // quantidadeEstoque -= quantidade; lança se < 0
    void atualizarPreco(novoPreco)
    void desativar()
}
```

---

## OSPeca (peças utilizadas em uma OS)

```java
record OSPeca(
    Long id,
    Long osId,
    Long pecaId,
    String pecaNome,              // snapshot do nome — preserva histórico se peça for renomeada
    int quantidade,
    BigDecimal precoUnitario,     // snapshot do preço no momento do uso
    String tecnicoUsername,       // quem adicionou a peça à OS
    Instant addedAt
) {
    BigDecimal subtotal()  // quantidade * precoUnitario
}
```

---

## HistoricoOS

```java
record HistoricoOS(
    Long id,
    Long osId,
    StatusOS statusAnterior,   // null = criação
    StatusOS statusNovo,
    String usuarioUsername,
    String observacao,         // nullable
    Instant timestamp
)
```

---

## Ports IN — contratos de use case

### ClienteUseCase

| Método | Descrição |
|--------|-----------|
| `Cliente criar(nome, telefone, email, cpf, endereco, observacoes)` | Cria cliente |
| `Cliente buscarPorId(id)` | Busca por ID |
| `PageResult<Cliente> listar(search, page, size)` | Listagem paginada com busca por nome/email/telefone |
| `Cliente atualizar(id, nome, telefone, email, cpf, endereco, observacoes)` | Atualiza dados |
| `void remover(id)` | Remove cliente (bloqueado se tiver OS em aberto) |
| `void vincularUsuario(clienteId, userId)` | Vincula conta User com ROLE_CLIENTE |

### InstrumentoUseCase

| Método | Descrição |
|--------|-----------|
| `Instrumento criar(tipo, marca, modelo, clienteId, numeroDeSerie, cor, descricao)` | Cria instrumento |
| `Instrumento buscarPorId(id)` | Busca por ID |
| `List<Instrumento> listarPorCliente(clienteId)` | Todos os instrumentos de um cliente |
| `Instrumento atualizar(id, tipo, marca, modelo, numeroDeSerie, cor, descricao)` | Atualiza |
| `void remover(id)` | Remove (bloqueado se tiver OS em aberto) |

### OrdemDeServicoUseCase

| Método | Descrição |
|--------|-----------|
| `OrdemDeServico abrir(instrumentoId, clienteId, atendenteUsername, descricaoProblema, observacoes)` | Abre nova OS com status RECEBIDO |
| `OrdemDeServico buscarPorId(id)` | Busca por ID |
| `OrdemDeServico buscarPorNumero(numero)` | Busca pelo número humano (OS-YYYY-NNNN) |
| `PageResult<OrdemDeServico> listar(status, clienteId, tecnicoUsername, page, size)` | Listagem filtrada |
| `List<OrdemDeServico> listarPorCliente(clienteId)` | Todas as OS de um cliente |
| `void adicionarTecnico(osId, tecnicoUsername)` | Adiciona técnico à OS |
| `void removerTecnico(osId, tecnicoUsername)` | Remove técnico da OS |
| `void atualizarLaudo(osId, laudoTecnico)` | Técnico registra diagnóstico |
| `void definirOrcamento(osId, valor, prazoEstimado)` | Define orçamento e muda para AGUARDANDO_APROVACAO |
| `void aprovarOrcamento(osId)` | Muda para EM_MANUTENCAO |
| `void recusarOrcamento(osId, observacao)` | Muda para CANCELADO |
| `void atualizarStatus(osId, novoStatus, usuarioUsername, observacao)` | Transição de status genérica |
| `void registrarEntrega(osId, valorFinal)` | Muda para ENTREGUE, registra valor final e data |
| `void cancelar(osId, observacao)` | Cancela a OS |
| `List<HistoricoOS> buscarHistorico(osId)` | Histórico de mudanças de status |

### PecaUseCase

| Método | Descrição |
|--------|-----------|
| `Peca criar(nome, descricao, precoUnitario, quantidadeInicial)` | Cadastra peça no estoque |
| `Peca buscarPorId(id)` | Busca por ID |
| `PageResult<Peca> listar(search, apenasAtivas, page, size)` | Listagem paginada |
| `Peca atualizar(id, nome, descricao, precoUnitario)` | Atualiza dados da peça |
| `void darEntrada(id, quantidade)` | Adiciona ao estoque |
| `void desativar(id)` | Soft-delete |
| `OSPeca adicionarNaOS(osId, pecaId, quantidade, tecnicoUsername)` | Reserva peça para uma OS (debita estoque) |
| `void removerDaOS(osId, osPecaId)` | Remove peça da OS (devolve ao estoque) |
| `List<OSPeca> listarPecasDaOS(osId)` | Peças utilizadas em uma OS |
