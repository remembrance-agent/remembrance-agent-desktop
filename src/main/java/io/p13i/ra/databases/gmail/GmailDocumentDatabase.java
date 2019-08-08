package io.p13i.ra.databases.gmail;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.repackaged.org.apache.commons.codec.binary.Base64;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePart;
import io.p13i.ra.databases.DocumentDatabase;
import io.p13i.ra.databases.cache.CachableDocument;
import io.p13i.ra.databases.cache.CachableDocumentDatabase;
import io.p13i.ra.databases.googledrive.GoogleDriveFolderDocumentDatabase;
import io.p13i.ra.models.Document;
import io.p13i.ra.utils.ListUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GmailDocumentDatabase implements DocumentDatabase, CachableDocumentDatabase {

    private List<GmailDocument> gmailDocuments;

    @Override
    public String getName() {
        return GmailDocumentDatabase.class.getSimpleName();
    }

    private Gmail getGmailService() {
        try {
            // Build a new authorized API client service.
            final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            Gmail service = new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                    .setApplicationName(APPLICATION_NAME)
                    .build();
            return service;
        } catch (IOException | GeneralSecurityException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public void loadDocuments() {
        this.gmailDocuments = new ArrayList<>();

        Gmail service = getGmailService();

        // Print the labels in the user's account.
        try {
            List<Message> response = service.users().messages().list("me").setMaxResults(10L)
                    .execute().getMessages();
            for (Message message : response) {
                Message r = service.users().messages().get("me", message.getId()).setFormat("full")
                        .execute();
                if (r == null) {
                    continue;
                }

                String body = getMessageContent(r);
                this.gmailDocuments.add(new GmailDocument(body, null, null, null));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Document> getAllDocuments() {
        return ListUtils.castUp(this.gmailDocuments);
    }

    @Override
    public List<CachableDocument> getDocumentsForSavingToCache() {
        return ListUtils.castUp(this.gmailDocuments);
    }

    private static final String APPLICATION_NAME = "Gmail API Java Quickstart";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";

    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        // Load client secrets.
        InputStream in = GoogleDriveFolderDocumentDatabase.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    /**
     * Global instance of the scopes required by this quickstart.
     * If modifying these scopes, delete your previously saved tokens/ folder.
     */
    private static final List<String> SCOPES = Collections.singletonList(GmailScopes.GMAIL_READONLY);
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

    public static void main(String... args) {
        // Build a new authorized API client service.
        GmailDocumentDatabase database = new GmailDocumentDatabase();
        database.loadDocuments();
        for (Document document : database.getAllDocuments()) {
            System.out.println(document.getContent());
        }
    }

    /*
    https://stackoverflow.com/a/38828761
     */
    private static String getMessageContent(Message message) {
        StringBuilder stringBuilder = new StringBuilder();
        getPlainTextFromMessagePartsRecursive(message.getPayload().getParts(), stringBuilder);
        return new String(Base64.decodeBase64(stringBuilder.toString()), StandardCharsets.UTF_8);
    }

    /*
    https://stackoverflow.com/a/38828761
     */
    private static void getPlainTextFromMessagePartsRecursive(List<MessagePart> messageParts, StringBuilder stringBuilder) {
        if (messageParts == null) {
            stringBuilder.append("null");
            return;
        }

        for (MessagePart messagePart : messageParts) {
            if (messagePart.getMimeType().equals("text/plain")) {
                stringBuilder.append(messagePart.getBody().getData());
            }

            if (messagePart.getParts() != null) {
                getPlainTextFromMessagePartsRecursive(messagePart.getParts(), stringBuilder);
            }
        }
    }
}
