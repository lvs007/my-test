package cn.mucang.simple.nativecache.sun;

import cn.mucang.simple.utils.api.ApiResponse;
import cn.mucang.simple.utils.api.BaseApi;
import cn.mucang.simple.utils.api.exception.ApiException;
import cn.mucang.simple.utils.api.exception.HttpException;
import cn.mucang.simple.utils.api.exception.InternalException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.List;

/**
 * Created by liangzhiyan on 2016/12/9.
 */
public class Test {
    private static String url = "http://server.kakamobi.cn/api/admin/sql/query.htm";

    static class Http extends BaseApi {

        private void query(long userId, String userName) throws Exception {
            ApiResponse response = httpGet("/api/internal/lin-shi/list-one.htm?userId=" + userId + "&groupId=11");
            List<Entity> list = response.getDataArray(Entity.class);
            Excel.testGenerate(list, userName);
//            System.out.println(response.getDataArray(Entity.class));
        }

        private long query(String userName) throws InternalException, ApiException, HttpException {
            ApiResponse response = httpGet("/api/internal/lin-shi/find-by-name.htm?name=" + userName);
            return response.getData().getLong("value");
        }

        @Override
        protected String getApiHost() {
            return "http://sun.mucang.cn";
        }

        @Override
        protected String getSignKey() {
            return null;
        }
    }

    public static class Entity {
        /**
         * 分组id（季度）
         */
        private long groupId;
        /**
         * 被评价人
         */
        private long userId;
        private String userName;
        /**
         * 评价人
         */
        private long evaluationPerson;
        private String evaluationPersonName;
        /**
         * 工作态度
         */
        private double attitude;
        /**
         * 沟通表达能力
         */
        private double communicate;
        /**
         * 专业能力
         */
        private double major;
        /**
         * 响应速度
         */
        private double response;
        /**
         * 协作满意程度
         */
        private double satisfaction;
        private String advantage;
        private String disadvantage;
        /**
         * 是否推荐评奖
         */
        private boolean recommend;
        private String recommendReason;

        public long getGroupId() {
            return groupId;
        }

        public void setGroupId(long groupId) {
            this.groupId = groupId;
        }

        public long getUserId() {
            return userId;
        }

        public void setUserId(long userId) {
            this.userId = userId;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public long getEvaluationPerson() {
            return evaluationPerson;
        }

        public void setEvaluationPerson(long evaluationPerson) {
            this.evaluationPerson = evaluationPerson;
        }

        public String getEvaluationPersonName() {
            return evaluationPersonName;
        }

        public void setEvaluationPersonName(String evaluationPersonName) {
            this.evaluationPersonName = evaluationPersonName;
        }

        public double getAttitude() {
            return attitude;
        }

        public void setAttitude(double attitude) {
            this.attitude = attitude;
        }

        public double getCommunicate() {
            return communicate;
        }

        public void setCommunicate(double communicate) {
            this.communicate = communicate;
        }

        public double getMajor() {
            return major;
        }

        public void setMajor(double major) {
            this.major = major;
        }

        public double getResponse() {
            return response;
        }

        public void setResponse(double response) {
            this.response = response;
        }

        public double getSatisfaction() {
            return satisfaction;
        }

        public void setSatisfaction(double satisfaction) {
            this.satisfaction = satisfaction;
        }

        public String getAdvantage() {
            return advantage;
        }

        public void setAdvantage(String advantage) {
            this.advantage = advantage;
        }

        public String getDisadvantage() {
            return disadvantage;
        }

        public void setDisadvantage(String disadvantage) {
            this.disadvantage = disadvantage;
        }

        public boolean isRecommend() {
            return recommend;
        }

        public void setRecommend(boolean recommend) {
            this.recommend = recommend;
        }

        public String getRecommendReason() {
            return recommendReason;
        }

        public void setRecommendReason(String recommendReason) {
            this.recommendReason = recommendReason;
        }

        @Override
        public String toString() {
            return "Entity{" +
                    "groupId=" + groupId +
                    ", userId=" + userId +
                    ", userName='" + userName + '\'' +
                    ", evaluationPerson=" + evaluationPerson +
                    ", evaluationPersonName='" + evaluationPersonName + '\'' +
                    ", attitude=" + attitude +
                    ", communicate=" + communicate +
                    ", major=" + major +
                    ", response=" + response +
                    ", satisfaction=" + satisfaction +
                    ", advantage='" + advantage + '\'' +
                    ", disadvantage='" + disadvantage + '\'' +
                    ", recommend=" + recommend +
                    ", recommendReason='" + recommendReason + '\'' +
                    '}';
        }
    }

    public static void main(String[] args) {
        Http http = new Http();
        try {
            List<String> userNameList = FileUtils.readLines(new File("/data/employee.txt"),"GBK");
            for (String userName : userNameList) {
                if (StringUtils.isBlank(userName)) {
                    continue;
                }
                long userId = http.query(userName);
                http.query(userId, userName);
            }
//            String userName = "梁志彦";
//            long userId = http.query(userName);
//            http.query(userId,userName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
