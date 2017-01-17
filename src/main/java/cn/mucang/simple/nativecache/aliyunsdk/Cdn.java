package cn.mucang.simple.nativecache.aliyunsdk;

import com.alibaba.fastjson.JSON;
import com.aliyuncs.cdn.model.v20141111.DescribeCdnDomainBaseDetailResponse;
import com.aliyuncs.cdn.model.v20141111.DescribeUserDomainsRequest;
import com.aliyuncs.cdn.model.v20141111.DescribeUserDomainsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.HttpResponse;
import com.aliyuncs.slb.model.v20140515.DescribeLoadBalancersResponse;

import java.util.List;

/**
 * Created by mc-050 on 2016/10/8.
 */
public class Cdn extends Base {

    private static void getRdsList() throws ClientException {
        DescribeCdnDomainBaseDetailResponse response;
        request();
    }

    private static void request() throws ClientException {
        //      初始化请求
        DescribeUserDomainsRequest describeUserDomainsRequest = new DescribeUserDomainsRequest();
        describeUserDomainsRequest.setPageNumber(1L);
        describeUserDomainsRequest.setPageSize(50L);
        try {
            DescribeUserDomainsResponse describeUserDomainsResponse = client.getAcsResponse(describeUserDomainsRequest);
            List<DescribeUserDomainsResponse.PageData> pageDataList = describeUserDomainsResponse.getDomains();
            for (DescribeUserDomainsResponse.PageData pageData : pageDataList) {
                System.out.println(JSON.toJSONString(pageData));
//                System.out.println(pageData.getDomainName() + "," + pageData.getCname() + "," + pageData.getDescription());
            }
            HttpResponse httpResponse = client.doAction(describeUserDomainsRequest);

            System.out.println(httpResponse.getUrl());
            System.out.println(new String(httpResponse.getContent()));
            //todo something.
        } catch (ServerException e) {
            e.printStackTrace();
        } catch (ClientException e) {
            e.printStackTrace();
        }
    }

    private static void getResult(DescribeLoadBalancersResponse response) {

    }

    public static void main(String[] args) {
        try {
            getRdsList();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
