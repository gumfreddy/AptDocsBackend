package tech.mrkdagods.docs_backend.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import tech.mrkdagods.docs_backend.models.Doc;

import java.util.List;

@Repository
public interface DocRepository extends MongoRepository<Doc, String> {
    List<Doc> findAllByOwnerId(String ownerId);
}
