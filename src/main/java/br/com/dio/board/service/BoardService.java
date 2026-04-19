package br.com.dio.board.service;

import br.com.dio.board.model.Board;
import br.com.dio.board.model.BoardColumn;
import br.com.dio.board.model.Card;
import br.com.dio.board.repository.BoardRepository;
import br.com.dio.board.repository.CardRepository;

import java.util.List;

public class BoardService {

    private final BoardRepository boardRepository;
    private final CardRepository cardRepository;

    public BoardService(BoardRepository boardRepository, CardRepository cardRepository) {
        this.boardRepository = boardRepository;
        this.cardRepository = cardRepository;
    }

    public Long createBoard(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("O nome do board não pode ser vazio.");
        }
        return boardRepository.createBoard(name.trim());
    }

    public List<Board> listBoards() {
        return boardRepository.findAllBoards();
    }

    public void showBoard(Long boardId) {
        Board board = boardRepository.findBoardById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("Board não encontrado."));

        System.out.println("\n===== BOARD =====");
        System.out.println("ID: " + board.id());
        System.out.println("Nome: " + board.name());

        List<BoardColumn> columns = boardRepository.findColumnsByBoardId(boardId);

        for (BoardColumn column : columns) {
            System.out.println("\n[" + column.id() + "] " + column.name() + " - " + column.kind());

            List<Card> cards = cardRepository.findCardsByColumnId(column.id());
            if (cards.isEmpty()) {
                System.out.println("  (sem cards)");
                continue;
            }

            for (Card card : cards) {
                System.out.println("  Card ID: " + card.id());
                System.out.println("  Título: " + card.title());
                System.out.println("  Descrição: " + card.description());
                System.out.println("  Bloqueado: " + card.blocked());
                if (card.blockReason() != null) {
                    System.out.println("  Motivo do bloqueio: " + card.blockReason());
                }
                System.out.println();
            }
        }
    }

    public Long createCard(Long boardId, String title, String description) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("O título do card não pode ser vazio.");
        }

        Long initialColumnId = boardRepository.findInitialColumnId(boardId)
                .orElseThrow(() -> new IllegalArgumentException("Coluna inicial não encontrada para o board."));

        return cardRepository.createCard(initialColumnId, title.trim(), description == null ? "" : description.trim());
    }

    public void moveCard(Long boardId, Long cardId, Long targetColumnId) {
        if (!boardRepository.existsColumnInBoard(boardId, targetColumnId)) {
            throw new IllegalArgumentException("A coluna de destino não pertence a esse board.");
        }

        Card card = cardRepository.findCardById(cardId)
                .orElseThrow(() -> new IllegalArgumentException("Card não encontrado."));

        if (card.blocked()) {
            throw new IllegalStateException("Não é possível mover um card bloqueado.");
        }

        cardRepository.moveCard(cardId, targetColumnId);
    }

    public void blockCard(Long cardId, String reason) {
        Card card = cardRepository.findCardById(cardId)
                .orElseThrow(() -> new IllegalArgumentException("Card não encontrado."));

        if (card.blocked()) {
            throw new IllegalStateException("O card já está bloqueado.");
        }

        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("Informe o motivo do bloqueio.");
        }

        cardRepository.blockCard(cardId, reason.trim());
    }

    public void unblockCard(Long cardId, String reason) {
        Card card = cardRepository.findCardById(cardId)
                .orElseThrow(() -> new IllegalArgumentException("Card não encontrado."));

        if (!card.blocked()) {
            throw new IllegalStateException("O card não está bloqueado.");
        }

        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("Informe o motivo do desbloqueio.");
        }

        cardRepository.unblockCard(cardId, reason.trim());
    }
}
