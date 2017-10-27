package longbridge.utils;

import longbridge.config.audits.RevisedEntitiesUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Longbridge on 10/26/2017.
 */
public class DateUtil {
    static Logger logger = LoggerFactory.getLogger(DateUtil.class);
    public static long convertDateToLong(String date){
        long milliseconds = 0;
        SimpleDateFormat f = new SimpleDateFormat("dd-MM-yyyy");
        try {
            if(!StringUtils.isEmpty(date)) {
                Date d = f.parse(date);
                milliseconds = d.getTime();
            }
            logger.info("the date in long is {}",milliseconds);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return milliseconds;
    }
}
