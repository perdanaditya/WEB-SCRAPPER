package io.onebrick.demo.web.scrapper.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.onebrick.demo.web.scrapper.entity.dto.EmployeeCredential;
import io.onebrick.demo.web.scrapper.entity.dto.EmployeeGeneralInfo;
import io.onebrick.demo.web.scrapper.entity.dto.EmployeeInfo;
import io.onebrick.demo.web.scrapper.entity.dto.EmploymentInfo;
import io.onebrick.demo.web.scrapper.entity.dto.SalaryInfo;
import io.onebrick.demo.web.scrapper.service.api.EmploymentService;
import io.onebrick.demo.web.scrapper.service.response.BaseResponse;
import io.onebrick.demo.web.scrapper.service.utilities.CsvUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import lombok.extern.log4j.Log4j2;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Log4j2
@Service
public class EmploymentServiceImpl implements EmploymentService {

  @Autowired
  private ObjectMapper mapper;

  private AsyncHttpClient asyncHttpClient;

  @Autowired
  public void setAsyncHttpClient(AsyncHttpClient asyncHttpClient) {
    this.asyncHttpClient = asyncHttpClient;
  }

  @Override
  public List<EmployeeInfo> getEmploymentInfo(String base64) {
    List<EmployeeCredential> employeeCredentials = CsvUtil
        .read(EmployeeCredential.class, base64, new String[]{"username", "userAccessToken"})
        .block();
    return getEmploymentInfo(employeeCredentials).block();
  }

  @Override
  public ByteArrayResource getEmploymentInfoFile(String base64) {
    List<EmployeeCredential> employeeCredentials = CsvUtil
        .read(EmployeeCredential.class, base64, new String[]{"username", "userAccessToken"})
        .block();
    List<EmployeeInfo> employeeInfos = getEmploymentInfo(employeeCredentials).block();
    return CsvUtil.generateCsv(new String[]{"username", "userAccessToken", "name",
        "ktpNumber", "bpjsCardNumber", "dob",
        "phoneNumber", "address", "gender",
        "totalBalance",
        "employmentInfos", "salaryInfos"}, employeeInfos, EmployeeInfo.class);
  }

  private Mono<List<EmployeeInfo>> getEmploymentInfo(List<EmployeeCredential> credentials) {
    List<EmployeeInfo> employeeInfos = new ArrayList<>();
    credentials.forEach(employeeCredential -> {
      EmployeeInfo employeeInfo = EmployeeInfo.builder()
          .username(employeeCredential.getUsername())
          .userAccessToken(employeeCredential.getUserAccessToken())
          .build();
      String employmentInfos = outboundEmploymentInfo(employeeCredential.getUserAccessToken());
      EmployeeGeneralInfo generalInfo = outboundGeneralInfo(
          employeeCredential.getUserAccessToken());
      String salaryInfos = outboundSalaryInfo(employeeCredential.getUserAccessToken());
      if (Objects.nonNull(employmentInfos)) {
        employeeInfo.setEmploymentInfos(employmentInfos);
      }
      if (Objects.nonNull(generalInfo)) {
        employeeInfo.setName(generalInfo.getName());
        employeeInfo.setKtpNumber(generalInfo.getKtpNumber());
        employeeInfo.setBpjsCardNumber(generalInfo.getBpjsCardNumber());
        employeeInfo.setDob(generalInfo.getDob());
        employeeInfo.setPhoneNumber(generalInfo.getPhoneNumber());
        employeeInfo.setAddress(generalInfo.getAddress());
        employeeInfo.setGender(generalInfo.getGender());
        employeeInfo.setTotalBalance(generalInfo.getTotalBalance());

      }
      if (Objects.nonNull(salaryInfos)) {
        employeeInfo.setSalaryInfos(salaryInfos);
      }
      employeeInfos.add(employeeInfo);
    });

    return Mono.just(employeeInfos);

  }

  private String outboundEmploymentInfo(String userToken) {
    Response response = null;
    try {
      response = asyncHttpClient
          .prepareGet("https://api.onebrick.io/v1/income/employment")
          .addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + userToken)
          .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
          .setRequestTimeout(100000)
          .execute()
          .get();
    } catch (InterruptedException e) {
      e.printStackTrace();
    } catch (ExecutionException e) {
      e.printStackTrace();
    }
    if (Objects.nonNull(response)) {
      log.info("Response {}", response.getResponseBody());
    }
    if (Objects.nonNull(response) && 200 == response.getStatusCode()) {
      return convertObjectToJsonInString(
          convertResponse(convertResponse(response.getResponseBody(), BaseResponse.class).getData(),
              EmploymentInfo[].class));
    }
    return response.getResponseBody();
  }

  private EmployeeGeneralInfo outboundGeneralInfo(String userToken) {
    Response response = null;
    try {
      response = asyncHttpClient
          .prepareGet("https://api.onebrick.io/v1/income/general")
          .addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + userToken)
          .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
          .setRequestTimeout(100000)
          .execute()
          .get();
    } catch (InterruptedException e) {
      e.printStackTrace();
    } catch (ExecutionException e) {
      e.printStackTrace();
    }
    if (Objects.nonNull(response) && 200 == response.getStatusCode()) {
      return convertResponse(
          convertResponse(response.getResponseBody(), BaseResponse.class).getData(),
          EmployeeGeneralInfo.class);
    } else if (Objects.nonNull(response) && Objects.nonNull(response.getResponseBody())) {
      return EmployeeGeneralInfo.builder().name(response.getResponseBody()).build();
    }
    return null;
  }

  private String outboundSalaryInfo(String userToken) {
    Response response = null;
    try {
      response = asyncHttpClient
          .prepareGet("https://api.onebrick.io/v1/income/salary")
          .addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + userToken)
          .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
          .setRequestTimeout(100000)
          .execute()
          .get();
    } catch (InterruptedException e) {
      e.printStackTrace();
    } catch (ExecutionException e) {
      e.printStackTrace();
    }
    if (Objects.nonNull(response) && 200 == response.getStatusCode()) {
      return convertObjectToJsonInString(
          convertResponse(convertResponse(response.getResponseBody(), BaseResponse.class).getData(),
              SalaryInfo[].class));
    }
    return response.getResponseBody();
  }

  private <T> T convertResponse(Object jsonObject, Class<T> clazz) {
    return convertJsonInStringToObject(convertObjectToJsonInString(jsonObject), clazz);
  }

  private String convertObjectToJsonInString(Object data) {
    try {
      if (data instanceof String) {
        return (String) data;
      }
      return mapper.writeValueAsString(data);
    } catch (Exception e) {
      log.error("Error on convertObjectToJsonInString.", e);
      return null;
    }
  }

  private <T> T convertJsonInStringToObject(String jsonInString, Class<T> clazz) {
    try {
      return mapper.readValue(jsonInString, clazz);
    } catch (Exception e) {
      log.error("Error on read value, convertObjectToJsonInString.", e);
      return null;
    }
  }
}
