package longbridge.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import longbridge.utils.PrettySerializer;
import org.hibernate.annotations.Where;
import org.hibernate.envers.Audited;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.io.IOException;
import java.util.List;

/**
 * @author Yemi Dalley
 * Created on 6/7/2018.
 *
 */

@Entity
@Audited(withModifiedFlag=true)
@Where(clause ="del_Flag='N'" )
public class Merchant extends AbstractEntity implements PrettySerializer{

	
	
	private String name;
    private String category;
    private boolean enabled;
    private String ownerReferenceName;

    @OneToMany( mappedBy = "merchant", cascade = CascadeType.ALL, orphanRemoval=true )
    private List<Product> products;
	

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String getOwnerReferenceName() {
		return ownerReferenceName;
	}

	public void setOwnerReferenceName(String ownerReferenceName) {
		this.ownerReferenceName = ownerReferenceName;
	}

	public List<Product> getProducts() {
		return products;
	}

	public void setProducts(List<Product> products) {
		this.products = products;
	}

	@Override
	public String toString() {
		return "Merchant [name=" + name + ", category=" + category + ", enabled=" + enabled
				+ ", ownerReferenceName=" + ownerReferenceName + "]";
	}

	@Override @JsonIgnore
	public JsonSerializer<Merchant> getSerializer() {
		return new JsonSerializer<>() {
			@Override
			public void serialize(Merchant value, JsonGenerator gen, SerializerProvider serializers)
					throws IOException {
				gen.writeStartObject();
				gen.writeStringField("Name", value.name);
				gen.writeStringField("Category", value.category);
				gen.writeStringField("Owner Reference Name", value.ownerReferenceName);
				gen.writeBooleanField("Enabled", value.enabled);
				gen.writeEndObject();
			}
		};
	}

	@Override @JsonIgnore
	public JsonSerializer<Merchant> getAuditSerializer() {
		return new JsonSerializer<>() {
			@Override
			public void serialize(Merchant value, JsonGenerator gen, SerializerProvider serializers)
					throws IOException {
				gen.writeStartObject();
				if (value.id != null) {
					gen.writeStringField("id", value.id.toString());
				} else {
					gen.writeStringField("id", "");
				}
				gen.writeStringField("name", value.name);
				gen.writeStringField("category", value.category);
				gen.writeStringField("ownerReferenceName", value.ownerReferenceName);
				gen.writeBooleanField("enabled", value.enabled);
				gen.writeEndObject();
			}
		};
	}

	}
