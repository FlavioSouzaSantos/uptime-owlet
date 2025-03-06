package br.com.uptimeowlet.backend.records;

import java.time.ZonedDateTime;

public record TokenOutput(String type, String value, String key, ZonedDateTime expiration) {
}
