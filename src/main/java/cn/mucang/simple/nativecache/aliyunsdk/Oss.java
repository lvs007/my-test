package cn.mucang.simple.nativecache.aliyunsdk;

import com.alibaba.fastjson.JSON;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.Bucket;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.ons.model.v20160503.OnsRegionListRequest;
import com.aliyuncs.ons.model.v20160503.OnsRegionListResponse;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mc-050 on 2016/11/2.
 */
public class Oss extends Base {
    private static void getRdsList(String ep) {
        // endpoint以杭州为例，其它region请按实际情况填写
        String endpoint = "http://oss-" + ep + ".aliyuncs.com";
// accessKey请登录https://ak-console.aliyun.com/#/查看
        String accessKeyId = "LTAIuz3oTaDk7nhh";
        String accessKeySecret = "zpPR3uVx6JZZ9ATdvBgkEEbSJ9UDj3";

// 创建OSSClient实例
        OSSClient ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);

// 列举bucket
        List<Bucket> buckets = ossClient.listBuckets();
        System.out.println(ep + "size=" + buckets.size());
        for (Bucket bucket : buckets) {
            System.out.println(JSON.toJSONString(bucket));
//            System.out.println(" - " + bucket.getName());
        }

// 关闭client
        ossClient.shutdown();
    }

    public static void main(String[] args) {
        try {
            for (String endpoint : getOnsRegionList()) {
                if (endpoint.contains("test")) {
                    continue;
                }
                getRdsList(endpoint);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<String> getOnsRegionList() {
        String regionId = "cn-hangzhou";
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
