package cn.mucang.simple.nativecache.aliyunsdk;

import com.alibaba.fastjson.JSON;
import com.aliyuncs.ecs.model.v20140526.*;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.rds.model.v20140815.CreateAccountRequest;
import com.aliyuncs.rds.model.v20140815.CreateDBInstanceRequest;
import com.aliyuncs.rds.model.v20140815.CreateDatabaseRequest;
import com.aliyuncs.rds.model.v20140815.ModifyParameterRequest;
import org.apache.commons.lang3.time.DateUtils;

import java.util.Date;
import java.util.UUID;

/**
 * Created by liangzhiyan on 2017/1/3.
 */
public class CreateEcs extends Base {

    private void create() {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        CreateInstanceRequest request = new CreateInstanceRequest();
        request.setRegionId("cn-hangzhou");
        request.setImageId("");
        request.setInstanceType("ecs.s1.small");
        request.setSecurityGroupId("G1510049939692405");
        request.setInstanceName("");
        request.setInternetChargeType("PayByBandwidth");//PayByTraffic
        request.setInternetMaxBandwidthOut(5);//单位M
        request.setPassword("");
        request.setSystemDiskSize(40);//单位G
        request.setDataDisk1Size(50);//单位G
        request.setDataDisk1SnapshotId("sp-23lzeu5l2");
        request.setInstanceChargeType("PrePaid");//PostPaid：后付费，即按量付费
        request.setPeriod(12);//一年
        request.setClientToken(uuid);//用于保证请求的幂等性。
    }

    private void listImage() throws ClientException {
        DescribeImagesRequest request = new DescribeImagesRequest();
        request.setRegionId("cn-hangzhou");
        request.setImageOwnerAlias("self");
        request.setPageSize(100);
        DescribeImagesResponse response = client.getAcsResponse(request);
        for (DescribeImagesResponse.Image image : response.getImages()) {
            System.out.println(JSON.toJSONString(image));
        }
    }

    public void listSecurityGroups() throws ClientException {
        DescribeSecurityGroupsRequest request = new DescribeSecurityGroupsRequest();
        request.setRegionId("cn-hangzhou");
        DescribeSecurityGroupsResponse response = client.getAcsResponse(request);
        for (DescribeSecurityGroupsResponse.SecurityGroup securityGroup : response.getSecurityGroups()) {
            System.out.println(JSON.toJSONString(securityGroup));
        }
    }

    /**
     * 执行自动快照策略
     * 一个磁盘只能执行一条自动快照策略，已拥有自动快照策略的情况下再进行执行操作视为修改当前执行的自动快照策略。
     * 可同时对多个磁盘执行同一条自动快照策略。
     * 对多个磁盘执行同一条自动快照策略时，不能保证这些磁盘的快照数据处于同一个时间点，即无法保证他们是并行快照。
     */
    public void applyAutoSnapshotPolicy() {
        ApplyAutoSnapshotPolicyRequest request = new ApplyAutoSnapshotPolicyRequest();
        request.setRegionId("cn-hangzhou");
        request.setautoSnapshotPolicyId("sp-23lzeu5l2");
        request.setdiskIds("");
    }

    public void listDisk() throws ClientException {
        DescribeDisksRequest request = new DescribeDisksRequest();
        request.setRegionId("cn-hangzhou");
        request.setInstanceId("i-bp197iltyo55b5fdcm4j");
        DescribeDisksResponse response = client.getAcsResponse(request);
        for (DescribeDisksResponse.Disk disk : response.getDisks()) {
            System.out.println(JSON.toJSONString(disk));
        }
    }

    /*****************************创建数据库********************************************/

    public void createRdsInstance() {
        CreateDBInstanceRequest request = new CreateDBInstanceRequest();
        request.setRegionId("cn-hangzhou");
        request.setEngine("mysql");
        request.setEngineVersion("5.5");
        request.setDBInstanceClass("rds.mysql.s1.small");//默认1核2G
        request.setDBInstanceStorage(10);//每5G进行递增
        request.setDBInstanceNetType("Intranet");//实例的网络连接类型：Internet代表公网，Intranet代表私网
        request.setSecurityIPList("");
        request.setSystemDBCharset("utf8mb4");
        request.setPayType("Prepaid");
        request.setPeriod("Year");
        request.setUsedTime("1");
        request.setClientToken("");
    }

    public void createDb(){
        CreateDatabaseRequest request = new CreateDatabaseRequest();
        request.setDBInstanceId("");
        request.setDBName("");
        request.setCharacterSetName("utf8mb4");
    }

    public void createAccount(){
        CreateAccountRequest request = new CreateAccountRequest();
        request.setDBInstanceId("");
        request.setActionName("");
        request.setAccountPassword("");
    }

    /**
     * 修改数据库编码，并重启
     */
    public void modifyChar() {
        ModifyParameterRequest request = new ModifyParameterRequest();
        request.setDBInstanceId("");
        request.setParameters("{\"character_set_client\":\"UTF8MB4\"}");
        request.setForcerestart(true);//需要重启
    }

    public static void main(String[] args) {
        CreateEcs createEcs = new CreateEcs();
        try {
//            createEcs.listImage();
//            createEcs.listSecurityGroups();
//            createEcs.listDisk();
//            System.out.println(UUID.randomUUID().toString().replace("-",""));
            String time = "2014-08-06";
            Date date = DateUtils.parseDate(time,"yyyy-MM-dd");
            System.out.println((new Date().getTime() - date.getTime()) / 86400000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
