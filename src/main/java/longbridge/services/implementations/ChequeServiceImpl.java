package longbridge.services.implementations;

import longbridge.services.ChequeService;
import longbridge.services.IntegrationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class ChequeServiceImpl implements ChequeService {

    @Autowired
    IntegrationService integrationService;

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public boolean isBalanceOk(int amount, String accountNumber) {
        BigDecimal availableBalance = integrationService.getAvailableBalance(accountNumber);
        BigDecimal deposit = new BigDecimal(amount);
        int comparator = availableBalance.compareTo(deposit);
        logger.info("the comparator {}", comparator);
        return comparator > 0;
    }
}
