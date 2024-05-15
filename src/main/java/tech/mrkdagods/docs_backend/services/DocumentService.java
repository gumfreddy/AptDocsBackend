package tech.mrkdagods.docs_backend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.mrkdagods.docs_backend.models.Doc;
import tech.mrkdagods.docs_backend.models.DocOwner;
import tech.mrkdagods.docs_backend.models.DocText;
import tech.mrkdagods.docs_backend.models.User;
import tech.mrkdagods.docs_backend.repositories.*;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DocumentService {
    private final UserRepository userRepository;
    private final DocRepository docRepository;
    private final DocOwnerRepository docOwnerRepository;
    private final SessionRepository sessionRepository;
    private final DocTextRepository docTextRepository;

    @Autowired
    public DocumentService(UserRepository userRepository,
                           DocRepository docRepository,
                           DocOwnerRepository docOwnerRepository,
                           SessionRepository sessionRepository,
                           DocTextRepository docTextRepository) {
        this.userRepository = userRepository;
        this.docRepository = docRepository;
        this.docOwnerRepository = docOwnerRepository;
        this.sessionRepository = sessionRepository;
        this.docTextRepository = docTextRepository;
    }

    public boolean createDocument(String sessionId, String name) {
        // find user?
        var session = sessionRepository.findById(sessionId);
        if (session.isEmpty()) {
            return false; // not found
        }

        final var doc = new Doc(
                UUID.randomUUID().toString(),
                name,
                session.get().getUserId(),
                Date.from(Instant.now()),
                Date.from(Instant.now()));

        docRepository.save(doc);

        // add in doc owners
        docOwnerRepository.save(new DocOwner(doc.getId(),
                session.get().getUserId(),
                true));

        return true;
    }

    public List<Doc> getDocumentsForUser(String sessionId) {
        var session = sessionRepository.findById(sessionId);
        if (session.isEmpty()) {
            return new ArrayList<>();
        }

        var docOwners = docOwnerRepository.findAllByUserId(session.get().getUserId());
        if (docOwners.isEmpty()) {
            return new ArrayList<>();
        }

        return docRepository.findAllById(
                docOwners.stream().map(DocOwner::getDocumentId).toList()
        );
    }

    public boolean deleteDocument(String sessionId, String docId) {
        var session = sessionRepository.findById(sessionId);
        if (session.isEmpty()) {
            return false;
        }

        final var doc = docRepository.findById(docId);
        if (doc.isEmpty()) {
            return false;
        }

        if (doc.get().getOwnerId().equals(session.get().getUserId())) {
            // delete doc kolo
            docRepository.deleteById(docId);

            // delete from doc owners
            docOwnerRepository.deleteAllByDocumentId(docId);
            return true;
        }

        // delete own access
        docOwnerRepository.deleteByUserIdAndDocumentId(session.get().getUserId(), docId);
        return true;
    }

    public boolean renameDocument(String sessionId, String docId, String name) {
        var session = sessionRepository.findById(sessionId);
        if (session.isEmpty()) {
            return false;
        }

        final var doc = docRepository.findById(docId);
        if (doc.isEmpty()) {
            return false;
        }

        if (doc.get().getOwnerId().equals(session.get().getUserId())) {
            // rename doc

            docRepository.deleteById(docId);
            docRepository.save(
                    new Doc(doc.get().getId(),
                            name,
                            doc.get().getOwnerId(),
                            doc.get().getCreationDate(),
                            Date.from(Instant.now())));

            return true;
        }

        return false;
    }

    public Map<String, Boolean> hasEditPermission(String sessionId, List<String> docIds) {
        var session = sessionRepository.findById(sessionId);
        if (session.isEmpty()) {
            return new HashMap<>(0);
        }

        return docOwnerRepository.findAllByUserId(session.get().getUserId())
                .stream()
                .filter(x -> docIds.contains(x.getDocumentId()))
                .map(x -> Map.entry(x.getDocumentId(), x.hasEditPermission()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    // returns all users for document
    public List<User> getDocumentUsers(String sessionId, String docId) {
        var session = sessionRepository.findById(sessionId);
        if (session.isEmpty()) {
            return new ArrayList<>();
        }

        return docOwnerRepository.findAllByDocumentId(docId)
                .stream()
                .map(DocOwner::getUserId)
                .map(userRepository::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
    }

    public Map<String, Boolean> usersHaveEditPermission(String sessionId, List<String> usersId, String docId) {
        var session = sessionRepository.findById(sessionId);
        if (session.isEmpty()) {
            return new HashMap<>(0);
        }

        // ensure doc Id is owned by session
        var doc = docRepository.findById(docId);
        if (doc.isEmpty() || !doc.get().getOwnerId().equals(session.get().getUserId())) {
            return new HashMap<>(0);
        }

        return usersId
                .stream()
                .map(x -> docOwnerRepository.findByUserIdAndDocumentId(x, docId))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(x -> Map.entry(x.getUserId(), x.hasEditPermission()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public boolean addUserToDocument(String sessionId, String docId, String userId, boolean hasEditPerm) {
        var session = sessionRepository.findById(sessionId);
        if (session.isEmpty()) {
            return false;
        }

        // ensure doc Id is owned by session
        var doc = docRepository.findById(docId);
        if (doc.isEmpty() || !doc.get().getOwnerId().equals(session.get().getUserId())) {
            return false;
        }

        var docOwner = docOwnerRepository.findByUserIdAndDocumentId(userId, docId);
        if (docOwner.isPresent()) {
            return false;
        }

        var newOwner = new DocOwner(docId, userId, hasEditPerm);
        docOwnerRepository.save(newOwner);
        return true;
    }

    public boolean deleteUserFromDocument(String sessionId, String docId, String userId) {
        var session = sessionRepository.findById(sessionId);
        if (session.isEmpty()) {
            return false;
        }

        // ensure doc Id is owned by session
        var doc = docRepository.findById(docId);
        var sessionUserId = session.get().getUserId();
        if (doc.isEmpty() || !doc.get().getOwnerId().equals(sessionUserId) || sessionUserId.equals(userId)) {
            return false;
        }

        var docOwner = docOwnerRepository.findByUserIdAndDocumentId(userId, docId);
        if (docOwner.isEmpty()) {
            return false;
        }

        docOwnerRepository.deleteByUserIdAndDocumentId(userId, docId);
        return true;
    }

    public boolean modifyUserPermissions(String sessionId, String docId, String userId, boolean hasEditPerm) {
        var session = sessionRepository.findById(sessionId);
        if (session.isEmpty()) {
            return false;
        }

        // ensure doc Id is owned by session
        var doc = docRepository.findById(docId);
        var sessionUserId = session.get().getUserId();
        if (doc.isEmpty() || !doc.get().getOwnerId().equals(sessionUserId) || sessionUserId.equals(userId)) {
            return false;
        }

        var docOwner = docOwnerRepository.findByUserIdAndDocumentId(userId, docId);
        if (docOwner.isEmpty() || docOwner.get().hasEditPermission() == hasEditPerm) {
            return false;
        }

        // delete old
        docOwnerRepository.deleteByUserIdAndDocumentId(userId, docId);

        var newOwner = new DocOwner(docId, userId, hasEditPerm);
        docOwnerRepository.save(newOwner);

        return true;
    }

    public boolean saveDocText(String docId, String text) {
        docTextRepository.deleteById(docId);

        var newDocText = new DocText(docId, text);
        docTextRepository.save(newDocText);
    }
}
