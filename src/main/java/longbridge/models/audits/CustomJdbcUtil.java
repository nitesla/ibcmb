package longbridge.models.audits;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;

/**
 * Created by ayoade_farooq@yahoo.com on 4/19/2017.
 */
public class CustomJdbcUtil {
Logger logger = LoggerFactory.getLogger(getClass());

    public static  boolean auditEntity(String s){
        System.out.println("E DEY ENTER HERE");
        boolean ok = false;
        String sql ="select * from audit_config where TABLE_NAME =?";
        try {
            PreparedStatement ps = connection().prepareStatement(sql);
            ps.setString(1,s);

            ResultSet rs  =  ps.executeQuery();

            if(rs!=null){

                ok = true;
            }




        } catch (Exception ex) {
            System.out.println(""+ex.getMessage());
        }
        return ok;
    }



    public static  Connection connection(){
        Connection c= null;
        try
        //()
        {
            CustomJdbcUtil util= new CustomJdbcUtil();

            final String URL =util.getPropertyFromFile("spring.datasource.url");
            final String USER= util.getPropertyFromFile("spring.datasource.username");
            final String  PWD = util.getPropertyFromFile("spring.datasource.password");
            Class.forName(util.getPropertyFromFile("spring.datasource.driver-class-name"));
            c = DriverManager.getConnection(URL, USER, PWD);
            util.logger.trace("Connection Successful");
            return c;
        }catch(Exception e)
        {
            System.out.println("  "+e.getMessage());
        }

        return c;
    }


    private   String getPropertyFromFile(String property) {
        Properties prop = new Properties();
        InputStream input = null;
        String value = "";

        try {
            input = this.getClass().getResourceAsStream("/application.properties");
            prop.load(input);
            value = prop.getProperty(property);
            this.logger.info("Property : {} \t Value : {}", property, value);
        } catch (IOException var14) {
            this.logger.error("Error in reading props file {}", var14.getLocalizedMessage());
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException var13) {
                    this.logger.error("error in closing input stream {}", var13.toString());
                }
            }

        }

        return value;
    }


}
