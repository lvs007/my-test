package cn.mucang.simple.nativecache.aliyunsdk;

import cn.mucang.simple.mq.impl.mns.MNSClient;
import cn.mucang.simple.mq.impl.mns.MNSClientBuilder;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by liangzhiyan on 2016/12/5.
 */
public class AlarmMns {

    private MNSClient client;

    public AlarmMns() {
        client = new MNSClientBuilder().
                setAccessId("LTAIhQw0hzBkRozj").
                setAccessKey("Vlvo2VOCW699vmpRPsXBlNfljPEkYU").
                setEndPoint("http://1510049939692405.mns.cn-hangzhou.aliyuncs.com").
                setQueue("cloud-monitor").build();
    }

    private void read() {
        while (true) {
            String value = client.pop(10);
            if (StringUtils.isNotBlank(value)) {
                System.out.println("[read]" + value);
            }
        }
    }

    public static void main(String[] args) {
        AlarmMns alarmMns = new AlarmMns();
        alarmMns.read();
    }
}
