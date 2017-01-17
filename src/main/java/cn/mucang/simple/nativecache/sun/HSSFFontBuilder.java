package cn.mucang.simple.nativecache.sun;

import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

/**
 * Created by zengrenyuan on 16/5/10.
 */
public class HSSFFontBuilder {

    private HSSFFont font;

    private HSSFFontBuilder(HSSFWorkbook workbook) {
        this.font = workbook.createFont();
    }

    public static HSSFFontBuilder newInstance(HSSFWorkbook workbook) {
        return new HSSFFontBuilder(workbook);
    }

    public static HSSFFontBuilder newInstance(HSSFSheet sheet) {
        return newInstance(sheet.getWorkbook());
    }

    /**
     * 设置字体大小
     */
    public HSSFFontBuilder setSize(short size) {
        font.setFontHeightInPoints(size);
        return this;
    }

    /**
     * 设置文字大小方式
     */
    public HSSFFontBuilder setBoldWeight(short boldWeight) {
        font.setBoldweight(boldWeight);
        return this;
    }

    /**
     * 设置字体颜色
     */
    public HSSFFontBuilder setColor(short fontColor) {
        font.setColor(fontColor);
        return this;
    }


    /**
     * 设置字体名称
     */
    public HSSFFontBuilder setFontName(String fontName) {
        font.setFontName(fontName);
        return this;
    }

    public HSSFFont build() {
        return font;
    }
}
