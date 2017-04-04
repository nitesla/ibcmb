package longbridge.models;


import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class CorpLimit extends Limit {

	@ManyToOne//TODO implement globallimit for corporate and for retail
	private Corporate corporate;
	
	
	public CorpLimit(){}
	
	


	public CorpLimit(Limit limit) {
		super(limit.getDescription(), limit.getType(), limit.getLowerLimit(), limit.getUpperLimit(),
				limit.getCurrency(), limit.getStatus(), limit.getEffectiveDate());
	}


	public Corporate getCorporate() {
		return corporate;
	}


	public void setCorporate(Corporate corporate) {
		this.corporate = corporate;
	}


	@Override //TODO fix toString()
	public String toString() {
		return "CorpLimit [corporate=" + corporate + ","+ super.toString() +"]";
	}
	
}
