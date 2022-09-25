package com.opendata.csv2table.controller;

import com.opendata.csv2table.service.Csv2TableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;

@Controller
public class ManageController {
    @Autowired
    Csv2TableService csv2TableService;

    @Value("${app.avro.file.path}")
    private String basePath;

    @GetMapping("/manage-dataset")
    public String manageDataset() throws IOException {

        return "manage-dataset";

    }
}
