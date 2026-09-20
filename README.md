# Bank Management System

A Java-based bank account management system built in two versions: a **console application** and a **web application** (Servlets + JSP), both powered by the same underlying business logic.

## Features

- **Authentication** — BCrypt-hashed passwords, no plain text storage
- **Session-based authorization** — deposit/withdraw/transfer always act on the logged-in user's own account, never a user-typed ID
- **Race-condition-safe transfers** — uses `SELECT ... FOR UPDATE` row locking and explicit transaction control (`commit`/`rollback`) to guarantee atomic, consistent fund transfers
- **Transaction history** — every deposit, withdrawal, and transfer is logged and viewable per account
- **DAO design pattern** — clean separation between application logic and data access, so the same `CustomerDAO`/`TransactionDAO` layer powers both the console app and the web app

## Tech Stack

- **Java** — core application logic
- **JDBC** — database connectivity
- **MySQL** — data persistence
- **BCrypt (jBCrypt)** — password hashing
- **Servlets + JSP** — web layer (Jakarta EE / Tomcat 10.1)
- **HTML/CSS** — web UI

## Architecture

```
View (Console menu / JSP pages)
        |
Controller (Servlets, for the web version)
        |
DAO layer (CustomerDAO, TransactionDAO)
        |
DBConnection (JDBC)
        |
MySQL Database
```

The console and web versions share the exact same DAO layer — only the presentation layer differs.

## Project Structure

```
BankManagementSystem/      → Console application
  ├── app/Main.java
  ├── dao/                 → CustomerDAO, TransactionDAO + implementations
  ├── model/                → Customer, Transaction
  └── util/DBConnection.java

BankWebApp/                → Web application
  ├── app/                 → LoginServlet, DepositServlet, WithdrawServlet,
  │                          TransferServlet, TransactionServlet, LogoutServlet
  └── webapp/               → login.jsp, dashboard.jsp, deposit.jsp,
                               withdraw.jsp, transfer.jsp, transactions.jsp
```

## Setup

1. Create a MySQL database and run the schema (see `schema.sql` if included, or create `customer` and `transactions` tables matching the model classes).
2. Copy `db.properties.example` to `db.properties` in each project and fill in your own MySQL credentials.
3. Add `jbcrypt-0.4.jar` and a MySQL Connector/J JAR to each project's build path.
4. For the web app: import as a Dynamic Web Project in Eclipse, link it to `BankManagementSystem` as a project dependency, and run on Apache Tomcat 10.1+.

## Known Limitations

- Uses `double` for currency (acceptable for a learning project; `BigDecimal` would be the production-correct choice)
- No self-service signup on the web version yet (customers are currently added via the console app)
- Minimal client-side validation beyond required fields

## What This Project Demonstrates

- Secure authentication and session-based authorization
- ACID-compliant transaction handling under concurrency
- Clean layered architecture (DAO pattern) reused across two different presentation layers
- Debugging real-world deployment issues (Jakarta EE namespace migration, classpath/deployment assembly configuration)
