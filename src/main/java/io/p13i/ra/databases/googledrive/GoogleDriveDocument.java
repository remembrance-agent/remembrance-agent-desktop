package io.p13i.ra.databases.googledrive;

import io.p13i.ra.models.Context;
import io.p13i.ra.models.AbstractDocument;

import java.util.Date;
import java.util.Objects;

/**
 *
 */
public class GoogleDriveDocument extends AbstractDocument {

    public GoogleDriveDocument(String id, String content, String filename, Date lastModified) {
        super(content, new Context(null, null, filename, lastModified));
        this.setLastModified(lastModified);
        this.setURL("https://docs.google.com/document/d/" + Objects.requireNonNull(id) + "/view");
    }
}
