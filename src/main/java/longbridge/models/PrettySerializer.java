package longbridge.models;

import com.fasterxml.jackson.databind.JsonSerializer;

/**
 * Created by chiomarose on 29/06/2017.
 */

public interface PrettySerializer
{
    public  <T> JsonSerializer<T> getSerializer();
}
