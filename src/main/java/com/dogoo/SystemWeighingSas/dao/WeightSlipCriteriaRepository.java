package com.dogoo.SystemWeighingSas.dao;

import com.dogoo.SystemWeighingSas.config.Constants;
import com.dogoo.SystemWeighingSas.entity.WeightSlip;
import com.dogoo.SystemWeighingSas.mapper.WeightMapper;
import com.dogoo.SystemWeighingSas.model.WeightSlipCriteria;
import com.dogoo.SystemWeighingSas.model.WeightSlipUi;
import com.dogoo.SystemWeighingSas.service.RoleService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Repository
public class WeightSlipCriteriaRepository {

    private final EntityManager entityManager;
    private final CriteriaBuilder criteriaBuilder;
    private final RoleService roleService;

    private final WeightMapper mapper;

    public WeightSlipCriteriaRepository(EntityManager entityManager,
                                        RoleService roleService,
                                        WeightMapper mapper) {
        this.entityManager = entityManager;
        this.criteriaBuilder = entityManager.getCriteriaBuilder();
        this.roleService = roleService;
        this.mapper = mapper;
    }

    public Page<WeightSlip> findAllWithFilters(Integer limit,
                                               Integer page,
                                               WeightSlipCriteria weightSlipCriteria) {
        CriteriaQuery<WeightSlip> criteriaQuery = criteriaBuilder.createQuery(WeightSlip.class);
        Root<WeightSlip> weightSlipRoot = criteriaQuery.from(WeightSlip.class);
        Predicate predicate = getPredicate(weightSlipCriteria, weightSlipRoot);
        criteriaQuery.where(predicate);
        setOrder(criteriaQuery, weightSlipRoot);

        TypedQuery<WeightSlip> typedQuery = entityManager.createQuery(criteriaQuery);
        typedQuery.setFirstResult(page * limit);
        typedQuery.setMaxResults(limit);

        Pageable pageable = PageRequest.of(page, limit);

        long employeesCount = getEmployeesCount(predicate);

        return new PageImpl<>(typedQuery.getResultList(), pageable, employeesCount);
    }

    public Page<WeightSlipUi> findAllWithFiltersUi(long accountId, String code, Integer limit,
                                                   Integer page,
                                                   WeightSlipCriteria weightSlipCriteria) {
        CriteriaQuery<WeightSlip> criteriaQuery = criteriaBuilder.createQuery(WeightSlip.class);
        Root<WeightSlip> weightSlipRoot = criteriaQuery.from(WeightSlip.class);
        Predicate predicate = getPredicate(weightSlipCriteria, weightSlipRoot);
        criteriaQuery.where(predicate);
        setOrder(criteriaQuery, weightSlipRoot);

        TypedQuery<WeightSlip> typedQuery = entityManager.createQuery(criteriaQuery);
        typedQuery.setFirstResult(page * limit);
        typedQuery.setMaxResults(limit);

        Pageable pageable = PageRequest.of(page, limit);

        long employeesCount = getEmployeesCount(predicate);
        boolean check = roleService.checkRoleCreate(accountId, code);
        List<WeightSlipUi> weightSlipList = typedQuery.getResultList().stream()
                .map(weightSlip -> {
                    return mapper.mapWeightSlipUi(check, getSoxe(weightSlip.getSoXe()), weightSlip);
                })
                .collect(Collectors.toList());

        return new PageImpl<>(weightSlipList, pageable, employeesCount);
    }

    private Predicate getPredicate(WeightSlipCriteria weightSlipCriteria,
                                   Root<WeightSlip> weightSlipRoot) {
        List<Predicate> predicates = new ArrayList<>();

        Predicate predicateAction
                = criteriaBuilder.equal(weightSlipRoot.get("deleted"), Boolean.FALSE);
        predicates.add(criteriaBuilder.or(predicateAction));
        if (Objects.nonNull(weightSlipCriteria.getDatabaseKey())) {
            predicates.add(
                    criteriaBuilder.like(weightSlipRoot.get("databaseKey"),
                            weightSlipCriteria.getDatabaseKey())
            );
        }

        if (Objects.nonNull(weightSlipCriteria.getSearch()) &&
                !weightSlipCriteria.getSearch().equals("")) {
            Predicate predicateKhachHang
                    = criteriaBuilder.like(criteriaBuilder.upper(weightSlipRoot.get("khachHang")),
                    '%' + weightSlipCriteria.getSearch().toUpperCase() + '%');
            Predicate predicateTenHang
                    = criteriaBuilder.like(criteriaBuilder.upper(weightSlipRoot.get("tenHang")),
                    '%' + weightSlipCriteria.getSearch() + '%');
            predicates.add(criteriaBuilder.or(predicateTenHang, predicateKhachHang));
        }

        if (Objects.nonNull(weightSlipCriteria.getSoXe())) {
            List<Predicate> list = new ArrayList<>();
            weightSlipCriteria.getSoXe().stream().filter(s -> !s.equals("")).forEach(s -> {
                Predicate predicateForGradeA
                        = criteriaBuilder.equal(weightSlipRoot.get("soXe"), s);
                list.add(predicateForGradeA);
            });

            if (!list.isEmpty())
                predicates.add(criteriaBuilder.or(list.toArray(new Predicate[0])));
        }
        if (Objects.nonNull(weightSlipCriteria.getTenHang())) {
            List<Predicate> list = new ArrayList<>();
            weightSlipCriteria.getTenHang().stream().filter(s -> !s.equals("")).forEach(s -> {
                Predicate predicateForGradeA
                        = criteriaBuilder.equal(weightSlipRoot.get("tenHang"), s);
                list.add(predicateForGradeA);
            });

            if (!list.isEmpty())
                predicates.add(criteriaBuilder.or(list.toArray(new Predicate[0])));
        }
        if (Objects.nonNull(weightSlipCriteria.getKhachHang())) {
            List<Predicate> list = new ArrayList<>();
            weightSlipCriteria.getKhachHang().stream().filter(s -> !s.equals("")).forEach(s -> {
                Predicate predicateForGradeA
                        = criteriaBuilder.equal(weightSlipRoot.get("khachHang"), s);
                list.add(predicateForGradeA);
            });

            if (!list.isEmpty())
                predicates.add(criteriaBuilder.or(list.toArray(new Predicate[0])));
        }

        if (weightSlipCriteria.getStartDate() != null && weightSlipCriteria.getEndDate() != null) {
            LocalDateTime startDate = weightSlipCriteria.getStartDate().withHour(0).withMinute(0).withSecond(0).withNano(0);
            LocalDateTime endDate = weightSlipCriteria.getEndDate().withHour(23).withMinute(59).withSecond(59).withNano(0);
            predicates.add(
                    criteriaBuilder.between(weightSlipRoot.get("ngayCan"),
                            Timestamp.valueOf(startDate), Timestamp.valueOf(endDate))
            );
        }

        if (weightSlipCriteria.getStartDate() != null && weightSlipCriteria.getEndDate() == null) {
            LocalDateTime startDate = weightSlipCriteria.getStartDate().withHour(0).withMinute(0).withSecond(0).withNano(0);
            predicates.add(
                    criteriaBuilder.greaterThanOrEqualTo(weightSlipRoot.get("ngayCan"),
                            Timestamp.valueOf(startDate))
            );
        }

        if (weightSlipCriteria.getStartDate() == null && weightSlipCriteria.getEndDate() != null) {
            LocalDateTime endDate = weightSlipCriteria.getEndDate().withHour(23).withMinute(59).withSecond(59).withNano(0);
            predicates.add(
                    criteriaBuilder.lessThanOrEqualTo(weightSlipRoot.get("ngayCan"),
                            Timestamp.valueOf(endDate))
            );
        }

        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }

    private void setOrder(CriteriaQuery<WeightSlip> criteriaQuery,
                          Root<WeightSlip> weightSlipRoot) {
        criteriaQuery.orderBy(criteriaBuilder.desc(weightSlipRoot.get("ngayCan")));
    }

    private long getEmployeesCount(Predicate predicate) {
        CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);
        Root<WeightSlip> countRoot = countQuery.from(WeightSlip.class);
        countQuery.select(criteriaBuilder.count(countRoot)).where(predicate);
        return entityManager.createQuery(countQuery).getSingleResult();
    }

    private String getSoxe(String soxe) {

        soxe = soxe.replace("-", "");
        String[] path = soxe.split("[a-zA-z]");

        if (path.length == 2) {
            String[] character = soxe.split("\\d");
            return path[0] + character[2].trim().toUpperCase() + "-" + path[1].trim();
        }

        return soxe;
    }
}
