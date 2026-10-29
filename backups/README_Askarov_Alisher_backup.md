# askarov-alisher-student-api

Student API of **Alisher Askarov** (group IT2-2304, student ID 38226).
DevOps midterm: Linux, Git, Docker Compose and Jenkins around a Spring Boot CRUD application.

Current stage: development

## Technologies

- Java 21, Spring Boot 3.5, Spring Data JPA
- PostgreSQL (H2 in memory for the tests)
- Gradle wrapper

## REST API

- `GET /` - application info and student name
- `GET /api/students` - all students
- `GET /api/students/{id}` - one student
- `POST /api/students` - create a student
- `PUT /api/students/{id}` - update a student
- `DELETE /api/students/{id}` - delete a student

## Project folders

- `scripts/` - Bash script `Askarov_Alisher_system_report.sh`
- `logs/` - system report log and log analysis
- `backups/` - backup copy of this README
- `src/` - Spring Boot application and tests
