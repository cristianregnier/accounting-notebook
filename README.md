# Accounting Notebook

A small REST API for keeping a single account ledger: check the current balance, credit or debit it, and browse the resulting transaction history. It's built with Spring Boot and keeps everything in memory, so it's best thought of as a minimal, well-tested model of double-entry-style balance tracking rather than a production ledger.

## What it does

The service manages exactly one account. Every credit or debit is validated, applied atomically, and recorded as an immutable `Transaction` with a generated id and timestamp. The account can never go negative — a debit that would overdraw the balance is rejected instead of applied.

### Example

Check the balance (starts at `0`):

```
GET /
→ 0
```

Credit the account:

```
POST /transactions
Content-Type: application/json

{ "type": "credit", "amount": 100 }

→ 201-style body:
{
  "id": "3f1b2c4a-...-...",
  "type": "credit",
  "amount": 100,
  "effectiveDate": "2026-07-28T12:00:00.000+00:00"
}
```

Try to overdraw it:

```
POST /transactions
{ "type": "debit", "amount": 500 }

→ 409 Conflict
{
  "timestamp": "...",
  "status": 409,
  "error": "Conflict",
  "message": "Insufficient founds. Account Balance $100."
}
```

## API

| Method | Path                 | Description                                   | Success response          |
|--------|----------------------|------------------------------------------------|----------------------------|
| GET    | `/`                  | Current account balance                        | `200` — `BigDecimal`       |
| GET    | `/transactions`      | Full transaction history, in commit order      | `200` — `Transaction[]`    |
| POST   | `/transactions`      | Commit a credit or debit                       | `200` — created `Transaction` |
| GET    | `/transactions/{id}` | Look up a single transaction by id             | `200` — `Transaction`      |

`POST /transactions` body (`CommitTransactionCommand`):

```json
{ "type": "credit | debit", "amount": 10.50 }
```

Interactive API docs (Swagger UI, via SpringFox) are served at `/swagger-ui.html` while the app is running.

### Validation & edge cases

Business rules live on the `Account` and `Transaction` domain objects, not the controllers, so they hold regardless of entry point:

| Rule | Enforced in | Result if violated |
|---|---|---|
| `type` and `amount` are required on a commit request | `CommitTransactionCommand` | `400 Bad Request` |
| Credit/debit `amount` must be non-null | `Account.credit` / `Account.debit` | `400 Bad Request` |
| Credit `amount` must be strictly greater than zero | `Account.credit` | `400 Bad Request` |
| Debit `amount` must be strictly greater than zero | `Account.debit` | `400 Bad Request` |
| A debit may not bring the balance below zero | `Account.updateBalance` | `409 Conflict` ("Insufficient founds...") |
| Initial account balance must be non-null and non-negative | `Account` constructor | `400 Bad Request` (only relevant if an `Account` is constructed with a starting balance) |
| Looking up an unknown transaction id | `AccountManager.findTransactionById` | `404 Not Found` |
| `Transaction` fields (`id`, `type`, `amount`, `effectiveDate`) are all required | `Transaction` constructor | `400 Bad Request` |

Errors are normalized by a single `@ControllerAdvice` (`RestExceptionHandler`) into a consistent JSON body:

```json
{ "timestamp": "...", "status": 400, "error": "Bad Request", "message": "..." }
```

`IllegalArgumentException` → `400`, `IllegalStateException` → `409`, `NotFoundException` → `404`.

## Architecture

The code follows a conventional layered Spring Boot structure, with domain invariants pushed down into the model layer instead of the service or controllers:

- **API layer** (`api/`) — `AccountController` and `TransactionController` are thin REST adapters; they do no validation of their own and simply delegate to the service. `CommitTransactionCommand` is a request DTO (command object) that fails fast if `type` or `amount` is missing, before the service is even invoked. `RestExceptionHandler` centralizes exception-to-HTTP-status mapping so controllers stay free of try/catch blocks.
- **Service layer** (`service/AccountManager`) — the single orchestration point for balance reads, transaction commits, and history/lookup. It guards all state access with a `java.util.concurrent.locks.ReentrantReadWriteLock`: reads (`getAccountBalance`, `getHistory`, `findTransactionById`) take the shared read lock and can run concurrently; `commitTransaction` takes the exclusive write lock, so writes are serialized and never interleave with reads.
- **Repository layer** (`repository/AccountRepository`) — a Spring-managed singleton bean holding one in-memory `Account`. It's a stand-in for persistence; there is no database.
- **Domain model** (`model/`) — `Account` is the aggregate root: it owns the balance, enforces that it can never go negative, and is the only place that mutates state (`credit`/`debit`, both funneling through a private `updateBalance`). It exposes its transaction history only as an unmodifiable copy. `Transaction` is an immutable value object (id, type, amount, effective date) validated at construction time.
- **Cross-cutting concerns** — Lombok (`@Getter`, `@Log`) removes boilerplate; SpringFox (`conf/SpringFoxConfig`) generates Swagger 2 API docs automatically from the controllers.

Because there's exactly one `Account` per `AccountRepository`, the API is deliberately single-account/single-tenant — there's no account id anywhere in the request or response shapes.

## Project structure

```
src
├── main
│   ├── java/com/cristianregnier/accountingnotebook
│   │   ├── AccountingNotebookApplication.java   # Spring Boot entry point
│   │   ├── api
│   │   │   ├── AccountController.java           # GET /
│   │   │   ├── TransactionController.java       # GET|POST /transactions, GET /transactions/{id}
│   │   │   ├── command/
│   │   │   │   └── CommitTransactionCommand.java
│   │   │   └── exception/
│   │   │       └── RestExceptionHandler.java    # @ControllerAdvice error mapping
│   │   ├── conf/
│   │   │   └── SpringFoxConfig.java              # Swagger/OpenAPI setup
│   │   ├── exception/
│   │   │   └── NotFoundException.java
│   │   ├── model/
│   │   │   ├── Account.java                      # aggregate root, invariants
│   │   │   └── Transaction.java                  # immutable value object
│   │   ├── repository/
│   │   │   └── AccountRepository.java            # in-memory single-account store
│   │   └── service/
│   │       └── AccountManager.java               # orchestration + concurrency control
│   └── resources/
│       └── application.properties
└── test
    └── java/com/cristianregnier/accountingnotebook
        ├── AccountingNotebookApplicationTests.java  # Spring context smoke test
        ├── AccountInvariantsTest.java                # Account domain invariants
        └── AccountManagerTest.java                   # service layer, with mocked repository
```

## Build, test, run

Built with **Maven** (wrapper included, no local Maven install required) and **Java 8**.

```bash
# Run the test suite
./mvnw test

# Build the jar
./mvnw clean package

# Run directly with Maven
./mvnw spring-boot:run

# ...or run the packaged jar
java -jar target/accounting-notebook-0.0.1-SNAPSHOT.jar
```

The app starts on the default Spring Boot port, `http://localhost:8080` (`application.properties` has no overrides). Swagger UI is then available at `http://localhost:8080/swagger-ui.html`.

## Known limitations

These follow directly from the current implementation:

- **Single account, no persistence.** `AccountRepository` holds exactly one `Account` in memory; all balance and history data is lost on restart, and there is no notion of multiple users or accounts.
- **No authentication or authorization.** Every endpoint is open.
- **Linear lookup by id.** `AccountManager.findTransactionById` filters the full history on every call rather than using an indexed/keyed store, which is O(n) in the number of transactions.
- **No pagination.** `GET /transactions` always returns the complete history.
- **Locking is process-local.** The `ReentrantReadWriteLock` in `AccountManager` only protects against concurrent access within a single JVM instance; it would not coordinate multiple app instances sharing state.

## Author

**Cristian Regnier**
[github.com/cristianregnier](https://github.com/cristianregnier)
