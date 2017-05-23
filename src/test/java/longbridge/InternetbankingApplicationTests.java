package longbridge;

import longbridge.models.*;
import longbridge.repositories.CorpTransferRequestRepo;
import longbridge.repositories.CorporateRepo;
import longbridge.repositories.CorporateUserRepo;
import longbridge.repositories.PendingAuthorizationRepo;
import longbridge.services.CorporateService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

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

    @Autowired
    CorporateRepo corporateRepo;

    @Autowired
    CorpTransferRequestRepo transferRequestRepo;

    @Autowired
    CorporateUserRepo corporateUserRepo;

    @Autowired
    PendingAuthorizationRepo pendingAuthorizationRepo;


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
    @Transactional
    public  void getApplicableRule(){
        List<CorpTransferRule> transferRules = new ArrayList<>();
        CorpTransferRule transferRule1 = new CorpTransferRule();
        transferRule1.setId(1L);
        transferRule1.setLowerLimitAmount(new BigDecimal("0.00"));
        transferRule1.setUpperLimitAmount(new BigDecimal("5000"));
        CorpTransferRule transferRule2 = new CorpTransferRule();
        transferRule1.setId(2L);
        transferRule2.setLowerLimitAmount(new BigDecimal("4001"));
        transferRule2.setUpperLimitAmount(new BigDecimal("12000"));
        transferRule2.setUnlimited(true);
        CorpTransferRule transferRule3 = new CorpTransferRule();
        transferRule1.setId(3L);
        transferRule3.setLowerLimitAmount(new BigDecimal("10000"));
        transferRule3.setUpperLimitAmount(new BigDecimal("20000"));
        transferRule3.setUnlimited(true);
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

    @Test
    @Transactional
    public void getQualifiedAuthorizers(){
        CorpTransferRequest transferRequest = new CorpTransferRequest();
        transferRequest.setAmount(new BigDecimal("150000"));
        Corporate corporate = corporateRepo.findOne(2L);
        transferRequest.setCorporate(corporate);
        int numRules = corporate.getCorpTransferRules().size();
        int numAuthorizers = corporateService.getQualifiedAuthorizers(transferRequest).size();
        assertThat(numAuthorizers).isEqualTo(3);
        assertThat(numRules).isEqualTo(3);

    }

    @Test
    @Transactional
    public void addCorpTransferRequest() {

        CorpTransferRequest transferRequest = new CorpTransferRequest();
        transferRequest.setAmount(new BigDecimal("150000"));
        Corporate corporate = corporateRepo.findOne(2L);
        transferRequest.setCorporate(corporate);
        List<CorporateUser> authorizers = corporateService.getQualifiedAuthorizers(transferRequest);
        List<PendingAuthorization> pendingAuthorizations = new ArrayList<>();
        for (CorporateUser authorizer : authorizers) {
            PendingAuthorization pendingAuthorization = new PendingAuthorization();
            pendingAuthorization.setAuthorizer(authorizer);
            pendingAuthorizations.add(pendingAuthorization);
        }
        transferRequest.setPendingAuthorizations(pendingAuthorizations);
        transferRequestRepo.save(transferRequest);

        assertThat(corporate.getCorpTransferRules().size()).isEqualTo(3);
        assertThat(authorizers.size()).isEqualTo(3);
        assertThat(pendingAuthorizations.size()).isEqualTo(3);
        assertThat(transferRequest.getPendingAuthorizations().size()).isEqualTo(3);
    }


//
//    @Test
//    @Transactional
//    public void authorizeTransfer(){
//        CorporateUser authorizer = corporateUserRepo.findOne(1L);
//        PendingAuthorization pendingAuthorization = pendingAuthorizationRepo.findOne(14L);
//        CorpTransferRequest transferRequest = pendingAuthorization.getCorpTransferRequest();
//        BigDecimal amount =  transferRequest.getAmount();
//        BigDecimal scaledAmount = amount.setScale(0);
//        assertThat(authorizer.getPendingAuthorizations().size()).isEqualTo(2);
//        assertThat(scaledAmount).isEqualTo(new BigDecimal("250000"));
//        assertThat(transferRequest.getPendingAuthorizations().size()).isEqualTo(5);
//        transferRequest.getPendingAuthorizations().remove(pendingAuthorization);
//        assertThat(transferRequest.getPendingAuthorizations().size()).isEqualTo(4);
//        transferRequestRepo.save(transferRequest);
//
//    }
}
