package longbridge.models;


import javax.persistence.Entity;

@Entity
public class CorpLimit extends Limit {

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


	@Override
	public String toString() {
		return "CorpLimit [corporate=" + corporate + ","+ super.toString() +"]";
	}
	
}
