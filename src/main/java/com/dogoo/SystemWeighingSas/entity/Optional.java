package com.dogoo.SystemWeighingSas.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.sql.Timestamp;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Entity(name = "DG_Optional")
@Data

@AllArgsConstructor
@NoArgsConstructor
public class Optional {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long optionalId;

    private Long cateId;

    private String name;

    private String description;

    private double cost;

    private int status;

    private Timestamp createDate = new Timestamp(System.currentTimeMillis());

}
