package longbridge.services.implementations;

import longbridge.models.AuditConfig;
import longbridge.repositories.AuditConfigRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by ayoade_farooq@yahoo.com on 4/19/2017.
 */
@Service
public class AuditConfigImpl {

    @Autowired
    private AuditConfigRepo configRepo;

	public AuditConfig findEntity(String s) {
		return configRepo.findFirstByEntityName(s);
	}

	@Override
	public Iterable<AuditConfig> getAllEntities() {
		List<String> tables = new ArrayList<>();
		entityManager.getEntityManagerFactory().getMetamodel().getEntities().stream()
				.filter(i -> !i.getName().endsWith("AUD")).filter(i -> !i.getName().endsWith("Entity"))
				.filter(i -> !i.getName().equalsIgnoreCase("AuditConfig")).map(i -> i.getName())
				.collect(Collectors.toList()).forEach(e -> tables.add(e));

		// .forEach(i ->tables.add(i.getName()));

		Collections.sort(tables);
		List<AuditConfig> configs = new ArrayList<AuditConfig>();
		for(String s : tables){
			AuditConfig entity = configRepo.findFirstByEntityName(s);
			if(entity == null){
				//not existing
				entity = new AuditConfig();
				entity.setEnabled("N");
				entity.setEntityName(s);
			}
			configs.add(entity);
		}
		return configs;
	}

	@Override
	public boolean saveAuditConfig(AuditConfig cfg) {
		configRepo.save(cfg);
		return true;
	}
}
