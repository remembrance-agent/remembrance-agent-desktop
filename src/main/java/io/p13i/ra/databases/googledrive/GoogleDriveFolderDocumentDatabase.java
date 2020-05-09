package io.p13i.ra.databases.googledrive;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import io.p13i.ra.databases.AbstractDocumentDatabase;
import io.p13i.ra.gui.User;
import io.p13i.ra.utils.FileIO;
import io.p13i.ra.utils.GoogleAPIUtils;
import io.p13i.ra.utils.LoggerUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

/**
 * Enables use of a Google Drive folder as a data store for a collection of notes
 */
public class GoogleDriveFolderDocumentDatabase extends AbstractDocumentDatabase<GoogleDriveDocument> {

    private static final Logger LOGGER = LoggerUtils.getLogger(GoogleDriveFolderDocumentDatabase.class);

    private static final String APPLICATION_NAME = "Remembrance Agent";

    /**
     * Global instance of the scopes required by this quickstart.
     * If modifying these scopes, delete your previously saved tokens/ folder.
     */
    private static final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE_READONLY);
    private static final String TOKENS_DIRECTORY_PATH =
            FileIO.ensureFolderExists(User.Home.Documents.RA.Tokens.getDirectory() + java.io.File.separator + "google-drive");

    private final String rootFolderID;
    private final List<GoogleDriveDocument> googleDriveDocuments;

    public GoogleDriveFolderDocumentDatabase(String rootFolderID) {
        this.rootFolderID = rootFolderID;
        this.googleDriveDocuments = new ArrayList<>();
    }

    @Override
    public void loadDocuments() {
        LOGGER.info("loadDocuments()");
        try {
            loadDocumentsRecursive(getClient(), this.googleDriveDocuments, this.rootFolderID);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets a Google Drive client
     *
     * @return Google Drive client
     */
    private Drive getClient() {
        try {
            // Build a new authorized API client service.
            final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            return new Drive.Builder(HTTP_TRANSPORT, GoogleAPIUtils.JSON_FACTORY, GoogleAPIUtils.getCredentials(HTTP_TRANSPORT, SCOPES, TOKENS_DIRECTORY_PATH))
                    .setApplicationName(APPLICATION_NAME)
                    .build();
        } catch (IOException | GeneralSecurityException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * Loads all documents from a folder, recursively
     *
     * @param service        the Drive client
     * @param documents      list of documents already garnered, added to in this method
     * @param parentFolderID the enclosing folder ID
     * @throws IOException if there is an issue reading from the API or writing to the byte buffer
     */
    private static void loadDocumentsRecursive(Drive service, List<GoogleDriveDocument> documents, String parentFolderID) throws IOException {
        FileList filesList = service.files().list()
                .setQ("'" + parentFolderID + "' in parents and mimeType != 'application/vnd.google-apps.folder'")
                .setSpaces("drive")
                .setFields("nextPageToken, files(id, name, mimeType, trashed, modifiedTime)")
                .execute();

        for (File file : filesList.getFiles()) {

            // Must not have ra:no-index in it
            if (file.getName().contains("ra:no-index")) {
                continue;
            }

            // Must be a Google Doc
            if (!file.getMimeType().equals("application/vnd.google-apps.document")) {
                continue;
            }

            // Must not be trashed
            if (file.getTrashed()) {
                continue;
            }

            LOGGER.info("Loading document: " + file.getName());

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            String fileContents;

            service.files().export(file.getId(), "text/plain")
                    .executeMediaAndDownloadTo(outputStream);
            fileContents = new String(outputStream.toByteArray(), StandardCharsets.UTF_8);

            documents.add(new GoogleDriveDocument(file.getId(), fileContents, file.getName(), new Date(file.getModifiedTime().getValue())));
        }

        FileList foldersList = service.files().list()
                .setQ("'" + parentFolderID + "' in parents and mimeType = 'application/vnd.google-apps.folder'")
                .setSpaces("drive")
                .setFields("nextPageToken, files(id)")
                .execute();
        for (File file : foldersList.getFiles()) {
            // recurse
            loadDocumentsRecursive(service, documents, file.getId());
        }

    }

    @Override
    public List<GoogleDriveDocument> getAllDocuments() {
        return this.googleDriveDocuments;
    }
}
