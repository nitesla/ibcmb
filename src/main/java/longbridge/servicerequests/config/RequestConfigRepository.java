package longbridge.servicerequests.config;

import longbridge.repositories.CommonRepo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public interface RequestConfigRepository extends CommonRepo<RequestConfig,Long> {

    Page<RequestConfig> findByType(String type, Pageable page);

    Page<RequestConfig> findByGroupId(Long owner, Pageable page);

    RequestConfig findFirstByName(String name);

}
