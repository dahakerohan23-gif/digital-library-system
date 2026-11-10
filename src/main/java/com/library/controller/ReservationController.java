package com.library.controller;

import com.library.model.Book;
import com.library.model.Reservation;
import com.library.model.User;
import com.library.repository.BookRepository;
import com.library.repository.ReservationRepository;
import com.library.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reservations")
@CrossOrigin(origins = "*")
public class ReservationController {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public List<Reservation> getAllReservations(@RequestParam(required = false) Long userId) {
        if (userId != null) {
            return reservationRepository.findByUserId(userId);
        }
        return reservationRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<?> reserveBook(@RequestBody Map<String, Object> payload) {
        Long bookId = Long.valueOf(payload.get("bookId").toString());
        Long userId = Long.valueOf(payload.get("userId").toString());

        var bookOpt = bookRepository.findById(bookId);
        var userOpt = userRepository.findById(userId);

        if (bookOpt.isEmpty() || userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid book or user");
        }

        Book book = bookOpt.get();
        User user = userOpt.get();

        if (reservationRepository.existsByBookIdAndUserIdAndStatus(bookId, userId, "PENDING")) {
            return ResponseEntity.badRequest().body("You already have a pending advance booking for this book.");
        }

        Reservation res = new Reservation(null, book.getId(), book.getTitle(), user.getId(), user.getName(), user.getEmail());
        Reservation saved = reservationRepository.save(res);

        return ResponseEntity.ok(saved);
    }
}
