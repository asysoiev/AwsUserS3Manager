package com.sandbox.translate;

public interface TranslateService extends AutoCloseable {
    String translateText(String text, String fromLanguage, String toLanguage);
}
