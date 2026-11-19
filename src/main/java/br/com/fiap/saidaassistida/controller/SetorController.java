package br.com.fiap.saidaassistida.controller;

import br.com.fiap.saidaassistida.dao.MoradorDao;
import br.com.fiap.saidaassistida.dao.SetorDao;
import br.com.fiap.saidaassistida.dto.*;
import br.com.fiap.saidaassistida.exception.EntidadeNaoEncontradaException;
import br.com.fiap.saidaassistida.model.Morador;
import br.com.fiap.saidaassistida.model.Setor;
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
@RequestMapping("/setores")
public class SetorController {

    private final SetorDao dao;
    private final MoradorDao moradorDao;
    private final ModelMapper mapper;

    public SetorController(SetorDao dao, MoradorDao moradorDao, ModelMapper mapper) {
        this.dao = dao;
        this.moradorDao = moradorDao;
        this.mapper = mapper;
    }

    @PostMapping
    public ResponseEntity<SetorResponseDto> cadastrar(@Valid @RequestBody SetorRequestDto dto, UriComponentsBuilder builder)
            throws SQLException {
        Setor setor = mapper.map(dto, Setor.class);
        dao.cadastrar(setor);
        URI uri = builder.path("/setores/{id}").buildAndExpand(setor.getId()).toUri();
        return ResponseEntity.created(uri).body(mapper.map(setor, SetorResponseDto.class));
    }

    @GetMapping
    public List<SetorResponseDto> listar() throws SQLException {
        List<SetorResponseDto> lista = new ArrayList<>();
        for (Setor s : dao.listar()) lista.add(mapper.map(s, SetorResponseDto.class));
        return lista;
    }

    @GetMapping("/{id}")
    public SetorResponseDto pesquisarPorId(@PathVariable int id) throws SQLException, EntidadeNaoEncontradaException {
        return mapper.map(dao.pesquisarPorId(id), SetorResponseDto.class);
    }

    @PutMapping("/{id}")
    public SetorResponseDto atualizar(@PathVariable int id, @Valid @RequestBody SetorRequestDto dto)
            throws SQLException, EntidadeNaoEncontradaException {
        Setor setor = mapper.map(dto, Setor.class);
        setor.setId(id);
        dao.atualizar(setor);
        return mapper.map(setor, SetorResponseDto.class);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable int id) throws SQLException, EntidadeNaoEncontradaException {
        dao.remover(id);   // setor com moradores -> FK violada -> 409 pelo handler
        return ResponseEntity.noContent().build();
    }

    // Rota aninhada do 1:N Setor -> Morador
    @GetMapping("/{id}/moradores")
    public List<MoradorResponseDto> listarMoradores(@PathVariable int id) throws SQLException, EntidadeNaoEncontradaException {
        dao.pesquisarPorId(id);  // 404 se o setor nao existir
        List<MoradorResponseDto> lista = new ArrayList<>();
        for (Morador m : moradorDao.listarPorSetor(id)) lista.add(mapper.map(m, MoradorResponseDto.class));
        return lista;
    }

    @PostMapping("/{id}/moradores")
    public ResponseEntity<MoradorResponseDto> cadastrarMorador(@PathVariable int id, @Valid @RequestBody MoradorRequestDto dto,
                                                              UriComponentsBuilder builder)
            throws SQLException, EntidadeNaoEncontradaException {
        Setor setor = dao.pesquisarPorId(id);
        Morador morador = mapper.map(dto, Morador.class);
        morador.setSetor(setor);
        moradorDao.cadastrar(morador);
        URI uri = builder.path("/moradores/{id}").buildAndExpand(morador.getId()).toUri();
        return ResponseEntity.created(uri).body(mapper.map(morador, MoradorResponseDto.class));
    }
}
