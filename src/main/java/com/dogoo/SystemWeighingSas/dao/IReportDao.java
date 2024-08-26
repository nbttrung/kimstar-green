package com.dogoo.SystemWeighingSas.dao;

import com.dogoo.SystemWeighingSas.entity.Report;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface IReportDao extends JpaRepository<Report,Long> {

    Report findAllById(long id);

    @Query("SELECT r FROM DG_Report r WHERE r.key = ?1 AND r.databaseKey LIKE %?2%")
    Page<Report> findAllByKeyAndDatabaseKey(String key, String databaseKey, Pageable pageable);

    @Query("SELECT COUNT(r) FROM DG_Report r WHERE r.key = ?1 AND upper(r.databaseKey) LIKE %?2%")
    long countByKeyAndDatabaseKey(String key, String databaseKey);
}
