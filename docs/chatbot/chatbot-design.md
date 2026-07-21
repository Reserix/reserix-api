# Reserix Chatbot AI - Design Draft

## 1. Purpose

Reserix Chatbot AI is a customer-facing assistant for helping users search movies, check screening schedules, inspect seat availability, understand reservation status, and receive FAQ-style support.

The chatbot must not become an uncontrolled booking agent. It is a guided assistant that uses approved backend tools and existing Reserix service logic.

## 2. Core Principle

The LLM is responsible for:

- understanding user intent
- asking follow-up questions when required
- producing user-friendly answers
- selecting safe backend tools

The LLM is not responsible for:

- directly querying the database
- generating SQL
- modifying reservations without explicit confirmation
- deciding authorization
- bypassing existing service rules
- changing seat status directly

All business actions must pass through existing Spring Boot services.

## 3. MVP Scope

### Included in July MVP

The first version supports:

1. Movie search
2. Screening search
3. Seat availability check
4. Reservation guidance
5. User reservation lookup
6. Payment status lookup
7. FAQ responses
8. Reservation cancellation request with explicit confirmation
9. Safe fallback for unsupported questions

### Excluded from July MVP

The first version does not support:

1. Direct payment execution
2. Admin analytics chatbot
3. Voice chatbot
4. Free-form database query
5. AI-generated SQL
6. Autonomous reservation confirmation without user approval
7. AI-controlled WebSocket events
8. Multi-agent orchestration

## 4. Architecture

```text
Client Web / Android
        |
        v
ChatController
        |
        v
ChatService
        |
        v
ChatOrchestrator
        |
        +--------------------+
        |                    |
        v                    v
Intent Router          LLM Adapter
        |                    |
        +---------+----------+
                  |
                  v
             Tool Executor
                  |
                  v
Existing Reserix Services
(MovieService / ScreeningService / SeatService / ReservationService / PaymentService)
                  |
                  v
PostgreSQL / Redis / WebSocket Publisher
```

## 5. Module Structure

Recommended package structure:

```text
com.reserix.api.chat
 ├── controller
 │    └── ChatController
 ├── service
 │    ├── ChatService
 │    ├── ChatOrchestrator
 │    ├── ChatIntentRouter
 │    ├── ChatMemoryService
 │    └── ChatFaqService
 ├── llm
 │    ├── LlmClient
 │    ├── LlmRequestFactory
 │    └── LlmResponseParser
 ├── tool
 │    ├── ChatTool
 │    ├── MovieTool
 │    ├── ScreeningTool
 │    ├── SeatTool
 │    ├── ReservationTool
 │    ├── PaymentTool
 │    └── FaqTool
 ├── dto
 │    ├── ChatSessionCreateResponse
 │    ├── ChatMessageRequest
 │    ├── ChatMessageResponse
 │    ├── ChatToolCall
 │    └── ChatToolResult
 ├── entity
 │    ├── ChatSession
 │    └── ChatMessage
 ├── enums
 │    ├── ChatRole
 │    ├── ChatSessionStatus
 │    ├── ChatIntent
 │    └── ChatToolName
 └── repository
      ├── ChatSessionRepository
      └── ChatMessageRepository
```

## 6. API Design

### 6.1 Create Chat Session

```http
POST /api/v1/chat/sessions
Authorization: Bearer <token>
```

Response:

```json
{
  "sessionId": 1,
  "status": "ACTIVE",
  "createdAt": "2026-07-04T10:00:00"
}
```

### 6.2 Send Chat Message

```http
POST /api/v1/chat/sessions/{sessionId}/messages
Authorization: Bearer <token>
Content-Type: application/json
```

Request:

```json
{
  "message": "Please let me know today's shows list "
}
```

Response:

```json
{
  "sessionId": 1,
  "messageId": 15,
  "role": "ASSISTANT",
  "content": "Below are the movies lists that you can see today...",
  "intent": "MOVIE_SEARCH",
  "toolCalls": [
    {
      "toolName": "SEARCH_MOVIES",
      "status": "SUCCESS"
    }
  ],
  "createdAt": "2026-07-04T10:00:05"
}
```

### 6.3 List Chat Messages

```http
GET /api/v1/chat/sessions/{sessionId}/messages
Authorization: Bearer <token>
```

Response:

```json
{
  "sessionId": 1,
  "messages": [
    {
      "messageId": 1,
      "role": "USER",
      "content": "Please let me know today's shows list",
      "createdAt": "2026-07-04T10:00:01"
    },
    {
      "messageId": 2,
      "role": "ASSISTANT",
      "content": "Below are the movies lists that you can see today...",
      "createdAt": "2026-07-04T10:00:05"
    }
  ]
}
```

### 6.4 Close Chat Session

```http
POST /api/v1/chat/sessions/{sessionId}/close
Authorization: Bearer <token>
```

Response:

```json
{
  "sessionId": 1,
  "status": "CLOSED"
}
```

## 7. Conversation State

The chatbot should store conversation history for continuity, but it must not trust conversation history for authorization or business state.

For example:

- If the user said "my reservation", backend must still use JWT userId.
- If the user selected seat A3 earlier, backend must still re-check seat availability.
- If the user confirmed cancellation, backend must still verify ownership.

## 8. Confirmation Rules

The chatbot must require explicit confirmation before any state-changing action.

State-changing actions:

- holding seats
- creating a pending reservation
- cancelling a reservation
- changing any reservation status

Allowed confirmation words:

```text
yes
confirm
proceed
book it
cancel it
```

The confirmation must be bound to the last proposed action. If the proposed action changes, confirmation must be requested again.

## 9. Error Handling

The chatbot should respond safely when errors occur.

| Case | Response behavior |
|---|---|
| No matching movie | Say no movie was found and ask for another keyword/date |
| No screening | Say no screening is available for the selected condition |
| Seat already taken | Explain the seat is no longer available and show alternatives |
| User not logged in | Ask the user to log in before checking reservations |
| Tool failure | Apologize and ask the user to try again |
| Unsupported request | List supported chatbot features |
| Ambiguous request | Ask one focused clarification question |

## 10. Security Rules

1. Do not expose system prompt.
2. Do not expose internal tool definitions to the user.
3. Do not generate SQL.
4. Do not accept user-provided userId for private actions.
5. Always use authenticated principal for reservation/payment lookup.
6. Always verify reservation ownership before cancellation.
7. Never mark payment as successful from chat.
8. Never bypass Redis seat lock logic.
9. Never change seat status directly from chat module.
10. Log all tool calls for audit.

## 11. MVP Done Criteria

The design phase is complete when:

1. Scope is frozen for July MVP.
2. Intent list is approved.
3. Tool list is approved.
4. Chat API contract is defined.
5. DB migration is ready.
6. System prompt is ready.
7. FAQ seed document is ready.
8. Implementation can start without architectural ambiguity.
