package longbridge.jobs;

import longbridge.models.PaymentStat;
import longbridge.services.CronJobService;
import longbridge.services.RecurringPaymentService;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.Collection;


@Component
@DisallowConcurrentExecution
public class RecurringPaymentJob implements Job {
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private RecurringPaymentService recurringPaymentService;


	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {

		try {

			Collection<PaymentStat> payments= recurringPaymentService.getDuePayments();
			payments.forEach(p ->recurringPaymentService.performRecurringPayment(p));

		}catch(NullPointerException e){
			logger.info("Getting Recurring Payment Transactions");
		}

	}

}

@Configuration
class RecurringPaymentScheduler{

	@Autowired
	CronJobService cronJobService;

	@Bean(name="directJob")
	public JobDetail jobDetail() {
		return JobBuilder.newJob().ofType(RecurringPaymentJob.class)
				.storeDurably()
				.withIdentity("directJob")
				.build();
	}

	@Bean(name="directTrigger")
	public Trigger trigger(@Qualifier("directJob") JobDetail job) {

				return TriggerBuilder.newTrigger().forJob(job)
				.withIdentity("directTrigger")
				.withSchedule(CronScheduleBuilder.cronSchedule(cronJobService.getCurrentExpression("category4"))).build();

	}

	@Bean(name="directScheduler")
	public Scheduler scheduler(@Qualifier("directTrigger") Trigger trigger, @Qualifier("directJob") JobDetail job)throws
			SchedulerException {
			StdSchedulerFactory factory = new StdSchedulerFactory();
			Scheduler scheduler = factory.getScheduler();
			scheduler.scheduleJob(job, trigger);
			scheduler.start();
			return scheduler;
	}


}
