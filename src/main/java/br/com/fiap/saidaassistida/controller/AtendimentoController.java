package br.com.fiap.saidaassistida.controller;

import br.com.fiap.saidaassistida.dao.AtendimentoDao;
import br.com.fiap.saidaassistida.dto.*;
import br.com.fiap.saidaassistida.exception.DadosInvalidosException;
import br.com.fiap.saidaassistida.exception.EntidadeNaoEncontradaException;
import br.com.fiap.saidaassistida.exception.RegraDeNegocioException;
import br.com.fiap.saidaassistida.model.Atendimento;
import br.com.fiap.saidaassistida.service.AtendimentoService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/atendimentos")
public class AtendimentoController {

    private final AtendimentoDao dao;
    private final AtendimentoService service;
    private final ModelMapper mapper;

    public AtendimentoController(AtendimentoDao dao, AtendimentoService service, ModelMapper mapper) {
        this.dao = dao;
        this.service = service;
        this.mapper = mapper;
    }

    @PostMapping
    public ResponseEntity<AtendimentoResponseDto> cadastrar(@Valid @RequestBody AtendimentoRequestDto dto, UriComponentsBuilder builder)
            throws SQLException, EntidadeNaoEncontradaException, RegraDeNegocioException, DadosInvalidosException {
        if (dto.getIdAlerta() <= 0) throw new DadosInvalidosException("idAlerta deve ser informado");
        Atendimento at = service.incluir(dto.getIdAlerta(), dto.getIdMorador(), dto.getMotivoPendencia());
        URI uri = builder.path("/atendimentos/{id}").buildAndExpand(at.getId()).toUri();
        return ResponseEntity.created(uri).body(mapper.map(at, AtendimentoResponseDto.class));
    }

    @GetMapping
    public List<AtendimentoResponseDto> listar() throws SQLException, EntidadeNaoEncontradaException {
        List<AtendimentoResponseDto> lista = new ArrayList<>();
        for (Atendimento at : dao.listar()) lista.add(mapper.map(at, AtendimentoResponseDto.class));
        return lista;
    }

    @GetMapping("/{id}")
    public AtendimentoResponseDto pesquisarPorId(@PathVariable int id) throws SQLException, EntidadeNaoEncontradaException {
        return mapper.map(dao.pesquisarPorId(id), AtendimentoResponseDto.class);
    }

    // PUT generico do CRUD: aqui so o motivo de pendencia e editavel; estado e recursos mudam pelas rotas de regra
    @PutMapping("/{id}")
    public AtendimentoResponseDto atualizar(@PathVariable int id, @Valid @RequestBody AtendimentoRequestDto dto)
            throws SQLException, EntidadeNaoEncontradaException {
        Atendimento at = dao.pesquisarPorId(id);
        at.setMotivoPendencia(dto.getMotivoPendencia());
        dao.atualizar(at);
        return mapper.map(at, AtendimentoResponseDto.class);
    }

    // Regra de negocio: designar veiculo + destino + responsavel (RN02-RN04 -> 409)
    @PutMapping("/{id}/designacao")
    public AtendimentoResponseDto designar(@PathVariable int id, @Valid @RequestBody DesignacaoRequestDto dto)
            throws SQLException, EntidadeNaoEncontradaException, RegraDeNegocioException {
        Atendimento at = service.designar(id, dto.getIdVeiculo(), dto.getIdDestino(), dto.getResponsavel());
        return mapper.map(at, AtendimentoResponseDto.class);
    }

    // Marcos: EQUIPE_A_CAMINHO, EMBARCADA, CHEGADA_CONFIRMADA, RECUSADO, PENDENCIA (RN05, RN07 -> 409)
    @PutMapping("/{id}/marcos")
    public AtendimentoResponseDto registrarMarco(@PathVariable int id, @Valid @RequestBody MarcoRequestDto dto)
            throws SQLException, EntidadeNaoEncontradaException, RegraDeNegocioException {
        Atendimento at = service.registrarMarco(id, dto.getMarco(), dto.getInstante(), dto.getMotivoPendencia());
        return mapper.map(at, AtendimentoResponseDto.class);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable int id)
            throws SQLException, EntidadeNaoEncontradaException, RegraDeNegocioException {
        Atendimento at = dao.pesquisarPorId(id);
        if (!Atendimento.AGUARDANDO.equals(at.getSituacao()))
            throw new RegraDeNegocioException("So e possivel remover atendimento ainda AGUARDANDO; use RECUSADO para encerrar");
        dao.remover(id);
        return ResponseEntity.noContent().build();
    }
}
