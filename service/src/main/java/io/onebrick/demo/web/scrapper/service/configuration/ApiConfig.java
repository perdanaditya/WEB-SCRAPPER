package io.onebrick.demo.web.scrapper.service.configuration;

import java.util.Arrays;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClientConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class ApiConfig {

  @Autowired
  private Environment environment;

  @Bean
  public AsyncHttpClient asyncHttpClient() {
    DefaultAsyncHttpClientConfig.Builder defaultConfigBuilder =
        new DefaultAsyncHttpClientConfig.Builder().setConnectTimeout(5000).setRequestTimeout(15000);

    if (Arrays.stream(environment.getActiveProfiles())
        .anyMatch(env -> (env.equalsIgnoreCase("dev") || env.equalsIgnoreCase("sandbox")))) {
      // note: java.util.concurrent.ExecutionException: no need for SSL for non production
      defaultConfigBuilder.setUseInsecureTrustManager(true);
    }
    return new DefaultAsyncHttpClient(defaultConfigBuilder.build());
  }
}
