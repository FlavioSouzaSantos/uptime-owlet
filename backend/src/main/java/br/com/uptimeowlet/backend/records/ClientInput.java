package br.com.uptimeowlet.backend.records;

public record ClientInput(String url, String method, String name,
                          int httpCodeForCheckIfServiceIsActive, int checkPeriod, int timeoutConnection,
                          int maxFailureForCheckIfServiceIsInactive, int periodForNewCheckAfterFailure) {
}
