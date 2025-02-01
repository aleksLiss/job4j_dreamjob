package ru.job4j.dreamjob.service;

import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Service;
import ru.job4j.dreamjob.model.User;
import ru.job4j.dreamjob.repository.Sql2oUserRepository;

import java.util.Collection;
import java.util.Optional;

@ThreadSafe
@Service
public class SimpleUserService implements UserService {

    private final Sql2oUserRepository sql2oUserRepository;

    public SimpleUserService(Sql2oUserRepository sql2oUserRepository) {
        this.sql2oUserRepository = sql2oUserRepository;
    }

    @Override
    public Optional<User> save(User user) {
        return sql2oUserRepository.save(user);
    }

    @Override
    public Optional<User> findByEmailAndPassword(String email, String password) {
        return sql2oUserRepository.findByEmailAndPassword(email, password);
    }

    @Override
    public Collection<User> findAll() {
        return sql2oUserRepository.findAll();
    }

    @Override
    public boolean deleteById(int id) {
        return sql2oUserRepository.deleteById(id);
    }
}
