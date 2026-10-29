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
- `GET /api/students?group=IT2-2304` - students of one group (sorted by last name)
- `GET /api/students/{id}` - one student
- `POST /api/students` - create a student
- `PUT /api/students/{id}` - update a student
- `DELETE /api/students/{id}` - delete a student

## Changes by Alisher Askarov (branch feature/askarov-alisher-crud)

- Read operation improved: `GET /api/students` can filter by group with `?group=`,
  the group is compared without case and the result is sorted by last name.
- Errors are returned as JSON: an unknown id gives `404` with the message
  `Student with id ... not found` (GET, PUT, DELETE), a student without first or
  last name gives `400 Bad Request` instead of a database error.
- Tests: `filterStudentsByGroup`, `unknownStudentGivesJson404`, `studentWithoutNameGives400`.

## Project folders

- `scripts/` - Bash script `Askarov_Alisher_system_report.sh`
- `logs/` - system report log and log analysis
- `backups/` - backup copy of this README
- `src/` - Spring Boot application and tests
