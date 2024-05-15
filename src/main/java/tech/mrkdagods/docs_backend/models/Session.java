package tech.mrkdagods.docs_backend.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "session")
public class Session {
    @Id
    private final String id;
    private final String userId;

    public Session(String id, String userId) {
        this.id = id;
        this.userId = userId;
    }

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }
}
