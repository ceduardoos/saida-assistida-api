package br.com.fiap.saidaassistida.dao;

import br.com.fiap.saidaassistida.exception.EntidadeNaoEncontradaException;
import br.com.fiap.saidaassistida.model.Alerta;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
public class AlertaDao {

    private static final String COLUNAS = "cd_alerta, cd_setor, ds_nivel, dt_abertura, dt_encerramento, st_alerta";
    private final DataSource dataSource;
    private final SetorDao setorDao;

    public AlertaDao(DataSource dataSource, SetorDao setorDao) {
        this.dataSource = dataSource;
        this.setorDao = setorDao;
    }

    public void cadastrar(Alerta a) throws SQLException {
        String sql = "INSERT INTO tb_alerta (" + COLUNAS + ") VALUES (sq_tb_alerta.NEXTVAL, ?, ?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, new String[]{"cd_alerta"})) {
            preencher(stmt, a);
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) a.setId(rs.getInt(1));
            }
        }
    }

    public Alerta pesquisarPorId(int id) throws SQLException, EntidadeNaoEncontradaException {
        String sql = "SELECT " + COLUNAS + " FROM tb_alerta WHERE cd_alerta = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) throw new EntidadeNaoEncontradaException("Alerta nao encontrado");
                return montar(rs);
            }
        }
    }

    public List<Alerta> listar() throws SQLException, EntidadeNaoEncontradaException {
        String sql = "SELECT " + COLUNAS + " FROM tb_alerta ORDER BY dt_abertura DESC";
        List<Alerta> lista = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) lista.add(montar(rs));
        }
        return lista;
    }

    public void atualizar(Alerta a) throws SQLException, EntidadeNaoEncontradaException {
        String sql = "UPDATE tb_alerta SET cd_setor = ?, ds_nivel = ?, dt_abertura = ?, dt_encerramento = ?, st_alerta = ? "
                + "WHERE cd_alerta = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            preencher(stmt, a);
            stmt.setInt(6, a.getId());
            if (stmt.executeUpdate() == 0) throw new EntidadeNaoEncontradaException("Alerta nao encontrado");
        }
    }

    public void remover(int id) throws SQLException, EntidadeNaoEncontradaException {
        String sql = "DELETE FROM tb_alerta WHERE cd_alerta = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            if (stmt.executeUpdate() == 0) throw new EntidadeNaoEncontradaException("Alerta nao encontrado");
        }
    }

    private void preencher(PreparedStatement stmt, Alerta a) throws SQLException {
        stmt.setInt(1, a.getSetor().getId());
        stmt.setString(2, a.getNivel());
        stmt.setObject(3, a.getDataAbertura());
        stmt.setObject(4, a.getDataEncerramento());
        stmt.setString(5, a.getSituacao());
    }

    private Alerta montar(ResultSet rs) throws SQLException, EntidadeNaoEncontradaException {
        Alerta a = new Alerta();
        a.setId(rs.getInt("cd_alerta"));
        a.setSetor(setorDao.pesquisarPorId(rs.getInt("cd_setor")));
        a.setNivel(rs.getString("ds_nivel"));
        a.setDataAbertura(rs.getObject("dt_abertura", LocalDateTime.class));
        a.setDataEncerramento(rs.getObject("dt_encerramento", LocalDateTime.class));
        a.setSituacao(rs.getString("st_alerta"));
        return a;
    }
}
