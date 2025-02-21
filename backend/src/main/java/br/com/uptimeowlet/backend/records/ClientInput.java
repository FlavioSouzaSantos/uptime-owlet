package br.com.uptimeowlet.backend.records;

public record ClientInput(String url, String method, String name,
                          int httpCodeForCheckIfServiceIsActive, long checkPeriod, long timeoutConnection,
                          int maxFailureForCheckIfServiceIsInactive, long periodForNewCheckAfterFailure) {
}
