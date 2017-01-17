package cn.mucang.simple.nativecache.test;

import cn.mucang.neptune.sdk.NeptuneHelper;
import cn.mucang.simple.mvc.exception.ClientException;
import cn.mucang.simple.utils.SignUtils;
import cn.mucang.simple.utils.api.exception.ApiException;
import cn.mucang.simple.utils.api.exception.HttpException;
import cn.mucang.simple.utils.api.exception.InternalException;
import cn.mucang.simple.utils.http.MucangHttpClient;
import cn.mucang.simple.utils.http.MucangNameValuePair;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mc-050 on 2016/10/26.
 */
public class Test1026 {

    private static String secretKey = "pWX6HP8b0SKP";

    private static NeptuneHelper neptuneHelper;

    static {
        neptuneHelper = new NeptuneHelper();
        neptuneHelper.setApiHost("http://neptune.mucang.cn");
    }

    public static void main(String[] args) {
        try {
            test();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void test() throws Exception {
        Map<String, Object> map = new HashMap();
        map.put("userId", 493);
        doPost("http://sso.kakamobi.com/api/internal/group-user-internal/list-some-group-user.htm",null,map);
    }

    private static  <T> List<T> doPostList(Class clazz, String url, Object object, Map map) throws Exception {
        return JSON.parseArray(doPost(url, object, map).getString("data"), clazz);
    }

    private static JSONObject doPost(String url, Object object, Map map) throws Exception {
        MucangHttpClient httpClient = MucangHttpClient.getDefault();
        String sign = SignUtils.signUrl(url, secretKey);
        JSONObject jsonObject = new JSONObject();
        if (object != null) {
            jsonObject = JSON.parseObject(JSON.toJSONString(object));
        }
        if (map != null && map.size() > 0) {
            jsonObject.putAll(map);
        }
        List<MucangNameValuePair> list = new ArrayList<MucangNameValuePair>();
        for (String key : jsonObject.keySet()) {
            MucangNameValuePair data = new MucangNameValuePair(key, jsonObject.getString(key));
            list.add(data);
        }
        String result = httpClient.httpPost(sign, list);
        System.out.println("result=" + result);
        JSONObject resultJsonObject = JSON.parseObject(result);
        if (!resultJsonObject.getObject("success", Boolean.class)) {
            throw new ClientException(resultJsonObject.getObject("message", String.class),
                    resultJsonObject.getObject("errorCode", Integer.class));
        }
        return resultJsonObject;

    }
}
