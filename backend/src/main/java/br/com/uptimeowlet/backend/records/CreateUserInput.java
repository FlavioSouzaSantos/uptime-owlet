package br.com.uptimeowlet.backend.records;

public record CreateUserInput(String login, String password, String passwordConfirmation) {
}
