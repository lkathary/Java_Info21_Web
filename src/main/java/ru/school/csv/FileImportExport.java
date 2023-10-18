package ru.school.csv;

import com.opencsv.CSVWriter;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import org.springframework.web.multipart.MultipartFile;


import javax.servlet.http.HttpServletResponse;
import java.io.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FileImportExport {

    public static <T> List<T> importCsv(MultipartFile multipartFile, Class<T> clazz) {
        try (Reader reader = new BufferedReader(new InputStreamReader(multipartFile.getInputStream()))) {

            CustomMappingStrategy<T> strategy = new CustomMappingStrategy<>();
            strategy.setType(clazz);

            CsvToBean<T> csvReader = new CsvToBeanBuilder<T>(reader)
                    .withType(clazz)
                    .withSeparator(',')
                    .withIgnoreLeadingWhiteSpace(true)
                    .withMappingStrategy(strategy)
                    .withSkipLines(1)
                    .withIgnoreEmptyLine(true)
                    .build();


            return csvReader.parse();

        } catch (IOException ex) {
            System.err.println(ex.getMessage());
            throw new RuntimeException(ex);
        }
    }

    public static <T> void exportCsv(HttpServletResponse servletResponse, List<T> data, Class<T> clazz) {

        servletResponse.setContentType("text/csv");
        servletResponse.addHeader("Content-Disposition", "attachment; filename=\"" + clazz.getName() + ".csv\"");

        try (Writer writer = servletResponse.getWriter()) {

            CustomMappingStrategy<T> strategy = new CustomMappingStrategy<>();
            strategy.setType(clazz);

            StatefulBeanToCsv<T> csvWriter = new StatefulBeanToCsvBuilder<T>(writer)
                    .withQuotechar(CSVWriter.NO_QUOTE_CHARACTER)
                    .withSeparator(CSVWriter.DEFAULT_SEPARATOR)
                    .withOrderedResults(true)
                    .withMappingStrategy(strategy)
                    .build();

            csvWriter.write(data);

        } catch (CsvDataTypeMismatchException | CsvRequiredFieldEmptyException | IOException ex) {
            System.err.println(ex.getMessage());
            throw new RuntimeException(ex);
        }
    }
    public static void resultExportCsv(HttpServletResponse servletResponse, Map<String, List<Object>> result) {
        servletResponse.setContentType("text/csv");
        servletResponse.addHeader("Content-Disposition", "attachment; filename=\"result.csv\"");

        try (PrintWriter writer = servletResponse.getWriter()) {
            StringBuilder keys = new StringBuilder();
            result.keySet().forEach(x -> keys.append(x).append(","));
            keys.deleteCharAt(keys.length() - 1);

            List<StringBuilder> values = new ArrayList<>();
            int size = result.values().stream().findAny().get().size();
            for(int i = 0; i < size; ++i) {
                values.add(new StringBuilder());
                for (List<Object> value : result.values()) {
                    if (value.get(i) != null) {
                        values.get(i).append(value.get(i).toString()).append(",");
                    } else {
                        values.get(i).append(",");
                    }
                }
                values.get(i).deleteCharAt(values.get(i).length() - 1);
            }
            writer.println(keys);
            values.forEach(writer::println);
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
            throw new RuntimeException(ex);
        }
    }
}
