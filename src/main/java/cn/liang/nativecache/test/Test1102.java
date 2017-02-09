package cn.liang.nativecache.test;

import com.alibaba.fastjson.JSON;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.ons.model.v20160503.OnsRegionListRequest;
import com.aliyuncs.ons.model.v20160503.OnsRegionListResponse;
import com.aliyuncs.ons.model.v20160503.OnsTopicListRequest;
import com.aliyuncs.ons.model.v20160503.OnsTopicListResponse;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mc-050 on 2016/11/2.
 */
public class Test1102 {
    public static void main(String[] args) {
//        for (String regionId : getOnsRegionList()) {
////            if (regionId.contains("test")) {
////                continue;
////            }
//            test(regionId);
//        }
//        getOnsRegionList();
        testtest();
    }

    public static void testtest() {
        String regionId = "cn-hangzhou";
        String accessKey = "LTAIhQw0hzBkRozj";
        String secretKey = "Vlvo2VOCW699vmpRPsXBlNfljPEkYU";
        String endPointName = regionId;
        String productName = "Ons";
        String domain = "ons.cn-hangzhou.aliyuncs.com";
        /**
         *根据自己需要访问的区域选择Region,并设置对应的接入点
         */
        try {
            DefaultProfile.addEndpoint(endPointName, regionId, productName, domain);
        } catch (ClientException e) {
            e.printStackTrace();
        }
        IClientProfile profile = DefaultProfile.getProfile(regionId, accessKey, secretKey);
        IAcsClient iAcsClient = new DefaultAcsClient(profile);
        OnsTopicListRequest request = new OnsTopicListRequest();
        /**
         *ONSRegionId是指你需要API访问MQ哪个区域的资源.
         *该值必须要根据OnsRegionList方法获取的列表来选择和配置,因为OnsRegionId是变动的,不能够写固定值
         */
        request.setOnsRegionId("cn-qingdao-publictest");
        request.setPreventCache(System.currentTimeMillis());
//        request.setTopic("XXXXXXXXXXXXX");
        try {
            OnsTopicListResponse response = iAcsClient.getAcsResponse(request);
            System.out.println("getRequestId:"+response.getRequestId());
            List<OnsTopicListResponse.PublishInfoDo> publishInfoDoList = response.getData();
            System.out.println(publishInfoDoList.size());
            for (OnsTopicListResponse.PublishInfoDo publishInfoDo : publishInfoDoList) {
                System.out.println(JSON.toJSONString(publishInfoDo));
                System.out.println(publishInfoDo.getTopic() + "     " + publishInfoDo.getOwner());
            }
        } catch (ServerException e) {
            e.printStackTrace();
        } catch (ClientException e) {
            e.printStackTrace();
        }
    }

    public static void test(String regionId) {
        String accessKey = "LTAIhQw0hzBkRozj";
        String secretKey = "Vlvo2VOCW699vmpRPsXBlNfljPEkYU";
        String endPointName = regionId;
        String productName = "Ons";
        String domain = "ons." + regionId + ".aliyuncs.com";
        /**
         *根据自己需要访问的区域选择Region,并设置对应的接入点
         */
//        try {
//            DefaultProfile.addEndpoint(endPointName, regionId, productName, domain);
//        } catch (ClientException e) {
//            e.printStackTrace();
//        }
        IClientProfile profile = DefaultProfile.getProfile(regionId, accessKey, secretKey);
        IAcsClient iAcsClient = new DefaultAcsClient(profile);
        OnsTopicListRequest request = new OnsTopicListRequest();
        /**
         *ONSRegionId是指你需要API访问MQ哪个区域的资源.
         *该值必须要根据OnsRegionList方法获取的列表来选择和配置,因为OnsRegionId是变动的,不能够写固定值
         */
        request.setOnsRegionId(regionId);
        request.setPreventCache(System.currentTimeMillis());
//        request.setTopic("XXXXXXXXXXXXX");
        try {
            OnsTopicListResponse response = iAcsClient.getAcsResponse(request);
            System.out.println("getRequestId:"+response.getRequestId());
            List<OnsTopicListResponse.PublishInfoDo> publishInfoDoList = response.getData();
            System.out.println(publishInfoDoList.size());
            for (OnsTopicListResponse.PublishInfoDo publishInfoDo : publishInfoDoList) {
                System.out.println(publishInfoDo.getTopic() + "     " + publishInfoDo.getOwner());
            }
        } catch (ServerException e) {
            e.printStackTrace();
        } catch (ClientException e) {
            e.printStackTrace();
        }
    }

    public static List<String> getOnsRegionList() {
        String regionId = "cn-shenzhen";
        String accessKey = "0UOWlENHxuK7gFNc";
        String secretKey = "Ks8xSxz1uLrlqL9k7t3Ve1A93EVRem";
        String endPointName = "cn-hangzhou";
        String productName = "Ons";
        String domain = "ons.cn-hangzhou.aliyuncs.com";
        /**
         *根据自己需要访问的区域选择Region,并设置对应的接入点
         */
//        try {
//            DefaultProfile.addEndpoint(endPointName, regionId, productName, domain);
//        } catch (ClientException e) {
//            e.printStackTrace();
//        }
        IClientProfile profile = DefaultProfile.getProfile(regionId, accessKey, secretKey);
        IAcsClient iAcsClient = new DefaultAcsClient(profile);
        OnsRegionListRequest request = new OnsRegionListRequest();
        /**
         *ONSRegionId是指你需要API访问MQ哪个区域的资源.
         *该值必须要根据OnsRegionList方法获取的列表来选择和配置,因为OnsRegionId是变动的,不能够写固定值
         */
//        request.setOnsRegionId("daily");
        request.setPreventCache(System.currentTimeMillis());
//        request.setTopic("XXXXXXXXXXXXX");
        List<String> regionIdList = new ArrayList<>();
        try {
            OnsRegionListResponse response = iAcsClient.getAcsResponse(request);
            List<OnsRegionListResponse.RegionDo> publishInfoDoList = response.getData();
            for (OnsRegionListResponse.RegionDo regionDo : publishInfoDoList) {
                regionIdList.add(regionDo.getOnsRegionId());
                System.out.println(regionDo.getOnsRegionId() + "     " + regionDo.getRegionName());
            }
        } catch (ServerException e) {
            e.printStackTrace();
        } catch (ClientException e) {
            e.printStackTrace();
        }
        return regionIdList;
    }
}
