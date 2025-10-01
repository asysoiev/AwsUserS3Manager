package com.sandbox.translate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.translate.TranslateClient;
import software.amazon.awssdk.services.translate.model.TranslateException;
import software.amazon.awssdk.services.translate.model.TranslateTextRequest;
import software.amazon.awssdk.services.translate.model.TranslateTextResponse;

public class AWSTranslateService implements TranslateService {

    public static final Logger logger = LoggerFactory.getLogger(AWSTranslateService.class);

    private final TranslateClient translateClient;

    public AWSTranslateService(Region region) {
        translateClient = TranslateClient.builder()
                .region(region)
                .build();
    }

    @Override
    public String translateText(String text, String fromLanguage, String toLanguage) {
        try {
            logger.info("Translating \"{}\" from \"{}\" to \"{}\"", text, fromLanguage, toLanguage);
            TranslateTextRequest textRequest = TranslateTextRequest.builder()
                    .sourceLanguageCode(fromLanguage)
                    .targetLanguageCode(toLanguage)
                    .text(text)
                    .build();

            TranslateTextResponse textResponse = translateClient.translateText(textRequest);
            logger.info("Translated text: {}", textResponse.translatedText());
            return textResponse.translatedText();
        } catch (TranslateException e) {
            logger.error("Failed to translate", e);
            return text;
        }
    }

    @Override
    public void close() throws Exception {
        translateClient.close();
    }
}
