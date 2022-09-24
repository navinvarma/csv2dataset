package com.opendata.csvdb.csv2table.controller;

import com.opencsv.CSVReader;
import com.opendata.csvdb.csv2table.model.CsvModel;
import com.opendata.csvdb.csv2table.service.Csv2TableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.List;


@Controller
public class ImportController {

    @Autowired
    Csv2TableService csv2TableService;

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/import-csv")
    public String importCSV() {
        return "import-csv";
    }

    @GetMapping("/manage-csv")
    public String manageCSV() {
        return "manage-csv";
    }

    @PostMapping("/preview-csv")
    public String previewCSV(@RequestParam("file") MultipartFile file, Model model) {

        // validate file
        if (file.isEmpty()) {
            model.addAttribute("message", "Please select a CSV file to upload.");
            model.addAttribute("status", false);
        } else {

            // parse CSV file
            try (Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {

                // build list of headers to display
                CSVReader csvReader = new CSVReader(reader);
                List<String> columnHeaders = csv2TableService.parseHeaders(csvReader);
                List<String> genericColumnHeaders = csv2TableService.convertGenericColumnHeader(columnHeaders);

                // parse data
                List<CsvModel> csvData = csv2TableService.parseData(csvReader);

                // trim down to preview
                List<CsvModel> csvDataPreview = csv2TableService.parseDataToPreview(csvData);

                // get column and row length
                int colLength = columnHeaders.size();
                int rowLength = csvData.size();

                // save users list on model
                model.addAttribute("csvDataPreview", csvDataPreview);
                model.addAttribute("columnHeaders", columnHeaders);
                model.addAttribute("genericColumnHeaders", genericColumnHeaders);
                model.addAttribute("colLength", colLength);
                model.addAttribute("rowLength", rowLength);
                model.addAttribute("status", true);

            } catch (Exception ex) {
                ex.printStackTrace();
                model.addAttribute("message", "An error occurred while processing the CSV file.");
                model.addAttribute("status", false);
            }
        }

        return "preview-csv";
    }
}
