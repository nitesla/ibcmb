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

    public static boolean validate(Date date1, Date date2){
        long difference = date2.getTime() - date1.getTime();
        long diffSeconds = difference / 1000 % 60;
        double secondToMinute = (((double) difference / (60 * 1000)));
//        long diffMinutes = difference / (60 * 1000) % 60;
        if(secondToMinute <= 5){
            return true;
        }

        return false;
    }

}




