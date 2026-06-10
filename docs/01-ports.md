# 01 — Ports — Target Music

Contratos de entrada e saída do núcleo de negócio (excluindo os ports do template de segurança, que estão em `docs/domain-model.md` herdado).

---

## Ports IN (Use Cases)

Definidos em `core/ports/in/`:

### ClienteUseCase

```java
interface ClienteUseCase {
    Cliente criar(String nome, String telefone, String email,
                  String cpf, String endereco, String observacoes);
    Cliente buscarPorId(Long id);
    PageResult<Cliente> listar(String search, int page, int size);
    Cliente atualizar(Long id, String nome, String telefone, String email,
                      String cpf, String endereco, String observacoes);
    void remover(Long id);
    void vincularUsuario(Long clienteId, Long userId);
}
```

### InstrumentoUseCase

```java
interface InstrumentoUseCase {
    Instrumento criar(TipoInstrumento tipo, String marca, String modelo,
                      Long clienteId, String numeroDeSerie, String cor, String descricao);
    Instrumento buscarPorId(Long id);
    List<Instrumento> listarPorCliente(Long clienteId);
    Instrumento atualizar(Long id, TipoInstrumento tipo, String marca, String modelo,
                          String numeroDeSerie, String cor, String descricao);
    void remover(Long id);
}
```

### OrdemDeServicoUseCase

```java
interface OrdemDeServicoUseCase {
    OrdemDeServico abrir(Long instrumentoId, Long clienteId,
                         String atendenteUsername, String descricaoProblema, String observacoes);
    OrdemDeServico buscarPorId(Long id);
    OrdemDeServico buscarPorNumero(String numero);
    PageResult<OrdemDeServico> listar(StatusOS status, Long clienteId,
                                      String tecnicoUsername, int page, int size);
    List<OrdemDeServico> listarPorCliente(Long clienteId);
    void atribuirTecnico(Long osId, String tecnicoUsername);
    void atualizarLaudo(Long osId, String laudoTecnico);
    void definirOrcamento(Long osId, BigDecimal valor, LocalDate prazoEstimado);
    void aprovarOrcamento(Long osId, String usuarioUsername);
    void recusarOrcamento(Long osId, String usuarioUsername, String observacao);
    void atualizarStatus(Long osId, StatusOS novoStatus, String usuarioUsername, String observacao);
    void registrarEntrega(Long osId, BigDecimal valorFinal, String usuarioUsername);
    void cancelar(Long osId, String usuarioUsername, String observacao);
    List<HistoricoOS> buscarHistorico(Long osId);
}
```

---

## Ports OUT (Repositórios / Infraestrutura)

Definidos em `core/ports/out/`:

### ClienteRepository

```java
interface ClienteRepository {
    Cliente save(Cliente cliente);
    Optional<Cliente> findById(Long id);
    Optional<Cliente> findByUserId(Long userId);
    PageResult<Cliente> findAll(String search, int page, int size);
    void deleteById(Long id);
    boolean hasOpenOS(Long clienteId);   // bloqueia deleção se true
}
```

### InstrumentoRepository

```java
interface InstrumentoRepository {
    Instrumento save(Instrumento instrumento);
    Optional<Instrumento> findById(Long id);
    List<Instrumento> findByClienteId(Long clienteId);
    void deleteById(Long id);
    boolean hasOpenOS(Long instrumentoId);   // bloqueia deleção se true
}
```

### OrdemDeServicoRepository

```java
interface OrdemDeServicoRepository {
    OrdemDeServico save(OrdemDeServico os);
    Optional<OrdemDeServico> findById(Long id);
    Optional<OrdemDeServico> findByNumero(String numero);
    PageResult<OrdemDeServico> findFiltered(StatusOS status, Long clienteId,
                                             String tecnicoUsername, int page, int size);
    List<OrdemDeServico> findByClienteId(Long clienteId);
    String generateNextNumero(int year);   // gera "OS-YYYY-NNNN" com seq atômica
}
```

### HistoricoOSRepository

```java
interface HistoricoOSRepository {
    void save(HistoricoOS historico);
    List<HistoricoOS> findByOsId(Long osId);
}
```

### OSNumeroSequencePort

```java
interface OSNumeroSequencePort {
    String next(int year);   // retorna próximo número formatado "OS-YYYY-NNNN"
}
```

Implementado via sequence PostgreSQL em hml/prod e contador H2 em dev.
