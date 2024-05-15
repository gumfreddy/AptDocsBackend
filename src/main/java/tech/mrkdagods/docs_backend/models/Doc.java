package tech.mrkdagods.docs_backend.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "Document")
public class Doc {
    @Id
    private final String id;
    private final String name;
    private final String ownerId;
    private final Date creationDate;
    private final Date modificationDate;

    public Doc(String id, String name, String ownerId, Date creationDate, Date modificationDate) {
        this.id = id;
        this.name = name;
        this.ownerId = ownerId;
        this.creationDate = creationDate;
        this.modificationDate = modificationDate;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public Date getModificationDate() {
        return modificationDate;
    }
}
