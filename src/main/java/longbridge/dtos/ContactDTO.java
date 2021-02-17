package longbridge.dtos;

import java.io.Serializable;

public class ContactDTO implements Serializable {

    private long id;
    private String firstName;
    private String lastName;
    private String email;
    private boolean external;

    public ContactDTO() {
    }

    public ContactDTO(String firstName, String lastName, String email, boolean external) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.external = external;
    }

    public String getFirstName() {
        return firstName;
    }

    public ContactDTO setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public String getLastName() {
        return lastName;
    }

    public ContactDTO setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public ContactDTO setEmail(String email) {
        this.email = email;
        return this;
    }

    public boolean isExternal() {
        return external;
    }

    public ContactDTO setExternal(boolean external) {
        this.external = external;
        return this;
    }

    public long getId() {
        return id;
    }

    public ContactDTO setId(long id) {
        this.id = id;
        return this;
    }


}
