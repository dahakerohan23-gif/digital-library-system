package com.library.controller;

import com.library.model.Book;
import com.library.model.IssueRecord;
import com.library.model.User;
import com.library.repository.BookRepository;
import com.library.repository.IssueRecordRepository;
import com.library.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/issues")
@CrossOrigin(origins = "*")
public class IssueController {

    private static final double FINE_PER_DAY = 5.0; // ₹5 per day

    @Autowired
    private IssueRecordRepository issueRecordRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public List<IssueRecord> getAllIssues(@RequestParam(required = false) Long userId) {
        List<IssueRecord> list = (userId != null) ? issueRecordRepository.findByUserId(userId) : issueRecordRepository.findAll();
        // Update fine calculations dynamically
        for (IssueRecord issue : list) {
            issue.updateOverdueStatus();
        }
        return list;
    }

    @PostMapping
    public ResponseEntity<?> issueBook(@RequestBody Map<String, Object> payload) {
        Long bookId = Long.valueOf(payload.get("bookId").toString());
        Long userId = Long.valueOf(payload.get("userId").toString());

        var bookOpt = bookRepository.findById(bookId);
        var userOpt = userRepository.findById(userId);

        if (bookOpt.isEmpty() || userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid book or user identifier");
        }

        Book book = bookOpt.get();
        User user = userOpt.get();

        if (book.getAvailableQuantity() <= 0) {
            return ResponseEntity.badRequest().body("No copies available in inventory. Please reserve via Advance Booking.");
        }

        // Decrement inventory
        book.setAvailableQuantity(book.getAvailableQuantity() - 1);
        bookRepository.save(book);

        // Standard 7 days borrowing period
        LocalDate issueDate = LocalDate.now();
        LocalDate dueDate = issueDate.plusDays(7);

        IssueRecord issue = new IssueRecord(null, book.getId(), book.getTitle(), user.getId(), user.getName(), user.getEmail(), issueDate, dueDate);
        IssueRecord saved = issueRecordRepository.save(issue);

        return ResponseEntity.ok(saved);
    }

    @PostMapping("/{id}/return")
    public ResponseEntity<?> returnBook(@PathVariable Long id) {
        return issueRecordRepository.findById(id).map(issue -> {
            if ("RETURNED".equals(issue.getStatus())) {
                return ResponseEntity.badRequest().body("Book is already marked as returned.");
            }

            LocalDate today = LocalDate.now();
            issue.setReturnDate(today);

            if (today.isAfter(issue.getDueDate())) {
                long days = ChronoUnit.DAYS.between(issue.getDueDate(), today);
                issue.setFineAmount(days * FINE_PER_DAY);
            }
            issue.setStatus("RETURNED");
            issueRecordRepository.save(issue);

            // Increment book inventory
            bookRepository.findById(issue.getBookId()).ifPresent(b -> {
                b.setAvailableQuantity(Math.min(b.getQuantity(), b.getAvailableQuantity() + 1));
                bookRepository.save(b);
            });

            return ResponseEntity.ok(issue);
        }).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/pay-fine")
    public ResponseEntity<?> payFine(@PathVariable Long id) {
        return issueRecordRepository.findById(id).map(issue -> {
            issue.setFinePaid(true);
            issueRecordRepository.save(issue);
            return ResponseEntity.ok(issue);
        }).orElse(ResponseEntity.notFound().build());
    }
}
