package ru.job4j.dreamjob.service;

import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Service;
import ru.job4j.dreamjob.model.Candidate;
import ru.job4j.dreamjob.repository.CandidateRepository;

import java.util.Collection;
import java.util.Optional;

@ThreadSafe
@Service
public class SimpleCandidateService implements CandidateService {

    private final CandidateRepository sql2oCandidateRepository;

    public SimpleCandidateService(CandidateRepository sql2oCandidateRepository) {
        this.sql2oCandidateRepository = sql2oCandidateRepository;
    }

    @Override
    public Candidate save(Candidate candidate) {
        return sql2oCandidateRepository.save(candidate);
    }

    @Override
    public boolean deleteById(int id) {
        return sql2oCandidateRepository.deleteById(id);
    }

    @Override
    public boolean update(Candidate candidate) {
        return sql2oCandidateRepository.update(candidate);
    }

    @Override
    public Optional<Candidate> findById(int id) {
        return sql2oCandidateRepository.findById(id);
    }

    @Override
    public Collection<Candidate> findAll() {
        return sql2oCandidateRepository.findAll();
    }
}
