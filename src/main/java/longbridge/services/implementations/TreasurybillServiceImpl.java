package longbridge.services.implementations;

import longbridge.services.IntegrationService;
import longbridge.services.TreasurybillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class TreasurybillServiceImpl implements TreasurybillService {
    @Autowired
    IntegrationService integrationService;

    Logger logger = LoggerFactory.getLogger(this.getClass());


    @Override
    public boolean isBalanceOk(int amount, String accountNumber) {
        BigDecimal availableBalance = integrationService.getAvailableBalance(accountNumber);
        BigDecimal deposit = new BigDecimal(amount);
        int comparator = availableBalance.compareTo(deposit);
        logger.info("the comparator {}", comparator);
        if (comparator == 1) {
            return true;
        }

        return false;
    }

}
