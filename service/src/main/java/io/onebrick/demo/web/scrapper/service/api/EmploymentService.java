package io.onebrick.demo.web.scrapper.service.api;

import io.onebrick.demo.web.scrapper.entity.dto.EmployeeCredential;
import io.onebrick.demo.web.scrapper.entity.dto.EmployeeInfo;
import java.util.List;
import org.springframework.core.io.ByteArrayResource;
import reactor.core.publisher.Mono;

public interface EmploymentService {
  List<EmployeeInfo> getEmploymentInfo(String base64);
  ByteArrayResource getEmploymentInfoFile(String base64);
}
