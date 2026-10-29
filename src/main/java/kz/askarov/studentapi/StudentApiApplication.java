package kz.askarov.studentapi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class StudentApiApplication {

    private static final Logger log = LoggerFactory.getLogger(StudentApiApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(StudentApiApplication.class, args);
    }

    // One line in the application log shows whose service is running
    @Bean
    CommandLineRunner ownerInfo(@Value("${student.full-name}") String name,
                                @Value("${student.group}") String group) {
        return args -> log.info("Student API of {} ({}) is ready", name, group);
    }
}
