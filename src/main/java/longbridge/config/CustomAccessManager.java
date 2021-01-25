package longbridge.config;

import longbridge.PermissionNotGranted;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CustomAccessManager implements AccessDecisionManager {

    private static final Logger logger = LoggerFactory.getLogger(CustomAccessManager.class);
    private AccessDecisionManager delegate;

    public CustomAccessManager(AccessDecisionManager decisionManager) {
        logger.debug("Creating Custom and delegate");
        delegate = decisionManager;
    }

    @Override
    public void decide(Authentication authentication, Object o, Collection<ConfigAttribute> collection) throws AccessDeniedException, InsufficientAuthenticationException {
        logger.debug("Deciding on {}", collection);
        try {
            delegate.decide(authentication, o, collection);
        } catch (AccessDeniedException e) {
            String permissions = getPermissions(collection.toString());
            throw new PermissionNotGranted(e.getMessage() + " Missing " + permissions, permissions, e);
        }
    }

    @Override
    public boolean supports(ConfigAttribute configAttribute) {
        return delegate.supports(configAttribute);
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return delegate.supports(aClass);
    }

    private String getPermissions(String input) {
        Matcher m = Pattern.compile("\\(([^\\)]+)\\)").matcher(input);
        StringBuilder sb = new StringBuilder();
        while (m.find()) {
            sb.append(StringUtils.strip(m.group(1), "'") + ",");
        }
        return StringUtils.strip(sb.toString(), ",");
    }

}
