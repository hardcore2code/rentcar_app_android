package com.static4u.netcar.utils;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;

import com.static4u.netcar.constant.URLConstant;

import net.lingala.zip4j.core.ZipFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * 文件管理器
 * Created by static on 21/02/2017.
 */
public class FileUtil {

    /**
     * 删除方法 这里只会删除某个文件夹下的文件，如果传入的directory是个文件，将不做处理 * * @param directory
     */
    public static void deleteDir(File dir) {
        if (dir != null && dir.exists() && dir.isDirectory()) {
            if (dir.isDirectory()) {
                String[] children = dir.list();
                for (int i = 0; i < children.length; i++) {
                    if ((new File(dir, children[i])).isDirectory()) {
                        deleteDir(new File(dir, children[i]));
                    } else {
                        (new File(dir, children[i])).delete();
                    }
                }
            }
        }
    }

    /**
     * 获取文件大小
     *
     * @param file
     * @return
     * @throws Exception
     */
    public static long getFolderSize(File file) throws Exception {
        long size = 0;
        try {
            File[] fileList = file.listFiles();
            for (int i = 0; i < fileList.length; i++) {
                // 如果下面还有文件
                if (fileList[i].isDirectory()) {
                    size = size + getFolderSize(fileList[i]);
                } else {
                    size = size + fileList[i].length();
                }
            }
        } catch (Exception e) {
            if (URLConstant.FORCE_LOG) {
                e.printStackTrace();
            }
        }
        return size;
    }


    /**
     * 格式化单位
     *
     * @param size
     * @return
     */
    public static String getFormatSize(double size) {
        double kiloByte = size / 1024;
        if (kiloByte < 1) {
            // return size + "Byte";
            return "0.0KB";
        }

        double megaByte = kiloByte / 1024;
        if (megaByte < 1) {
            BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));
            return result1.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "KB";
        }

        double gigaByte = megaByte / 1024;
        if (gigaByte < 1) {
            BigDecimal result2 = new BigDecimal(Double.toString(megaByte));
            return result2.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "MB";
        }

        double teraBytes = gigaByte / 1024;
        if (teraBytes < 1) {
            BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));
            return result3.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "GB";
        }
        BigDecimal result4 = new BigDecimal(teraBytes);
        return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString()
                + "TB";
    }

    /**
     * 是否有SD卡
     */
    public static boolean hasSDCard() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * 获取SD卡路径
     *
     * @return
     */
    public static String getSDPath() {
        String sdDir = "";
        // SDカートをあるかどうか判断する
        if (hasSDCard()) {
            // 获取跟目录
            sdDir = Environment.getExternalStorageDirectory().getPath();
        }
        return sdDir;
    }

    /**
     * 获取APP默认存储路径
     */
    public static String getDefaultPath() {
        String path = getSDPath() + "/tax/";
        File dir = new File(path);
        if (!dir.isDirectory()) {
            dir.mkdirs();
        }
        return path;
    }

    /**
     * 读文件
     *
     * @param filePath 文件路径
     * @return 输入流
     * @throws IOException 输入输出异常
     */
    public static String readSDFile(String filePath) {
        String res = "";
        try {
            File file = new File(filePath);
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int count = 0;
            while ((count = fis.read(buffer)) >= 0) {
                baos.write(buffer, 0, count);
            }

            res = Base64Utils.encode(baos.toByteArray());

            fis.close();
        } catch (Exception e) {
            if (URLConstant.FORCE_LOG) {
                e.printStackTrace();
            }
        }
        return res;
    }

    /**
     * 写文件
     *
     * @param fileName  保存到文件路径
     * @param write_str 文件流
     */
    public static void writeSDFile(String fileName, String write_str) {
        try {
            File file = new File(fileName);
            FileOutputStream fos = new FileOutputStream(file);
            byte[] bytes = write_str.getBytes();
            fos.write(bytes);

            fos.close();
        } catch (Exception e) {
            if (URLConstant.FORCE_LOG) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 二进制转字符串
     *
     * @param b
     * @return
     */
    public static String byte2hex(byte[] b) // 二进制转字符串
    {
        StringBuffer sb = new StringBuffer();
        String stmp = "";
        for (int n = 0; n < b.length; n++) {
            stmp = Integer.toHexString(b[n] & 0XFF);
            if (stmp.length() == 1) {
                sb.append("0" + stmp);
            } else {
                sb.append(stmp);
            }

        }
        return sb.toString();
    }

    /**
     * 字符串转二进制
     *
     * @param str 要转换的字符串
     * @return 转换后的二进制数组
     */
    public static byte[] hex2byte(String str) { // 字符串转二进制
        if (str == null)
            return null;
        str = str.trim();
        int len = str.length();
        if (len == 0 || len % 2 == 1)
            return null;
        byte[] b = new byte[len / 2];
        try {
            for (int i = 0; i < str.length(); i += 2) {
                b[i / 2] = (byte) Integer
                        .decode("0X" + str.substring(i, i + 2)).intValue();
            }
            return b;
        } catch (Exception e) {
            return null;
        }
    }

    public static void writeStreamToFile(String folder, String name, byte[] content) {
        try {
            File fileFolder = new File(folder);
            if (!fileFolder.exists()) {
                fileFolder.mkdirs();
            }

            File file = new File(folder + name);
            FileOutputStream outStream = new FileOutputStream(file);
            outStream.write(content);
            outStream.close();
        } catch (Exception e) {
            if (URLConstant.FORCE_LOG) {
                e.printStackTrace();
            }
        }
    }

    public static void writeStreamToFile(String folder, String name, char[] content, String encode) {
        try {
            File fileFolder = new File(folder);
            if (!fileFolder.exists()) {
                fileFolder.mkdirs();
            }

            File file = new File(folder + name);
            FileOutputStream outStream = new FileOutputStream(file);
            OutputStreamWriter osw = new OutputStreamWriter(outStream, Charset.forName(encode));
            osw.write(content);
            osw.close();
            outStream.close();
        } catch (Exception e) {
            if (URLConstant.FORCE_LOG) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @param bmp     获取的bitmap数据
     * @param picName 自定义的图片名
     */
    public static void saveBmp2Gallery(Bitmap bmp, String picName, Context context) {
        String fileName = null;
        //系统相册目录
        String galleryPath = Environment.getExternalStorageDirectory()
                + File.separator + Environment.DIRECTORY_DCIM
                + File.separator + "Camera" + File.separator;
        // 声明文件对象
        File file = null;
        // 声明输出流
        FileOutputStream outStream = null;
        try {
            // 如果有目标文件，直接获得文件对象，否则创建一个以filename为名称的文件
            file = new File(galleryPath, picName + ".jpg");
            // 获得文件相对路径
            fileName = file.toString();
            // 获得输出流，如果文件中有内容，追加内容
            outStream = new FileOutputStream(fileName);
            if (null != outStream) {
                bmp.compress(Bitmap.CompressFormat.JPEG, 90, outStream);
            }
        } catch (Exception e) {
            e.getStackTrace();
        } finally {
            try {
                if (outStream != null) {
                    outStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //通知相册更新
//        MediaStore.Images.Media.insertImage(context.getContentResolver(), bmp, fileName, null);
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(file);
        intent.setData(uri);
        context.sendBroadcast(intent);
//        ToastUtil.show("图片保存成功");
    }

    /**
     * 解压文件（带密码）
     *
     * @param zipFile 文件源
     * @param dest    解压到
     * @param pwd     解压密码
     */
    public static void unzip(File zipFile, String dest, String pwd) {
        try {

            ZipFile zFile = new ZipFile(zipFile);  // 首先创建ZipFile指向磁盘上的.zip文件
            zFile.setFileNameCharset("GBK");       // 设置文件名编码，在GBK系统中需要设置
            if (!zFile.isValidZipFile()) {   // 验证.zip文件是否合法，包括文件是否存在、是否为zip文件、是否被损坏等
//                throw new ZipException("压缩文件不合法,可能被损坏.");
            }
            File destDir = new File(dest);     // 解压目录
            if (destDir.isDirectory() && !destDir.exists()) {
                destDir.mkdir();
            }
            if (zFile.isEncrypted()) {
                zFile.setPassword(pwd.toCharArray());  // 设置密码
            }
            zFile.extractAll(dest);      // 将文件抽出到解压目录(解压)
        } catch (Exception e) {
            if (URLConstant.FORCE_LOG) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 将assets中的文件写入SD卡
     *
     * @param fileName 文件名
     * @param filePath SD卡目录
     */
    public static void writeAssertsToSDCard(Context context, String fileName, String filePath) {
        InputStream inputStream;
        try {
            inputStream = context.getResources().getAssets().open(fileName);
            File file = new File(filePath);
            if (!file.exists()) {
                file.mkdirs();
            }
            FileOutputStream fileOutputStream = new FileOutputStream(filePath + fileName);
            byte[] buffer = new byte[512];
            int count = 0;
            while ((count = inputStream.read(buffer)) > 0) {
                fileOutputStream.write(buffer, 0, count);
            }
            fileOutputStream.flush();
            fileOutputStream.close();
            inputStream.close();
        } catch (IOException e) {
            if (URLConstant.FORCE_LOG) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 文件是否存在
     *
     * @param filePath
     * @return
     */
    public static boolean isExist(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * <p>解压zip字节流  转换成Map<String ,byte[]> </p>
     *
     * @param data
     * @return
     */
    public static Map<String, byte[]> unZipByte(byte[] data) {
        ZipInputStream in;
        HashMap<String, byte[]> map = new HashMap<>();
        try {
            in = new ZipInputStream(new ByteArrayInputStream(data));
            ZipEntry z;
            while ((z = in.getNextEntry()) != null) {
                // the entry is a directory.
                if (z.isDirectory()) {

                } else {
                    String name = z.getName();
                    int buf;
                    byte[] buffers = new byte[204800];
                    int len = 204800;
                    byte[] data1 = null;
                    // write to ByteArray
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    while ((buf = in.read(buffers, 0, len)) != -1) {
                        out.write(buffers, 0, buf);
                    }
                    map.put(name, out.toByteArray());
                }
            }
        } catch (Exception e) {
            if (URLConstant.FORCE_LOG) {
                e.printStackTrace();
            }
        }
        return map;
    }

    /**
     * 检查文件是否包含后缀名，不包含则添加
     *
     * @param path
     * @return
     */
    public static String checkPathIsFull(String path) {
        String type = getFileType(path);
        if (!path.endsWith(type)) {
            path += "." + type;
        }
        return path;
    }

    /**
     * 检查文件是否是图片
     *
     * @param path
     * @return
     */
    public static boolean isImage(String path) {
        String type = getFileType(path);
        return "jpg".equals(type) || "png".equals(type);
    }

    /**
     * 获取图片类型
     *
     * @param filePath
     * @return
     */
    public static String getFileType(String filePath) {
        HashMap<String, String> mFileTypes = new HashMap<String, String>();
        mFileTypes.put("FFD8FF", "jpg");
        mFileTypes.put("89504E47", "png");
        mFileTypes.put("47494638", "gif");
        mFileTypes.put("49492A00", "tif");
        mFileTypes.put("424D", "bmp");
        //
        mFileTypes.put("41433130", "dwg"); //CAD
        mFileTypes.put("38425053", "psd");
        mFileTypes.put("7B5C727466", "rtf"); //日记本
        mFileTypes.put("3C3F786D6C", "xml");
        mFileTypes.put("68746D6C3E", "html");
        mFileTypes.put("44656C69766572792D646174653A", "eml"); //邮件
        mFileTypes.put("D0CF11E0", "doc");
        mFileTypes.put("5374616E64617264204A", "mdb");
        mFileTypes.put("252150532D41646F6265", "ps");
        mFileTypes.put("255044462D312E", "pdf");
        mFileTypes.put("504B0304", "zip");
        mFileTypes.put("52617221", "rar");
        mFileTypes.put("57415645", "wav");
        mFileTypes.put("41564920", "avi");
        mFileTypes.put("2E524D46", "rm");
        mFileTypes.put("000001BA", "mpg");
        mFileTypes.put("000001B3", "mpg");
        mFileTypes.put("6D6F6F76", "mov");
        mFileTypes.put("3026B2758E66CF11", "asf");
        mFileTypes.put("4D546864", "mid");
        mFileTypes.put("1F8B08", "gz");

        mFileTypes.put("", "");
        return mFileTypes.get(getFileHeader(filePath));
    }

    /**
     * 获取文件头信息
     *
     * @param filePath
     * @return
     */
    public static String getFileHeader(String filePath) {
        FileInputStream is = null;
        String value = "";
        try {
            is = new FileInputStream(filePath);
            byte[] b = new byte[3];
            is.read(b, 0, b.length);
            value = bytesToHexString(b);
        } catch (Exception e) {
            if (URLConstant.FORCE_LOG) {
                e.printStackTrace();
            }
        } finally {
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                    if (URLConstant.FORCE_LOG) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return value;
    }

    /**
     * 将byte字节转换为十六进制字符串
     *
     * @param src
     * @return
     */
    private static String bytesToHexString(byte[] src) {
        StringBuilder builder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }
        String hv;
        for (int i = 0; i < src.length; i++) {
            hv = Integer.toHexString(src[i] & 0xFF).toUpperCase();
            if (hv.length() < 2) {
                builder.append(0);
            }
            builder.append(hv);
        }
        return builder.toString();
    }

    /**
     * 调用系统下载进程下载
     *
     * @param context     调用者
     * @param downloadUrl 下载地址
     * @param savePath    保存路径
     * @param saveName    保存文件名
     * @return 下载ID
     */
    public static long downloadDoc(Context context, String downloadUrl, String savePath, String saveName, String title) {
        // 创建下载请求
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(downloadUrl));
        /*
         * 设置在通知栏是否显示下载通知(下载进度), 有 3 个值可选:
         *    VISIBILITY_VISIBLE:                   下载过程中可见, 下载完后自动消失 (默认)
         *    VISIBILITY_VISIBLE_NOTIFY_COMPLETED:  下载过程中和下载完成后均可见
         *    VISIBILITY_HIDDEN:                    始终不显示通知
         */
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        // 设置通知的标题和描述
        request.setTitle(title);
        request.setDescription(saveName);

        /*
         * 设置允许使用的网络类型, 可选值:
         *     NETWORK_MOBILE:      移动网络
         *     NETWORK_WIFI:        WIFI网络
         *     NETWORK_BLUETOOTH:   蓝牙网络
         * 默认为所有网络都允许
         */
        // request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);

        // 添加请求头
        // request.addRequestHeader("User-Agent", "Chrome Mozilla/5.0");

        // 设置下载文件的保存位置
        File saveFile = new File(savePath, saveName);
        request.setDestinationUri(Uri.fromFile(saveFile));

        /*
         * 2. 获取下载管理器服务的实例, 添加下载任务
         */
        DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);

        // 将下载请求加入下载队列, 返回一个下载ID
        long downloadId = manager.enqueue(request);

        // 如果中途想取消下载, 可以调用remove方法, 根据返回的下载ID取消下载, 取消下载后下载保存的文件将被删除
        // manager.remove(downloadId);

        return downloadId;
    }

    public static String getFileNameInPath(String path) {
        if (CommonUtil.isEmptyOrNull(path)) {
            return "";
        }

        String[] paths = path.split("/");
        if (paths.length < 2) {
            return path;
        }

        return paths[paths.length - 1];
    }

    /**
     * 选择系统方式打开文件
     *
     * @param context 上下文
     * @param path    文件路径
     * @param type    文件类型
     */
    public static void openFile(Context context, String path, String type) {
        // 跳到文件夹
        File dir = new File(path);
        if (!dir.exists()) {
            return;
        }

        Intent it = new Intent(Intent.ACTION_VIEW);
        it.addCategory(Intent.CATEGORY_DEFAULT);
        it.setDataAndType(Uri.fromFile(dir), type);
        context.startActivity(it);
    }
}
