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
@Entity(name = "DG_Report")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String ListMaPhieu;
    private String name;
    private String databaseKey;
    private String key;
    private Timestamp createDate = new Timestamp(System.currentTimeMillis());

    private Timestamp startDate = new Timestamp(System.currentTimeMillis());
    private Timestamp endDate = new Timestamp(System.currentTimeMillis());

    private Long accountId;

}
