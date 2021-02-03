package longbridge.services;


import longbridge.dtos.CoverageDetailsDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Collection;
import java.util.List;
import java.util.Map;


public interface CoverageService {



Page<CoverageDetailsDTO> getCoverages(String coverageName, String customerId,Pageable pageable);
Map<String, List<String>> getCoverageDetails(String coverageName, String customerId);
List<CoverageDetailsDTO>  getCoverage(String coverageName, String customerId);


}
