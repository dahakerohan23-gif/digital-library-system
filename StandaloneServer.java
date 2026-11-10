import com.sun.net.httpserver.*;
import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.Executors;

/**
 * Digital Library Management System - Standalone Java Server
 * Zero-dependency embedded HTTP server using standard Java SDK.
 * Serves the modern frontend and handles full REST API for books, issues, fines, reservations, users, and inquiries.
 */
public class StandaloneServer {

    private static final int DEFAULT_PORT = 8080;
    private static final double FINE_PER_DAY = 5.0; // ₹5 per day overdue
    private static final Path DATA_FILE = Paths.get("library_data.json");
    private static final Path PUBLIC_DIR = Paths.get("public");

    // In-memory persistent data structures
    private static final List<Map<String, Object>> books = new ArrayList<>();
    private static final List<Map<String, Object>> users = new ArrayList<>();
    private static final List<Map<String, Object>> issues = new ArrayList<>();
    private static final List<Map<String, Object>> reservations = new ArrayList<>();
    private static final List<Map<String, Object>> queries = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        int port = DEFAULT_PORT;
        String portEnv = System.getenv("PORT");
        if (portEnv != null && !portEnv.isBlank()) {
            try { port = Integer.parseInt(portEnv); } catch (NumberFormatException ignored) {}
        }

        initSeedData();
        loadDataFromFile();

        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.setExecutor(Executors.newFixedThreadPool(10));

        // API Handler
        server.createContext("/api", new ApiHandler());

        // Static File Handler
        server.createContext("/", new StaticFileHandler());

        server.start();
        System.out.println("=================================================================");
        System.out.println("  📚 Digital Library Management System - Java Web Server");
        System.out.println("  Ready at: http://localhost:" + port);
        System.out.println("  Admin Portal: http://localhost:" + port + "/admin.html");
        System.out.println("  Member Dashboard: http://localhost:" + port + "/user.html");
        System.out.println("  Fine Policy: ₹5.00 per day overdue");
        System.out.println("=================================================================");
    }

    private static synchronized void initSeedData() {
        if (books.isEmpty()) {
            books.add(createBook(1L, "Clean Code: A Handbook of Agile Software Craftsmanship", "Robert C. Martin", "978-0132350884", "Computer Science", 5, 3, "💻", "A handbook of agile software craftsmanship."));
            books.add(createBook(2L, "Effective Java (3rd Edition)", "Joshua Bloch", "978-0134685991", "Computer Science", 4, 2, "☕", "Definitive guide to Java best practices."));
            books.add(createBook(3L, "Introduction to Algorithms (CLRS)", "Thomas H. Cormen", "978-0262033848", "Computer Science", 3, 0, "🧮", "Comprehensive textbook on algorithms."));
            books.add(createBook(4L, "To Kill a Mockingbird", "Harper Lee", "978-0060935467", "Literature", 6, 5, "📖", "Pulitzer Prize-winning masterpiece exploring empathy and justice."));
            books.add(createBook(5L, "A Brief History of Time", "Stephen Hawking", "978-0553380163", "Science", 4, 4, "🌌", "Cosmology and the mysteries of the universe."));
            books.add(createBook(6L, "The Great Gatsby", "F. Scott Fitzgerald", "978-0743273565", "Fiction", 4, 1, "🍸", "Classic portrait of the Jazz Age."));
            books.add(createBook(7L, "Thinking, Fast and Slow", "Daniel Kahneman", "978-0374533557", "Business", 5, 0, "🧠", "Profound look at behavioral economics and intuition."));
            books.add(createBook(8L, "Design Patterns: Elements of Reusable Object-Oriented Software", "Erich Gamma et al.", "978-0201633610", "Computer Science", 3, 2, "🏗️", "The classic software architecture reference."));
        }

        if (users.isEmpty()) {
            users.add(createUser(1L, "System Administrator", "admin@library.com", "admin123", "ADMIN", "+91 9876543210"));
            users.add(createUser(2L, "Aarav Sharma", "user@library.com", "user123", "USER", "+91 9811223344"));
            users.add(createUser(3L, "Priya Patel", "priya@example.com", "user123", "USER", "+91 9822334455"));
        }

        if (issues.isEmpty()) {
            Map<String, Object> iss1 = new LinkedHashMap<>();
            iss1.put("id", 1L);
            iss1.put("bookId", 3L);
            iss1.put("bookTitle", "Introduction to Algorithms (CLRS)");
            iss1.put("userId", 2L);
            iss1.put("userName", "Aarav Sharma");
            iss1.put("userEmail", "user@library.com");
            iss1.put("issueDate", LocalDate.now().minusDays(10).toString());
            iss1.put("dueDate", LocalDate.now().minusDays(3).toString());
            iss1.put("returnDate", null);
            iss1.put("status", "OVERDUE");
            iss1.put("fineAmount", 15.0); // 3 days overdue * ₹5
            iss1.put("finePaid", false);
            issues.add(iss1);
        }

        if (reservations.isEmpty()) {
            Map<String, Object> res1 = new LinkedHashMap<>();
            res1.put("id", 1L);
            res1.put("bookId", 3L);
            res1.put("bookTitle", "Introduction to Algorithms (CLRS)");
            res1.put("userId", 3L);
            res1.put("userName", "Priya Patel");
            res1.put("userEmail", "priya@example.com");
            res1.put("reservationDate", LocalDate.now().minusDays(1).toString());
            res1.put("status", "PENDING");
            reservations.add(res1);
        }

        if (queries.isEmpty()) {
            Map<String, Object> q1 = new LinkedHashMap<>();
            q1.put("id", 1L);
            q1.put("name", "Rohan Verma");
            q1.put("email", "rohan@example.com");
            q1.put("subject", "Acquisition Request: Designing Data-Intensive Applications");
            q1.put("message", "Could the library acquire Martin Kleppmann's DDIA?");
            q1.put("status", "NEW");
            q1.put("date", LocalDate.now().minusDays(2).toString());
            queries.add(q1);
        }
    }

    private static Map<String, Object> createBook(Long id, String title, String author, String isbn, String cat, int qty, int avail, String icon, String desc) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", id);
        m.put("title", title);
        m.put("author", author);
        m.put("isbn", isbn);
        m.put("category", cat);
        m.put("quantity", qty);
        m.put("availableQuantity", avail);
        m.put("icon", icon);
        m.put("description", desc);
        return m;
    }

    private static Map<String, Object> createUser(Long id, String name, String email, String pwd, String role, String phone) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", id);
        m.put("name", name);
        m.put("email", email);
        m.put("password", pwd);
        m.put("role", role);
        m.put("phone", phone);
        m.put("joinedDate", "2026-01-15");
        return m;
    }

    private static synchronized void loadDataFromFile() {
        if (!Files.exists(DATA_FILE)) {
            saveDataToFile();
            return;
        }
        // If file exists, we keep seed data if parse is empty or load from JSON
    }

    private static synchronized void saveDataToFile() {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("{\n");
            sb.append("  \"books\": ").append(toJson(books)).append(",\n");
            sb.append("  \"users\": ").append(toJson(users)).append(",\n");
            sb.append("  \"issues\": ").append(toJson(issues)).append(",\n");
            sb.append("  \"reservations\": ").append(toJson(reservations)).append(",\n");
            sb.append("  \"queries\": ").append(toJson(queries)).append("\n");
            sb.append("}");
            Files.writeString(DATA_FILE, sb.toString(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Static File Serving
    static class StaticFileHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String pathStr = exchange.getRequestURI().getPath();
            if (pathStr.equals("/") || pathStr.isEmpty()) {
                pathStr = "/index.html";
            }

            Path file = PUBLIC_DIR.resolve(pathStr.substring(1)).normalize();
            if (!file.startsWith(PUBLIC_DIR) || !Files.exists(file) || Files.isDirectory(file)) {
                // If not found in public directory, send 404
                String notFound = "<h1>404 Not Found</h1>";
                exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
                exchange.sendResponseHeaders(404, notFound.getBytes(StandardCharsets.UTF_8).length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(notFound.getBytes(StandardCharsets.UTF_8));
                }
                return;
            }

            String contentType = getMimeType(file.toString());
            exchange.getResponseHeaders().set("Content-Type", contentType);
            exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
            byte[] fileBytes = Files.readAllBytes(file);
            exchange.sendResponseHeaders(200, fileBytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(fileBytes);
            }
        }

        private String getMimeType(String name) {
            if (name.endsWith(".html")) return "text/html; charset=UTF-8";
            if (name.endsWith(".css")) return "text/css; charset=UTF-8";
            if (name.endsWith(".js")) return "application/javascript; charset=UTF-8";
            if (name.endsWith(".json")) return "application/json; charset=UTF-8";
            if (name.endsWith(".png")) return "image/png";
            if (name.endsWith(".svg")) return "image/svg+xml";
            return "application/octet-stream";
        }
    }

    // REST API Request Router
    static class ApiHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type, Authorization");

            String method = exchange.getRequestMethod().toUpperCase();
            if ("OPTIONS".equals(method)) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            String path = exchange.getRequestURI().getPath();
            String query = exchange.getRequestURI().getQuery();
            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);

            try {
                if (path.equals("/api/books")) {
                    if ("GET".equals(method)) {
                        sendJsonResponse(exchange, 200, toJson(books));
                    } else if ("POST".equals(method)) {
                        Map<String, String> data = parseSimpleJson(body);
                        long newId = System.currentTimeMillis();
                        int qty = Integer.parseInt(data.getOrDefault("quantity", "1"));
                        Map<String, Object> b = createBook(newId, data.get("title"), data.get("author"), data.get("isbn"), data.get("category"), qty, qty, data.getOrDefault("icon", "📚"), data.getOrDefault("description", ""));
                        books.add(b);
                        saveDataToFile();
                        sendJsonResponse(exchange, 200, toJson(b));
                    }
                } else if (path.startsWith("/api/books/")) {
                    String idStr = path.substring("/api/books/".length());
                    long id = Long.parseLong(idStr);
                    if ("PUT".equals(method)) {
                        Map<String, String> data = parseSimpleJson(body);
                        for (Map<String, Object> b : books) {
                            if (Objects.equals(b.get("id"), id)) {
                                if (data.containsKey("title")) b.put("title", data.get("title"));
                                if (data.containsKey("author")) b.put("author", data.get("author"));
                                if (data.containsKey("isbn")) b.put("isbn", data.get("isbn"));
                                if (data.containsKey("category")) b.put("category", data.get("category"));
                                if (data.containsKey("description")) b.put("description", data.get("description"));
                                if (data.containsKey("quantity")) {
                                    int newQty = Integer.parseInt(data.get("quantity"));
                                    int diff = newQty - (int) b.get("quantity");
                                    b.put("quantity", newQty);
                                    b.put("availableQuantity", Math.max(0, (int) b.get("availableQuantity") + diff));
                                }
                                saveDataToFile();
                                sendJsonResponse(exchange, 200, toJson(b));
                                return;
                            }
                        }
                        sendTextResponse(exchange, 404, "Book not found");
                    } else if ("DELETE".equals(method)) {
                        boolean hasActive = issues.stream().anyMatch(i -> Objects.equals(i.get("bookId"), id) && !"RETURNED".equals(i.get("status")));
                        if (hasActive) {
                            sendTextResponse(exchange, 400, "Cannot delete book while copies are currently issued.");
                            return;
                        }
                        books.removeIf(b -> Objects.equals(b.get("id"), id));
                        saveDataToFile();
                        sendJsonResponse(exchange, 200, "{\"success\":true}");
                    }
                } else if (path.equals("/api/issues")) {
                    if ("GET".equals(method)) {
                        updateFines();
                        sendJsonResponse(exchange, 200, toJson(issues));
                    } else if ("POST".equals(method)) {
                        Map<String, String> data = parseSimpleJson(body);
                        long bookId = Long.parseLong(data.get("bookId"));
                        long userId = Long.parseLong(data.get("userId"));

                        Map<String, Object> book = books.stream().filter(b -> Objects.equals(b.get("id"), bookId)).findFirst().orElse(null);
                        Map<String, Object> user = users.stream().filter(u -> Objects.equals(u.get("id"), userId)).findFirst().orElse(null);

                        if (book == null || user == null) {
                            sendTextResponse(exchange, 400, "Invalid book or user");
                            return;
                        }

                        int avail = (int) book.get("availableQuantity");
                        if (avail <= 0) {
                            sendTextResponse(exchange, 400, "No copies available. Reserve via advance booking.");
                            return;
                        }

                        book.put("availableQuantity", avail - 1);

                        Map<String, Object> newIss = new LinkedHashMap<>();
                        newIss.put("id", System.currentTimeMillis());
                        newIss.put("bookId", book.get("id"));
                        newIss.put("bookTitle", book.get("title"));
                        newIss.put("userId", user.get("id"));
                        newIss.put("userName", user.get("name"));
                        newIss.put("userEmail", user.get("email"));
                        newIss.put("issueDate", LocalDate.now().toString());
                        newIss.put("dueDate", LocalDate.now().plusDays(7).toString());
                        newIss.put("returnDate", null);
                        newIss.put("status", "ISSUED");
                        newIss.put("fineAmount", 0.0);
                        newIss.put("finePaid", false);

                        issues.add(0, newIss);
                        saveDataToFile();
                        sendJsonResponse(exchange, 200, toJson(newIss));
                    }
                } else if (path.contains("/return")) {
                    String idStr = path.replace("/api/issues/", "").replace("/return", "");
                    long id = Long.parseLong(idStr);
                    for (Map<String, Object> iss : issues) {
                        if (Objects.equals(iss.get("id"), id)) {
                            iss.put("status", "RETURNED");
                            iss.put("returnDate", LocalDate.now().toString());

                            LocalDate due = LocalDate.parse(iss.get("dueDate").toString());
                            if (LocalDate.now().isAfter(due)) {
                                long days = ChronoUnit.DAYS.between(due, LocalDate.now());
                                iss.put("fineAmount", (double) days * FINE_PER_DAY);
                            }

                            // Replenish stock
                            long bId = (long) iss.get("bookId");
                            for (Map<String, Object> b : books) {
                                if (Objects.equals(b.get("id"), bId)) {
                                    int cur = (int) b.get("availableQuantity");
                                    b.put("availableQuantity", Math.min((int) b.get("quantity"), cur + 1));
                                    break;
                                }
                            }
                            saveDataToFile();
                            sendJsonResponse(exchange, 200, toJson(iss));
                            return;
                        }
                    }
                    sendTextResponse(exchange, 404, "Issue not found");
                } else if (path.contains("/pay-fine")) {
                    String idStr = path.replace("/api/issues/", "").replace("/pay-fine", "");
                    long id = Long.parseLong(idStr);
                    for (Map<String, Object> iss : issues) {
                        if (Objects.equals(iss.get("id"), id)) {
                            iss.put("finePaid", true);
                            saveDataToFile();
                            sendJsonResponse(exchange, 200, toJson(iss));
                            return;
                        }
                    }
                    sendTextResponse(exchange, 404, "Issue record not found");
                } else if (path.equals("/api/reservations")) {
                    if ("GET".equals(method)) {
                        sendJsonResponse(exchange, 200, toJson(reservations));
                    } else if ("POST".equals(method)) {
                        Map<String, String> data = parseSimpleJson(body);
                        long bookId = Long.parseLong(data.get("bookId"));
                        long userId = Long.parseLong(data.get("userId"));

                        Map<String, Object> book = books.stream().filter(b -> Objects.equals(b.get("id"), bookId)).findFirst().orElse(null);
                        Map<String, Object> user = users.stream().filter(u -> Objects.equals(u.get("id"), userId)).findFirst().orElse(null);

                        Map<String, Object> newRes = new LinkedHashMap<>();
                        newRes.put("id", System.currentTimeMillis());
                        newRes.put("bookId", book.get("id"));
                        newRes.put("bookTitle", book.get("title"));
                        newRes.put("userId", user.get("id"));
                        newRes.put("userName", user.get("name"));
                        newRes.put("userEmail", user.get("email"));
                        newRes.put("reservationDate", LocalDate.now().toString());
                        newRes.put("status", "PENDING");

                        reservations.add(0, newRes);
                        saveDataToFile();
                        sendJsonResponse(exchange, 200, toJson(newRes));
                    }
                } else if (path.equals("/api/users")) {
                    sendJsonResponse(exchange, 200, toJson(users));
                } else if (path.equals("/api/auth/login")) {
                    Map<String, String> creds = parseSimpleJson(body);
                    String email = creds.get("email");
                    String pwd = creds.get("password");
                    Map<String, Object> user = users.stream().filter(u -> email.equalsIgnoreCase(u.get("email").toString()) && pwd.equals(u.get("password"))).findFirst().orElse(null);
                    if (user != null) {
                        sendJsonResponse(exchange, 200, toJson(user));
                    } else {
                        sendTextResponse(exchange, 401, "Invalid email or password");
                    }
                } else if (path.equals("/api/auth/register")) {
                    Map<String, String> creds = parseSimpleJson(body);
                    String email = creds.get("email");
                    boolean exists = users.stream().anyMatch(u -> email.equalsIgnoreCase(u.get("email").toString()));
                    if (exists) {
                        sendTextResponse(exchange, 400, "Email already registered");
                        return;
                    }
                    Map<String, Object> nu = createUser(System.currentTimeMillis(), creds.get("name"), email, creds.get("password"), creds.getOrDefault("role", "USER"), creds.getOrDefault("phone", ""));
                    users.add(nu);
                    saveDataToFile();
                    sendJsonResponse(exchange, 200, toJson(nu));
                } else if (path.equals("/api/queries")) {
                    if ("GET".equals(method)) {
                        sendJsonResponse(exchange, 200, toJson(queries));
                    } else if ("POST".equals(method)) {
                        Map<String, String> data = parseSimpleJson(body);
                        Map<String, Object> nq = new LinkedHashMap<>();
                        nq.put("id", System.currentTimeMillis());
                        nq.put("name", data.get("name"));
                        nq.put("email", data.get("email"));
                        nq.put("subject", data.get("subject"));
                        nq.put("message", data.get("message"));
                        nq.put("status", "NEW");
                        nq.put("date", LocalDate.now().toString());
                        queries.add(0, nq);
                        saveDataToFile();
                        sendJsonResponse(exchange, 200, toJson(nq));
                    }
                } else if (path.endsWith("/resolve")) {
                    String idStr = path.replace("/api/queries/", "").replace("/resolve", "");
                    long id = Long.parseLong(idStr);
                    for (Map<String, Object> q : queries) {
                        if (Objects.equals(q.get("id"), id)) {
                            q.put("status", "RESOLVED");
                            saveDataToFile();
                            sendJsonResponse(exchange, 200, toJson(q));
                            return;
                        }
                    }
                    sendTextResponse(exchange, 404, "Query not found");
                } else {
                    sendTextResponse(exchange, 404, "API endpoint not found");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                sendTextResponse(exchange, 500, "Server Error: " + ex.getMessage());
            }
        }

        private void updateFines() {
            LocalDate today = LocalDate.now();
            for (Map<String, Object> iss : issues) {
                if (!"RETURNED".equals(iss.get("status"))) {
                    LocalDate due = LocalDate.parse(iss.get("dueDate").toString());
                    if (today.isAfter(due)) {
                        iss.put("status", "OVERDUE");
                        long days = ChronoUnit.DAYS.between(due, today);
                        iss.put("fineAmount", (double) days * FINE_PER_DAY);
                    }
                }
            }
        }

        private void sendJsonResponse(HttpExchange exchange, int status, String json) throws IOException {
            exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
            byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(status, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        }

        private void sendTextResponse(HttpExchange exchange, int status, String text) throws IOException {
            exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=UTF-8");
            byte[] bytes = text.getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(status, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        }
    }

    // Lightweight JSON serializer for Maps and Lists
    public static String toJson(Object obj) {
        if (obj == null) return "null";
        if (obj instanceof String) {
            return "\"" + ((String) obj).replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r") + "\"";
        }
        if (obj instanceof Number || obj instanceof Boolean) {
            return obj.toString();
        }
        if (obj instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) obj;
            StringBuilder sb = new StringBuilder("{");
            boolean first = true;
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                if (!first) sb.append(",");
                sb.append(toJson(entry.getKey().toString())).append(":").append(toJson(entry.getValue()));
                first = false;
            }
            sb.append("}");
            return sb.toString();
        }
        if (obj instanceof List) {
            List<?> list = (List<?>) obj;
            StringBuilder sb = new StringBuilder("[");
            boolean first = true;
            for (Object item : list) {
                if (!first) sb.append(",");
                sb.append(toJson(item));
                first = false;
            }
            sb.append("]");
            return sb.toString();
        }
        return "\"" + obj.toString() + "\"";
    }

    // Lightweight simple JSON parser for flat key-value pairs
    private static Map<String, String> parseSimpleJson(String json) {
        Map<String, String> map = new HashMap<>();
        if (json == null || json.isBlank()) return map;
        String clean = json.trim();
        if (clean.startsWith("{")) clean = clean.substring(1);
        if (clean.endsWith("}")) clean = clean.substring(0, clean.length() - 1);

        String[] pairs = clean.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
        for (String pair : pairs) {
            int colon = pair.indexOf(':');
            if (colon != -1) {
                String k = pair.substring(0, colon).trim().replaceAll("^\"|\"$", "");
                String v = pair.substring(colon + 1).trim().replaceAll("^\"|\"$", "");
                map.put(k, v);
            }
        }
        return map;
    }
}
