package br.com.fiap.saidaassistida.service;

import br.com.fiap.saidaassistida.dao.*;
import br.com.fiap.saidaassistida.exception.EntidadeNaoEncontradaException;
import br.com.fiap.saidaassistida.exception.RegraDeNegocioException;
import br.com.fiap.saidaassistida.model.*;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.time.LocalDateTime;

/**
 * Regras de negocio do atendimento (edital: pelo menos uma regra alem do CRUD, com excecao propria -> 409).
 * RN01 duplicidade, RN02 compatibilidade, RN03 capacidade, RN04 ocupacao, RN05 chegada, RN07 sequencia.
 */
@Service
public class AtendimentoService {

    private final AtendimentoDao atendimentoDao;
    private final MoradorDao moradorDao;
    private final AlertaDao alertaDao;
    private final VeiculoDao veiculoDao;
    private final DestinoDao destinoDao;

    public AtendimentoService(AtendimentoDao atendimentoDao, MoradorDao moradorDao, AlertaDao alertaDao,
                              VeiculoDao veiculoDao, DestinoDao destinoDao) {
        this.atendimentoDao = atendimentoDao;
        this.moradorDao = moradorDao;
        this.alertaDao = alertaDao;
        this.veiculoDao = veiculoDao;
        this.destinoDao = destinoDao;
    }

    // Inclui um morador em um alerta. RN01: um atendimento por morador em cada alerta.
    public Atendimento incluir(int idAlerta, int idMorador, String motivoPendencia)
            throws SQLException, EntidadeNaoEncontradaException, RegraDeNegocioException {
        Alerta alerta = alertaDao.pesquisarPorId(idAlerta);
        Morador morador = moradorDao.pesquisarPorId(idMorador);

        if (!"ABERTO".equals(alerta.getSituacao()))
            throw new RegraDeNegocioException("O alerta esta encerrado; nao e possivel incluir atendimento");
        if ("SAIU_DA_LISTA".equals(morador.getSituacaoCadastro()))
            throw new RegraDeNegocioException("O morador pediu saida da lista; inclusao exige decisao do operador");
        if (morador.getSetor().getId() != alerta.getSetor().getId())
            throw new RegraDeNegocioException("O morador nao pertence ao setor do alerta");
        if (atendimentoDao.existe(idMorador, idAlerta))
            throw new RegraDeNegocioException("Este morador ja possui atendimento neste alerta");

        Atendimento at = new Atendimento();
        at.setMorador(morador);
        at.setAlerta(alerta);
        at.setSituacao(Atendimento.AGUARDANDO);
        at.setMotivoPendencia(motivoPendencia);
        atendimentoDao.cadastrar(at);
        return at;
    }

    // Designa veiculo, destino e responsavel. RN02, RN03, RN04 e RN06.
    public Atendimento designar(int idAtendimento, int idVeiculo, int idDestino, String responsavel)
            throws SQLException, EntidadeNaoEncontradaException, RegraDeNegocioException {
        Atendimento at = atendimentoDao.pesquisarPorId(idAtendimento);
        Veiculo veiculo = veiculoDao.pesquisarPorId(idVeiculo);
        Destino destino = destinoDao.pesquisarPorId(idDestino);
        Morador morador = at.getMorador();

        if (!Atendimento.AGUARDANDO.equals(at.getSituacao()))
            throw new RegraDeNegocioException("So e possivel designar um atendimento que esta AGUARDANDO");

        // RN02 compatibilidade do veiculo com a necessidade
        if (!veiculo.isDisponivel())
            throw new RegraDeNegocioException("Veiculo indisponivel");
        if (morador.isPrecisaMaca() && veiculo.getQtMacas() == 0)
            throw new RegraDeNegocioException("Morador precisa de maca e o veiculo " + veiculo.getNome() + " nao possui maca");
        if (morador.isUsaCadeiraRodas() && veiculo.getQtCadeiras() == 0 && veiculo.getQtMacas() == 0)
            throw new RegraDeNegocioException("Morador usa cadeira de rodas e o veiculo nao tem posicao adaptada");

        // RN03 acompanhante ocupa assento
        int assentosNecessarios = morador.getQtAcompanhante() + (morador.isPrecisaMaca() || morador.isUsaCadeiraRodas() ? 0 : 1);
        if (veiculo.getQtAssentos() < assentosNecessarios)
            throw new RegraDeNegocioException("Veiculo sem assentos suficientes para morador e acompanhante");

        // RN04 ocupacao: um veiculo por atendimento ativo (revalidado aqui, no momento da confirmacao)
        if (veiculoDao.estaOcupado(idVeiculo))
            throw new RegraDeNegocioException("Veiculo ja reservado para outro atendimento em andamento");

        // RN02/RN03 destino compativel e com vaga
        if (!destino.isDisponivel())
            throw new RegraDeNegocioException("Destino indisponivel");
        if ((morador.isPrecisaMaca() || morador.isUsaCadeiraRodas()) && !destino.isAcessivel())
            throw new RegraDeNegocioException("Destino nao e acessivel para a necessidade do morador");
        int pessoas = 1 + morador.getQtAcompanhante();
        if (destinoDao.contarOcupacao(idDestino) + pessoas > destino.getQtVagas())
            throw new RegraDeNegocioException("Destino sem vagas suficientes");

        at.setVeiculo(veiculo);
        at.setDestino(destino);
        at.setResponsavel(responsavel);
        at.setSituacao(Atendimento.DESIGNADA);
        at.setMotivoPendencia(null);
        at.setDataDesignacao(LocalDateTime.now());
        atendimentoDao.atualizar(at);
        return at;
    }

    // Avanca o estado ou registra recusa/pendencia. RN05 e RN07.
    public Atendimento registrarMarco(int idAtendimento, String marco, LocalDateTime instante, String motivoPendencia)
            throws SQLException, EntidadeNaoEncontradaException, RegraDeNegocioException {
        Atendimento at = atendimentoDao.pesquisarPorId(idAtendimento);
        LocalDateTime quando = instante != null ? instante : LocalDateTime.now();

        if (Atendimento.CHEGADA_CONFIRMADA.equals(at.getSituacao()) || Atendimento.RECUSADO.equals(at.getSituacao()))
            throw new RegraDeNegocioException("Atendimento ja encerrado (" + at.getSituacao() + ")");

        switch (marco) {
            case "EQUIPE_A_CAMINHO" -> {
                exigirEstado(at, Atendimento.DESIGNADA);
                at.setSituacao(Atendimento.EQUIPE_A_CAMINHO);
            }
            case "EMBARCADA" -> {
                exigirEstado(at, Atendimento.DESIGNADA, Atendimento.EQUIPE_A_CAMINHO);
                if (at.getDataDesignacao() != null && quando.isBefore(at.getDataDesignacao()))
                    throw new RegraDeNegocioException("Embarque nao pode ser anterior a designacao");
                at.setDataEmbarque(quando);
                at.setSituacao(Atendimento.EMBARCADA);
            }
            case "CHEGADA_CONFIRMADA" -> {
                exigirEstado(at, Atendimento.EMBARCADA);              // RN05: embarque nao basta, mas e pre-requisito
                if (at.getDestino() == null || at.getResponsavel() == null)
                    throw new RegraDeNegocioException("Chegada exige destino e responsavel");
                if (at.getDataEmbarque() != null && quando.isBefore(at.getDataEmbarque()))
                    throw new RegraDeNegocioException("Chegada nao pode ser anterior ao embarque");
                at.setDataChegada(quando);
                at.setSituacao(Atendimento.CHEGADA_CONFIRMADA);
            }
            case "RECUSADO" -> {
                if (Atendimento.EMBARCADA.equals(at.getSituacao()))
                    throw new RegraDeNegocioException("Nao e possivel recusar apos o embarque");
                at.setSituacao(Atendimento.RECUSADO);                 // RN09: recusa nao e saida da lista
            }
            case "PENDENCIA" -> {
                if (motivoPendencia == null)
                    throw new RegraDeNegocioException("Informe o motivoPendencia");
                at.setMotivoPendencia(motivoPendencia);
            }
            default -> throw new RegraDeNegocioException("Marco desconhecido");
        }
        atendimentoDao.atualizar(at);
        return at;
    }

    private void exigirEstado(Atendimento at, String... permitidos) throws RegraDeNegocioException {
        for (String p : permitidos) if (p.equals(at.getSituacao())) return;
        throw new RegraDeNegocioException("Transicao invalida a partir do estado " + at.getSituacao());
    }
}
