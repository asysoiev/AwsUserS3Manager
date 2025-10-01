package com.sandbox.polly;

import software.amazon.awssdk.services.polly.model.Engine;
import software.amazon.awssdk.services.polly.model.OutputFormat;
import software.amazon.awssdk.services.polly.model.TextType;
import software.amazon.awssdk.services.polly.model.Voice;

import java.io.InputStream;
import java.util.List;

public interface PollyService extends AutoCloseable {
    List<Voice> getAllVoices(Engine engine);

    default Voice getVoice(Engine engine) {
        return getVoice(engine, null);
    }

    Voice getVoice(Engine engine, String voiceName);

    InputStream synthesize(String text, Voice voice, OutputFormat format, TextType textType, Engine engine);
}
