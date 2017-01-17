package cn.mucang.simple.nativecache.aliyunsdk;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.cms.model.QueryMetricListRequest;
import com.aliyuncs.cms.model.QueryMetricListResponse;
import com.aliyuncs.ecs.model.v20140526.*;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.FormatType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;

/**
 * Created by mc-050 on 2016/10/8.
 */
public class Ecs {
    private static IClientProfile profile;
    private static IAcsClient client;

    static {
        String serverUrl = "http://ecs.aliyuncs.com/";
        String accessKeyId = "LTAIjoXy7Bm234kS";
        String accessKeySecret = "WUKntYnfrFtzDjHwwvXSH005lcFSE0";
        profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, accessKeySecret);
        client = new DefaultAcsClient(profile);
//        profile = DefaultProfile.getProfile("cn-beijing", accessKeyId, accessKeySecret);
    }

    private static void getInstances() throws ClientException {
        DescribeInstancesRequest describeInstancesRequest = new DescribeInstancesRequest();
        describeInstancesRequest.setPageSize(100);
        describeInstancesRequest.setPageNumber(1);
        DescribeInstancesResponse response = client.getAcsResponse(describeInstancesRequest);
        int pageNumber = response.getTotalCount() / 100 + 1;
        System.out.println(response.getTotalCount());
        System.out.println(response.getInstances());
        System.out.println(response.getPageNumber());
        System.out.println(response.getPageSize());
//        for (DescribeInstancesResponse.Instance instance : response.getInstances()){
//            System.out.println(instance.getHostName());
//        }
        for (int i = 1; i <= pageNumber; i++) {
            describeInstancesRequest.setPageNumber(i);
            client.getAcsResponse(describeInstancesRequest);
//            System.out.println(response.getInstances());
//            System.out.println(response.getPageNumber());
//            System.out.println(response.getTotalCount());
            for (DescribeInstancesResponse.Instance instance : response.getInstances()) {
//                System.out.println(instance.getPublicIpAddress());
                System.out.println(instance.getInstanceId() + "--" + instance.getExpiredTime());
            }
        }
    }

    public static void testAlarm(String instranceId) throws ClientException {
        QueryMetricListRequest request = new QueryMetricListRequest();
        request.setAcceptFormat(FormatType.JSON);
        request.setProject("acs_ecs");
        request.setMetric("CPUUtilization");
        request.setStartTime("2016-11-23");
        request.setDimensions("{\"instanceId\":\"" + instranceId + "\"}");
        QueryMetricListResponse response = client.getAcsResponse(request);
        System.out.println(response.getDatapoints());
    }

    private static void getInstancesStatus() throws ClientException {
        DescribeInstanceStatusRequest request = new DescribeInstanceStatusRequest();
        DescribeInstanceStatusResponse response = client.getAcsResponse(request);
        System.out.println(response.getTotalCount());
        System.out.println(response.getInstanceStatuses());
    }

    public static void main(String[] args) {
//        DescribeImagesRequest describe = new DescribeImagesRequest();
        DescribeEipAddressesRequest describeEipAddressesRequest = new DescribeEipAddressesRequest();
//        describe.setImageOwnerAlias("self");
        try {
//            DescribeImagesResponse response = client.getAcsResponse(describe);
//            DescribeEipAddressesResponse response = client.getAcsResponse(describeEipAddressesRequest);
//            System.out.println(response.getTotalCount());
//            System.out.println(response.getEipAddresses());
            getInstances();
//            getInstancesStatus();
        } catch (ClientException e) {
            e.printStackTrace();
        }
    }
}
