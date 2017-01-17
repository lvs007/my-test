package cn.mucang.simple.nativecache.test;

import cn.mucang.score.center.common.utils.DateTimeUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.joda.time.DateTime;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mc-050 on 2016/10/19.
 */
public class Test1019 {

    public enum ResourceTypeEnum {
        ecs, rds, mns, ons, cdn, slb, drds
    }

    public static void main(String[] args) {
        System.out.println(read9());
//        insert();
        insertEmp();
    }

    public static void insert() {
        String path8 = "D://test/1990-02-19-8.csv";
        File file8 = new File(path8);
        File wFile = new File("D://test/insertempinfo_new.csv");
        String common = "insert into t_employee_info(employee_id,office_place,department_id,company_id,company_name,department_name,title,job_type,begin_time,create_time) values";
        try {
            Map<Long, String> map = read9();
            List<String> lines = FileUtils.readLines(file8, "UTF-8");
            for (String line : lines) {
                if (StringUtils.isBlank(line)) {
                    continue;
                }
                line = line.replaceAll("\"", "");
                EmployeeInfo employeeInfo = EmployeeInfo.build(line);
                employeeInfo.setEntry_time(map.get(employeeInfo.getSso_id()));
                if (employeeInfo.getStatus() == 0) {
                    continue;
                }
                FileUtils.write(wFile, common + employeeInfo.build() + ";", "UTF-8", true);
                FileUtils.write(wFile, "\r\n", true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void insertEmp() {
        String path8 = "D://test/1990-02-19-12.csv";
        File file8 = new File(path8);
        File wFile = new File("D://test/insertemp.csv");
        String common = "insert into t_employee(sso_id,fullname,entry_time,status,create_time) values";
        try {
            List<String> lines = FileUtils.readLines(file8, "UTF-8");
            for (String line : lines) {
                if (StringUtils.isBlank(line)) {
                    continue;
                }
                line = line.replaceAll("\"", "");
                Employee employeeInfo = Employee.build(line);
                FileUtils.write(wFile, common + employeeInfo.build() + ";", "UTF-8", true);
                FileUtils.write(wFile, "\r\n", true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class Employee {
        private long sso_id;
        private String fullname;
        private String entry_time;
        private int status = 2;
        private String create_time = "2016-10-26";

        private static SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");

        public static Employee build(String line) throws ParseException {
            String[] array = line.split(",");
            Employee employee = new Employee();
            employee.setSso_id(Long.parseLong(array[0]));
            employee.setFullname(array[1]);
            employee.setEntry_time(sf.format(DateUtils.parseDate(array[2], "yyyy-MM-dd hh:mm:ss")));
            return employee;
        }

        public long getSso_id() {
            return sso_id;
        }

        public void setSso_id(long sso_id) {
            this.sso_id = sso_id;
        }

        public String getFullname() {
            return fullname;
        }

        public void setFullname(String fullname) {
            this.fullname = fullname;
        }

        public String getEntry_time() {
            return entry_time;
        }

        public void setEntry_time(String entry_time) {
            this.entry_time = entry_time;
        }

        public String build() {
            return
                    "(" + sso_id +
                            ",'" + fullname + '\'' +
                            ",'" + entry_time + '\'' +
                            "," + status +
                            ",'" + create_time + ")";
        }
    }

    private static class EmployeeInfo {
        private long id;
        private long sso_id;
        private String office_place;
        private Long department_id;
        private Long company_id;
        private String company_name;
        private String department_name;
        private String title;
        private String job_type;
        private int status;
        private String entry_time;

        public static EmployeeInfo build(String line) {
            String[] array = line.split(",");
            EmployeeInfo employeeInfo = new EmployeeInfo();
            employeeInfo.setId(Long.parseLong(array[0]));
            employeeInfo.setSso_id(Long.parseLong(array[1]));
            employeeInfo.setOffice_place(array[4]);
            employeeInfo.setDepartment_id(array[5].equals("null") ? null : Long.parseLong(array[5]));
            employeeInfo.setCompany_id(array[6].equals("null") ? null : Long.parseLong(array[6]));
            employeeInfo.setCompany_name(array[7]);
            employeeInfo.setDepartment_name(array[8]);
            employeeInfo.setTitle(array[9]);
            employeeInfo.setJob_type(array[10]);
            employeeInfo.setStatus(Integer.parseInt(array[11]));
            return employeeInfo;
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public long getSso_id() {
            return sso_id;
        }

        public void setSso_id(long sso_id) {
            this.sso_id = sso_id;
        }

        public String getOffice_place() {
            return office_place;
        }

        public void setOffice_place(String office_place) {
            this.office_place = office_place;
        }

        public Long getDepartment_id() {
            return department_id;
        }

        public void setDepartment_id(Long department_id) {
            this.department_id = department_id;
        }

        public Long getCompany_id() {
            return company_id;
        }

        public void setCompany_id(Long company_id) {
            this.company_id = company_id;
        }

        public String getCompany_name() {
            return company_name;
        }

        public void setCompany_name(String company_name) {
            this.company_name = company_name;
        }

        public String getDepartment_name() {
            return department_name;
        }

        public void setDepartment_name(String department_name) {
            this.department_name = department_name;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getJob_type() {
            return job_type;
        }

        public void setJob_type(String job_type) {
            this.job_type = job_type;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getEntry_time() {
            return entry_time;
        }

        public void setEntry_time(String entry_time) {
            this.entry_time = entry_time;
        }

        public String build() {
            return
                    "(" + id +
                            ",'" + office_place + '\'' +
                            "," + department_id +
                            "," + company_id +
                            ",'" + company_name + '\'' +
                            ",'" + department_name + '\'' +
                            ",'" + title + '\'' +
                            ",'" + job_type + '\'' +
                            ",'" + entry_time + '\'' +
                            ",'2016-10-26')";
        }

        @Override
        public String toString() {
            return "EmployeeInfo{" +
                    "id=" + id +
                    ", office_place='" + office_place + '\'' +
                    ", department_id=" + department_id +
                    ", company_id=" + company_id +
                    ", company_name='" + company_name + '\'' +
                    ", department_name='" + department_name + '\'' +
                    ", title='" + title + '\'' +
                    ", job_type='" + job_type + '\'' +
                    ", entry_time='" + entry_time + '\'' +
                    '}';
        }
    }

    public static Map<Long, String> read9() {
        String path9 = "D://test/1990-02-19-11.csv";
        File file9 = new File(path9);
        Map<Long, String> map = new HashMap<>();
        try {
            List<String> lines = FileUtils.readLines(file9, "UTF-8");
            for (String line : lines) {
                if (StringUtils.isBlank(line)) {
                    continue;
                }
                line = line.replaceAll("\"", "");
                String[] array = line.split(",");
                if (array.length > 2) {
                    map.put(Long.parseLong(array[0]), array[2]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }
}
