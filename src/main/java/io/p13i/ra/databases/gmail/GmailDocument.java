package io.p13i.ra.databases.gmail;

import io.p13i.ra.models.Context;
import io.p13i.ra.models.AbstractDocument;

import java.util.Date;

public class GmailDocument extends AbstractDocument {

    public GmailDocument(String id, String body, String subject, String sender, Date receiveDate) {
        super(body, new Context(null, sender, subject, receiveDate));
        this.setLastModified(receiveDate);
        this.setURL("https://mail.google.com/mail/u/0/#inbox/" + id);
    }
}
