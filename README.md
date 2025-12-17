# Dragons of Mugloar – Automated Game Client

This project is a **Java / Spring Boot** application that plays the **Dragons of Mugloar** game automatically by
interacting with the public Mugloar API.

The application:

- Starts a new game
- Repeatedly selects and solves missions based on configurable strategy rules
- Buys items based on configurable strategy rules
- Tracks reputation and adapts mission selection accordingly
- Ends when no further progress is possible or lives run out

---

It is **not a UI application** — it runs from the command line and logs progress.

---

## External API

The app communicates with the **Mugloar Game API**.

### API Endpoints Used

| Purpose | Endpoint |
|------|--------|
| Start game | `POST /game/start` |
| Fetch missions | `GET /{gameId}/messages` |
| Solve mission | `POST /{gameId}/solve/{adId}` |
| Investigate reputation | `POST /{gameId}/investigate/reputation` |
| List shop items | `GET /{gameId}/shop` |
| Buy shop item | `POST /{gameId}/shop/buy/{itemId}` |

All calls are performed using Spring’s `RestClient` with retry support for server errors.

---

## How to run the application

### Prerequisites

- Java 25+
- Maven
- Internet connection

### Run from IntelliJ

1. Open the project in IntelliJ
2. Ensure Maven dependencies are downloaded
3. Run the Spring Boot application main class
4. Watch the logs for game progress

### Run from command line

```bash
mvn spring-boot:run
```

---

## Configuration

All strategy behavior is configurable via `application.yml`.

---

## Game Strategy Overview

### Mission selection strategy

- All available missions are fetched each turn
- Missions are filtered:
    - Forbidden phrases are removed
    - Reputation-based rules are applied
- Remaining missions are scored using:

```
expectedScore = reward × probabilityWeight
```
---

### Shop strategy

Each turn, the default strategy:

1. Buy a healing potion if:
    - Lives are below the emergency threshold
    - Max healing potion limit not reached
    - Enough gold is available
2. Otherwise:
    - Buy an advanced item if gold allows
    - Else buy a basic item
3. If no conditions are met, no purchase is made

The engine will keep buying items **until the strategy returns no further purchase**.

---

## Game loop behavior

A game turn proceeds as follows:

1. Investigate current reputation
2. Attempt to select and solve a mission
3. Attempt shop purchases
4. Repeat until:
    - There are no missions that can be chosen
    - No shop purchase is possible
    - No lives remaining

---

## Error handling & reliability

- Client (4xx) and server (5xx) errors are mapped to custom exceptions
- Server errors are retried automatically using Spring Retry
- Fatal errors are logged and cause graceful shutdown

---

## Testing

The project includes:

- Unit tests for strategy logic
- API client tests

---

## Summary

This application demonstrates:

- A clean, extensible game-playing strategy
- Proper handling of mutable state in a Spring singleton
- Clear separation of concerns between engine, strategy, and API client
- Configuration-driven behavior

It can easily be extended with new strategy rules, reputation logic, or scoring models.
