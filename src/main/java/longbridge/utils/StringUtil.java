package longbridge.utils;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Longbridge on 7/19/2017.
 */
public class StringUtil {
    public static List<String> splitByComma(String word){
       return Arrays.asList(word.split(","));
    }
}
