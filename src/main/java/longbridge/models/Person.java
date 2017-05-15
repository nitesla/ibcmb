package longbridge.models;

public interface Person {
	
	boolean isExternal();

	String getFirstName();

	void setFirstName(String firstName);

	String getLastName();

	void setLastName(String lastName);

	String getEmail();

	void setEmail(String email);

}