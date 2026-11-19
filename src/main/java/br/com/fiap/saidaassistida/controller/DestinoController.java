package br.com.fiap.saidaassistida.controller;

import br.com.fiap.saidaassistida.dao.DestinoDao;
import br.com.fiap.saidaassistida.dto.DestinoRequestDto;
import br.com.fiap.saidaassistida.dto.DestinoResponseDto;
import br.com.fiap.saidaassistida.exception.EntidadeNaoEncontradaException;
import br.com.fiap.saidaassistida.model.Destino;
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
@RequestMapping("/destinos")
public class DestinoController {

    private final DestinoDao dao;
    private final ModelMapper mapper;

    public DestinoController(DestinoDao dao, ModelMapper mapper) {
        this.dao = dao;
        this.mapper = mapper;
    }

    @PostMapping
    public ResponseEntity<DestinoResponseDto> cadastrar(@Valid @RequestBody DestinoRequestDto dto, UriComponentsBuilder builder)
            throws SQLException {
        Destino v = mapper.map(dto, Destino.class);
        dao.cadastrar(v);
        URI uri = builder.path("/destinos/{id}").buildAndExpand(v.getId()).toUri();
        return ResponseEntity.created(uri).body(mapper.map(v, DestinoResponseDto.class));
    }

    @GetMapping
    public List<DestinoResponseDto> listar() throws SQLException {
        List<DestinoResponseDto> lista = new ArrayList<>();
        for (Destino v : dao.listar()) lista.add(mapper.map(v, DestinoResponseDto.class));
        return lista;
    }

    @GetMapping("/{id}")
    public DestinoResponseDto pesquisarPorId(@PathVariable int id) throws SQLException, EntidadeNaoEncontradaException {
        return mapper.map(dao.pesquisarPorId(id), DestinoResponseDto.class);
    }

    @PutMapping("/{id}")
    public DestinoResponseDto atualizar(@PathVariable int id, @Valid @RequestBody DestinoRequestDto dto)
            throws SQLException, EntidadeNaoEncontradaException {
        Destino v = mapper.map(dto, Destino.class);
        v.setId(id);
        dao.atualizar(v);
        return mapper.map(v, DestinoResponseDto.class);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable int id) throws SQLException, EntidadeNaoEncontradaException {
        dao.remover(id);
        return ResponseEntity.noContent().build();
    }
}
