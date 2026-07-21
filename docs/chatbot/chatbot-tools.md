# Reserix Chatbot AI - Tool Specification

## 1. Purpose

This document defines the backend tools that the chatbot can use.

Tools are controlled wrappers around existing Reserix services. They are not direct database access methods.

## 2. Tool Execution Rule

Every tool must follow these rules:

1. Validate input.
2. Use existing service layer.
3. Enforce authorization in backend.
4. Return structured result.
5. Log tool call and result status.
6. Never expose sensitive internal data to the user.
7. Never allow the LLM to decide user identity.

## 3. Tool Name Enum

Recommended Java enum:

```java
public enum ChatToolName {
    SEARCH_MOVIES,
    SEARCH_SCREENINGS,
    GET_SEAT_STATUS,
    GET_MY_RESERVATIONS,
    GET_PAYMENT_STATUS,
    SEARCH_FAQ,
    HOLD_SEATS,
    CANCEL_RESERVATION
}
```

## 4. Read-only Tools

Read-only tools should be implemented first.

### 4.1 SEARCH_MOVIES

Purpose:

Find movies by keyword, genre, or date.

Input:

```json
{
  "keyword": "avatar",
  "genre": "ACTION",
  "date": "2026-07-15"
}
```

Output:

```json
{
  "movies": [
    {
      "movieId": 1,
      "title": "Avatar",
      "durationMinutes": 160,
      "description": "..."
    }
  ]
}
```

Service dependency:

```text
MovieService
```

Safety:

- If no result, return empty list.
- Do not invent movies.

### 4.2 SEARCH_SCREENINGS

Purpose:

Find screenings for a movie/date/theater.

Input:

```json
{
  "movieId": 1,
  "movieTitle": "Avatar",
  "date": "2026-07-15",
  "theaterId": 2,
  "timeRange": "EVENING"
}
```

Output:

```json
{
  "screenings": [
    {
      "screeningId": 10,
      "movieTitle": "Avatar",
      "theaterName": "Central Cinema",
      "roomName": "Room 1",
      "startTime": "2026-07-15T19:30:00",
      "endTime": "2026-07-15T22:00:00",
      "availableSeatCount": 42
    }
  ]
}
```

Service dependency:

```text
ScreeningService
SeatService or ReservationSeatRepository for availability summary
```

Safety:

- Do not expose internal seat lock keys.
- Do not create reservations from this tool.

### 4.3 GET_SEAT_STATUS

Purpose:

Return seat availability for a selected screening.

Input:

```json
{
  "screeningId": 10,
  "seatCodes": ["A3", "A4"]
}
```

Output:

```json
{
  "screeningId": 10,
  "seats": [
    {
      "seatId": 100,
      "code": "A3",
      "status": "AVAILABLE",
      "seatType": "STANDARD",
      "price": 12000
    },
    {
      "seatId": 101,
      "code": "A4",
      "status": "PENDING",
      "seatType": "STANDARD",
      "price": 12000
    }
  ]
}
```

Service dependency:

```text
SeatService
ReservationService
Redis lock service if current lock status is needed
```

Safety:

- Must reflect current reservation/lock status.
- Must not hold seats.

### 4.4 GET_MY_RESERVATIONS

Purpose:

Return reservations of authenticated user.

Input:

```json
{
  "status": "PENDING"
}
```

Server-side context:

```text
authenticatedUserId
```

Output:

```json
{
  "reservations": [
    {
      "reservationId": 30,
      "movieTitle": "Avatar",
      "startTime": "2026-07-15T19:30:00",
      "seats": ["A3", "A4"],
      "status": "PENDING",
      "expireAt": "2026-07-15T19:40:00"
    }
  ]
}
```

Service dependency:

```text
ReservationService
```

Safety:

- Do not accept userId from model/user input.
- Always use authenticated user context.

### 4.5 GET_PAYMENT_STATUS

Purpose:

Return payment status for a reservation.

Input:

```json
{
  "reservationId": 30
}
```

Server-side context:

```text
authenticatedUserId
```

Output:

```json
{
  "reservationId": 30,
  "reservationStatus": "PENDING",
  "paymentStatus": "READY",
  "amount": 24000
}
```

Service dependency:

```text
PaymentService
ReservationService
```

Safety:

- Must verify reservation ownership.
- Must not mark payment as paid.
- Must not trigger payment confirmation.

### 4.6 SEARCH_FAQ

Purpose:

Answer policy or usage questions from approved FAQ content.

Input:

```json
{
  "query": "How long is a seat held before payment?"
}
```

Output:

```json
{
  "matches": [
    {
      "question": "How long is a seat held before payment?",
      "answer": "A pending reservation expires after 5 minutes unless payment is completed.",
      "source": "reservation-policy"
    }
  ]
}
```

Service dependency:

```text
ChatFaqService
```

Safety:

- Use only approved FAQ content.
- If no answer exists, say that policy is not available.

## 5. State-changing Tools

State-changing tools must not be called without explicit confirmation.

### 5.1 HOLD_SEATS

Purpose:

Create a pending reservation or seat hold using existing reservation flow.

Input:

```json
{
  "screeningId": 10,
  "seatIds": [100, 101]
}
```

Server-side context:

```text
authenticatedUserId
```

Output:

```json
{
  "reservationId": 30,
  "status": "PENDING",
  "expireAt": "2026-07-15T19:40:00",
  "seats": ["A3", "A4"]
}
```

Service dependency:

```text
ReservationService.createReservation(...)
Redis lock service
```

Safety:

- Must re-check seat availability.
- Must use existing reservation creation logic.
- Must use authenticated userId.
- Must fail if seats are already locked/reserved.
- Must not confirm reservation.
- Must not execute payment.

Confirmation required:

```text
YES
```

### 5.2 CANCEL_RESERVATION

Purpose:

Cancel a user's reservation.

Input:

```json
{
  "reservationId": 30
}
```

Server-side context:

```text
authenticatedUserId
```

Output:

```json
{
  "reservationId": 30,
  "status": "CANCELED"
}
```

Service dependency:

```text
ReservationService.cancelReservation(...)
```

Safety:

- Must verify reservation ownership.
- Must check cancelable status.
- Must not cancel another user's reservation.
- Must release related seat status through existing logic.

Confirmation required:

```text
YES
```

## 6. Pending Action Model

When the chatbot proposes a state-changing action, store a pending action in conversation context.

Recommended structure:

```json
{
  "type": "HOLD_SEATS",
  "screeningId": 10,
  "seatIds": [100, 101],
  "summary": "Avatar, 2026-07-15 19:30, A3/A4",
  "createdAt": "2026-07-15T18:59:00",
  "expiresAt": "2026-07-15T19:01:00"
}
```

A pending action should expire quickly, for example after 2 minutes.

## 7. Tool Result Status

Recommended status values:

```java
public enum ChatToolResultStatus {
    SUCCESS,
    EMPTY,
    VALIDATION_FAILED,
    AUTH_REQUIRED,
    FORBIDDEN,
    CONFLICT,
    ERROR
}
```

## 8. Audit Logging

Each tool call should be logged.

Minimum fields:

```text
session_id
message_id
tool_name
input_json
result_status
result_json
created_at
```

For security-sensitive tools, log:

```text
authenticated_user_id
reservation_id
screening_id
seat_ids
```

## 9. Implementation Priority

Implement in this order:

1. SEARCH_FAQ
2. SEARCH_MOVIES
3. SEARCH_SCREENINGS
4. GET_SEAT_STATUS
5. GET_MY_RESERVATIONS
6. GET_PAYMENT_STATUS
7. HOLD_SEATS
8. CANCEL_RESERVATION

Reason:

- Start with safe read-only tools.
- Add reservation-changing tools only after confirmation flow is implemented.
