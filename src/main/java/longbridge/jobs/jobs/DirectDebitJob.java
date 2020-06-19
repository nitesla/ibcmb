package longbridge.jobs;

//<<<<<<< HEAD:src/main/java/longbridge/jobs/jobs/DirectDebitJob.java
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import longbridge.models.Payment;
import longbridge.services.CronJobService;
import org.joda.time.LocalDate;
//=======
import longbridge.models.Payment;
import longbridge.services.CronJobService;
import longbridge.services.DirectDebitService;
//>>>>>>> 598cd74a9ed9e8e9986fe283f260980c47418ef5:src/main/java/longbridge/jobs/DirectDebitJob.java
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
//<<<<<<< HEAD:src/main/java/longbridge/jobs/jobs/DirectDebitJob.java

import longbridge.services.DirectDebitService;
//=======
//>>>>>>> 598cd74a9ed9e8e9986fe283f260980c47418ef5:src/main/java/longbridge/jobs/DirectDebitJob.java
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

//<<<<<<< HEAD:src/main/java/longbridge/jobs/jobs/DirectDebitJob.java
//=======
import java.util.Collection;

//>>>>>>> 598cd74a9ed9e8e9986fe283f260980c47418ef5:src/main/java/longbridge/jobs/DirectDebitJob.java

@Component
@DisallowConcurrentExecution
public class DirectDebitJob implements Job {
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private DirectDebitService directDebitService;


	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {

		try {

			Collection<Payment> payments= directDebitService.getDuePayments();
			payments.forEach(p ->directDebitService.performDirectDebitPayment(p));

		}catch(NullPointerException e){
			logger.info("Getting Direct Debit Transactions");
		}

	}

}
@Configuration
class DirectDebitScheduler{

	@Autowired
	CronJobService cronJobService;

	@Bean(name="directJob")
	public JobDetail jobDetail() {
		return JobBuilder.newJob().ofType(DirectDebitJob.class)
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
