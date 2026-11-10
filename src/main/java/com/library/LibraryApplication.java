package com.library;

import com.library.model.*;
import com.library.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDate;

@SpringBootApplication
public class LibraryApplication {

    public static void main(String[] args) {
        SpringApplication.run(LibraryApplication.class, args);
    }

    @Bean
    CommandLineRunner initDatabase(
            UserRepository userRepository,
            BookRepository bookRepository,
            IssueRecordRepository issueRecordRepository,
            ReservationRepository reservationRepository,
            QueryMessageRepository queryMessageRepository) {
        return args -> {
            // Seed Admin and Demo User if none exist
            if (userRepository.count() == 0) {
                User admin = new User(null, "System Administrator", "admin@library.com", "admin123", "ADMIN", "+91 9876543210");
                User user1 = new User(null, "Aarav Sharma", "user@library.com", "user123", "USER", "+91 9811223344");
                User user2 = new User(null, "Priya Patel", "priya@example.com", "user123", "USER", "+91 9822334455");

                userRepository.save(admin);
                userRepository.save(user1);
                userRepository.save(user2);
            }

            // Seed Books
            if (bookRepository.count() == 0) {
                bookRepository.save(new Book(null, "Clean Code: A Handbook of Agile Software Craftsmanship", "Robert C. Martin", "978-0132350884", "Computer Science", 5, 3, "💻", "A must-read handbook of agile software craftsmanship."));
                bookRepository.save(new Book(null, "Effective Java (3rd Edition)", "Joshua Bloch", "978-0134685991", "Computer Science", 4, 2, "☕", "Definitive guide to Java programming best practices."));
                bookRepository.save(new Book(null, "Introduction to Algorithms (CLRS)", "Thomas H. Cormen", "978-0262033848", "Computer Science", 3, 0, "🧮", "Standard academic textbook on modern algorithms."));
                bookRepository.save(new Book(null, "To Kill a Mockingbird", "Harper Lee", "978-0060935467", "Literature", 6, 5, "📖", "Pulitzer Prize-winning masterpiece exploring empathy and justice."));
                bookRepository.save(new Book(null, "A Brief History of Time", "Stephen Hawking", "978-0553380163", "Science", 4, 4, "🌌", "Landmark exploration of cosmology, space, and time."));
                bookRepository.save(new Book(null, "The Great Gatsby", "F. Scott Fitzgerald", "978-0743273565", "Fiction", 4, 1, "🍸", "Classic portrait of the Jazz Age and the American dream."));
                bookRepository.save(new Book(null, "Thinking, Fast and Slow", "Daniel Kahneman", "978-0374533557", "Business", 5, 0, "🧠", "Profound look at behavioral economics and human intuition."));
                bookRepository.save(new Book(null, "Design Patterns: Elements of Reusable Object-Oriented Software", "Erich Gamma et al.", "978-0201633610", "Computer Science", 3, 2, "🏗️", "The classic Gang of Four software engineering reference."));
            }

            // Seed Sample Issue Record with Overdue Fine
            if (issueRecordRepository.count() == 0) {
                User demoUser = userRepository.findByEmail("user@library.com").orElse(null);
                Book clrsBook = bookRepository.findAll().stream().filter(b -> b.getIsbn().equals("978-0262033848")).findFirst().orElse(null);

                if (demoUser != null && clrsBook != null) {
                    IssueRecord overdueIssue = new IssueRecord(null, clrsBook.getId(), clrsBook.getTitle(), demoUser.getId(), demoUser.getName(), demoUser.getEmail(), LocalDate.now().minusDays(10), LocalDate.now().minusDays(3));
                    overdueIssue.setStatus("OVERDUE");
                    overdueIssue.setFineAmount(15.0); // 3 days overdue * ₹5
                    issueRecordRepository.save(overdueIssue);
                }
            }

            // Seed Sample Advance Booking
            if (reservationRepository.count() == 0) {
                User priya = userRepository.findByEmail("priya@example.com").orElse(null);
                Book clrsBook = bookRepository.findAll().stream().filter(b -> b.getIsbn().equals("978-0262033848")).findFirst().orElse(null);

                if (priya != null && clrsBook != null) {
                    reservationRepository.save(new Reservation(null, clrsBook.getId(), clrsBook.getTitle(), priya.getId(), priya.getName(), priya.getEmail()));
                }
            }

            // Seed Sample Query
            if (queryMessageRepository.count() == 0) {
                queryMessageRepository.save(new QueryMessage(null, 1L, "Rohan Verma", "rohan@example.com", "Acquisition Request: Designing Data-Intensive Applications", "Could the library acquire Martin Kleppmann's DDIA?"));
            }
        };
    }
}
