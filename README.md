# LogistPulse — Smart Inventory Management System

A Spring Boot backend service demonstrating behavioral design patterns (**Command** and **Chain of Responsibility**) to process, validate, and audit warehouse inventory reservations.

---

## Architecture & Design Patterns

Every incoming reservation request (`POST /reservations`) flows through an auditable, decoupled pipeline:

```text
Client ──▶ ReservationController (Invoker)
                  │
                  ▼
           DecisionLedger (Audit Log)
                  │
                  ▼
         ReserveStockCommand (Command)
                  │
                  ▼
     ValidationChainService (Chain of Responsibility)
       [ SKU ──▶ Stock ──▶ Lote ──▶ Capacidad ]
                  │
                  ▼ (If all guardrails pass)
         InventoryReceiver (Receiver)
```

### 1. Command Pattern
* **Role:** Encapsulates inventory reservation requests as standalone, auditable objects.
* **Key Classes:**
  * `ReservationController` (**Invoker**): Receives HTTP requests, builds the command, and submits it to the ledger.
  * `Command` & `ReserveStockCommand` (**Command**): Binds request parameters (`sku`, `quantity`, `batchId`, `strategy`) with execution logic.
  * `DecisionLedger` (**Audit/Queue**): Records an immutable log entry with a unique `transactionId` before executing the command.
  * `InventoryReceiver` (**Receiver**): Executes the actual stock reservation logic once validated.

### 2. Chain of Responsibility Pattern
* **Role:** Enforces sequential business guardrails before modifying inventory. If any link fails, execution halts immediately and returns a `400 Bad Request` identifying the failed step.
* **Validation Sequence:**
  1. **`SkuHandler`**: Verifies that the SKU code is present and valid.
  2. **`StockHandler`**: Ensures requested quantity is positive and does not exceed available stock (`100` units simulated).
  3. **`LoteHandler`**: Rejects batches that are expired (`isBatchExpired`) or in quarantine (`isBatchQuarantined`).
  4. **`CapacidadHandler`**: Enforces maximum zone/AMR dispatch capacity (`50` units simulated).

---

## Project Structure

```text
logistpulse/
├── pom.xml
├── README.md
└── src/
    └── main/
        ├── java/com/usfq/logistpulse/
        │   ├── LogistPulseApplication.java
        │   ├── chain/
        │   │   ├── CapacidadHandler.java
        │   │   ├── LoteHandler.java
        │   │   ├── ReservationRequest.java
        │   │   ├── SkuHandler.java
        │   │   ├── StockHandler.java
        │   │   ├── ValidationChainService.java
        │   │   ├── ValidationException.java
        │   │   └── ValidationHandler.java
        │   ├── command/
        │   │   ├── Command.java
        │   │   └── ReserveStockCommand.java
        │   ├── controller/
        │   │   └── ReservationController.java
        │   ├── ledger/
        │   │   └── DecisionLedger.java
        │   └── receiver/
        │       └── InventoryReceiver.java
        └── resources/
            └── application.properties
```

---

## Prerequisites & Running the App

* **Java 17+**
* **Maven 3.8+** (or use the included Maven wrapper `./mvnw`)

Start the Spring Boot server on port `8080`:

```bash
./mvnw spring-boot:run
```

---

## API Usage & Testing

### Endpoint: `POST /reservations`

#### Valid Reservation Request (Passes All Guardrails)
```bash
curl -X POST http://localhost:8080/reservations \
  -H "Content-Type: application/json" \
  -d '{
    "sku": "SKU-9001",
    "quantity": 15,
    "batchId": "LOTE-A1",
    "isBatchExpired": false,
    "isBatchQuarantined": false,
    "strategy": "FEFO"
  }'
```

**Response (`200 OK`):**
```json
{
  "status": "SUCCESS",
  "transactionId": "c8f1a2b4-...",
  "message": "Reserva validada y registrada en el Ledger."
}
```

#### Rejected Request Example (Fails `LoteHandler`)
```bash
curl -X POST http://localhost:8080/reservations \
  -H "Content-Type: application/json" \
  -d '{
    "sku": "SKU-9001",
    "quantity": 15,
    "batchId": "LOTE-OLD",
    "isBatchExpired": true,
    "isBatchQuarantined": false
  }'
```

**Response (`400 Bad Request`):**
```json
{
  "status": "REJECTED",
  "transactionId": "9a4e7d10-...",
  "failedLink": "Lote",
  "reason": "El lote LOTE-OLD está vencido."
}
```

---

## Roadmap
* [x] **Phase 1:** Command Pattern & Decision Ledger (`POST /reservations`)
* [x] **Phase 2:** Chain of Responsibility Guardrails (`SKU → Stock → Lote → Capacidad`)
* [ ] **Phase 3:** Strategy Pattern (`FEFO`, `FIFO`, `LIFO` batch allocation)

Access Link:
https://github.com/elorgus/LogistPulse_Patrones
