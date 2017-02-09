package cn.liang.nativecache.test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mc-050 on 2016/7/19.
 */
public enum DemandPlatformStatusEnum {

    init(0,"初始化","初始化"),

    design_ing(1,"设计进行中","设计"),
    design_finish(12,"设计完成","设计"),
    design_stop(13,"设计暂停中","暂停"),
    design_block(14,"设计阻塞中","阻塞"),

    develop_ing(2,"开发进行中","开发"),
    develop_finish(22,"开发完成","开发"),
    develop_stop(23,"开发暂停中","暂停"),
    develop_block(24,"开发阻塞中","阻塞"),

    test_ing(3,"测试进行中","测试"),
    test_finish(32,"测试完成","测试"),
    test_stop(33,"测试暂停中","暂停"),
    test_block(34,"测试阻塞中","阻塞"),

    check_ing(4,"验收进行中","验收"),
    check_finish(42,"验收完成","验收"),
    check_stop(43,"验收暂停中","暂停"),
    check_block(44,"验收阻塞中","阻塞"),

    finish(5,"完成","完成");

    DemandPlatformStatusEnum(int value, String name, String aliasName){
        this.value = value;
        this.name = name;
        this.aliasName = aliasName;
    }

    public static DemandPlatformStatusEnum getDemandPlatformStatusEnum(int value){
        for (DemandPlatformStatusEnum demandPlatformStatusEnum : values()){
            if (demandPlatformStatusEnum.value == value){
                return demandPlatformStatusEnum;
            }
        }
        return null;
    }

    public static String getAliasName(int value){
        for (DemandPlatformStatusEnum demandPlatformStatusEnum : values()){
            if (demandPlatformStatusEnum.value == value){
                return demandPlatformStatusEnum.aliasName;
            }
        }
        return ""+value;
    }

    public static List<Long> getStatus(String aliasName){
        List<Long> statusList = new ArrayList<>();
        for (DemandPlatformStatusEnum demandPlatformStatusEnum : values()){
            if (demandPlatformStatusEnum.aliasName.equals(aliasName)){
                statusList.add((long) demandPlatformStatusEnum.value);
            }
        }
        return statusList;
    }

    public int value;
    public String name;
    public String aliasName;
}
