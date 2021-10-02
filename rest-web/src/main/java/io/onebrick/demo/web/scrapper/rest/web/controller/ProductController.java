package io.onebrick.demo.web.scrapper.rest.web.controller;

import io.onebrick.demo.web.scrapper.entity.constant.ApiPath;
import io.onebrick.demo.web.scrapper.service.api.ProductService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.Calendar;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

/**
 * @author Rizky Perdana
 */
@Slf4j
@RestController
@RequestMapping(ApiPath.PRODUCT_ROUTE)
@Api(value = "Product")
public class ProductController {

  @Autowired
  private ProductService productService;

  @ApiOperation(value = "Get list of Tokopedia Products in file")
  @GetMapping(produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
  public Mono<ResponseEntity<ByteArrayResource>> findProductsFile() {
    String filename = "Product_" + Calendar.getInstance().getTime().getTime() + ".csv";
    return productService.getProductsCsv()
        .map(byteArrayResource -> ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
            .body(byteArrayResource))
        .subscribeOn(Schedulers.elastic());
  }
}
