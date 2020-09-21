package longbridge.utils;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ReflectionUtils {

public static void nullifyStrings( Object o, int depth ) {

	Class klass = o.getClass();
	Set<Field> all = new HashSet<>();
	do{
		List<Field> fieldsInClass = getFieldsInClass(klass);
		all.addAll(fieldsInClass);
		klass = klass.getSuperclass();
		depth--;
	}while(depth >= 0);
	
    for ( Field f : all) {
        f.setAccessible(true);
        try {
            if ( f.getType().equals( String.class ) ) {
                String value = (String) f.get( o );
                if ( value != null && value.trim().isEmpty() ) {
                    f.set( o , null);
                }
            }
        } catch ( Exception e ) { 
            throw new RuntimeException(e);
        }
    }
    
  

}

private static List<Field> getFieldsInClass(Class k){
    return Arrays.asList(k.getDeclaredFields());
}

}