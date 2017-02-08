package cn.mucang.simple.nativecache.test;

import cn.mucang.simple.utils.api.ApiResponse;
import cn.mucang.simple.utils.api.BaseApi;
import cn.mucang.simple.utils.api.exception.ApiException;
import cn.mucang.simple.utils.api.exception.HttpException;
import cn.mucang.simple.utils.api.exception.InternalException;
import cn.mucang.simple.utils.http.MucangNameValuePair;
import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mc-050 on 2017/2/7 11:33.
 * KIVEN will tell you life,send email to xxx@163.com
 */
public class Test20170207 {

    private static class TestHttp extends BaseApi {

        public Object createNotice() throws InternalException, ApiException, HttpException {
            List<MucangNameValuePair> pairList = new ArrayList<>();
            MucangNameValuePair pair1 = new MucangNameValuePair("sendType", "[2]");
            MucangNameValuePair pair2 = new MucangNameValuePair("title", "测试消息");
            MucangNameValuePair pair3 = new MucangNameValuePair("content", "测试一个消息");
            MucangNameValuePair pair4 = new MucangNameValuePair("userIds", "[270]");
            pairList.add(pair1);
            pairList.add(pair2);
            pairList.add(pair3);
            pairList.add(pair4);
            ApiResponse response = httpPost("/api/internal/helper/notice.htm", pairList);
            return response.getData().getBoolean("value");
        }

        public void get() throws InternalException, ApiException, HttpException {
            ApiResponse response = httpGet("/api/internal/helper/employees.htm");
            System.out.println(JSON.parseArray(response.getJsonObject().get("data").toString()));
        }

        @Override
        protected String getApiHost() {
            return "http://sun.mucang.cn";
        }

        @Override
        protected String getSignKey() {
            return "oCjNeHpZr0z8Vvfm";
        }
    }

    public static void main(String[] args) {
        TestHttp testHttp = new TestHttp();
        try {
//            System.out.println(testHttp.createNotice());
            testHttp.get();
        } catch (InternalException e) {
            e.printStackTrace();
        } catch (ApiException e) {
            e.printStackTrace();
        } catch (HttpException e) {
            e.printStackTrace();
        }
    }
}
