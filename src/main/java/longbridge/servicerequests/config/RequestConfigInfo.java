package longbridge.servicerequests.config;

public class RequestConfigInfo {
    Long id;
    String name;
    String description;
    boolean system;

    public RequestConfigInfo(Long id, String name, String description, boolean system) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.system = system;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isSystem() {
        return system;
    }
}
