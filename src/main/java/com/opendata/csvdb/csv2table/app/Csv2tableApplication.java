package com.opendata.csvdb.csv2table.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.opendata.csvdb"})
public class Csv2tableApplication {

    public static void main(String[] args) {
        SpringApplication.run(Csv2tableApplication.class, args);
    }

}
