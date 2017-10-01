package longbridge.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by ayoade_farooq@yahoo.com on 4/20/2017.
 */
@Service
public class IpAddressUtils
{
        @Autowired
    public IpAddressUtils(HttpServletRequest request) {
        this.request = request;
    }

    private HttpServletRequest request;


    public   final String getClientIP() {
        final String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
        	String remoteHost = request.getRemoteHost();
            getIpsource( request.getRemoteAddr());
            return request.getRemoteAddr();

        }
        getIpsource(  xfHeader.split(",")[0]);

        return xfHeader.split(",")[0];
    }



    public void getIpsource(String s){

        try {

            InetAddress address = InetAddress.getByName(s);

            if (address instanceof Inet4Address) {
                // your IP is IPv4
                System.out.println("Your IP is >>>> "+s);
                System.out.println("Your IP is ipv4 ");
            } else if (address instanceof Inet6Address) {
                // your IP is IPv6
                System.out.println("Your IP is >>>> "+s);
                System.out.println("Your IP is ipv6 ");
            }

        } catch(UnknownHostException e) {

            //  your address was a machine name like a DNS name, and couldn't be found
            e.printStackTrace();

        }
    }
}
