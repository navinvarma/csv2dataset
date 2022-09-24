package com.opendata.csvdb.csv2table.service;

import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.exceptions.CsvValidationException;
import com.opendata.csvdb.csv2table.model.CsvModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class Csv2TableService {

    @Value("${app.preview.max.rows}")
    private int maxRows;

    public List<String> parseHeaders(CSVReader csvReader) throws CsvValidationException, IOException {
        String[] headers = csvReader.readNext();
        List<String> headerList = List.of(headers);

        return headerList;
    }

    public List<String> convertGenericColumnHeader(List<String> headerList) {
        List<String> genericHeaderList = new ArrayList<>();
        for (int i = 0; i < headerList.size(); i++) {
            String headerIndex = "column" + i;
            genericHeaderList.add(headerIndex);
        }
        return genericHeaderList;
    }

    public List<CsvModel> parseData(CSVReader csvReader) {
        // use column position mapping
        ColumnPositionMappingStrategy columnPositionMappingStrategy = new ColumnPositionMappingStrategy();
        columnPositionMappingStrategy.setType(CsvModel.class);


        // create csv bean reader and load data
        CsvToBean<CsvModel> csvToBean = new CsvToBeanBuilder(csvReader)
                .withType(CsvModel.class)
                .withMappingStrategy(columnPositionMappingStrategy)
                .withIgnoreLeadingWhiteSpace(true)
                .build();

        // convert `CsvToBean` object to list of model objects
        List<CsvModel> csvDataFull = csvToBean.parse();

        return csvDataFull;
    }

    public List<CsvModel> parseDataToPreview(List<CsvModel> csvData) {
        // get number of rows
        int rowLength = csvData.size();

        // check whether row length < 500, if not set max preview to configured maxRows
        int toIndex = rowLength < maxRows ? rowLength : maxRows;

        // trim down rows for preview
        List<CsvModel> csvDataPreview = csvData.subList(0, toIndex);

        return csvDataPreview;
    }
}
