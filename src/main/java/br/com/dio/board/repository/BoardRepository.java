package br.com.dio.board.repository;

import br.com.dio.board.config.ConnectionFactory;
import br.com.dio.board.model.Board;
import br.com.dio.board.model.BoardColumn;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BoardRepository {

    public Long createBoard(String name) {
        String sql = "INSERT INTO boards (name) VALUES (?)";

        try (var connection = ConnectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, name);
            statement.executeUpdate();

            try (var keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    Long boardId = keys.getLong(1);
                    createDefaultColumns(boardId);
                    return boardId;
                }
            }

            throw new IllegalStateException("Não foi possível obter o ID do board criado.");
        } catch (Exception e) {
            throw new RuntimeException("Erro ao criar board.", e);
        }
    }

    private void createDefaultColumns(Long boardId) {
        String sql = "INSERT INTO board_columns (board_id, name, kind, position_index) VALUES (?, ?, ?, ?)";

        try (var connection = ConnectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, boardId);
            statement.setString(2, "A Fazer");
            statement.setString(3, "INITIAL");
            statement.setInt(4, 1);
            statement.addBatch();

            statement.setLong(1, boardId);
            statement.setString(2, "Em Progresso");
            statement.setString(3, "PENDING");
            statement.setInt(4, 2);
            statement.addBatch();

            statement.setLong(1, boardId);
            statement.setString(2, "Concluído");
            statement.setString(3, "FINAL");
            statement.setInt(4, 3);
            statement.addBatch();

            statement.executeBatch();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao criar colunas padrão do board.", e);
        }
    }

    public List<Board> findAllBoards() {
        String sql = "SELECT id, name FROM boards ORDER BY id";
        List<Board> boards = new ArrayList<>();

        try (var connection = ConnectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             var resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                boards.add(new Board(
                        resultSet.getLong("id"),
                        resultSet.getString("name")
                ));
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao listar boards.", e);
        }

        return boards;
    }

    public Optional<Board> findBoardById(Long boardId) {
        String sql = "SELECT id, name FROM boards WHERE id = ?";

        try (var connection = ConnectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, boardId);

            try (var resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(new Board(
                            resultSet.getLong("id"),
                            resultSet.getString("name")
                    ));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar board por ID.", e);
        }

        return Optional.empty();
    }

    public List<BoardColumn> findColumnsByBoardId(Long boardId) {
        String sql = """
                SELECT id, board_id, name, kind, position_index
                FROM board_columns
                WHERE board_id = ?
                ORDER BY position_index
                """;

        List<BoardColumn> columns = new ArrayList<>();

        try (var connection = ConnectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, boardId);

            try (var resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    columns.add(new BoardColumn(
                            resultSet.getLong("id"),
                            resultSet.getLong("board_id"),
                            resultSet.getString("name"),
                            resultSet.getString("kind"),
                            resultSet.getInt("position_index")
                    ));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar colunas do board.", e);
        }

        return columns;
    }

    public Optional<Long> findInitialColumnId(Long boardId) {
        String sql = """
                SELECT id
                FROM board_columns
                WHERE board_id = ? AND kind = 'INITIAL'
                LIMIT 1
                """;

        try (var connection = ConnectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, boardId);

            try (var resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(resultSet.getLong("id"));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar coluna inicial.", e);
        }

        return Optional.empty();
    }

    public boolean existsColumnInBoard(Long boardId, Long columnId) {
        String sql = "SELECT COUNT(*) total FROM board_columns WHERE board_id = ? AND id = ?";

        try (var connection = ConnectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, boardId);
            statement.setLong(2, columnId);

            try (var resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("total") > 0;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao validar coluna do board.", e);
        }

        return false;
    }
}
