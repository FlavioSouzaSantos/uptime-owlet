package br.com.uptimeowlet.backend.services;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class I18nService {

    private final MessageSource messageSource;

    public String getMessage(@NonNull String code) {
        return getMessage(code, LocaleContextHolder.getLocale(), (Object) null);
    }
    public String getMessage(@NonNull String code, @NonNull Locale locale) {
        return getMessage(code, locale, (Object) null);
    }
    public String getMessage(@NonNull String code, Object... args) {
        return getMessage(code, LocaleContextHolder.getLocale(), args);
    }
    public String getMessage(@NonNull String code, @NonNull Locale locale, Object... args) {
        return messageSource.getMessage(code, args, locale);
    }
}
