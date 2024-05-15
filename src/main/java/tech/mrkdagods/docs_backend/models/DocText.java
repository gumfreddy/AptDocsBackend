package tech.mrkdagods.docs_backend.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "DocText")
public class DocText {
    @Id
    private final String documentId;
    private final String text;

    public DocText(String documentId, String text) {
        this.documentId = documentId;
        this.text = text;
    }

    public String getDocumentId() {
        return documentId;
    }

    public String getText() {
        return text;
    }
}
