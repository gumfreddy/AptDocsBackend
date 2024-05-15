package tech.mrkdagods.docs_backend.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import tech.mrkdagods.docs_backend.models.DocText;

public interface DocTextRepository extends MongoRepository<DocText, String> {

}
