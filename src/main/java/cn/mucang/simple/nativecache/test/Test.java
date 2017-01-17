package cn.mucang.simple.nativecache.test;

import cn.mucang.score.center.common.ScoreCenterContant;
import cn.mucang.score.mall.api.MallApi;
import cn.mucang.simple.mvc.exception.ClientException;
import cn.mucang.simple.sso.SSOHelper;
import cn.mucang.simple.utils.MiscUtils;
import cn.mucang.simple.utils.api.ApiResponse;
import cn.mucang.simple.utils.api.BaseApi;
import cn.mucang.simple.utils.http.MucangHttpClient;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math.analysis.interpolation.MicrosphereInterpolatingFunction;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Enumeration;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * Created by mc-050 on 2016/7/29.
 */
public class Test {

    public static void validStatus(int old, int status) {
        if (DemandPlatformStatusEnum.getDemandPlatformStatusEnum(status) == null) {
            throw new ClientException("当前进度状态设置错误", 400);
        }
        if (old == DemandPlatformStatusEnum.finish.value) {
            throw new ClientException("当前平台已经完成，不能再修改", 400);
        }
        if (old == 0) {//未开始的进度，可以进行开始估时、暂停、阻塞操作
            if (status != DemandPlatformStatusEnum.design_ing.value
                    && status != DemandPlatformStatusEnum.design_block.value
                    && status != DemandPlatformStatusEnum.design_stop.value) {
                throw new ClientException("当前进度只能变为设计状态", 400);
            }
        } else {
            String oldStatus = String.valueOf(old);
            String newStatus = String.valueOf(status);
            if (oldStatus.length() == 1) {//已经开始的进度，可以进行完成、暂停、阻塞操作
                if (!newStatus.startsWith(oldStatus)) {
                    throw new ClientException("当前进度状态设置错误", 400);
                }
            } else if (oldStatus.length() == 2) {
                String start = oldStatus.substring(0, 1);
                int end = Integer.parseInt(start) + 1;
                if (oldStatus.endsWith("3") || oldStatus.endsWith("4")) {
                    // 阻塞或暂停的进度，可以进行开始、暂停、阻塞操作
                    if (!newStatus.startsWith(start) || (newStatus.length() == 2 && newStatus.endsWith("2"))) {
                        throw new ClientException("当前进度状态设置错误", 400);
                    }
                } else if (oldStatus.endsWith("2")) {
                    //完成的进度，可以暂停、阻塞、开启下一阶段
                    if ((newStatus.startsWith(start) && newStatus.length() != 1)
                            || end == status) {
                        ;
                    } else {
                        throw new ClientException("当前进度状态设置错误", 400);
                    }
                }

            }
        }
    }

    private static void insert(String userId, String companyId) throws IOException {
        MucangHttpClient httpClient = MucangHttpClient.getDefault();
        String result = httpClient.httpGet("http://sso.kakamobi.com/api/open/test/insert-company.htm?userId=" + userId + "&companyId=" + companyId);
        System.out.println("result = " + result);
    }

    public static void insertCompany() {
        String path = "D://test/北京.txt";
        path = "D://test/武汉.txt";
        File file = new File(path);
        try {
            List<String> userIdList = FileUtils.readLines(file);
//            insert("333", "55");
            for (String userId : userIdList) {
//                insert(userId, "55");//北京：55，武汉：56，天津：57，武汉安米：58
                insert(userId, "56");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
//        long time = DateTimeUtils.getBeforeMonth(1);
//        int date = MiscUtils.dateToInt(new Date(time));
//        System.out.println(date);
//        validStatus(23,22);
//        test();
//        test11();
//        testMall();
//        query();
//        test55();
//        insert();
//        threadQuery();
//        insertCompany();
//        testzip();
//        tttest();
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
//        try {
//            System.out.println(simpleDateFormat.parse("2016-09-29 11:55:53"));
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        System.out.println(MiscUtils.parseDate("2016-09-29 11:55:53","yyyy-MM-dd"));
        update();
    }

    public static void update() {
        String path = "D://test/没有公司人员名单1.csv";
        File file = new File(path);
        File wFile = new File("D://test/update.csv");
        String wuhancommon = "update t_user set partypath=\"武汉木仓信息技术有限公司\" where disabled=0 and id=";
        String beijing = "update t_user set partypath=\"北京木仓科技有限公司\" where id=";
        try {
            List<String> line = FileUtils.readLines(file, "GBK");
            for (String uid : line) {
                System.out.println(uid);
                String[] array = uid.split(",");
                if (array.length > 3 && "北京".equals(array[3])){
                    FileUtils.write(wFile, beijing + array[0] + ";", "UTF-8", true);
                    FileUtils.write(wFile, "\r\n", true);
                }else {
                    FileUtils.write(wFile, wuhancommon + array[0] + ";", "UTF-8", true);
                    FileUtils.write(wFile, "\r\n", true);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void tttest(){
        File file = new File("D://test/insert.csv");
        System.out.println(file.getPath());
        System.out.println(file.getName());
        System.out.println(file.getParent());
    }

    public static void test55() {
        String str = "xxx.jsp";
        System.out.println(str.substring(str.indexOf(".")));
    }

    public static void test11() {
        String key = "nihao##hello";
        long begin = System.currentTimeMillis();
        for (int i = 0; i < 1000; i++) {
            StringUtils.split(key + i, "##");
        }
        long end = System.currentTimeMillis();
        System.out.println("StringUtils.split耗时：" + (end - begin));
        for (int i = 0; i < 1000; i++) {
            key += i;
            key.split("##");
        }
        long endend = System.currentTimeMillis();
        System.out.println("String.split耗时：" + (endend - end));
    }

    public static void test() {
        SSOHelper ssoHelper = new SSOHelper("58d25dcf876c4403a088586e4de5cc49", "PPHc88M1rKt8ITRJuIzwz");
        ssoHelper.setHost("http://sso.kakamobi.com");
//        ssoHelper = new SSOHelper("5ef567ec4c2b4f4f84fca8d9caa7517a", "2eSC9Km5vzlMQUCDVywjq");
//        ssoHelper.setHost("http://127.0.0.1:8080");//
        try {
//            System.out.println(ssoHelper.getRoles());new SSOHelper("5ef567ec4c2b4f4f84fca8d9caa7517a","2eSC9Km5vzlMQUCDVywjq");//
//            System.out.println(ssoHelper.getUserInfo(12));
//            System.out.println(ssoHelper.getUsers());
//            System.out.println(ssoHelper.getRoles());
//            System.out.println(ssoHelper.getRoleUsers("项目管理员"));
//            System.out.println(ssoHelper.getUserOrganization(52, Organization.OrgType.Department));
//            System.out.println(ssoHelper.getOrganization(25));
//            System.out.println(ssoHelper.getOrgUsers(25));
//            System.out.println(ssoHelper.getOrgList(Organization.OrgType.Company).get(0).getName());
//            System.out.println(ssoHelper.getAllUsers());
//            System.out.println(ssoHelper.getSunUserInfo(333));
//            RiddleOne riddle = new RiddleOne();
//            String value = riddle.sign("/api/internal/helper/users.htm?accessId=5ef567ec4c2b4f4f84fca8d9caa7517a&_r=7bb6a91ccb3a4e6b90544638ecbf7bc1", "2eSC9Km5vzlMQUCDVywjq");
//            System.out.println("sign = "+value);//cd9fc774ac16cb73d5dca86c295944aa
//            System.out.println(ssoHelper.getAllProject());
//            System.out.println(ssoHelper.getUserVacation(333));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void testMall() {
        MallApi mallApi = new MallApi();
        mallApi.setHost("http://127.0.0.1:8095");
        try {
            System.out.println(mallApi.setUserLevel("test", 3, ScoreCenterContant.Product.jiakaobaodian));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void insert() {
        String path = "D://test/jiaxiao_db.csv";
        File file = new File(path);
        File wFile = new File("D://test/insertnew.csv");
        String common = "insert into t_user_product_score(user_id,product,score,`status`,create_time,update_time) select t.user_id,\"jiaolianbaodian\" as product,t.score,t.`status`,t.create_time,t.update_time from t_user_score t where t.user_id = ";
        try {
            List<String> line = FileUtils.readLines(file, "UTF-8");
            for (String uid : line) {
                FileUtils.write(wFile, common + uid + ";", "UTF-8", true);
                FileUtils.write(wFile, "\r\n", true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void query() {
        String path = "D://test/uids.csv";
        File file = new File(path);
        File wFile = new File("D://test/score.csv");
        try {
            List<String> line = FileUtils.readLines(file, "UTF-8");
            for (String uid : line) {
                FileUtils.write(wFile, "\"" + uid + "\"" + "\t\"" + String.valueOf(http(uid, "jiakaobaodian")) + "\"", true);
                FileUtils.write(wFile, "\r\n", true);
//                break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static ExecutorService executorService = Executors.newFixedThreadPool(5);

    public static void threadQuery() {
        final String userId = UUID.randomUUID().toString().replace("-", "");
        for (int i = 0; i < 10; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    System.out.println("score = " + http(userId, "jiakaobaodian"));
                }
            }).start();
//            executorService.submit(new Runnable() {
//                @Override
//                public void run() {
//                    System.out.println("score = " + http(userId, "jiakaobaodian"));
//                }
//            });
        }
    }

    private static Integer http(String uid, String product) {
        Http http = new Http();
        try {
            return http.getScore(uid, product);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    static class Http extends BaseApi {

        public int getScore(String uid, String product) throws Exception {
            uid = uid.trim();
            ApiResponse response = httpGet("/api/server/score-center/query-score.htm?uid=" + uid + "&product=" + product);
            JSONObject jsonObject = response.getJsonObject();
            return jsonObject.getInteger("data");
        }

        @Override
        protected String getApiHost() {
//            return "http://score.vega.kakamobi.cn";
            return "http://score.cheyouquan.ttt.mucang.cn";
        }

        @Override
        protected String getSignKey() {
            return "IjUkxVsoNTFdOvp66a";
        }
    }

    public static void testzip() {
        File file = new File("D://test/引导页.zip");
        try {
            System.out.println("come");
            getZipFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static ZipFile delete(ZipFile zip) {
        try {
            System.out.println("mid");
            String fileName = UUID.randomUUID().toString().replace("-", "");
            String outPath = "d:/data/upload/temp/zip-unzip/" + fileName + ".zip";
            OutputStream out = new FileOutputStream(new File(outPath));
            ZipOutputStream zipOutputStream = new ZipOutputStream(out);
            for (Enumeration entries = zip.entries(); entries.hasMoreElements(); ) {
                ZipEntry entry = (ZipEntry) entries.nextElement();
                String zipEntryName = entry.getName();
                if (StringUtils.isBlank(zipEntryName) || zipEntryName.startsWith("__MACOSX")) {
                    System.out.println("nihao");
                    continue;
                }
                zipOutputStream.putNextEntry(entry);
            }
            zipOutputStream.close();
            out.close();
            File file = new File(outPath);
            ZipFile zipFile = getZipFile(file);
            FileUtils.deleteQuietly(file);
            return zipFile;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("[unzip] 解压zip文件出错");
        }
        return null;
    }

    private static ZipFile getZipFile(File zipFile) throws IOException {
        ZipFile zip = new ZipFile(zipFile, Charset.forName("UTF-8"));
        Enumeration entries = zip.entries();
        while (entries.hasMoreElements()) {
            try {
                entries.nextElement();
                zip.close();
                zip = new ZipFile(zipFile, Charset.forName("UTF-8"));
                break;
            } catch (Exception e) {
                zip = new ZipFile(zipFile, Charset.forName("GBK"));
                break;
            }
        }
        ZipFile tmp = delete(zip);
        if (tmp != null) {
            zip = tmp;
        }
        System.out.println("zip="+zip);
        return zip;
    }
}
