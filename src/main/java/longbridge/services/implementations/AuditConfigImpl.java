package longbridge.services.implementations;

import longbridge.config.audits.CustomRevisionEntity;
import longbridge.config.audits.ModifiedEntityTypeEntity;
import longbridge.config.audits.RevisedEntitiesUtil;
import longbridge.dtos.AdminUserDTO;
import longbridge.dtos.AuditDTO;
import longbridge.dtos.CodeDTO;
//import longbridge.dtos.RevisionInfo;
import longbridge.dtos.VerificationDTO;
import longbridge.exception.InternetBankingException;
import longbridge.models.*;
import longbridge.repositories.AuditConfigRepo;
import longbridge.repositories.CustomRevisionEntityRepo;
import longbridge.repositories.ModifiedEntityTypeEntityRepo;
import longbridge.security.userdetails.CustomUserPrincipal;
import longbridge.services.AuditConfigService;
import longbridge.utils.Verifiable;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.poi.ss.formula.functions.T;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.query.AuditEntity;
import org.hibernate.envers.query.AuditQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Math.E;

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

    @Autowired
	ModifiedEntityTypeEntityRepo modifiedEntityTypeEntityRepo;




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
	public Page<ModifiedEntityTypeEntity> audit(String pattern, Pageable pageDetails)
	{
		Page<ModifiedEntityTypeEntity> page = modifiedEntityTypeEntityRepo.findUsingPattern(pattern, pageDetails);
		//Page<ModifiedEntityTypeEntity> pageImpl = new PageImpl<AdminUserDTO>(dtOs,pageDetails,t);
		return page;
	}

	@Override
	public Page<ModifiedEntityTypeEntity> getRevisionEntities(String pattern, Pageable pageDetails)
	{
		Page<ModifiedEntityTypeEntity> page=modifiedEntityTypeEntityRepo.findUsingPattern(pattern,pageDetails);
		return page;
	}

	public Page<ModifiedEntityTypeEntity> getRevisionEntities(Pageable pageable)
	{
		Page<ModifiedEntityTypeEntity> modifiedEntityTypeEntities=modifiedEntityTypeEntityRepo.findAll(pageable);


		return modifiedEntityTypeEntities;
	}
	public Page<ModifiedEntityTypeEntity> getRevisionEntitiesByDate(Pageable pageable)
	{

		Page<ModifiedEntityTypeEntity> modifiedEntityTypeEntities=modifiedEntityTypeEntityRepo.findAllEnityByRevision(pageable);
		return modifiedEntityTypeEntities;
	}
	@Override
	public Page<T>  revisedEntityDetails(String entityName,String revisionNo,Pageable pageable){
		List<Object> revisionList = new ArrayList<>();
		Page<CustomRevisionEntity> revisionEntities=null;
		List<String> RevisionDetails = new ArrayList<>();
		String className = PACKAGE_NAME + entityName;
		try
		{
			Class<?> clazz  = Class.forName(PACKAGE_NAME + entityName);
			AuditReader auditReader = AuditReaderFactory.get(entityManager);
			Integer revNo=Integer.parseInt(revisionNo);
			AuditQuery query = auditReader.createQuery().forEntitiesAtRevision(clazz,revNo);
			revisionList = query.getResultList();
			logger.info("this is the revision list"+query);
			logger.info("this is the revision list"+revisionList);
			List<T> classes = query.getResultList();

		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (NumberFormatException e)
		{
			e.printStackTrace();
		}

		return null;
	}
	private List<String> getEachDetails(String details)
	{
		String[] classDetails = StringUtils.substringsBetween(details,"'","'");
		return Arrays.asList(classDetails);
	}

	@Override
	public Page<ModifiedEntityTypeEntity> getRevisedEntitiesDetails(Integer id,Pageable pageable)
	{
		System.out.println("this is the page"+id);

		CustomRevisionEntity revisionEntity = customRevisionEntityRepo.findUniqueCustomEnity(id);

		Page<ModifiedEntityTypeEntity> modifiedEntityTypeEntities=modifiedEntityTypeEntityRepo.findEnityByRevision(revisionEntity,pageable);

		return modifiedEntityTypeEntities;

	}
	public Page<ModifiedEntityTypeEntity> getRevisedDetailsForEntity(Integer id,String classname,Pageable pageable)
	{

		List<Integer> revIds = RevisedEntitiesUtil.revisedEntityDetails(classname,id);
		List<CustomRevisionEntity> revisionEntity = customRevisionEntityRepo.findCustomEnityDetails(revIds);
		classname = PACKAGE_NAME+classname;
		Page<ModifiedEntityTypeEntity> modifiedEntityTypeEntities=modifiedEntityTypeEntityRepo.findEnityByRevisions(revisionEntity,pageable,classname);
		return modifiedEntityTypeEntities;

	}

	private List<String> getHeaders(String details){
		List<String> headerDetails = new ArrayList<>();
		String firstHeader = StringUtils.substringBetween(details,"{","'");
		headerDetails.add(firstHeader);
		String[] midItems = StringUtils.substringsBetween(details,", ","=");
		for (String midItem:midItems) {
			headerDetails.add(midItem);
		}
		String lastHeader= StringUtils.substringBetween(details,"{","'");
		headerDetails.add(lastHeader);
		return null;

	}
//	@Override
//	public Page<CustomRevisionEntity>  revisedEntityDetails(String entityName,Pageable pageable)
//	{
//		List<T> revisionList = new ArrayList<>();
//		Page<CustomRevisionEntity> revisionEntities=null;
//		try
//		{
//			logger.info("this is the revision list",revisionEntities);
//			Class<?> clazz  = Class.forName(PACKAGE_NAME + entityName);
//			AuditReader auditReader = AuditReaderFactory.get(entityManager);
//			AuditQuery query = auditReader.createQuery().forRevisionsOfEntity(clazz, true, true);
//			revisionList = query.getResultList();
//			revisionEntities=customRevisionEntityRepo.findCustomRevisionId(revisionList,pageable);
//			logger.info("this is the revision list",revisionEntities);
//			query.getResultList();
//		}
//		catch (ClassNotFoundException e)
//		{
//			e.printStackTrace();
//		}
//
//		return revisionEntities;
//	}
	@Override
	public Page<AuditConfig> findEntities(String pattern, Pageable pageDetails)
	{
		return configRepo.findUsingPattern(pattern,pageDetails);

	}



	@Override
	public AuditConfig getAuditEntity(Long id)
	{
		AuditConfig auditConfig = this.configRepo.findOne(id);
		return auditConfig;
	}

	@Override
	public List<T> revisedEntity(String entityName) {
		List<Object> revisionList = new ArrayList<>();
		Page<CustomRevisionEntity> revisionEntities = null;
		List<String> RevisionDetails = new ArrayList<>();
		List<T> classes = null;
		try
		{
			Class<?> clazz  = Class.forName(PACKAGE_NAME + entityName);
			AuditReader auditReader = AuditReaderFactory.get(entityManager);
			List<Number> revisions = auditReader.getRevisions(clazz, 32);
			AuditQuery query = auditReader.createQuery().forRevisionsOfEntity(clazz,true,true);
			revisionList = query.getResultList();
			logger.info("this is the revision list"+revisionList);
			List<Code> codes = query.getResultList();
			for (Code code:codes) {

				logger.info("this is the class code "+code);
			}
//			getEachDetails(String.valueOf(revisionList));

		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (NumberFormatException e){
			e.printStackTrace();
		}

		return classes;
	}
	public Page<AuditDTO> revisedEntity(String entityName,Pageable pageable) {
		List<AuditDTO> compositeAudits = new ArrayList<>();
		Page<CustomRevisionEntity> revisionEntities = null;
		List<String> RevisionDetails = new ArrayList<>();
		Page<ModifiedEntityTypeEntity> allEnityByRevisionByClass = null;
		try
		{

			Class<?> clazz  = Class.forName(PACKAGE_NAME + entityName);
			entityName = PACKAGE_NAME + entityName;
			AuditReader auditReader = AuditReaderFactory.get(entityManager);
			allEnityByRevisionByClass = modifiedEntityTypeEntityRepo.findAllEnityByRevisionByClass(entityName,pageable);
			for (ModifiedEntityTypeEntity entity: allEnityByRevisionByClass) {
				AuditDTO auditDTO = new AuditDTO();
				logger.info("revision id  "+entity.getRevision().getId());
				AuditQuery query = auditReader.createQuery().forEntitiesAtRevision(clazz,entity.getRevision().getId());
				List<Object> abstractEntities = query.getResultList();
				logger.info("the abstract entity {}",abstractEntities);
				auditDTO.setEntityDetails(abstractEntities.get(0));
				auditDTO.setModifiedEntities(entity);
				compositeAudits.add(auditDTO);
			}
			logger.info("this is the revision list"+compositeAudits);
//			Page<T> tPage = (Page<T>) compositeAudits;

		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (NumberFormatException e){
			e.printStackTrace();
		}catch (Exception e){
			e.printStackTrace();
		}

		return new PageImpl<AuditDTO>(compositeAudits, pageable, allEnityByRevisionByClass.getTotalPages());
	}
}
