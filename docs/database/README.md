# Database overview

## Database Entity Flow

```mermaid
erDiagram
    USER {
        int id PK
        string email UK
        string username
        string password
        int role
        enum status
        datetime created_at
        datetime updated_at
    }

    MOVIE {
        int id PK
        string title 
        string description
        datetime created_at
        datetime updated_at
    }

    THEATER {
        int id PK
        string name
        datetime created_at
        datetime updated_at
    }

    ROOM {
        int id PK
        int theater_id FK
        string name
        datetime created_at
        datetime updated_at
    }

    SEATS {
        int id PK
        int theater_id FK, UK
        int room_id FK, UK 
        int row_number UK
        int column_number UK
        int seat_type
        datetime created_at
        datetime updated_at
    }
  
    SCREENING {
        int id PK
        int theater_id UK, FK
        int movie_id UK, FK
        int room_id UK, FK
        int time UK
        datetime created_at
        datetime updated_at
    }

    RESERVATIONSEATS {
        int id PK
        int screening_id FK, UK
        int seats_id FK, UK
        int status
        datetime created_at
        datetime updated_at
    }

    PAYMENT {
        int id PK
        int user_id FK, UK
        int reservation_seats_id FK, UK
        int status
        datetime created_at
        datetime updated_at
    }

    RESERVATIONSEATS || --o{ SCREENING : "contains"
    RESERVATIONSEATS || --o{ SEATS : "contains"

    SEATS || --o{ ROOM : "contains"
    ROOM || --o{ THEATER: "contains"

    SCREENING || --o{ ROOM: "contains"
    SCREENING || --o{ MOVIE: "contains"

    PAYMENT || --o{ RESERVATIONSEATS: "contains"
```