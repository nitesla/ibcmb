package longbridge.services;


import java.util.Map;

import longbridge.dtos.ServiceRequestDTO;

public interface ServiceRequestFeeService {
	
	Map<String, Number> getServiceRequestCharge(ServiceRequestDTO dto);

}
