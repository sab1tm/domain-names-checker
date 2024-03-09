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

    public List<Domain> getLimitTodayReleases(int limit) {
        return domainRepository.getLimitTodayReleases(limit);
    }

    public List<Domain> getLimitOldTodayReleases(int limit) {
        return domainRepository.getLimitOldTodayReleases(limit);
    }

    public List<Domain> getByStatus(DomainStatusEnum status) {
        return domainRepository.getByStatus(status);
    }

    public List<Domain> getByFilter(DomainFilterEnum filterEnum) {
        if (Objects.nonNull(filterEnum)) {
            switch (filterEnum) {
                case ACTUAL -> {
                    return domainRepository.getAll();
                }
                case TODAY -> {
                    return domainRepository.getTodayReleases();
                }
                case TOMORROW -> {
                    return domainRepository.getTomorrowReleases();
                }
                case AVAILABLE -> {
                    return domainRepository.getByStatus(DomainStatusEnum.AVAILABLE);
                }
                case TAKEN -> {
                    return domainRepository.getByStatus(DomainStatusEnum.TAKEN);
                }
                case FAVORITE -> {
                    return domainRepository.getFavorite();
                }
            }
        }
        return domainRepository.getTodayReleases();
    }

    public void setFavorite(String name) {
        domainRepository.setFavorite(name);
    }
}
