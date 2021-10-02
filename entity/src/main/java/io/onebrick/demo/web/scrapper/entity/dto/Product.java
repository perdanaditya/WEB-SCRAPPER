package io.onebrick.demo.web.scrapper.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import lombok.NoArgsConstructor;

/**
 * @author Rizky Perdana
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Product {

  private String productName;
  private String description;
  private String imageUrl;
  private BigDecimal price;
  private Double rate;
  private String storeName;
}
