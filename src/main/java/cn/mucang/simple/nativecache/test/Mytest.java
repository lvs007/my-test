package cn.mucang.simple.nativecache.test;

import cn.mucang.simple.mvc.exception.ClientException;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.*;
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
        while (entries.hasMoreElements()){
            ZipEntry zipEntry = (ZipEntry) entries.nextElement();
            System.out.println("name = "+zipEntry.getName());
        }
    }

    public static void unzip(File zipFile,String descDir,String pre) throws IOException{
        File pathFile = new File(descDir);
        if(!pathFile.exists()){
            pathFile.mkdirs();
        }
        ZipFile zip = new ZipFile(zipFile);
        for(Enumeration entries = zip.entries();entries.hasMoreElements();){
            ZipEntry entry = (ZipEntry)entries.nextElement();
            String zipEntryName = entry.getName();
            if (StringUtils.isNotBlank(pre)){
                zipEntryName = zipEntryName.substring(pre.length());
            }
            InputStream in = zip.getInputStream(entry);
            String outPath = (descDir+zipEntryName).replaceAll("\\*", "/");
            //判断路径是否存在,不存在则创建文件路径
            File file = new File(outPath.substring(0, outPath.lastIndexOf('/')));
            if(!file.exists()){
                file.mkdirs();
            }
            //判断文件全路径是否为文件夹,如果是上面已经上传,不需要解压
            if(new File(outPath).isDirectory()){
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
    public static boolean isCanUnzip(File zipFile) throws IOException{
        ZipInputStream zip = new ZipInputStream(new FileInputStream(zipFile),charset);
        int i = 0;
        ZipEntry zipEntry = zip.getNextEntry();
        System.out.println("entry = "+zipEntry);
        while (zipEntry!= null){
            if (zipEntry.getName().endsWith("/index.htm")
                    || zipEntry.getName().endsWith("/index.html")
                    || zipEntry.getName().endsWith("/start.htm")
                    || zipEntry.getName().endsWith("/start.html")){
                System.out.println("name = "+zipEntry.getName());
                i++;
            }
            System.out.println("name = "+zipEntry.getName());
            if (i>=2){
                return true;
            }
            zipEntry = zip.getNextEntry();
        }
        return false;
    }

    public static boolean canUnzip(String file) throws IOException{
        InputStream in = new BufferedInputStream(new FileInputStream(file));
        ZipInputStream zin = new ZipInputStream(in,charset);
        ZipEntry ze;
        while ((ze = zin.getNextEntry()) != null) {
            System.out.println("zipentity = "+ze);
            if (ze.isDirectory()) {
            } else {
                System.err.println("file - " + ze.getName() + " : "
                        + ze.getSize() + " bytes");
                long size = ze.getSize();
                System.out.println("name = "+ze.getName());
            }
        }
        zin.closeEntry();
        return false;
    }

    public static boolean zipF(String file) throws IOException{
        ZipFile zip = new ZipFile(file, charset);
        System.out.println("size = "+zip.size());
        Enumeration entries = zip.entries();
        String index = "";
        List<String> list = new ArrayList<>();
        while (entries.hasMoreElements()){
            ZipEntry zipEntry = (ZipEntry) entries.nextElement();
            System.out.println(zipEntry.getName());
            if (zipEntry.getName().endsWith("index.htm")
                    || zipEntry.getName().endsWith("index.html")){
                index = zipEntry.getName();
            }
            if (zipEntry.getName().startsWith("index.htm")){
                return true;
            }
            list.add(zipEntry.getName());
        }
        if (StringUtils.isNotBlank(index) && index.startsWith("index")){
            return true;
        }else if (StringUtils.isNotBlank(index)){
            String pre = index.substring(0,index.lastIndexOf("/")+1);
            System.out.println("pre = "+pre);
            for (String name : list){
                if (!name.startsWith(pre)){
                    return false;
                }
            }
            return true;
        }
        return false;
    }


    public static void main(String[] args) {
        String str = "D:\\木仓科技相关文档\\需求文档\\积分后台二期.zip";//"D:\\木仓科技相关文档\\社区文档\\社区V2.5.zip";
        str = "D:\\木仓科技相关文档\\bug系统\\驾校之家web3.1-列表页及详情页修改.zip";
        File file = new File(str);
        try {
//            zip(file);
//            unzip(file,"D:/zip/");
//            System.out.println(isCanUnzip(file));
//            System.out.println(File.separator);
//            canUnzip(str);
//            System.out.println(zipF(str));
//
//            String zipEntryName = "afsfa/fasdfa/fdfa.html";
//            String pre = "afsfa/";
//            System.out.println(zipEntryName = zipEntryName.substring(pre.length()));
//            SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
//            System.out.println(sf.parse("2016-06-07").getTime());
//            Long tt = null;
//            long ttt = tt.longValue();

//            String regex = "[1-2]{1}[0-9]{3}-[0-1]{1}[1-9]{1}-[0-3]{1}[0-9]{1}";
//            System.out.println("1997-01-00".matches(regex));
//            System.out.println(JSON.toJSONString(Arrays.asList(keySplit("sun@|api/admin/list.htm"))));
//            System.out.println("--------------------------------------------------------------");
//            String json = "[{\n" +
//                    "platform:\"test\"," +
//                    "status:\"\"," +
//                    "spendTime:\"\"," +
//                    "reason:\"\"" +
//                    "},{\n" +
//                    "platform:\"\"," +
//                    "status:\"\"," +
//                    "spendTime:\"\"," +
//                    "reason:\"\"" +
//                    "}]";
//            for (Object object : JSON.parseArray(json)){
//                JSONObject jsonObject = (JSONObject) object;
//                System.out.println(jsonObject.getInteger("statusss"));
//                System.out.println("obj:"+jsonObject);
//            }
//            System.out.println(JSON.parseArray(json));
//
//            String url = "http://polaris.kakamobi.cn/api/open/monitor-data/set.htm?key=key-43&subKey=2-.kibana&group=abc##as123";
//            System.out.println(url = URLEncoder.encode(url, "UTF-8"));
            String json = "[[true,false,true,false,true],[true,true]]";
            JSONArray jsonArray = JSON.parseArray(json);
            List<List> lists = JSON.parseArray(json, List.class);
            System.out.println(""+lists);
            List<String> list = new ArrayList<>();
            list.add("dfa");
            list.add("adf");
            list.add("zdfafa");
            list.add("daa");
            list.add("ger");
            Collections.sort(list);
            System.out.println("list = "+list);
            validStatus(12, 2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void validStatus(int oldstatus,int status){
        if (status <= 0){
            throw new ClientException("当前进度状态设置错误",400);
        }
        if (oldstatus == 0 && status != 1){
            throw new ClientException("当前进度只能变为设计进行中状态",400);
        }
        String oldStatus = String.valueOf(oldstatus);
        String newStatus = String.valueOf(status);
        if (oldstatus != 0 && oldStatus.length() == 1 && !newStatus.startsWith(oldStatus)){
            throw new ClientException("当前进度没有完成。不能跨进度设置",400);
        }
        if (oldStatus.length() == 2){
            String start = oldStatus.substring(0,1);
            int end = Integer.parseInt(start) + 1;

            if (newStatus.length() == 1 && oldStatus.endsWith("2") && status != end){
                throw new ClientException("当前进度状态设置错误",400);
            }else if (newStatus.length() == 1 && !newStatus.equals(start) && !oldStatus.endsWith("2")){
                throw new ClientException("当前进度状态设置错误",400);
            }
            if (newStatus.length() == 2 && !newStatus.startsWith(start)){
                throw new ClientException("当前进度状态设置错误",400);
            }
        }
    }

    private static String[] keySplit(String key){
        String[] keys = new String[2];
        String[] name = StringUtils.split(key,"\\|");
        if (name.length < 2){
            keys[0] = name[0].trim();
            keys[1] = "";
        }else {
            keys[0] = name[0].trim();
            keys[1] = name[1].trim();
        }
        return keys;
    }
}
