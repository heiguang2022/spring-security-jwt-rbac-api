# Spring Security JWT Authentication and RBAC API

A portfolio-ready Spring Boot backend demonstrating stateless authentication, rotating refresh tokens, and role-based authorization with clear security tests.

> This is a self-initiated, reproducible portfolio demonstration. It contains no employer or client source code, credentials, or user data.

## Portfolio snapshot

- Java 17 and Spring Boot 3.5
- Spring Security resource server with HMAC-signed JWT access tokens
- BCrypt password hashing with cost factor 12
- USER and ADMIN role-based access control
- 15-minute access tokens and rotating 7-day refresh tokens
- SHA-256 refresh-token hashes stored in the database; raw tokens are never persisted
- Structured JSON responses for validation, `401`, `403`, and `409` errors
- H2 zero-setup demo and MySQL 8.4 Docker profile
- Swagger UI and OpenAPI bearer authentication
- 9 passing MockMvc integration tests
- GitHub Actions CI

## Security endpoints

| Method | Path | Access | Purpose |
|---|---|---|---|
| `POST` | `/api/auth/register` | Public | Create a USER and issue a token pair |
| `POST` | `/api/auth/login` | Public | Verify credentials and issue tokens |
| `POST` | `/api/auth/refresh` | Public | Rotate an active refresh token |
| `POST` | `/api/auth/logout` | Authenticated | Revoke the caller's refresh token |
| `GET` | `/api/profile` | USER or ADMIN | Return the authenticated profile |
| `GET` | `/api/admin/users` | ADMIN | List registered users |

## Run locally

Requirements: Java 17+ and Maven 3.6.3+.

```bash
mvn spring-boot:run
```

Open:

- Swagger UI: <http://localhost:8080/swagger-ui.html>
- OpenAPI JSON: <http://localhost:8080/v3/api-docs>
- Health check: <http://localhost:8080/actuator/health>

Register a user:

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"user@example.com","password":"StrongPass1"}'
```

Copy the returned access token into Swagger's **Authorize** dialog or send it as `Authorization: Bearer <token>`.

The included [`requests.http`](requests.http) file covers the full login, profile, refresh, logout, and admin flow.

## Run with MySQL and Docker

```bash
docker compose up --build
```

The Compose values and default JWT secret are local demonstrations only. Set a randomly generated secret of at least 32 bytes and provide database credentials through a secret manager for any deployed environment.

## Test

```bash
mvn verify
```

The 9 integration tests verify registration, email normalization, duplicate prevention, password rules, valid and invalid login, protected routes, USER/ADMIN authorization, refresh-token rotation, replay rejection, and logout revocation.

## Security design

- **Short-lived access:** JWT access tokens expire after 15 minutes.
- **Refresh rotation:** every refresh invalidates the previous token and returns a new pair.
- **No raw refresh-token storage:** only deterministic SHA-256 hashes are persisted for lookup and revocation.
- **Least privilege:** registration always creates a USER; ADMIN cannot be self-assigned through the public API.
- **Stateless requests:** protected endpoints verify a signed bearer token without an HTTP session.
- **Consistent failures:** authentication and authorization failures return explicit JSON `401` and `403` responses.

This compact demo intentionally excludes social OAuth, email verification, password reset, MFA, asymmetric keys, and distributed revocation. Those require additional infrastructure and should be scoped separately.

The security sequence is available as editable Mermaid in [`docs/security-flow.mmd`](docs/security-flow.mmd).

## Project structure

```text
src/main/java/.../security/
├── auth/          # Registration, login, token issue and rotation
├── config/        # Spring Security, JWT, JSON failures, OpenAPI
├── user/          # Users, roles, protected resources
└── web/           # Consistent controller error responses

src/test/java/     # End-to-end security tests
docker/mysql/      # MySQL schema
portfolio/         # Upwork-ready copy and gallery assets
```

## License

Available under the [MIT License](LICENSE).
