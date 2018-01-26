package longbridge.services.bulkTransfers;


import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.client.RestTemplate;

/**
 * @author Ayoade Farooq
 */
@Configuration
@EnableBatchProcessing(modular = true)
@EnableAsync
public class NAPSJobConfig {

    private static final String DEFAULT_BATCH_ID = "";

    @Autowired
    public JobBuilderFactory jobBuilderFactory;


    @Bean
    @StepScope
    ItemReader<TransactionStatus> restReader(@Value("#{jobParameters[batchId]}")String batchId) {
        return new TransferStatusReader(batchId);
    }

    @Bean
    ItemProcessor<TransactionStatus, TransactionStatus> restProcessor() {
        return new TransferStatusProcessor();
    }

    @Bean
    ItemWriter<TransactionStatus> restWriter() {
        return new TransferStatusWritter();
    }

    @Bean
    Step restStep(/**ItemReader<TransactionStatus> transferStatusReader,**/
//                         ItemProcessor<TransactionStatus, TransactionStatus> restProcessor,
//                         ItemWriter<TransactionStatus> restWriter,
                         StepBuilderFactory stepBuilderFactory,
                  BulkTransferStatusNotificationListener statusNotificationListener) {
        return stepBuilderFactory.get("restStep")
                .<TransactionStatus, TransactionStatus>chunk(1)
                .reader(restReader(DEFAULT_BATCH_ID))
                .processor(restProcessor())
                .writer(restWriter())
                .listener(statusNotificationListener)
                .build();
    }

    @Bean
    Job restJob(JobBuilderFactory jobBuilderFactory,
                       @Qualifier("restStep") Step restStep,
                BulkTransferStatusNotificationListener statusNotificationListener) {
        return jobBuilderFactory.get("restJob")
                .incrementer(new RunIdIncrementer())
                .listener(statusNotificationListener)
                .flow(restStep)
                .end()
                .build();
    }
}
