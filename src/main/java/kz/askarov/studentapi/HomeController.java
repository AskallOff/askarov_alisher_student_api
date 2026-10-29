package kz.askarov.studentapi;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.info.BuildProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    private final String studentName;
    private final String studentGroup;
    private final String studentId;
    private final String version;

    public HomeController(@Value("${student.full-name}") String studentName,
                          @Value("${student.group}") String studentGroup,
                          @Value("${student.id}") String studentId,
                          ObjectProvider<BuildProperties> buildProperties) {
        this.studentName = studentName;
        this.studentGroup = studentGroup;
        this.studentId = studentId;
        BuildProperties build = buildProperties.getIfAvailable();
        this.version = build != null ? build.getVersion() : "dev";
    }

    @GetMapping("/")
    public Map<String, Object> home() {
        Map<String, Object> info = new LinkedHashMap<>();
        info.put("application", "askarov-alisher-student-api");
        info.put("version", version);
        info.put("student", studentName);
        info.put("group", studentGroup);
        info.put("studentId", studentId);
        info.put("students", "/api/students");
        info.put("health", "/actuator/health");
        return info;
    }
}
