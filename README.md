# Distributed Complaint Management System

A Java RMI-based distributed system that allows multiple clients to submit and view complaints through a centralized server. Supports both a command-line interface and a Swing GUI client.

---

## Architecture

```
Client (CLI / GUI)
       │
       │  Java RMI (port 1099)
       ▼
    Server
       │
  ComplaintServiceImpl
       │
  ComplaintManager (Thread-safe, Read-Write Lock)
       │
  RequestHandler (Thread Pool – 5 threads)
```

**Key Components:**

| File | Role |
|---|---|
| `Server.java` | Starts RMI registry and binds the service |
| `ComplaintService.java` | Remote interface (contract between client & server) |
| `ComplaintServiceImpl.java` | Server-side implementation with thread pool |
| `ComplaintManager.java` | Thread-safe complaint storage using `ReentrantReadWriteLock` |
| `RequestHandler.java` | Runnable that logs and processes each request asynchronously |
| `Client.java` | Command-line client |
| `ClientGUI.java` | Swing GUI client |

---

## Prerequisites

- Java JDK 8 or higher
- No external libraries required (pure Java SE)

---

## Setup & Running

### 1. Compile all source files

```bash
cd src
javac *.java
```

### 2. Start the Server

```bash
java Server
```

Expected output:
```
================================================
  DISTRIBUTED COMPLAINT MANAGEMENT SYSTEM
  Server Component
================================================

[✓] Complaint Service created
[✓] RMI Registry created on port 1099
[✓] Complaint Service bound to RMI Registry

Server is ready to accept client connections
Press Ctrl+C to stop the server
```

### 3. Run a Client (in a separate terminal)

**CLI Client:**
```bash
java Client
# Or pass User ID directly:
java Client Alice
```

**GUI Client:**
```bash
java ClientGUI
```

---

## Features

- **Submit Complaint** — Clients submit complaints with a user ID; each gets a unique auto-incremented ID and timestamp.
- **View All Complaints** — Retrieves the full complaint list from the server.
- **Complaint Count** — Returns the total number of complaints logged.
- **Server Status** — Shows active thread count, total complaints, and total requests processed.

---

## Concurrency Design

The system uses two concurrency mechanisms:

**Read-Write Lock (`ReentrantReadWriteLock`) in `ComplaintManager`:**
- Multiple clients can *read* simultaneously (view, count).
- Write operations (submit) acquire an exclusive lock.
- This maximizes throughput for read-heavy workloads.

**Thread Pool (`ExecutorService`) in `ComplaintServiceImpl`:**
- A fixed pool of 5 threads handles incoming requests asynchronously via `RequestHandler`.
- Prevents server overload under concurrent client load.

---

## Sample Complaint Entry Format

```
ID: 1001 | User: Alice | Time: 2025-05-01 14:32:10 | Complaint: Internet is slow | Thread: pool-1-thread-2
```

---

## Notes

- Server must be running on `localhost:1099` before any client connects.
- If the RMI registry is already running on port 1099, the server will reuse it automatically.
- The GUI client generates a random User ID on startup (e.g., `USER_4823`) which can be changed before connecting.
- To stop the server gracefully, press `Ctrl+C`.
