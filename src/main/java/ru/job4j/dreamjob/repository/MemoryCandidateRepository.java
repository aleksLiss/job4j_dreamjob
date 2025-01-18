package ru.job4j.dreamjob.repository;

import org.springframework.stereotype.Repository;
import ru.job4j.dreamjob.model.Candidate;

import java.time.LocalDateTime;
import java.util.*;

@Repository
public class MemoryCandidateRepository implements CandidateRepository {

    public static final MemoryCandidateRepository INSTANCE = new MemoryCandidateRepository();
    private int nextInt = 1;
    private final Map<Integer, Candidate> candidates = new HashMap<>();

    private MemoryCandidateRepository() {
        save(new Candidate(0, "Вова",
                "Возраст: 37. Город: Москва. Уровень: Джуниор.", LocalDateTime.now()));
        save(new Candidate(0, "Миша",
                "Возраст: 18. Город: Минск. Уровень: Сеньор.", LocalDateTime.now()));
        save(new Candidate(0, "Нина",
                "Возраст: 19. Город: Калининград. Уровень: Миддл.", LocalDateTime.now()));
        save(new Candidate(0, "Коля",
                "Возраст: 35. Город: Москва. Уровень: Интерн.", LocalDateTime.now()));
    }

    public static MemoryCandidateRepository getInstance() {
        return INSTANCE;
    }

    @Override
    public Candidate save(Candidate candidate) {
        candidate.setId(nextInt++);
        candidates.put(candidate.getId(), candidate);
        return candidate;
    }

    @Override
    public void deleteById(int id) {
        candidates.remove(id);
    }

    @Override
    public boolean update(Candidate candidate) {
        return candidates.computeIfPresent(candidate.getId(),
                (id, oldCandidate) -> new Candidate(
                        oldCandidate.getId(),
                        candidate.getName(),
                        candidate.getDescription(),
                        candidate.getCreationDate())) != null;
    }

    @Override
    public Optional<Candidate> findById(int id) {
        return Optional.ofNullable(candidates.get(id));
    }

    @Override
    public Collection<Candidate> findAll() {
        return candidates.values();
    }
}
