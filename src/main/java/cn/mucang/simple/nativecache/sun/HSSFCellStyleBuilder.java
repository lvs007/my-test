package cn.mucang.simple.nativecache.sun;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

/**
 * Created by zengrenyuan on 16/5/10.
 */
public class HSSFCellStyleBuilder {
    private HSSFCellStyle style;

    private HSSFCellStyleBuilder(HSSFWorkbook workbook) {
        this.style = workbook.createCellStyle();
    }

    public static HSSFCellStyleBuilder newInstance(HSSFWorkbook workbook) {
        return new HSSFCellStyleBuilder(workbook);
    }

    public static HSSFCellStyleBuilder newInstance(HSSFSheet sheet) {
        return newInstance(sheet.getWorkbook());
    }

    /**
     * 设置单元格背景颜色
     *
     * @param bg 颜色索引
     * @return
     */
    public HSSFCellStyleBuilder setBg(short bg) {
        if (bg != 0) {
            style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
            style.setFillForegroundColor(bg);
        }
        return this;
    }

    /**
     * 设置字体
     */
    public HSSFCellStyleBuilder setFont(HSSFFont font) {
        style.setFont(font);
        return this;
    }

    /**
     * 为单完格设置默认的边框(黑色)
     */
    public HSSFCellStyleBuilder addDefault() {
        style.setBorderBottom((short) 1);
        style.setBorderLeft((short) 1);
        style.setBorderRight((short) 1);
        style.setBorderTop((short) 1);
        return this;
    }

    /**
     * 设置居中对齐
     */
    public HSSFCellStyleBuilder setAlignCenter() {
        setAlign(HSSFCellStyle.ALIGN_CENTER);
        return this;
    }


    public HSSFCellStyleBuilder setAlign(short align) {
        style.setAlignment(align);
        return this;
    }

    /**
     * 设置水平垂直居中对齐
     */
    public HSSFCellStyleBuilder setAlignAndVerticalCenter() {
        setAlignCenter();
        setVerticalAlignment();
        return this;
    }

    /**
     * 设置垂直居中对齐
     */
    public HSSFCellStyleBuilder setVerticalAlignment() {
        setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        return this;
    }

    /**
     * 设置垂直对齐方式
     */
    public HSSFCellStyleBuilder setVerticalAlignment(short verticalAlignment) {
        style.setVerticalAlignment(verticalAlignment);
        return this;
    }


    public HSSFCellStyle build() {
        return style;
    }
}