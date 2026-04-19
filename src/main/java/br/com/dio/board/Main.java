package br.com.dio.board;

import br.com.dio.board.config.DatabaseMigrationManager;
import br.com.dio.board.repository.BoardRepository;
import br.com.dio.board.repository.CardRepository;
import br.com.dio.board.service.BoardService;
import br.com.dio.board.ui.BoardMenu;

public class Main {
    public static void main(String[] args) {
        try {
            DatabaseMigrationManager.migrate();

            var boardRepository = new BoardRepository();
            var cardRepository = new CardRepository();
            var boardService = new BoardService(boardRepository, cardRepository);
            var menu = new BoardMenu(boardService);

            menu.start();
        } catch (Exception e) {
            System.err.println("Erro ao iniciar a aplicação: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
