package longbridge;

import longbridge.models.Account;
import longbridge.models.CorpTransferRule;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;
import static org.assertj.core.api.Assertions.*;


@RunWith(SpringRunner.class)
@SpringBootTest
public class InternetbankingApplicationTests {

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

}
