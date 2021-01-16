package longbridge.config;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import groovy.lang.Lazy;
import longbridge.models.*;
import longbridge.repositories.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Component
@ConditionalOnProperty(
        value = "icon.boot",
        havingValue = "true",
        matchIfMissing = false)
public class AppInit implements InitializingBean {
    Logger logger = LoggerFactory.getLogger(this.getClass());


    @Autowired
    private AdminUserRepo adminUserRepo;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    @Lazy
    private PermissionRepo permissionRepo;
    @Autowired
    @Lazy
    private RoleRepo rolerepository;

    @Autowired
    @Lazy
    private SettingRepo settingRepo;

    @Autowired
    private CodeRepo codeRepository;

    private Role defaultRole;

    @Value("${auto.load.file.code:codes.csv}")
    private String codesFile;

    @Value("${auto.load.file.permission:permissions.csv}")
    private String permissionsFile;

    @Value("${auto.load.file.settings:settings.csv}")
    private String settingsFile;


    @Transactional
    @Override
    public void afterPropertiesSet() {

        loadCodes();
        loadPermissions();

        loadSettings();
        if (adminUserRepo.count() <= 0)
            createDefaultAdmin(createDefaultRole());

    }


    private Role createDefaultRole() {

        Role role = new Role();
        role.setDelFlag("N");
        role.setName("Default Admin Profile");
        role.setEmail("test@yahoo.com");
        List<Permission> permissions = permissionRepo.findByUserType(UserType.ADMIN.toString());
        role.setPermissions(permissions);
        role.setUserType(UserType.ADMIN);
        logger.info("Creating default Profile ....");
        defaultRole = rolerepository.save(role);
        return defaultRole;
    }

    private void loadCodes() {
        List<Code> codes = loadObjectList(Code.class, codesFile);
        codes.forEach(c -> {
            if (codeRepository.findByTypeAndCode(c.getType(), c.getCode()) == null) {
                try {
                    codeRepository.save(c);
                } catch (Exception e) {
                    logger.trace("not adding code {}", c);
                }
            }
        });
    }

    private void loadPermissions() {
        List<Permission> permissions = loadObjectList(Permission.class, permissionsFile);
        permissions.forEach(c -> {
            if (!permissionRepo.existsByNameAndUserType(c.getName(), c.getUserType())) {
                try {
                    permissionRepo.save(c);
                } catch (Exception e) {
                    logger.trace("not adding permission {}", c);
                }
            }
        });
    }

    private void loadSettings() {
        List<Setting> setting = loadObjectList(Setting.class, settingsFile);
        setting.forEach(c -> {
            if (!settingRepo.existsByName(c.getName())) {
                try {
                    settingRepo.save(c);
                } catch (Exception e) {
                    logger.trace("not adding setting {}", c);
                }
            }
        });
    }

    private void createDefaultAdmin(Role role) {


        AdminUser user = new AdminUser();
        user.setDelFlag("N");
        // user.set
        user.setUserName("su");
        user.setUserType(UserType.ADMIN);
        user.setStatus("A");
        user.setRole(role);
        user.setExpiryDate(new Date());
        user.setFirstName("Default");
        user.setLastName("Admin");
        user.setEmail("default@example.com");

        try {
            user.setPassword(encoder.encode("su"));

            adminUserRepo.save(user);
        } catch (Exception e) {
            logger.error("Can't create user", e);
            e.printStackTrace();
        }
    }

    public <T> List<T> loadObjectList(Class<T> type, String fileName) {
        try {
            CsvSchema bootstrapSchema = CsvSchema.emptySchema().withHeader();
            CsvMapper mapper = new CsvMapper().configure(CsvParser.Feature.ALLOW_TRAILING_COMMA, true)
                    .configure(CsvParser.Feature.FAIL_ON_MISSING_COLUMNS, false)
                    .configure(CsvParser.Feature.INSERT_NULLS_FOR_MISSING_COLUMNS, true)
                    .configure(CsvParser.Feature.SKIP_EMPTY_LINES, true)
                    .configure(CsvParser.Feature.TRIM_SPACES, true)
                    .configure(CsvParser.Feature.WRAP_AS_ARRAY, false)
                    .configure(CsvParser.Feature.IGNORE_TRAILING_UNMAPPABLE, true);
            File file = new ClassPathResource(fileName).getFile();
            MappingIterator<T> readValues =
                    mapper.readerWithTypedSchemaFor(type).with(bootstrapSchema).readValues(file);
            return readValues.readAll();
        } catch (Exception e) {
            logger.error("Error occurred while loading object list from file " + fileName, e);
            return Collections.emptyList();
        }
    }
}
