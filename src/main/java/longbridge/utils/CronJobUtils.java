package longbridge.utils;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.WebRequest;

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
            builderExprDesc.append(second +pluralizeWordChecker(" second",second));
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


    public static Map<String,String> getWeeklyExpression(String hour, String minute, String[] days){
        Map<String,String> expression = new HashMap<>();
        String exrInit = "0 ";
        StringBuilder builderExprVal = new StringBuilder(exrInit);
        StringBuilder builderExprDesc = new StringBuilder("");
            builderExprVal.append(minute);
            builderExprVal.append(" "+hour);
            builderExprVal.append(" ? * ");
            builderExprVal.append(days[0]);

        builderExprDesc.append("Every ");
        builderExprDesc.append(getFullMonthName(days[0]));
        if(days.length>1) {
            for (int i = 1;i<=days.length-1;i++ ) {
                builderExprVal.append(","+days[i]);
                    builderExprDesc.append("," + getFullMonthName(days[i]));
            }
        }
        builderExprDesc.append(" starting at ");
        builderExprDesc.append(normalizeTime(hour));
        builderExprDesc.append(":");
        if(minute.length() >1) {
            builderExprDesc.append(minute);
        }else{
            builderExprDesc.append("0"+minute);
        }
        builderExprDesc.append(getTimeSuffix(hour));
            builderExprVal.append(" *");
        expression.put("value",builderExprVal.toString());
        expression.put("desc",builderExprDesc.toString());
        logger.info("week expression {}",builderExprVal);
        /*
Sample Cron expression expected
0 0 12 ? * MON *
*/
        return expression;
    }


    public static Map<String,String> getMonthlyExpression(String monthChecker, String monthDay, String monthNum, String monthCategory, String monthWeek, String monthNumDesp, String monthHour, String monthMinute){
        Map<String,String> expression = new HashMap<>();
        String exrInit = "0 ";
        StringBuilder builderExprVal = new StringBuilder(exrInit);
        StringBuilder builderExprDesc = new StringBuilder("");
        builderExprVal.append(monthMinute);
        builderExprVal.append(" " + monthHour);
        if("eachMonthDay".equalsIgnoreCase(monthChecker)) {
            builderExprVal.append(" " + monthDay);
            builderExprVal.append(" 1/" + monthNum);
            builderExprVal.append(" * ?");
            builderExprDesc.append("day "+monthDay+" of every "+monthNum+pluralizeWordChecker(" month",monthNum));
        }else {
            builderExprVal.append(" ? ");
            builderExprVal.append(" 1/" + monthNum);
            builderExprVal.append(" "+monthWeek+"#"+monthCategory);
            builderExprVal.append(" "+monthNumDesp);
            builderExprVal.append(" *");
            builderExprDesc.append(numberToExpr(monthCategory)+" "+getFullMonthName(monthWeek)+" of every "+monthNum+pluralizeWordChecker(" month",monthNum));
        }
        logger.info("month expression {}",builderExprVal);
        expression.put("value",builderExprVal.toString());
        expression.put("desc",builderExprDesc.toString());
        /*
Sample Cron expression expected
0 0 12 ? * MON *
*/
        return expression;
    }

    public static Map<String,String> getYearlyExpression(String yearChecker, String yearMonth1, String yearMonthNum, String yearCategory, String yearMonthWeek, String yearMonth2, String yearHour, String yearMinute){
        Map<String,String> expression = new HashMap<>();
        String exrInit = "0 ";
        StringBuilder builderExprVal = new StringBuilder(exrInit);
        StringBuilder builderExprDesc = new StringBuilder("");
        builderExprVal.append(yearMinute);
        builderExprVal.append(" " + yearHour);
        if("perMonth".equalsIgnoreCase(yearChecker)) {
            builderExprVal.append(" " + yearMonthNum);
            builderExprVal.append(" " + yearMonth1);
            builderExprVal.append(" ? *");
            builderExprDesc.append("Every "+monthNumTOFullName(yearMonth1)+" "+yearMonthNum);
        }else {
            builderExprVal.append(" ? ");
            builderExprVal.append(" " + yearMonth2);
            builderExprVal.append(" "+yearMonthWeek+"#"+yearCategory);
            builderExprVal.append(" *");
            builderExprVal.append(" ? ");
            builderExprDesc.append(numberToExpr(yearCategory)+" "+getFullMonthName(yearMonthWeek)+" of "+monthNumTOFullName(yearMonth1));
        }
        builderExprDesc.append(" starting by ");
        builderExprDesc.append(normalizeTime(yearHour));
        builderExprDesc.append(":");
        if(yearMinute.length() >1) {
            builderExprDesc.append(yearMinute);
        }else{
            builderExprDesc.append("0"+yearMinute);
        }
        builderExprDesc.append(getTimeSuffix(yearHour));
        expression.put("value",builderExprVal.toString());
        expression.put("desc",builderExprDesc.toString());
        logger.info("year expression {}",builderExprVal);
        /*
Sample Cron expression expected
0 0 12 ? * MON *
*/
        return expression;
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
            case "weekDiv":
                return getWeeklyExpression(webRequest.getParameter("weekHour"), webRequest.getParameter("weekSecond"), webRequest.getParameterValues("weekDay"));
            case "monthDiv":
                return getMonthlyExpression(webRequest.getParameter("monthChecker"), webRequest.getParameter("monthDay"), webRequest.getParameter("monthNum"), webRequest.getParameter("monthCategory"), webRequest.getParameter("monthWeek"), webRequest.getParameter("monthNumDesp"), webRequest.getParameter("monthHour"), webRequest.getParameter("monthMinute"));
            case "yearDiv":
            return getYearlyExpression(webRequest.getParameter("yearChecker"), webRequest.getParameter("yearMonth1"), webRequest.getParameter("yearMonthNum"), webRequest.getParameter("yearCategory"), webRequest.getParameter("yearMonthWeek"), webRequest.getParameter("yearMonth2"), webRequest.getParameter("yearHour"), webRequest.getParameter("yearMinute"));
        }
        return null   ;
    }

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
    private static String numberToExpr(String num){
        switch (num){
            case "1":
                return "First";
            case "2":
                return "Second";
            case "3":
                return "Third";
            case "4":
                return "Fourth";
            default:
                return "";
        }
    }
    private static String monthNumTOFullName(String monthNum){
        switch (monthNum){
            case "1":
                return "January";
            case "2":
                return "February";
            case "3":
                return "March";
            case "4":
                return "April";
            case "5":
                return "May";
            case "6":
                return "June";
            case "7":
                return "July";
            case "8":
                return "August";
            case "9":
                return "September";
            case "10":
                return "October";
            case "11":
                return "November";
            case "12":
                return "December";
            default:
                return "";
        }
    }
    private static String getFullMonthName(String month){
        switch (month){
            case "MON":
                return "Monday";
            case "TUE":
                return "Tuesday";
            case "WED":
                return "Wednessday";
            case "THU":
                return "Thursday";
            case "FRI":
                return "Friday";
            case "SAT":
                return "Saturday";
            case "SUN":
                return "Sunday";
            default:
                return "";
        }
    }
}
