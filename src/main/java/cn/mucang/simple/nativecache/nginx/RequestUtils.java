package cn.mucang.simple.nativecache.nginx;

import java.util.Map;

/**
 * Created by mc-050 on 2016/4/8.
 */
public class RequestUtils {

    private static final String URI = "uri";
    private static final String METHOD = "request-method";
    private static final String ORIGIN = "Origin";
    private static final String HEADER = "headers";
    private static final String COOKIE = "cookie";
    private static final String SCHEME = "scheme";

    public static String getScheme(Map request){
        return String.valueOf(request.get(SCHEME));
    }

    public static String getUri(Map request){
        return String.valueOf(request.get(URI));
    }

    public static String getMethod(Map request){
        return String.valueOf(request.get(METHOD));
    }

    public static String getOrigin(Map request){
        Map header = (Map) request.get(HEADER);
        return String.valueOf(header.get(ORIGIN));
    }

    public static String getHeader(Map request,String name){
        Map header = (Map) request.get(HEADER);
        return String.valueOf(header.get(name));
    }

    public static String getParameter(Map request,String name){
        return String.valueOf(request.get(name));
    }

    public static Map getCookie(Map request){
        return (Map) request.get(COOKIE);
    }
}
