package longbridge.services.implementations;

import com.fasterxml.jackson.databind.ObjectMapper;
import longbridge.config.audits.CustomRevisionEntity;
import longbridge.config.audits.ModifiedEntityTypeEntity;
import longbridge.config.audits.RevisedEntitiesUtil;
import longbridge.dtos.AuditDTO;
//import longbridge.dtos.RevisionInfo;
import longbridge.dtos.AuditSearchDTO;
import longbridge.exception.InternetBankingException;
import longbridge.models.*;
import longbridge.repositories.AuditConfigRepo;
import longbridge.repositories.AuditRepoImpl;
import longbridge.repositories.CustomRevisionEntityRepo;
import longbridge.repositories.ModifiedEntityTypeEntityRepo;
import longbridge.services.AuditConfigService;
import longbridge.utils.PrettySerializer;
import longbridge.utils.SerializeUtil;
import longbridge.utils.StringUtil;
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
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;

import static longbridge.config.audits.RevisedEntitiesUtil.getSearchedModifiedEntity;
import static longbridge.utils.StringUtil.convertFieldToTitle;
import static longbridge.utils.StringUtil.convertToJSON;
import static longbridge.utils.StringUtil.searchModifiedEntityTypeEntity;

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
	AuditRepoImpl auditRepo;
	@Autowired
	CustomRevisionEntityRepo customRevisionEntityRepo;

	@Autowired
	ModifiedEntityTypeEntityRepo modifiedEntityTypeEntityRepo;
//	SessionFactory sessionFactory = entityManager.unwrap(SessionFactory.class);
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
//		Page<ModifiedEntityTypeEntity> page = modifiedEntityTypeEntityRepo.findUsingPattern(pattern, pageDetails);
		//Page<ModifiedEntityTypeEntity> pageImpl = new PageImpl<AdminUserDTO>(dtOs,pageDetails,t);
		return null;
	}

	@Override
	public Page<ModifiedEntityTypeEntity> getRevisionEntities(String pattern, Pageable pageDetails)
	{
//		Page<ModifiedEntityTypeEntity> page=modifiedEntityTypeEntityRepo.findUsingPattern(pattern,pageDetails);
		return null;
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
//						auditDTO.setFullEntity(jsonObject);
					}else {
//						JSONObject jsonObject = convertToJSON(abstractEntity.toString());
//						auditDTO.setFullEntity(jsonObject);
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
				Map<String, Object> entityDetials = null;
					entityDetials = RevisedEntitiesUtil.getEntityDetailsById(entityName, entity.getRevision().getId());
				JSONObject jsonObject = new JSONObject();;
				jsonObject.putAll(entityDetials);
				if(entityName.contains("Beneficiary")){
					Map<String, Object> currentEntityDetails = RevisedEntitiesUtil.getCurrentEntityDetails(entityName, (BigDecimal) jsonObject.get("ID"));
					jsonObject.clear();
					jsonObject.putAll(currentEntityDetails);
				}
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
		}catch (InvalidDataAccessApiUsageException e){
			e.printStackTrace();
		}catch (Exception e){
			e.printStackTrace();
		}
		logger.info("this is the revision list {} element is {}",allEnityByRevisionByClass.getTotalPages(),allEnityByRevisionByClass.getTotalElements());
		return new PageImpl<AuditDTO>(compositeAudits, pageable, allEnityByRevisionByClass.getTotalElements());
	}


	public Page<AuditDTO> searchRevisedEntity(String entityName,Pageable pageable,String search){
		List<AuditDTO> compositeAudits = new ArrayList<>();
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
		if(entityName.contains("TransRequest")|| entityName.contains("BulkTransfer")){
			entityName = "TransRequest";
		}
		Page<ModifiedEntityTypeEntity> allEnityByRevisionByClass = null;
		Collection<Integer> revIds = new ArrayList<>();
		try
		{

			Class<?> clazz  = Class.forName(PACKAGE_NAME + entityName);
			if(entityName.contains("Beneficiary")) {
				revIds = RevisedEntitiesUtil.getSearchedAndMergedRevisedEntityID(entityName, clazz, search);
			}else {
				revIds = RevisedEntitiesUtil.getSearchedRevisedEntityID(entityName, clazz, search);
			}
			String fullEntityName = PACKAGE_NAME + entityName;
			AuditReader auditReader = AuditReaderFactory.get(entityManager);
			String timeStamp = "";
			if(ts == null){
				timeStamp = "";
			}else {
				timeStamp = ts.toString();
			}
			if(timeStamp.isEmpty()) {
				allEnityByRevisionByClass = modifiedEntityTypeEntityRepo.findEnityByRevisionBySearch(pageable,fullEntityName, search,revIds);

			}else {
				allEnityByRevisionByClass = modifiedEntityTypeEntityRepo.findEnityByRevisionBySearch(pageable, search,revIds,ts.toString());
			}
			for (ModifiedEntityTypeEntity entity: allEnityByRevisionByClass) {
			AuditDTO auditDTO = new AuditDTO();
			Map<String, Object> entityDetials = RevisedEntitiesUtil.getEntityDetailsById(entityName, entity.getRevision().getId());
			JSONObject jsonObject = new JSONObject();;
			jsonObject.putAll(entityDetials);
				if(entityName.contains("Beneficiary")){
					Map<String, Object> currentEntityDetails = RevisedEntitiesUtil.getCurrentEntityDetails(entityName, (BigDecimal) jsonObject.get("ID"));
					jsonObject.clear();
					jsonObject.putAll(currentEntityDetails);
				}
			auditDTO.setFullEntity(jsonObject);
			auditDTO.setModifiedEntities(entity);
			compositeAudits.add(auditDTO);
		}
			logger.info("this is the revision list {} element is {}",allEnityByRevisionByClass.getTotalPages(),allEnityByRevisionByClass.getTotalElements());
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

	@Override
	public Map<String, Object> getFormatedEntityDetails(String entityName) {
		String className = PACKAGE_NAME+entityName;
		Class<?> cl = null;
		Map<String,Object> map = new HashMap<>();
		try {

			List<String> classFields =  new ArrayList<>();
			List<String> headers =  new ArrayList<>();
			cl = Class.forName(className);
			Class<?> superclass = cl.getSuperclass();
			Field[] declaredFields = cl.getDeclaredFields();
			//DTYPE is a unique fields the is not attached to the model, needed to gotten seperately
			if(entityName.contains("TransRequest")){
				headers.add("DTYPE");
				classFields.add("fullEntity.DTYPE");
			}
			for (Field field:declaredFields) {
				String fieldName = StringUtil.extractedFieldName(field.toString());
				if(fieldName.equalsIgnoreCase("serialVersionUID")) {
					continue;
				}
				if(entityName.equalsIgnoreCase("CorporateUser")||entityName.equalsIgnoreCase("RetailUser")){
					if(fieldName.equalsIgnoreCase("corporate") || fieldName.equalsIgnoreCase("tempPassword") ){
						continue;
					}
				}

				Map<String, Object> fieldsNameAfterCheck = StringUtil.getFieldsNameAfterCheck(field, fieldName);
				logger.info("the fieldsName {}	",fieldsNameAfterCheck);
				if(!(boolean) fieldsNameAfterCheck.get("ignoreField")) {
					headers.add(convertFieldToTitle(fieldName));
					classFields.add("fullEntity." + fieldsNameAfterCheck.get("field"));
				}
			}
			Map<String, List<String>> allFields = StringUtil.addSupperClassFields(superclass, headers, classFields);
			headers = allFields.get("headers");
			classFields = allFields.get("classFields");
			if(!classFields.isEmpty()) {

				map.put("fields",classFields);
			}else {
				map.put("fields",null);
			}
			map.put("headers",headers);
			map.put("headerSize",headers.size());
		} catch (ClassNotFoundException e) {
			map.put("fields",null);
			map.put("headers","");
			map.put("headerSize",0);
			e.printStackTrace();
		}
		return map;
	}
	public List<ModifiedEntityTypeEntity> getAll(){
//		Session session = sessionFactory.getCurrentSession();
//		Query query =  session.createQuery("from ModifiedEntityTypeEntity");
//		List<ModifiedEntityTypeEntity> branchList = query.list();
//		logger.info("the list returned {}",branchList.get(0));
		return null;
	}
	@Override
	public Page<ModifiedEntityTypeEntity> searchModifiedEntity(AuditSearchDTO auditSearchDTO, Pageable pageable){
		List<ModifiedEntityTypeEntity> searchedModifiedEntity = getSearchedModifiedEntity(auditSearchDTO);
		long searchSize = Long.valueOf(searchedModifiedEntity.size());
		return new PageImpl<ModifiedEntityTypeEntity>(searchedModifiedEntity, pageable, searchSize);
	}
	@Override
	public Page<ModifiedEntityTypeEntity> searchMod(Pageable pageable, AuditSearchDTO auditSearchDTO){

		Page<ModifiedEntityTypeEntity> modifiedEntityTypeEntities = auditRepo.findModifiedEntityBySearch(pageable,auditSearchDTO);
	return modifiedEntityTypeEntities;
	}
}
