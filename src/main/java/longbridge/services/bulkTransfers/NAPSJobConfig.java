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

    private static final String PROPERTY_REST_API_URL = "rest.api.to.database.job.api.url";
    @Autowired
    public JobBuilderFactory jobBuilderFactory;
    @Bean
    @StepScope
    ItemReader<TransactionStatus> restReader(@Value("#{jobParameters[batchId]}")String batchId,Environment environment, RestTemplate restTemplate) {
        return new TransferStatusReader(batchId,environment.getRequiredProperty(PROPERTY_REST_API_URL), restTemplate);
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
    Step restStep(ItemReader<TransactionStatus> restReader,
                         ItemProcessor<TransactionStatus, TransactionStatus> restProcessor,
                         ItemWriter<TransactionStatus> restWriter,
                         StepBuilderFactory stepBuilderFactory) {
        return stepBuilderFactory.get("restStep")
                .<TransactionStatus, TransactionStatus>chunk(1)
                .reader(restReader)
                .processor(restProcessor)
                .writer(restWriter)
                .build();
    }

    @Bean
    Job restJob(JobBuilderFactory jobBuilderFactory,
                       @Qualifier("restStep") Step restStep) {
        return jobBuilderFactory.get("restJob")
                .incrementer(new RunIdIncrementer())
                .flow(restStep)
                .end()
                .build();
    }
}
