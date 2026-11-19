package br.com.fiap.saidaassistida.dao;

import br.com.fiap.saidaassistida.exception.EntidadeNaoEncontradaException;
import br.com.fiap.saidaassistida.model.Morador;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
public class MoradorDao {

    private static final String COLUNAS = "cd_morador, cd_setor, nm_morador, ds_endereco, ds_contato, "
            + "vl_latitude, vl_longitude, in_maca, in_cadeira_rodas, qt_acompanhante, ds_barreira_acesso, "
            + "ds_codigo_confirmacao, dt_confirmacao, st_cadastro, dt_pedido_saida";

    private final DataSource dataSource;
    private final SetorDao setorDao;

    public MoradorDao(DataSource dataSource, SetorDao setorDao) {
        this.dataSource = dataSource;
        this.setorDao = setorDao;
    }

    public void cadastrar(Morador m) throws SQLException {
        String sql = "INSERT INTO tb_morador (cd_morador, cd_setor, nm_morador, ds_endereco, ds_contato, "
                + "vl_latitude, vl_longitude, in_maca, in_cadeira_rodas, qt_acompanhante, ds_barreira_acesso, "
                + "ds_codigo_confirmacao, st_cadastro) "
                + "VALUES (sq_tb_morador.NEXTVAL, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 'ATIVO')";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, new String[]{"cd_morador"})) {
            stmt.setInt(1, m.getSetor().getId());
            stmt.setString(2, m.getNome());
            stmt.setString(3, m.getEndereco());
            stmt.setString(4, m.getContato());
            stmt.setDouble(5, m.getLatitude());
            stmt.setDouble(6, m.getLongitude());
            stmt.setString(7, m.isPrecisaMaca() ? "S" : "N");
            stmt.setString(8, m.isUsaCadeiraRodas() ? "S" : "N");
            stmt.setInt(9, m.getQtAcompanhante());
            stmt.setString(10, m.getBarreiraAcesso());
            stmt.setString(11, m.getCodigoConfirmacao());
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) m.setId(rs.getInt(1));
            }
        }
        m.setSituacaoCadastro("ATIVO");
    }

    public Morador pesquisarPorId(int id) throws SQLException, EntidadeNaoEncontradaException {
        String sql = "SELECT " + COLUNAS + " FROM tb_morador WHERE cd_morador = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) throw new EntidadeNaoEncontradaException("Morador nao encontrado");
                return montar(rs);
            }
        }
    }

    public Morador pesquisarPorCodigo(String codigo) throws SQLException, EntidadeNaoEncontradaException {
        String sql = "SELECT " + COLUNAS + " FROM tb_morador WHERE ds_codigo_confirmacao = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, codigo);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) throw new EntidadeNaoEncontradaException("Codigo de confirmacao nao encontrado");
                return montar(rs);
            }
        }
    }

    public List<Morador> listar() throws SQLException, EntidadeNaoEncontradaException {
        return consultarLista("SELECT " + COLUNAS + " FROM tb_morador ORDER BY cd_morador", 0);
    }

    // Rota aninhada GET /setores/{id}/moradores
    public List<Morador> listarPorSetor(int idSetor) throws SQLException, EntidadeNaoEncontradaException {
        return consultarLista("SELECT " + COLUNAS + " FROM tb_morador WHERE cd_setor = ? ORDER BY nm_morador", idSetor);
    }

    // Elegiveis para um alerta: moradores ATIVOS do setor que ainda nao tem atendimento naquele alerta
    public List<Morador> listarElegiveis(int idSetor, int idAlerta) throws SQLException, EntidadeNaoEncontradaException {
        String sql = "SELECT " + COLUNAS + " FROM tb_morador m WHERE m.cd_setor = ? AND m.st_cadastro = 'ATIVO' "
                + "AND NOT EXISTS (SELECT 1 FROM tb_atendimento a WHERE a.cd_morador = m.cd_morador AND a.cd_alerta = ?)";
        List<Morador> lista = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idSetor);
            stmt.setInt(2, idAlerta);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) lista.add(montar(rs));
            }
        }
        return lista;
    }

    public void atualizar(Morador m) throws SQLException, EntidadeNaoEncontradaException {
        String sql = "UPDATE tb_morador SET cd_setor = ?, nm_morador = ?, ds_endereco = ?, ds_contato = ?, "
                + "vl_latitude = ?, vl_longitude = ?, in_maca = ?, in_cadeira_rodas = ?, qt_acompanhante = ?, "
                + "ds_barreira_acesso = ? WHERE cd_morador = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, m.getSetor().getId());
            stmt.setString(2, m.getNome());
            stmt.setString(3, m.getEndereco());
            stmt.setString(4, m.getContato());
            stmt.setDouble(5, m.getLatitude());
            stmt.setDouble(6, m.getLongitude());
            stmt.setString(7, m.isPrecisaMaca() ? "S" : "N");
            stmt.setString(8, m.isUsaCadeiraRodas() ? "S" : "N");
            stmt.setInt(9, m.getQtAcompanhante());
            stmt.setString(10, m.getBarreiraAcesso());
            stmt.setInt(11, m.getId());
            if (stmt.executeUpdate() == 0) throw new EntidadeNaoEncontradaException("Morador nao encontrado");
        }
    }

    // Confirmacao pelo cuidador: so os campos de necessidade + horario da confirmacao
    public void confirmar(Morador m) throws SQLException, EntidadeNaoEncontradaException {
        String sql = "UPDATE tb_morador SET ds_contato = ?, in_maca = ?, in_cadeira_rodas = ?, qt_acompanhante = ?, "
                + "ds_barreira_acesso = ?, dt_confirmacao = ? WHERE cd_morador = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, m.getContato());
            stmt.setString(2, m.isPrecisaMaca() ? "S" : "N");
            stmt.setString(3, m.isUsaCadeiraRodas() ? "S" : "N");
            stmt.setInt(4, m.getQtAcompanhante());
            stmt.setString(5, m.getBarreiraAcesso());
            stmt.setObject(6, LocalDateTime.now());
            stmt.setInt(7, m.getId());
            if (stmt.executeUpdate() == 0) throw new EntidadeNaoEncontradaException("Morador nao encontrado");
        }
    }

    // RN08: pedido de saida da lista nao apaga nada
    public void registrarSaidaDaLista(int id) throws SQLException, EntidadeNaoEncontradaException {
        String sql = "UPDATE tb_morador SET st_cadastro = 'SAIU_DA_LISTA', dt_pedido_saida = ? WHERE cd_morador = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, LocalDateTime.now());
            stmt.setInt(2, id);
            if (stmt.executeUpdate() == 0) throw new EntidadeNaoEncontradaException("Morador nao encontrado");
        }
    }

    public void remover(int id) throws SQLException, EntidadeNaoEncontradaException {
        String sql = "DELETE FROM tb_morador WHERE cd_morador = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            if (stmt.executeUpdate() == 0) throw new EntidadeNaoEncontradaException("Morador nao encontrado");
        }
    }

    private List<Morador> consultarLista(String sql, int parametro) throws SQLException, EntidadeNaoEncontradaException {
        List<Morador> lista = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            if (sql.contains("?")) stmt.setInt(1, parametro);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) lista.add(montar(rs));
            }
        }
        return lista;
    }

    private Morador montar(ResultSet rs) throws SQLException, EntidadeNaoEncontradaException {
        Morador m = new Morador();
        m.setId(rs.getInt("cd_morador"));
        m.setSetor(setorDao.pesquisarPorId(rs.getInt("cd_setor")));
        m.setNome(rs.getString("nm_morador"));
        m.setEndereco(rs.getString("ds_endereco"));
        m.setContato(rs.getString("ds_contato"));
        m.setLatitude(rs.getDouble("vl_latitude"));
        m.setLongitude(rs.getDouble("vl_longitude"));
        m.setPrecisaMaca("S".equals(rs.getString("in_maca")));
        m.setUsaCadeiraRodas("S".equals(rs.getString("in_cadeira_rodas")));
        m.setQtAcompanhante(rs.getInt("qt_acompanhante"));
        m.setBarreiraAcesso(rs.getString("ds_barreira_acesso"));
        m.setCodigoConfirmacao(rs.getString("ds_codigo_confirmacao"));
        m.setDataConfirmacao(rs.getObject("dt_confirmacao", LocalDateTime.class));
        m.setSituacaoCadastro(rs.getString("st_cadastro"));
        m.setDataPedidoSaida(rs.getObject("dt_pedido_saida", LocalDateTime.class));
        return m;
    }
}
