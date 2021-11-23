package io.onebrick.demo.web.scrapper.service.utilities;

import com.opencsv.CSVWriter;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvException;
import io.vavr.control.Try;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import reactor.core.publisher.Mono;

/**
 * @author Rizky Perdana
 */
@Slf4j
public class CsvUtil {
  private CsvUtil() {
  }
  public static <T> ByteArrayResource generateCsv(String[] columns, List<T> objectList, Class<T> clazz) {
    log.info("Generating csv file");
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
      log.info("Generate csv file complete at {}", new Date());
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

  public static <T> Mono<List<T>> read(Class<T> clazz, String base64, String[] columns) {
    log.info("Base64 to decode {}" + base64);
    ColumnPositionMappingStrategy<T> mappingStrategy = new ColumnPositionMappingStrategy<>();
    mappingStrategy.setType(clazz);
    mappingStrategy.setColumnMapping(columns);
    CsvToBeanBuilder<T> csvToBeanBuilder = new CsvToBeanBuilder<>(
        new InputStreamReader(
            new ByteArrayInputStream(decode(base64))));
    return Try.of(() -> Mono.just(csvToBeanBuilder
        .withMappingStrategy(mappingStrategy)
        .withSkipLines(1)//remove header from list
        .build().parse()))
        .getOrElse(Mono.error(new Exception("CSV_FILE_INVALID")));
  }

  private static byte[] decode(String base64) {
    try {
      return Base64.getDecoder().decode(base64);
    } catch (Exception e) {
      log.info("BASE64_DECODER_ERROR", e);
//      throw new Exception("BASE64_DECODER_ERROR");
      return null;
    }
  }
}
