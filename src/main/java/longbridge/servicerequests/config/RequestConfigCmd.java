package longbridge.servicerequests.config;

import java.util.List;

public interface RequestConfigCmd  {
    List<SRFieldDTO> getFields();

    String getName();

    String getDescription();

    String getType();

    boolean isAuthRequired();

    Long getGroupId();
}
