package com.opendata.csvdb.csv2table.service;

import com.opendata.csvdb.csv2table.model.CsvModel;

import java.util.HashMap;
import java.util.List;

public class Csv2TableService {

    public HashMap<Long, HashMap<String, String>> convertCsvModelToMap(List<String> headerList, List<CsvModel> csvData) {
        HashMap<Long, HashMap<String, String>> returnMap = new HashMap<>();
        HashMap<String, String> keyVals = new HashMap<>();
        long rowId = 0;


        return returnMap;
    }
}
