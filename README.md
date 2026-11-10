# 📚 Digital Library Management System (TASK 5)

[![Java](https://img.shields.io/badge/Java-21%2B-ED8B00?logo=openjdk&logoColor=white)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.5-6DB33F?logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Vercel Ready](https://img.shields.io/badge/Vercel-Deployment%20Ready-000000?logo=vercel&logoColor=white)](https://vercel.com)
[![Docker](https://img.shields.io/badge/Docker-Containerized-2496ED?logo=docker&logoColor=white)](https://www.docker.com/)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

A web-based digital library management system featuring distinct **Administrator** and **Member (User)** portals, dynamic book catalogue browsing and real-time search, automated borrowing and returns, **automatic overdue fine calculation (₹5.00/day)**, **advance booking (reserve queue)** for checked-out books, and an integrated **contact/query desk**.

---

## 🚀 Live Demo & Deployment

This project includes **dual deployment support**:
1. **Vercel (Live Web Client)**: Configured via [`vercel.json`](file:///d:/LMS%20java/vercel.json) with intelligent local state persistence and seed data for instant 1-click evaluation.
2. **Render / Railway / Docker (Live Java Backend)**: Configured via [`Dockerfile`](file:///d:/LMS%20java/Dockerfile) and [`render.yaml`](file:///d:/LMS%20java/render.yaml) for full cloud hosting of the JVM server.

---

## 📋 Feature Checklist Implementation

### 🛡️ Admin Module
- [x] **Admin Authentication**: Secure login with administrative role verification.
- [x] **Add New Book**: Enter title, author, ISBN, category, quantity, and description with instant inventory sync.
- [x] **Edit & Delete Books**: Update title, quantity, or category, with delete protection preventing removal of currently borrowed books.
- [x] **View Issued Books & Due Dates**: Real-time auditing table displaying borrower details, checkout date, due date, and overdue countdown.
- [x] **Member Account Management**: View all registered user profiles, borrow limits, contact information, and account status.
- [x] **Fine Management**: Live fine tracking with a 1-click **"Mark Fine as Paid"** administrative action.
- [x] **Inquiry Resolution**: Review and mark user contact queries as resolved.

### 👤 User (Member) Module
- [x] **Registration & Login**: Self-service registration with automatic role assignment and authentication.
- [x] **Browse by Category**: Categorized navigation (Computer Science, Literature, Science, Fiction, Business, Mathematics).
- [x] **Live Real-time Search**: Search books dynamically by title, author name, or ISBN.
- [x] **Issue a Book**: Decrements available stock count and records a 7-day return due date.
- [x] **Return a Book**: Member can return borrowed books directly; available inventory increments immediately.
- [x] **Automated Overdue Fines**: Automatically calculates **₹5.00 per day** overdue once the due date passes.
- [x] **Advance Booking (Reservation)**: When a title has 0 available copies, members can reserve a spot in the priority queue.
- [x] **Contact / Query Desk**: Members can submit queries/requests stored in the database for admin review.

---

## 🔑 Default Credentials

| Role | Email | Password | Quick Action |
|---|---|---|---|
| **Administrator** | `admin@library.com` | `admin123` | Full control of inventory, fines, and users |
| **Member (User)** | `user@library.com` | `user123` | Borrow, return, reserve, and view fine statement |

*(You can also register any new member account directly on the login page).*

---

## 🛠️ Architecture & Tech Stack

```
d:\LMS java\
├── public/                      # Static Web Client (Vercel & Java Server Root)
│   ├── css/style.css            # Responsive, modern UI styling
│   ├── js/app.js                # REST API client with offline demo fallback
│   ├── index.html               # Main Book Catalogue & search interface
│   ├── admin.html               # Administrator Control Panel
│   ├── user.html                # Member Dashboard & fine accounts
│   ├── login.html               # Authentication & Registration
│   └── contact.html             # Support & Query form
│
├── src/main/                    # Standard Spring Boot Architecture
│   ├── java/com/library/
│   │   ├── model/               # Book, User, IssueRecord, Reservation, QueryMessage
│   │   ├── repository/          # Spring Data JPA repositories
│   │   ├── controller/          # REST endpoints (/api/books, /api/issues, etc.)
│   │   └── LibraryApplication.java
│   └── resources/
│       ├── application.properties
│       └── static/              # Packaged frontend assets
│
├── StandaloneServer.java        # Zero-dependency Java HTTP Server (Instant run with JDK)
├── run.bat / run.sh             # 1-click local launch scripts
├── pom.xml                      # Maven configuration for Spring Boot
├── vercel.json                  # 1-click Vercel deployment configuration
├── Dockerfile                   # Multi-stage Docker build for Render/Railway
└── render.yaml                  # Render cloud blueprint
```

---

## 💻 How to Run Locally

### Option 1: Instant Run with Standard Java (Zero Maven Needed)
If you have Java installed on your machine, simply execute:

**On Windows:**
Double-click [`run.bat`](file:///d:/LMS%20java/run.bat) or run in PowerShell:
```powershell
.\run.bat
```
Or directly:
```powershell
javac StandaloneServer.java
java StandaloneServer
```

**On Linux / macOS:**
```bash
chmod +x run.sh
./run.sh
```

Then open your browser to:
- **Catalogue & Search:** [http://localhost:8080](http://localhost:8080)
- **Admin Portal:** [http://localhost:8080/admin.html](http://localhost:8080/admin.html)
- **Member Dashboard:** [http://localhost:8080/user.html](http://localhost:8080/user.html)

---

### Option 2: Run with Spring Boot & Maven
```bash
mvn spring-boot:run
```
Access the application at [http://localhost:8080](http://localhost:8080) and H2 Database Console at [http://localhost:8080/h2-console](http://localhost:8080/h2-console).

---

## 🌐 Deploy to Vercel (Get Live Link)

### Method A: Deploy using Vercel CLI
```bash
# 1. Install Vercel CLI (if not already installed)
npm i -g vercel

# 2. Login to your Vercel account
vercel login

# 3. Deploy from the project root
vercel --prod
```

### Method B: Deploy via GitHub (Recommended)
1. Push this project to GitHub (see GitHub steps below).
2. Go to [https://vercel.com/new](https://vercel.com/new).
3. Import your GitHub repository `digital-library-system`.
4. Leave root directory as `./` (or `public`) and click **Deploy**.
5. Your live link (e.g. `https://digital-library-system.vercel.app`) is active immediately!

---

## 🌐 Deploy Java Backend to Render (Optional Free Cloud Host)
1. Push repository to GitHub.
2. Sign in to [https://render.com](https://render.com).
3. Select **New +** -> **Web Service**.
4. Connect your GitHub repository.
5. Choose **Docker** as environment (Render automatically picks up the [`Dockerfile`](file:///d:/LMS%20java/Dockerfile)).
6. Click **Create Web Service** to receive a free public HTTPS URL!

---

## 🐙 Upload to GitHub

Follow these steps in your terminal to create and push to a new GitHub repository:

```bash
# 1. Initialize git (already configured in workspace)
git init

# 2. Add all files
git add .

# 3. Commit changes
git commit -m "Initial commit: Digital Library Management System with Admin & User roles, ₹5/day fines, and advance bookings"

# 4. Set default branch to main
git branch -M main

# 5. Link your GitHub remote repository (replace with your repo URL)
git remote add origin https://github.com/<YOUR_GITHUB_USERNAME>/digital-library-system.git

# 6. Push to GitHub
git push -u origin main
```

---

## 📄 License
This project is open-source and available under the [MIT License](LICENSE).
