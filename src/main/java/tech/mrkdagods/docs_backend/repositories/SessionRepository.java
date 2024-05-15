package tech.mrkdagods.docs_backend.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import tech.mrkdagods.docs_backend.models.Session;

import java.util.List;

@Repository
public interface SessionRepository extends MongoRepository<Session, String> {
    Session findByUserId(String userId);
    List<Session> findAllByUserId(String userId);
}

