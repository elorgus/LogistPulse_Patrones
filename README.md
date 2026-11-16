# LogistPulse — Smart Inventory Management System

A Spring Boot backend service demonstrating behavioral design patterns (**Command**, **Chain of Responsibility**, **Strategy**, **State**, and **Observer**) to process, validate, allocate, track, and audit warehouse inventory reservations.

---

## Architecture & Pipeline Flow

Every incoming reservation request (`POST /reservations`) traverses a decoupled, event-driven pipeline:

```text
Client ──▶ ReservationController (Invoker)
                  │
                  ▼
           DecisionLedger (Pre-Execution Audit)
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
           ├──▶ PickingContext (Strategy: FEFO / FIFO / LIFO)
           ├──▶ BatchContext (State: Almacenado ──▶ Reservado)
           └──▶ ApplicationEventPublisher (Observer: StockChangedEvent)
                  ├──▶ ReplenishmentObserver (ROP / EOQ Alerts)
                  ├──▶ PulseFleetObserver (AMR Dispatch & 3D Twin)
                  └──▶ LedgerStockObserver (Post-Execution Audit)
```

---

## Implemented Design Patterns

### 1. Command Pattern (`command/`)
* **Role:** Encapsulates inventory operations as standalone, auditable objects.
* **Key Classes:** `Command`, `ReserveStockCommand`, `ReservationController` (Invoker), `DecisionLedger`, and `InventoryReceiver` (Receiver).

### 2. Chain of Responsibility (`chain/`)
* **Role:** Enforces sequential business guardrails before modifying inventory. If any link fails, execution halts immediately with a `400 Bad Request` and a clear rejection reason.
* **Sequence:** `SkuHandler` → `StockHandler` → `LoteHandler` (expired/quarantined check) → `CapacidadHandler`.

### 3. Strategy Pattern (`strategy/`)
* **Role:** Dynamically selects the optimal warehouse batch at runtime without conditional `if/else` chains using Spring's named bean map injection.
* **Policies:**
  * **`FEFO`**: First-Expired, First-Out
  * **`FIFO`**: First-In, First-Out
  * **`LIFO`**: Last-In, First-Out

### 4. State Pattern (`state/`)
* **Role:** Models each batch (`Lote`) as a finite-state machine enforcing valid lifecycle transitions and throwing `IllegalStateException` on invalid transitions.
* **Lifecycle:** `Recibido` → `Almacenado` → `Reservado` → `Enviado` (with `Cuarentena` as a detour from `Almacenado`).

### 5. Observer Pattern (`observer/`)
* **Role:** Uses Spring's `ApplicationEventPublisher` and `@EventListener` to broadcast `StockChangedEvent` updates to independent downstream subscribers without coupling them to `InventoryReceiver`.
* **Subscribers:**
  * **`ReplenishmentObserver`**: Monitors reorder points (`ROP`) and triggers `EOQ` alerts.
  * **`PulseFleetObserver`**: Dispatches Autonomous Mobile Robots (`AMR`) and updates the 3D digital twin.
  * **`LedgerStockObserver`**: Logs post-execution inventory balance changes.

---

## Project Structure

```text
logistpulse/
├── pom.xml
├── README.md
└── src/main/java/com/usfq/logistpulse/
    ├── LogistPulseApplication.java
    ├── command/
    │   ├── Command.java
    │   ├── DecisionLedger.java
    │   ├── InventoryReceiver.java
    │   ├── ReservationController.java
    │   └── ReserveStockCommand.java
    ├── chain/
    │   ├── Handlers.java
    │   ├── ReservationRequest.java
    │   ├── ValidationChainService.java
    │   ├── ValidationException.java
    │   └── ValidationHandler.java
    ├── strategy/
    │   ├── FefoPickingPolicy.java
    │   ├── FifoPickingPolicy.java
    │   ├── InventoryBatch.java
    │   ├── LifoPickingPolicy.java
    │   ├── PickingContext.java
    │   └── PickingPolicy.java
    ├── state/
    │   ├── AlmacenadoState.java
    │   ├── BatchContext.java
    │   ├── BatchLifecycleController.java
    │   ├── BatchState.java
    │   ├── CuarentenaState.java
    │   ├── EnviadoState.java
    │   ├── RecibidoState.java
    │   └── ReservadoState.java
    └── observer/
        ├── LedgerStockObserver.java
        ├── PulseFleetObserver.java
        ├── ReplenishmentObserver.java
        └── StockChangedEvent.java
```

---

## Prerequisites & Running the App

* **Java 17+**
* **Maven 3.8+**

Start the Spring Boot server on port `8080`:

```bash
./mvnw spring-boot:run
```

---

## API Usage & Testing

### 1. Reserve Stock (`POST /reservations`)
Runs the Command, validates via Chain of Responsibility, picks a batch via Strategy, transitions batch State to `RESERVADO`, and notifies all Observers:

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

### 2. Transition Batch State (`POST /batches/{batchId}/{action}`)
Available actions: `store`, `quarantine`, `reserve`, `ship`.

**Valid transition (`RESERVADO` $\rightarrow$ `ENVIADO`):**
```bash
curl -X POST http://localhost:8080/batches/LOTE-EXPIRES-SOON/ship
```

**Invalid transition (Returns `400 Bad Request`):**
```bash
curl -X POST http://localhost:8080/batches/LOTE-EXPIRES-SOON/quarantine
```

---

## Roadmap
* [x] **Pattern 1:** Command Pattern & Decision Ledger (`POST /reservations`)
* [x] **Pattern 2:** Chain of Responsibility (`SKU → Stock → Lote → Capacidad`)
* [x] **Pattern 3:** Strategy Pattern (`FEFO`, `FIFO`, `LIFO`)
* [x] **Pattern 4:** State Pattern (`Recibido → Almacenado → Reservado → Enviado`)
* [x] **Pattern 5:** Observer Pattern (`StockChangedEvent` + `@EventListener`)
* [ ] **Pattern 6:** Final Behavioral Pattern
