package cn.mucang.simple.nativecache.sun;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by liangzhiyan on 2016/12/9.
 */
public class Excel {

    public static void testGenerate(List<Test.Entity> list, String name) throws Exception {
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("评价");
//        HSSFCellStyle titleStyle = HSSFCellStyleBuilder.newInstance(sheet.getWorkbook())
//                .setFont(HSSFFontBuilder.newInstance(sheet.getWorkbook())
//                        .setSize((short) 11)
//                        .setFontName("微软雅黑")
//                        .build())
//                .setBg(new HSSFColor.LIGHT_YELLOW().getIndex())
//                .setAlignAndVerticalCenter()
//                .addDefault()
//                .build();
        HSSFCellStyle style = HSSFCellStyleBuilder.newInstance(sheet.getWorkbook())
                .setAlignAndVerticalCenter()
                .addDefault()
                .build();
        int cellCount = 10;
        sheet.setDefaultColumnWidth(20);
//        {
//            HSSFRow row = sheet.createRow(sheet.getLastRowNum());
//            HSSFSheetUtils.createColumn(row, cellCount, titleStyle);
//            HSSFCell titleCell = HSSFSheetUtils.setColumnValue(row, 0, "合并标题");
//            HSSFSheetUtils.addMergedHorizontalCell(titleCell, cellCount - 1);
//            row.setHeightInPoints(30);
//        }
        {
            HSSFRow row = sheet.createRow(sheet.getLastRowNum());
            HSSFSheetUtils.createColumn(row, cellCount, style);
            int index = 0;
            HSSFSheetUtils.setColumnValue(row, index++, "评价人");
            HSSFSheetUtils.setColumnValue(row, index++, "工作态度（得分）");
            HSSFSheetUtils.setColumnValue(row, index++, "沟通表达能力（得分）");
            HSSFSheetUtils.setColumnValue(row, index++, "专业能力（得分）");
            HSSFSheetUtils.setColumnValue(row, index++, "响应速度（得分）");
            HSSFSheetUtils.setColumnValue(row, index++, "协作满意程度（得分）");
            HSSFSheetUtils.setColumnValue(row, index++, "优点");
            HSSFSheetUtils.setColumnValue(row, index++, "缺点");
            HSSFSheetUtils.setColumnValue(row, index++, "是否被推荐评奖");
            HSSFSheetUtils.setColumnValue(row, index++, "推荐理由");
        }
        DecimalFormat df = new DecimalFormat("0.00");
        for (Test.Entity entity : list) {
            HSSFRow row = sheet.createRow(sheet.getLastRowNum() + 1);
            HSSFSheetUtils.createColumn(row, cellCount, style);
            int index = 0;
            HSSFSheetUtils.setColumnValue(row, index++, entity.getEvaluationPersonName());
            HSSFSheetUtils.setColumnValue(row, index++, df.format(entity.getAttitude()));
            HSSFSheetUtils.setColumnValue(row, index++, df.format(entity.getCommunicate()));
            HSSFSheetUtils.setColumnValue(row, index++, df.format(entity.getMajor()));
            HSSFSheetUtils.setColumnValue(row, index++, df.format(entity.getResponse()));
            HSSFSheetUtils.setColumnValue(row, index++, df.format(entity.getSatisfaction()));
            HSSFSheetUtils.setColumnValue(row, index++, entity.getAdvantage());
            HSSFSheetUtils.setColumnValue(row, index++, entity.getDisadvantage());
            HSSFSheetUtils.setColumnValue(row, index++, entity.isRecommend() ? "是" : "否");
            HSSFSheetUtils.setColumnValue(row, index++, entity.getRecommendReason());
        }

//        {
//            int nameCellIndex = 0;
//            int workCellIndex = 5;
//            int exceptionCellIndex = 6;
//            HSSFRow row = sheet.createRow(sheet.getLastRowNum() + 1);
//            HSSFRow row2 = sheet.createRow(sheet.getLastRowNum() + 1);
//            HSSFSheetUtils.createColumn(row, cellCount, style);
//            HSSFSheetUtils.createColumn(row2, cellCount, style);
//            HSSFSheetUtils.addMergedVerticalRow(row.getCell(nameCellIndex), 1);
//            HSSFSheetUtils.addMergedVerticalRow(row.getCell(workCellIndex), 1);
//            HSSFSheetUtils.addMergedVerticalRow(row.getCell(exceptionCellIndex), 1);
//
//        }
        File file = new File("/data/wuhan/" + name + ".xls");
        file.delete();
        try (OutputStream os = new FileOutputStream(file)) {
            workbook.write(os);
        }
    }

    public static void main(String[] args) throws Exception {
        Excel.testGenerate(new ArrayList<Test.Entity>(), "test");
    }
}
