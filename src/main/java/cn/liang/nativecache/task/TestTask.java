package cn.liang.nativecache.task;

import cn.mucang.simple.utils.api.ApiResponse;
import cn.mucang.simple.utils.api.BaseApi;
import cn.mucang.simple.utils.api.exception.ApiException;
import cn.mucang.simple.utils.api.exception.HttpException;
import cn.mucang.simple.utils.api.exception.InternalException;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mc-050 on 2016/9/28.
 */
public class TestTask {

    public static void main(String[] args) {
        Http http = new Http();
        String userId = "test";
        String date = "2016-09-25";
        int days = 2;
        try {
            http.listCheckIn();
//            http.listMonthCheckIn();
//            http.listSerialCheckIn();
//            http.checkIn(userId);
//            http.checkInAgain(userId, date);
//            http.serialCheckIn(userId, days);
//            http.getSerialDays(userId);
//            String title = "0701谭谈交通+成都天欣驾校教练被谭sir抓";
//            int index = title.indexOf("</a>");
//            System.out.println(index);
//            if (index >= 0){
//                title = title.substring(index+4);
//            }
//            System.out.println(title);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class Http extends BaseApi {

        public void listCheckIn() throws InternalException, ApiException, HttpException {
            ApiResponse response = httpGet("/api/open/check-in/list-check-in.htm?_productCategory=jiakaobaodian");
            System.out.println(response.getJsonObject());
        }

        public void listMonthCheckIn() throws InternalException, ApiException, HttpException {
            ApiResponse response = httpGet("/api/open/check-in/list-month-check-in.htm?_productCategory=jiakaobaodian");
            System.out.println(response.getJsonObject());
        }

        public void listSerialCheckIn() throws InternalException, ApiException, HttpException {
            ApiResponse response = httpGet("/api/open/check-in/list-serial-check-in.htm?_productCategory=jiakaobaodian");
            System.out.println(response.getJsonObject());
        }

        public void checkIn(String userId) throws InternalException, ApiException, HttpException {
            ApiResponse response = httpGet("/api/open/check-in/check-in.htm?_productCategory=jiakaobaodian&userId=" + userId);
            System.out.println(response.getJsonObject());
        }

        public void getSerialDays(String userId) throws InternalException, ApiException, HttpException {
            ApiResponse response = httpGet("/api/open/check-in/get-serial-days.htm?_productCategory=jiakaobaodian&userId=" + userId);
            System.out.println(response.getJsonObject());
        }

        public void checkInAgain(String userId, String date) throws InternalException, ApiException, HttpException {
            ApiResponse response = httpGet("/api/open/check-in/check-in-again.htm?_productCategory=jiakaobaodian&userId=" + userId + "&date=" + date);
            System.out.println(response.getJsonObject());
        }

        public void serialCheckIn(String userId, int serialDays) throws InternalException, ApiException, HttpException {
            ApiResponse response = httpGet("/api/open/check-in/serial-check-in.htm?_productCategory=jiakaobaodian&userId=" + userId + "&serialDays=" + serialDays);
            System.out.println(response.getJsonObject());
        }

        @Override
        protected String getApiHost() {
            return "task-center.ttt.mucang.cn";
        }

        @Override
        protected String getSignKey() {
            return "bpix4kT4nXxBObcaYM";
        }
    }
}
