package longbridge.models;

import javax.persistence.Entity;

/**
 * Created by Wunmi on 3/28/2017.
 *  OperationsUser is a Staff of the bank tasked with managing customers. Tellers and Customer relationship
 *  manager fall into this group.
 */

@Entity

public class OperationsUser extends User {
	
	@Override
	public String toString() {
		return "OperationsUser ["+super.toString()+"]";
	}


}
