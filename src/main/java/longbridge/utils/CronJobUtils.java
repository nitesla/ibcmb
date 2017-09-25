package longbridge.utils;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.WebRequest;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Longbridge on 7/1/2017.
 */
public class CronJobUtils {
    private static Logger logger = LoggerFactory.getLogger(new CronJobUtils().getClass());

    public static Map<String,String> getSecondExpression(String second){
        Map<String,String> expression = new HashMap<>();
        String desc = "Every ";
        if(second!=null && !second.equalsIgnoreCase("")) {
            String exrInit = "*/";
            StringBuilder builderExprVal = new StringBuilder(exrInit);
            StringBuilder builderExprDesc = new StringBuilder(desc);
            builderExprVal.append(second);
            builderExprVal.append(" * * * * *");
            builderExprDesc.append(second +" second(s)");
            expression.put("desc",builderExprDesc.toString());
            expression.put("value",builderExprVal.toString());
            logger.info("seconds expression {} and desc {}", builderExprVal,desc);

            return expression;
        }
        return null;
    }
    public static Map<String,String> getMinuteExpression(String minute){
        Map<String,String> expression = new HashMap<>();
        String desc = "Every ";
        if(minute!=null && !minute.equalsIgnoreCase("")) {
            String exrInit = "0 0/";
            StringBuilder builderExprVal = new StringBuilder(exrInit);
            StringBuilder builderExprDesc = new StringBuilder(desc);
            builderExprVal.append(minute);
            builderExprVal.append(" * 1/1 * ? *");
            builderExprDesc.append(minute +pluralizeWordChecker(" minute",minute));
            expression.put("desc",builderExprDesc.toString());
            expression.put("value",builderExprVal.toString());
            logger.info("minutes expression {} and desc {}", builderExprVal,desc);
            return expression;
        }

        return null;
    }
    public static Map<String,String> getHourExpression(String hourChecker, String exactHour, String hour, String minute){
        Map<String,String> expression = new HashMap<>();
        String exrInit = "0 ";
        StringBuilder builderExprVal = new StringBuilder(exrInit);
        StringBuilder builderExprDesc = new StringBuilder("");
        if(hourChecker.equalsIgnoreCase("everyHour")){
            builderExprVal.append("0 0/"+exactHour);
            builderExprDesc.append("Every ");
            builderExprDesc.append(exactHour);
            builderExprDesc.append(pluralizeWordChecker(" hour",exactHour));
        }else {
            builderExprDesc.append(normalizeTime(hour));
            builderExprDesc.append(":");
            if(minute.length() >1) {
                builderExprDesc.append(minute);
            }else{
                builderExprDesc.append("0"+minute);
            }
            builderExprDesc.append(getTimeSuffix(hour));
            builderExprDesc.append(" every day");
            builderExprVal.append(minute);
            builderExprVal.append(" "+hour);
        }
        builderExprVal.append(" 1/1 * ? *");
        expression.put("desc",builderExprDesc.toString());
        expression.put("value",builderExprVal.toString());
//        logger.info("hour expression {} and desc {}",builderExprVal,builderExprDesc);
//        0 0 0/20 1/1 * ? *
//        0 12 6 1/1 * ? *
        return expression;
    }
    public static Map<String,String> getDailyExpression(String dailyChecker,String dayInterval, String dailyHour,String dailyMin){
        logger.info("dailyChecker {} dayInterval {} and dailyHour {} dailyMin {}",dailyChecker,dayInterval,dailyHour,dailyMin);
        Map<String,String> expression = new HashMap<>();
        String exrInit = "0 ";
        StringBuilder builderExprVal = new StringBuilder(exrInit);
        StringBuilder builderExprDesc = new StringBuilder("");
        if(dailyChecker.equalsIgnoreCase("everyDay")){
            builderExprVal.append(dailyMin);
            builderExprVal.append(" "+dailyHour);
            builderExprVal.append(" 1/"+dayInterval);
            builderExprVal.append(" * ? *");
            builderExprDesc.append("Every "+dayInterval+" "+pluralizeWordChecker("day",dayInterval)+" starting at ");
            builderExprDesc.append(normalizeTime(dailyHour));
            builderExprDesc.append(":");
            if(dailyMin.length() >1) {
                builderExprDesc.append(dailyMin);
            }else{
                builderExprDesc.append("0"+dailyMin);
            }
            builderExprDesc.append(getTimeSuffix(dailyHour));
        }else {
            builderExprDesc.append("Monday to Friday every week starting at ");
            builderExprDesc.append(normalizeTime(dailyHour));
            builderExprDesc.append(":");
            if(dailyMin.length() >1) {
                builderExprDesc.append(dailyMin + " ");
            }else{
                builderExprDesc.append("0"+dailyMin);
            }
            builderExprDesc.append(getTimeSuffix(dailyHour));
            builderExprVal.append(dailyMin);
            builderExprVal.append(" "+dailyHour);
            builderExprVal.append(" ? * MON-FRI *");
        }
        expression.put("desc",builderExprDesc.toString());
        expression.put("value",builderExprVal.toString());
//        logger.info("Daily expression {} and description is {}",builderExprVal,builderExprDesc);
        //Sample Cron expression expected
//        	0 9 12 1/4 * ? *
//        	0 9 12 ? * MON-FRI *
        return expression;
    }

    @NotNull
    public static String getWeeklyExpression(String hour, String minute, String[] days){

        String exrInit = "0 ";
        StringBuilder stringBuilder = new StringBuilder(exrInit);
            stringBuilder.append(minute);
            stringBuilder.append(" "+hour);
            stringBuilder.append(" ? * ");
            stringBuilder.append(days[0]);
        if(days.length>1) {
            for (int i = 1;i<=days.length-1;i++ ) {
                stringBuilder.append(","+days[i]);
            }
        }
            stringBuilder.append(" *");

        logger.info("week expression {}",stringBuilder);
        /*
Sample Cron expression expected
0 0 12 ? * MON *
*/
        return stringBuilder.toString();
    }
    @NotNull
    public static String getMonthlyExpression(String monthChecker, String monthDay, String monthNum, String monthCategory, String monthWeek, String monthNumDesp, String monthHour, String monthMinute){

        String exrInit = "0 ";
        StringBuilder stringBuilder = new StringBuilder(exrInit);
        stringBuilder.append(monthMinute);
        stringBuilder.append(" " + monthHour);
        if("eachMonthDay".equalsIgnoreCase(monthChecker)) {
            stringBuilder.append(" " + monthDay);
            stringBuilder.append(" 1/" + monthNum);
            stringBuilder.append(" * ?");
        }else {
            stringBuilder.append(" ? ");
            stringBuilder.append(" 1/" + monthNum);
            stringBuilder.append(" "+monthWeek+"#"+monthCategory);
            stringBuilder.append(" "+monthNumDesp);
            stringBuilder.append(" *");

        }
        logger.info("month expression {}",stringBuilder);
        /*
Sample Cron expression expected
0 0 12 ? * MON *
*/
        return stringBuilder.toString();
    }

    @NotNull
    public static String getYearlyExpression(String yearChecker, String yearMonth1, String yearMonthNum, String yearCategory, String yearMonthWeek, String yearMonth2, String yearHour, String yearMinute){

        String exrInit = "0 ";
        StringBuilder stringBuilder = new StringBuilder(exrInit);
        stringBuilder.append(yearMinute);
        stringBuilder.append(" " + yearHour);
        if("perMonth".equalsIgnoreCase(yearChecker)) {
            stringBuilder.append(" " + yearMonthNum);
            stringBuilder.append(" " + yearMonth1);
            stringBuilder.append(" ? *");
        }else {
            stringBuilder.append(" ? ");
            stringBuilder.append(" " + yearMonth2);
            stringBuilder.append(" "+yearMonthWeek+"#"+yearCategory);
            stringBuilder.append(" *");

        }
        logger.info("year expression {}",stringBuilder);
        /*
Sample Cron expression expected
0 0 12 ? * MON *
*/
        return stringBuilder.toString();
    }

    public static Map<String,String> getCronExpression(String schedule,WebRequest webRequest){
        String expr = "";
        switch (schedule) {
            case "secondsDiv":
                return getSecondExpression(webRequest.getParameter("second"));
            case "minuteDiv":
                return getMinuteExpression(webRequest.getParameter("minute"));
            case "hourDiv":
                return getHourExpression(webRequest.getParameter("hourChecker"), webRequest.getParameter("exactHour"), webRequest.getParameter("hour"), webRequest.getParameter("hourMin"));
            case "dailyDiv":
                return getDailyExpression(webRequest.getParameter("dailyChecker"), webRequest.getParameter("dayInterval"), webRequest.getParameter("dailyHour"), webRequest.getParameter("dailyMin"));
//            case "weekDiv":
//                return getWeeklyExpression(webRequest.getParameter("weekHour"), webRequest.getParameter("weekSecond"), webRequest.getParameterValues("weekDay"));
//            case "monthDiv":
//                return getMonthlyExpression(webRequest.getParameter("monthChecker"), webRequest.getParameter("monthDay"), webRequest.getParameter("monthNum"), webRequest.getParameter("monthCategory"), webRequest.getParameter("monthWeek"), webRequest.getParameter("monthNumDesp"), webRequest.getParameter("monthHour"), webRequest.getParameter("monthMinute"));
//            case "yearDiv":
//            return getYearlyExpression(webRequest.getParameter("yearChecker"), webRequest.getParameter("yearMonth1"), webRequest.getParameter("yearMonthNum"), webRequest.getParameter("yearCategory"), webRequest.getParameter("yearMonthWeek"), webRequest.getParameter("yearMonth2"), webRequest.getParameter("yearHour"), webRequest.getParameter("yearMinute"));
        }
        return null   ;
    }
@org.jetbrains.annotations.NotNull
private static String getTimeSuffix(String hour){
    if(Integer.parseInt(hour)>11){
        return "PM";
    }else{
        return "AM";
    }
}
    private static String pluralizeWordChecker(String word, String num){
        if(Integer.parseInt(num) <=1){
            return word;
        }else {
            StringBuilder stringBuilder =  new StringBuilder(word);
            stringBuilder.append("s");
            return stringBuilder.toString();
        }
    }
    private static String normalizeTime(String hour){
        if(Integer.parseInt(hour) >12){
            String regHour = String.valueOf(Integer.parseInt(hour) -12);
            return regHour;
        }else{
            return hour;
        }
    }
}
