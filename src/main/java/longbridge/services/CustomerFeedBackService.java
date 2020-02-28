package longbridge.services;

import longbridge.dtos.CustomerFeedBackDTO;
import longbridge.dtos.CustomerFeedBackSummaryDTO;
import longbridge.exception.InternetBankingException;
import longbridge.models.CustomerFeedBack;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface CustomerFeedBackService {

    String addFeedBack(CustomerFeedBackDTO feedBack) throws InternetBankingException;

    List<CustomerFeedBackSummaryDTO> getCustomerFeedBackSummary(Date startDate, Date endDate);


}
