package br.com.fiap.saidaassistida.dao;

import br.com.fiap.saidaassistida.exception.EntidadeNaoEncontradaException;
import br.com.fiap.saidaassistida.model.Destino;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class DestinoDao {

    private static final String COLUNAS = "cd_destino, nm_destino, ds_tipo, ds_endereco, qt_vagas, in_acessivel, in_disponivel";
    private final DataSource dataSource;

    public DestinoDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void cadastrar(Destino d) throws SQLException {
        String sql = "INSERT INTO tb_destino (" + COLUNAS + ") VALUES (sq_tb_destino.NEXTVAL, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, new String[]{"cd_destino"})) {
            preencher(stmt, d);
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) d.setId(rs.getInt(1));
            }
        }
    }

    public Destino pesquisarPorId(int id) throws SQLException, EntidadeNaoEncontradaException {
        String sql = "SELECT " + COLUNAS + " FROM tb_destino WHERE cd_destino = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) throw new EntidadeNaoEncontradaException("Destino nao encontrado");
                return montar(rs);
            }
        }
    }

    public List<Destino> listar() throws SQLException {
        String sql = "SELECT " + COLUNAS + " FROM tb_destino ORDER BY cd_destino";
        List<Destino> lista = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) lista.add(montar(rs));
        }
        return lista;
    }

    public void atualizar(Destino d) throws SQLException, EntidadeNaoEncontradaException {
        String sql = "UPDATE tb_destino SET nm_destino = ?, ds_tipo = ?, ds_endereco = ?, qt_vagas = ?, "
                + "in_acessivel = ?, in_disponivel = ? WHERE cd_destino = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            preencher(stmt, d);
            stmt.setInt(7, d.getId());
            if (stmt.executeUpdate() == 0) throw new EntidadeNaoEncontradaException("Destino nao encontrado");
        }
    }

    public void remover(int id) throws SQLException, EntidadeNaoEncontradaException {
        String sql = "DELETE FROM tb_destino WHERE cd_destino = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            if (stmt.executeUpdate() == 0) throw new EntidadeNaoEncontradaException("Destino nao encontrado");
        }
    }

    // Vagas ocupadas = atendimentos que apontam para o destino e ainda nao foram recusados
    public int contarOcupacao(int idDestino) throws SQLException {
        String sql = "SELECT COUNT(*) FROM tb_atendimento WHERE cd_destino = ? AND st_atendimento <> 'RECUSADO'";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idDestino);
            try (ResultSet rs = stmt.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    private void preencher(PreparedStatement stmt, Destino d) throws SQLException {
        stmt.setString(1, d.getNome());
        stmt.setString(2, d.getTipo());
        stmt.setString(3, d.getEndereco());
        stmt.setInt(4, d.getQtVagas());
        stmt.setString(5, d.isAcessivel() ? "S" : "N");
        stmt.setString(6, d.isDisponivel() ? "S" : "N");
    }

    private Destino montar(ResultSet rs) throws SQLException {
        Destino d = new Destino();
        d.setId(rs.getInt("cd_destino"));
        d.setNome(rs.getString("nm_destino"));
        d.setTipo(rs.getString("ds_tipo"));
        d.setEndereco(rs.getString("ds_endereco"));
        d.setQtVagas(rs.getInt("qt_vagas"));
        d.setAcessivel("S".equals(rs.getString("in_acessivel")));
        d.setDisponivel("S".equals(rs.getString("in_disponivel")));
        return d;
    }
}
