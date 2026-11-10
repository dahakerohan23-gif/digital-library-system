package com.library.controller;

import com.library.model.Book;
import com.library.repository.BookRepository;
import com.library.repository.IssueRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
@CrossOrigin(origins = "*")
public class BookController {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private IssueRecordRepository issueRecordRepository;

    @GetMapping
    public List<Book> getAllBooks(@RequestParam(required = false) String category,
                                  @RequestParam(required = false) String search) {
        if (category != null && !category.equalsIgnoreCase("ALL")) {
            return bookRepository.findByCategoryIgnoreCase(category);
        }
        if (search != null && !search.isBlank()) {
            return bookRepository.findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(search, search);
        }
        return bookRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Book> getBookById(@PathVariable Long id) {
        return bookRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> addBook(@RequestBody Book book) {
        if (book.getTitle() == null || book.getTitle().isBlank()) {
            return ResponseEntity.badRequest().body("Title is required");
        }
        if (book.getIsbn() != null && bookRepository.existsByIsbn(book.getIsbn())) {
            return ResponseEntity.badRequest().body("Book with this ISBN already exists");
        }
        if (book.getAvailableQuantity() == 0 && book.getQuantity() > 0) {
            book.setAvailableQuantity(book.getQuantity());
        }
        Book saved = bookRepository.save(book);
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateBook(@PathVariable Long id, @RequestBody Book updatedBook) {
        return bookRepository.findById(id).map(book -> {
            int qtyDiff = updatedBook.getQuantity() - book.getQuantity();
            book.setTitle(updatedBook.getTitle());
            book.setAuthor(updatedBook.getAuthor());
            book.setIsbn(updatedBook.getIsbn());
            book.setCategory(updatedBook.getCategory());
            book.setQuantity(updatedBook.getQuantity());
            book.setAvailableQuantity(Math.max(0, book.getAvailableQuantity() + qtyDiff));
            book.setDescription(updatedBook.getDescription());
            if (updatedBook.getIcon() != null && !updatedBook.getIcon().isBlank()) {
                book.setIcon(updatedBook.getIcon());
            }
            return ResponseEntity.ok(bookRepository.save(book));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBook(@PathVariable Long id) {
        var activeIssues = issueRecordRepository.findByBookIdAndStatusNot(id, "RETURNED");
        if (!activeIssues.isEmpty()) {
            return ResponseEntity.badRequest().body("Cannot delete book while copies are currently issued to members.");
        }
        if (bookRepository.existsById(id)) {
            bookRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
