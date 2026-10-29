package kz.askarov.studentapi;

import java.net.URI;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final StudentRepository repository;

    public StudentController(StudentRepository repository) {
        this.repository = repository;
    }

    // GET /api/students - all students, GET /api/students?group=IT2-2304 - only one group
    @GetMapping
    public List<Student> findAll(@RequestParam(required = false) String group) {
        if (group != null && !group.isBlank()) {
            return repository.findByGroupNameIgnoreCaseOrderByLastNameAsc(group.trim());
        }
        return repository.findAll(Sort.by("id"));
    }

    @GetMapping("/{id}")
    public Student findOne(@PathVariable Long id) {
        return repository.findById(id).orElseThrow(() -> new StudentNotFoundException(id));
    }

    @PostMapping
    public ResponseEntity<Student> create(@RequestBody Student student) {
        checkNames(student);
        student.setId(null);
        Student saved = repository.save(student);
        return ResponseEntity.created(URI.create("/api/students/" + saved.getId())).body(saved);
    }

    @PutMapping("/{id}")
    public Student update(@PathVariable Long id, @RequestBody Student data) {
        checkNames(data);
        Student student = repository.findById(id).orElseThrow(() -> new StudentNotFoundException(id));
        student.setFirstName(data.getFirstName());
        student.setLastName(data.getLastName());
        student.setEmail(data.getEmail());
        student.setGroupName(data.getGroupName());
        student.setCourse(data.getCourse());
        return repository.save(student);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!repository.existsById(id)) {
            throw new StudentNotFoundException(id);
        }
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // first and last name are required (NOT NULL columns), otherwise 400 Bad Request
    private static void checkNames(Student student) {
        if (student.getFirstName() == null || student.getFirstName().isBlank()
                || student.getLastName() == null || student.getLastName().isBlank()) {
            throw new IllegalArgumentException("firstName and lastName are required");
        }
    }
}
