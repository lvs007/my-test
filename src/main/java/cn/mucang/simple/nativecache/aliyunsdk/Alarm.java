package cn.mucang.simple.nativecache.aliyunsdk;

import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.AcsResponse;
import com.aliyuncs.RoaAcsRequest;
import com.aliyuncs.alert.model.v20150815.ListAlertRequest;
import com.aliyuncs.alert.model.v20150815.ListAlertResponse;
import com.aliyuncs.alert.model.v20150815.ListNotifyHistoryRequest;
import com.aliyuncs.alert.model.v20150815.ListNotifyHistoryResponse;
import com.aliyuncs.cms.model.QueryMetricLastResponse;
import com.aliyuncs.cms.model.QueryMetricListRequest;
import com.aliyuncs.cms.model.QueryMetricListResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.FormatType;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by mc-050 on 2016/10/11.
 */
public class Alarm extends Base {

    //执行请求
    protected <T extends RoaAcsRequest, K extends AcsResponse> K execute(T request) {
        try {
            //todo something.
            request.setContentType(FormatType.JSON);
            request.setAcceptFormat(FormatType.JSON);
            AcsResponse response = client.getAcsResponse(request);
            return (K) response;
        } catch (ServerException e) {
            //todo something.
            e.printStackTrace();
        } catch (ClientException e) {
            //todo something.
            e.printStackTrace();
        }
        return null;
    }

    public void listRule() throws InvocationTargetException, IllegalAccessException {
        ListAlertRequest request = new ListAlertRequest();
        request.setProjectName("acs_custom_" + 1510049939692405L);
//        request.setAlertName("dianping%");//实例id+"%"
        request.setPage(1);
        request.setPageSize(100);
        ListAlertResponse response = execute(request);
        if (!response.getcode().equals("200")) {
            System.out.println(response.getmessage());//打印错误信息
        } else {
            int totalCount = Integer.valueOf(response.gettotal());//获取结果总数量
            System.out.println("rule total:"+response.gettotal());
//            System.out.println(response.getDatapoints());//打印结果
            for (JSONObject jsonObject : response.getDatapoints()) {
//                System.out.println(jsonObject);
                AlarmEntity alramEntity = JSONObject.parseObject(jsonObject.toJSONString(), AlarmEntity.class);
                int value = cycleAlarmInfo(alramEntity.getProject(), alramEntity.getName());
                if (value > 0)
                    System.out.println(alramEntity);
            }
        }
    }

    public int cycleAlarmInfo(String projectName, String alertName) {
        ListNotifyHistoryRequest request = new ListNotifyHistoryRequest();
        request.setProjectName(projectName);//报警的projectName
        request.setAlertName(alertName);//报警规则名称
//        request.setStartTime("2016-10-27 12:03:14");//默认一天前
//        request.setEndTime("2016-10-27 14:03:14");//默认当前时间
        request.setPageSize(20);
        request.setPage(1);
        ListNotifyHistoryResponse response = execute(request);
        int totalCount = 0;
        if (!response.getcode().equals("200")) {
            System.out.println(response.getmessage());//打印错误信息
        } else {
            System.out.println("total:"+response.gettotal());
            totalCount = Integer.valueOf(response.gettotal());//获取结果总数量
            if (totalCount > 0) {
//                System.out.println(response.getDatapoints());
                List<AlarmData> alarmDataList = new ArrayList<>();
                for (JSONObject jsonObject : response.getDatapoints()) {
                    alarmDataList.add(JSONObject.parseObject(jsonObject.toJSONString(), AlarmData.class));
                }
                System.out.println(alarmDataList);//打印结果
            }
        }
        return totalCount;
    }

    private static class AlarmEntity {
        private boolean template;
        private String project;
        private String description;
        private long gmtCreate;
        private int type;
        private String userId;
        private String uuid;
        private int status;
        private String name;
        private int interval;
        private String id;
        private String alertState;


        public boolean isTemplate() {
            return template;
        }

        public void setTemplate(boolean template) {
            this.template = template;
        }

        public String getProject() {
            return project;
        }

        public void setProject(String project) {
            this.project = project;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public long getGmtCreate() {
            return gmtCreate;
        }

        public void setGmtCreate(long gmtCreate) {
            this.gmtCreate = gmtCreate;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getUuid() {
            return uuid;
        }

        public void setUuid(String uuid) {
            this.uuid = uuid;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getInterval() {
            return interval;
        }

        public void setInterval(int interval) {
            this.interval = interval;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getAlertState() {
            return alertState;
        }

        public void setAlertState(String alertState) {
            this.alertState = alertState;
        }

        @Override
        public String toString() {
            return "AlramEntity{" +
                    "template=" + template +
                    ", project='" + project + '\'' +
                    ", description='" + description + '\'' +
                    ", gmtCreate=" + gmtCreate +
                    ", type=" + type +
                    ", userId='" + userId + '\'' +
                    ", uuid='" + uuid + '\'' +
                    ", status=" + status +
                    ", name='" + name + '\'' +
                    ", interval=" + interval +
                    ", id='" + id + '\'' +
                    ", alertState='" + alertState + '\'' +
                    '}';
        }
    }

    public void testAlarm() throws ClientException {
        QueryMetricListRequest request = new QueryMetricListRequest();
        request.setAcceptFormat(FormatType.JSON);
        request.setProject("acs_ecs");
        request.setMetric("CPUUtilization");
        request.setStartTime("2016-11-23");
        request.setDimensions("{\"instanceId\":\"i-25snt0az4\"}");
        QueryMetricListResponse response = client.getAcsResponse(request);
        System.out.println(response.getDatapoints());
    }

    public static void main(String[] args) {
        Alarm alarm = new Alarm();
        try {
//            alarm.testAlarm();
            alarm.listRule();
        } catch (Exception e) {
            e.printStackTrace();
        }
        float time = 5.0000f;
        int tmp = (int) time;
        if (time - tmp != 0) {
            System.out.println("时间类型为全天的不能够请几点几天，只能够请整数天，请分开请假");
        }
//        if (!String.valueOf(1).matches("d+.{0,1}0{0,1}")){
//            System.out.println("时间类型为全天的不能够请几点几天，只能够请整数天，请分开请假");
//        }
//        test();
    }

    public static void test() {
        Map<Long, String> map = new ConcurrentHashMap<>();
        map.put(1L, "1nihao");
        map.put(2L, "2nihao");
        map.put(3L, "3nihao");
        System.out.println(map);
        for (String value : map.values()) {
            if (value.contains("2")) {
                map.remove(2L);
            }
        }
        System.out.println(map);
    }

}
