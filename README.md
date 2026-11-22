# TaskMaster

A simple backend for collaborative task tracking. Users register, create projects, invite team members, and create, assign, search and track tasks with comments and file attachments.

**Tech stack:** Java 21, Spring Boot, Spring Security with JWT, Spring Data JPA, PostgreSQL, Gradle.

## Setup

1. Install Java 21 and PostgreSQL, and create a database named `taskmaster`.
2. Set these environment variables (they are never stored in the code):

   | Variable | Meaning |
   |---|---|
   | `DB_PASSWORD` | PostgreSQL password |
   | `DB_USERNAME` | PostgreSQL user (optional, default `postgres`) |
   | `JWT_SECRET` | Base64 text of at least 32 bytes used to sign tokens |

   Example secret (PowerShell): `[Convert]::ToBase64String((1..32 | ForEach-Object { Get-Random -Maximum 256 }) -as [byte[]])`

3. Run the app: `./gradlew bootRun` (Windows PowerShell: `.\gradlew bootRun`). It starts on `http://localhost:8080`. Tables are created automatically.

## How authentication works

- `POST /api/auth/register` creates an account. Passwords are stored as BCrypt hashes.
- `POST /api/auth/login` returns a JWT token (valid for 24 hours).
- Send the token on every other request: `Authorization: Bearer <token>`.
- **Logout:** tokens are stateless, so logging out means the client deletes its token.

## API

All endpoints except `/api/auth/**` need the token. Errors use one JSON shape: `{ "status": 400, "message": "...", "errors": { "field": "reason" } }`.

| Method | Path | Description |
|---|---|---|
| POST | `/api/auth/register` | Create an account (`name`, `email`, `password` min 8 characters) |
| POST | `/api/auth/login` | Log in (`email`, `password`) and get a token |
| GET | `/api/users/me` | View my profile |
| PUT | `/api/users/me` | Update my profile (`name`) |
| POST | `/api/projects` | Create a project (`name`, `description`). The creator becomes the owner and a member |
| GET | `/api/projects` | List projects I belong to |
| GET | `/api/projects/{id}` | View a project (members only) |
| POST | `/api/projects/{id}/members` | Add a registered user by `email` (owner only) |
| GET | `/api/projects/{id}/members` | List project members |
| POST | `/api/projects/{id}/tasks` | Create a task (`title`, `description`, `dueDate`, `priority`, `status`, `assignedToEmail`) |
| GET | `/api/projects/{id}/tasks` | List a project's tasks (filters below) |
| GET | `/api/tasks/my` | List tasks assigned to me (filters below) |
| GET | `/api/tasks/{id}` | View a task |
| PUT | `/api/tasks/{id}` | Update a task |
| DELETE | `/api/tasks/{id}` | Delete a task (task creator or project owner) |
| PATCH | `/api/tasks/{id}/complete` | Mark a task completed |
| PUT | `/api/tasks/{id}/assign` | Assign a task to a project member (`email`) |
| POST | `/api/tasks/{id}/comments` | Add a comment (`text`) |
| GET | `/api/tasks/{id}/comments` | List comments |
| POST | `/api/tasks/{id}/attachments` | Upload a file (multipart form field `file`, max 5MB) |
| GET | `/api/tasks/{id}/attachments` | List attachments |
| GET | `/api/attachments/{id}/download` | Download an attachment |

**Task list filters** (all optional, can be combined):

- `status` is `TODO`, `IN_PROGRESS` or `COMPLETED`.
- `search` matches text in the title or description, ignoring case.
- `sortBy` is `id`, `title`, `dueDate`, `priority` or `status`.
- `direction` is `asc` (default) or `desc`.

Example: `GET /api/projects/1/tasks?status=TODO&search=login&sortBy=dueDate&direction=asc`

**Access rules:** only project members can see or change a project's tasks, comments and attachments. A task can only be assigned to a project member.

## Project structure

```
controller/   REST endpoints
service/      business rules
repository/   database access (Spring Data JPA)
entity/       database tables
dto/          request and response objects
security/     JWT creation and the JWT request filter
config/       Spring Security setup
exception/    one place that formats errors
```

## Not implemented

The optional extensions are left out: real-time notifications and AI-generated task descriptions.

## Notes

- Uploaded files are saved in the `uploads/` folder next to the app (set by `app.upload-dir`). They are not committed to git.
- Task filtering and sorting run in Java after loading the tasks, to keep the code simple. For very large data sets this would move into database queries.
