# Deployment notes (MVP)

Issue #1 is intentionally not containerized yet. Docker Compose is scheduled after the order flow and test baseline are stable.

When containerization is introduced, the application will receive database and Redis settings through environment variables rather than committed secrets.
