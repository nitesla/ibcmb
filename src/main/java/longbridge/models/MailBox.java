package longbridge.models;

import org.hibernate.annotations.Where;
import org.hibernate.envers.Audited;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import javax.persistence.*;

import java.io.IOException;
import java.util.Collection;

/**
 * Created by Wunmi on 27/03/2017.
 */
@Entity
@Audited
@Where(clause ="del_Flag='N'" )
public class MailBox extends AbstractEntity{
    private Long userId;
    private UserType userType;

    @OneToMany(mappedBy = "mailBox")
    private Collection<Message> messages;

	@Override
	public String serialize() throws JsonProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	
	public static OperationCode getAddCode() {
		// TODO Auto-generated method stub
		return null;
	}

	public static OperationCode getModifyCode() {
		// TODO Auto-generated method stub
		return null;
	}
}
