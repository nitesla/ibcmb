package longbridge.utils;


import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * Created by Longbridge on 1/11/2018.
 */
public final class CookieUtil {

        private CookieUtil() {}

        public static Cookie getCookie(HttpServletRequest request) {
            if (request.getCookies() != null) {
                for (Cookie cookie : request.getCookies()) {
                    if (cookie.getName().equalsIgnoreCase("time_out_time")) {
                       // System.out.println("cookie match");
                        return cookie;
                    }
                }
            }
            Cookie cookie = new Cookie("time_out_time","");
            cookie.setPath("/");
            return cookie;
        }

}
