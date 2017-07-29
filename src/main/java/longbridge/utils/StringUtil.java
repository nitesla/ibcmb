package longbridge.utils;

import org.springframework.web.context.request.WebRequest;

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
                    System.out.println("my answer "+answers.get(i)+" entrust "+entAnswers.get(i));
                }
            }
            System.out.println("no of mis match is "+noOfMismatch);
            if(noOfMismatch==0){
                return "true";
            }
        }
        return "false";
    }
    public static String compareAnswers(WebRequest webRequest, List<String>entAnswers ){
        int noOfMismatch = 0;
        List<String> providedAnswers = Arrays.asList(webRequest.getParameter("secAnswer").split(","));
        int noOfSecQn = Integer.parseInt(webRequest.getParameter("noOfSecQn"));
        for (int i=0;i<noOfSecQn;i++){
            if(!entAnswers.get(i).equalsIgnoreCase(providedAnswers.get(i))){
                noOfMismatch++;
            }
        }
            if(noOfMismatch==0){
                return "true";
            }
        return "false";
    }
}
