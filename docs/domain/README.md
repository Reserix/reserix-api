# Domain Overview

## Domains

| Domain      | Responsibility                                                                                                     |
|-------------|--------------------------------------------------------------------------------------------------------------------|
| User        | Manage user's identity, status and permissions                                                                     |
| Theater     | Manage the theater informations including the seats information                                                    |
| Movie       | Manage the movie information like title, description and duration etc                                              |
| Screening   | Manage the movie schedule on each theater                                                                          |
| Reservation | Manage the seats and prevent the double booking, and confirm/cancel/expire the booking based on the payment result |
| Payment     | Request/Approve/Cancel/Validate the reservation                                                                    |

## Core Rules

- Theater should be the physical information about the theater.
- Reservation should manage the status of seats on each movie and theater.
- Payment should manage only the payment status.