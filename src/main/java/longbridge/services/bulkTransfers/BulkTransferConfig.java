package longbridge.services.bulkTransfers;

import longbridge.dtos.CreditRequestDTO;
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

    @Bean
    @StepScope
    ItemReader<CreditRequestDTO> customReader(@Value("#{jobParameters[batchId]}")String batchId) {
        return new BulkTransferReader(batchId);
    }

    @Bean
    ItemProcessor<CreditRequestDTO, CreditRequestDTO> customProcessor() {
        return new BulkTransferProcessor();
    }

    @Bean
    ItemWriter<CreditRequestDTO> customWriter() {
        return new BulkTransferWriter();
    }

    @Bean
    Step customStep(ItemReader<CreditRequestDTO> customReader,
                         ItemProcessor<CreditRequestDTO, CreditRequestDTO> customProcessor,
                         ItemWriter<CreditRequestDTO> customWriter,
                         StepBuilderFactory stepBuilderFactory) {
        return stepBuilderFactory.get("customStep")
                .<CreditRequestDTO, CreditRequestDTO>chunk(2000)
                .reader(customReader(DEFAULT_BATCH_ID))
                .processor(customProcessor)
                .writer(customWriter)
                .build();
    }

    @Bean
    Job customJob(JobBuilderFactory jobBuilderFactory,
                       @Qualifier("customStep") Step customStep) {
        return jobBuilderFactory.get("customJob")
                .incrementer(new RunIdIncrementer())
                .flow(customStep)
                .end()
                .build();
    }


}
