package br.com.dio.board.ui;

import br.com.dio.board.service.BoardService;

import java.util.Scanner;

public class BoardMenu {

    private final BoardService boardService;
    private final Scanner scanner = new Scanner(System.in);

    public BoardMenu(BoardService boardService) {
        this.boardService = boardService;
    }

    public void start() {
        int option = -1;

        while (option != 0) {
            printMenu();
            option = readInt("Escolha uma opção: ");

            try {
                switch (option) {
                    case 1 -> createBoard();
                    case 2 -> listBoards();
                    case 3 -> showBoard();
                    case 4 -> createCard();
                    case 5 -> moveCard();
                    case 6 -> blockCard();
                    case 7 -> unblockCard();
                    case 0 -> System.out.println("Encerrando...");
                    default -> System.out.println("Opção inválida.");
                }
            } catch (Exception e) {
                System.out.println("Erro: " + e.getMessage());
            }
        }
    }

    private void printMenu() {
        System.out.println("""
                
                ===== MENU BOARD =====
                1 - Criar board
                2 - Listar boards
                3 - Exibir board
                4 - Criar card
                5 - Mover card
                6 - Bloquear card
                7 - Desbloquear card
                0 - Sair
                """);
    }

    private void createBoard() {
        System.out.print("Nome do board: ");
        String name = scanner.nextLine();
        Long boardId = boardService.createBoard(name);
        System.out.println("Board criado com sucesso. ID: " + boardId);
    }

    private void listBoards() {
        var boards = boardService.listBoards();

        if (boards.isEmpty()) {
            System.out.println("Nenhum board encontrado.");
            return;
        }

        System.out.println("\n===== BOARDS =====");
        boards.forEach(board ->
                System.out.println("ID: " + board.id() + " | Nome: " + board.name()));
    }

    private void showBoard() {
        Long boardId = readLong("Informe o ID do board: ");
        boardService.showBoard(boardId);
    }

    private void createCard() {
        Long boardId = readLong("Informe o ID do board: ");
        System.out.print("Título do card: ");
        String title = scanner.nextLine();
        System.out.print("Descrição do card: ");
        String description = scanner.nextLine();

        Long cardId = boardService.createCard(boardId, title, description);
        System.out.println("Card criado com sucesso. ID: " + cardId);
    }

    private void moveCard() {
        Long boardId = readLong("Informe o ID do board: ");
        Long cardId = readLong("Informe o ID do card: ");
        Long targetColumnId = readLong("Informe o ID da coluna de destino: ");

        boardService.moveCard(boardId, cardId, targetColumnId);
        System.out.println("Card movido com sucesso.");
    }

    private void blockCard() {
        Long cardId = readLong("Informe o ID do card: ");
        System.out.print("Motivo do bloqueio: ");
        String reason = scanner.nextLine();

        boardService.blockCard(cardId, reason);
        System.out.println("Card bloqueado com sucesso.");
    }

    private void unblockCard() {
        Long cardId = readLong("Informe o ID do card: ");
        System.out.print("Motivo do desbloqueio: ");
        String reason = scanner.nextLine();

        boardService.unblockCard(cardId, reason);
        System.out.println("Card desbloqueado com sucesso.");
    }

    private int readInt(String message) {
        while (true) {
            try {
                System.out.print(message);
                int value = Integer.parseInt(scanner.nextLine());
                return value;
            } catch (NumberFormatException e) {
                System.out.println("Digite um número inteiro válido.");
            }
        }
    }

    private Long readLong(String message) {
        while (true) {
            try {
                System.out.print(message);
                return Long.parseLong(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Digite um número válido.");
            }
        }
    }
}
