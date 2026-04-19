package br.com.dio.board.model;

public record Card(Long id, Long columnId, String title, String description, boolean blocked, String blockReason) {
}
