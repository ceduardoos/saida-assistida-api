package br.com.fiap.saidaassistida.dao;

import br.com.fiap.saidaassistida.exception.EntidadeNaoEncontradaException;
import br.com.fiap.saidaassistida.model.Atendimento;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
public class AtendimentoDao {

    private static final String COLUNAS = "cd_atendimento, cd_morador, cd_alerta, cd_veiculo, cd_destino, nm_responsavel, "
            + "st_atendimento, ds_motivo_pendencia, dt_designacao, dt_embarque, dt_chegada";

    private final DataSource dataSource;
    private final MoradorDao moradorDao;
    private final AlertaDao alertaDao;
    private final VeiculoDao veiculoDao;
    private final DestinoDao destinoDao;

    public AtendimentoDao(DataSource dataSource, MoradorDao moradorDao, AlertaDao alertaDao,
                          VeiculoDao veiculoDao, DestinoDao destinoDao) {
        this.dataSource = dataSource;
        this.moradorDao = moradorDao;
        this.alertaDao = alertaDao;
        this.veiculoDao = veiculoDao;
        this.destinoDao = destinoDao;
    }

    public void cadastrar(Atendimento at) throws SQLException {
        String sql = "INSERT INTO tb_atendimento (cd_atendimento, cd_morador, cd_alerta, st_atendimento, ds_motivo_pendencia) "
                + "VALUES (sq_tb_atendimento.NEXTVAL, ?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, new String[]{"cd_atendimento"})) {
            stmt.setInt(1, at.getMorador().getId());
            stmt.setInt(2, at.getAlerta().getId());
            stmt.setString(3, at.getSituacao());
            stmt.setString(4, at.getMotivoPendencia());
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) at.setId(rs.getInt(1));
            }
        }
    }

    public Atendimento pesquisarPorId(int id) throws SQLException, EntidadeNaoEncontradaException {
        String sql = "SELECT " + COLUNAS + " FROM tb_atendimento WHERE cd_atendimento = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) throw new EntidadeNaoEncontradaException("Atendimento nao encontrado");
                return montar(rs);
            }
        }
    }

    public List<Atendimento> listar() throws SQLException, EntidadeNaoEncontradaException {
        return consultarLista("SELECT " + COLUNAS + " FROM tb_atendimento ORDER BY cd_atendimento", 0);
    }

    // Rota aninhada GET /alertas/{id}/atendimentos (1:N Alerta -> Atendimento)
    public List<Atendimento> listarPorAlerta(int idAlerta) throws SQLException, EntidadeNaoEncontradaException {
        return consultarLista("SELECT " + COLUNAS + " FROM tb_atendimento WHERE cd_alerta = ? ORDER BY st_atendimento, cd_atendimento", idAlerta);
    }

    public boolean existe(int idMorador, int idAlerta) throws SQLException {
        String sql = "SELECT COUNT(*) FROM tb_atendimento WHERE cd_morador = ? AND cd_alerta = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idMorador);
            stmt.setInt(2, idAlerta);
            try (ResultSet rs = stmt.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        }
    }

    // Atualiza designacao, estado, motivo e marcos. Usado por designar, avancar, recusar e pelo PUT generico.
    public void atualizar(Atendimento at) throws SQLException, EntidadeNaoEncontradaException {
        String sql = "UPDATE tb_atendimento SET cd_veiculo = ?, cd_destino = ?, nm_responsavel = ?, st_atendimento = ?, "
                + "ds_motivo_pendencia = ?, dt_designacao = ?, dt_embarque = ?, dt_chegada = ? WHERE cd_atendimento = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            if (at.getVeiculo() == null) stmt.setNull(1, Types.NUMERIC); else stmt.setInt(1, at.getVeiculo().getId());
            if (at.getDestino() == null) stmt.setNull(2, Types.NUMERIC); else stmt.setInt(2, at.getDestino().getId());
            stmt.setString(3, at.getResponsavel());
            stmt.setString(4, at.getSituacao());
            stmt.setString(5, at.getMotivoPendencia());
            stmt.setObject(6, at.getDataDesignacao());
            stmt.setObject(7, at.getDataEmbarque());
            stmt.setObject(8, at.getDataChegada());
            stmt.setInt(9, at.getId());
            if (stmt.executeUpdate() == 0) throw new EntidadeNaoEncontradaException("Atendimento nao encontrado");
        }
    }

    public void remover(int id) throws SQLException, EntidadeNaoEncontradaException {
        String sql = "DELETE FROM tb_atendimento WHERE cd_atendimento = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            if (stmt.executeUpdate() == 0) throw new EntidadeNaoEncontradaException("Atendimento nao encontrado");
        }
    }

    private List<Atendimento> consultarLista(String sql, int parametro) throws SQLException, EntidadeNaoEncontradaException {
        List<Atendimento> lista = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            if (sql.contains("?")) stmt.setInt(1, parametro);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) lista.add(montar(rs));
            }
        }
        return lista;
    }

    private Atendimento montar(ResultSet rs) throws SQLException, EntidadeNaoEncontradaException {
        Atendimento at = new Atendimento();
        at.setId(rs.getInt("cd_atendimento"));
        at.setMorador(moradorDao.pesquisarPorId(rs.getInt("cd_morador")));
        at.setAlerta(alertaDao.pesquisarPorId(rs.getInt("cd_alerta")));
        int idVeiculo = rs.getInt("cd_veiculo");
        if (!rs.wasNull()) at.setVeiculo(veiculoDao.pesquisarPorId(idVeiculo));
        int idDestino = rs.getInt("cd_destino");
        if (!rs.wasNull()) at.setDestino(destinoDao.pesquisarPorId(idDestino));
        at.setResponsavel(rs.getString("nm_responsavel"));
        at.setSituacao(rs.getString("st_atendimento"));
        at.setMotivoPendencia(rs.getString("ds_motivo_pendencia"));
        at.setDataDesignacao(rs.getObject("dt_designacao", LocalDateTime.class));
        at.setDataEmbarque(rs.getObject("dt_embarque", LocalDateTime.class));
        at.setDataChegada(rs.getObject("dt_chegada", LocalDateTime.class));
        return at;
    }
}
