package longbridge.config.audits;

import longbridge.models.AuditConfig;
import longbridge.repositories.AuditConfigRepo;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.metamodel.EntityType;
import java.util.stream.Collectors;

@Component
public class AuditConfigInitializer implements InitializingBean {

    @Autowired
    EntityManager entityManager;

    @Autowired
    private AuditConfigRepo configRepo;

    @Transactional
    @Override
    public void afterPropertiesSet() throws Exception
    {
        try
        {
            entityManager.getEntityManagerFactory().getMetamodel().getEntities().stream()
                    .filter(i -> !i.getName().endsWith("AUD")).filter(i -> !i.getName().endsWith("Entity"))
                    .filter(i -> !i.getName().equalsIgnoreCase("AuditConfig")).map(EntityType::getName)
                    .collect(Collectors.toList()).forEach(e -> {
                if (!configRepo.existsByEntityName(e))
                {
                    AuditConfig entity = new AuditConfig();
                    entity.setEnabled("N");
                    entity.setEntityName(e);
                    configRepo.save(entity);
                }
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

}
