package cn.mucang.simple.nativecache.nginx;

import nginx.clojure.java.NginxJavaHeaderFilter;
import nginx.clojure.java.NginxJavaRequest;
import org.apache.commons.io.FileUtils;

import javax.servlet.ServletException;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static nginx.clojure.MiniConstants.HEADERS;

/**
 * Created by mc-050 on 2016/4/8.
 */
public class ResponseHeaderHandler implements NginxJavaHeaderFilter {
    @Override
    public Object[] doFilter(int status, Map<String, Object> request, Map<String, Object> responseHeaders)
            throws IOException {
        FileUtils.writeLines(new File("d:/key.txt"), request.keySet(), true);
        FileUtils.writeLines(new File("d:/value.txt"),request.values(),true);
        FileUtils.writeLines(new File("d:/cookiekey.txt"),((Map)request.get(HEADERS)).keySet(),true);
        FileUtils.writeLines(new File("d:/cookievalue.txt"),((Map)request.get(HEADERS)).values(),true);
        NginxJavaRequest ngRequest = (NginxJavaRequest) request;
        //SSO权限认证
        UrlFilter urlFilter = new UrlFilter();
        try {
            urlFilter.doFilter(request,responseHeaders);
        } catch (ServletException e) {
            e.printStackTrace();
        }
        //
        ngRequest.setVariable("host",RequestUtils.getParameter(request, "Host"));
        ngRequest.setVariable("","");
        ngRequest.put("cookie",RequestUtils.getCookie(request));
        return  new Object[] { 200, request, responseHeaders};
    }
}
