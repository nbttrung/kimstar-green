package com.dogoo.SystemWeighingSas.entity;

import com.dogoo.SystemWeighingSas.enumEntity.TypeEnum;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Entity(name = "DG_ProductCategory")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cateId;

    private String name;

    private String description;

    @Enumerated(EnumType.STRING)
    private TypeEnum type;

    private int status;

    private Timestamp createDate = new Timestamp(System.currentTimeMillis());

}
