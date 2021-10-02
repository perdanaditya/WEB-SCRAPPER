package io.onebrick.demo.web.scrapper.service.api;

import org.springframework.core.io.ByteArrayResource;
import reactor.core.publisher.Mono;

/**
 * @author Rizky Perdana
 */
public interface ProductService {

  Mono<ByteArrayResource> getProductsCsv();
}
