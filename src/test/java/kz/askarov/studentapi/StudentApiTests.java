package kz.askarov.studentapi;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class StudentApiTests {

    private static final String ALISHER = """
            {"firstName":"Alisher","lastName":"Askarov","email":"alisher.askarov@example.com",
             "groupName":"IT2-2304","course":3}""";

    @Autowired
    private MockMvc mvc;

    @Autowired
    private StudentRepository repository;

    @BeforeEach
    void cleanDatabase() {
        repository.deleteAll();
    }

    private long createAlisher() throws Exception {
        String json = mvc.perform(post("/api/students").contentType(MediaType.APPLICATION_JSON).content(ALISHER))
                .andReturn().getResponse().getContentAsString();
        return Long.parseLong(json.replaceAll(".*\"id\":(\\d+).*", "$1"));
    }

    @Test
    void homePageShowsStudentName() throws Exception {
        mvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.student").value("Alisher Askarov"))
                .andExpect(jsonPath("$.studentId").value("38226"));
    }

    @Test
    void createAndReadStudent() throws Exception {
        mvc.perform(post("/api/students").contentType(MediaType.APPLICATION_JSON).content(ALISHER))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.lastName").value("Askarov"));

        mvc.perform(get("/api/students"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void updateStudent() throws Exception {
        long id = createAlisher();
        mvc.perform(put("/api/students/" + id).contentType(MediaType.APPLICATION_JSON)
                        .content(ALISHER.replace("\"course\":3", "\"course\":4")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.course").value(4));
    }

    @Test
    void filterStudentsByGroup() throws Exception {
        createAlisher();
        mvc.perform(post("/api/students").contentType(MediaType.APPLICATION_JSON)
                .content(ALISHER.replace("IT2-2304", "SE-2301").replace("Askarov", "Other")));

        mvc.perform(get("/api/students").param("group", "it2-2304"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].lastName").value("Askarov"));
    }

    @Test
    void deleteStudent() throws Exception {
        long id = createAlisher();
        mvc.perform(delete("/api/students/" + id)).andExpect(status().isNoContent());
        mvc.perform(get("/api/students/" + id)).andExpect(status().isNotFound());
    }

    @Test
    void unknownStudentGivesJson404() throws Exception {
        mvc.perform(get("/api/students/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Student with id 999 not found"));
    }

    @Test
    void studentWithoutNameGives400() throws Exception {
        mvc.perform(post("/api/students").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\":\"\",\"groupName\":\"IT2-2304\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("firstName and lastName are required"));
    }
}
