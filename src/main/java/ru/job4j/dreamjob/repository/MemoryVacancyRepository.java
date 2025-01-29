package ru.job4j.dreamjob.repository;

import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Repository;
import ru.job4j.dreamjob.model.Vacancy;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Repository
@ThreadSafe
public class MemoryVacancyRepository implements VacancyRepository {

    private final AtomicInteger nextId = new AtomicInteger(1);
    private final Map<Integer, Vacancy> vacansies = new HashMap<>();

    private MemoryVacancyRepository() {
        save(new Vacancy(0, "Intern Java Developer",
                getDefaultDescriptionAboutVacancies("Intern"), LocalDateTime.now(), true, 1, 0));
        save(new Vacancy(0, "Junior Java Developer",
                getDefaultDescriptionAboutVacancies("junior"), LocalDateTime.now(), true, 2, 0));
        save(new Vacancy(0, "Junior+ Java Developer",
                getDefaultDescriptionAboutVacancies("junior+"), LocalDateTime.now(), true, 3, 0));
        save(new Vacancy(0, "Middle Java Developer",
                getDefaultDescriptionAboutVacancies("middle"), LocalDateTime.now(), true, 2, 0));
        save(new Vacancy(0, "Middle+ Java Developer",
                getDefaultDescriptionAboutVacancies("middle+"), LocalDateTime.now(), true, 2, 0));
        save(new Vacancy(0, "Senior Java Developer",
                getDefaultDescriptionAboutVacancies("senior"), LocalDateTime.now(), true, 1, 0));
    }

    @Override
    public Vacancy save(Vacancy vacancy) {
        vacancy.setId(nextId.incrementAndGet());
        vacansies.put(vacancy.getId(), vacancy);
        return vacancy;
    }

    @Override
    public boolean deleteById(int id) {
        vacansies.remove(id);
        return true;
    }

    @Override
    public boolean update(Vacancy vacancy) {
        return vacansies.computeIfPresent(vacancy.getId(), (id, oldVacancy) -> {
            return new Vacancy(
                    oldVacancy.getId(), vacancy.getTitle(), vacancy.getDescription(),
                    vacancy.getCreationDate(), vacancy.getVisible(), vacancy.getCityId(), vacancy.getFileId()
            );
        }) != null;
    }

    @Override
    public Optional<Vacancy> findById(int id) {
        return Optional.ofNullable(vacansies.get(id));
    }

    @Override
    public Collection<Vacancy> findAll() {
        return vacansies.values();
    }

    private String getDefaultDescriptionAboutVacancies(String title) {
        String defaultDescrition;
        switch (title.toLowerCase()) {
            case "intern":
                defaultDescrition = "Интерн Java-разработчик — это начинающий специалист,\n"
                        + "который проходит стажировку или обучение в области разработки программного обеспечения с использованием языка Java.";
                break;
            case "junior":
                defaultDescrition = "Джуниор Java разработчик — это начинающий специалист в области программирования,\n"
                        + " который работает с языком Java и связанными с ним технологиями.";
                break;
            case "junior+":
                defaultDescrition = "Джуниор+ Java разработчик — это специалист, который находится на промежуточной стадии\n"
                        + "между позиции джуниор (начинающий) и миддл (средний) разработчик.";
                break;
            case "middle":
                defaultDescrition = "Миддл Java разработчик — это специалист со средним уровнем опыта в разработке программного обеспечения на языке Java.\n"
                        + "Он обычно обладает более глубокими знаниями и навыками, чем джуниор и джуниор+,\n"
                        + "и способен решать более сложные задачи без постоянного контроля со стороны более опытных коллег.";
                break;
            case "middle+":
                defaultDescrition = "Миддл+ Java разработчик — это специалист с более высоким уровнем опыта и знаний, чем миддл разработчик,\n"
                        + "но еще не достигший уровня сеньора. Обычно такие разработчики имеют от 4 до 6 лет опыта работы\n"
                        + "в области разработки программного обеспечения на языке Java и способны самостоятельно решать сложные задачи,\n"
                        + "а также принимать участие в проектировании архитектуры приложений.\n";
                break;
            case "senior":
                defaultDescrition = "Сеньор Java разработчик — это высококвалифицированный специалист с значительным опытом работы\n"
                        + "в разработке программного обеспечения на языке Java. Обычно такие разработчики имеют от 5 до 10 лет опыта\n"
                        + "и способны самостоятельно решать сложные задачи,\n"
                        + "а также принимать участие в проектировании архитектуры приложений и управлении проектами.\n";
                break;
            default:
                defaultDescrition = "Такой вакансии не существует.";
                break;
        }
        return defaultDescrition;
    }
}
