package tech.mrkdagods.docs_backend.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import tech.mrkdagods.docs_backend.models.DocOwner;

import java.util.List;
import java.util.Optional;

@Repository
public interface DocOwnerRepository extends MongoRepository<DocOwner, String> {
    List<DocOwner> findAllByUserId(String userId);
    List<DocOwner> findAllByDocumentId(String docId);
    Optional<DocOwner> findByUserIdAndDocumentId(String userId, String docId);

    void deleteByUserIdAndDocumentId(String userId, String documentId);
    void deleteAllByDocumentId(String documentId);
}
