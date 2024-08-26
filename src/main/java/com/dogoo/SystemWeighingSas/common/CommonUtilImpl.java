package com.dogoo.SystemWeighingSas.common;

import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.regex.Pattern;

@Service
public class CommonUtilImpl {

    public String vnNormalize(String value) {
        char d_VN = '\u0111';
        char D_VN = '\u0110';

        if (value == null) {
            return "";
        }

        try {
            String temp = Normalizer.normalize(value, Normalizer.Form.NFD);
            Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
            return pattern.matcher(temp)
                    .replaceAll("")
                    .replace(d_VN,'d')
                    .replace(D_VN,'D');

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return "";
    }
}
