package cn.mucang.simple.nativecache.test;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;

/**
 * Created by mc-050 on 2016/6/20.
 */
public class TestException {
    public TestException() {
    }

    boolean testEx() throws Exception {
        boolean ret = true;
        try {
            ret = testEx1();
        } catch (Exception e) {
            System.out.println("testEx, catch exception");
            ret = false;
            throw e;
        } finally {
            System.out.println("testEx, finally; return value=" + ret);
            return ret;
        }
    }

    boolean testEx1() throws Exception {
        boolean ret = true;
        try {
            ret = testEx2();
            if (!ret) {
                return false;
            }
            System.out.println("testEx1, at the end of try");
            return ret;
        } catch (Exception e) {
            System.out.println("testEx1, catch exception");
            ret = false;
            throw e;
        } finally {
            System.out.println("testEx1, finally; return value=" + ret);
            return ret;
        }
    }

    boolean testEx2() throws Exception {
        boolean ret = true;
        try {
            int b = 12;
            int c;
            for (int i = 2; i >= -2; i--) {
                c = b / i;
                System.out.println("i=" + i);
            }
            return true;
        } catch (Exception e) {
            System.out.println("testEx2, catch exception");
            ret = false;
            throw e;
        } finally {
            System.out.println("testEx2, finally; return value=" + ret);
            return ret;
        }

    }

    public static void main(String[] args) {
        TestException testException1 = new TestException();
        try {
//            testException1.testEx();
//            String SPLIT = ",|\\r\\n|\\n|\\r";
//            String context = "1231231\r\n4234234\r\n3123143\r\nfadfasdf\r\nfadsfasdffder,gsgadg,123123";
//            System.out.println(Arrays.asList(context.split(SPLIT)));
            read();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void read() throws IOException {
        String path = "D://test/驾考宝典验收测试用例(1).xls";
        Workbook workbook;
        if (path.endsWith(".xls")) {
            workbook = new HSSFWorkbook(new FileInputStream(new File(path)));
        } else if (path.endsWith(".xlsx")) {
            workbook = new XSSFWorkbook(new FileInputStream(new File(path)));
        } else {
            return;
        }
        Sheet sheet = workbook.getSheetAt(0);
        boolean find = false;
        for (int i = 0; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (find == false) {
                Cell cell = row.getCell(0);
                if (StringUtils.contains(cell.getStringCellValue(), "模块")) {
                    find = true;
                }
            } else {
                System.out.println("i = "+i);
                parse(row);
            }
        }
    }

    private static void parse(Row row) {
        int cellNum = 0;
        //
        Cell cell = row.getCell(cellNum++);
        if (cell == null || StringUtils.isBlank(cell.getStringCellValue())){
            return;
        }
        System.out.println(cell.getStringCellValue());
        System.out.println(row.getCell(cellNum++).getStringCellValue());
        String value = row.getCell(cellNum++).getStringCellValue();
        if (StringUtils.isBlank(value)){
            System.out.println("null=============================");
        }
        System.out.println(row.getCell(cellNum++).getStringCellValue());
//
    }
}
