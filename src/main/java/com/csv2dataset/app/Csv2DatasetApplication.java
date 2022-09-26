package com.csv2dataset.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.csv2dataset"})
public class Csv2DatasetApplication {

    public static void main(String[] args) {
        SpringApplication.run(Csv2DatasetApplication.class, args);
    }

}
