package cn.mucang.simple.nativecache.sun;

import cn.mucang.simple.utils.MiscUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * @author Zeng RenYuan
 * @since 2015-08-06 09:08:18
 */
public class HSSFSheetUtils {

    private static final Logger LOG = LoggerFactory.getLogger(HSSFSheetUtils.class);


    /**
     * 生成单元格
     *
     * @param row        sheet的行
     * @param endIndex   结束单元格索引
     * @param style      单元格的样式
     */
    public static void createColumn(HSSFRow row, int endIndex, HSSFCellStyle style) {
        createColumn(row, 0, endIndex, style);
    }

    /**
     * 生成单元格
     *
     * @param row        sheet的行
     * @param startIndex 开始单元格索引
     * @param endIndex   结束单元格索引
     * @param style      单元格的样式
     */
    public static void createColumn(HSSFRow row, int startIndex, int endIndex, HSSFCellStyle style) {
        for (int i = startIndex; i < endIndex; i++) {
            HSSFCell cell = row.createCell(i);
            cell.setCellStyle(style);
        }
    }


    /**
     * 生成单元格并设置单元格的宽度
     */
    public static HSSFCell setColumnValueAndWidth(HSSFRow row, int cellIndex, String value, int size) {
        HSSFCell cell = setColumnValue(row, cellIndex, value, null);
        cell.getSheet().setColumnWidth(cell.getColumnIndex(), size);
        return cell;
    }

    /**
     * 设置单元格的值
     *
     * @param row       sheet的行
     * @param cellIndex 当前行的单元格索引
     * @param value     单元格的值
     */
    public static HSSFCell setColumnValue(HSSFRow row, int cellIndex, String value) {
        return setColumnValue(row, cellIndex, value, null);
    }

    /**
     * 设置单元格的值
     *
     * @param row       sheet的行
     * @param cellIndex 当前行的单元格索引
     * @param value     单元格的值
     * @param style     单元格的样式
     */
    public static HSSFCell setColumnValue(HSSFRow row, int cellIndex, String value, HSSFCellStyle style) {
        HSSFCell cell = row.getCell(cellIndex);
        if (cell == null) {
            return null;
        }
        cell.setCellValue(value);
        if (style != null) {
            cell.setCellStyle(style);
        }
        return cell;
    }


    /**
     * 设置单完格值,并向下合并单完格
     *
     * @param row       sheet的行
     * @param cellIndex 当前行的单元格索引
     * @param value     单元格的值
     */
    public static void setColumnValueAndMergedVerticalCell(HSSFRow row, int cellIndex, String value) {
        setColumnValueAndMergedVerticalCell(row, cellIndex, value, null);
    }

    /**
     * 设置单完格值,并向下合并单完格
     *
     * @param row       sheet的行
     * @param cellIndex 当前行的单元格索引
     * @param value     单元格的值
     */
    public static void setColumnValueAndMergedVerticalCell(HSSFRow row, int cellIndex, String value, HSSFCellStyle style) {
        setColumnValue(row, cellIndex, value, style);
        addMergedVerticalCell(row, cellIndex);
    }

    /**
     * 向下合单完格
     *
     * @param row       sheet的行
     * @param cellIndex 当前行的单元格索引
     */
    public static void addMergedVerticalCell(HSSFRow row, int cellIndex) {
        addMergedRegion(row, cellIndex, row.getSheet().getRow(row.getRowNum() + 1), cellIndex);
    }

    /**
     * 指定行与列合并单完格
     *
     * @param startRow        合并开始的行号
     * @param startCellIndex  合并开始的列号
     * @param targetRow       合并结束的行号
     * @param targetCellIndex 合并结束的列号
     */
    public static void addMergedRegion(HSSFRow startRow, int startCellIndex, HSSFRow targetRow, int targetCellIndex) {
        HSSFCell startCell = startRow.getCell(startCellIndex);
        HSSFCell targetCell = targetRow.getCell(targetCellIndex);
        if (startCell == null || targetCell == null) {
            LOG.error("error merged region startCell or targetCell is null, (startCell==null)={}  (startCell==null)={}", startCell == null, targetCell == null);
            return;
        }
        addMergedRegion(startCell, targetCell);
    }

    /**
     * 合并单元格
     *
     * @param startCell  合并开始的单元格
     * @param targetCell 合并结束的单元格
     */
    public static void addMergedRegion(HSSFCell startCell, HSSFCell targetCell) {
        if (startCell.getRowIndex() > targetCell.getRowIndex() || startCell.getColumnIndex() > targetCell.getColumnIndex()) {
            LOG.error("error merged region startCell or targetCell is null, row index error:{} cell index error:{} ", startCell.getRowIndex() > targetCell.getRowIndex(), startCell.getColumnIndex() > targetCell.getColumnIndex());
            return;
        }
        CellRangeAddress cellRangeAddress = new CellRangeAddress(
                startCell.getRowIndex(),
                targetCell.getRowIndex(),
                startCell.getColumnIndex(),
                targetCell.getColumnIndex());
        startCell.getSheet().addMergedRegion(cellRangeAddress);
    }


    /**
     * 向下合单完格
     *
     * @param startCell    sheet的单元格
     * @param addMergedRow 要合并的行号
     */
    public static void addMergedVerticalRow(HSSFCell startCell, int addMergedRow) {
        addMergedRegion(startCell, addMergedRow, 0);
    }

    /**
     * 向下合单完格
     *
     * @param startCell      sheet的单元格
     * @param addColumnIndex 要合并的单元格
     */
    public static void addMergedHorizontalCell(HSSFCell startCell, int addColumnIndex) {
        addMergedRegion(startCell, 0, addColumnIndex);
    }

    /**
     * 合并单元格
     *
     * @param startCell      sheet的单元格
     * @param addRowIndex    目标行在当前单元格的基础上增加行号
     * @param addColumnIndex 目标行在当前行增加列号
     */
    public static void addMergedRegion(HSSFCell startCell, int addRowIndex, int addColumnIndex) {
        if (startCell == null) {
            LOG.error("merged region startCell is null");
            return;
        }
        HSSFSheet sheet = startCell.getSheet();
        HSSFRow row = sheet.getRow(startCell.getRowIndex() + addRowIndex);
        if (row == null) {
            LOG.error("merged region target row is null,start row index :{}  target row index:{}", startCell.getRowIndex(), startCell.getRowIndex() + addRowIndex);
            return;
        }
        HSSFCell targetCell = row.getCell(startCell.getColumnIndex() + addColumnIndex);
        if (targetCell == null) {
            LOG.error("merged region target cell is null,start cell index :{}  target cell index:{}", startCell.getColumnIndex(), startCell.getColumnIndex() + addColumnIndex);
            return;
        }
        addMergedRegion(startCell, targetCell);
    }

    /**
     * 读取单元格的内容为字符串
     *
     * @param row       sheet的行对象
     * @param cellIndex 单元格索引
     * @return
     */
    public static String readCellValue(Row row, int cellIndex) {
        Cell cell = row.getCell(cellIndex);
        if (cell == null) {
            return null;
        }
        return readCellValue(cell);
    }

    /**
     * 读取单元格的内容为字符串
     *
     * @param cell 单元格
     * @return
     */
    public static String readCellValue(Cell cell) {
        String ret;
        if (cell == null) {
            return null;
        }
        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_BLANK:
                ret = "";
                break;
            case Cell.CELL_TYPE_BOOLEAN:
                ret = String.valueOf(cell.getBooleanCellValue());
                break;
            case Cell.CELL_TYPE_ERROR:
                ret = null;
                break;
            case Cell.CELL_TYPE_FORMULA:
                Workbook wb = cell.getSheet().getWorkbook();
                CreationHelper crateHelper = wb.getCreationHelper();
                FormulaEvaluator evaluator = crateHelper.createFormulaEvaluator();
                ret = readCellValue(evaluator.evaluateInCell(cell));
                break;
            case Cell.CELL_TYPE_NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    Date theDate = cell.getDateCellValue();
                    ret = MiscUtils.formatDate(theDate, MiscUtils.STANDARDPATTERN);
                } else {
                    ret = NumberToTextConverter.toText(cell.getNumericCellValue());
                }
                break;
            case Cell.CELL_TYPE_STRING:
                ret = cell.getRichStringCellValue().getString();
                break;
            default:
                ret = null;
        }
        return ret;
    }
}