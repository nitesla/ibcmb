package longbridge.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.StringTokenizer;

public class MoneyConversion {
    private org.slf4j.Logger logger= org.slf4j.LoggerFactory.getLogger(this.getClass());

    public static final String[] units = {
            "", " one", "two", "three", "four", "five", "six", "seven",
            "eight", "nine", "ten", "eleven", "twelve", "thirteen", "fourteen",
            "fifteen", "sixteen", "seventeen", "eighteen", "nineteen"
    };

    public static final String[] tens = {
            "", // 0
            "", // 1
            "twenty", // 2
            "thirty", // 3
            "forty", // 4
            "fifty", // 5
            "sixty", // 6
            "seventy", // 7
            "eighty", // 8
            "ninety" // 9
    };

    public static String doubleConvert(final String n) {

        try {
            Double amt = Double.parseDouble(n);
            String pass = amt + "";
            StringTokenizer token = new StringTokenizer(pass, ".");
            String first = token.nextToken();
            String last = token.nextToken();

            pass = convert(Integer.parseInt(first))+" ";
//            pass= pass + code.getDescription();

            if (last.length()>0 && Integer.valueOf(last)>0) {
                Integer newLast = Integer.valueOf(last);
                if (last.length() == 1){
                    newLast *= 10;
                }
//                String desc = "";
//                if (null != code.getExtraInfo()){
//                    desc = code.getExtraInfo();
//                }
                pass += ", " + convert(newLast);
            }
            return StringUtils.upperCase(pass + "ONLY.");

        } catch (NumberFormatException nf) {
            return "";
        }
    }

    public static String convert(final Integer n) {
        if (n < 0) {
            return "minus " + convert(-n);
        }

        if (n < 20) {
            return units[n];
        }

        if (n < 100) {
            return tens[n / 10] + ((n % 10 != 0) ? " " : "") + units[n % 10];
        }

        if (n < 1000) {
            return units[n / 100] + " hundred " + ((n % 100 != 0) ? " " : "") + convert(n % 100);
        }

        if (n < 1000000) {
            return convert(n / 1000) + " thousand " + ((n % 1000 != 0) ? " " : "") + convert(n % 1000);
        }

        if (n < 1000000000) {
            return convert(n / 1000000) + " million " + ((n % 1000000 != 0) ? " " : "") + convert(n % 1000000);
        }

        return convert(n / 1000000000) + " billion " + ((n % 1000000000 != 0) ? " " : "") + convert(n % 1000000000);
    }



}
