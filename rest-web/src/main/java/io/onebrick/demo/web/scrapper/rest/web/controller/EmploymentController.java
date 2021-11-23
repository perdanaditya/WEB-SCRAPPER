package io.onebrick.demo.web.scrapper.rest.web.controller;

import io.onebrick.demo.web.scrapper.entity.constant.ApiPath;
import io.onebrick.demo.web.scrapper.entity.dto.EmployeeInfo;
import io.onebrick.demo.web.scrapper.rest.web.configuration.CommonResponseHelper;
import io.onebrick.demo.web.scrapper.service.api.EmploymentService;
import io.onebrick.demo.web.scrapper.service.response.BaseResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.Calendar;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiPath.EMPLOYMENT_ROUTE)
@Api(value = "Employment")
public class EmploymentController {

  @Autowired
  private EmploymentService employmentService;


  @ApiOperation(value = "Get list of Tokopedia Products in file")
  @PostMapping(path = "/employee")
  @Transactional(timeout = 100000000)
  public BaseResponse<List<EmployeeInfo>> findEmployeeFile(@RequestBody String base64) {
    List<EmployeeInfo> employeeInfos = employmentService.getEmploymentInfo(base64);
    return CommonResponseHelper.constructSuccessResponse(employeeInfos);
  }

  @ApiOperation(value = "Get list of Tokopedia Products in file")
  @PostMapping(produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
  @Transactional(timeout = 100000000)
  public ResponseEntity<ByteArrayResource> findEmployeeFileWithUpload(@RequestBody String base64) {
    String filename = "Employee_" + Calendar.getInstance().getTime().getTime() + ".csv";
    return ResponseEntity.ok()
        .contentType(MediaType.APPLICATION_OCTET_STREAM)
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
        .body(employmentService.getEmploymentInfoFile(base64));
  }
}
