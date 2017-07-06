package longbridge.services.implementations;

import longbridge.config.audits.CustomRevisionEntity;
import longbridge.dtos.CodeDTO;
import longbridge.exception.InternetBankingException;
import longbridge.models.AuditConfig;
import longbridge.models.AuditRetrieve;
import longbridge.models.Code;
import longbridge.repositories.AuditConfigRepo;
import longbridge.repositories.CustomRevisionEntityRepo;
import longbridge.services.AuditConfigService;
import longbridge.utils.Verifiable;
import org.apache.poi.ss.formula.functions.T;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.query.AuditEntity;
import org.hibernate.envers.query.AuditQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ayoade_farooq@yahoo.com on 4/19/2017.
 */
@Service
public class AuditConfigImpl implements AuditConfigService {

	private static final String PACKAGE_NAME = "longbridge.models.";

	private Logger logger = LoggerFactory.getLogger(this.getClass());


    @Autowired
    private AuditConfigRepo configRepo;
    @Autowired
	EntityManager entityManager;

    @Autowired
	CustomRevisionEntityRepo customRevisionEntityRepo;


	public AuditConfig findEntity(String s) {
		return configRepo.findFirstByEntityName(s);
	}

	@Override
	public Iterable<AuditConfig> getAllEntities() {
		return configRepo.findAll();
	}

	@Override
	@Verifiable(operation="AUDIT_CONFIG",description="Configuring Audit")
    public boolean saveAuditConfig(AuditConfig cfg) throws InternetBankingException
	{
		configRepo.save(cfg);
		return true;
	}

	@Override
	public Page<AuditConfig> getEntities(Pageable pageDetails) {
		return configRepo.findAll(pageDetails);
	}

	@Override
	public List<T> revisedEntity(String entityName)
	{
		List<T> revisionList = new ArrayList<>();
		List<CustomRevisionEntity> revID=new ArrayList<>();
		try
		{
			Class<?> clazz  = Class.forName(PACKAGE_NAME + entityName);

			AuditReader auditReader = AuditReaderFactory.get(entityManager);
			AuditQuery query = auditReader.createQuery().forRevisionsOfEntity(clazz, true, true);
			revisionList = query.getResultList();
			query.addProjection(AuditEntity.revisionNumber());
			revisionList = query.getResultList();
			query.getResultList();
			revID=customRevisionEntityRepo.findCustomRevisionId(revisionList);
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}

		return revisionList;
	  }
	@Override
	public Map revisedEntityDetails(String entityName)
	{
		List<T> revisionList = new ArrayList<>();
		List<CustomRevisionEntity> revID=new ArrayList<>();
		Map<String,Object> classAuditDetails =  new HashMap<>();
		try
		{
			Class<?> clazz  = Class.forName(PACKAGE_NAME + entityName);

			AuditReader auditReader = AuditReaderFactory.get(entityManager);
			AuditQuery query = auditReader.createQuery().forRevisionsOfEntity(clazz, true, true);
			revisionList = query.getResultList();
			classAuditDetails.put("classDetials",revisionList);
			query.addProjection(AuditEntity.revisionNumber());
			revisionList = query.getResultList();
			classAuditDetails.put("revisionNumbers",revisionList);
			query.getResultList();
			revID=customRevisionEntityRepo.findCustomRevisionId(revisionList);
			classAuditDetails.put("revisionDetails",revID);
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}

		return classAuditDetails;
	}
	@Override
	public Page<AuditConfig> findEntities(String pattern, Pageable pageDetails) {
		return configRepo.findUsingPattern(pattern,pageDetails);
	}



	@Override
	public AuditConfig getAuditEntity(Long id)
	{
		AuditConfig auditConfig = this.configRepo.findOne(id);
		return auditConfig;
	}
}
