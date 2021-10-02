package io.onebrick.demo.web.scrapper.service.impl;

import io.onebrick.demo.web.scrapper.entity.dto.Product;
import io.onebrick.demo.web.scrapper.service.api.ProductService;
import io.onebrick.demo.web.scrapper.service.configuration.TokopediaProperties;
import io.onebrick.demo.web.scrapper.service.utilities.CsvUtil;
import io.onebrick.demo.web.scrapper.service.utilities.TokopediaScrapperUtil;
import java.util.Date;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Log4j2
public class ProductServiceImpl implements ProductService {

  @Autowired
  private TokopediaProperties tokopediaProperties;

  @Override
  public Mono<ByteArrayResource> getProductsCsv() {
    return TokopediaScrapperUtil.findProducts(tokopediaProperties)
        .collectList()
        .doOnNext(products -> log.info("Generating csv file"))
        .map(products -> CsvUtil
            .generateCsv(tokopediaProperties.getCsvHeader(), products, Product.class))
        .doOnNext(byteArrayResource -> log.info("Generate csv file complete at {}", new Date()));
  }
}
