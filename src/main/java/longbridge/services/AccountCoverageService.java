package longbridge.services;


import longbridge.config.CoverageInfo;
import longbridge.models.EntityId;
import java.util.List;


public interface AccountCoverageService {

    List<CoverageInfo> getCoverageDetails(EntityId id);

}
