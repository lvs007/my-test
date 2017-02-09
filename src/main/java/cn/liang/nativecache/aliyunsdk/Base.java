package cn.liang.nativecache.aliyunsdk;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;

/**
 * Created by mc-050 on 2016/10/8.
 */
public abstract class Base {

    protected static int pageSize = 100;

    protected static IClientProfile profile;
    protected static IAcsClient client;

    static {
        String serverUrl = "http://ecs.aliyuncs.com/";
        String accessKeyId = "123";
        String accessKeySecret = "234";
        //beijing,shanghai,shenzhen,qingdao,hangzhou
        profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, accessKeySecret);
        client = new DefaultAcsClient(profile);

//        profile = DefaultProfile.getProfile("cn-beijing", accessKeyId, accessKeySecret);
    }
}
