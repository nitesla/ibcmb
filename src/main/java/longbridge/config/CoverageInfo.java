package longbridge.config;


import longbridge.dtos.CoverageDetailsDTO;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.annotation.SessionScope;


import java.util.List;
@Configuration
public class CoverageInfo {

    @Bean
    @SessionScope
    public CoverageInfo sessionScopedBean(){
        return new CoverageInfo();
    }

    private List<CoverageDetailsDTO> coverage;

    public List<CoverageDetailsDTO> getCoverage() {
        return coverage;
    }

    public void setCoverage(List<CoverageDetailsDTO> coverage) {
        this.coverage = coverage;
    }
}
