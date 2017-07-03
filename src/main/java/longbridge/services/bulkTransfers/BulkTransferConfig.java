
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
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Created by ayoade_farooq@yahoo.com on 6/22/2017.
 */
@Configuration
@EnableBatchProcessing(modular = true)
@EnableAsync
public class BulkTransferConfig
{

    private static final String DEFAULT_BATCH_ID = "";
    @Autowired
    public JobBuilderFactory jobBuilderFactory;
    @Bean
    @StepScope
    ItemReader<TransferDTO> customReader(@Value("#{jobParameters[batchId]}")String batchId) {
        return new BulkTransferReader(batchId);
    }

    @Bean
    ItemProcessor<TransferDTO, TransferDTO> customProcessor() {
        return new BulkTransferProcessor();
    }

    @Bean
    ItemWriter<TransferDTO> customWriter() {
        return new BulkTransferWriter();
    }

    @Bean
    Step customStep(ItemReader<TransferDTO> customReader,
                         ItemProcessor<TransferDTO, TransferDTO> customProcessor,
                         ItemWriter<TransferDTO> customWriter,
                         StepBuilderFactory stepBuilderFactory,
                    BulkTransferNotificationListener listener) {
        return stepBuilderFactory.get("customStep")
                .<TransferDTO, TransferDTO>chunk(2000)
                .reader(customReader(DEFAULT_BATCH_ID))
                .processor(customProcessor)
                .writer(customWriter)
                .listener(listener)
                .build();
    }

    @Bean
    Job customJob(JobBuilderFactory jobBuilderFactory,
                       @Qualifier("customStep") Step customStep,
                  BulkTransferNotificationListener listener) {
        return jobBuilderFactory.get("customJob")
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .flow(customStep)
                .end()
                .build();
    }


}

