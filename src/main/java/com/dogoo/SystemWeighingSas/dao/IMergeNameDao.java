package com.dogoo.SystemWeighingSas.dao;

import com.dogoo.SystemWeighingSas.entity.MergeName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface IMergeNameDao extends JpaRepository<MergeName, Long> {

    @Query("select ac from DG_mergeName ac Where ac.name=:name AND ac.databaseKey=:databaseKey AND ac.reportId=:reportId ")
    MergeName findAllByNameAndDatabaseKey(@Param("name") String name,
                                          @Param("databaseKey") String databaseKey ,
                                          @Param("reportId") Long reportId);

    @Query("select ac from DG_mergeName ac Where ( ac.listKhachHang LIKE %?1% OR ac.listKhachHang LIKE %?2% ) " +
            " AND ac.databaseKey=?3 AND ac.reportId=?4 ORDER BY createDate DESC ")
    List<MergeName> findAllByLikeListKh(String kH1 , String kH2 ,String databaseKey, Long reportId );


}
