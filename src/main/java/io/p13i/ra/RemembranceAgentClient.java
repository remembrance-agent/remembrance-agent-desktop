package io.p13i.ra;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import io.p13i.ra.databases.DocumentDatabase;
import io.p13i.ra.databases.localdisk.LocalDiskDocumentDatabase;
import io.p13i.ra.models.Context;
import io.p13i.ra.models.Document;
import io.p13i.ra.models.ScoredDocument;
import io.p13i.ra.utils.CharacterBuffer;
import io.p13i.ra.utils.DateUtils;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

public class RemembranceAgentClient implements NativeKeyListener {

        private static final Logger LOGGER = Logger.getLogger( RemembranceAgentClient.class.getName() );

        private static CharacterBuffer characterBuffer = new CharacterBuffer(30);
        private static Timer remembranceAgentUpdateTimer = new Timer();
        private static RemembranceAgent remembranceAgent;

        public static void main(String[] args) {

            try {
                InputStream stream = RemembranceAgentClient.class.getClassLoader().getResourceAsStream("logging.properties");
                LogManager.getLogManager().readConfiguration(stream);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // jnativehook produces a lot of logs
            Logger logger = java.util.logging.Logger.getLogger(GlobalScreen.class.getPackage().getName());
            logger.setLevel(Level.WARNING);

            DocumentDatabase database = new LocalDiskDocumentDatabase("/Users/p13i/Projects/glass-notes/sample-documents");
            LOGGER.info("Using " + database.getName());

            remembranceAgent = new RemembranceAgent(database);
            LOGGER.info("Initialized Remembrance Agent.");

            LOGGER.info("Indexing documents...");
            List<Document> documentsIndexed = remembranceAgent.indexDocuments();
            LOGGER.info(String.format("Indexing complete. Added %d documents:", documentsIndexed.size()));
            for (Document document : documentsIndexed) {
                LOGGER.info(document.toString());
            }

            // Add the key logger
            try {
                GlobalScreen.registerNativeHook();
                GlobalScreen.addNativeKeyListener(new RemembranceAgentClient());
            } catch (NativeHookException e) {
                System.exit(1);
            }
            LOGGER.info("Added native key logger.");

            // Start the RA task
            remembranceAgentUpdateTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    String query = characterBuffer.toString();
                    Context context = new Context(null, "p13i", query, DateUtils.now());
                    final int numSuggestions = 2;

                    LOGGER.info("Sending query to RA: '" + query + "'");
                    List<ScoredDocument> suggestions = remembranceAgent.determineSuggestions(query, context, numSuggestions);

                    if (suggestions.isEmpty()) {
                        LOGGER.info("No suggestions :(");
                    } else {
                        LOGGER.info(String.format("Got %d suggestions:", suggestions.size()));

                        for (ScoredDocument doc : suggestions) {
                            LOGGER.info(" -> " + doc.toString());
                        }
                    }
                }
            }, 5000, 5000);
            LOGGER.info("Scheduled Remembrance Agent update task.");
        }

        public void nativeKeyPressed(NativeKeyEvent e) {
            String keyText = NativeKeyEvent.getKeyText(e.getKeyCode());
            LOGGER.info("Keystroke: " + keyText);

            Character characterToAdd = null;
            if (keyText.length() == 1) {
                characterToAdd = keyText.charAt(0);
            } else {
                if (keyText.equals("Space")) {
                    characterToAdd = ' ';
                }
            }
            if (characterToAdd != null) {
                characterBuffer.add(characterToAdd);
                LOGGER.info("Full buffer: " + characterBuffer.toString());
            }

        }

        public void nativeKeyReleased(NativeKeyEvent e) {
            // Nothing
        }

        public void nativeKeyTyped(NativeKeyEvent e) {
            // Nothing here
        }
}