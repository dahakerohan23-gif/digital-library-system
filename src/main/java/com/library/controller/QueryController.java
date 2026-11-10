package com.library.controller;

import com.library.model.QueryMessage;
import com.library.repository.QueryMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/queries")
@CrossOrigin(origins = "*")
public class QueryController {

    @Autowired
    private QueryMessageRepository queryMessageRepository;

    @GetMapping
    public List<QueryMessage> getAllQueries() {
        return queryMessageRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<QueryMessage> submitQuery(@RequestBody QueryMessage message) {
        if (message.getStatus() == null) {
            message.setStatus("NEW");
        }
        QueryMessage saved = queryMessageRepository.save(message);
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/{id}/resolve")
    public ResponseEntity<?> resolveQuery(@PathVariable Long id) {
        return queryMessageRepository.findById(id).map(q -> {
            q.setStatus("RESOLVED");
            queryMessageRepository.save(q);
            return ResponseEntity.ok(q);
        }).orElse(ResponseEntity.notFound().build());
    }
}
