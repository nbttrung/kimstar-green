package com.dogoo.SystemWeighingSas.dao;

import com.dogoo.SystemWeighingSas.entity.WeightSlip;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.util.List;

public interface IWeightSlipDao extends JpaRepository<WeightSlip, Long> {

    WeightSlip findByWeightSlipId(long weightSlipId);

    @Query("SELECT COUNT(weight_slip_id) FROM DG_WeightSlip Where databaseKey = :databaseKey AND handVote = 'false' ")
    Integer getCountWeightSlip(@Param("databaseKey") String code);

    @Query("select ws from DG_WeightSlip ws Where ws.key=:key AND ws.databaseKey=:databaseKey AND ws.handVote = 'false' ")
    List<WeightSlip> getAllWeightSlipByKeyAndDatabase(@Param("key") String key,
                                                      @Param("databaseKey") String databaseKey);

    @Query("select ws from DG_WeightSlip ws Where ws.maPhieu=:maPhieu AND ws.databaseKey = :databaseKey")
    WeightSlip getWeightSlipByMaPhieu(@Param("maPhieu") String maPhieu, @Param("databaseKey") String databaseKey);

    @Query(" DELETE FROM DG_WeightSlip WHERE maPhieu=:maPhieu ")
    void deleteWeightSlipByMaPhieu(@Param("maPhieu") String maPhieu);

    @Modifying
    @Transactional
    @Query(" DELETE FROM DG_WeightSlip ws WHERE ws.maPhieu in(:strings) AND ws.databaseKey = :databaseKey AND ws.deleted = 'true' ")
    void deleteWeightSlipByMaPhieus(@Param("strings") List<String> strings,
                                    @Param("databaseKey") String databaseKey);

    @Modifying
    @Transactional
    @Query("update DG_WeightSlip u set u.action = 'deleteTemp' " +
            " where u.maPhieu in(:strings) AND u.databaseKey = :databaseKey AND u.deleted = 'false' AND u.action = 'delete' ")
    void updateWhenDeleteWeightSlipTemp(@Param("strings") List<String> strings,
                                        @Param("databaseKey") String databaseKey);

    @Modifying
    @Transactional
    @Query("update DG_WeightSlip u set u.deleted = :deleted, u.action = :action " +
            " where u.weightSlipId in(:ids) AND u.handVote='false' ")
    void updateWhenDeleteWeightSlipCloudMany(@Param("ids") List<Long> ids,
                                             @Param("action") String action,
                                             @Param("deleted") boolean deleted);

    @Modifying
    @Transactional
    @Query("update DG_WeightSlip u set u.deleted = :deleted, u.action = :action " +
            " where u.weightSlipId = :id AND u.handVote='false' ")
    void updateWhenDeleteWeightSlipCloudOne(@Param("id") Long id,
                                            @Param("action") String action,
                                            @Param("deleted") boolean deleted);

    @Modifying
    @Transactional
    @Query(" DELETE FROM DG_WeightSlip ws WHERE ws.weightSlipId in(:longs) AND ws.handVote='true' ")
    void deleteWeightSlipByIds(@Param("longs") List<Long> longs);

//    @Modifying
//    @Query("update DG_WeightSlip u set u.khachHang=:khachHang, u.tenHang=:tenHang," +
//            " u.maHang=:maHang, u.maKH=:maKH, u.soXe=:soXe, u.coTai=:coTai, u.tareWeight=:tareWeight," +
//            " u.hang=:hang, u.ngayCan=:ngayCan, u.gioCoTai=:gioCoTai, u.gioKTai=gioKTai, u.ghiChu=:ghiChu," +
//            " u.xN=:xN, u.urlCoTai1=:urlCoTai1, u.urlCoTai2=:urlCoTai2, u.urlKoTai1=:urlKoTai1, u.urlKoTai2=:urlKoTai2," +
//            " u.chietKhau=:chietKhau, u.donGia=:donGia, u.sauCK=:sauCK, u.thanhTien=:thanhTien where u.maPhieu=:maPhieu")
//    void updateWeightSlipByMaPhieu(@Param("khachHang") String khachHang, @Param("tenHang") String tenHang ,
//                                   @Param("maHang") String maHang, @Param("maKH") String maKH,
//                                   @Param("soXe") String soXe, @Param("coTai") long coTai,
//                                   @Param("tareWeight") long tareWeight, @Param("hang") long hang,
//                                   @Param("ngayCan") Timestamp ngayCan, @Param("gioCoTai") Timestamp gioCoTai,
//                                   @Param("gioKTai") Timestamp gioKTai, @Param("ghiChu") String ghiChu,
//                                   @Param("xN") String xN, @Param("urlCoTai1") String urlCoTai1,
//                                   @Param("urlCoTai2") String urlCoTai2, @Param("urlKoTai1") String urlKoTai1,
//                                   @Param("urlKoTai2") String urlKoTai2, @Param("chietKhau") String chietKhau,
//                                   @Param("donGia") String donGia, @Param("sauCK") String sauCK,
//                                   @Param("thanhTien") Long thanhTien , @Param("maPhieu") String maPhieu);

    @Query("select ws from DG_WeightSlip ws where ws.databaseKey = :databaseKey and ws.handVote='false' ORDER BY ws.ngayCan DESC, ws.gioKTai DESC ")
    List<WeightSlip> getAllWeightSlipOrder(@Param("databaseKey") String databaseKey);

    @Query("select ws from DG_WeightSlip ws where databaseKey = :databaseKey AND ws.action='delete' AND ws.handVote='false' ")
    List<WeightSlip> getAllWeightSlipAction(@Param("databaseKey") String databaseKey);

    Page<WeightSlip> findAllByDatabaseKey(String databaseKey, Pageable pageable);

    long countByDatabaseKey(String databaseKey);

    @Query(value = "SELECT COUNT(t) from DG_WeightSlip t where t.ngayCan BETWEEN :startDate AND :endDate " +
            " AND t.databaseKey = :databaseKey AND action != 'delete' ")
    long countNgayCan(
            @Param("startDate") Timestamp startDate,
            @Param("endDate") Timestamp endDate,
            @Param("databaseKey") String databaseKey);

    @Query(value = "SELECT t from DG_WeightSlip t where t.ngayCan BETWEEN :startDate AND :endDate " +
            " AND t.databaseKey = :databaseKey AND action != 'delete' ")
    List<WeightSlip> sumWeight(
            @Param("startDate") Timestamp startDate,
            @Param("endDate") Timestamp endDate,
            @Param("databaseKey") String databaseKey);

    @Query(value = "SELECT t from DG_WeightSlip t where t.ngayCan BETWEEN :startDate AND :endDate " +
            " AND t.databaseKey = :databaseKey AND action != 'delete' ")
    List<WeightSlip> sumRevenue(
            @Param("startDate") Timestamp startDate,
            @Param("endDate") Timestamp endDate,
            @Param("databaseKey") String databaseKey);

    @Query(value = "SELECT COUNT(DISTINCT t.khachHang) from DG_WeightSlip t where t.ngayCan BETWEEN :startDate AND :endDate " +
            " AND t.databaseKey = :databaseKey AND action != 'delete' ")
    long countKh(
            @Param("startDate") Timestamp startDate,
            @Param("endDate") Timestamp endDate,
            @Param("databaseKey") String databaseKey);

    @Query("SELECT DISTINCT soXe FROM DG_WeightSlip WHERE upper(soXe) LIKE %?1% AND databaseKey = ?2 ")
    List<String> findSoXeDistinct(String soxe, String databaseKey);

    @Query("SELECT DISTINCT tenHang FROM DG_WeightSlip WHERE databaseKey = ?1 and upper(tenHang) LIKE %?2%")
    List<String> findTenHangDistinct(String databaseKey, String tenHang);

    @Query("SELECT DISTINCT khachHang FROM DG_WeightSlip WHERE databaseKey = ?1 ")
    List<String> findKhachHangDistinct(String databaseKey);

    List<WeightSlip> findByKhachHangContainingAllIgnoreCase(String khachHang);

    void deleteByMaPhieu(String maPhieu);

    @Query("select ws from DG_WeightSlip ws WHERE ws.weightSlipId in(:strings) AND ws.databaseKey = :databaseKey")
    List<WeightSlip> findAllByMaPhieuIds(@Param("strings") List<Long> strings,
                                         @Param("databaseKey") String databaseKey);

}
