package br.com.fiap.saidaassistida.controller;

import br.com.fiap.saidaassistida.dao.MoradorDao;
import br.com.fiap.saidaassistida.dto.ConfirmacaoRequestDto;
import br.com.fiap.saidaassistida.dto.MoradorResponseDto;
import br.com.fiap.saidaassistida.exception.EntidadeNaoEncontradaException;
import br.com.fiap.saidaassistida.model.Morador;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;

// Pagina do cuidador (/confirmar/:codigo no Front). O codigo e demonstrativo, nao e autenticacao.
@RestController
@RequestMapping("/confirmacoes")
public class ConfirmacaoController {

    private final MoradorDao dao;
    private final ModelMapper mapper;

    public ConfirmacaoController(MoradorDao dao, ModelMapper mapper) {
        this.dao = dao;
        this.mapper = mapper;
    }

    @GetMapping("/{codigo}")
    public MoradorResponseDto consultar(@PathVariable String codigo) throws SQLException, EntidadeNaoEncontradaException {
        return mapper.map(dao.pesquisarPorCodigo(codigo), MoradorResponseDto.class);
    }

    @PutMapping("/{codigo}")
    public MoradorResponseDto confirmar(@PathVariable String codigo, @Valid @RequestBody ConfirmacaoRequestDto dto)
            throws SQLException, EntidadeNaoEncontradaException {
        Morador m = dao.pesquisarPorCodigo(codigo);
        m.setContato(dto.getContato());
        m.setPrecisaMaca(dto.isPrecisaMaca());
        m.setUsaCadeiraRodas(dto.isUsaCadeiraRodas());
        m.setQtAcompanhante(dto.getQtAcompanhante());
        m.setBarreiraAcesso(dto.getBarreiraAcesso());
        dao.confirmar(m);
        return mapper.map(dao.pesquisarPorId(m.getId()), MoradorResponseDto.class);
    }

    @PutMapping("/{codigo}/saida-da-lista")
    public MoradorResponseDto sairDaLista(@PathVariable String codigo) throws SQLException, EntidadeNaoEncontradaException {
        Morador m = dao.pesquisarPorCodigo(codigo);
        dao.registrarSaidaDaLista(m.getId());
        return mapper.map(dao.pesquisarPorId(m.getId()), MoradorResponseDto.class);
    }
}
