package com.sandbox.commands.polly;

import com.sandbox.commands.BaseCommand;
import com.sandbox.config.converters.StringToPollyEngine;
import com.sandbox.polly.PollyService;
import com.sandbox.polly.PollyServiceImpl;
import com.sandbox.translate.AWSTranslateService;
import com.sandbox.translate.TranslateService;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.FactoryRegistry;
import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.polly.model.Engine;
import software.amazon.awssdk.services.polly.model.LanguageCode;
import software.amazon.awssdk.services.polly.model.OutputFormat;
import software.amazon.awssdk.services.polly.model.TextType;
import software.amazon.awssdk.services.polly.model.Voice;

import java.io.InputStream;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

@CommandLine.Command(name = PollyDemoCommand.COMMAND, description = "Translate text from english and synthesize it by all available non english AWS Polly engines.")
public class PollyDemoCommand extends BaseCommand {
    public static final Logger logger = LoggerFactory.getLogger(PollyDemoCommand.class);

    public static final String COMMAND = "pollyDemo";
    public static final String ENGINE = COMMAND + ".engine";
    @CommandLine.Option(names = "--" + ENGINE,
            description = "Amazon Polly text-to-speech voice engine: standard, neural, long-form, generative",/*possible values: software.amazon.awssdk.services.polly.model.Engine*/
            converter = StringToPollyEngine.class,
            defaultValue = "standard")
    private Engine pollyEngine;

    public static final String TEXT_TO_SPEECH = COMMAND + ".textToSpeech";
    @CommandLine.Option(names = "--" + TEXT_TO_SPEECH,
            description = "Text to speach",
            defaultValue = "This is text to speach with translation sandbox")
    private String textToSpeech;

    private final Map<String, String> toEnglishTranslationCache = new HashMap<>();

    public PollyDemoCommand(Properties props) {
        super(props);
    }

    @Override
    protected void executeCommand() {
        Region region = baseAWSConfig.getRegion();

        try (PollyService polly = new PollyServiceImpl(region);
             TranslateService translateService = new AWSTranslateService(region)) {
            Voice infoVoice = polly.getVoice(Engine.STANDARD);

            Set<Engine> engines = EnumSet.allOf(Engine.class);
            if (pollyEngine != null) {
                engines = EnumSet.of(pollyEngine);
            }
            for (Engine engine : engines) {
                List<Voice> voices = polly.getAllVoices(engine);
                for (Voice voice : voices) {
                    //skip english, input text is in english
                    if (voice.languageCodeAsString().startsWith("en")) {
                        continue;
                    }

                    String translatedText = toEnglishTranslationCache.computeIfAbsent(voice.languageCodeAsString(), k ->
                            translateService.translateText(textToSpeech, LanguageCode.EN_GB.toString(), voice.languageCodeAsString()));

                    //voice info
                    String voiceInfo = "<speak><break time='1s'/>Now you will hear the phrase: \"" +
                                       textToSpeech + "\"\n" +
                                       " in " + voice.languageName() + " language. " +
                                       "<break time='300ms'/>Engine: " + engine + "\n" +
                                       "<break time='300ms'/>Voice: " + voice.name() + "\n" +
                                       "<break time='300ms'/>Gender: " + voice.genderAsString() + "\n" +
                                       "</speak>";
                    logger.info(voiceInfo);
                    InputStream infoStream = polly.synthesize(voiceInfo, infoVoice, OutputFormat.MP3, TextType.SSML, Engine.STANDARD);
                    //play voice information
                    playAudio(infoStream);

                    //translated text
                    InputStream stream = polly.synthesize(translatedText, voice, OutputFormat.MP3, TextType.TEXT, engine);
                    //play voice information
                    playAudio(stream);
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    private void playAudio(InputStream stream) {
        try {
            AdvancedPlayer player = new AdvancedPlayer(stream,
                    FactoryRegistry.systemRegistry().createAudioDevice());
            player.setPlayBackListener(new PlaybackListener() {
                public void playbackStarted(PlaybackEvent evt) {
                    logger.info("Playback started");
                }

                public void playbackFinished(PlaybackEvent evt) {
                    logger.info("Playback finished");
                }
            });
            // play it!
            player.play();
        } catch (JavaLayerException e) {
            throw new RuntimeException(e);
        }
    }

}
