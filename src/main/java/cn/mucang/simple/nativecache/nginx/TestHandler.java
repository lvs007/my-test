package cn.mucang.simple.nativecache.nginx;

import cn.mucang.simple.sso.client.ConfigObject;
import cn.mucang.simple.sso.client.SSOClientFilter;
import cn.mucang.simple.sso.client.SSOWebUtils;
import cn.mucang.simple.sso.model.SSOUser;
import nginx.clojure.java.NginxJavaRequest;
import nginx.clojure.java.NginxJavaRingHandler;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.security.Principal;
import java.util.*;

import static nginx.clojure.java.Constants.*;

/**
 * Created by mc-050 on 2016/4/7.
 */
public class TestHandler implements NginxJavaRingHandler {

    private Logger logger = LoggerFactory.getLogger(TestHandler.class);

    private final long updateInterval = 60000;//重新读取配置文件的间隔,默认一分钟读取一次
    private static List<ConfigObject> configList = new ArrayList<>();
    private long lastUpdateTime;

    @Override
    public Object[] invoke(Map<String, Object> map) throws IOException {
        System.out.println(map);
        logger.error("測試數據：" + map);
        FileUtils.writeLines(new File("d:/key.txt"),map.keySet(),true);
        FileUtils.writeLines(new File("d:/value.txt"),map.values(),true);
        FileUtils.writeLines(new File("d:/cookiekey.txt"),((Map)map.get(HEADERS)).keySet(),true);
        FileUtils.writeLines(new File("d:/cookievalue.txt"),((Map)map.get(HEADERS)).values(),true);
        Map<String,String> cookie = RequestUtils.getCookie(map);
        NginxJavaRequest ngRequest = (NginxJavaRequest) map;
        //SSO权限认证
        Map response = new HashMap();
        UrlFilter urlFilter = new UrlFilter();
        try {
            urlFilter.doFilter(map,response);
        } catch (ServletException e) {
            e.printStackTrace();
        }
        //
        ngRequest.setVariable("host",RequestUtils.getParameter(map, "Host"));
        ngRequest.setVariable("","");
        ngRequest.put("cookie",cookie);
        if ("302".equals(response.get("status"))){
            return new Object[]{302,"redirect="+response.get("redirect"),""};
        }
        return  PHASE_DONE;
    }

}

