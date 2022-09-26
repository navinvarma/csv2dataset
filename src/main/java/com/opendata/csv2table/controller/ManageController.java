package com.opendata.csv2table.controller;

import com.opendata.csv2table.service.Csv2TableService;
import org.apache.avro.Schema;
import org.apache.avro.file.DataFileReader;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.DatumReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class ManageController {
    @Autowired
    Csv2TableService csv2TableService;

    @Value("${app.avro.file.path}")
    private String basePath;

    @GetMapping("/manage-dataset")
    public String manageDataset(Model model) throws IOException {
        File folder = new File(basePath);
        String[] files = folder.list();

        model.addAttribute("files", files);

        return "manage-dataset";

    }

    @PostMapping("/manage-dataset")
    public String manageDataset(@RequestParam("datasetSelected") String datasetSelected, Model model) throws IOException {
        try {
            File folder = new File(basePath);
            String[] files = folder.list();
            File selectedFile = new File(basePath + datasetSelected);

            DatumReader<GenericRecord> datumReader = new GenericDatumReader<>();
            DataFileReader<GenericRecord> dataFileReader = new DataFileReader<>(selectedFile, datumReader);
            Schema schema = dataFileReader.getSchema();
            Map<String, Object> props = schema.getObjectProps();
            Integer numRows = (Integer) props.get("numRows");
            Integer numColumns = (Integer) props.get("numColumns");
            List<String> columnHeaders = (List<String>) props.get("columnHeaders");
            List<GenericRecord> rows = new ArrayList<>();

            while (dataFileReader.iterator().hasNext()) {
                GenericRecord record = dataFileReader.iterator().next();
                rows.add(record);
            }

            model.addAttribute("files", files);
            model.addAttribute("datasetSelected", datasetSelected);
            model.addAttribute("columnHeaders", columnHeaders);
            model.addAttribute("numColumns", numColumns);
            model.addAttribute("numRows", numRows);
            model.addAttribute("rows", rows);
            model.addAttribute("status", true);

        } catch (Exception ex) {
            ex.printStackTrace();
            model.addAttribute("message", "An error occurred while processing the Avro file.");
            model.addAttribute("status", false);
        }
        return "manage-dataset";

    }
}
