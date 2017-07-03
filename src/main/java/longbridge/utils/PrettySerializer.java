package longbridge.utils;

import com.fasterxml.jackson.databind.JsonSerializer;

public interface PrettySerializer {
	public  <T> JsonSerializer<T> getSerializer();
}
