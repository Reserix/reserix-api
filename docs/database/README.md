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
        int duration_minutes
        datetime created_at
        datetime updated_at
    }

    THEATER {
        int id PK
        string name
        string address
        datetime created_at
        datetime updated_at
    }

    ROOM {
        int id PK
        int theater_id FK
        string name
        int row_count
        int column_count
        datetime created_at
        datetime updated_at
    }

    SEAT {
        int id PK
        int room_id FK 
        int row_number
        int column_number
        enum seat_type
        datetime created_at
        datetime updated_at
    }
  
    SCREENING {
        int id PK
        int movie_id FK
        int room_id FK
        datetime start_time
        datetime end_time
        enum status
        datetime created_at
        datetime updated_at
    }
    
    RESERVATION {
        int id PK
        int user_id FK
        int screening_id FK
        enum status
        datetime expire_at
        datetime created_at
        datetime updated_at
    }

    RESERVATION_SEAT {
        int id PK
        int reservation_id FK
        int screening_id FK
        int seat_id FK
        enum status
        datetime created_at
        datetime updated_at
    }

    PAYMENT {
        int id PK
        int user_id FK
        int reservation_id FK
        enum status
        datetime created_at
        datetime updated_at
    }

    THEATER ||--o{ ROOM : has
    ROOM ||--o{ SEAT : has
    ROOM ||--o{ SCREENING : hosts
    MOVIE ||--o{ SCREENING : shown_as

    USER ||--o{ RESERVATION : makes
    SCREENING ||--o{ RESERVATION : has
    RESERVATION ||--o{ RESERVATION_SEAT : contains
    RESERVATION ||--o| PAYMENT : paid_by
    SEAT ||--o{ RESERVATION_SEAT : selected
```

## Constraints
- USER UNIQUE(email)
- SEAT UNIQUE(room_id, row_number, column_number)
- RESERVATION UNIQUE(id, screening_id)
- RESERVATION_SEAT UNIQUE(screening_id, seat_id)
- RESERVATION_SEAT FK(reservation_id, screening_id) → RESERVATION(id, screening_id)
- PAYMENT UNIQUE(reservation_id)
- ROOM UNIQUE(theater_id, name)
- THEATER UNIQUE(address, name)
- SCREENING UNIQUE(room_id, start_time)
- SCREENING must not overlap in same room

