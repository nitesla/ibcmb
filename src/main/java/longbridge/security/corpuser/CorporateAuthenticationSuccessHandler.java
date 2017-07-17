package longbridge.security.corpuser;

import longbridge.dtos.SettingDTO;
import longbridge.models.CorporateUser;
import longbridge.models.UserType;
import longbridge.repositories.CorporateUserRepo;
import longbridge.security.FailedLoginService;
import longbridge.security.SessionUtils;
import longbridge.security.userdetails.CustomUserPrincipal;
import longbridge.services.ConfigurationService;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Date;

@Component("corporateAuthenticationSuccessHandler")
public class CorporateAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	SessionUtils sessionUtils;
	@Autowired
	FailedLoginService failedLoginService;
	private LocalDate today = LocalDate.now();
	private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
	@Autowired
	private CorporateUserRepo corporateUserRepo;
	@Autowired
	private MessageSource messageSource;
	@Autowired
	private ConfigurationService configService;

	@Override
	public void onAuthenticationSuccess(final HttpServletRequest request, final HttpServletResponse response,
			final Authentication authentication) throws IOException {
		handle(request, response, authentication);
		final HttpSession session = request.getSession(false);
		if (session != null) {
			sessionUtils.setTimeout(session);
			String s = authentication.getName();

			String userName = "";
			String corpId = "";
			if (s != null) {
				try {
					userName = s;

				} catch (Exception e) {
					e.printStackTrace();
					userName = authentication.getName();

				}
			}
			// CorporateUser user =
			// corporateUserRepo.findFirstByUserNameIgnoreCaseAndCorporate_CustomerIdIgnoreCase(userName,
			// corpId);
			CorporateUser user = corporateUserRepo.findFirstByUserNameIgnoreCase(userName);
			if (user != null) {
				sessionUtils.validateExpiredPassword(user, session);
				user.setLastLoginDate(new Date());
				failedLoginService.loginSucceeded(user);
			}

		}
		clearAuthenticationAttributes(request);
	}

	protected void handle(final HttpServletRequest request, final HttpServletResponse response,
			final Authentication authentication) throws IOException {
		final String targetUrl = determineTargetUrl(authentication);

		if (response.isCommitted()) {
			logger.debug("Response has already been committed. Unable to redirect to " + targetUrl);
			return;
		}

		redirectStrategy.sendRedirect(request, response, targetUrl);
	}

	protected String determineTargetUrl(final Authentication authentication) {
		CustomUserPrincipal userDetails = (CustomUserPrincipal) authentication.getPrincipal();
		// CorporateUser corporateUser =
		// corporateUserRepo.findFirstByUserName(userDetails.getUsername());

		CorporateUser corporateUser = corporateUserRepo
				.findFirstByUserNameIgnoreCaseAndCorporate_Id(userDetails.getUsername(), userDetails.getCorpId());

		boolean isUser = corporateUser.getUserType().equals(UserType.CORPORATE);

		String isFirstLogon = corporateUser.getIsFirstTimeLogon();

		if ("Y".equalsIgnoreCase(isFirstLogon)) {
			return "/corporate/setup";
		}

		SettingDTO setting = configService.getSettingByName("ENABLE_CORPORATE_2FA");
		boolean tokenAuth = false;
		if (setting != null && setting.isEnabled()) {
			tokenAuth = (setting.getValue().equalsIgnoreCase("YES") ? true : false);
		}

		if (tokenAuth) {
			return "/corporate/token";
		}
		if (isUser) {
			return "/corporate/dashboard";
		} else {
			throw new IllegalStateException();
		}
	}

	protected void clearAuthenticationAttributes(final HttpServletRequest request) {
		final HttpSession session = request.getSession(false);
		if (session == null) {
			return;
		}
		session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
	}

	protected RedirectStrategy getRedirectStrategy() {
		return redirectStrategy;
	}

	public void setRedirectStrategy(final RedirectStrategy redirectStrategy) {
		this.redirectStrategy = redirectStrategy;
	}
}