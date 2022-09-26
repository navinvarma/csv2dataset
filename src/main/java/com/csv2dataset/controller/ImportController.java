package com.csv2dataset.controller;

import com.csv2dataset.model.CsvModel;
import com.csv2dataset.model.ImportedDataset;
import com.csv2dataset.service.Csv2TableService;
import com.opencsv.CSVReader;
import org.apache.commons.io.ByteOrderMark;
import org.apache.commons.io.input.BOMInputStream;
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
import java.util.List;


@Controller
public class ImportController {

    @Autowired
    Csv2TableService csv2TableService;

    @Autowired
    ImportedDataset currentImportedDataset;

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/import-dataset")
    public String importDataset() {
        return "import-dataset";
    }

    @PostMapping("/process-dataset")
    public String processDataset(@RequestParam("datasetName") String datasetName,
                                 @RequestParam("datasetUrl") String datasetUrl,
                                 Model model) {
        try {
            // update imported dataset data
            currentImportedDataset.setDatasetName(datasetName);
            currentImportedDataset.setDatasetUrl(datasetUrl);
            // save dataset to Avro file
            String filePath = csv2TableService.parseDataToAvro(currentImportedDataset);
            currentImportedDataset.setFilePath(filePath);

            model.addAttribute("datasetName", currentImportedDataset.getDatasetName());
            model.addAttribute("columnHeaders", currentImportedDataset.getColumnHeaders());
            model.addAttribute("numColumns", currentImportedDataset.getNumColumns());
            model.addAttribute("numRows", currentImportedDataset.getNumRows());
            model.addAttribute("filePath", currentImportedDataset.getFilePath());
            model.addAttribute("status", true);
        } catch (Exception ex) {
            ex.printStackTrace();
            model.addAttribute("message", "An error occurred while processing the CSV file.");
            model.addAttribute("status", false);
        }

        return "processed-dataset";
    }

    @PostMapping("/preview-dataset")
    public String previewDataSet(@RequestParam("file") MultipartFile file, Model model) {

        // validate file
        if (file.isEmpty()) {
            model.addAttribute("message", "Please select a CSV file to upload.");
            model.addAttribute("status", false);
        } else {

            // parse CSV file
            try (Reader reader = new BufferedReader(
                    new InputStreamReader(new BOMInputStream(file.getInputStream(), false, ByteOrderMark.UTF_8,
                            ByteOrderMark.UTF_16BE, ByteOrderMark.UTF_16LE,
                            ByteOrderMark.UTF_32BE, ByteOrderMark.UTF_32LE)))) {

                String datasetName = file.getOriginalFilename();
                datasetName = datasetName.replaceAll(" ", "_");
                datasetName = datasetName.replaceAll("-", "_");

                // re-initialize bean
                currentImportedDataset = new ImportedDataset();

                // build list of headers to display
                CSVReader csvReader = new CSVReader(reader);
                List<String> columnHeaders = csv2TableService.parseHeaders(csvReader);
                List<String> genericColumnHeaders = csv2TableService.convertGenericColumnHeader(columnHeaders);

                // parse data
                List<CsvModel> csvData = csv2TableService.parseData(csvReader);

                // trim down to preview
                List<CsvModel> csvDataPreview = csv2TableService.parseDataToPreview(csvData);

                // get column and row length
                int numColumns = columnHeaders.size();
                int numRows = csvData.size();

                // save users list on model
                currentImportedDataset.setDatasetName(datasetName);
                currentImportedDataset.setCsvData(csvData);
                currentImportedDataset.setColumnHeaders(columnHeaders);
                currentImportedDataset.setNumColumns(numColumns);
                currentImportedDataset.setNumRows(numRows);
                model.addAttribute("datasetName", currentImportedDataset.getDatasetName());
                model.addAttribute("csvDataPreview", csvDataPreview);
                model.addAttribute("columnHeaders", currentImportedDataset.getColumnHeaders());
                model.addAttribute("genericColumnHeaders", genericColumnHeaders);
                model.addAttribute("numColumns", currentImportedDataset.getNumRows());
                model.addAttribute("numRows", currentImportedDataset.getNumColumns());
                model.addAttribute("status", true);

            } catch (Exception ex) {
                ex.printStackTrace();
                model.addAttribute("message", "An error occurred while processing the CSV file.");
                model.addAttribute("status", false);
            }
        }
        return "preview-dataset";
    }
}
