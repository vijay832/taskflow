# TaskFlow — Task Management API & UI

A small full-stack task management app built to demonstrate the same stack used in production
work: **Java 17, Spring Boot, Spring Security (JWT), Spring Data JPA, PostgreSQL/H2, and React.js.**

Users register, log in, and manage their own tasks (create, update, mark status, delete).
Each task is scoped to its owner via JWT-authenticated requests.

## Architecture

```
taskflow/
├── backend/                     Spring Boot REST API (Java 17)
│   ├── src/main/java/com/taskflow/
│   │   ├── model/               JPA entities: User, Task, Role, TaskStatus
│   │   ├── repository/          Spring Data JPA repositories
│   │   ├── service/             Business logic (TaskService, UserDetailsServiceImpl)
│   │   ├── controller/          REST endpoints (AuthController, TaskController)
│   │   ├── security/            JWT generation/validation + auth filter
│   │   ├── config/              Spring Security configuration
│   │   └── dto/                 Request/response DTOs (Java records)
│   └── src/test/java/...        JUnit 5 + Mockito unit tests
├── frontend/                    React SPA
│   └── src/
│       ├── api/                 Axios client with JWT interceptor
│       └── components/          Login, TaskList, TaskForm, PrivateRoute, Logout
├── docker-compose.yml           PostgreSQL container for local dev
└── README.md
```

**Request flow:** React (Axios) → REST controller → service layer → Spring Data JPA
repository → database. `JwtAuthenticationFilter` runs before every request to validate
the bearer token and populate the Spring Security context so controllers can resolve
`Authentication.getName()` as the current user.

## Tech decisions worth mentioning in the walkthrough

- **Stateless JWT auth** instead of sessions — matches the auth pattern used in the
  NGRM and Justice Case Management projects on the resume (OAuth2/JWT).
- **H2 in-memory DB by default** so anyone reviewing the project can run it with zero
  setup; a `postgres` Spring profile + `docker-compose.yml` is included to show the
  production-realistic path (matches Amazon RDS/PostgreSQL experience).
- **Ownership checks in the service layer**, not the controller, so authorization logic
  is unit-testable independent of HTTP.
- **`TaskService` is `@Transactional`** so the Hibernate session stays open long enough
  to lazily load the task's owner during ownership checks — without it, calls like
  delete/update throw a `LazyInitializationException` once the initial repository call's
  session closes.
- **DTOs as Java records** to keep request/response contracts explicit and immutable.

## Running it locally

**Backend** (from `backend/`):
```bash
mvn spring-boot:run
# API available at http://localhost:8080
# H2 console at http://localhost:8080/h2-console (JDBC URL: jdbc:h2:mem:taskflow)
```

To run against Postgres instead:
```bash
docker compose up -d postgres
mvn spring-boot:run -Dspring-boot.run.profiles=postgres
```

Run tests:
```bash
mvn test
```

**Frontend** (from `frontend/`):
```bash
npm install
npm start
# App available at http://localhost:3000
```

## API endpoints

| Method | Endpoint             | Auth required | Description            |
|--------|-----------------------|---------------|-------------------------|
| POST   | `/api/auth/register`  | No            | Create account, returns JWT |
| POST   | `/api/auth/login`     | No            | Login, returns JWT      |
| GET    | `/api/tasks`          | Yes           | List current user's tasks |
| POST   | `/api/tasks`          | Yes           | Create a task            |
| PUT    | `/api/tasks/{id}`     | Yes           | Update a task (owner only) |
| DELETE | `/api/tasks/{id}`     | Yes           | Delete a task (owner only) |


