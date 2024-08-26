package com.dogoo.SystemWeighingSas.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class WeightSlipUi {

    private Long weightSlipId;
    private String maPhieu = "";
    private String khachHang = "";
    private Timestamp ngayCan;
    private String tenHang = "";
    private String soXe = "";
    @JsonProperty("hang")
    private long hang;
    private Timestamp gioKTai;
    private Long thanhTien;
    private String donGia;
    private Boolean permission;

}
