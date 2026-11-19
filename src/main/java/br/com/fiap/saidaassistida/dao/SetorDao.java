package br.com.fiap.saidaassistida.dao;

import br.com.fiap.saidaassistida.exception.EntidadeNaoEncontradaException;
import br.com.fiap.saidaassistida.model.Setor;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class SetorDao {

    private final DataSource dataSource;

    public SetorDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void cadastrar(Setor setor) throws SQLException {
        String sql = "INSERT INTO tb_setor (cd_setor, nm_setor, ds_referencia, ds_nivel_risco) "
                + "VALUES (sq_tb_setor.NEXTVAL, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, new String[]{"cd_setor"})) {
            stmt.setString(1, setor.getNome());
            stmt.setString(2, setor.getReferencia());
            stmt.setString(3, setor.getNivelRisco());
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) setor.setId(rs.getInt(1));
            }
        }
    }

    public Setor pesquisarPorId(int id) throws SQLException, EntidadeNaoEncontradaException {
        String sql = "SELECT cd_setor, nm_setor, ds_referencia, ds_nivel_risco FROM tb_setor WHERE cd_setor = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) throw new EntidadeNaoEncontradaException("Setor nao encontrado");
                return montar(rs);
            }
        }
    }

    public List<Setor> listar() throws SQLException {
        String sql = "SELECT cd_setor, nm_setor, ds_referencia, ds_nivel_risco FROM tb_setor ORDER BY cd_setor";
        List<Setor> lista = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) lista.add(montar(rs));
        }
        return lista;
    }

    public void atualizar(Setor setor) throws SQLException, EntidadeNaoEncontradaException {
        String sql = "UPDATE tb_setor SET nm_setor = ?, ds_referencia = ?, ds_nivel_risco = ? WHERE cd_setor = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, setor.getNome());
            stmt.setString(2, setor.getReferencia());
            stmt.setString(3, setor.getNivelRisco());
            stmt.setInt(4, setor.getId());
            if (stmt.executeUpdate() == 0) throw new EntidadeNaoEncontradaException("Setor nao encontrado");
        }
    }

    public void remover(int id) throws SQLException, EntidadeNaoEncontradaException {
        String sql = "DELETE FROM tb_setor WHERE cd_setor = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            if (stmt.executeUpdate() == 0) throw new EntidadeNaoEncontradaException("Setor nao encontrado");
        }
    }

    private Setor montar(ResultSet rs) throws SQLException {
        return new Setor(rs.getInt("cd_setor"), rs.getString("nm_setor"),
                rs.getString("ds_referencia"), rs.getString("ds_nivel_risco"));
    }
}
