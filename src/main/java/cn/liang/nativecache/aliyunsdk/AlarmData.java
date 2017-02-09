package cn.liang.nativecache.aliyunsdk;

import java.io.Serializable;
import java.util.List;

/**
 * Created by liangzhiyan on 2016/11/11.
 */
public class AlarmData implements Serializable {
    private long lastTime;
    private List<String> contactGroups;
    private String expression;
    private String metricName;
    private int level;
    private String project;
    private String metricProject;
    private String userId;
    private String evaluationCount;
    private String alertName;
    private Receivers receivers;
    private int sendStatus;
    private long alertTime;
    private String value;
    private String rowKey;
    private String dimensions;

    public String getDimensions() {
        return dimensions;
    }

    public void setDimensions(String dimensions) {
        this.dimensions = dimensions;
    }

    public static class Dimensions {
        private String userId;
        private String instanceId;
        private String type;

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getInstanceId() {
            return instanceId;
        }

        public void setInstanceId(String instanceId) {
            this.instanceId = instanceId;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        @Override
        public String toString() {
            return "Dimensions{" +
                    "userId='" + userId + '\'' +
                    ", instanceId='" + instanceId + '\'' +
                    ", type='" + type + '\'' +
                    '}';
        }
    }

    public static class Receivers {
        private List<String> Mail;
        private List<String> ALIIM;
        private List<String> SMS;
        private List<String> HttpNotify;
        private List<String> Goc;
        private List<String> EmpId;
        private List<String> LogHub;
        private List<String> DING;

        public List<String> getMail() {
            return Mail;
        }

        public void setMail(List<String> mail) {
            Mail = mail;
        }

        public List<String> getALIIM() {
            return ALIIM;
        }

        public void setALIIM(List<String> ALIIM) {
            this.ALIIM = ALIIM;
        }

        public List<String> getSMS() {
            return SMS;
        }

        public void setSMS(List<String> SMS) {
            this.SMS = SMS;
        }

        public List<String> getHttpNotify() {
            return HttpNotify;
        }

        public void setHttpNotify(List<String> httpNotify) {
            HttpNotify = httpNotify;
        }

        public List<String> getGoc() {
            return Goc;
        }

        public void setGoc(List<String> goc) {
            Goc = goc;
        }

        public List<String> getEmpId() {
            return EmpId;
        }

        public void setEmpId(List<String> empId) {
            EmpId = empId;
        }

        public List<String> getLogHub() {
            return LogHub;
        }

        public void setLogHub(List<String> logHub) {
            LogHub = logHub;
        }

        public List<String> getDING() {
            return DING;
        }

        public void setDING(List<String> DING) {
            this.DING = DING;
        }

        @Override
        public String toString() {
            return "Receivers{" +
                    "Mail=" + Mail +
                    ", ALIIM=" + ALIIM +
                    ", SMS=" + SMS +
                    ", HttpNotify=" + HttpNotify +
                    ", Goc=" + Goc +
                    ", EmpId=" + EmpId +
                    ", LogHub=" + LogHub +
                    ", DING=" + DING +
                    '}';
        }
    }

    public long getLastTime() {
        return lastTime;
    }

    public void setLastTime(long lastTime) {
        this.lastTime = lastTime;
    }

    public List<String> getContactGroups() {
        return contactGroups;
    }

    public void setContactGroups(List<String> contactGroups) {
        this.contactGroups = contactGroups;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public String getMetricName() {
        return metricName;
    }

    public void setMetricName(String metricName) {
        this.metricName = metricName;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getMetricProject() {
        return metricProject;
    }

    public void setMetricProject(String metricProject) {
        this.metricProject = metricProject;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEvaluationCount() {
        return evaluationCount;
    }

    public void setEvaluationCount(String evaluationCount) {
        this.evaluationCount = evaluationCount;
    }

    public String getAlertName() {
        return alertName;
    }

    public void setAlertName(String alertName) {
        this.alertName = alertName;
    }

    public Receivers getReceivers() {
        return receivers;
    }

    public void setReceivers(Receivers receivers) {
        this.receivers = receivers;
    }

    public int getSendStatus() {
        return sendStatus;
    }

    public void setSendStatus(int sendStatus) {
        this.sendStatus = sendStatus;
    }

    public long getAlertTime() {
        return alertTime;
    }

    public void setAlertTime(long alertTime) {
        this.alertTime = alertTime;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getRowKey() {
        return rowKey;
    }

    public void setRowKey(String rowKey) {
        this.rowKey = rowKey;
    }

//    public Dimensions getDimensions() {
//        return dimensions;
//    }
//
//    public void setDimensions(Dimensions dimensions) {
//        this.dimensions = dimensions;
//    }

    @Override
    public String toString() {
        return "AlarmData{" +
                "lastTime=" + lastTime +
                ", contactGroups=" + contactGroups +
                ", expression='" + expression + '\'' +
                ", metricName='" + metricName + '\'' +
                ", level=" + level +
                ", project='" + project + '\'' +
                ", metricProject='" + metricProject + '\'' +
                ", userId='" + userId + '\'' +
                ", evaluationCount='" + evaluationCount + '\'' +
                ", alertName='" + alertName + '\'' +
                ", receivers=" + receivers +
                ", sendStatus=" + sendStatus +
                ", alertTime=" + alertTime +
                ", value='" + value + '\'' +
                ", rowKey='" + rowKey + '\'' +
                ", dimensions=" + dimensions +
                '}';
    }
}
