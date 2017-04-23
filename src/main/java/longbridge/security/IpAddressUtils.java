package longbridge.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

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
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }
}
