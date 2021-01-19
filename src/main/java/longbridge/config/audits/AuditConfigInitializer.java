package longbridge.config.audits;

import longbridge.models.AuditConfig;
import longbridge.repositories.AuditConfigRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.metamodel.EntityType;

@Component
public class AuditConfigInitializer implements InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuditConfigInitializer.class);
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private AuditConfigRepo configRepo;

    @Transactional
    @Override
    public void afterPropertiesSet() {
        try {
            entityManager.getEntityManagerFactory().getMetamodel().getEntities().stream().filter(e -> ok(e.getName())).forEach(this::saveCfg);
        } catch (Exception e) {
            LOGGER.error("Audit Initialization Error", e);

        }
    }

    private void saveCfg(EntityType<?> a) {
        String eName = a.getName();
        if (!configRepo.existsByEntityName(eName)) {
            AuditConfig entity = new AuditConfig();
            entity.setEnabled("N");
            entity.setEntityName(eName);
            entity.setFullName(a.getJavaType().getCanonicalName());
            configRepo.save(entity);
        }

    }

    private boolean ok(String name) {
        return !name.endsWith("AUD") && !name.endsWith("Entity") && !name.endsWith("AuditConfig");
    }

}
