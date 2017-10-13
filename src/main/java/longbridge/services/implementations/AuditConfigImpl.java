package longbridge.services.implementations;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import longbridge.config.audits.CustomRevisionEntity;
import longbridge.config.audits.ModifiedEntityTypeEntity;
import longbridge.config.audits.RevisedEntitiesUtil;
import longbridge.dtos.AuditDTO;
//import longbridge.dtos.RevisionInfo;
import longbridge.exception.InternetBankingException;
import longbridge.models.*;
import longbridge.repositories.AuditConfigRepo;
import longbridge.repositories.CustomRevisionEntityRepo;
import longbridge.repositories.ModifiedEntityTypeEntityRepo;
import longbridge.services.AuditConfigService;
import longbridge.utils.PrettySerializer;
import longbridge.utils.SerializeUtil;
import longbridge.utils.Verifiable;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.formula.functions.T;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.query.AuditQuery;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;

import static longbridge.utils.StringUtil.convertToJSON;

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
	public List<AuditConfig> getEntities() {
		return configRepo.findAllOrderByEntityNameAsc();
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
	public Page<AuditDTO> revisedEntity(String entityName,Pageable pageable){
		List<AuditDTO> compositeAudits = new ArrayList<>();
		Page<CustomRevisionEntity> revisionEntities = null;
		List<String> RevisionDetails = new ArrayList<>();
		Page<ModifiedEntityTypeEntity> allEnityByRevisionByClass = null;
		try
		{

			Class<?> clazz  = Class.forName(PACKAGE_NAME + entityName);
			String fullEntityName = PACKAGE_NAME + entityName;
			AuditReader auditReader = AuditReaderFactory.get(entityManager);
			allEnityByRevisionByClass = modifiedEntityTypeEntityRepo.findAllEnityByRevisionByClass(fullEntityName,pageable);
			for (ModifiedEntityTypeEntity entity: allEnityByRevisionByClass) {
				AuditDTO auditDTO = new AuditDTO();
				logger.info("revision id  "+entity.getRevision().getId());
				AuditQuery query = auditReader.createQuery().forEntitiesModifiedAtRevision(clazz,entity.getRevision().getId());
				List<AbstractEntity> abstractEntities = query.getResultList();
				ObjectMapper prettyMapper = new ObjectMapper();
				AbstractEntity abstractEntity = null;
				try {
					abstractEntity = abstractEntities.get(0);
					if (abstractEntity instanceof PrettySerializer) {
						JSONObject jsonObject = SerializeUtil.getPrettySerialJSON(abstractEntity);
						auditDTO.setFullEntity(jsonObject);
                    }else {
						JSONObject jsonObject = convertToJSON(abstractEntity.toString());
						auditDTO.setFullEntity(jsonObject);
					}
				} catch (IndexOutOfBoundsException e) {
					e.printStackTrace();
					continue;
				}
				auditDTO.setModifiedEntities(entity);
				compositeAudits.add(auditDTO);
			}
			logger.info("this is the revision list {} element is {}",allEnityByRevisionByClass.getTotalPages(),allEnityByRevisionByClass.getTotalElements());
//			Page<T> tPage = (Page<T>) compositeAudits;

		}
		catch (ClassNotFoundException e){
			e.printStackTrace();
		}
		catch (NumberFormatException e){
			e.printStackTrace();
		}catch (Exception e){
			e.printStackTrace();
		}

		return new PageImpl<AuditDTO>(compositeAudits, pageable, allEnityByRevisionByClass.getTotalElements());
	}
	public Page<AuditDTO> revisedEntityByQuery(String entityName,Pageable pageable){
		List<AuditDTO> compositeAudits = new ArrayList<>();
		Page<CustomRevisionEntity> revisionEntities = null;
		List<String> RevisionDetails = new ArrayList<>();
		Page<ModifiedEntityTypeEntity> allEnityByRevisionByClass = null;
		logger.info("the entity name {}",entityName);
		if(entityName.contains("TransRequest")|| entityName.contains("BulkTransfer")){
			entityName = "TransRequest";
		}
		try{
			Class<?> clazz  = Class.forName(PACKAGE_NAME + entityName);
			String fullEntityName = PACKAGE_NAME + entityName;
			AuditReader auditReader = AuditReaderFactory.get(entityManager);
			allEnityByRevisionByClass = modifiedEntityTypeEntityRepo.findAllEnityByRevisionByClass(fullEntityName,pageable);
			for (ModifiedEntityTypeEntity entity: allEnityByRevisionByClass) {
				AuditDTO auditDTO = new AuditDTO();
//				logger.info("revision id  "+entity.getRevision().getId());
				Map<String, Object> entityDetials = RevisedEntitiesUtil.getEntityDetailsById(entityName, entity.getRevision().getId());
//				logger.info("the entity details {}",entityDetials);
				JSONObject jsonObject = new JSONObject();;
				jsonObject.putAll(entityDetials);
//				logger.info("the entity details {}",jsonObject);
				auditDTO.setFullEntity(jsonObject);

				auditDTO.setModifiedEntities(entity);
				compositeAudits.add(auditDTO);
			}
	}
	catch (ClassNotFoundException e){
		e.printStackTrace();
	}
	catch (NumberFormatException e){
		e.printStackTrace();
	}catch (Exception e){
		e.printStackTrace();
	}
			logger.info("this is the revision list {} element is {}",allEnityByRevisionByClass.getTotalPages(),allEnityByRevisionByClass.getTotalElements());
//			Page<T> tPage = (Page<T>) compositeAudits;


		return new PageImpl<AuditDTO>(compositeAudits, pageable, allEnityByRevisionByClass.getTotalElements());
	}
	public Page<AuditDTO> searchRevisedEntity(String entityName,Pageable pageable,String search){
		List<AuditDTO> compositeAudits = new ArrayList<>();
		Page<CustomRevisionEntity> revisionEntities = null;
		List<String> RevisionDetails = new ArrayList<>();
		logger.info("entity name in {}",entityName);
		Timestamp ts = null;
		try {
			ts = Timestamp.valueOf(search);
			logger.info("the time stamp {}",ts);
		} catch (IllegalArgumentException e){

		}catch (Exception e) {
			e.printStackTrace();
		}
//		System.out.println(ts.getNanos());

		Page<ModifiedEntityTypeEntity> allEnityByRevisionByClass = null;
		try
		{

			Class<?> clazz  = Class.forName(PACKAGE_NAME + entityName);
			String fullEntityName = PACKAGE_NAME + entityName;
			AuditReader auditReader = AuditReaderFactory.get(entityManager);
			String timeStamp = "";
			if(ts == null){
				timeStamp = "";
			}else {
				timeStamp = ts.toString();
			}
			if(timeStamp.isEmpty()) {
				allEnityByRevisionByClass = modifiedEntityTypeEntityRepo.findAllEnityByRevisionBySearch(fullEntityName, pageable, search);

			}else {
				allEnityByRevisionByClass = modifiedEntityTypeEntityRepo.findAllEnityByRevisionBySearch(fullEntityName, pageable, search,ts.toString());
			}for (ModifiedEntityTypeEntity entity: allEnityByRevisionByClass) {
				AuditDTO auditDTO = new AuditDTO();
				logger.info("revision id  "+entity.getRevision().getId());
				AuditQuery query = auditReader.createQuery().forEntitiesModifiedAtRevision(clazz,entity.getRevision().getId());
				List<AbstractEntity> abstractEntities = query.getResultList();
				ObjectMapper prettyMapper = new ObjectMapper();

				AbstractEntity abstractEntity = null;
				try {
					abstractEntity = abstractEntities.get(0);
					if (abstractEntity instanceof PrettySerializer) {
						JSONObject jsonObject = SerializeUtil.getPrettySerialJSON(abstractEntity);
						auditDTO.setFullEntity(jsonObject);
					}
				} catch (IndexOutOfBoundsException e) {
					e.printStackTrace();
					continue;
				}
//				AuditDTO auditDTO = new AuditDTO();
//				logger.info("revision id  "+entity.getRevision().getId());
//				AuditQuery query = auditReader.createQuery().forEntitiesAtRevision(clazz,entity.getRevision().getId());
//
//				List<Object> abstractEntities = query.getResultList();
//				switch (entityName){
//					case "TransRequest":
//						TransRequest transRequest = (TransRequest) abstractEntities.get(0);
//						if (transRequest != null){
////						transRequest.getFinancialInstitution().getInstitutionName();
////						logger.info("The finanial institution {}",transRequest.getFinancialInstitution().getInstitutionName());
////							auditDTO.setFinacialInstitution(null);
//							transRequest.setFinancialInstitution(null);
//						}
//						auditDTO.setEntityDetails((Object)transRequest);
//						break;
//					case "AdminUser":
//						logger.info("this is the enitity name is "+entityName);
//						AdminUser adminUser = (AdminUser) abstractEntities.get(0);
//						adminUser.setRole(null);
//						logger.info("this is the enitity list {}",adminUser.getAlertPreference());
//						auditDTO.setEntityDetails((Object)adminUser);
//						break;
//					case "CorporateUser":
//						CorporateUser corporateUser = (CorporateUser) abstractEntities.get(0);
//						corporateUser.setCorporate(null);
//						corporateUser.setTempPassword(null);
//						corporateUser.setAlertPreference(null);
//						corporateUser.setRole(null);
//						auditDTO.setEntityDetails((Object)corporateUser);
////						parse(corporateUser.getSerializer().toString());
//						break;
//						default:
//							auditDTO.setEntityDetails(abstractEntities.get(0));
//							break;
//				}
				auditDTO.setModifiedEntities(entity);
				compositeAudits.add(auditDTO);
			}
			logger.info("this is the revision list {} element is {}",allEnityByRevisionByClass.getTotalPages(),allEnityByRevisionByClass.getTotalElements());
//			Page<T> tPage = (Page<T>) compositeAudits;

		}
		catch (ClassNotFoundException e){
			e.printStackTrace();
		}
		catch (NumberFormatException e){
			e.printStackTrace();
		}catch (Exception e){
			e.printStackTrace();
		}

		return new PageImpl<AuditDTO>(compositeAudits, pageable, allEnityByRevisionByClass.getTotalElements());
	}

}
