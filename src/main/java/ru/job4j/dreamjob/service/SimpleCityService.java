package ru.job4j.dreamjob.service;

import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Service;
import ru.job4j.dreamjob.model.City;
import ru.job4j.dreamjob.repository.CityRepository;

import java.util.Collection;

@ThreadSafe
@Service
public class SimpleCityService implements CityService {

    private final CityRepository sql2oCityRepository;

    public SimpleCityService(CityRepository sql2oCityRepository) {
        this.sql2oCityRepository = sql2oCityRepository;
    }

    @Override
    public Collection<City> findAll() {
        return sql2oCityRepository.findAll();
    }
}