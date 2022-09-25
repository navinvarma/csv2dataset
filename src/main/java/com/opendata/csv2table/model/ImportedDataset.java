package com.opendata.csv2table.model;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ImportedDataset {
    private String datasetName;
    private String datasetUrl;
    private List<CsvModel> csvData;
    private List<String> columnHeaders;
    private int numColumns;
    private int numRows;

    public int getNumColumns() {
        return numColumns;
    }

    public void setNumColumns(int numColumns) {
        this.numColumns = numColumns;
    }

    public int getNumRows() {
        return numRows;
    }

    public void setNumRows(int numRows) {
        this.numRows = numRows;
    }

    public String getDatasetName() {
        return datasetName;
    }

    public void setDatasetName(String datasetName) {
        this.datasetName = datasetName;
    }

    public String getDatasetUrl() {
        return datasetUrl;
    }

    public void setDatasetUrl(String datasetUrl) {
        this.datasetUrl = datasetUrl;
    }

    public List<CsvModel> getCsvData() {
        return csvData;
    }

    public void setCsvData(List<CsvModel> csvData) {
        this.csvData = csvData;
    }

    public List<String> getColumnHeaders() {
        return columnHeaders;
    }

    public void setColumnHeaders(List<String> columnHeaders) {
        this.columnHeaders = columnHeaders;
    }
}
