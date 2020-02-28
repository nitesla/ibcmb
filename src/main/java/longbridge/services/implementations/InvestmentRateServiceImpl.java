package longbridge.services.implementations;

import longbridge.dtos.CodeDTO;
import longbridge.dtos.InvestmentRateDTO;
import longbridge.exception.InternetBankingException;
import longbridge.exception.VerificationInterruptedException;
import longbridge.repositories.InvestmentRateRepo;
import longbridge.services.InvestmentRateService;
import longbridge.models.InvestmentRate;
import longbridge.utils.Verifiable;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
public class InvestmentRateServiceImpl implements InvestmentRateService {

    @Autowired
    InvestmentRateRepo rateRepo;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    MessageSource messageSource;
    @Autowired
    private InvestmentRateRepo investmentRateRepo;


    private Locale locale = LocaleContextHolder.getLocale();

    Logger logger= LoggerFactory.getLogger(this.getClass());

   public List<String> getDistinctInvestments(){
       return rateRepo.findDistinctFirstByInvestmentName();
   }
    @Verifiable(operation = "ADD_RATE", description = "Add Rate")
   public String addRate(InvestmentRateDTO investmentRateDTO){
       try {
           InvestmentRate investmentRate = modelMapper.map(investmentRateDTO,InvestmentRate.class);
           rateRepo.save(investmentRate);
           logger.info("Added new rate {} of type {}", investmentRate.getValue(), investmentRate.getInvestmentName());
           return messageSource.getMessage("rate.add.success", null, locale);
       } catch (VerificationInterruptedException e) {
           return e.getMessage();
       } catch (Exception e) {
           throw new InternetBankingException(messageSource.getMessage("rate.add.failure", null, locale), e);
       }
   }

   public Page<InvestmentRate> getAllRatesByInvestmentName(String investmentName, Pageable pageable){
       Page<InvestmentRate> investmentRates=rateRepo.findAllByInvestmentName(investmentName,pageable);
       return investmentRates;
   }

    public InvestmentRateDTO getInvestmentRate(Long rateId){
       InvestmentRate investmentRate=rateRepo.findOneById(rateId);
         return modelMapper.map(investmentRate,InvestmentRateDTO.class);
    }
    @Override
    @Transactional
    @Verifiable(operation = "UPDATE_RATE", description = "Updating a Rate")
    public String updateRate(InvestmentRateDTO investmentRateDTO){
        try {
            InvestmentRate investmentRate = modelMapper.map(investmentRateDTO,InvestmentRate.class);
            rateRepo.save(investmentRate);
            logger.info("Updated investment rate with Id {}", investmentRate.getId());
            return messageSource.getMessage("rate.update.success", null, locale);
        } catch (VerificationInterruptedException e) {
            return e.getMessage();
        }
        catch (InternetBankingException e){
            throw e;
        }
        catch (Exception e) {
            throw new InternetBankingException(messageSource.getMessage("rate.update.failure", null, locale), e);
        }

    }

    @Override
    @Transactional
    @Verifiable(operation = "DELETE_RATE", description = "Deleting a Rate")
    public String deleteRate(Long rateId) {
       logger.info("rateo"+rateId);
        try {
            InvestmentRate investmentRate = rateRepo.findOneById(rateId);
            rateRepo.delete(investmentRate);
            logger.info("Rate {} has been deleted", rateId.toString());
            return messageSource.getMessage("code.delete.success", null, locale);
        } catch (VerificationInterruptedException e) {
            return e.getMessage();
        } catch (InternetBankingException e) {
            throw e;
        } catch (Exception e) {
            throw new InternetBankingException(messageSource.getMessage("rate.delete.failure", null, locale), e);
        }
    }
    @Override
   public Optional<Integer> getRateByTenorAndAmount(String investmentName, int tenor, int amount){
        return investmentRateRepo.findRateByTenorAndAmount(investmentName,tenor,amount) ;

    }

    @Override
    public List<InvestmentRate> getInvestmentRateByInvestmentName(String investmentName) {
        return investmentRateRepo.findByInvestmentName(investmentName);
    }


}
