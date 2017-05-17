package longbridge.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Fortune on 5/17/2017.
 */
public class DateFormatter {

    static SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy hh:mm a");

    public static String format(Date date){
        return dateFormatter.format(date);
    }

}


