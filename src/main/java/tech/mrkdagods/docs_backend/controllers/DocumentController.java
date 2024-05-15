package tech.mrkdagods.docs_backend.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.mrkdagods.docs_backend.models.Doc;
import tech.mrkdagods.docs_backend.models.User;
import tech.mrkdagods.docs_backend.services.DocumentService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("doc")
public class DocumentController {
    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @PostMapping("/create")
    public ResponseEntity<Void> create(@RequestBody Map<String, String> body) {
        final var result = documentService.createDocument(body.get("sessionId"), body.get("name"));
        return new ResponseEntity<>(result ? HttpStatus.OK : HttpStatus.FORBIDDEN); // 200 OR 403
    }

    @PostMapping("/docs")
    public ResponseEntity<List<Doc>> getDocs(@RequestBody Map<String, String> body) {
        final var result = documentService.getDocumentsForUser(body.get("sessionId"));
        return new ResponseEntity<>(result, HttpStatus.OK); // 200
    }

    @PostMapping("/delete")
    public ResponseEntity<Void> delete(@RequestBody Map<String, String> body) {
        final var result = documentService.deleteDocument(body.get("sessionId"), body.get("docId"));
        return new ResponseEntity<>(result ? HttpStatus.OK : HttpStatus.FORBIDDEN); // 200 OR 403
    }

    @PostMapping("/rename")
    public ResponseEntity<Void> rename(@RequestBody Map<String, String> body) {
        final var result = documentService.renameDocument(body.get("sessionId"),
                body.get("docId"),
                body.get("name"));

        return new ResponseEntity<>(result ? HttpStatus.OK : HttpStatus.FORBIDDEN); // 200 OR 403
    }

    @SuppressWarnings("unchecked")
    @PostMapping("/checkperms")
    public ResponseEntity<Map<String, Boolean>> hasEditPerms(@RequestBody Map<String, Object> body) {
        final var result = documentService.hasEditPermission(
                (String) body.get("sessionId"),
                (List<String>) body.get("docIds"));

        return new ResponseEntity<>(result, HttpStatus.OK); // 200
    }

    @PostMapping("/users")
    public ResponseEntity<Map<String, Map<String, Object>>> getDocumentUsers(@RequestBody Map<String, Object> body) {
        final var users = documentService.getDocumentUsers(
                (String) body.get("sessionId"),
                (String) body.get("docId")
        );

        final var result = documentService.usersHaveEditPermission(
                (String) body.get("sessionId"),
                users.stream().map(User::getId).toList(),
                (String) body.get("docId")
        ).entrySet()
                .stream()
                .map(x -> Map.entry(
                        x.getKey(),
                        Map.of("user", users.stream().filter(y -> y.getId().equals(x.getKey())).findFirst().get(),
                                "write", x.getValue())
                ))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        return new ResponseEntity<>(result, HttpStatus.OK); // 200
    }

    @PostMapping("/adduser")
    public ResponseEntity<Void> addUser(@RequestBody Map<String, Object> body) {
        final var result = documentService.addUserToDocument(
                (String) body.get("sessionId"),
                (String) body.get("docId"),
                (String) body.get("userId"),
                (boolean) body.get("edit"));

        return new ResponseEntity<>(result ? HttpStatus.OK : HttpStatus.FORBIDDEN); // 200 OR 403
    }

    @PostMapping("/deleteuser")
    public ResponseEntity<Void> deleteUser(@RequestBody Map<String, Object> body) {
        final var result = documentService.deleteUserFromDocument(
                (String) body.get("sessionId"),
                (String) body.get("docId"),
                (String) body.get("userId"));

        return new ResponseEntity<>(result ? HttpStatus.OK : HttpStatus.FORBIDDEN); // 200 OR 403
    }

    @PostMapping("/modifyuser")
    public ResponseEntity<Void> modifyUser(@RequestBody Map<String, Object> body) {
        final var result = documentService.modifyUserPermissions(
                (String) body.get("sessionId"),
                (String) body.get("docId"),
                (String) body.get("userId"),
                (boolean) body.get("edit"));

        return new ResponseEntity<>(result ? HttpStatus.OK : HttpStatus.FORBIDDEN); // 200 OR 403
    }

    @PostMapping("/")
}
