package longbridge.services;


import longbridge.servicerequests.client.ServiceRequestDTO;

import java.util.Map;

public interface ServiceRequestFeeService {
	
	Map<String, Number> getServiceRequestCharge(ServiceRequestDTO dto);

}
