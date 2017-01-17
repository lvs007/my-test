package cn.mucang.simple.nativecache.aliyunsdk;

import cn.mucang.simple.utils.api.ApiResponse;
import cn.mucang.simple.utils.api.BaseApi;
import cn.mucang.simple.utils.api.exception.ApiException;
import cn.mucang.simple.utils.api.exception.HttpException;
import cn.mucang.simple.utils.api.exception.InternalException;

/**
 * Created by liangzhiyan on 2016/11/22.
 */
public class Test {

    private static class Http extends BaseApi {

        public void get() throws InternalException, ApiException, HttpException {
            ApiResponse apiResponse = httpGet("/api/open/slave/allocate-url.htm");
            System.out.println(apiResponse.getJsonObject());
        }

        @Override
        protected String getApiHost() {
            return "https://server-comet.mucang.cn";
        }

        @Override
        protected String getSignKey() {
            return "OwCW8dZHx3y6JvaSxU";
        }
    }

    public static void main(String[] args) {
//        Http http = new Http();
//        try {
//            http.get();
//        } catch (InternalException e) {
//            e.printStackTrace();
//        } catch (ApiException e) {
//            e.printStackTrace();
//        } catch (HttpException e) {
//            e.printStackTrace();
//        }
//        System.setProperty("javax.net.ssl.trustStore", "C:\\Program Files\\Java\\jdk1.8.0_73\\jre\\lib\\security\\jssecacerts");
        String image = "http://image.cms.jiaxiaozhijia.com/jiaxiao-cms/2016/11/23/17/100a2e10873f4cc7990f063f586ccd4b_530X397.jpeg";
        if (!image.matches(".*_(\\d*X\\d*)\\..*")) {
            System.out.println("hello go");
        } else {
            System.out.println("budui");
        }
        String phone = "+8612345678911";
        phone = phone.substring(phone.length() - 11);
        System.out.println("phone:"+phone);

    }
}
