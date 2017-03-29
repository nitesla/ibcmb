package longbridge.models;

import javax.persistence.*;
import java.util.Collection;

/**
 * Created by Wunmi on 27/03/2017.
 */
@Entity
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long Id;
    private String userName;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private boolean enabled;
    private Long customerType_Id;
    private Long corporateCustomer_Id;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "customer_role", joinColumns =
    @JoinColumn(name = "customer_id", referencedColumnName = "Id"), inverseJoinColumns =
    @JoinColumn(name = "role_id", referencedColumnName = "Id"))
    private Collection<Role> roles;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "customer_profile", joinColumns =
    @JoinColumn(name = "customer_id", referencedColumnName = "Id"), inverseJoinColumns =
    @JoinColumn(name = "profile_id", referencedColumnName = "Id"))
    private Collection<Profile> profiles;

    public Long getId() {
        return Id;
    }

    public void setId(Long id) {
        Id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Long getCustomerType_Id() {
        return customerType_Id;
    }

    public void setCustomerType_Id(Long customerType_Id) {
        this.customerType_Id = customerType_Id;
    }

    public Long getCorporateCustomer_Id() {
        return corporateCustomer_Id;
    }

    public void setCorporateCustomer_Id(Long corporateCustomer_Id) {
        this.corporateCustomer_Id = corporateCustomer_Id;
    }

    public Collection<Role> getRoles() {
        return roles;
    }

    public void setRoles(Collection<Role> roles) {
        this.roles = roles;
    }

    public Collection<Profile> getProfiles() {
        return profiles;
    }

    public void setProfiles(Collection<Profile> profiles) {
        this.profiles = profiles;
    }
}
