package com.opendata.csvdb.csv2table.controller;

import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opendata.csvdb.csv2table.model.CsvModel;
import org.springframework.beans.factory.annotation.Value;
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
import java.util.ArrayList;
import java.util.List;


@Controller
public class FileUploadController {

    @Value("${app.preview.max.rows}")
    private int maxRows;

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
                String[] headers = csvReader.readNext();
                List<String> headerList = List.of(headers);
                List<String> headerIndexList = new ArrayList<>();
                for (int i = 0; i < headerList.size(); i++) {
                    String headerIndex = "column" + i;
                    headerIndexList.add(headerIndex);
                }

                // get column and row length
                int colLength = headers.length;

                // use column position mapping
                ColumnPositionMappingStrategy columnPositionMappingStrategy = new ColumnPositionMappingStrategy();
                columnPositionMappingStrategy.setType(CsvModel.class);


                // create csv bean reader and load data
                CsvToBean<CsvModel> csvToBean = new CsvToBeanBuilder(reader)
                        .withType(CsvModel.class)
                        .withMappingStrategy(columnPositionMappingStrategy)
                        .withIgnoreLeadingWhiteSpace(true)
                        .build();

                // convert `CsvToBean` object to list of model objects
                List<CsvModel> csvDataFull = csvToBean.parse();
                int rowLength = csvDataFull.size();

                // check whether row length < 500, if not set max preview to configured maxRows
                int toIndex = rowLength < maxRows ? rowLength : maxRows;

                // trim down rows for preview
                List<CsvModel> csvData = csvDataFull.subList(0, toIndex);

                // save users list on model
                model.addAttribute("csvData", csvData);
                model.addAttribute("headerList", headerList);
                model.addAttribute("headerIndexList", headerIndexList);
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
