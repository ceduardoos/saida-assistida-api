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
