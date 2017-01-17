package cn.mucang.simple.nativecache.nginx;

import cn.mucang.simple.sso.client.SSOClientFilter;
import cn.mucang.simple.sso.client.SSOWebUtils;
import cn.mucang.simple.sso.client.SimpleCache;
import cn.mucang.simple.sso.model.SSOUser;
import cn.mucang.simple.utils.*;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

/**
 * Created by mc-050 on 2016/4/11.
 */
public class SSOUtils {

    private static final Logger LOG = LoggerFactory.getLogger(SSOUtils.class);

    private static final SimpleCache cache = new SimpleCache();
    public static final RiddleWrapper INSTANCE = RiddleWrapper.INSTANCE;

    static final String PATH_FETCH_BY_WEIXIN = "/client/service/fetch-by-weixin.htm";
    static final String PATH_FETCH_BY_APP = "/client/service/fetch-by-app.htm";
    private static final String TOKEN_PARAM = "token";
    private static final String SSO_USER_ATTRIBUTE = "__SSO_USER__";

    public static String serverUrl = "https://sso.kakamobi.com";//静态变量用于存放服务器地址和应用名称
    public static String appName;

    public static SSOUser getCurrentUser(Map request) {

        SSOUser user = null;
        boolean isHandle = false;
        String url = RequestUtils.getUri(request);
        {// try ssoToken
            String appToken = RequestUtils.getParameter(request, "ssoToken");
            if (StringUtils.isNotBlank(appToken) && !PATH_FETCH_BY_APP.equals(url)) {
                isHandle = true;
                user = (SSOUser) cache.get("appToken:" + appToken);
                if (user == null) {
                    appName = RequestUtils.getParameter(request,"appName");
                    if (StringUtils.isBlank(appName)) {
                        throw new IllegalArgumentException("未设置appName，请使用sso过滤器");
                    }
                    user = fetchWithApp(appToken);
                    if (user != null) {
                        cache.put("appToken:" + appToken, user);
                    }
                }
            }
        }
        if (!isHandle) {// try weixinToken
            String weixinToken = RequestUtils.getParameter(request,"weixinToken");
            if (StringUtils.isNotBlank(weixinToken) && !PATH_FETCH_BY_WEIXIN.equals(url)) {
                isHandle = true;
                user = (SSOUser) cache.get("weixinToken:" + weixinToken);
                if (user == null) {
                    appName = RequestUtils.getParameter(request,"appName");
                    if (StringUtils.isBlank(appName)) {
                        throw new IllegalArgumentException("未设置appName，请使用sso过滤器");
                    }
                    user = fetchWithWeixin(weixinToken);
                    if (user != null) {
                        cache.put("weixinToken:" + weixinToken, user);
                    }
                }
            }
        }
        if (!isHandle) {// try session
            String token = (String) RequestUtils.getCookie(request).get(SSO_USER_ATTRIBUTE);

            if (StringUtils.isEmpty(token)) {
                token = RequestUtils.getHeader(request,"X-Simple-Token");
            }
            if (StringUtils.isEmpty(token)) {
                token = RequestUtils.getParameter(request,"accessToken");
            }
            if (StringUtils.isNotBlank(token)) {
                user = (SSOUser) cache.get("token:" + token);
            }
        }
        return user;
    }

    private static SSOUser fetchWithWeixin(String token) {
        if (StringUtils.isNotBlank(token)) {
            String url = SignUtils.signUrl(serverUrl + PATH_FETCH_BY_WEIXIN + "?"
                            + "weixinToken" + "=" + token
                            + "&" + SSOClientFilter.APP_NAME + "=" + Encodes.urlDecode(appName),
                    SSOClientFilter.SIGN_KEY);
            return fetchFromServer(url);
        }
        return null;
    }

    private static SSOUser fetchWithApp(String token) {

        if (StringUtils.isNotBlank(token)) {
            String url = SignUtils.signUrl(serverUrl + PATH_FETCH_BY_APP + "?"
                            + "appToken" + "=" + token
                            + "&" + SSOClientFilter.APP_NAME + "=" + Encodes.urlDecode(appName),
                    SSOClientFilter.SIGN_KEY);
            return fetchFromServer(url);
        }
        return null;
    }

    /**
     * 根据登录服务器返回的token，去服务器取当前的用户信息
     *
     * @param url
     * @return
     */
    public static SSOUser fetchFromServer(String url) {
        JSONObject json = readJson(url);
        if (json == null) {
            return null;
        }
        try {
            if (json.getBooleanValue("success")) {
                SSOUser user = json.getObject("data", SSOUser.class);
                return user;
            }
        } catch (Exception ex) {
            LOG.error(null, ex);
        }

        return null;
    }

    public static JSONObject readJson(String url) {
        try {
            String content = openUrl(url);
            if (StringUtils.isNotBlank(content)) {
                return JSON.parseObject(content);
            }
        } catch (Exception ex) {
            LOG.error(null, ex);
        }
        return null;
    }

    private static String openUrl(String url) {
        try {
            URL u = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) u.openConnection();
            conn.setRequestProperty("X-Data-From", "SSOClientFilter");
            try (InputStream is = conn.getInputStream()) {
                String back = IOUtils.toString(is, "UTF-8");
                return back;
            }
        } catch (Exception ex) {
            LOG.warn(url, ex);
        }
        return null;
    }

    /**
     * 请求sso服务器修改密码，如果修改成功，则返回null，否则返回错误信息
     *
     * @param host
     * @param userId
     * @param oldPassword
     * @param newPassword
     * @return
     */
    public static String changePassword(String host, long userId, String oldPassword, String newPassword) {
        String u = host
                + "?userId=" + userId
                + "&oldPassword=" + Encodes.urlEncode(oldPassword) + "&newPassword=" + Encodes.urlEncode(newPassword);
        String url = SignUtils.signUrl(u, SSOClientFilter.SIGN_KEY);
        JSONObject json = readJson(url);
        if (json == null) {
            return "请求失败";
        } else {
            boolean succ = json.getBooleanValue("success");
            if (succ) {
                return null;
            } else {
                String message = json.getString("message");
                if (StringUtils.isBlank(message)) {
                    message = "访问失败";
                }
                return message;
            }
        }

    }

    public static boolean isValidSign(Map request, Map response, String[] signKey, String resignValue) {
        String sign = RequestUtils.getParameter(request,"sign");
        String resign = RequestUtils.getParameter(request,"resign");
        String fullUrl = RequestUtils.getHeader(request,"X-Original-Uri");
        if(StringUtils.isBlank(fullUrl)) {
            fullUrl = getFullUrl(request);
        }

        return StringUtils.isNotBlank(sign) && INSTANCE.isValidSign(fullUrl, signKey)?true:StringUtils.isBlank(sign) && StringUtils.equals(resign, resignValue);
    }

    public static String getFullUrl(Map request) {
        String query = RequestUtils.getParameter(request, "query-string");
        return StringUtils.isBlank(query)?RequestUtils.getUri(request):RequestUtils.getUri(request) + "?" + query;
    }

    /**
     * 有两种情况会使用这个接口，1：刚登录成功, 2：浏览器修改用户信息
     * 假设都没有token说明该用户没有登录成功，或者过期了
     *
     * @param request
     * @param user
     */
    public static void setCurrentUser(Map request, SSOUser user) {
        String token = RequestUtils.getParameter(request,TOKEN_PARAM);
        if (StringUtils.isEmpty(token)) {
            token = (String) RequestUtils.getCookie(request).get(SSO_USER_ATTRIBUTE);
        }
        setCurrentUser(request, token, user);

    }

    public static void setCurrentUser(Map request, String token, SSOUser user) {
        if (user == null) {
            RequestUtils.getCookie(request).remove(SSO_USER_ATTRIBUTE);
            if (StringUtils.isNotBlank(token)) {
                cache.evict("token:" + token);
            }
        } else {
            RequestUtils.getCookie(request).put(SSO_USER_ATTRIBUTE, token);
            if (StringUtils.isNotBlank(token)) {
                cache.put("token:" + token, user);
            }
        }
    }

    public static String getFullUrlWithHost(Map request) {
        StringBuilder sb = new StringBuilder();
        sb.append(RequestUtils.getScheme(request));
        sb.append("://");
        sb.append(RequestUtils.getHeader(request,"Host"));
        sb.append(getFullUrl(request));
        return sb.toString();
    }

    public static String getContextWithHost(Map request) {
        StringBuilder sb = new StringBuilder();
        sb.append(RequestUtils.getScheme(request));
        sb.append("://");
        sb.append(RequestUtils.getHeader(request,"Host"));
        String context = RequestUtils.getParameter(request,"context-path");
        if (StringUtils.isNotBlank(context)) {
            sb.append(context);
        }
        return sb.toString();
    }

    public static SSOWebUtils.RequestType parseRequestType(Map request) {
        String xmlhttp = RequestUtils.getHeader(request,"X-Requested-With");
        //如果为空，则表示是一个标准的请求，否则就是一个ajax的请求了
        if (!StringUtils.equalsIgnoreCase("XMLHttpRequest", xmlhttp)) {
            return SSOWebUtils.RequestType.Normal;
        } else {
            String dataType = RequestUtils.getHeader(request,"X-Data-Type");
            if (StringUtils.endsWithIgnoreCase("html", dataType)) {
                return SSOWebUtils.RequestType.AjaxHtml;
            } else {
                return SSOWebUtils.RequestType.AjaxJson;
            }
        }
    }

    public static SSOUser getProfile(String host, long userId, String appName) {
        host = host + "?userId=" + userId + "&appName=" + Encodes.urlEncode(appName);
        String url = SignUtils.signUrl(host, SSOClientFilter.SIGN_KEY);
        JSONObject json = readJson(url);
        if (json == null) {
            return null;
        }
        try {
            if (json.getBooleanValue("success")) {
                SSOUser user = json.getObject("data", SSOUser.class);
                return user;
            }
        } catch (Exception ex) {
            LOG.error(null, ex);
        }
        return null;
    }

}
