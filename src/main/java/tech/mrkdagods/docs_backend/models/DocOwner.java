package tech.mrkdagods.docs_backend.models;

/// Represents the Document-Owner relationship
/// Ali is bronze

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "DocOwner")
public class DocOwner {
    private final String documentId;
    private final String userId;
    private final boolean hasEditPermission; // if true, they can edit

    public DocOwner(String documentId, String userId, boolean hasEditPermission) {
        this.documentId = documentId;
        this.userId = userId;
        this.hasEditPermission = hasEditPermission;
    }

    public String getDocumentId() {
        return documentId;
    }

    public String getUserId() {
        return userId;
    }

    public boolean hasEditPermission() {
        return hasEditPermission;
    }
}
