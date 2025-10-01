package com.sandbox.polly;

import org.apache.commons.lang3.ObjectUtils;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.polly.PollyClient;
import software.amazon.awssdk.services.polly.model.DescribeVoicesRequest;
import software.amazon.awssdk.services.polly.model.DescribeVoicesResponse;
import software.amazon.awssdk.services.polly.model.Engine;
import software.amazon.awssdk.services.polly.model.OutputFormat;
import software.amazon.awssdk.services.polly.model.SynthesizeSpeechRequest;
import software.amazon.awssdk.services.polly.model.TextType;
import software.amazon.awssdk.services.polly.model.Voice;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class PollyServiceImpl implements PollyService {

    private final PollyClient polly;

    public PollyServiceImpl(Region region) {
        polly = PollyClient.builder()
                .region(region)
                .build();
    }

    @Override
    public List<Voice> getAllVoices(Engine engine) {
        DescribeVoicesRequest describeVoiceRequest = DescribeVoicesRequest.builder()
                .engine(engine)
                .build();
        DescribeVoicesResponse describeVoicesResult = polly.describeVoices(describeVoiceRequest);
        return describeVoicesResult.voices();
    }


    /**
     * Get AWS voice configuration
     *
     * @return
     */
    @Override
    public Voice getVoice(Engine engine, String voiceName) {
        DescribeVoicesRequest describeVoiceRequest = DescribeVoicesRequest.builder()
                .engine(engine)
                .build();
        DescribeVoicesResponse describeVoicesResult = polly.describeVoices(describeVoiceRequest);
        Voice voice = describeVoicesResult.voices().stream()
                .filter(v -> ObjectUtils.isEmpty(voiceName) || v.name().equals(voiceName))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Voice not found"));
        return voice;
    }

    /**
     * Synthesizes text to speach
     *
     * @param text
     * @param voice
     * @param format
     * @param textType
     * @return
     * @throws IOException
     */
    @Override
    public InputStream synthesize(String text, Voice voice, OutputFormat format, TextType textType, Engine engine) {
        SynthesizeSpeechRequest synthReq = SynthesizeSpeechRequest.builder()
                .text(text)
                .textType(textType)
                .voiceId(voice.id())
                .speechMarkTypes()
                .engine(engine)
                .outputFormat(format)
                .build();

        return polly.synthesizeSpeech(synthReq);
    }

    @Override
    public void close() throws Exception {
        polly.close();
    }
}
