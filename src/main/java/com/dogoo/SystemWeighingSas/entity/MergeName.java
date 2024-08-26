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
@Entity(name = "DG_mergeName")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MergeName {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

//    @Column(columnDefinition = "TEXT String default \"\" ")
    private String listKhachHang;

    private String databaseKey;

    private Timestamp createDate = new Timestamp(System.currentTimeMillis());

    private Long reportId ;

}
