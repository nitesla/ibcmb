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
    public static String compareAnswers(List<String>answers,List<String>entAnswers ){
        int noOfMismatch = 0;
        if((answers.size()>0)&&(entAnswers.size()>0)) {
            for(int i =0; i<answers.size();i++){
                if(!answers.get(i).equalsIgnoreCase(entAnswers.get(i))){
                    noOfMismatch++;
                }
            }
//            logger.info("no of mis match is {}",noOfMismatch);
            if(noOfMismatch==0){
                return "true";
            }
        }
        return "false";
    }
}
