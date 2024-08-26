package com.dogoo.SystemWeighingSas.config;

import com.dogoo.SystemWeighingSas.entity.WeightSlip;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

public class Constants {
    public static ObjectMapper SERIALIZER = new ObjectMapper();

    public static final String ACTION_CREATE = "create";
    public static final String ACTION_UPDATE = "update";
    public static final String ACTION_DELETE = "delete";
    public static final String ACTION_DELETE_TEMP = "deleteTemp";

    public static List<Future<List<WeightSlip>>> jobSubmit = new ArrayList<>();

    public static int size = 100;

    public static final List<String> arrayPathAdmin = List.of("overview-seller" , "weighing-station" ,
            "business-commissions" , "download" , "customer");
    public static final List<String> arrayPathAdminUser = List.of("overview-weighing-station" , "account" ,
            "subscription-package" , "report", "weighing-slip", "report-weighing-station");
}
