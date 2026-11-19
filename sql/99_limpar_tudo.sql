-- Apaga tabelas e sequences da API para recomecar do zero.
-- Depois, rode 00_rodar_tudo.sql de novo (F5).
DROP TABLE tb_atendimento CASCADE CONSTRAINTS;
DROP TABLE tb_destino     CASCADE CONSTRAINTS;
DROP TABLE tb_veiculo     CASCADE CONSTRAINTS;
DROP TABLE tb_alerta      CASCADE CONSTRAINTS;
DROP TABLE tb_morador     CASCADE CONSTRAINTS;
DROP TABLE tb_setor       CASCADE CONSTRAINTS;
DROP SEQUENCE sq_tb_atendimento;
DROP SEQUENCE sq_tb_destino;
DROP SEQUENCE sq_tb_veiculo;
DROP SEQUENCE sq_tb_alerta;
DROP SEQUENCE sq_tb_morador;
DROP SEQUENCE sq_tb_setor;
