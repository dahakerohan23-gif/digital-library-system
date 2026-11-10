// Digital Library Management System - Frontend & API Client

const API_BASE = '/api';
const FINE_RATE_PER_DAY = 5.0; // ₹5 per day overdue
const BORROWING_PERIOD_DAYS = 7;

// Default Seed Data for offline/demo/Vercel static mode
const SEED_BOOKS = [
  {
    id: 1,
    title: "Clean Code: A Handbook of Agile Software Craftsmanship",
    author: "Robert C. Martin",
    isbn: "978-0132350884",
    category: "Computer Science",
    quantity: 5,
    availableQuantity: 3,
    description: "A must-read for any developer looking to write cleaner, more maintainable code.",
    icon: "💻"
  },
  {
    id: 2,
    title: "Effective Java (3rd Edition)",
    author: "Joshua Bloch",
    isbn: "978-0134685991",
    category: "Computer Science",
    quantity: 4,
    availableQuantity: 2,
    description: "Definitive guide to Java best practices and modern programming idioms.",
    icon: "☕"
  },
  {
    id: 3,
    title: "Introduction to Algorithms (CLRS)",
    author: "Thomas H. Cormen",
    isbn: "978-0262033848",
    category: "Computer Science",
    quantity: 3,
    availableQuantity: 0, // Out of stock to demonstrate advance booking!
    description: "Comprehensive textbook on contemporary computer algorithms.",
    icon: "🧮"
  },
  {
    id: 4,
    title: "To Kill a Mockingbird",
    author: "Harper Lee",
    isbn: "978-0060935467",
    category: "Literature",
    quantity: 6,
    availableQuantity: 5,
    description: "Pulitzer Prize-winning masterpiece exploring justice and empathy.",
    icon: "📖"
  },
  {
    id: 5,
    title: "A Brief History of Time",
    author: "Stephen Hawking",
    isbn: "978-0553380163",
    category: "Science",
    quantity: 4,
    availableQuantity: 4,
    description: "Landmark book in science writing exploring cosmology and black holes.",
    icon: "🌌"
  },
  {
    id: 6,
    title: "The Great Gatsby",
    author: "F. Scott Fitzgerald",
    isbn: "978-0743273565",
    category: "Fiction",
    quantity: 4,
    availableQuantity: 1,
    description: "Classic portrait of the Jazz Age, wealth, and the elusive American Dream.",
    icon: "🍸"
  },
  {
    id: 7,
    title: "Thinking, Fast and Slow",
    author: "Daniel Kahneman",
    isbn: "978-0374533557",
    category: "Business",
    quantity: 5,
    availableQuantity: 0, // Out of stock for reservation
    description: "Profound look at behavioral economics and the two systems driving our choices.",
    icon: "🧠"
  },
  {
    id: 8,
    title: "Design Patterns: Elements of Reusable Object-Oriented Software",
    author: "Erich Gamma, Richard Helm, Ralph Johnson, John Vlissides",
    isbn: "978-0201633610",
    category: "Computer Science",
    quantity: 3,
    availableQuantity: 2,
    description: "Classic Gang of Four patterns that transformed software engineering.",
    icon: "🏗️"
  }
];

const SEED_USERS = [
  {
    id: 1,
    name: "System Administrator",
    email: "admin@library.com",
    password: "admin123",
    role: "ADMIN",
    phone: "+91 9876543210",
    joinedDate: "2026-01-15"
  },
  {
    id: 2,
    name: "Aarav Sharma",
    email: "user@library.com",
    password: "user123",
    role: "USER",
    phone: "+91 9811223344",
    joinedDate: "2026-02-01"
  },
  {
    id: 3,
    name: "Priya Patel",
    email: "priya@example.com",
    password: "user123",
    role: "USER",
    phone: "+91 9822334455",
    joinedDate: "2026-02-10"
  }
];

// Helper to format date offset
function formatDateOffset(daysOffset) {
  const d = new Date();
  d.setDate(d.getDate() + daysOffset);
  return d.toISOString().split('T')[0];
}

const SEED_ISSUES = [
  {
    id: 1,
    bookId: 3,
    bookTitle: "Introduction to Algorithms (CLRS)",
    userId: 2,
    userName: "Aarav Sharma",
    userEmail: "user@library.com",
    issueDate: formatDateOffset(-10), // issued 10 days ago
    dueDate: formatDateOffset(-3),     // due 3 days ago (overdue!)
    returnDate: null,
    status: "OVERDUE",
    fineAmount: 15.0, // 3 days overdue * ₹5 = ₹15
    finePaid: false
  },
  {
    id: 2,
    bookId: 1,
    bookTitle: "Clean Code: A Handbook of Agile Software Craftsmanship",
    userId: 2,
    userName: "Aarav Sharma",
    userEmail: "user@library.com",
    issueDate: formatDateOffset(-2),
    dueDate: formatDateOffset(5),
    returnDate: null,
    status: "ISSUED",
    fineAmount: 0.0,
    finePaid: false
  },
  {
    id: 3,
    bookId: 7,
    bookTitle: "Thinking, Fast and Slow",
    userId: 3,
    userName: "Priya Patel",
    userEmail: "priya@example.com",
    issueDate: formatDateOffset(-5),
    dueDate: formatDateOffset(2),
    returnDate: null,
    status: "ISSUED",
    fineAmount: 0.0,
    finePaid: false
  }
];

const SEED_RESERVATIONS = [
  {
    id: 1,
    bookId: 3,
    bookTitle: "Introduction to Algorithms (CLRS)",
    userId: 3,
    userName: "Priya Patel",
    userEmail: "priya@example.com",
    reservationDate: formatDateOffset(-1),
    status: "PENDING"
  }
];

const SEED_QUERIES = [
  {
    id: 1,
    name: "Rohan Verma",
    email: "rohan@example.com",
    subject: "Request for System Architecture Books",
    message: "Could the library acquire copies of 'Designing Data-Intensive Applications' by Martin Kleppmann?",
    status: "NEW",
    date: formatDateOffset(-2)
  }
];

// LocalStore Layer
const LocalStore = {
  init() {
    if (!localStorage.getItem('dlms_initialized')) {
      localStorage.setItem('dlms_books', JSON.stringify(SEED_BOOKS));
      localStorage.setItem('dlms_users', JSON.stringify(SEED_USERS));
      localStorage.setItem('dlms_issues', JSON.stringify(SEED_ISSUES));
      localStorage.setItem('dlms_reservations', JSON.stringify(SEED_RESERVATIONS));
      localStorage.setItem('dlms_queries', JSON.stringify(SEED_QUERIES));
      localStorage.setItem('dlms_initialized', 'true');
    }
  },

  get(key) {
    this.init();
    try {
      return JSON.parse(localStorage.getItem(`dlms_${key}`)) || [];
    } catch (e) {
      return [];
    }
  },

  set(key, data) {
    localStorage.setItem(`dlms_${key}`, JSON.stringify(data));
  },

  calculateFines() {
    const issues = this.get('issues');
    const today = new Date();
    today.setHours(0, 0, 0, 0);

    let changed = false;
    issues.forEach(issue => {
      if (issue.status !== 'RETURNED') {
        const due = new Date(issue.dueDate);
        due.setHours(0, 0, 0, 0);
        const diffTime = today - due;
        const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));

        if (diffDays > 0) {
          issue.status = 'OVERDUE';
          issue.fineAmount = diffDays * FINE_RATE_PER_DAY;
          changed = true;
        } else {
          issue.status = 'ISSUED';
          issue.fineAmount = 0.0;
        }
      }
    });

    if (changed) {
      this.set('issues', issues);
    }
  }
};

// Auth State Helper
const Auth = {
  getCurrentUser() {
    try {
      const u = localStorage.getItem('dlms_current_user');
      return u ? JSON.parse(u) : null;
    } catch (e) {
      return null;
    }
  },

  setCurrentUser(user) {
    if (user) {
      localStorage.setItem('dlms_current_user', JSON.stringify(user));
    } else {
      localStorage.removeItem('dlms_current_user');
    }
  },

  logout() {
    this.setCurrentUser(null);
    window.location.href = 'index.html';
  },

  requireAuth(requiredRole = null) {
    const user = this.getCurrentUser();
    if (!user) {
      window.location.href = 'login.html';
      return null;
    }
    if (requiredRole && user.role !== requiredRole) {
      alert(`Access restricted to ${requiredRole}s only.`);
      window.location.href = 'index.html';
      return null;
    }
    return user;
  }
};

// Unified Data Service with Backend + LocalStore Fallback
const DataService = {
  async fetchWithFallback(endpoint, options = {}, fallbackAction) {
    try {
      const res = await fetch(`${API_BASE}${endpoint}`, {
        ...options,
        headers: { 'Content-Type': 'application/json', ...(options.headers || {}) }
      });
      if (!res.ok) throw new Error('API server returned error');
      return await res.json();
    } catch (err) {
      // Backend not running (e.g. static Vercel host), use LocalStore seamlessly!
      return fallbackAction();
    }
  },

  async getBooks() {
    return this.fetchWithFallback('/books', {}, () => LocalStore.get('books'));
  },

  async addBook(book) {
    return this.fetchWithFallback('/books', {
      method: 'POST',
      body: JSON.stringify(book)
    }, () => {
      const books = LocalStore.get('books');
      const newBook = {
        id: Date.now(),
        ...book,
        quantity: parseInt(book.quantity, 10),
        availableQuantity: parseInt(book.quantity, 10),
        icon: book.icon || "📚"
      };
      books.push(newBook);
      LocalStore.set('books', books);
      return newBook;
    });
  },

  async updateBook(id, book) {
    return this.fetchWithFallback(`/books/${id}`, {
      method: 'PUT',
      body: JSON.stringify(book)
    }, () => {
      const books = LocalStore.get('books');
      const idx = books.findIndex(b => b.id === Number(id));
      if (idx !== -1) {
        const prev = books[idx];
        const qtyDiff = parseInt(book.quantity, 10) - prev.quantity;
        books[idx] = {
          ...prev,
          ...book,
          quantity: parseInt(book.quantity, 10),
          availableQuantity: Math.max(0, prev.availableQuantity + qtyDiff)
        };
        LocalStore.set('books', books);
        return books[idx];
      }
      throw new Error('Book not found');
    });
  },

  async deleteBook(id) {
    return this.fetchWithFallback(`/books/${id}`, {
      method: 'DELETE'
    }, () => {
      const issues = LocalStore.get('issues');
      const hasActiveIssues = issues.some(i => i.bookId === Number(id) && i.status !== 'RETURNED');
      if (hasActiveIssues) {
        throw new Error('Cannot delete book while copies are currently issued to members.');
      }
      let books = LocalStore.get('books');
      books = books.filter(b => b.id !== Number(id));
      LocalStore.set('books', books);
      return { success: true };
    });
  },

  async issueBook(bookId, user) {
    return this.fetchWithFallback('/issues', {
      method: 'POST',
      body: JSON.stringify({ bookId, userId: user.id })
    }, () => {
      const books = LocalStore.get('books');
      const book = books.find(b => b.id === Number(bookId));
      if (!book) throw new Error('Book not found');
      if (book.availableQuantity <= 0) throw new Error('No available copies to issue. Please reserve via Advance Booking.');

      // Decrement quantity
      book.availableQuantity -= 1;
      LocalStore.set('books', books);

      const issues = LocalStore.get('issues');
      const today = new Date().toISOString().split('T')[0];
      const dueDate = formatDateOffset(BORROWING_PERIOD_DAYS);

      const newIssue = {
        id: Date.now(),
        bookId: book.id,
        bookTitle: book.title,
        userId: user.id,
        userName: user.name,
        userEmail: user.email,
        issueDate: today,
        dueDate: dueDate,
        returnDate: null,
        status: 'ISSUED',
        fineAmount: 0.0,
        finePaid: false
      };

      issues.unshift(newIssue);
      LocalStore.set('issues', issues);
      return newIssue;
    });
  },

  async returnBook(issueId) {
    return this.fetchWithFallback(`/issues/${issueId}/return`, {
      method: 'POST'
    }, () => {
      const issues = LocalStore.get('issues');
      const issue = issues.find(i => i.id === Number(issueId));
      if (!issue) throw new Error('Issue record not found');
      if (issue.status === 'RETURNED') throw new Error('Book is already marked as returned');

      const todayStr = new Date().toISOString().split('T')[0];
      issue.returnDate = todayStr;

      // Calculate fine if overdue
      const today = new Date();
      today.setHours(0,0,0,0);
      const due = new Date(issue.dueDate);
      due.setHours(0,0,0,0);
      const diffTime = today - due;
      const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));

      if (diffDays > 0) {
        issue.fineAmount = diffDays * FINE_RATE_PER_DAY;
      }

      issue.status = 'RETURNED';

      // Increment available quantity
      const books = LocalStore.get('books');
      const book = books.find(b => b.id === issue.bookId);
      if (book) {
        book.availableQuantity = Math.min(book.quantity, book.availableQuantity + 1);
        LocalStore.set('books', books);
      }

      LocalStore.set('issues', issues);
      return issue;
    });
  },

  async payFine(issueId) {
    return this.fetchWithFallback(`/issues/${issueId}/pay-fine`, {
      method: 'POST'
    }, () => {
      const issues = LocalStore.get('issues');
      const issue = issues.find(i => i.id === Number(issueId));
      if (!issue) throw new Error('Record not found');
      issue.finePaid = true;
      LocalStore.set('issues', issues);
      return issue;
    });
  },

  async reserveBook(bookId, user) {
    return this.fetchWithFallback('/reservations', {
      method: 'POST',
      body: JSON.stringify({ bookId, userId: user.id })
    }, () => {
      const books = LocalStore.get('books');
      const book = books.find(b => b.id === Number(bookId));
      if (!book) throw new Error('Book not found');

      const reservations = LocalStore.get('reservations');
      const alreadyReserved = reservations.some(r => r.bookId === book.id && r.userId === user.id && r.status === 'PENDING');
      if (alreadyReserved) {
        throw new Error('You already have an active advance booking for this book.');
      }

      const newRes = {
        id: Date.now(),
        bookId: book.id,
        bookTitle: book.title,
        userId: user.id,
        userName: user.name,
        userEmail: user.email,
        reservationDate: new Date().toISOString().split('T')[0],
        status: 'PENDING'
      };

      reservations.unshift(newRes);
      LocalStore.set('reservations', reservations);
      return newRes;
    });
  },

  async getIssues() {
    LocalStore.calculateFines();
    return this.fetchWithFallback('/issues', {}, () => LocalStore.get('issues'));
  },

  async getReservations() {
    return this.fetchWithFallback('/reservations', {}, () => LocalStore.get('reservations'));
  },

  async getUsers() {
    return this.fetchWithFallback('/users', {}, () => LocalStore.get('users'));
  },

  async getQueries() {
    return this.fetchWithFallback('/queries', {}, () => LocalStore.get('queries'));
  },

  async submitQuery(queryData) {
    return this.fetchWithFallback('/queries', {
      method: 'POST',
      body: JSON.stringify(queryData)
    }, () => {
      const queries = LocalStore.get('queries');
      const newQuery = {
        id: Date.now(),
        ...queryData,
        status: 'NEW',
        date: new Date().toISOString().split('T')[0]
      };
      queries.unshift(newQuery);
      LocalStore.set('queries', queries);
      return newQuery;
    });
  },

  async resolveQuery(queryId) {
    return this.fetchWithFallback(`/queries/${queryId}/resolve`, {
      method: 'PUT'
    }, () => {
      const queries = LocalStore.get('queries');
      const q = queries.find(item => item.id === Number(queryId));
      if (q) {
        q.status = 'RESOLVED';
        LocalStore.set('queries', queries);
        return q;
      }
      throw new Error('Query not found');
    });
  },

  async login(email, password) {
    return this.fetchWithFallback('/auth/login', {
      method: 'POST',
      body: JSON.stringify({ email, password })
    }, () => {
      const users = LocalStore.get('users');
      const user = users.find(u => u.email.toLowerCase() === email.toLowerCase() && u.password === password);
      if (!user) throw new Error('Invalid email or password');
      return user;
    });
  },

  async register(name, email, password, phone, role = 'USER') {
    return this.fetchWithFallback('/auth/register', {
      method: 'POST',
      body: JSON.stringify({ name, email, password, phone, role })
    }, () => {
      const users = LocalStore.get('users');
      if (users.some(u => u.email.toLowerCase() === email.toLowerCase())) {
        throw new Error('Email is already registered');
      }
      const newUser = {
        id: Date.now(),
        name,
        email,
        password,
        phone: phone || '',
        role: role.toUpperCase(),
        joinedDate: new Date().toISOString().split('T')[0]
      };
      users.push(newUser);
      LocalStore.set('users', users);
      return newUser;
    });
  }
};

// UI Toast Notification Helper
function showToast(message, type = 'info') {
  let container = document.querySelector('.toast-container');
  if (!container) {
    container = document.createElement('div');
    container.className = 'toast-container';
    document.body.appendChild(container);
  }

  const toast = document.createElement('div');
  toast.className = `toast ${type}`;
  toast.innerHTML = `
    <span>${type === 'success' ? '✓' : type === 'danger' ? '✕' : 'ℹ'}</span>
    <span>${message}</span>
  `;

  container.appendChild(toast);
  setTimeout(() => {
    toast.style.opacity = '0';
    toast.style.transform = 'translateY(10px)';
    setTimeout(() => toast.remove(), 250);
  }, 3500);
}

// Global Nav Bar Updater
function updateNav() {
  const user = Auth.getCurrentUser();
  const navActions = document.getElementById('navActions');
  const userGreeting = document.getElementById('userGreeting');

  if (navActions) {
    if (user) {
      navActions.innerHTML = `
        <div class="user-pill">
          <div class="user-avatar">${user.name.charAt(0).toUpperCase()}</div>
          <span>${user.name}</span>
          <span class="badge ${user.role === 'ADMIN' ? 'badge-danger' : 'badge-info'}" style="font-size: 0.65rem;">${user.role}</span>
        </div>
        ${user.role === 'ADMIN' ? '<a href="admin.html" class="btn btn-secondary btn-sm">Admin Panel</a>' : '<a href="user.html" class="btn btn-secondary btn-sm">My Dashboard</a>'}
        <button onclick="Auth.logout()" class="btn btn-secondary btn-sm">Logout</button>
      `;
    } else {
      navActions.innerHTML = `
        <a href="login.html" class="btn btn-primary btn-sm">Login / Register</a>
      `;
    }
  }
}

// Tab Switching Helper
function initTabs() {
  document.querySelectorAll('.tab-btn').forEach(btn => {
    btn.addEventListener('click', () => {
      const target = btn.dataset.tab;
      const nav = btn.closest('.tab-nav');
      nav.querySelectorAll('.tab-btn').forEach(b => b.classList.remove('active'));
      btn.classList.add('active');

      const parent = nav.parentElement;
      parent.querySelectorAll('.tab-pane').forEach(pane => pane.classList.remove('active'));
      const targetPane = document.getElementById(target);
      if (targetPane) targetPane.classList.add('active');
    });
  });
}

// Modal Toggle Helpers
function openModal(id) {
  const modal = document.getElementById(id);
  if (modal) modal.classList.add('active');
}

function closeModal(id) {
  const modal = document.getElementById(id);
  if (modal) modal.classList.remove('active');
}

// Initialize on DOM Ready
document.addEventListener('DOMContentLoaded', () => {
  LocalStore.init();
  updateNav();
  initTabs();
});
