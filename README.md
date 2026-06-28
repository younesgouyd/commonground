<h1 style="text-align: center">CommonGround</h1>

CommonGround is a cross-platform social app that facilitates the creation and discovery of real-world events. Unlike traditional social platforms focused on content consumption, CommonGround encourages real-world activities.

#### Examples of events
- Fan convention.
- Book swapping or book exchange.
- Friends party.
- Chess competition.
- Restaurant grand opening.

## Features
- Create and share your event as an admin with properties such as a description, location, time, tags, public/private, attendee limit (if private)...etc.
- Find events near your current or a specified location and apply various filters to find your desired event.
- Follow other users to easily discover events with shared interests.
- Users who are interested in coming will join a temporary group chat to discuss the details of the event and get hyped about it prior to the gathering.




## Tech Stack

### Backend (`:server`)
- **Core Framework:** Spring Boot (Web, Security) for the RESTful API layer and secure filter chain.
- **Database Engine:** PostgreSQL with the **PostGIS** geospatial engine extension to handle geographic coordinate operations efficiently at scale.
- **Object-Relational Mapping (ORM):** Spring Data JPA with **Hibernate Spatial** for for indexing events and optimizing the search.
- **Authentication Engine:** Stateless architecture using **JJWT (Java JWT)**.

### Client (`:client`)
- **Cross-Platform Compilation:** **Kotlin Multiplatform (KMP)** for shared logic.
- **UI Framework:** **Compose Multiplatform** with **Maplibre Compose** for a reactive, shared user interface design system.
- **Networking:** **Ktor Client** for communicating with the backend's RESTful API.