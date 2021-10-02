package io.onebrick.demo.web.scrapper.service.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Rizky Perdana
 */
@Data
@ConfigurationProperties("web.scrapper.tokopedia")
public class TokopediaProperties {

  private int dataLimit = 100;
  private String[] csvHeader;
  private boolean headless = Boolean.TRUE;
  private String userAgent;
  private String url;
}
