package io.onebrick.demo.web.scrapper.rest.web.controller;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import io.onebrick.demo.web.scrapper.entity.constant.ApiPath;
import io.onebrick.demo.web.scrapper.service.api.ProductService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

/**
 * @author Rizky Perdana
 */
public class ProductControllerTest {

  @InjectMocks
  private ProductController productController;
  @Mock
  private ProductService productService;
  private MockMvc mockMvc;

  @Before
  public void before() {
    initMocks(this);
    this.mockMvc = standaloneSetup(this.productController).build();
  }

  @After
  public void after() {
    verifyNoMoreInteractions(this.productService);
  }

  @Test
  public void findProductsFile() throws Exception {
    when(productService.getProductsCsv())
        .thenReturn(new ByteArrayResource("test".getBytes(), "test"));

    MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
        .get(ApiPath.PRODUCT_ROUTE)
        .accept(MediaType.APPLICATION_OCTET_STREAM)
        .contentType(MediaType.APPLICATION_OCTET_STREAM);
    this.mockMvc.perform(builder)
        .andExpect(status().isOk());
    verify(productService, times(1)).getProductsCsv();
  }
}