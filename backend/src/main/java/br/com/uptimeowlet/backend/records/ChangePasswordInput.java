package br.com.uptimeowlet.backend.records;

public record ChangePasswordInput(int id, String currentPassword, String newPassword) {
}
