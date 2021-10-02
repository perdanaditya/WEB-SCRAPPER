package io.onebrick.demo.web.scrapper.service.impl;

import static org.mockito.MockitoAnnotations.initMocks;

import io.onebrick.demo.web.scrapper.service.configuration.TokopediaProperties;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.test.StepVerifier;

/**
 * @author Rizky Perdana
 */
@Ignore
@Slf4j
public class ProductServiceImplTest {

  @InjectMocks
  private ProductServiceImpl productService;

  @Before
  public void before() {
    initMocks(this);
    TokopediaProperties properties = new TokopediaProperties();
    properties.setDataLimit(1);
    properties.setCsvHeader(
        new String[]{"Product Name", "Description", "Image Url", "Price", "Rate", "Store Name"});
    properties.setHeadless(true);
    properties.setUserAgent(
        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/94.0.4606.61 Safari/537.36");
    properties.setUrl("https://www.tokopedia.com/p/handphone-tablet/handphone");
    ReflectionTestUtils
        .setField(productService, "tokopediaProperties", properties);
  }

  @Test
  public void findProductsFile() {
    StepVerifier.create(productService.getProductsCsv())
        .assertNext(byteArrayResource -> {
          Assert.assertNotNull(byteArrayResource);
          createFile(byteArrayResource);
        })
        .expectComplete()
        .verify();
  }

  private void createFile(ByteArrayResource byteArrayResource) {
    File file = new File("Product_" + Calendar.getInstance().getTime().getTime() + ".csv");
    if (!file.exists()) {
      try {
        file.createNewFile();
      } catch (IOException e) {
        log.error("createFile", e);
      }
    }
    try (FileOutputStream fos = new FileOutputStream("pathname")) {
      fos.write(byteArrayResource.getByteArray());
    } catch (Exception e) {
      log.error("createFile", e);
    }
  }
}