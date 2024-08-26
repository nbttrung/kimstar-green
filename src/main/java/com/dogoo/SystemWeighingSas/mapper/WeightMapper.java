package com.dogoo.SystemWeighingSas.mapper;

import com.dogoo.SystemWeighingSas.config.Constants;
import com.dogoo.SystemWeighingSas.encyptDecrypt.EncryptDecryptService;
import com.dogoo.SystemWeighingSas.entity.WeightSlip;
import com.dogoo.SystemWeighingSas.model.WeightSlipUi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
@Slf4j
public class WeightMapper {

    private final EncryptDecryptService encryptDecryptService;

    public WeightMapper(EncryptDecryptService encryptDecryptService) {
        this.encryptDecryptService = encryptDecryptService;
    }

    public WeightSlipUi mapWeightSlipUi(boolean check , String soXe, WeightSlip weightSlip){
        WeightSlipUi to = Constants.SERIALIZER.convertValue(weightSlip, WeightSlipUi.class);
        to.setPermission(check);
        to.setSoXe(soXe);
        to.setHang(Long.parseLong(getDecrypt(weightSlip.getWeight())));
        to.setDonGia(getDecrypt(weightSlip.getUnitPrice()));
        to.setThanhTien(Long.parseLong(getDecrypt(weightSlip.getIntoMoney())));
        return to;
    }

    public String getDecrypt(String input){
        try {
            return encryptDecryptService.decrypt(input);

        }catch (Exception ex) {
            log.error(ex.getMessage());
        }

        return "";
    }
}
