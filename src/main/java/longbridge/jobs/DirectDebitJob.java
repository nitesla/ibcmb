package longbridge.jobs;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.List;

import longbridge.exception.TransferException;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import longbridge.models.DirectDebit;
import longbridge.services.DirectDebitService;


/**
 * Created by chigozirim torti on 5/12/17.
 */

public class DirectDebitJob implements ActionListener {
	private Logger logger= LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private DirectDebitService directDebitService;

	public void executeDirectDebits() throws TransferException {
		logger.info("Executing direct debits for today: ", LocalDate.now());
		List<DirectDebit> dueDirectDebits = directDebitService.getDueDirectDebits();
		
		for (DirectDebit directDebit : dueDirectDebits) {
			logger.info(directDebit.toString());
			directDebitService.performDirectDebit(directDebit);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			executeDirectDebits();
		} catch (TransferException e1) {
			e1.printStackTrace();
		}
	}
}
