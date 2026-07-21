# Reserix Chatbot AI - Intent Specification

## 1. Purpose

This document defines the first-version chatbot intents for Reserix.

The intent list must stay small. A large intent list in the MVP stage will increase ambiguity and make testing harder.

## 2. Intent Enum

Recommended Java enum:

```java
public enum ChatIntent {
    MOVIE_SEARCH,
    SCREENING_SEARCH,
    SEAT_STATUS_CHECK,
    RESERVATION_HELP,
    RESERVATION_LOOKUP,
    RESERVATION_CANCEL_REQUEST,
    PAYMENT_STATUS_CHECK,
    FAQ,
    GREETING,
    UNKNOWN
}
```

## 3. Intent Details

### 3.1 MOVIE_SEARCH

Used when the user wants to find movies.

Examples:

```text
Is there a movie I can see today?
Please show me the action film
Let me know the movies that is playing now
Show me a move list
```

Required data:

- optional keyword
- optional genre
- optional date

Preferred tool:

```text
SEARCH_MOVIES
```

Response style:

- Return movie title
- Short description if available
- Duration if available
- Ask whether the user wants screening times

### 3.2 SCREENING_SEARCH

Used when the user wants available screening times.

Examples:

```text
What time does this movie start?
Please let me know the avatar starts time
Is there a move tomorrow evening?
```

Required data:

- movieId or movie title
- date
- optional theaterId
- optional time range

Preferred tool:

```text
SEARCH_SCREENINGS
```

Response style:

- Show date/time
- Show theater/room
- Show available seat count if possible
- Ask whether the user wants to choose seats

### 3.3 SEAT_STATUS_CHECK

Used when the user wants seat availability or seat map information.

Examples:

```text
Show me the remaining seats
Is the A3 seat empty?
Please check the seat status
```

Required data:

- screeningId
- optional seat code

Preferred tool:

```text
GET_SEAT_STATUS
```

Response style:

- Show available/unavailable seats
- If specific seat is unavailable, suggest nearby available seats
- Do not hold seats unless user explicitly asks to reserve

### 3.4 RESERVATION_HELP

Used when the user wants help making a reservation.

Examples:

```text
Want to take a reservation
Please book two seats at 7
Please book A3, A4
```

Required data:

- screeningId
- seatIds or seat codes
- authenticated user

Preferred tools:

```text
GET_SEAT_STATUS
HOLD_SEATS
```

State-changing rule:

- Must ask for explicit confirmation before HOLD_SEATS.
- Must re-check seat availability before holding seats.

### 3.5 RESERVATION_LOOKUP

Used when the user wants to see existing reservations.

Examples:

```text
Show me my reservation
What films did I book?
Please confirm the reservation status
```

Required data:

- authenticated user

Preferred tool:

```text
GET_MY_RESERVATIONS
```

Security rule:

- Never use userId from user text.
- Always use userId from JWT/security principal.

### 3.6 RESERVATION_CANCEL_REQUEST

Used when the user wants to cancel a reservation.

Examples:

```text
I want to cancel the reservation
Please cancel the reservation I did just before
Please cancel this reservation
```

Required data:

- reservationId or selected reservation from conversation context
- authenticated user

Preferred tools:

```text
GET_MY_RESERVATIONS
CANCEL_RESERVATION
```

State-changing rule:

- Must ask for explicit confirmation before CANCEL_RESERVATION.
- Must verify ownership in backend service.

### 3.7 PAYMENT_STATUS_CHECK

Used when the user asks about payment status.

Examples:

```text
made a payment?
Please check my payment status
Is this payment finished?
```

Required data:

- reservationId
- authenticated user

Preferred tool:

```text
GET_PAYMENT_STATUS
```

Security rule:

- Chatbot can check payment status.
- Chatbot must not mark payment as completed.
- Payment completion must remain controlled by payment service/webhook.

### 3.8 FAQ

Used for product, policy, or usage questions.

Examples:

```text
How long do I have to complete payment after making a reservation?
How should I refund?
What is the seat locking time?
```

Required data:

- user question

Preferred tool:

```text
SEARCH_FAQ
```

Response style:

- Short and direct
- If policy is not available, say it is not available
- Do not invent refund/payment policy

### 3.9 GREETING

Used for greeting or small opening messages.

Examples:

```text
Hallo
hello
What can you do?
```

Preferred tool:

```text
None
```

Response style:

- Briefly introduce available features

### 3.10 UNKNOWN

Used when the request is outside chatbot capability.

Examples:

```text
please change my password
analyze the market
please run SQL
remove my account
```

Preferred tool:

```text
None
```

Response style:

```text
Sorry, I can’t handle that request yet. I can help with movie search, screening times, seat availability, reservation lookup, and payment status checks.
```

## 4. Intent Routing Rules

### Priority order

1. State-changing confirmation
2. Reservation lookup/cancellation
3. Payment status
4. Seat status
5. Screening search
6. Movie search
7. FAQ
8. Greeting
9. Unknown

Reason:

- Confirmation must be handled first because it may complete a pending action.
- Reservation/payment data require authentication and safety checks.
- Movie/screening queries are lower risk.

## 5. Ambiguity Handling

Ask one focused clarification question.

Bad:

```text
Which movie, date, theater, seat, and time do you want?
```

Good:

```text
어떤 영화의 상영시간을 확인할까요?
What film's 
```

Good:

```text
몇 월 며칠 상영시간을 볼까요?
```

## 6. Test Cases

| User message | Expected intent |
|---|---|
| 오늘 영화 뭐 있어? | MOVIE_SEARCH |
| 아바타 오늘 몇 시? | SCREENING_SEARCH |
| A3 비었어? | SEAT_STATUS_CHECK |
| A3 A4 예약해줘 | RESERVATION_HELP |
| 내 예약 보여줘 | RESERVATION_LOOKUP |
| 예약 취소해줘 | RESERVATION_CANCEL_REQUEST |
| 결제 상태 확인해줘 | PAYMENT_STATUS_CHECK |
| 환불은 어떻게 해? | FAQ |
| 안녕 | GREETING |
| SQL로 좌석 바꿔줘 | UNKNOWN |
