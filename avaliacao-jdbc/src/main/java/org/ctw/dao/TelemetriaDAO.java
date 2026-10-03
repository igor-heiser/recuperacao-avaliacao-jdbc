package org.ctw.dao;

import org.ctw.config.ConnectionFactory;
import org.ctw.exception.DatabaseException;
import org.ctw.model.HistoricoTelemetria;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TelemetriaDAO {
    public HistoricoTelemetria inserir(
            HistoricoTelemetria telemetria
    ) {
        String sql = """
                INSERT INTO historico_telemetria (
                    motor_id,
                    data_hora,
                    temperatura_carcaca,
                    rpm_atual,
                    corrente_fase_a,
                    corrente_fase_b,
                    corrente_fase_c,
                    vibracao_global
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
            preencherParametros(statement, telemetria);

            int linhasAfetadas = statement.executeUpdate();

            if (linhasAfetadas == 0) {
                throw new DatabaseException(
                        "Nenhuma telemetria foi cadastrada.",
                        null
                );
            }

            try (ResultSet chaves =
                         statement.getGeneratedKeys()) {

                if (chaves.next()) {
                    telemetria.setId(chaves.getLong(1));
                }
            }

            return telemetria;

        } catch (SQLException e) {
            throw new DatabaseException(
                    "Erro ao cadastrar a telemetria.", e
            );
        }
    }

    public List<HistoricoTelemetria> listarUltimas(
            int limite
    ) {
        String sql = """
                SELECT id,
                       motor_id,
                       data_hora,
                       temperatura_carcaca,
                       rpm_atual,
                       corrente_fase_a,
                       corrente_fase_b,
                       corrente_fase_c,
                       vibracao_global
                  FROM historico_telemetria
                 ORDER BY data_hora DESC
                 LIMIT ?
                """;

        List<HistoricoTelemetria> telemetrias =
                new ArrayList<>();

        try (
                Connection connection =
                        ConnectionFactory.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {
            statement.setInt(1, limite);

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                while (resultSet.next()) {
                    telemetrias.add(
                            mapearTelemetria(resultSet)
                    );
                }
            }

            return telemetrias;

        } catch (SQLException e) {
            throw new DatabaseException(
                    "Erro ao listar as telemetrias.", e
            );
        }
    }

    public Optional<HistoricoTelemetria> buscarPorId(
            Long id
    ) {
        String sql = """
                SELECT id,
                       motor_id,
                       data_hora,
                       temperatura_carcaca,
                       rpm_atual,
                       corrente_fase_a,
                       corrente_fase_b,
                       corrente_fase_c,
                       vibracao_global
                  FROM historico_telemetria
                 WHERE id = ?
                """;

        try (
                Connection connection =
                        ConnectionFactory.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {
            statement.setLong(1, id);

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                if (resultSet.next()) {
                    return Optional.of(
                            mapearTelemetria(resultSet)
                    );
                }

                return Optional.empty();
            }

        } catch (SQLException e) {
            throw new DatabaseException(
                    "Erro ao buscar a telemetria.", e
            );
        }
    }

    public List<HistoricoTelemetria> buscarPorMotor(
            Integer motorId
    ) {
        String sql = """
                SELECT id,
                       motor_id,
                       data_hora,
                       temperatura_carcaca,
                       rpm_atual,
                       corrente_fase_a,
                       corrente_fase_b,
                       corrente_fase_c,
                       vibracao_global
                  FROM historico_telemetria
                 WHERE motor_id = ?
                 ORDER BY data_hora DESC
                """;

        List<HistoricoTelemetria> telemetrias =
                new ArrayList<>();

        try (
                Connection connection =
                        ConnectionFactory.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {
            statement.setInt(1, motorId);

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                while (resultSet.next()) {
                    telemetrias.add(
                            mapearTelemetria(resultSet)
                    );
                }
            }

            return telemetrias;

        } catch (SQLException e) {
            throw new DatabaseException(
                    "Erro ao consultar as telemetrias "
                            + "do motor.",
                    e
            );
        }
    }

    public List<HistoricoTelemetria> buscarPorPeriodo(
            Integer motorId,
            LocalDateTime inicio,
            LocalDateTime fim
    ) {
        String sql = """
                SELECT id,
                       motor_id,
                       data_hora,
                       temperatura_carcaca,
                       rpm_atual,
                       corrente_fase_a,
                       corrente_fase_b,
                       corrente_fase_c,
                       vibracao_global
                  FROM historico_telemetria
                 WHERE motor_id = ?
                   AND data_hora BETWEEN ? AND ?
                 ORDER BY data_hora
                """;

        List<HistoricoTelemetria> telemetrias =
                new ArrayList<>();

        try (
                Connection connection =
                        ConnectionFactory.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {
            statement.setInt(1, motorId);

            statement.setTimestamp(
                    2,
                    Timestamp.valueOf(inicio)
            );

            statement.setTimestamp(
                    3,
                    Timestamp.valueOf(fim)
            );

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                while (resultSet.next()) {
                    telemetrias.add(
                            mapearTelemetria(resultSet)
                    );
                }
            }

            return telemetrias;

        } catch (SQLException e) {
            throw new DatabaseException(
                    "Erro ao consultar telemetrias "
                            + "por período.",
                    e
            );
        }
    }

    public boolean atualizar(
            HistoricoTelemetria telemetria
    ) {
        String sql = """
                UPDATE historico_telemetria
                   SET motor_id = ?,
                       data_hora = ?,
                       temperatura_carcaca = ?,
                       rpm_atual = ?,
                       corrente_fase_a = ?,
                       corrente_fase_b = ?,
                       corrente_fase_c = ?,
                       vibracao_global = ?
                 WHERE id = ?
                """;

        try (
                Connection connection =
                        ConnectionFactory.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {
            preencherParametros(statement, telemetria);
            statement.setLong(9, telemetria.getId());

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new DatabaseException(
                    "Erro ao atualizar a telemetria.", e
            );
        }
    }

    public boolean excluir(Long id) {
        String sql = """
                DELETE FROM historico_telemetria
                 WHERE id = ?
                """;

        try (
                Connection connection =
                        ConnectionFactory.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {
            statement.setLong(1, id);

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new DatabaseException(
                    "Erro ao excluir a telemetria.", e
            );
        }
    }

    private void preencherParametros(
            PreparedStatement statement,
            HistoricoTelemetria telemetria
    ) throws SQLException {

        statement.setInt(
                1,
                telemetria.getMotorId()
        );

        statement.setTimestamp(
                2,
                Timestamp.valueOf(
                        telemetria.getDataHora()
                )
        );

        statement.setBigDecimal(
                3,
                telemetria.getTemperaturaCarcaca()
        );

        statement.setInt(
                4,
                telemetria.getRpmAtual()
        );

        statement.setBigDecimal(
                5,
                telemetria.getCorrenteFaseA()
        );

        statement.setBigDecimal(
                6,
                telemetria.getCorrenteFaseB()
        );

        statement.setBigDecimal(
                7,
                telemetria.getCorrenteFaseC()
        );

        statement.setBigDecimal(
                8,
                telemetria.getVibracaoGlobal()
        );
    }
    private HistoricoTelemetria mapearTelemetria(
            ResultSet resultSet
    ) throws SQLException {

        HistoricoTelemetria telemetria =
                new HistoricoTelemetria();

        telemetria.setId(
                resultSet.getLong("id")
        );

        telemetria.setMotorId(
                resultSet.getInt("motor_id")
        );

        Timestamp dataHora =
                resultSet.getTimestamp("data_hora");

        if (dataHora != null) {
            telemetria.setDataHora(
                    dataHora.toLocalDateTime()
            );
        }

        telemetria.setTemperaturaCarcaca(
                resultSet.getBigDecimal(
                        "temperatura_carcaca"
                )
        );

        telemetria.setRpmAtual(
                resultSet.getInt("rpm_atual")
        );

        telemetria.setCorrenteFaseA(
                resultSet.getBigDecimal(
                        "corrente_fase_a"
                )
        );

        telemetria.setCorrenteFaseB(
                resultSet.getBigDecimal(
                        "corrente_fase_b"
                )
        );

        telemetria.setCorrenteFaseC(
                resultSet.getBigDecimal(
                        "corrente_fase_c"
                )
        );

        telemetria.setVibracaoGlobal(
                resultSet.getBigDecimal(
                        "vibracao_global"
                )
        );

        return telemetria;
    }

}
