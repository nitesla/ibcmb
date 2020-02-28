package longbridge.services.implementations;

import longbridge.dtos.CustomerFeedBackDTO;
import longbridge.dtos.CustomerFeedBackSummaryDTO;
import longbridge.exception.InternetBankingException;
import longbridge.models.CustomerFeedBack;
import longbridge.repositories.CustomerFeedBackRepo;
import longbridge.services.CustomerFeedBackService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Service
public class CustomerFeedBackServiceImpl implements CustomerFeedBackService {

    @Autowired
    CustomerFeedBackRepo customerFeedBackRepo;

    @Autowired
    MessageSource messageSource;

    @Autowired
    ModelMapper modelMapper;

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private Locale locale = LocaleContextHolder.getLocale();

    @Override
    public String addFeedBack(CustomerFeedBackDTO feedBackDTO) throws InternetBankingException {
        if (null != feedBackDTO) {
            CustomerFeedBack feedBack = modelMapper.map(feedBackDTO, CustomerFeedBack.class);
            customerFeedBackRepo.save(feedBack);

        } else {
            logger.info("Customer did not give a feed back {}", feedBackDTO);
        }
        return null;
    }

    @Override
    public List<CustomerFeedBackSummaryDTO> getCustomerFeedBackSummary(Date startDate, Date endDate){
        List<CustomerFeedBackSummaryDTO> results = new ArrayList<>();

        List<String> distinctTranTypes = customerFeedBackRepo.findDistinctTranTypes(startDate,endDate);
        logger.info("distinct trantypes {}",distinctTranTypes);
        distinctTranTypes.forEach(tranType -> {
            CustomerFeedBackSummaryDTO result = new CustomerFeedBackSummaryDTO();
            String retailRating = customerFeedBackRepo.findAverageForUserType(tranType, "RETAIL", startDate, endDate);
           logger.info("the retailrating is {}",retailRating);
            String corporateRating = customerFeedBackRepo.findAverageForUserType(tranType, "CORPORATE", startDate, endDate);;
            String bothRating = customerFeedBackRepo.findAverageForBothTypes(tranType, startDate, endDate);
            result.setTranType(tranType);
            result.setRetailRatingReviews(Integer.parseInt(this.getStringPart(0, retailRating)));
            result.setRetailRating(new BigDecimal(Double.parseDouble(this.getStringPart(1, retailRating))).setScale(0, RoundingMode.HALF_UP));
            result.setCorporateRatingReviews(Integer.parseInt(this.getStringPart(0, corporateRating)));
            result.setCorporateRating(new BigDecimal(Double.parseDouble(this.getStringPart(1, corporateRating))).setScale(0, RoundingMode.HALF_UP));
            result.setBothRatingReviews(Integer.parseInt(this.getStringPart(0, bothRating)));
            result.setBothRating(new BigDecimal(Double.parseDouble(this.getStringPart(1, bothRating))).setScale(0, RoundingMode.HALF_UP));

            results.add(result);
        });
        return results;

    }

    private String getStringPart(int part, String stringToSplit){
        String result = stringToSplit != null ? stringToSplit.split(",")[part] : "0";
        if(result == null) return "0";
        if(result.equals("")) return "0";
        if(result.equalsIgnoreCase("null")) return "0";
        return result;
    }


}
