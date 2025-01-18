package ru.job4j.dreamjob.repository;

import ru.job4j.dreamjob.model.Vacancy;

import java.time.LocalDateTime;
import java.util.*;

public class MemoryVacancyRepository implements VacancyRepository {

    public static final MemoryVacancyRepository INSTANCE = new MemoryVacancyRepository();
    private int nextId = 1;
    private final Map<Integer, Vacancy> vacansies = new HashMap<>();

    private MemoryVacancyRepository() {
        save(new Vacancy(0, "Intern Java Developer",
                getDefaultDescriptionAboutVacancies("Intern"), LocalDateTime.now()));
        save(new Vacancy(0, "Junior Java Developer",
                getDefaultDescriptionAboutVacancies("junior"), LocalDateTime.now()));
        save(new Vacancy(0, "Junior+ Java Developer",
                getDefaultDescriptionAboutVacancies("junior+"), LocalDateTime.now()));
        save(new Vacancy(0, "Middle Java Developer",
                getDefaultDescriptionAboutVacancies("middle"), LocalDateTime.now()));
        save(new Vacancy(0, "Middle+ Java Developer",
                getDefaultDescriptionAboutVacancies("middle+"), LocalDateTime.now()));
        save(new Vacancy(0, "Senior Java Developer",
                getDefaultDescriptionAboutVacancies("senior"), LocalDateTime.now()));
    }

    public static MemoryVacancyRepository getInstance() {
        return INSTANCE;
    }

    @Override
    public Vacancy save(Vacancy vacancy) {
        vacancy.setId(nextId++);
        vacansies.put(vacancy.getId(), vacancy);
        return vacancy;
    }

    @Override
    public void deleteById(int id) {
        vacansies.remove(id);
    }

    @Override
    public boolean update(Vacancy vacancy) {
        return vacansies.computeIfPresent(vacancy.getId(),
                (id, oldVacancy) -> new Vacancy(
                        oldVacancy.getId(),
                        vacancy.getTitle(),
                        vacancy.getDescription(),
                        vacancy.getCreationDate())) != null;
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
