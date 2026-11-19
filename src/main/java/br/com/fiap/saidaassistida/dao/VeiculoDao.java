package br.com.fiap.saidaassistida.dao;

import br.com.fiap.saidaassistida.exception.EntidadeNaoEncontradaException;
import br.com.fiap.saidaassistida.model.Veiculo;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class VeiculoDao {

    private static final String COLUNAS = "cd_veiculo, nm_veiculo, ds_tipo, qt_assentos, qt_cadeiras, qt_macas, in_disponivel";
    private final DataSource dataSource;

    public VeiculoDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void cadastrar(Veiculo v) throws SQLException {
        String sql = "INSERT INTO tb_veiculo (" + COLUNAS + ") VALUES (sq_tb_veiculo.NEXTVAL, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, new String[]{"cd_veiculo"})) {
            preencher(stmt, v);
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) v.setId(rs.getInt(1));
            }
        }
    }

    public Veiculo pesquisarPorId(int id) throws SQLException, EntidadeNaoEncontradaException {
        String sql = "SELECT " + COLUNAS + " FROM tb_veiculo WHERE cd_veiculo = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) throw new EntidadeNaoEncontradaException("Veiculo nao encontrado");
                return montar(rs);
            }
        }
    }

    public List<Veiculo> listar() throws SQLException {
        String sql = "SELECT " + COLUNAS + " FROM tb_veiculo ORDER BY cd_veiculo";
        List<Veiculo> lista = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) lista.add(montar(rs));
        }
        return lista;
    }

    public void atualizar(Veiculo v) throws SQLException, EntidadeNaoEncontradaException {
        String sql = "UPDATE tb_veiculo SET nm_veiculo = ?, ds_tipo = ?, qt_assentos = ?, qt_cadeiras = ?, "
                + "qt_macas = ?, in_disponivel = ? WHERE cd_veiculo = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            preencher(stmt, v);
            stmt.setInt(7, v.getId());
            if (stmt.executeUpdate() == 0) throw new EntidadeNaoEncontradaException("Veiculo nao encontrado");
        }
    }

    public void remover(int id) throws SQLException, EntidadeNaoEncontradaException {
        String sql = "DELETE FROM tb_veiculo WHERE cd_veiculo = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            if (stmt.executeUpdate() == 0) throw new EntidadeNaoEncontradaException("Veiculo nao encontrado");
        }
    }

    // RN04: veiculo ja reservado em outro atendimento que ainda nao chegou nem foi recusado
    public boolean estaOcupado(int idVeiculo) throws SQLException {
        String sql = "SELECT COUNT(*) FROM tb_atendimento WHERE cd_veiculo = ? "
                + "AND st_atendimento IN ('DESIGNADA','EQUIPE_A_CAMINHO','EMBARCADA')";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idVeiculo);
            try (ResultSet rs = stmt.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        }
    }

    private void preencher(PreparedStatement stmt, Veiculo v) throws SQLException {
        stmt.setString(1, v.getNome());
        stmt.setString(2, v.getTipo());
        stmt.setInt(3, v.getQtAssentos());
        stmt.setInt(4, v.getQtCadeiras());
        stmt.setInt(5, v.getQtMacas());
        stmt.setString(6, v.isDisponivel() ? "S" : "N");
    }

    private Veiculo montar(ResultSet rs) throws SQLException {
        Veiculo v = new Veiculo();
        v.setId(rs.getInt("cd_veiculo"));
        v.setNome(rs.getString("nm_veiculo"));
        v.setTipo(rs.getString("ds_tipo"));
        v.setQtAssentos(rs.getInt("qt_assentos"));
        v.setQtCadeiras(rs.getInt("qt_cadeiras"));
        v.setQtMacas(rs.getInt("qt_macas"));
        v.setDisponivel("S".equals(rs.getString("in_disponivel")));
        return v;
    }
}
