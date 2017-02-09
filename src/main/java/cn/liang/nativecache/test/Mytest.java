package cn.liang.nativecache.test;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

/**
 * Created by mc-050 on 2016/6/16.
 */
public class Mytest {

    public static void zip(File zipFile) throws IOException {
        ZipFile zip = new ZipFile(zipFile);
        Enumeration entries = zip.entries();
        while (entries.hasMoreElements()) {
            ZipEntry zipEntry = (ZipEntry) entries.nextElement();
            System.out.println("name = " + zipEntry.getName());
        }
    }

    public static void unzip(File zipFile, String descDir, String pre) throws IOException {
        File pathFile = new File(descDir);
        if (!pathFile.exists()) {
            pathFile.mkdirs();
        }
        ZipFile zip = new ZipFile(zipFile);
        for (Enumeration entries = zip.entries(); entries.hasMoreElements(); ) {
            ZipEntry entry = (ZipEntry) entries.nextElement();
            String zipEntryName = entry.getName();
            if (StringUtils.isNotBlank(pre)) {
                zipEntryName = zipEntryName.substring(pre.length());
            }
            InputStream in = zip.getInputStream(entry);
            String outPath = (descDir + zipEntryName).replaceAll("\\*", "/");
            //判断路径是否存在,不存在则创建文件路径
            File file = new File(outPath.substring(0, outPath.lastIndexOf('/')));
            if (!file.exists()) {
                file.mkdirs();
            }
            //判断文件全路径是否为文件夹,如果是上面已经上传,不需要解压
            if (new File(outPath).isDirectory()) {
                continue;
            }
            //输出文件路径信息
            System.out.println(outPath);
            OutputStream out = new FileOutputStream(outPath);
            IOUtils.copy(in, out);
            in.close();
            out.close();
        }
        System.out.println("******************解压完毕********************");
    }

    private static Charset charset = Charset.forName("utf8");

    public static boolean isCanUnzip(File zipFile) throws IOException {
        ZipInputStream zip = new ZipInputStream(new FileInputStream(zipFile), charset);
        int i = 0;
        ZipEntry zipEntry = zip.getNextEntry();
        System.out.println("entry = " + zipEntry);
        while (zipEntry != null) {
            if (zipEntry.getName().endsWith("/index.htm")
                    || zipEntry.getName().endsWith("/index.html")
                    || zipEntry.getName().endsWith("/start.htm")
                    || zipEntry.getName().endsWith("/start.html")) {
                System.out.println("name = " + zipEntry.getName());
                i++;
            }
            System.out.println("name = " + zipEntry.getName());
            if (i >= 2) {
                return true;
            }
            zipEntry = zip.getNextEntry();
        }
        return false;
    }

    public static boolean canUnzip(String file) throws IOException {
        InputStream in = new BufferedInputStream(new FileInputStream(file));
        ZipInputStream zin = new ZipInputStream(in, charset);
        ZipEntry ze;
        while ((ze = zin.getNextEntry()) != null) {
            System.out.println("zipentity = " + ze);
            if (ze.isDirectory()) {
            } else {
                System.err.println("file - " + ze.getName() + " : "
                        + ze.getSize() + " bytes");
                long size = ze.getSize();
                System.out.println("name = " + ze.getName());
            }
        }
        zin.closeEntry();
        return false;
    }

    public static boolean zipF(String file) throws IOException {
        ZipFile zip = new ZipFile(file, charset);
        System.out.println("size = " + zip.size());
        Enumeration entries = zip.entries();
        String index = "";
        List<String> list = new ArrayList<>();
        while (entries.hasMoreElements()) {
            ZipEntry zipEntry = (ZipEntry) entries.nextElement();
            System.out.println(zipEntry.getName());
            if (zipEntry.getName().endsWith("index.htm")
                    || zipEntry.getName().endsWith("index.html")) {
                index = zipEntry.getName();
            }
            if (zipEntry.getName().startsWith("index.htm")) {
                return true;
            }
            list.add(zipEntry.getName());
        }
        if (StringUtils.isNotBlank(index) && index.startsWith("index")) {
            return true;
        } else if (StringUtils.isNotBlank(index)) {
            String pre = index.substring(0, index.lastIndexOf("/") + 1);
            System.out.println("pre = " + pre);
            for (String name : list) {
                if (!name.startsWith(pre)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }


    public static void main(String[] args) {

    }

}
