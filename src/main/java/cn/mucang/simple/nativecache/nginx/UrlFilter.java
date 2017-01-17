package cn.mucang.simple.nativecache.nginx;

import cn.mucang.simple.health.service.HealthManager;
import cn.mucang.simple.sso.client.AntPathMatcher;
import cn.mucang.simple.sso.client.ConfigObject;
import cn.mucang.simple.sso.client.SSOWebUtils;
import cn.mucang.simple.sso.model.SSOUser;
import cn.mucang.simple.utils.Encodes;
import cn.mucang.simple.utils.SignUtils;
import cn.mucang.simple.utils.UrlBuilder;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import java.io.*;
import java.util.*;

/**
 * Created by mc-050 on 2016/4/8.
 */
public class UrlFilter {

    private static final String DEFAULT_CHARSET = "UTF8";
    public static final String TOKEN_PARAM = "token";
    public static final String APP_NAME = "appName";
    public static final String SIGN_PARAM = "sign";
    public static final String PATH_MANAGE_ROLE_USERS = "/client/manage/role-users.htm";
    public static final String SIGN_KEY = "!@#$%^&*()";
    public static final String RESIGN_VALUE = "fuckshit";
    private static final Logger LOG = LoggerFactory.getLogger(UrlFilter.class);
    private static final String PATH_LOGIN = "/client/service/login.htm";
    private static final String PATH_FETCH = "/client/service/fetch.htm";
    static final String PATH_FETCH_BY_WEIXIN = "/client/service/fetch-by-weixin.htm";
    static final String PATH_FETCH_BY_APP = "/client/service/fetch-by-app.htm";
    private static final String PATH_CHANGE_PWD = "/client/service/change-password.htm";
    private static final String PATH_UPDATE = "/client/service/update-profile.htm";
    private static final String PATH_LOGOUT = "/client/service/logout.htm";
    private static final String PATH_PROFILE = "/client/service/profile.htm";
    //以下是项目管理员访问的地址
    private static final String PATH_MANAGE_ROLES = "/client/manage/roles.htm";
    private static final String PATH_MANAGE_USERS = "/client/manage/users.htm";
    private static final String PATH_MANAGE_USERS_BY_PROJECT = "/client/manage/users-by-project.htm";
    private static final String PATH_MANAGE_USER_ROLES = "/client/manage/user-roles.htm";
    private static final String PATH_MANAGE_ADD_ROLE = "/client/manage/add-role.htm";
    private static final String PATH_MANAGE_REMOVE_ROLE = "/client/manage/remove-role.htm";
    private static final String PATH_MANAGE_ADD_USER = "/client/manage/add-user.htm";
    private static final String PATH_MANAGE_REMOVE_USER = "/client/manage/remove-user.htm";
    //以下地址为本filter直接拦截的地址
    private static final String URL_PROFILE = "/_/profile";//只提供JSON接口
    private static final String URL_CHANGE_PWD = "/_/change-pwd";//只提供JSON接口
    private static final String URL_UPDATE = "/_/update";//只提供JSON接口
    private static final String URL_LOGOUT = "/_/logout";//只提供网页接口
    private static final String URL_LOGIN = "/_/login";//只提供网页接口
    //直接拦截项目管理员的请求，首先检查是不是管理员权限
    private static final String URL_MANAGE_ROLES = "/_/manage/roles";//只提供JSON接口
    private static final String URL_MANAGE_USERS = "/_/manage/users";//只提供JSON接口
    private static final String URL_MANAGE_USERS_BY_PROJECT = "/_/manage/users-by-project";//只提供JSON接口
    private static final String URL_MANAGE_USER_ROLES = "/_/manage/user-roles";//只提供JSON接口
    private static final String URL_MANAGE_ROLE_USERS = "/_/manage/role-users";//只提供JSON接口
    private static final String URL_MANAGE_ADD_ROLE = "/_/manage/add-role";//只提供JSON接口
    private static final String URL_MANAGE_REMOVE_ROLE = "/_/manage/remove-role";//只提供JSON接口
    private static final String URL_MANAGE_ADD_USER = "/_/manage/add-user";//只提供JSON接口
    private static final String URL_MANAGE_REMOVE_USER = "/_/manage/remove-user";//只提供JSON接口
    private final long updateInterval = 60000;//重新读取配置文件的间隔,默认一分钟读取一次
    private AntPathMatcher pathMatcher = new AntPathMatcher();
    private List<ConfigObject> configList = new ArrayList<>();
    private long lastUpdateTime;
    //以下信息必须要配置，不能有默认值
    private String serverUrl;//服务器的URL，这个URL不需要带上任何路径，路径是固定的，只需要写比如http://login.kakamobi.com
    private String appName;//应用名称
    private String configFilePath;//配置文件的路径

//    @PostConstruct
//    public void init(FilterConfig filterConfig) throws ServletException {
//        this.serverUrl = filterConfig.getInitParameter("serverUrl");
//        this.appName = filterConfig.getInitParameter("appName");
//        this.configFilePath = filterConfig.getInitParameter("configFilePath");
//        filterConfig.getServletContext().setAttribute(SSOWebUtils.SSO_SERVER_URL, serverUrl);
//        filterConfig.getServletContext().setAttribute(SSOWebUtils.SSO_APP_NAME, appName);
//        SSOWebUtils.serverUrl = serverUrl;
//        SSOWebUtils.appName = appName;
//        reloadConfigListIfNeed();
//    }

    private List<ConfigObject> readFromStream(InputStream is) throws IOException {
        List<ConfigObject> list = new ArrayList<>();
        List<String> lines = IOUtils.readLines(is, "utf8");
        for (String line : lines) {
            if (StringUtils.isBlank(line) || line.startsWith("#")) {
                continue;
            }
            String[] ss = line.split("=", 2);
            if (ss.length != 2) {
                continue;
            }
            ConfigObject config = new ConfigObject();
            String pattern = ss[0].trim();
            config.setPathPattern(pattern);
            String right = ss[1].trim();
            if (StringUtils.equalsIgnoreCase(right, "*")) {
                config.setPass(true);
            } else if (right.contains("&") && right.contains("|")) {
                throw new RuntimeException("非法的配置，不能同时含有&和|");
            } else if (right.contains("&")) {
                config.setRolesOp(ConfigObject.RolesOp.All);
                config.setRoles(right.split("\\&"));
            } else if (right.contains("|")) {
                config.setRolesOp(ConfigObject.RolesOp.Any);
                config.setRoles(right.split("\\|"));
            } else {
                config.setRoles(new String[]{right});
            }
            list.add(config);
        }
        return list;
    }

    private void reloadConfigListIfNeed() {
        long now = System.currentTimeMillis();
        if (now - lastUpdateTime > updateInterval) {
            lastUpdateTime = now;
            //如果没有配置文件，则取默认的，只拦截/admin，其他的都不拦截
            //优先读取本地文件，如果没有，则读取classpath目录下的文件
            InputStream is = null;
            try {
                if (StringUtils.isNotBlank(configFilePath) && new File(configFilePath).exists()) {
                    is = new FileInputStream(configFilePath);
                } else {
                    is = Thread.currentThread().getContextClassLoader().getResourceAsStream("/auth.txt");
                }
                List<ConfigObject> configList = new ArrayList<>();
                configList = readFromStream(is);
                //添加默认的到第一链
                {
                    ConfigObject config = new ConfigObject();
                    config.setPass(true);
                    config.setPathPattern("/_/**");
                    configList.add(0, config);
                }
                //访问图标的也给放过
                {
                    ConfigObject config = new ConfigObject();
                    config.setPass(true);
                    config.setPathPattern("/favicon.ico");
                    configList.add(0, config);
                }
                this.configList = configList;
            } catch (IOException | RuntimeException ex) {
                LOG.warn(null, ex);
            } finally {
                IOUtils.closeQuietly(is);
            }
        }
    }

    public boolean doFilter(Map request, Map response) throws IOException, ServletException {

        long start = System.currentTimeMillis();
        boolean tmp = doFilterInternal(request, response);
        String uri = RequestUtils.getUri(request);
        String method = RequestUtils.getMethod(request);
        long end = System.currentTimeMillis();
        HealthManager.getUrlStat().increment(uri, method, end - start);
        return tmp;
    }


    /**
     * 此方法的调用可能性有如下几种 ：
     * 1. 当请求是AJAX的时候，服务器登录完以后，一定会把回调地址改为此。
     * 2. 当AJAX收到需要登录的时候，会自动用iframe打开此地址，并且可以选择传一个callback参数，可能可以用于关闭iframe窗口。
     *
     * @param request
     * @param response
     * @throws IOException
     * @throws ServletException
     */
    private void doLogin(Map request, Map response) throws IOException, ServletException {
        String callback = RequestUtils.getParameter(request,"callback");
        String location = RequestUtils.getParameter(request,"location");
        String relogin = RequestUtils.getParameter(request,"relogin");
        SSOUser user = SSOUtils.getCurrentUser(request);
        if (user != null) {
            String backUrl = RequestUtils.getParameter(request,"backUrl");
            if (StringUtils.isNotBlank(backUrl)) {
                response.put("redirect",backUrl);
                response.put("status",302);
                return;
            }
            //如果此时已经登录了，有两种情况
            //1，是relogin为空，则显示登录成功，否则提示没有权限，需要重新登录
            if (StringUtils.isBlank(relogin)) {
                String script = "";
                Map<String, String> map = new HashMap<>();
                if (StringUtils.isNotBlank(location)) {
                    int index = location.indexOf("#");
                    if (index != -1) {
                        //如果在最后面，则直接加上，否则加上&再加上
                        if (index == location.length() - 1) {
                            location += "login=success";
                        } else {
                            location += "&login=success";
                        }
                    } else {
                        location += "#login=success";
                    }
                    script = "try{"
                            + "parent.location='" + location + "';"
                            + "}catch(e){}";
                } else if (StringUtils.isNotBlank(callback)) {
                    script = "try{parent."
                            + callback + "();"
                            + "}catch(e){}";
                }
                map.put("__do_script__", script);
                response.put("status",200);
            } else {
                String fullUrl = SSOUtils.getFullUrlWithHost(request);
                UrlBuilder ub = new UrlBuilder(fullUrl);
                ub.removeParam("relogin");
                StringBuilder sb = new StringBuilder();
                sb.append(serverUrl).append(PATH_LOGIN).append("?");
                sb.append("appName=").append(Encodes.urlDecode(appName));
                sb.append("&relogin=true");
                sb.append("&redirectUrl=").append(Encodes.urlEncode(ub.getUri()));
                String url = SignUtils.signUrl(sb.toString(), SIGN_KEY);
                Map<String, String> map = new HashMap<>();
                map.put("${reloginUrl}", url);
//                response.putAll(map);
                response.put("status",302);
                response.put("redirect",url);
            }
        } else {//如果为空，则表示要跳转到登录服务器去了，由这里先签一下名再跳过去
            //如果为空，则有两种情况，一种是服务器重定向回来，还有一种是将要去验证
            if (isFromLoginServer(request)) {
                boolean success = handleFromLoginServer(request, response, false);
                if (success) {
                    String backUrl = RequestUtils.getParameter(request,"backUrl");
                    String nodeUrl = RequestUtils.getParameter(request,"nodeUrl");
                    if (StringUtils.isNotBlank(nodeUrl)) {// 需要跳转到node，传递token参数
                        String token = RequestUtils.getParameter(request,TOKEN_PARAM);//token不可为空
                        StringBuilder sb = new StringBuilder(1024);
                        if (nodeUrl.contains("?")) {
                            if (!nodeUrl.endsWith("&")) {
                                sb.append("&");
                            }
                        } else {
                            sb.append("?");
                        }
                        sb.append("accessToken=").append(token).append("&backUrl=");
                        if (StringUtils.isNotBlank(backUrl)) {
                            sb.append(Encodes.urlEncode(backUrl));
                        }
                        response.put("redirect",sb.toString());
                        response.put("status",302);

                    } else if (StringUtils.isNotBlank(backUrl)) {
                        response.put("redirect", backUrl);
                        response.put("status",302);
                    }
                }
            } else {
                issueRedirectToLoginServer(request, response);
            }
        }
    }

    private void renderJsonSuccess(Object data, Map request, Map response) throws IOException {
        Map<String, Object> result = new HashMap<>();
        //如果为null,表示当前用户还没有登录
        result.put("success", true);
        result.put("errorCode", 0);
        result.put("data", data);
        renderJson(result, request, response);
    }

    private void renderJsonError(String errorMessage, int errorCode, Map request, Map response) throws IOException {
        Map<String, Object> result = new HashMap<>();
        //如果为null,表示当前用户还没有登录
        result.put("success", false);
        result.put("errorCode", errorCode);
        result.put("message", errorMessage);
        renderJson(result, request, response);
    }

    private void renderJson(Map<String, Object> map, Map request, Map response) throws IOException {
        addCrossDomainHeaders(request, response);
        response.putAll(map);
    }

    private void doLogout(Map request, Map response) throws IOException, ServletException {
        SSOUser user = SSOUtils.getCurrentUser(request);
        if (user != null) {
            SSOUtils.setCurrentUser(request, null);
        }
        //不管如何，都通知sso的中心服务器，需要退出登录，重定向就可以了
        StringBuilder sb = new StringBuilder();
        sb.append(serverUrl).append(PATH_LOGOUT).append("?");
        sb.append("appName=").append(Encodes.urlDecode(appName));
        String url = SignUtils.signUrl(sb.toString(), SIGN_KEY);
        response.put("status",302);
        response.put("redirect",url);
    }

    private void doProfile(Map request, Map response) throws IOException, ServletException {
        SSOUser user = SSOUtils.getCurrentUser(request);
        Map<String, Object> result = new HashMap<>();
        //如果为null,表示当前用户还没有登录
        if (user != null) {
            user = SSOUtils.getProfile(serverUrl + PATH_PROFILE, user.getUserId(), appName);
            if (user != null) {
                SSOUtils.setCurrentUser(request, user);
            }
        }
        if (user == null) {
            result.put("success", true);
            result.put("message", "当前用户未登录");
            result.put("errorCode", 1);
        } else {
            result.put("success", true);
            result.put("data", user);
        }
        renderJson(result, request, response);
    }

    protected boolean doFilterInternal(Map request, Map response) throws IOException, ServletException {
        String path = RequestUtils.getUri(request);
        String method = RequestUtils.getMethod(request);
        //放过OPTIONS方法的请求。
        if (StringUtils.equalsIgnoreCase("OPTIONS", method)) {
            //如果是四个URL，则自己处理跨域的问题，否则，让下一级处理
            switch (path) {
                case URL_CHANGE_PWD:
                case URL_PROFILE:
                case URL_UPDATE:
                case URL_MANAGE_ADD_ROLE:
                case URL_MANAGE_REMOVE_ROLE:
                case URL_MANAGE_ADD_USER:
                case URL_MANAGE_REMOVE_USER:
                case URL_MANAGE_ROLES:
                case URL_MANAGE_USERS:
                case URL_MANAGE_USERS_BY_PROJECT:
                case URL_MANAGE_USER_ROLES:
                case URL_MANAGE_ROLE_USERS: {
                    addCrossDomainHeaders(request, response);
                    return true;
                }
            }
            return true;
        }
        //以下地址是直接处理，不经过权限校验，如果没有权限，则返回false，
        switch (path) {

            case URL_LOGOUT: {
                doLogout(request, response);
                return true;
            }
            case URL_PROFILE: {
                doProfile(request, response);
                return true;
            }

        }
        List<ConfigObject> list = getConfigList();
        for (ConfigObject config : list) {
            if (pathMatcher.match(config.getPathPattern(), path)) {
                //如果当前用户为null，则看是否是服务器返回的登录以后的请求
                if (isFromLoginServer(request)) {
                    handleFromLoginServer(request, response, true);
                } else {
                    if (config.isPass()) { // 如果是** 就直接跳过吧，不用创建session
                        break;
                    }
                    SSOUser user = SSOUtils.getCurrentUser(request);
                    if (user == null) {
                        user = tryFetchFromServer(request);
                    }
                    CheckPermissionResult result = checkPermission(user, config);
                    if (result == CheckPermissionResult.Ok) {
                        break;
                    } else if (result == CheckPermissionResult.NoUser) {
                        issueRedirectToLoginServer(request, response);
                    } else if (result == CheckPermissionResult.NoPermission) {
                        showNoPermission(request, response);
                    }
                }
                return true;
            }
        }
        //如果是自置的PATH，则拦截并处理之
        switch (path) {
            case URL_LOGIN:
                doLogin(request, response);
                return true;
            case URL_LOGOUT:
                doLogout(request, response);
                return true;
            case URL_PROFILE:
                doProfile(request, response);
                return true;
        }
        return true;
    }

    private SSOUser tryFetchFromServer(Map req) {
        String token = RequestUtils.getParameter(req,"accessToken");
        if (StringUtils.isNotBlank(token)) {
            String url = SignUtils.signUrl(serverUrl + PATH_FETCH + "?"
                            + TOKEN_PARAM + "=" + token
                            + "&" + APP_NAME + "=" + Encodes.urlDecode(appName),
                    SIGN_KEY);
            SSOUser user = SSOUtils.fetchFromServer(url);
            //如果不为空，则存到当前的会话里面去
            if (user != null) {
                SSOUtils.setCurrentUser(req, user);
                return user;
            }
        }
        return null;
    }

    private boolean handleFromLoginServer(Map request, Map response, boolean shouldRedirect) throws IOException {
        boolean success = false;
        String token = RequestUtils.getParameter(request,TOKEN_PARAM);
        if (SSOUtils.isValidSign(request, response, new String[]{SIGN_KEY}, RESIGN_VALUE)) {
            String url = SignUtils.signUrl(serverUrl + PATH_FETCH + "?"
                            + TOKEN_PARAM + "=" + token
                            + "&" + APP_NAME + "=" + Encodes.urlDecode(appName),
                    SIGN_KEY);
            SSOUser user = SSOUtils.fetchFromServer(url);
            //如果不为空，则存到当前的会话里面去
            if (user != null) {
                SSOUtils.setCurrentUser(request, user);
                success = true;
            }
        } else {
            LOG.warn("有人模拟服务器返回登录信息,url:{}", SSOUtils.getFullUrl(request));
        }
        //到这一步了，无论登录是否成功，都要重定向到普通页面
        if (shouldRedirect) {
            UrlBuilder ub = new UrlBuilder(getRequestFullUrl(request));
            ub.removeParam(TOKEN_PARAM);
            ub.removeParam("sign");
            response.put("status",302);
            response.put("redirect", ub.getUri());
        }
        return success;
    }

    private String getRequestFullUrl(Map request) {
        //因为可能从nginx反向代理并且urlrewrite，所以需要读取原始的URL，如果可以的话
        String originalUrl = RequestUtils.getHeader(request,"X-Original-Uri");
        if (StringUtils.isBlank(originalUrl)) {
            originalUrl = SSOUtils.getFullUrlWithHost(request);
        } else {
            originalUrl = RequestUtils.getScheme(request) + "://" + RequestUtils.getHeader(request,"Host") + originalUrl;
        }
        return originalUrl;
    }

    private void showNoPermission(Map request, Map response) throws IOException {
        SSOWebUtils.RequestType requestType = SSOUtils.parseRequestType(request);
        if (requestType == SSOWebUtils.RequestType.Normal) {
            String fullUrl = SSOUtils.getFullUrlWithHost(request);
            StringBuilder sb = new StringBuilder();
            sb.append(serverUrl).append(PATH_LOGIN).append("?");
            sb.append("appName=").append(Encodes.urlDecode(appName));
            sb.append("&relogin=true");
            sb.append("&redirectUrl=").append(Encodes.urlEncode(fullUrl));
            String url = SignUtils.signUrl(sb.toString(), SIGN_KEY);
            Map<String, String> map = new HashMap<>();
            map.put("${reloginUrl}", url);
//            response.putAll(map);
            response.put("status",302);
            response.put("redirect",url);
        } else {
            addCrossDomainHeaders(request, response);

            Map<String, Object> map = new LinkedHashMap<>();
            map.put("success", false);
            map.put("errorCode", -2);//这里-2表示已登录，但是权限不足
            String urlPrefix = SSOUtils.getContextWithHost(request);
            map.put("data", urlPrefix + URL_LOGIN);
            map.put("message", "当前操作权限不足");
            response.putAll(map);
        }
    }

    protected boolean isFromLoginServer(Map request) throws IOException {
        String path = RequestUtils.getUri(request);
        if (StringUtils.equals(PATH_FETCH, path)) {
            return false;
        }
        return RequestUtils.getParameter(request,TOKEN_PARAM) != null
                && RequestUtils.getParameter(request,SIGN_PARAM) != null;
    }

    protected void addCrossDomainHeaders(Map request, Map response) {
        String origin = RequestUtils.getOrigin(request);
        if (StringUtils.isNotBlank(origin)) {
            String accessControlRequestHeaders = RequestUtils.getHeader(request, "access-control-request-headers");
            String accessControlRequestMethod = RequestUtils.getHeader(request,"access-control-request-method");

            response.put("Access-Control-Allow-Origin", origin);
            response.put("Access-Control-Allow-Headers", accessControlRequestHeaders);
            response.put("Access-Control-Allow-Methods", accessControlRequestMethod);
            response.put("Access-Control-Allow-Credentials", "true");
            response.put("Access-Control-Max-Age", "600");
        }
    }

    protected void issueRedirectToLoginServer(Map request, Map response) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append(serverUrl).append(PATH_LOGIN).append("?");
        sb.append("appName=").append(Encodes.urlDecode(appName));
        String originalUrl = getRequestFullUrl(request);
        sb.append("&redirectUrl=").append(Encodes.urlEncode(originalUrl));
        String url = SignUtils.signUrl(sb.toString(), SIGN_KEY);
        //如果是普通请求，则采用重定向，如果是AJAX请求，则采取JSON的方式
        SSOWebUtils.RequestType requestType = SSOUtils.parseRequestType(request);
        if (requestType == SSOWebUtils.RequestType.Normal) {
            response.put("status",302);
            response.put("redirect",url);
        } else {
            addCrossDomainHeaders(request, response);
//            response.setCharacterEncoding("UTF-8");
//            response.setContentType("application/json");

            Map<String, Object> map = new LinkedHashMap<>();
            map.put("success", false);
            map.put("errorCode", -1);//这里-1表示需要重新登录
            map.put("message", "当前操作需要登录");
            Map<String, Object> dataMap = new HashMap<>();
            dataMap.put("redirectUrl", url);
            String urlPrefix = SSOUtils.getContextWithHost(request);
            dataMap.put("loginUrl", urlPrefix + URL_LOGIN);
            map.put("data", dataMap);
//            response.putAll(map);
            response.put("status",302);
            response.put("redirect",url);
        }

    }

    private CheckPermissionResult checkPermission(SSOUser user, ConfigObject config) {
        if (config.isPass()) {
            return CheckPermissionResult.Ok;
        }
        if (user == null) {
            return CheckPermissionResult.NoUser;
        }
        String[] configRoles = config.getRoles();
        BitSet bs = new BitSet(configRoles.length);
        for (int i = 0; i < configRoles.length; i++) {
            String configRole = configRoles[i];
            if (ArrayUtils.contains(user.getRoles(), configRole)) {
                bs.set(i);
            }
        }
        boolean succ = false;
        if (config.getRolesOp() == ConfigObject.RolesOp.All) {
            succ = bs.cardinality() == configRoles.length;
        } else {
            succ = bs.cardinality() > 0;
        }
        return succ ? CheckPermissionResult.Ok : CheckPermissionResult.NoPermission;
    }

    protected List<ConfigObject> getConfigList() {
        reloadConfigListIfNeed();
        return configList;
    }

    public void setPathMatcher(AntPathMatcher pathMatcher) {
        this.pathMatcher = pathMatcher;
    }

    private enum CheckPermissionResult {

        /**
         * 表示检查通过
         */
        Ok,
        /**
         * 表示当前没有用户，也就是需要登录，这个时候
         * 一般是重定向到登录服务器进行登录
         */
        NoUser,
        /**
         * 表示当前有用户，但是用户没有权限，这个时候就需要提示一下并支持重新登录了
         */
        NoPermission;
    }
}
