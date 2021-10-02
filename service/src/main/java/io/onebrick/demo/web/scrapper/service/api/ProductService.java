package io.onebrick.demo.web.scrapper.service.api;

import org.springframework.core.io.ByteArrayResource;

/**
 * @author Rizky Perdana
 */
public interface ProductService {

  ByteArrayResource getProductsCsv();
}
