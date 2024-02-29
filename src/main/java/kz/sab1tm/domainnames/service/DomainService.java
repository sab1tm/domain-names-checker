package kz.sab1tm.domainnames.service;

import kz.sab1tm.domainnames.model.Domain;
import kz.sab1tm.domainnames.repository.DomainRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@AllArgsConstructor
public class DomainService {

    private final DomainRepository domainRepository;

    public void create(Domain entity) {
        domainRepository.create(entity);
    }

    public void update(String name, LocalDate releaseDate, LocalDate checkDate) {
        domainRepository.update(name, releaseDate, checkDate);
    }

    public void deleteByName(String name) {
        domainRepository.deleteByName(name);
    }

    public Domain getByName(String name) {
        return domainRepository.getByName(name);
    }

    public List<Domain> getAll() {
        return domainRepository.getAll();
    }
}
