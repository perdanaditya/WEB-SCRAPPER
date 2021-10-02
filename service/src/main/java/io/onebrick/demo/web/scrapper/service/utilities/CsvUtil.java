package io.onebrick.demo.web.scrapper.service.utilities;

import com.opencsv.CSVWriter;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;

/**
 * @author Rizky Perdana
 */
@Slf4j
public class CsvUtil {
  private CsvUtil() {
  }
  public static <T> ByteArrayResource generateCsv(String[] columns, List<T> objectList, Class<T> clazz) {
    try {
      ByteArrayOutputStream stream = new ByteArrayOutputStream();
      OutputStreamWriter outputStreamWriter = new OutputStreamWriter(stream);
      CSVWriter writer = new CSVWriter(outputStreamWriter);

      StatefulBeanToCsv<T> beanToCsv = new StatefulBeanToCsvBuilder<T>(writer)
          .withMappingStrategy(constructMappingStrategy(columns, clazz))
          .build();

      writer.writeNext(columns);
      beanToCsv.write(objectList);
      outputStreamWriter.flush();
      return new ByteArrayResource(stream.toByteArray());
    } catch (CsvException | IOException e) {
      log.error("failed parse to csv columns={} objectList={} class={}", columns, objectList, clazz, e);
      return null;
    }
  }

  private static <T> ColumnPositionMappingStrategy<T> constructMappingStrategy(String[] columns, Class<T> clazz) {
    ColumnPositionMappingStrategy<T> mappingStrategy = new ColumnPositionMappingStrategy<>();
    mappingStrategy.setType(clazz);
    mappingStrategy.setColumnMapping(columns);

    return mappingStrategy;
  }
}
