package kz.sab1tm.domainnames.service;

import kz.sab1tm.domainnames.model.Domain;
import kz.sab1tm.domainnames.model.enumeration.DomainFilterEnum;
import kz.sab1tm.domainnames.model.enumeration.DomainStatusEnum;
import kz.sab1tm.domainnames.repository.DomainRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
public class DomainService {

    private final DomainRepository domainRepository;

    public void create(Domain entity) {
        domainRepository.create(entity);
    }

    public void update(Domain entity) {
        domainRepository.update(entity);
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

    public List<Domain> get10TodayReleases() {
        return domainRepository.get10TodayReleases();
    }

    public List<Domain> get10OldTodayReleases() {
        return domainRepository.get10OldTodayReleases();
    }

    public List<Domain> getByStatus(DomainStatusEnum status) {
        return domainRepository.getByStatus(status);
    }

    public List<Domain> getByFilter(DomainFilterEnum filterEnum) {
        if (Objects.nonNull(filterEnum)) {
            switch (filterEnum) {
                case ALL -> {
                    return domainRepository.getAll();
                }
                case TODAY -> {
                    return domainRepository.getTodayReleases();
                }
                case TOMORROW -> {
                    return domainRepository.getTomorrowReleases();
                }
                case AVAILABLE -> {
                    return domainRepository.getAvailable();
                }
                case TAKEN -> {
                    return domainRepository.getTaken();
                }
            }
        }
        return domainRepository.getTodayReleases();
    }
}
