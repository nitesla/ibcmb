package longbridge.services;


import longbridge.dtos.ServiceRequestDTO;

import java.util.Map;

public interface ServiceRequestFeeService {
	
	Map<String, Number> getServiceRequestCharge(ServiceRequestDTO dto);

}
