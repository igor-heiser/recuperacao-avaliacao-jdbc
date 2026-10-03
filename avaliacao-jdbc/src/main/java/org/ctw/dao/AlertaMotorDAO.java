package org.ctw.dao;

import org.ctw.config.ConnectionFactory;
import org.ctw.exception.DatabaseException;
import org.ctw.model.AlertaMotor;
import org.ctw.model.Motor;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Timer;

public class AlertaMotorDAO {

    public AlertaMotor inserir(AlertaMotor alerta) {
        String sql = """
                INSERT INTO alertas_motores (
                    motor_id,
                    data_alerta,
                    tipo_anomalia,
                    criticidade,
                    descricao,
                    resolvido
                )
                VALUES (?, ?, ?, ?, ?, ?)
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
            preencherParametros(statement, alerta);

            int linhasAfetadas = statement.executeUpdate();

            if (linhasAfetadas == 0) {
                throw new DatabaseException(
                        "Nenhum alerta foi inserido.",
                        null
                );
            }

            try (ResultSet keys =
                         statement.getGeneratedKeys()) {

                if (keys.next()) {
                    alerta.setId(keys.getInt(1));
                }
            }

            return alerta;

        } catch (SQLException e) {
            throw new DatabaseException(
                    "Erro ao inserir o alerta.", e
            );
        }
    }

    public List<AlertaMotor> listarTodos() {
        String sql = """
                SELECT id,
                   motor_id,
                   data_alerta,
                   tipo_anomalia,
                   criticidade,
                   descricao,
                   resolvido
                  FROM alertas_motores
                 ORDER BY data_alerta
                """;

        List<AlertaMotor> alertas = new ArrayList<>();

        try (
                Connection connection =
                        ConnectionFactory.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql);

                ResultSet resultSet =
                        statement.executeQuery()
        ) {
            while (resultSet.next()) {
                alertas.add(mapearAlerta(resultSet));
            }

            return alertas;

        } catch (SQLException e) {
            throw new DatabaseException(
                    "Erro ao listar os alertas.", e
            );
        }
    }

    public Optional<AlertaMotor> buscarPorId(Integer id) {
        String sql = """
                SELECT id,
                   motor_id,
                   data_alerta,
                   tipo_anomalia,
                   criticidade,
                   descricao,
                   resolvido
                  FROM alertas_motores
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
                            mapearAlerta(resultSet)
                    );
                }

                return Optional.empty();
            }

        } catch (SQLException e) {
            throw new DatabaseException(
                    "Erro ao buscar o alerta.", e
            );
        }
    }

    public List<AlertaMotor> listarNaoResolvidos() {
        String sql = """
                SELECT id,
                   motor_id,
                   data_alerta,
                   tipo_anomalia,
                   criticidade,
                   descricao,
                   resolvido
                  FROM alertas_motores
                 WHERE resolvido is false
                """;

        List<AlertaMotor> alertas = new ArrayList<>();

        try (
                Connection connection =
                        ConnectionFactory.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql);

                ResultSet resultSet =
                        statement.executeQuery()
        ) {
            while (resultSet.next()) {
                alertas.add(mapearAlerta(resultSet));
            }

            return alertas;

        } catch (SQLException e) {
            throw new DatabaseException(
                    "Erro ao listar os alertas não resolvidos.", e
            );
        }
    }

    public List<AlertaMotor> buscarPorCriticidade(String criticidade) {
        String sql = """
                SELECT id,
                   motor_id,
                   data_alerta,
                   tipo_anomalia,
                   criticidade,
                   descricao,
                   resolvido
                  FROM alertas_motores
                 WHERE criticidade LIKE ?
                """;

        List<AlertaMotor> alertas = new ArrayList<>();

        try (
                Connection connection =
                        ConnectionFactory.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {
            statement.setString(1, "%" + criticidade + "%");

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                while (resultSet.next()) {
                    alertas.add(mapearAlerta(resultSet));
                }
            }

            return alertas;

        } catch (SQLException e) {
            throw new DatabaseException(
                    "Erro ao filtrar os alertas por criticidade.", e
            );
        }
    }

    public boolean marcarComoResolvido(Integer id) {
        String sql = """
                UPDATE alertas_motores
                   SET resolvido = true
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
                    "Erro ao atualizar o alerta.", e
            );
        }
    }

    public boolean excluir(Integer id) {
        String sql = """
                DELETE FROM alertas_motores
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
                    "Erro ao excluir o alerta.", e
            );
        }
    }

    private AlertaMotor mapearAlerta(ResultSet resultSet) throws SQLException {
        AlertaMotor alertaMotor = new AlertaMotor();

        alertaMotor.setId(resultSet.getInt("id"));

        int motorId = resultSet.getInt("motor_id");

        if (!resultSet.wasNull()) {
            alertaMotor.setMotorId(motorId);
        }

        Timestamp data = resultSet.getTimestamp("data_alerta");

        if (data != null) {
            alertaMotor.setDataAlerta(
                    data.toLocalDateTime()
            );
        }

        alertaMotor.setTipoAnomalia(
                resultSet.getString("tipo_anomalia")
        );

        alertaMotor.setCriticidade(
                resultSet.getString("criticidade")
        );

        alertaMotor.setDescricao(
                resultSet.getString("descricao")
        );

        alertaMotor.setResolvido(
                resultSet.getBoolean("resolvido")
        );

        return alertaMotor;
    }

    private void preencherParametros(
            PreparedStatement statement,
            AlertaMotor alertaMotor
    ) throws SQLException {

        if (alertaMotor.getMotorId() == null) {
            statement.setNull(1, Types.INTEGER);
        } else {
            statement.setInt(1, alertaMotor.getMotorId());
        }

        if (alertaMotor.getDataAlerta() == null) {
            statement.setNull(2, Types.DATE);
        } else {
            statement.setTimestamp(
                    2,
                    Timestamp.valueOf(alertaMotor.getDataAlerta())
            );
        }

        statement.setString(3, alertaMotor.getTipoAnomalia());
        statement.setString(4, alertaMotor.getCriticidade());
        statement.setString(5, alertaMotor.getDescricao());
        statement.setBoolean(6, alertaMotor.isResolvido());
    }

}