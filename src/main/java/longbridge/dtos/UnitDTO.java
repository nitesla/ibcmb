package longbridge.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Fortune on 4/26/2017.
 */
public class UnitDTO {

    @JsonProperty("DT_RowId")
    private Long id;
    private int version;
    private String name;
    private String code;
    private ArrayList<UnitPersonnelDTO> personnel;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public ArrayList<UnitPersonnelDTO> getPersonnel() {
        return personnel;
    }

    public void setPersonnel(ArrayList<UnitPersonnelDTO> personnel) {
        this.personnel = personnel;
    }

    @Override
    public String toString() {
        return "UnitDTO{" +
                "id=" + id +
                ", version=" + version +
                ", name='" + name + '\'' +
                ", personnel=" + personnel +
                '}';
    }
}
