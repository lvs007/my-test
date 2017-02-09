package cn.liang.nativecache.aliyunsdk;

import com.aliyuncs.drds.model.v20150413.DescribeDrdsInstancesRequest;
import com.aliyuncs.drds.model.v20150413.DescribeDrdsInstancesResponse;
import com.aliyuncs.exceptions.ClientException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mc-050 on 2016/10/8.
 */
public class Rds extends Base {

    private static void getRdsList() throws ClientException {
        DescribeDrdsInstancesResponse response;
        int pageNumber = 1; //= response.getTotalRecordCount() / pageSize + 1;
        int page = 1;
            response = request(page);
            getResult(response);
    }

    private static DescribeDrdsInstancesResponse request(int pageNumber) throws ClientException {
        DescribeDrdsInstancesRequest request = new DescribeDrdsInstancesRequest();
        request.setConnectTimeout(10000);
        request.setReadTimeout(20000);
        request.setType("1");
        DescribeDrdsInstancesResponse response = client.getAcsResponse(request);
        return response;
    }

    private static void getResult(DescribeDrdsInstancesResponse response){
        for (DescribeDrdsInstancesResponse.Instance dbInstance : response.getData()) {
            System.out.println(dbInstance.getDrdsInstanceId());
        }
    }

    public static void main(String[] args) {
        try {
            getRdsList();
//            List<String> list = new ArrayList<>();
//            list.add("asdfa");
//            list.add("xxdf");
//            System.out.println(StringUtils.join(list,"','"));
//            parseRds("D:\\code\\project\\mall\\score-backend-web\\target\\classes\\application.properties",0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static final String BEGIN_MARK = "#";
    private static final String RDS_MARK = "rds";
    public static void parseRds(String path, long appId) {
        try {
            File file = new File(path);
            List<String> lines = FileUtils.readLines(file);
            List<String> resourceKeyList = new ArrayList<>();
            for (String line : lines) {
                if (StringUtils.isNotBlank(line) && !line.startsWith(BEGIN_MARK) && line.contains(RDS_MARK)) {
                    int index = line.indexOf("//");
                    if (index >= 0) {
                        line = line.substring(index + 2);
                        index = line.indexOf(".");
                        if (index > 0) {
                            line = line.substring(0, index);
                            resourceKeyList.add(line);
                        }
                    }
                }
            }
            System.out.println("list = "+resourceKeyList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
