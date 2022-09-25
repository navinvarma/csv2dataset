package com.opendata.csv2table.service;

import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.exceptions.CsvValidationException;
import com.opendata.csv2table.model.CsvModel;
import com.opendata.csv2table.model.ImportedDataset;
import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.apache.avro.file.CodecFactory;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class Csv2TableService {

    @Value("${app.preview.max.rows}")
    private int maxRows;

    @Value("${app.avro.file.path}")
    private String basePath;

    public List<String> parseHeaders(CSVReader csvReader) {
        String[] headers = new String[0];
        try {
            headers = csvReader.readNext();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (CsvValidationException e) {
            throw new RuntimeException(e);
        }
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

    public String parseDataToAvro(ImportedDataset currentImportedDataset) {
        try {
            String datasetName = currentImportedDataset.getDatasetName();
            List<String> columnHeaders = currentImportedDataset.getColumnHeaders();
            List<String> genericColumnHeaders = convertGenericColumnHeader(columnHeaders);
            List<CsvModel> csvDataFull = currentImportedDataset.getCsvData();

            SchemaBuilder.FieldAssembler<Schema> fields = SchemaBuilder
                    .record(datasetName).fields();
            for (int i = 0; i < genericColumnHeaders.size(); i++) {
                String columnHeader = String.valueOf(columnHeaders.get(i));
                fields.optionalString(columnHeader);
            }
            Schema finalSchema = fields.endRecord();
            finalSchema.addProp("numRows", currentImportedDataset.getNumRows());
            finalSchema.addProp("numColumns", currentImportedDataset.getNumColumns());
            finalSchema.addProp("columnHeaders", currentImportedDataset.getColumnHeaders());

            File file = new File(basePath + datasetName + ".avro");
            GenericDatumWriter<GenericRecord> genericDatumWriter = new GenericDatumWriter<>(finalSchema);
            DataFileWriter<GenericRecord> dataFileWriter = new DataFileWriter<>(genericDatumWriter);
            dataFileWriter.setCodec(CodecFactory.deflateCodec(9));
            dataFileWriter.create(finalSchema, file);

            for (int i = 0; i < csvDataFull.size(); i++) {
                GenericData.Record record = new GenericData.Record(finalSchema);
                for (int j = 0; j < genericColumnHeaders.size(); j++) {
                    String genericColumnHeader = genericColumnHeaders.get(j);
                    String columnHeader = columnHeaders.get(j);
                    record.put(columnHeader, csvDataFull.get(i).retrieveColumnValue(genericColumnHeader));
                }
                dataFileWriter.append(record);
            }

            dataFileWriter.close();
            return file.getPath();
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
