package longbridge.security.userdetails;

import longbridge.models.CorporateUser;
import longbridge.models.Role;
import longbridge.models.User;
import org.joda.time.LocalDate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Transactional
public class CustomUserPrincipal implements CustomeUserDetails {

	private static final long serialVersionUID = 1L;

	private final User user;
	private String ipAddress;
	private LocalDate today = LocalDate.now();
	private Long corpId;

	public CustomUserPrincipal(User user) {
		this.user = user;
	}
	public CustomUserPrincipal(CorporateUser user) {
		this.user = user;
		this.corpId=user.getCorporate().getId();
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	@Override
	public String getUsername() {
		return user.getUserName();
	}

	@Override
	@Transactional
	public Collection<? extends GrantedAuthority> getAuthorities() {
		final List<GrantedAuthority> authorities = new ArrayList<>();

		getPrivileges(user.getRole()).forEach(i -> authorities.add(new SimpleGrantedAuthority(i)));

		return authorities;
	}

	@Override
	public String getPassword() {
		return user.getPassword();
	}

	@Override
	public boolean isAccountNonExpired() {
//		boolean result = false;
//		LocalDate date = new LocalDate(user.getExpiryDate());
//		if (date == null ||today.isBefore(date)) {
//			result = true;
//		}
//		return result;
		return isAccountNonLocked();
	}

	@Override
	public boolean isAccountNonLocked() {
		boolean result = false;
		LocalDate date = new LocalDate(user.getLockedUntilDate());
		if (date == null || today.isAfter(date) || today.isEqual(date)) {
			result = true;
		}
		return result;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		//return isEnabled();
		return true;
	}

	@Override
	public boolean isEnabled() {
		try {
			return user.getStatus().equalsIgnoreCase("A");
		} catch (Exception e) {
			return false;
		}
	}

	public User getUser() {
		return user;
	}

	private List<String> getPrivileges(Role roles) {

		List<String> privileges = new ArrayList<>();
		roles.getPermissions().forEach(i ->{


					if(user.getUserType()!=null  && i.getUserType()!=null &&   (i.getUserType().equals(user.getUserType().toString()) ))
						privileges.add(i.getCode());


		}


		);

		privileges.add(roles.getUserType().toString());

		return privileges;
	}

	public Long getCorpId() {
		return corpId;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		CustomUserPrincipal that = (CustomUserPrincipal) o;

		return user.equals(that.user);
	}

	@Override
	public int hashCode() {
		return user.hashCode();
	}


}
