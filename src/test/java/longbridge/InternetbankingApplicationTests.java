package longbridge;

import longbridge.models.Account;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
public class InternetbankingApplicationTests {

	@Test
	public void contextLoads() {

        Account account = new Account();
        account.setAccountNumber("123456");
        String acct = account.getAccountNumber();
        assertEquals("123456", acct);

	}

}
