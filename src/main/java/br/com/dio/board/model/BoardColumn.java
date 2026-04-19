package br.com.dio.board.model;

public record BoardColumn(Long id, Long boardId, String name, String kind, int positionIndex) {
}
