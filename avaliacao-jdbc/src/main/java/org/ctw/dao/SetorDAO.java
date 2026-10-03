package org.ctw.dao;

import org.ctw.config.ConnectionFactory;
import org.ctw.exception.DatabaseException;
import org.ctw.model.Setor;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SetorDAO {

    public Setor inserir(Setor setor) {
        String sql = """
                INSERT INTO setores (
                    nome,
                    localizacao
                )
                VALUES (?, ?)
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
            statement.setString(1, setor.getNome());
            statement.setString(
                    2,
                    setor.getLocalizacao()
            );

            int linhasAfetadas = statement.executeUpdate();

            if (linhasAfetadas == 0) {
                throw new DatabaseException(
                        "Nenhum setor foi cadastrado.",
                        null
                );
            }

            try (ResultSet chaves =
                         statement.getGeneratedKeys()) {

                if (chaves.next()) {
                    setor.setId(chaves.getInt(1));
                }
            }

            return setor;

        } catch (SQLException e) {
            throw new DatabaseException(
                    "Erro ao cadastrar o setor.", e
            );
        }
    }

    public List<Setor> listarTodos() {
        String sql =
                """
                        SELECT id,
                               nome,
                                                localizacao
                                     FROM setores
                                    ORDER BY nome
                        """;

        List<Setor> setores = new ArrayList<>();

        try (
                Connection connection =
                        ConnectionFactory.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql);

                ResultSet resultSet =
                        statement.executeQuery()
        ) {
            while (resultSet.next()) {
                setores.add(mapearSetor(resultSet));
            }

            return setores;

        } catch (SQLException e) {
            throw new DatabaseException(
                    "Erro ao listar os setores.", e
            );
        }
    }

    public Optional<Setor> buscarPorId(Integer id) {
        String sql =
                """
                        SELECT id,
                        nome,
                        localizacao
                          FROM setores
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
                            mapearSetor(resultSet)
                    );
                }

                return Optional.empty();
            }

        } catch (SQLException e) {
            throw new DatabaseException(
                    "Erro ao buscar o setor.", e
            );
        }
    }

    public List<Setor> buscarPorNome(String nome) {
        String sql =
                """
                        
                                SELECT
                                id,
                                           nome,
                                         localizacao
                                    FROM setores
                                   WHERE nome LIKE ?
                         ORDER BY nome
                        """;

        List<Setor> setores = new ArrayList<>();

        try (
                Connection connection =
                        ConnectionFactory.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {
            statement.setString(1, "%" + nome + "%");

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                while (resultSet.next()) {
                    setores.add(mapearSetor(resultSet));
                }
            }

            return setores;

        } catch (SQLException e) {
            throw new DatabaseException(
                    "Erro ao pesquisar os setores.", e
            );
        }
    }

    public boolean
    atualizar(Setor setor
    ) {
        String sql = """
                
                        UPDATE setores
                   SET nome = ?,
                       localizacao = ?
                 WHERE id = ?
                """;

        try (
                Connection connection =
                        ConnectionFactory.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {
            statement.setString(1, setor.getNome());

            statement.setString(
                    2,
                    setor.getLocalizacao()
            );

            statement.setInt(3, setor.getId());

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new DatabaseException(
                    "Erro ao atualizar o setor.", e
            );
        }
    }

    public boolean excluir(Integer id) {
        String sql = """
                DELETE FROM setores
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
                    "Não foi possível excluir o setor. "
                            + "Verifique se existem motores associados.",
                    e
            );
        }
    }

    public boolean
    possuiMotores(Integer
                          setorId) {
        String sql = """
                
                        SELECT EXISTS (
                    SELECT 1
                      FROM motores
                     WHERE setor_id = ?
                ) AS possui_motores
                """;

        try (
                Connection connection =
                        ConnectionFactory.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {
            statement.setInt(1, setorId);

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                resultSet.next();

                return resultSet.getBoolean(
                        "possui_motores"
                );
            }

        } catch (SQLException e) {
            throw new DatabaseException(
                    "Erro ao verificar os motores do setor.",
                    e
            );
        }
    }

    private Setor mapearSetor(ResultSet resultSet)
            throws SQLException {

        Setor setor = new Setor();

        setor.setId(resultSet.getInt("id"));
        setor.setNome(resultSet.
                getString("nome"));
        setor.setLocalizacao(
                resultSet.getString("localizacao")
        );

        return setor;
    }
}

