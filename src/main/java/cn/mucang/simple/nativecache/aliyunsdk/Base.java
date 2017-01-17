package cn.mucang.simple.nativecache.aliyunsdk;

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
//        String accessKeyId = "8wZVOR8vthCmBFf4";
//        String accessKeySecret = "gs3JFoLNjk5n2BlhDDhATZXJhBMGVm";
//        String accessKeyId = "LTAIhQw0hzBkRozj";
//        String accessKeySecret = "Vlvo2VOCW699vmpRPsXBlNfljPEkYU";
        String accessKeyId = "LTAIjoXy7Bm234kS";
        String accessKeySecret = "WUKntYnfrFtzDjHwwvXSH005lcFSE0";
        //beijing,shanghai,shenzhen,qingdao,hangzhou
        profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, accessKeySecret);
        client = new DefaultAcsClient(profile);

//        profile = DefaultProfile.getProfile("cn-beijing", accessKeyId, accessKeySecret);
    }
}
