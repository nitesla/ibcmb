package longbridge.dtos;

import java.util.List;

/**
 * Created by mac on 31/01/2018.
 */
public class ReportParameterDTO {
    private String parameterName;
    private String parameterDesc;
    private String datatype;
    private String code;
    private List<CodeDTO> codeValues;


    public String getParameterName() {
        return parameterName;
    }

    public void setParameterName(String parameterName) {
        this.parameterName = parameterName;
    }

    public String getParameterDesc() {
        return parameterDesc;
    }

    public void setParameterDesc(String parameterDesc) {
        this.parameterDesc = parameterDesc;
    }

    public String getDatatype() {
        return datatype;
    }

    public void setDatatype(String datatype) {
        this.datatype = datatype;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<CodeDTO> getCodeValues() {
        return codeValues;
    }

    public void setCodeValues(List<CodeDTO> codeValues) {
        this.codeValues = codeValues;
    }

    @Override
    public String toString() {
        return "ReportParameterDTO{" +
                "parameterName='" + parameterName + '\'' +
                ", parameterDesc='" + parameterDesc + '\'' +
                ", datatype='" + datatype + '\'' +
                ", code='" + code + '\'' +
                ", codeValues=" + codeValues +
                '}';
    }
}
