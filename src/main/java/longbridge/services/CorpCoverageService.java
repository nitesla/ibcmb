package longbridge.services;

import com.fasterxml.jackson.databind.JsonNode;

public interface CorpCoverageService {
    JsonNode getCoverage(String coverage);
}
