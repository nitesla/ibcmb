package longbridge;

import longbridge.models.Account;
import longbridge.models.CorpTransferRequest;
import longbridge.models.CorpTransferRule;
import longbridge.models.Corporate;
import longbridge.services.CorporateService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.assertj.core.api.Assertions.*;


@RunWith(SpringRunner.class)
@SpringBootTest
public class InternetbankingApplicationTests {

    @Autowired
    CorporateService corporateService;

	@Test
	public void testAccount() {

        Account account = new Account();
        account.setAccountNumber("123456");
        String acct = account.getAccountNumber();
        assertThat("123456").isEqualTo(acct);

	}

	@Test
    public  void  testTransferRule(){
        CorpTransferRule transferRule = new CorpTransferRule();
        transferRule.setLowerLimitAmount(new BigDecimal("5000.25"));
        transferRule.setUpperLimitAmount(new BigDecimal("3000.75"));
        assertThat(transferRule.getLowerLimitAmount().toString()).isEqualTo("5000.25");
        assertThat(transferRule.getUpperLimitAmount().toString()).isEqualTo("3000.75");

    }

    @Test
    public  void getApplicableRule(){
        List<CorpTransferRule> transferRules = new ArrayList<>();
        CorpTransferRule transferRule1 = new CorpTransferRule();
        transferRule1.setId(1L);
        transferRule1.setLowerLimitAmount(new BigDecimal("0.00"));
        transferRule1.setUpperLimitAmount(new BigDecimal("5000"));
        CorpTransferRule transferRule2 = new CorpTransferRule();
        transferRule1.setId(2L);
        transferRule2.setLowerLimitAmount(new BigDecimal("4001"));
        transferRule2.setUpperLimitAmount(new BigDecimal("10000"));
        CorpTransferRule transferRule3 = new CorpTransferRule();
        transferRule1.setId(3L);
        transferRule3.setLowerLimitAmount(new BigDecimal("10000"));
        transferRule3.setUpperLimitAmount(new BigDecimal("20000"));
        transferRule3.setInfinite(true);
        transferRules.add(transferRule1);
        transferRules.add(transferRule2);
        transferRules.add(transferRule3);
        Corporate corporate = new Corporate();
        corporate.setCorpTransferRules(transferRules);
        CorpTransferRequest transferRequest = new CorpTransferRequest();
        transferRequest.setAmount(new BigDecimal("100000"));
        transferRequest.setCorporate(corporate);
        CorpTransferRule rule = corporateService.getApplicableTransferRule(transferRequest);
        assertThat(rule).isEqualTo(transferRule3);

    }


}
