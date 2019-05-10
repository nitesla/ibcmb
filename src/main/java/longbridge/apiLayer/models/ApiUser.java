package longbridge.apiLayer.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;


public class ApiUser {

    @ApiModelProperty(notes = "User's name. If is a Corporate User, add Corporate ID to field.",  example = "Chrissy:C00854", required = true, position = 0)
    @NotNull(message ="userName")
    private String userName;
    @NotNull(message = "password")
    private String passWord;
//    @NotNull(message="cifid")
//    private  String cifid;


    @JsonProperty(required = true)
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

//    public String getCifid() {
//        return cifid;
//    }
//
//    public void setCifid(String cifid) {
//        this.cifid = cifid;
//    }
}
