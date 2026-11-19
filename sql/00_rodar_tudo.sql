-- Rodar tudo de uma vez: tabelas + carga de teste (F5 no SQL Developer)
-- =====================================================================
-- Egide - Saida Assistida | Script de tabelas e sequences (Oracle FIAP)
-- Padrao da disciplina: tb_nome, sq_tb_nome, prefixos cd_, nm_, ds_, dt_, qt_, in_, st_
-- Ordem: tabelas sem FK primeiro. Executar uma vez no usuario do grupo.
-- =====================================================================

-- Setor de risco do bairro (referencia territorial do cenario)
CREATE TABLE tb_setor (
    cd_setor        NUMBER(9)      PRIMARY KEY,
    nm_setor        VARCHAR2(80)   NOT NULL,
    ds_referencia   VARCHAR2(200),
    ds_nivel_risco  VARCHAR2(10)   NOT NULL,
    CONSTRAINT ck_setor_nivel CHECK (ds_nivel_risco IN ('BAIXO','MEDIO','ALTO','MUITO_ALTO'))
);
COMMENT ON TABLE tb_setor IS 'Setor de risco (poligono do bairro) usado para abrir alertas e agrupar moradores';
CREATE SEQUENCE sq_tb_setor START WITH 1 INCREMENT BY 1 NOCACHE;

-- Morador que precisa de apoio para sair (dados ficticios; origem real seria o e-SUS)
CREATE TABLE tb_morador (
    cd_morador          NUMBER(9)      PRIMARY KEY,
    cd_setor            NUMBER(9)      NOT NULL,
    nm_morador          VARCHAR2(100)  NOT NULL,
    ds_endereco         VARCHAR2(200)  NOT NULL,
    ds_contato          VARCHAR2(30),
    vl_latitude         NUMBER(9,6)    NOT NULL,
    vl_longitude        NUMBER(9,6)    NOT NULL,
    in_maca             CHAR(1)        DEFAULT 'N' NOT NULL,
    in_cadeira_rodas    CHAR(1)        DEFAULT 'N' NOT NULL,
    qt_acompanhante     NUMBER(2)      DEFAULT 0 NOT NULL,
    ds_barreira_acesso  VARCHAR2(200),
    ds_codigo_confirmacao VARCHAR2(12) NOT NULL,
    dt_confirmacao      DATE,
    st_cadastro         VARCHAR2(15)   DEFAULT 'ATIVO' NOT NULL,
    dt_pedido_saida     DATE,
    CONSTRAINT fk_morador_setor FOREIGN KEY (cd_setor) REFERENCES tb_setor (cd_setor),
    CONSTRAINT un_morador_codigo UNIQUE (ds_codigo_confirmacao),
    CONSTRAINT ck_morador_maca CHECK (in_maca IN ('S','N')),
    CONSTRAINT ck_morador_cadeira CHECK (in_cadeira_rodas IN ('S','N')),
    CONSTRAINT ck_morador_acomp CHECK (qt_acompanhante >= 0),
    CONSTRAINT ck_morador_lat CHECK (vl_latitude BETWEEN -90 AND 90),
    CONSTRAINT ck_morador_lon CHECK (vl_longitude BETWEEN -180 AND 180),
    CONSTRAINT ck_morador_st CHECK (st_cadastro IN ('ATIVO','SAIU_DA_LISTA'))
);
COMMENT ON TABLE tb_morador IS 'Pessoa com necessidade de apoio para evacuacao; confirma dados por codigo e pode pedir saida da lista';
CREATE SEQUENCE sq_tb_morador START WITH 1 INCREMENT BY 1 NOCACHE;

-- Alerta aberto pela Defesa Civil para um setor
CREATE TABLE tb_alerta (
    cd_alerta        NUMBER(9)     PRIMARY KEY,
    cd_setor         NUMBER(9)     NOT NULL,
    ds_nivel         VARCHAR2(10)  NOT NULL,
    dt_abertura      DATE          NOT NULL,
    dt_encerramento  DATE,
    st_alerta        VARCHAR2(10)  DEFAULT 'ABERTO' NOT NULL,
    CONSTRAINT fk_alerta_setor FOREIGN KEY (cd_setor) REFERENCES tb_setor (cd_setor),
    CONSTRAINT ck_alerta_nivel CHECK (ds_nivel IN ('AMARELO','LARANJA','VERMELHO')),
    CONSTRAINT ck_alerta_st CHECK (st_alerta IN ('ABERTO','ENCERRADO')),
    CONSTRAINT ck_alerta_datas CHECK (dt_encerramento IS NULL OR dt_encerramento >= dt_abertura)
);
COMMENT ON TABLE tb_alerta IS 'Alerta de risco por setor; amarelo = verificacao, laranja/vermelho = retirada';
CREATE SEQUENCE sq_tb_alerta START WITH 1 INCREMENT BY 1 NOCACHE;

-- Veiculo municipal disponivel para a operacao
CREATE TABLE tb_veiculo (
    cd_veiculo     NUMBER(9)     PRIMARY KEY,
    nm_veiculo     VARCHAR2(60)  NOT NULL,
    ds_tipo        VARCHAR2(20)  NOT NULL,
    qt_assentos    NUMBER(2)     NOT NULL,
    qt_cadeiras    NUMBER(2)     DEFAULT 0 NOT NULL,
    qt_macas       NUMBER(2)     DEFAULT 0 NOT NULL,
    in_disponivel  CHAR(1)       DEFAULT 'S' NOT NULL,
    CONSTRAINT ck_veiculo_tipo CHECK (ds_tipo IN ('AMBULANCIA','VAN_ADAPTADA','CARRO')),
    CONSTRAINT ck_veiculo_cap CHECK (qt_assentos >= 0 AND qt_cadeiras >= 0 AND qt_macas >= 0),
    CONSTRAINT ck_veiculo_disp CHECK (in_disponivel IN ('S','N'))
);
COMMENT ON TABLE tb_veiculo IS 'Veiculo com capacidades separadas: assentos, posicoes de cadeira de rodas e macas';
CREATE SEQUENCE sq_tb_veiculo START WITH 1 INCREMENT BY 1 NOCACHE;

-- Destino de acolhimento
CREATE TABLE tb_destino (
    cd_destino     NUMBER(9)      PRIMARY KEY,
    nm_destino     VARCHAR2(100)  NOT NULL,
    ds_tipo        VARCHAR2(20)   NOT NULL,
    ds_endereco    VARCHAR2(200)  NOT NULL,
    qt_vagas       NUMBER(4)      NOT NULL,
    in_acessivel   CHAR(1)        DEFAULT 'N' NOT NULL,
    in_disponivel  CHAR(1)        DEFAULT 'S' NOT NULL,
    CONSTRAINT ck_destino_tipo CHECK (ds_tipo IN ('CASA_FAMILIAR','ESCOLA_ABRIGO','ABRIGO_ACESSIVEL','UNIDADE_SAUDE')),
    CONSTRAINT ck_destino_vagas CHECK (qt_vagas >= 0),
    CONSTRAINT ck_destino_acess CHECK (in_acessivel IN ('S','N')),
    CONSTRAINT ck_destino_disp CHECK (in_disponivel IN ('S','N'))
);
COMMENT ON TABLE tb_destino IS 'Local para onde a pessoa e levada: casa de familiar, escola-abrigo, abrigo acessivel ou unidade de saude';
CREATE SEQUENCE sq_tb_destino START WITH 1 INCREMENT BY 1 NOCACHE;

-- Atendimento = um morador em um alerta (resolve o N:N Morador x Alerta)
CREATE TABLE tb_atendimento (
    cd_atendimento       NUMBER(9)     PRIMARY KEY,
    cd_morador           NUMBER(9)     NOT NULL,
    cd_alerta            NUMBER(9)     NOT NULL,
    cd_veiculo           NUMBER(9),
    cd_destino           NUMBER(9),
    nm_responsavel       VARCHAR2(80),
    st_atendimento       VARCHAR2(20)  DEFAULT 'AGUARDANDO' NOT NULL,
    ds_motivo_pendencia  VARCHAR2(30),
    dt_designacao        DATE,
    dt_embarque          DATE,
    dt_chegada           DATE,
    CONSTRAINT fk_atend_morador FOREIGN KEY (cd_morador) REFERENCES tb_morador (cd_morador),
    CONSTRAINT fk_atend_alerta  FOREIGN KEY (cd_alerta)  REFERENCES tb_alerta (cd_alerta),
    CONSTRAINT fk_atend_veiculo FOREIGN KEY (cd_veiculo) REFERENCES tb_veiculo (cd_veiculo),
    CONSTRAINT fk_atend_destino FOREIGN KEY (cd_destino) REFERENCES tb_destino (cd_destino),
    CONSTRAINT un_atend_morador_alerta UNIQUE (cd_morador, cd_alerta),        -- RN01
    CONSTRAINT ck_atend_st CHECK (st_atendimento IN
        ('AGUARDANDO','DESIGNADA','EQUIPE_A_CAMINHO','EMBARCADA','CHEGADA_CONFIRMADA','RECUSADO')),
    CONSTRAINT ck_atend_motivo CHECK (ds_motivo_pendencia IS NULL OR ds_motivo_pendencia IN
        ('SEM_CONTATO','ACESSO_IMPEDIDO','SEM_RECURSO','SAIDA_PROPRIA_PENDENTE')),
    CONSTRAINT ck_atend_datas CHECK (
        (dt_embarque IS NULL OR dt_designacao IS NULL OR dt_embarque >= dt_designacao) AND
        (dt_chegada  IS NULL OR dt_embarque   IS NULL OR dt_chegada  >= dt_embarque))    -- RN07
);
COMMENT ON TABLE tb_atendimento IS 'Acompanhamento de uma pessoa em um alerta, da espera ate a chegada confirmada';
CREATE SEQUENCE sq_tb_atendimento START WITH 1 INCREMENT BY 1 NOCACHE;

-- Carga minima para testar a API (a carga completa de 96 registros e entrega de Banco)
INSERT INTO tb_setor VALUES (sq_tb_setor.NEXTVAL, 'Setor 1 - Beira do corrego', 'Rua A ate Rua C', 'MUITO_ALTO');
INSERT INTO tb_setor VALUES (sq_tb_setor.NEXTVAL, 'Setor 2 - Encosta norte', 'Rua D ate Rua F', 'ALTO');
INSERT INTO tb_setor VALUES (sq_tb_setor.NEXTVAL, 'Setor 3 - Parte baixa', 'Rua G ate Rua J', 'MEDIO');

INSERT INTO tb_morador (cd_morador, cd_setor, nm_morador, ds_endereco, ds_contato, vl_latitude, vl_longitude,
  in_maca, in_cadeira_rodas, qt_acompanhante, ds_barreira_acesso, ds_codigo_confirmacao)
VALUES (sq_tb_morador.NEXTVAL, 1, 'Maria F. (ficticio)', 'Rua A, 12', '(11) 90000-0001', -23.550520, -46.633308,
  'S', 'N', 1, 'Escada na entrada', 'MOR-0001');
INSERT INTO tb_morador (cd_morador, cd_setor, nm_morador, ds_endereco, ds_contato, vl_latitude, vl_longitude,
  in_maca, in_cadeira_rodas, qt_acompanhante, ds_barreira_acesso, ds_codigo_confirmacao)
VALUES (sq_tb_morador.NEXTVAL, 1, 'Jose P. (ficticio)', 'Rua B, 40', '(11) 90000-0002', -23.551100, -46.634000,
  'N', 'S', 0, NULL, 'MOR-0002');

INSERT INTO tb_veiculo VALUES (sq_tb_veiculo.NEXTVAL, 'Ambulancia DC-01', 'AMBULANCIA', 2, 0, 1, 'S');
INSERT INTO tb_veiculo VALUES (sq_tb_veiculo.NEXTVAL, 'Van adaptada DC-02', 'VAN_ADAPTADA', 8, 2, 0, 'S');
INSERT INTO tb_veiculo VALUES (sq_tb_veiculo.NEXTVAL, 'Carro DC-03', 'CARRO', 4, 0, 0, 'S');

INSERT INTO tb_destino VALUES (sq_tb_destino.NEXTVAL, 'Escola Municipal (abrigo)', 'ESCOLA_ABRIGO', 'Rua K, 100', 120, 'N', 'S');
INSERT INTO tb_destino VALUES (sq_tb_destino.NEXTVAL, 'Abrigo acessivel CRAS', 'ABRIGO_ACESSIVEL', 'Rua L, 55', 30, 'S', 'S');

INSERT INTO tb_alerta VALUES (sq_tb_alerta.NEXTVAL, 1, 'VERMELHO', SYSDATE, NULL, 'ABERTO');
COMMIT;
