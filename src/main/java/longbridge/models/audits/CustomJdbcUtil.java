package longbridge.models.audits;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Created by ayoade_farooq@yahoo.com on 4/19/2017.
 */
public class CustomJdbcUtil {


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


//            while(rs.next()){
//                return "";
//
//            }

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

            final String URL ="jdbc:mysql://localhost/ibanking";
            final String USER= "root";
            final String  PWD = "inheritance";
            Class.forName("com.mysql.jdbc.Driver");
            c = DriverManager.getConnection(URL, USER, PWD);
            System.out.println("sucess");
            return c;
        }catch(Exception e)
        {
            System.out.println("  "+e.getMessage());
        }

        return c;
    }



}
