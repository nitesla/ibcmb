package longbridge.services.implementations;

import longbridge.dtos.OperationsUserDTO;
import longbridge.exception.InternetBankingException;
import longbridge.models.OperationsUser;
import longbridge.models.Role;
import longbridge.repositories.OperationsUserRepo;
import longbridge.services.MailService;
import longbridge.services.OperationsUserService;
import longbridge.services.PasswordService;
import longbridge.utils.ReflectionUtils;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
<<<<<<< HEAD
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
=======
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
>>>>>>> stash
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

/**
 * Created by ayoade_farooq@yahoo.com on 3/29/2017. Modified by Fortune
 */
@Service
public class OperationsUserServiceImpl implements OperationsUserService {
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private OperationsUserRepo operationsUserRepo;
	private BCryptPasswordEncoder passwordEncoder;

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	MailService mailService;

	@Autowired
	PasswordService passwordService;

<<<<<<< HEAD
    @Autowired
    MessageSource messageSource;
    Locale locale = LocaleContextHolder.getLocale();

    public OperationsUserServiceImpl() {
=======
	public OperationsUserServiceImpl() {
>>>>>>> stash

	}

	@Override
	public boolean isValidUsername(String username) {
		boolean isValid = false;
		OperationsUser opsUser = operationsUserRepo.findFirstByUserName(username);
		if (opsUser == null) {
			isValid = true;
		}
		return isValid;
	}

<<<<<<< HEAD
    @Override
    public boolean isValidUsername(String username) {
        OperationsUser opsUser = operationsUserRepo.findFirstByUserName(username);
        return (opsUser == null) ? true : false;

    }
=======
	@Autowired
	public OperationsUserServiceImpl(OperationsUserRepo operationsUserRepo, BCryptPasswordEncoder passwordEncoder) {
		this.operationsUserRepo = operationsUserRepo;
		this.passwordEncoder = passwordEncoder;
	}
>>>>>>> stash

	@Override
	public OperationsUserDTO getUser(Long id) {
		OperationsUser user = operationsUserRepo.findOne(id);
		return convertEntityToDTO(user);
	}

	@Override
	public Iterable<OperationsUserDTO> getUsers() {
		Iterable<OperationsUser> operationsUsers = operationsUserRepo.findAll();
		return convertEntitiesToDTOs(operationsUsers);
	}

	@Override
	public void setPassword(OperationsUser user, String password) {

	}

<<<<<<< HEAD
    @Override
    public String setPassword(OperationsUser user, String password) throws InternetBankingException {
        return null;//TODO
    }
=======
	@Override
	@Transactional
	public void changeActivationStatus(Long userId) {
		OperationsUser user = operationsUserRepo.findOne(userId);
		String oldStatus = user.getStatus();
		String newStatus = "ACTIVE".equals(oldStatus) ? "INACTIVE" : "ACTIVE";
		user.setStatus(newStatus);
		operationsUserRepo.save(user);
		if ("INACTIVE".equals(oldStatus) && "ACTIVE".equals(newStatus)) {
			String password = passwordService.generatePassword();
			user.setPassword(passwordEncoder.encode(password));
			mailService.send(user.getEmail(),
					String.format("Your new password to Operations console is %s and your current username is %s",
							password, user.getUserName()));
		}
		logger.info("Operations user {} status changed from {} to {}", user.getUserName(), oldStatus, newStatus);
	}

	@Override
	public OperationsUserDTO getUserByName(String name) {
		OperationsUser opsUser = this.operationsUserRepo.findFirstByUserName(name);
		return convertEntityToDTO(opsUser);
	}
>>>>>>> stash

<<<<<<< HEAD
    @Override
    @Transactional
    public String changeActivationStatus(Long userId) throws InternetBankingException {
        OperationsUser user = operationsUserRepo.findOne(userId);
        String oldStatus = user.getStatus();
        String newStatus = "ACTIVE".equals(oldStatus) ? "INACTIVE" : "ACTIVE";
        user.setStatus(newStatus);
        operationsUserRepo.save(user);
        if ("INACTIVE".equals(oldStatus) && "ACTIVE".equals(newStatus)) {
            String password = passwordService.generatePassword();
            user.setPassword(passwordEncoder.encode(password));
            mailService.send(user.getEmail(),"Mail from Internet Banking", String.format("Your new password to Operations console is %s and your current username is %s", password, user.getUserName()));
        }
        logger.info("Operations user {} status changed from {} to {}", user.getUserName(), oldStatus, newStatus);
        return messageSource.getMessage("user.status.success", null, locale);
    }
=======
	@Override
	@Transactional
	public boolean addUser(OperationsUserDTO user) {
		boolean ok = false;
		if (user != null) {
			OperationsUser opsUser = new OperationsUser();
			opsUser.setFirstName(user.getFirstName());
			opsUser.setLastName(user.getLastName());
			opsUser.setUserName(user.getUserName());
			opsUser.setEmail(user.getEmail());
			opsUser.setDateCreated(new Date());
			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) + 2);
			opsUser.setExpiryDate(calendar.getTime());
			Role role = new Role();
			role.setId(Long.parseLong(user.getRoleId()));
			opsUser.setRole(role);
			String password = passwordService.generatePassword();
			opsUser.setPassword(passwordEncoder.encode(password));
			this.operationsUserRepo.save(opsUser);
			mailService.send(user.getEmail(),
					String.format("Your username is %s and password is %s", user.getUserName(), password));
			ok = true;
			logger.info("New operations user: {} created", opsUser.getUserName());
>>>>>>> stash

		} else {
			logger.error("Aborted Operations user creation. NULL user supplied");
		}
		return ok;

<<<<<<< HEAD
    @Override
    public OperationsUser getUserByName(String name) {
        OperationsUser opsUser = this.operationsUserRepo.findFirstByUserName(name);
        return opsUser;
    }
=======
	}
>>>>>>> stash

<<<<<<< HEAD
    @Override
    @Transactional
    public String addUser(OperationsUserDTO user) throws InternetBankingException {
        OperationsUser opsUser = new OperationsUser();
        opsUser.setFirstName(user.getFirstName());
        opsUser.setLastName(user.getLastName());
        opsUser.setUserName(user.getUserName());
        opsUser.setEmail(user.getEmail());
        opsUser.setDateCreated(new Date());
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) + 2);
        opsUser.setExpiryDate(calendar.getTime());
        Role role = new Role();
        role.setId(Long.parseLong(user.getRoleId()));
        opsUser.setRole(role);
        String password = passwordService.generatePassword();
        opsUser.setPassword(passwordEncoder.encode(password));
        this.operationsUserRepo.save(opsUser);
=======
	@Override
	@Transactional
	public boolean updateUser(OperationsUserDTO userDTO) {
		boolean ok = false;
		if (userDTO != null) {
			OperationsUser operationsUser = new OperationsUser();
			operationsUser.setId(userDTO.getId());
			operationsUser.setVersion(userDTO.getVersion());
			operationsUser.setFirstName(userDTO.getFirstName());
			operationsUser.setLastName(userDTO.getLastName());
			operationsUser.setUserName(userDTO.getUserName());
			Role role = new Role();
			role.setId(Long.parseLong(userDTO.getRoleId()));
			operationsUser.setRole(role);
			operationsUserRepo.save(operationsUser);
			ok = true;
			logger.info("Operations user {} updated", operationsUser.getUserName());
>>>>>>> stash

<<<<<<< HEAD
        mailService.send(user.getEmail(), "Mail from Internet banking",String.format("Your username is %s and password is %s", user.getUserName(), password));
        logger.info("New operations user: {} created", opsUser.getUserName());
        return messageSource.getMessage("user.create.success", null, locale);
=======
		} else {
			logger.error("Aborted Operations user update. NULL user supplied");
		}
		return ok;
	}
>>>>>>> stash

	@Override
	public void deleteUser(Long userId) {
		operationsUserRepo.delete(userId);
		logger.warn("Operations user with Id {} deleted", userId);

<<<<<<< HEAD
    @Override
    @Transactional
    public String updateUser(OperationsUserDTO userDTO) throws InternetBankingException {
        OperationsUser operationsUser = new OperationsUser();
        operationsUser.setId(userDTO.getId());
        operationsUser.setVersion(userDTO.getVersion());
        operationsUser.setFirstName(userDTO.getFirstName());
        operationsUser.setLastName(userDTO.getLastName());
        operationsUser.setUserName(userDTO.getUserName());
        Role role = new Role();
        role.setId(Long.parseLong(userDTO.getRoleId()));
        operationsUser.setRole(role);
        operationsUserRepo.save(operationsUser);
        logger.info("Operations user {} updated", operationsUser.getUserName());
        return messageSource.getMessage("user.update.success", null, locale);
    }
=======
	}

	@Override
	@Transactional
	public boolean resetPassword(Long id) {
		boolean ok = false;
		OperationsUser user = operationsUserRepo.findOne(id);
>>>>>>> stash

		if (user != null) {
			String newPassword = passwordService.generatePassword();
			user.setPassword(passwordEncoder.encode(newPassword));
			user.setExpiryDate(new Date());
			operationsUserRepo.save(user);
			mailService.send(user.getEmail(), "Your new password to Internet Banking is " + newPassword);
			ok = true;
			logger.info("Operations user {} password reset successfully", user.getUserName());

<<<<<<< HEAD
    @Override
    public String deleteUser(Long userId) throws InternetBankingException {
        operationsUserRepo.delete(userId);
        logger.warn("Operations user with Id {} deleted", userId);
        return messageSource.getMessage("user.delete.success", null, locale);

=======
		}
		return ok;
	}
>>>>>>> stash

	@Override
	@Transactional
	public boolean changePassword(OperationsUserDTO user, String oldPassword, String newPassword) {
		boolean ok = false;

<<<<<<< HEAD
    @Override
    @Transactional
    public String resetPassword(Long id) throws InternetBankingException {
        OperationsUser user = operationsUserRepo.findOne(id);
        String newPassword = passwordService.generatePassword();
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setExpiryDate(new Date());
        operationsUserRepo.save(user);
        mailService.send(user.getEmail(),"Mail form Internet banking", "Your new password to Internet Banking is " + newPassword);
        logger.info("Operations user {} password reset successfully", user.getUserName());
        return messageSource.getMessage("password.reset.success", null, locale);
    }
=======
		try {
			if (this.passwordEncoder.matches(oldPassword, user.getPassword())) {
				OperationsUser opsUser = operationsUserRepo.findOne(user.getId());
				opsUser.setPassword(this.passwordEncoder.encode(newPassword));
				this.operationsUserRepo.save(opsUser);
				logger.info("User {}'s password has been updated", user.getId());
				ok = true;
			} else {
				logger.error("Could not change password for operations user {} due to incorrect old password",
						user.getUserName());
			}
		} catch (Exception e) {
			logger.error("Aborted password change{}", e.toString());
		}
		return ok;
	}

	@Override
	public void generateAndSendPassword(OperationsUser user) {
		// TODO

	}
>>>>>>> stash

<<<<<<< HEAD
    @Override
    @Transactional
    public String changePassword(OperationsUser user, String oldPassword, String newPassword) throws InternetBankingException {
=======
	private OperationsUserDTO convertEntityToDTO(OperationsUser operationsUser) {
		return modelMapper.map(operationsUser, OperationsUserDTO.class);
	}
>>>>>>> stash

<<<<<<< HEAD
        if (this.passwordEncoder.matches(oldPassword, user.getPassword())) {
            OperationsUser opsUser = operationsUserRepo.findOne(user.getId());
            opsUser.setPassword(this.passwordEncoder.encode(newPassword));
            this.operationsUserRepo.save(opsUser);
            logger.info("User {}'s password has been updated", user.getId());

        } else {
            logger.error("Could not change password for operations user {} due to incorrect old password", user.getUserName());
        }

        return messageSource.getMessage("password.cahnge.success", null, locale);
    }
=======
	private OperationsUser convertDTOToEntity(OperationsUserDTO operationsUserDTO) {
		return modelMapper.map(operationsUserDTO, OperationsUser.class);
	}
>>>>>>> stash

<<<<<<< HEAD
=======
	private List<OperationsUserDTO> convertEntitiesToDTOs(Iterable<OperationsUser> operationsUsers) {
		List<OperationsUserDTO> operationsUserDTOList = new ArrayList<>();
		for (OperationsUser operationsUser : operationsUsers) {
			OperationsUserDTO userDTO = convertEntityToDTO(operationsUser);
			userDTO.setRole(operationsUser.getRole().getName());
			operationsUserDTOList.add(userDTO);
		}
		return operationsUserDTOList;
	}
>>>>>>> stash

<<<<<<< HEAD
    @Override
    public String generateAndSendPassword(OperationsUser user) throws InternetBankingException {
        return null;//TODO
    }
=======
	@Override
	public Page<OperationsUserDTO> getUsers(Pageable pageDetails) {
		Page<OperationsUser> page = operationsUserRepo.findAll(pageDetails);
		List<OperationsUserDTO> dtOs = convertEntitiesToDTOs(page.getContent());
		long t = page.getTotalElements();
		Page<OperationsUserDTO> pageImpl = new PageImpl<OperationsUserDTO>(dtOs, pageDetails, t);
		return pageImpl;
	}
>>>>>>> stash

<<<<<<< HEAD
    private OperationsUserDTO convertEntityToDTO(OperationsUser operationsUser) {
        return modelMapper.map(operationsUser, OperationsUserDTO.class);
    }

    private OperationsUser convertDTOToEntity(OperationsUserDTO operationsUserDTO) {
        return modelMapper.map(operationsUserDTO, OperationsUser.class);
    }

    private List<OperationsUserDTO> convertEntitiesToDTOs(Iterable<OperationsUser> operationsUsers) {
        List<OperationsUserDTO> operationsUserDTOList = new ArrayList<>();
        for (OperationsUser operationsUser : operationsUsers) {
            OperationsUserDTO userDTO = convertEntityToDTO(operationsUser);
            userDTO.setRole(operationsUser.getRole().getName());
            operationsUserDTOList.add(userDTO);
        }
        return operationsUserDTOList;
    }

    @Override
    public Page<OperationsUserDTO> getUsers(Pageable pageDetails) {
        Page<OperationsUser> page = operationsUserRepo.findAll(pageDetails);
        List<OperationsUserDTO> dtOs = convertEntitiesToDTOs(page.getContent());
        long t = page.getTotalElements();

        Page<OperationsUserDTO> pageImpl = new PageImpl<OperationsUserDTO>(dtOs, pageDetails, t);
        return pageImpl;
    }

=======
	@Override
	public Page<OperationsUserDTO> findUsers(OperationsUserDTO example, Pageable pageDetails) {
		ExampleMatcher matcher = ExampleMatcher.matchingAll().withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
				.withIgnoreCase().withIgnorePaths("version","noOfAttempts").withIgnoreNullValues();
		OperationsUser entity = convertDTOToEntity(example);
		ReflectionUtils.nullifyStrings(entity,1);
		Page<OperationsUser> page = operationsUserRepo.findAll(Example.of(entity, matcher), pageDetails);
		List<OperationsUserDTO> dtOs = convertEntitiesToDTOs(page.getContent());
		long t = page.getTotalElements();
		Page<OperationsUserDTO> pageImpl = new PageImpl<OperationsUserDTO>(dtOs, pageDetails, t);
		return pageImpl;
	}
>>>>>>> stash

}
