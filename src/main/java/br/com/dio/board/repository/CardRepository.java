package br.com.dio.board.repository;

import br.com.dio.board.config.ConnectionFactory;
import br.com.dio.board.model.Card;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CardRepository {

    public Long createCard(Long columnId, String title, String description) {
        String sql = "INSERT INTO cards (column_id, title, description) VALUES (?, ?, ?)";

        try (var connection = ConnectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setLong(1, columnId);
            statement.setString(2, title);
            statement.setString(3, description);
            statement.executeUpdate();

            try (var keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getLong(1);
                }
            }

            throw new IllegalStateException("Não foi possível obter o ID do card criado.");
        } catch (Exception e) {
            throw new RuntimeException("Erro ao criar card.", e);
        }
    }

    public List<Card> findCardsByColumnId(Long columnId) {
        String sql = """
                SELECT id, column_id, title, description, blocked, block_reason
                FROM cards
                WHERE column_id = ?
                ORDER BY id
                """;

        List<Card> cards = new ArrayList<>();

        try (var connection = ConnectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, columnId);

            try (var resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    cards.add(new Card(
                            resultSet.getLong("id"),
                            resultSet.getLong("column_id"),
                            resultSet.getString("title"),
                            resultSet.getString("description"),
                            resultSet.getBoolean("blocked"),
                            resultSet.getString("block_reason")
                    ));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao listar cards da coluna.", e);
        }

        return cards;
    }

    public Optional<Card> findCardById(Long cardId) {
        String sql = """
                SELECT id, column_id, title, description, blocked, block_reason
                FROM cards
                WHERE id = ?
                """;

        try (var connection = ConnectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, cardId);

            try (var resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(new Card(
                            resultSet.getLong("id"),
                            resultSet.getLong("column_id"),
                            resultSet.getString("title"),
                            resultSet.getString("description"),
                            resultSet.getBoolean("blocked"),
                            resultSet.getString("block_reason")
                    ));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar card por ID.", e);
        }

        return Optional.empty();
    }

    public void moveCard(Long cardId, Long targetColumnId) {
        String sql = "UPDATE cards SET column_id = ? WHERE id = ?";

        try (var connection = ConnectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, targetColumnId);
            statement.setLong(2, cardId);
            statement.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao mover card.", e);
        }
    }

    public void blockCard(Long cardId, String reason) {
        String updateSql = "UPDATE cards SET blocked = true, block_reason = ? WHERE id = ?";
        String historySql = "INSERT INTO card_block_history (card_id, action_type, reason) VALUES (?, 'BLOCK', ?)";

        try (var connection = ConnectionFactory.getConnection()) {
            connection.setAutoCommit(false);

            try (PreparedStatement updateStatement = connection.prepareStatement(updateSql);
                 PreparedStatement historyStatement = connection.prepareStatement(historySql)) {

                updateStatement.setString(1, reason);
                updateStatement.setLong(2, cardId);
                updateStatement.executeUpdate();

                historyStatement.setLong(1, cardId);
                historyStatement.setString(2, reason);
                historyStatement.executeUpdate();

                connection.commit();
            } catch (Exception e) {
                connection.rollback();
                throw e;
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao bloquear card.", e);
        }
    }

    public void unblockCard(Long cardId, String reason) {
        String updateSql = "UPDATE cards SET blocked = false, block_reason = NULL WHERE id = ?";
        String historySql = "INSERT INTO card_block_history (card_id, action_type, reason) VALUES (?, 'UNBLOCK', ?)";

        try (var connection = ConnectionFactory.getConnection()) {
            connection.setAutoCommit(false);

            try (PreparedStatement updateStatement = connection.prepareStatement(updateSql);
                 PreparedStatement historyStatement = connection.prepareStatement(historySql)) {

                updateStatement.setLong(1, cardId);
                updateStatement.executeUpdate();

                historyStatement.setLong(1, cardId);
                historyStatement.setString(2, reason);
                historyStatement.executeUpdate();

                connection.commit();
            } catch (Exception e) {
                connection.rollback();
                throw e;
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao desbloquear card.", e);
        }
    }
}
