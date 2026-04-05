# ATS — Event-Driven Applicant Tracking System

A **production-style, distributed Applicant Tracking System** built with Spring Boot microservices, Kafka, Redis, PostgreSQL, and real AI-powered resume screening using HuggingFace — fully containerized with Docker.

> Not just a CRUD app — a real distributed system with event-driven architecture, async processing, and intelligent candidate screening.

---

## What It Does

1. **Candidate applies** → Application is stored in PostgreSQL
2. **Kafka event fired** → Screening service processes it asynchronously
3. **AI scores the resume** → HuggingFace computes semantic similarity
4. **Bonus logic applied** → +10 if resume matches job title
5. **Result published** → Notification service sends email

---

## Architecture

```
Client
↓
API Gateway (JWT auth · port 8080)
↓
Application Service (port 8081)
→ generates JWT (/auth/login)
→ stores application in PostgreSQL
→ publishes to Kafka: application.submitted
↓
Kafka
↓
Screening Service (port 8082)
→ consumes application event
→ calls Job Service (Redis → PostgreSQL)
→ calls HuggingFace API (AI scoring)
→ publishes to Kafka: screening.completed
↓
Kafka
↓
Notification Service (port 8083)
→ sends result email via SMTP

![ATS Architecture](./ATS-architecture.drawio.png)
```

---

## Services

| Service                | Port | Responsibility                                      |
| ---------------------- | ---- | --------------------------------------------------- |
| `api-gateway`          | 8080 | Routing, entry point                                |
| `application-service`  | 8081 | Authentication, application storage, Kafka producer |
| `screening-service`    | 8082 | AI scoring, Kafka consumer/producer                 |
| `notification-service` | 8083 | Email notifications                                 |
| `job-service`          | 8084 | Job data, Redis caching, PostgreSQL                 |

---

## Infrastructure

| Component       | Purpose                        |
| --------------- | ------------------------------ |
| PostgreSQL      | Application + job data storage |
| Apache Kafka    | Event-driven communication     |
| Redis           | Job caching                    |
| HuggingFace API | AI scoring                     |
| SMTP (Gmail)    | Email delivery                 |

---

## Event Flow (Core Design)

`application.submitted` → produced by Application Service, consumed by Screening Service
`screening.completed` → produced by Screening Service, consumed by Notification Service

---

## AI Screening Engine

Model used:
`sentence-transformers/all-MiniLM-L6-v2`

### Process:

Convert resume + job description → embeddings
Compute similarity score (0–100)
Add bonus if job title matches
Cap score at 100

| Score | Result   |
| ----- | -------- |
| ≥ 70  | Passed   |
| < 70  | Rejected |

---

## Redis Caching

```
First request  → PostgreSQL → cached in Redis  
Next requests  → served from Redis  
```

Used in Job Service via:

`@Cacheable`
`@CacheEvict`

---

## Shared Module

Contains shared DTOs (e.g., `ScreeningResultEvent`) used across services.

Ensures:

consistent event structure
reduced duplication
better maintainability

---

## Automated Setup

Run everything with one command:

```bash
chmod +x setup.sh && ./setup.sh
```

The script:

builds shared module
builds all services
builds Docker images
starts full system

---

## API Examples

### Create Job

```bash
curl -X POST http://localhost:8084/jobs \
  -H "Content-Type: application/json" \
  -d '{"title":"Java Developer","description":"Spring Boot Kafka"}'
```

### Submit Application

```bash
curl -X POST http://localhost:8081/apply \
  -H "Content-Type: application/json" \
  -d '{
    "candidateName":"John",
    "candidateEmail":"john@example.com",
    "resumeText":"Java Spring Boot Kafka developer",
    "jobId":1
  }'
```

---

## Security

JWT tokens generated in Application Service
API Gateway routes authenticated requests

---

## Limitations

JWT validation not fully enforced in API Gateway filter
No retry mechanism or DLQ for Kafka failures
Event payload uses JSON instead of strict schema enforcement
Job data fetch is synchronous (can introduce latency under load)
Email delivery depends on external SMTP availability

---

## Future Enhancements

Dead-letter queue (DLQ) for Kafka
Retry mechanisms
Full JWT enforcement in gateway
Resume parsing (PDF support)
Recruiter dashboard
Elasticsearch integration

---

## System Status

| Component           | Status  |
| ------------------- | ------- |
| Microservices       | Running |
| Kafka               | Working |
| Redis               | Working |
| PostgreSQL          | Working |
| AI Scoring          | Working |
| Email Notifications | Working |
| Docker Setup        | Working |

---

## Final Summary

This system demonstrates:

event-driven architecture
microservices design
AI integration
caching strategies
distributed system debugging
containerized deployment

---

## License

MIT License

## Code Review PR


