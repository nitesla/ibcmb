package longbridge.services;
import com.fasterxml.jackson.databind.JsonNode;

public interface CoverageService {

    JsonNode getCoverage(String coverage);
    boolean  isCoverageEnabled(String coverage);
}
