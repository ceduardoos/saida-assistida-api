package br.com.fiap.saidaassistida.controller;

import br.com.fiap.saidaassistida.dao.MoradorDao;
import br.com.fiap.saidaassistida.dao.SetorDao;
import br.com.fiap.saidaassistida.dto.MoradorEdicaoRequestDto;
import br.com.fiap.saidaassistida.dto.MoradorRequestDto;
import br.com.fiap.saidaassistida.dto.MoradorResponseDto;
import br.com.fiap.saidaassistida.exception.DadosInvalidosException;
import br.com.fiap.saidaassistida.exception.EntidadeNaoEncontradaException;
import br.com.fiap.saidaassistida.model.Morador;
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
@RequestMapping("/moradores")
public class MoradorController {

    private final MoradorDao dao;
    private final SetorDao setorDao;
    private final ModelMapper mapper;

    public MoradorController(MoradorDao dao, SetorDao setorDao, ModelMapper mapper) {
        this.dao = dao;
        this.setorDao = setorDao;
        this.mapper = mapper;
    }

    @PostMapping
    public ResponseEntity<MoradorResponseDto> cadastrar(@Valid @RequestBody MoradorRequestDto dto, UriComponentsBuilder builder)
            throws SQLException, EntidadeNaoEncontradaException, DadosInvalidosException {
        if (dto.getIdSetor() <= 0) throw new DadosInvalidosException("idSetor deve ser informado");
        Morador morador = mapper.map(dto, Morador.class);
        morador.setSetor(setorDao.pesquisarPorId(dto.getIdSetor()));   // 404 se setor nao existe
        dao.cadastrar(morador);
        URI uri = builder.path("/moradores/{id}").buildAndExpand(morador.getId()).toUri();
        return ResponseEntity.created(uri).body(mapper.map(morador, MoradorResponseDto.class));
    }

    @GetMapping
    public List<MoradorResponseDto> listar() throws SQLException, EntidadeNaoEncontradaException {
        List<MoradorResponseDto> lista = new ArrayList<>();
        for (Morador m : dao.listar()) lista.add(mapper.map(m, MoradorResponseDto.class));
        return lista;
    }

    @GetMapping("/{id}")
    public MoradorResponseDto pesquisarPorId(@PathVariable int id) throws SQLException, EntidadeNaoEncontradaException {
        return mapper.map(dao.pesquisarPorId(id), MoradorResponseDto.class);
    }

    @PutMapping("/{id}")
    public MoradorResponseDto atualizar(@PathVariable int id, @Valid @RequestBody MoradorEdicaoRequestDto dto)
            throws SQLException, EntidadeNaoEncontradaException, DadosInvalidosException {
        if (dto.getIdSetor() <= 0) throw new DadosInvalidosException("idSetor deve ser informado");
        Morador morador = mapper.map(dto, Morador.class);
        morador.setId(id);
        morador.setSetor(setorDao.pesquisarPorId(dto.getIdSetor()));
        dao.atualizar(morador);
        return mapper.map(dao.pesquisarPorId(id), MoradorResponseDto.class);
    }

    // RN08: pedido de saida da lista (nao e DELETE)
    @PutMapping("/{id}/saida-da-lista")
    public MoradorResponseDto sairDaLista(@PathVariable int id) throws SQLException, EntidadeNaoEncontradaException {
        dao.registrarSaidaDaLista(id);
        return mapper.map(dao.pesquisarPorId(id), MoradorResponseDto.class);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable int id) throws SQLException, EntidadeNaoEncontradaException {
        dao.remover(id);   // morador com atendimentos -> 409 pelo handler
        return ResponseEntity.noContent().build();
    }
}
