package org.ctw.dao;

import org.ctw.config.ConnectionFactory;
import org.ctw.exception.DatabaseException;
import org.ctw.model.Motor;
import org.ctw.model.MotorResumo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MotorDAO {
    public List<Motor> listarTodos() {
        String sql = """
                SELECT id,
                       setor_id,
                       codigo_ativo,
                       fabricante,
                       modelo,
                       potencia_kw,
                       rpm_nominal,
                       data_instalacao,
                       status_atual
                  FROM motores
                 ORDER BY codigo_ativo
                """;

        List<Motor> motores = new ArrayList<>();

        try (
                Connection connection =
                        ConnectionFactory.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql);

                ResultSet resultSet =
                        statement.executeQuery()
        ) {
            while (resultSet.next()) {
                motores.add(mapearMotor(resultSet));
            }

            return motores;

        } catch (SQLException e) {
            throw new DatabaseException(
                    "Erro ao listar os motores.", e
            );
        }
    }

    public Optional<Motor> buscarPorId(Integer id) {
        String sql = """
                SELECT id,
                       setor_id,
                       codigo_ativo,
                       fabricante,
                       modelo,
                       potencia_kw,
                       rpm_nominal,
                       data_instalacao,
                       status_atual
                  FROM motores
                 WHERE id = ?
                """;

        try (
                Connection connection =
                        ConnectionFactory.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {
            statement.setInt(1, id);

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                if (resultSet.next()) {
                    return Optional.of(
                            mapearMotor(resultSet)
                    );
                }

                return Optional.empty();
            }

        } catch (SQLException e) {
            throw new DatabaseException(
                    "Erro ao buscar o motor.", e
            );
        }
    }

    public Motor inserir(Motor motor) {
        String sql = """
                INSERT INTO motores (
                    setor_id,
                    codigo_ativo,
                    fabricante,
                    modelo,
                    potencia_kw,
                    rpm_nominal,
                    data_instalacao,
                    status_atual
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (
                Connection connection =
                        ConnectionFactory.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(
                                sql,
                                Statement.RETURN_GENERATED_KEYS
                        )
        ) {
            preencherParametros(statement, motor);

            int linhasAfetadas = statement.executeUpdate();

            if (linhasAfetadas == 0) {
                throw new DatabaseException(
                        "Nenhum motor foi inserido.",
                        null
                );
            }

            try (ResultSet keys =
                         statement.getGeneratedKeys()) {

                if (keys.next()) {
                    motor.setId(keys.getInt(1));
                }
            }

            return motor;

        } catch (SQLException e) {
            throw new DatabaseException(
                    "Erro ao inserir o motor.", e
            );
        }
    }

    public boolean atualizar(Motor motor) {
        String sql = """
                UPDATE motores
                   SET setor_id = ?,
                       codigo_ativo = ?,
                       fabricante = ?,
                       modelo = ?,
                       potencia_kw = ?,
                       rpm_nominal = ?,
                       data_instalacao = ?,
                       status_atual = ?
                 WHERE id = ?
                """;

        try (
                Connection connection =
                        ConnectionFactory.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {
            preencherParametros(statement, motor);
            statement.setInt(9, motor.getId());

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new DatabaseException(
                    "Erro ao atualizar o motor.", e
            );
        }
    }

    public boolean excluir(Integer id) {
        String sql = """
                DELETE FROM motores
                 WHERE id = ?
                """;

        try (
                Connection connection =
                        ConnectionFactory.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {
            statement.setInt(1, id);

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new DatabaseException(
                    "Não foi possível excluir o motor. "
                            + "Verifique se existem telemetrias "
                            + "ou alertas associados.",
                    e
            );
        }
    }

    public List<Motor> filtrar(
            String fabricante,
            String status
    ) {
        String sql = """
                SELECT id,
                       setor_id,
                       codigo_ativo,
                       fabricante,
                       modelo,
                       potencia_kw,
                       rpm_nominal,
                       data_instalacao,
                       status_atual
                  FROM motores
                 WHERE fabricante LIKE ?
                   AND status_atual = ?
                 ORDER BY codigo_ativo
                """;

        List<Motor> motores = new ArrayList<>();

        try (
                Connection connection =
                        ConnectionFactory.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {
            statement.setString(1, "%" + fabricante + "%");
            statement.setString(2, status);

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                while (resultSet.next()) {
                    motores.add(mapearMotor(resultSet));
                }
            }

            return motores;

        } catch (SQLException e) {
            throw new DatabaseException(
                    "Erro ao filtrar os motores.", e
            );
        }
    }

    /**
     * Queremos exibir:
     ** todos os motores;
     ** setor e localização;
     ** quantidade de telemetrias;
     ** média de temperatura;
     ** média de vibração.
     */

    public List<MotorResumo> gerarResumo() {
        String sql = """
                SELECT
                    m.id AS motor_id,
                    m.codigo_ativo,
                    m.fabricante,
                    m.status_atual,
                    s.nome AS setor,
                    s.localizacao,
                    COALESCE(t.quantidade_leituras, 0)
                        AS quantidade_leituras,
                    t.media_temperatura,
                    t.media_vibracao,
                    COALESCE(a.alertas_pendentes, 0)
                     AS alertas_pendentes
                       FROM motores m
                       LEFT JOIN setores s
                              ON s.id = m.setor_id
                       LEFT JOIN (
                           SELECT
                      motor_id,
                      COUNT(*) AS quantidade_leituras,
                      ROUND(
                          AVG(temperatura_carcaca), 2
                      ) AS media_temperatura,
                      ROUND(
                          AVG(vibracao_global), 2
                      ) AS media_vibracao
                             FROM historico_telemetria
                             GROUP BY motor_id
                         ) t ON t.motor_id = m.id
                      LEFT JOIN (
                          SELECT
                              motor_id,
                              COUNT(*) AS alertas_pendentes
                       FROM alertas_motores
                       WHERE resolvido = FALSE
                       GROUP BY motor_id
                      ) a ON a.motor_id = m.id
                      ORDER BY m.codigo_ativo;
                """;

        List<MotorResumo> resumos = new ArrayList<>();

        try (
                Connection connection =
                        ConnectionFactory.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql);

                ResultSet resultSet =
                        statement.executeQuery()
        ) {
            while (resultSet.next()) {
                MotorResumo resumo = new MotorResumo();

                resumo.setMotorId(
                        resultSet.getInt("motor_id")
                );
                resumo.setCodigoAtivo(
                        resultSet.getString("codigo_ativo")
                );
                resumo.setFabricante(
                        resultSet.getString("fabricante")
                );
                resumo.setStatus(
                        resultSet.getString("status_atual")
                );
                resumo.setSetor(
                        resultSet.getString("setor")
                );
                resumo.setLocalizacao(
                        resultSet.getString("localizacao")
                );
                resumo.setQuantidadeLeituras(
                        resultSet.getLong(
                                "quantidade_leituras"
                        )
                );
                resumo.setMediaTemperatura(
                        resultSet.getBigDecimal(
                                "media_temperatura"
                        )
                );
                resumo.setMediaVibracao(
                        resultSet.getBigDecimal(
                                "media_vibracao"
                        )
                );

                resumos.add(resumo);
            }

            return resumos;

        } catch (SQLException e) {
            throw new DatabaseException(
                    "Erro ao gerar o resumo.", e
            );
        }
    }


    private Motor mapearMotor(ResultSet resultSet)
            throws SQLException {

        Motor motor = new Motor();

        motor.setId(resultSet.getInt("id"));

        int setorId = resultSet.getInt("setor_id");

        if (!resultSet.wasNull()) {
            motor.setSetorId(setorId);
        }

        motor.setCodigoAtivo(
                resultSet.getString("codigo_ativo")
        );
        motor.setFabricante(
                resultSet.getString("fabricante")
        );
        motor.setModelo(
                resultSet.getString("modelo")
        );
        motor.setPotenciaKw(
                resultSet.getBigDecimal("potencia_kw")
        );
        motor.setRpmNominal(
                resultSet.getInt("rpm_nominal")
        );

        Date data = resultSet.getDate("data_instalacao");

        if (data != null) {
            motor.setDataInstalacao(
                    data.toLocalDate()
            );
        }

        motor.setStatusAtual(
                resultSet.getString("status_atual")
        );

        return motor;
    }

    private void preencherParametros(
            PreparedStatement statement,
            Motor motor
    ) throws SQLException {

        if (motor.getSetorId() == null) {
            statement.setNull(1, Types.INTEGER);
        } else {
            statement.setInt(1, motor.getSetorId());
        }

        statement.setString(2, motor.getCodigoAtivo());
        statement.setString(3, motor.getFabricante());
        statement.setString(4, motor.getModelo());
        statement.setBigDecimal(5, motor.getPotenciaKw());
        statement.setInt(6, motor.getRpmNominal());

        if (motor.getDataInstalacao() == null) {
            statement.setNull(7, Types.DATE);
        } else {
            statement.setDate(
                    7,
                    Date.valueOf(motor.getDataInstalacao())
            );
        }

        statement.setString(8, motor.getStatusAtual());
    }
}

