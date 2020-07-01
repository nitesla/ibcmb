package longbridge.config;


import longbridge.dtos.CoverageDetailsDTO;


import java.util.List;

public class CoverageSession {
    List<CoverageDetailsDTO> coverage;

    public List<CoverageDetailsDTO> getCoverage() {
        return coverage;
    }

    public void setCoverage(List<CoverageDetailsDTO> coverage) {
        this.coverage = coverage;
    }
}
