package cn.mucang.simple.nativecache.score;

import cn.mucang.score.center.ScoreCenterManager;
import cn.mucang.score.center.common.ScoreCenterContant;
import cn.mucang.score.center.common.vo.UserScoreVo;
import cn.mucang.simple.utils.http.MucangHttpClient;
import cn.mucang.simple.utils.http.MucangNameValuePair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by mc-050 on 2016/8/26.
 */
public class TestScore {

    private static ScoreCenterManager scoreCenterManager = new ScoreCenterManager();

    public static void main(String[] args) {
        scoreCenterManager.setHost("http://score.cheyouquan.ttt.mucang.cn");
//        addScore();
//        decrScore();
//        thread();
//        queryScore();
//        decrScoreOne();
        try {
            testSign();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void queryScore() {
        for (int i = 0; i < 100; i++) {
            scoreCenterManager.queryScore("test" + i, ScoreCenterContant.Product.jiaolianbaodian);
        }
    }

    //    @Test
    public static void addScore() {
        for (int i = 0; i < 1000; i++) {
            UserScoreVo userScoreVo = new UserScoreVo();
            userScoreVo.setDesc("积分添加测试");
            userScoreVo.setProduct(ScoreCenterContant.Product.jiaolianbaodian);
            userScoreVo.setScore(new Random().nextInt(100) + 1);
            userScoreVo.setSource(ScoreCenterContant.Source.manager_operation);
            userScoreVo.setToken("eqEU$9Jh=OwtMtH&8JBsOwIU");
            userScoreVo.setUserId("test" + i);
            scoreCenterManager.addScore(userScoreVo);
        }
    }

    public static void decrScore() {
        for (int i = 0; i < 100; i++) {
            UserScoreVo userScoreVo = new UserScoreVo();
            userScoreVo.setDesc("积分减少测试");
            userScoreVo.setProduct(ScoreCenterContant.Product.jiaolianbaodian);
            userScoreVo.setScore(new Random().nextInt(100) + 1);
            userScoreVo.setSource(ScoreCenterContant.Source.manager_operation);
            userScoreVo.setToken("eqEU$9Jh=OwtMtH&8JBsOwIU");
            userScoreVo.setUserId("test" + i);
            scoreCenterManager.decrScore(userScoreVo);
        }
    }

    public static void decrScoreOne() {
        UserScoreVo userScoreVo = new UserScoreVo();
        userScoreVo.setDesc("积分减少测试");
        userScoreVo.setProduct(ScoreCenterContant.Product.jiakaobaodian);
        userScoreVo.setScore(10);
        userScoreVo.setSource(ScoreCenterContant.Source.manager_operation);
        userScoreVo.setToken("eqEU$9Jh=OwtMtH&8JBsOwIU");
        userScoreVo.setUserId("test");
        scoreCenterManager.decrScore(userScoreVo);
    }

    public static void testSign() throws IOException {
        String sign = "dc9fccfc6da5cf3e28aa6f52e777f1e1";
        String url = "http://score.cheyouquan.ttt.mucang.cn/api/server/scoreCenter/decrScore.htm?sign=dc9fccfc6da5cf3e28aa6f52e777f1e1";
        MucangHttpClient httpClient = MucangHttpClient.getDefault();
        List<MucangNameValuePair> pairList = new ArrayList<>();
        MucangNameValuePair mucangNameValuePair = new MucangNameValuePair("data", "{\"desc\":\"积分减少测试\",\"product\":\"jiakaobaodian\",\"score\":10,\"source\":\"manager_operation\",\"token\":\"eqEU$9Jh=OwtMtH&8JBsOwIU\",\"userId\":\"test1\"}");
        pairList.add(mucangNameValuePair);
        httpClient.httpPost(url, pairList);
    }

    public static void thread() {

        ExecutorService executorService = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 100; i++) {
            executorService.submit(new ThreadScore());
        }
    }

    public static class ThreadScore implements Runnable {

        @Override
        public void run() {
            UserScoreVo userScoreVo = new UserScoreVo();
            userScoreVo.setDesc("积分减少测试");
            userScoreVo.setProduct(ScoreCenterContant.Product.jiaolianbaodian);
            userScoreVo.setScore(new Random().nextInt(10) + 1);
            userScoreVo.setSource(ScoreCenterContant.Source.manager_operation);
            userScoreVo.setToken("eqEU$9Jh=OwtMtH&8JBsOwIU");
            userScoreVo.setUserId("test0");
            scoreCenterManager.decrScore(userScoreVo);
        }
    }
}
