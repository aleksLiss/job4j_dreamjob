package ru.job4j.dreamjob.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.sql2o.Sql2o;
import ru.job4j.dreamjob.configuration.DatasourceConfiguration;
import ru.job4j.dreamjob.model.Candidate;

import javax.sql.DataSource;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class Sql2oCandidateRepositoryTest {

    private static Sql2oCandidateRepository sql2oCandidateRepository;

    @BeforeAll
    public static void initRepository() throws Exception {
        Properties properties = new Properties();
        try (InputStream inputStream = Sql2oCandidateRepositoryTest.class.getClassLoader()
                .getResourceAsStream("connection.properties")) {
            properties.load(inputStream);
        }
        String url = properties.getProperty("datasource.url");
        String username = properties.getProperty("datasource.username");
        String password = properties.getProperty("datasource.password");
        DatasourceConfiguration configuration = new DatasourceConfiguration();
        DataSource datasource = configuration.connectionPool(url, username, password);
        Sql2o sql2o = configuration.databaseClient(datasource);
        sql2oCandidateRepository = new Sql2oCandidateRepository(sql2o);
    }

    @AfterEach
    public void deleteCandidate() {
        Collection<Candidate> candidates = sql2oCandidateRepository.findAll();
        for (Candidate candidate : candidates) {
            sql2oCandidateRepository.deleteById(candidate.getId());
        }
    }

    @Test
    public void whenSaveCandidateThenReturnThisCandidate() {
        LocalDateTime creationDate = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        Candidate candidate = new Candidate(0, "vova", "Java Dev", creationDate);
        Candidate result = sql2oCandidateRepository.save(candidate);
        assertThat(result).isEqualTo(candidate);
    }

    @Test
    public void whenDontSaveCandidateThenReturnEmptyResult() {
        Collection<Candidate> result = sql2oCandidateRepository.findAll();
        assertThat(result).isEqualTo(emptyList());
    }

    @Test
    public void whenSaveAndFindByIdCandidateThenReturnSavedCandidate() {
        LocalDateTime creationDate = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        Candidate candidate = new Candidate(0, "vova", "Java", creationDate);
        sql2oCandidateRepository.save(candidate);
        Candidate result = sql2oCandidateRepository.findById(candidate.getId()).get();
        assertThat(result.getId()).isEqualTo(candidate.getId());
    }

    @Test
    public void whenSaveSomeCandidatesAndFindAllThenReturnAllSavedCandidates() {
        LocalDateTime creationDate = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        Candidate candidate1 = new Candidate(0, "vova", "JAva", creationDate);
        Candidate candidate2 = new Candidate(1, "nina", "Kotlin", creationDate);
        Candidate candidate3 = new Candidate(2, "kolya", "C", creationDate);
        sql2oCandidateRepository.save(candidate1);
        sql2oCandidateRepository.save(candidate2);
        sql2oCandidateRepository.save(candidate3);
        Collection<Candidate> result = sql2oCandidateRepository
                .findAll();
        List<Candidate> expected = List.of(candidate1, candidate2, candidate3);
        assertThat(result).isEqualTo(expected);
    }

    @Test
    public void whenSaveCandidateAndDeleteThenReturnNothing() {
        LocalDateTime creationDate = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        Candidate candidate1 = new Candidate(0, "vova", "JAva", creationDate);
        sql2oCandidateRepository.save(candidate1);
        sql2oCandidateRepository.deleteById(candidate1.getId());
        assertThat(sql2oCandidateRepository.findById(0)).isEqualTo(Optional.empty());
    }

    @Test
    public void whenSaveCandidateAndUpdateThenReturnCandidateWithNewParameters() {
        LocalDateTime creationDate = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        Candidate oldCandidate = sql2oCandidateRepository.save(new Candidate(0, "nina", "Java",
                creationDate));
        Candidate updatedCandidate = new Candidate(
                oldCandidate.getId(), "vova", "Kotlin",
                creationDate.plusHours(24));
        boolean isUpdated = sql2oCandidateRepository.update(updatedCandidate);
        Candidate savedVacancy = sql2oCandidateRepository.findById(updatedCandidate.getId()).get();
        assertThat(isUpdated).isTrue();
        assertThat(savedVacancy).usingRecursiveComparison().isEqualTo(updatedCandidate);
    }

}