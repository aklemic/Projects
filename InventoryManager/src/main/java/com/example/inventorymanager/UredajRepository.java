package com.example.inventorymanager;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

public class UredajRepository {

    private static final Logger LOGGER = Logger.getLogger(UredajRepository.class.getName());

    private final InMemoryDataSource dataSource;

    public UredajRepository(InMemoryDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<Uredaj> findAll() {
        return dataSource.getUredaji();
    }

    public Optional<Uredaj> findById(Long id) {
        return dataSource.getUredaji().stream()
                .filter(u -> u.getId().equals(id))
                .findFirst();
    }
    public void save(Uredaj uredaj) {
        List<Uredaj> uredaji = dataSource.getUredaji();

        findById(uredaj.getId()).ifPresentOrElse(
                postojeći -> {
                    int index = uredaji.indexOf(postojeći);
                    uredaji.set(index, uredaj);
                },
                () -> uredaji.add(uredaj)
        );
    }

    public void deleteById(Long id) {
        List<Uredaj> uredaji = dataSource.getUredaji();
        uredaji.removeIf(u -> u.getId().equals(id));
    }
}
