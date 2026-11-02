# StockIA Platform

RESTful API of **StockIA**, the smart inventory and demand forecasting platform for restaurants built by **DataBit**
(Open Source course, UPC). It serves the StockIA Web Application and follows Domain-Driven Design with one package per
bounded context and a hexagonal layering (domain, application, infrastructure, interfaces).

## Technologies

- Java 21, Spring Boot 3.5 (Web, Data JPA, Validation, Security), Maven
- MySQL 8 (H2 in memory for tests and the `local` profile)
- JWT bearer tokens (jjwt) and BCrypt password hashing
- springdoc-openapi (OpenAPI 3 + Swagger UI)
- ONNX Runtime for Java (demand forecasting model trained with scikit-learn)
- Open-Meteo weather forecast API (third-party service, no API key)
- JUnit 5, AssertJ, Spring MockMvc, GitHub Actions

## Bounded contexts

| Package | Bounded context | Main endpoints (`/api/v1`) |
|---|---|---|
| `iam` | Identity & Access Management: sign-in, account lock (5 wrong passwords lock the account 15 minutes), password recovery, team and roles | `/authentication/**`, `/users/**` |
| `restaurantregistration` | Restaurant Registration: sign-up of a restaurant with its owner and the restaurant profile | `/restaurant-registrations`, `/restaurants/current` |
| `subscriptionpayments` | Subscription & Payments: plan catalog and simulated Stripe / PayPal checkout | `/plans`, `/subscriptions/**` |
| `stockmanagement` | Stock Management: supply lots with expiration dates and exact quantities (up to 32 decimals) | `/inventory-items/**` |
| `recipesmanagement` | Recipes Management: recipes, dish price and the Ingredient Deduction Engine for sales | `/recipes/**`, `/sales/**` |
| `notificationsmessaging` | Notifications & Messaging: automatic and manual alerts by WhatsApp / email (simulated) | `/alerts/**` |
| `mlrecommendations` | ML & Recommendations: demand forecast (ONNX model + Peruvian calendar + weather) and purchase / menu recommendations | `/demand-forecasts/**`, `/recommendations/**` |
| `analyticsdashboard` | Analytics & Dashboard: KPIs and the Weekly Savings Calculator | `/dashboard/summary`, `/dashboard/weekly-savings` |
| `shared` | Shared kernel: `Quantity`, `Result`, problem details, i18n, security context, OpenAPI, snake_case naming, demo seeder | `/health` |

Each bounded context is organized as:

```
<context>/
├── domain/          model (aggregates, entities, value objects, events), domain services, repository ports
├── application/     commands, queries, command/query service interfaces and their internal implementations,
│                    ACL facade implementation, event handlers, outbound services (anti-corruption layer)
├── infrastructure/  JPA entities, mappers, Spring Data repositories and adapters, external services
└── interfaces/      REST controllers, resources, transform mappers and the ACL facade other contexts use
```

Contexts talk to each other only through their `interfaces/acl` facades and through domain events
(`StockLevelChangedEvent`, `DemandForecastGeneratedEvent`).

## Running the API

Requirements: JDK 21 and Maven 3.9 (or Docker).

```bash
# 1. In-memory H2 database (no MySQL needed)
SPRING_PROFILES_ACTIVE=local mvn spring-boot:run

# 2. Local MySQL (default "dev" profile; the database stockia_os is created automatically)
DATABASE_USER=root DATABASE_PASSWORD=root mvn spring-boot:run

# 3. Docker: MySQL + API
docker compose up --build
```

The API listens on `http://localhost:8181`:

- Swagger UI: http://localhost:8181/swagger-ui.html
- OpenAPI document: http://localhost:8181/v3/api-docs
- Health: http://localhost:8181/api/v1/health

### Demo accounts

With `stockia.seed.demo-data=true` (default) the first start loads a demo restaurant: 10 supply lots (one product
only in an expired lot, one without stock), 4 recipes, 120 days of sales history, automatic alerts and a first forecast.

| Role | Email | Password |
|---|---|---|
| Administrator | `admin@databitecorp.com` | `stockia123` |
| Employee | `empleado@databitecorp.com` | `stockia123` |

### Configuration (environment variables)

| Variable | Default | Purpose |
|---|---|---|
| `SPRING_PROFILES_ACTIVE` | `dev` | `dev` (local MySQL), `prod` (all settings from the environment), `local` (H2) |
| `DATABASE_URL`, `DATABASE_USER`, `DATABASE_PASSWORD` | local MySQL | Database connection |
| `STOCKIA_JWT_SECRET` | development secret | HS256 secret, at least 32 characters |
| `STOCKIA_CORS_ALLOWED_ORIGINS` | `http://localhost:4200,https://*.vercel.app` | Origins of the web application |
| `STOCKIA_WEB_APP_URL` | `http://localhost:4200` | Base of the password recovery link |
| `STOCKIA_SEED_DEMO_DATA` | `true` | Load the demo restaurant |
| `STOCKIA_WEATHER_ENABLED` | `true` | Call Open-Meteo (Lima climatology is used when it is off or unreachable) |
| `PORT` | `8181` | HTTP port |

## Internationalization

Messages, validation errors, alerts, recommendations and plan features are returned in **English by default** and in
**Spanish** when the request sends `Accept-Language: es` (`es-419`, `es-PE`, … also work). The texts are in
`src/main/resources/messages.properties` and `messages_es.properties`.

Errors follow RFC 9457 problem details: `title` is a stable error code (for example `InvalidCredentials`,
`CannotSell`, `AccountLocked`), `detail` is the localized message and `details` carries extra data when the web
application needs it (remaining sign-in attempts, the supplies that block a sale, …).

## Tests

```bash
mvn verify
```

- Unit tests: `Quantity`, stock allocation (freshest usable lot first, never an expired one), account lock and
  password reset, Peruvian calendar, alert policy, recommendation policy, weekly savings, and the **ONNX parity test**
  (features and predictions of the Java model equal the Python training pipeline).
- Integration tests (`StockiaPlatformIntegrationTest`, MockMvc + H2): security, i18n, sales with expired stock,
  demand forecast, sign-up and password recovery.
- `tests/smoke/smoke_test.py`: black-box test of the packaged API against MySQL, run by GitHub Actions.

## Git workflow

GitFlow with `main` (releases) and `develop` (integration); features in `feature/<name>`, releases in
`release/<version>` and fixes in `hotfix/<version>`. Commits follow Conventional Commits and versions follow Semantic
Versioning (current version: `1.0.1`).

## Team

DataBit — UPC, Open Source (1ASI0729), section 7747.
