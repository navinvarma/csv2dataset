package com.opendata.csv2table.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.opendata"})
public class Csv2tableApplication {

    public static void main(String[] args) {
        SpringApplication.run(Csv2tableApplication.class, args);
    }

}
