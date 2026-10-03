-- Criar e selecionar o banco
CREATE DATABASE IF NOT EXISTS TelemetriaMotores;
USE TelemetriaMotores;

-- TABELA: Setores
CREATE TABLE setores (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    localizacao VARCHAR(100)
);

-- TABELA: Motores
CREATE TABLE motores (
    id INT AUTO_INCREMENT PRIMARY KEY,
    setor_id INT,
    codigo_ativo VARCHAR(50) UNIQUE NOT NULL,
    fabricante VARCHAR(100),
    modelo VARCHAR(100),
    potencia_kw DECIMAL(6,2),
    rpm_nominal INT,
    data_instalacao DATE,
    status_atual VARCHAR(30) DEFAULT 'Operando',
    FOREIGN KEY (setor_id) REFERENCES setores(id)
);

-- TABELA: Telemetria (Dados dos Sensores)
CREATE TABLE historico_telemetria (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    motor_id INT,
    data_hora DATETIME DEFAULT CURRENT_TIMESTAMP,
    temperatura_carcaca DECIMAL(5,2),
    rpm_atual INT,
    corrente_fase_a DECIMAL(5,2),
    corrente_fase_b DECIMAL(5,2),
    corrente_fase_c DECIMAL(5,2),
    vibracao_global DECIMAL(5,2),
    FOREIGN KEY (motor_id) REFERENCES motores(id)
);

-- TABELA: Alertas
CREATE TABLE alertas_motores (
    id INT AUTO_INCREMENT PRIMARY KEY,
    motor_id INT,
    data_alerta DATETIME DEFAULT CURRENT_TIMESTAMP,
    tipo_anomalia VARCHAR(100),
    criticidade VARCHAR(20),
    descricao TEXT,
    resolvido BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (motor_id) REFERENCES motores(id)
);

-- INSERTS INICIAIS
INSERT INTO setores (nome, localizacao) VALUES 
('Linha de Usinagem Heavy Duty', 'Galpão A'),
('Sistema de Refrigeração Central (Chillers)', 'Área Externa'),
('Moagem de Insumos', 'Galpão B');

INSERT INTO motores (setor_id, codigo_ativo, fabricante, modelo, potencia_kw, rpm_nominal, data_instalacao, status_atual) VALUES 
(1, 'MOT-USI-01', 'WEG', 'W22 Magnet', 45.00, 1800, '2024-02-10', 'Operando'),
(1, 'MOT-USI-02', 'Siemens', '1LE1', 37.00, 3600, '2024-05-18', 'Alerta'),
(2, 'MOT-CHILL-01', 'WEG', 'W22 Premium', 75.00, 1780, '2023-11-01', 'Operando'),
(3, 'MOT-MOE-01', 'ABB', 'M3BP', 110.00, 1180, '2025-01-15', 'Manutenção');

-- PROCEDURE PARA GERAR OS 500 INSERTS
DELIMITER $$
CREATE PROCEDURE PopularTelemetria500()
BEGIN
    DECLARE i INT DEFAULT 1;
    DECLARE motor_selecionado INT;
    DECLARE data_registro DATETIME;
    
    SET data_registro = DATE_SUB(NOW(), INTERVAL 5 DAY);

    WHILE i <= 500 DO
        SET motor_selecionado = (i % 3) + 1;
        SET data_registro = DATE_ADD(data_registro, INTERVAL 15 MINUTE);
        
        IF motor_selecionado = 1 THEN
            INSERT INTO historico_telemetria 
                (motor_id, data_hora, temperatura_carcaca, rpm_atual, corrente_fase_a, corrente_fase_b, corrente_fase_c, vibracao_global)
            VALUES (1, data_registro, ROUND(55 + (RAND() * 5), 2), ROUND(1790 + (RAND() * 10)), ROUND(81 + (RAND() * 2), 2), ROUND(81 + (RAND() * 2), 2), ROUND(81 + (RAND() * 2), 2), ROUND(1.5 + (RAND() * 0.8), 2));
        ELSEIF motor_selecionado = 2 THEN
            INSERT INTO historico_telemetria 
                (motor_id, data_hora, temperatura_carcaca, rpm_atual, corrente_fase_a, corrente_fase_b, corrente_fase_c, vibracao_global)
            VALUES (2, data_registro, ROUND(75 + (RAND() * 15), 2), ROUND(3560 + (RAND() * 35)), ROUND(67 + (RAND() * 3), 2), ROUND(75 + (RAND() * 8), 2), ROUND(66 + (RAND() * 3), 2), ROUND(4.0 + (RAND() * 4.5), 2));
        ELSE
            INSERT INTO historico_telemetria 
                (motor_id, data_hora, temperatura_carcaca, rpm_atual, corrente_fase_a, corrente_fase_b, corrente_fase_c, vibracao_global)
            VALUES (3, data_registro, ROUND(62 + (RAND() * 6), 2), ROUND(1765 + (RAND() * 10)), ROUND(134 + (RAND() * 4), 2), ROUND(134 + (RAND() * 4), 2), ROUND(134 + (RAND() * 4), 2), ROUND(2.0 + (RAND() * 0.7), 2));
        END IF;
        
        SET i = i + 1;
    END WHILE;
END$$
DELIMITER ;

-- Executa e limpa a procedure
CALL PopularTelemetria500();
DROP PROCEDURE PopularTelemetria500;

-- Alerta inicial do Motor 2
INSERT INTO alertas_motores (motor_id, data_alerta, tipo_anomalia, criticidade, descricao) VALUES 
(2, NOW(), 'Alta Vibração Mecânica e Sobrecarga de Fase', 'Crítica', 'Vibração atingiu níveis elevados na última hora de análise automatizada.');
