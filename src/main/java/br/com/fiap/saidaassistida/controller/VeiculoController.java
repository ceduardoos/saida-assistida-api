package br.com.fiap.saidaassistida.controller;

import br.com.fiap.saidaassistida.dao.VeiculoDao;
import br.com.fiap.saidaassistida.dto.VeiculoRequestDto;
import br.com.fiap.saidaassistida.dto.VeiculoResponseDto;
import br.com.fiap.saidaassistida.exception.EntidadeNaoEncontradaException;
import br.com.fiap.saidaassistida.model.Veiculo;
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
@RequestMapping("/veiculos")
public class VeiculoController {

    private final VeiculoDao dao;
    private final ModelMapper mapper;

    public VeiculoController(VeiculoDao dao, ModelMapper mapper) {
        this.dao = dao;
        this.mapper = mapper;
    }

    @PostMapping
    public ResponseEntity<VeiculoResponseDto> cadastrar(@Valid @RequestBody VeiculoRequestDto dto, UriComponentsBuilder builder)
            throws SQLException {
        Veiculo v = mapper.map(dto, Veiculo.class);
        dao.cadastrar(v);
        URI uri = builder.path("/veiculos/{id}").buildAndExpand(v.getId()).toUri();
        return ResponseEntity.created(uri).body(mapper.map(v, VeiculoResponseDto.class));
    }

    @GetMapping
    public List<VeiculoResponseDto> listar() throws SQLException {
        List<VeiculoResponseDto> lista = new ArrayList<>();
        for (Veiculo v : dao.listar()) lista.add(mapper.map(v, VeiculoResponseDto.class));
        return lista;
    }

    @GetMapping("/{id}")
    public VeiculoResponseDto pesquisarPorId(@PathVariable int id) throws SQLException, EntidadeNaoEncontradaException {
        return mapper.map(dao.pesquisarPorId(id), VeiculoResponseDto.class);
    }

    @PutMapping("/{id}")
    public VeiculoResponseDto atualizar(@PathVariable int id, @Valid @RequestBody VeiculoRequestDto dto)
            throws SQLException, EntidadeNaoEncontradaException {
        Veiculo v = mapper.map(dto, Veiculo.class);
        v.setId(id);
        dao.atualizar(v);
        return mapper.map(v, VeiculoResponseDto.class);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable int id) throws SQLException, EntidadeNaoEncontradaException {
        dao.remover(id);
        return ResponseEntity.noContent().build();
    }
}
