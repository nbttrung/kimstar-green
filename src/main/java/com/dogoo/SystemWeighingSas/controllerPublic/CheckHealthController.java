package com.dogoo.SystemWeighingSas.controllerPublic;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
@RequestMapping("/o/health-check-rest/v1.0/health")
public class CheckHealthController {

    @GetMapping()
    public void getCheckHealth() {

    }

}
