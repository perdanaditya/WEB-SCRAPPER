package io.onebrick.demo.web.scrapper.service.impl;

import io.onebrick.demo.web.scrapper.entity.dto.Product;
import io.onebrick.demo.web.scrapper.service.api.ProductService;
import io.onebrick.demo.web.scrapper.service.configuration.TokopediaProperties;
import io.onebrick.demo.web.scrapper.service.utilities.CsvUtil;
import io.onebrick.demo.web.scrapper.service.utilities.TokopediaScrapperUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class ProductServiceImpl implements ProductService {

  @Autowired
  private TokopediaProperties tokopediaProperties;

  @Override
  public ByteArrayResource getProductsCsv() {
    return CsvUtil.generateCsv(tokopediaProperties.getCsvHeader(),
        TokopediaScrapperUtil.findProducts(tokopediaProperties), Product.class);
  }
}
