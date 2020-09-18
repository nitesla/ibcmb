package longbridge.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.AttributeConverter;
import java.io.IOException;
import java.util.Map;

public class CoverageMapConverter implements AttributeConverter<Map<String, Object>, String> {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static ObjectMapper mapper;

@Override
public String convertToDatabaseColumn(Map<String, Object> coverageDetails) {

        String coverageDetailsJson = null;
        try {
            coverageDetailsJson = mapper.writeValueAsString(coverageDetails);
        } catch (final JsonProcessingException e) {
        logger.error("JSON writing error", e);
        }

        return coverageDetailsJson;
        }

@Override
public Map<String, Object> convertToEntityAttribute(String coverageDetailsJson) {

        Map<String, Object> coverageDetails = null;
        try {
            coverageDetails =  mapper.readValue(coverageDetailsJson, Map.class);
        } catch (final IOException e) {
        logger.error("JSON reading error", e);
        }

        return coverageDetails;
        }

        }
