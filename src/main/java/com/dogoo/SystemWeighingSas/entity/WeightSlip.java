package com.dogoo.SystemWeighingSas.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Entity(name = "DG_WeightSlip")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WeightSlip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long weightSlipId;

    
    private String key = "";
    
    private String databaseKey = "";
    
    private String action = "";
    
    private String maPhieu = "";
    
    private String khachHang = "";
    
    private String tenHang = "";
    
    private String maHang = "";
    
    private String maKH = "";
    
    private String soXe = "";

    /*@Column(name = "kl_co_tai")
    private long coTai;
    @Column(name = "kl_k_tai")
    private long tareWeight;
    @Column(name = "kl_hang")
    private long hang;*/

    private String canKTai;
    private String canCoTai;
    private String weight;

    private Timestamp ngayCan;
//    @Column(columnDefinition = "TIMESTAMP")
//    private LocalDateTime ngayCan;
    private Timestamp gioCoTai;
    private Timestamp gioKTai;
    
    private String ghiChu = "";
    
    private String xN = "";

    private String urlCoTai1 = "";
    
    private String urlCoTai2 = "";
    
    private String urlKoTai1 = "";
    
    private String urlKoTai2 = "";
    
//    private String chietKhau = "";
    private String discount = "";

//    private String donGia = "";
    private String unitPrice = "";

//    private String sauCK = "";
    private String afterDiscount = "";

//    private Long thanhTien;
    private String intoMoney = "";

//    private Long shipFee = 0L;
    private String transportFee = "";

    @Column(columnDefinition = "boolean default false")
    private Boolean deleted = false;
    @Column(columnDefinition = "boolean default false")
    private Boolean handVote = false;

}
