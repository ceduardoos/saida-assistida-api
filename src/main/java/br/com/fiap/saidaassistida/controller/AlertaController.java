package br.com.fiap.saidaassistida.controller;

import br.com.fiap.saidaassistida.dao.AlertaDao;
import br.com.fiap.saidaassistida.dao.AtendimentoDao;
import br.com.fiap.saidaassistida.dao.MoradorDao;
import br.com.fiap.saidaassistida.dao.SetorDao;
import br.com.fiap.saidaassistida.dto.*;
import br.com.fiap.saidaassistida.exception.EntidadeNaoEncontradaException;
import br.com.fiap.saidaassistida.exception.RegraDeNegocioException;
import br.com.fiap.saidaassistida.model.Alerta;
import br.com.fiap.saidaassistida.model.Atendimento;
import br.com.fiap.saidaassistida.model.Morador;
import br.com.fiap.saidaassistida.service.AtendimentoService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/alertas")
public class AlertaController {

    private final AlertaDao dao;
    private final SetorDao setorDao;
    private final AtendimentoDao atendimentoDao;
    private final MoradorDao moradorDao;
    private final AtendimentoService service;
    private final ModelMapper mapper;

    public AlertaController(AlertaDao dao, SetorDao setorDao, AtendimentoDao atendimentoDao, MoradorDao moradorDao,
                            AtendimentoService service, ModelMapper mapper) {
        this.dao = dao;
        this.setorDao = setorDao;
        this.atendimentoDao = atendimentoDao;
        this.moradorDao = moradorDao;
        this.service = service;
        this.mapper = mapper;
    }

    @PostMapping
    public ResponseEntity<AlertaResponseDto> cadastrar(@Valid @RequestBody AlertaRequestDto dto, UriComponentsBuilder builder)
            throws SQLException, EntidadeNaoEncontradaException {
        Alerta alerta = montar(dto);
        dao.cadastrar(alerta);
        URI uri = builder.path("/alertas/{id}").buildAndExpand(alerta.getId()).toUri();
        return ResponseEntity.created(uri).body(mapper.map(alerta, AlertaResponseDto.class));
    }

    @GetMapping
    public List<AlertaResponseDto> listar() throws SQLException, EntidadeNaoEncontradaException {
        List<AlertaResponseDto> lista = new ArrayList<>();
        for (Alerta a : dao.listar()) lista.add(mapper.map(a, AlertaResponseDto.class));
        return lista;
    }

    @GetMapping("/{id}")
    public AlertaResponseDto pesquisarPorId(@PathVariable int id) throws SQLException, EntidadeNaoEncontradaException {
        return mapper.map(dao.pesquisarPorId(id), AlertaResponseDto.class);
    }

    @PutMapping("/{id}")
    public AlertaResponseDto atualizar(@PathVariable int id, @Valid @RequestBody AlertaRequestDto dto)
            throws SQLException, EntidadeNaoEncontradaException {
        Alerta alerta = montar(dto);
        alerta.setId(id);
        dao.atualizar(alerta);
        return mapper.map(alerta, AlertaResponseDto.class);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable int id) throws SQLException, EntidadeNaoEncontradaException {
        dao.remover(id);   // alerta com atendimentos -> 409 pelo handler
        return ResponseEntity.noContent().build();
    }

    // ---- Rota aninhada do 1:N Alerta -> Atendimento ----

    @GetMapping("/{id}/atendimentos")
    public List<AtendimentoResponseDto> listarAtendimentos(@PathVariable int id) throws SQLException, EntidadeNaoEncontradaException {
        dao.pesquisarPorId(id);
        List<AtendimentoResponseDto> lista = new ArrayList<>();
        for (Atendimento at : atendimentoDao.listarPorAlerta(id)) lista.add(mapper.map(at, AtendimentoResponseDto.class));
        return lista;
    }

    @PostMapping("/{id}/atendimentos")
    public ResponseEntity<AtendimentoResponseDto> incluirAtendimento(@PathVariable int id, @Valid @RequestBody AtendimentoRequestDto dto,
                                                                    UriComponentsBuilder builder)
            throws SQLException, EntidadeNaoEncontradaException, RegraDeNegocioException {
        Atendimento at = service.incluir(id, dto.getIdMorador(), dto.getMotivoPendencia());
        URI uri = builder.path("/atendimentos/{id}").buildAndExpand(at.getId()).toUri();
        return ResponseEntity.created(uri).body(mapper.map(at, AtendimentoResponseDto.class));
    }

    // Moradores ativos do setor que ainda nao foram incluidos neste alerta (fila do painel /operacao)
    @GetMapping("/{id}/elegiveis")
    public List<MoradorResponseDto> listarElegiveis(@PathVariable int id) throws SQLException, EntidadeNaoEncontradaException {
        Alerta alerta = dao.pesquisarPorId(id);
        List<MoradorResponseDto> lista = new ArrayList<>();
        for (Morador m : moradorDao.listarElegiveis(alerta.getSetor().getId(), id))
            lista.add(mapper.map(m, MoradorResponseDto.class));
        return lista;
    }

    private Alerta montar(AlertaRequestDto dto) throws SQLException, EntidadeNaoEncontradaException {
        Alerta alerta = new Alerta();
        alerta.setSetor(setorDao.pesquisarPorId(dto.getIdSetor()));
        alerta.setNivel(dto.getNivel());
        alerta.setDataAbertura(dto.getDataAbertura() != null ? dto.getDataAbertura() : LocalDateTime.now());
        alerta.setDataEncerramento(dto.getDataEncerramento());
        alerta.setSituacao(dto.getSituacao() != null ? dto.getSituacao() : "ABERTO");
        return alerta;
    }
}
