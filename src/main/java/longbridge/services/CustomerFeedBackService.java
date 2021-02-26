package longbridge.services;

import longbridge.dtos.CustomerFeedBackDTO;
import longbridge.dtos.CustomerFeedBackSummaryDTO;

import java.util.Date;
import java.util.List;

public interface CustomerFeedBackService {

    String addFeedBack(CustomerFeedBackDTO feedBack) ;

    List<CustomerFeedBackSummaryDTO> getCustomerFeedBackSummary(Date startDate, Date endDate);


}
