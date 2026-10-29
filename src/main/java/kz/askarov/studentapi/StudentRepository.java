package kz.askarov.studentapi;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, Long> {

    // Spring Data builds the query from the method name:
    // ... WHERE upper(group_name) = upper(?) ORDER BY last_name
    List<Student> findByGroupNameIgnoreCaseOrderByLastNameAsc(String groupName);
}
