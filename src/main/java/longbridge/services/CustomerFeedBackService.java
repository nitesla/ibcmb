package longbridge.services;

import longbridge.dtos.CustomerFeedBackDTO;
import longbridge.dtos.CustomerFeedBackSummaryDTO;
import longbridge.exception.InternetBankingException;

import java.util.Date;
import java.util.List;

public interface CustomerFeedBackService {

    String addFeedBack(CustomerFeedBackDTO feedBack) throws InternetBankingException;

    List<CustomerFeedBackSummaryDTO> getCustomerFeedBackSummary(Date startDate, Date endDate);


}
