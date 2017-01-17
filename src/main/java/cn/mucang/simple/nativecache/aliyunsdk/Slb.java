package cn.mucang.simple.nativecache.aliyunsdk;

import com.alibaba.fastjson.JSON;
import com.aliyuncs.alidns.model.v20150109.DescribeDomainRecordsRequest;
import com.aliyuncs.alidns.model.v20150109.DescribeDomainRecordsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.slb.model.v20140515.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mc-050 on 2016/11/2.
 */
public class Slb extends Base {

    private static void getRdsList() throws ClientException {
        DescribeLoadBalancersResponse response;
        response = request();
        getResult(response);
    }

    private static DescribeLoadBalancersResponse request() throws ClientException {
        DescribeLoadBalancersRequest request = new DescribeLoadBalancersRequest();
        request.setConnectTimeout(10000);
        request.setReadTimeout(200000);
        DescribeLoadBalancersResponse response = client.getAcsResponse(request);
        return response;
    }

    private static void getResult(DescribeLoadBalancersResponse response) {
        System.out.println("size=" + response.getLoadBalancers().size());
        for (DescribeLoadBalancersResponse.LoadBalancer loadBalancer : response.getLoadBalancers()) {
            System.out.println(loadBalancer.getLoadBalancerId() + "," + loadBalancer.getLoadBalancerName());
        }
    }

    private static void getEcs() throws ClientException {
        DescribeLoadBalancerAttributeRequest request = new DescribeLoadBalancerAttributeRequest();
        request.setLoadBalancerId("154e17de11a-cn-hangzhou-dg-a01");
        DescribeLoadBalancerAttributeResponse response = client.getAcsResponse(request);
        List<DescribeLoadBalancerAttributeResponse.BackendServer> backendServerList = response.getBackendServers();
        for (DescribeLoadBalancerAttributeResponse.BackendServer backendServer : backendServerList){
            System.out.println(backendServer.getServerId()+" | "+backendServer.getWeight());
        }
        System.out.println(response.getBackendServers());
    }

    /**
     * **********************负载均衡******************************
     */

    private static void getBalance(String domain) throws ClientException {
        DescribeDomainRecordsRequest describeDomainRecordsRequest = new DescribeDomainRecordsRequest();
        describeDomainRecordsRequest.setPageSize(100L);
        describeDomainRecordsRequest.setPageNumber(1L);
        describeDomainRecordsRequest.setActionName("DescribeDomainRecords");
        describeDomainRecordsRequest.setDomainName(domain);
        DescribeDomainRecordsResponse response = client.getAcsResponse(describeDomainRecordsRequest);
        parseResponse(response);
    }

    private static void parseResponse(DescribeDomainRecordsResponse response) {
        for (DescribeDomainRecordsResponse.Record record : response.getDomainRecords()) {
            System.out.println(record.getRecordId() + " | " + record.getDomainName());
        }
    }

    private static void update() throws ClientException {
        SetBackendServersRequest request = new SetBackendServersRequest();

        List<SetBackendServersResponse.BackendServer> backendServerList = new ArrayList<>();
        SetBackendServersResponse.BackendServer backendServer = new SetBackendServersResponse.BackendServer();
        backendServer.setServerId("i-bp1ci5lznff33galur34");
        backendServer.setWeight("5");
        backendServerList.add(backendServer);

        request.setBackendServers(JSON.toJSONString(backendServerList));
        request.setLoadBalancerId("154e17de11a-cn-hangzhou-dg-a01");
        request.setActionName("SetBackendServers");
        SetBackendServersResponse response = client.getAcsResponse(request);
        for (SetBackendServersResponse.BackendServer server : response.getBackendServers()) {
            System.out.println(server.getServerId() + " | " + server.getWeight());
        }
    }

    public static void main(String[] args) {
        try {
//            getRdsList();
//            getBalance("open.res.kakamobi.cn");
            update();
//            getEcs();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
