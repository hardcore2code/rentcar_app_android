package com.static4u.netcar.utils;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.static4u.netcar.constant.URLConstant;
import com.static4u.netcar.ui.DialogMessageUtil;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadUtil {
    public static final String KEY_UPDATE_PROGRESS = "update_progress";
    public static final String KEY_POSITION = "key_position";
    public static final String KEY_DOWNLOAD_STATUS = "key_download_status";
    public static final String ACTION_DOWNLOAD_GUIDE = "action_download_guide";
    // 下载成功即为解压成功
    public final static int DOWNLOAD_ING = 1, DOWNLOAD_FAIL = 2, DOWNLOAD_SUCCESS = 3;

    private Context context;
    private String fileName = ""; // 下载和本地文件名
    private String localPath = ""; // 本地保存路径（包含文件名）
    private String downloadLink = ""; // 下载链接

    private File downloadFile;
    private long remoteFileSize;
    private long currentSize = 0; // 本地已经下载了多少（继续下载，没有效果）

    private ProgressBar pb;
    private DialogMessageUtil util;
    private int progress;

    public enum Status {
        DOWNLOADING, PAUSE, COMPLETE
    }

    public void setUtil(DialogMessageUtil util) {
        this.util = util;
    }

    public String getDownloadUrl() {
        return downloadLink;
    }

    private Status status = Status.DOWNLOADING;

    public void pause() {
        status = Status.PAUSE;
    }

    public void setProgressBar(ProgressBar pb) {
        this.pb = pb;
        if (pb != null) {
            this.pb.setProgress(progress);
        }
        if (util != null) {
            util.setProgress(progress);
        }
    }

    public DownloadUtil(Context context, String link, String name, ProgressBar pb) {
        this.context = context;
        this.downloadLink = link;
        this.fileName = name;
        this.pb = pb;
        if (currentSize > 0) {
            status = Status.PAUSE;
        }
    }

    public void start(String path) {
        status = Status.DOWNLOADING;
        if (context != null && !CommonUtil.isEmptyOrNull(downloadLink) && !CommonUtil.isEmptyOrNull(fileName)) {
            File dir = new File(path);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            localPath = path + fileName;

            if (path != null) {
                downloadFile = new File(localPath);
                if (downloadFile.exists()) {
                    deleteFile(downloadFile);
                }

                // 开始下载
                new Thread(new UpdateThread()).start();
            }
        }
    }

    /**
     * 继续下载前初始化
     */
    private void initBeforeResume() {
        try {
            URL url = new URL(downloadLink);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(5000);
            connection.setRequestMethod("GET");
            remoteFileSize = connection.getContentLength();

            File file = new File(localPath);
            if (!file.exists()) {
                file.createNewFile();
            }
            // 本地访问文件
            RandomAccessFile accessFile = new RandomAccessFile(file, "rwd");
            accessFile.setLength(remoteFileSize);
            accessFile.close();
            connection.disconnect();

            if (pb != null) {
                pb.setProgress((int) (currentSize * 100 / remoteFileSize));
            }
        } catch (Exception e) {
            if (URLConstant.FORCE_LOG) {
                e.printStackTrace();
            }
        }
    }

    class UpdateThread implements Runnable {
        Message msg = handler.obtainMessage();

        @Override
        public void run() {
            try {
                if (!downloadFile.exists()) {
                    downloadFile.createNewFile();
                }
                initBeforeResume();

                long size = downloadFileWithPause(downloadLink, downloadFile);
                if (status != Status.PAUSE) {

                    if (size > 0 && size == remoteFileSize) {
                        // 下载成功

                        // zipPath = CommonUtil.getSDPath() +
                        // "/mjtt/test/测试主题.zip";
                        // unzipToFolderPath = CommonUtil.getSDPath() +
                        // "/yangchen/Theme/测试主题";

                        // File toFile = new File(unzipToFolderPath);
                        // if (!toFile.exists()) {
                        // toFile.mkdir();
                        // }
                        // try {
                        // ZipUtil.unZipToFolder(zipPath, unzipToFolderPath);

                        msg.what = DOWNLOAD_SUCCESS;
                        handler.sendMessage(msg);
                        // } catch (Exception e) {
                        // e.printStackTrace();
                        // if (toFile.exists()) {
                        // toFile.delete();
                        // }
                        // msg.what = DOWNLOAD_FAIL;
                        // handler.sendMessage(msg);
                        // }
                    } else {
                        // 下载失败
                        msg.what = DOWNLOAD_FAIL;
                        msg.arg1 = (int) (size * 100 / remoteFileSize);
                        progress = msg.arg1;
                        handler.sendMessage(msg);
                    }
                }
            } catch (Exception e) {
                if (URLConstant.FORCE_LOG) {
                    e.printStackTrace();
                }

                // 下载失败
                msg.what = DOWNLOAD_FAIL;
                handler.sendMessage(msg);
            }
        }
    }

    private long downloadFileWithPause(String downloadUrl, File toFile) throws Exception {
        int downloadCount = 0; // 下载百分比*100

        HttpURLConnection connection = null;
        InputStream is = null;
        RandomAccessFile raf = null;
        try {
            URL url = new URL(downloadUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("User-Agent", "PacificHttpClient");
            if (currentSize > 0) {
                connection.setRequestProperty("RANGE", "bytes=" + currentSize + "-" + remoteFileSize);
            }
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(20000);
            if (connection.getResponseCode() == 404) {
                throw new Exception("Download connect net 404!");
            }

            is = connection.getInputStream();
            raf = new RandomAccessFile(toFile, "rwd");
            raf.seek(currentSize);
            byte[] buf = new byte[1024];
            int readSize = -1;
            while ((readSize = is.read(buf)) != -1) {
                // fos.write(buf, 0, readSize);
                if (status == Status.PAUSE) {
                    return currentSize;
                }
                raf.write(buf, 0, readSize);

                currentSize += readSize;

                int temp = (int) (currentSize * 100 / remoteFileSize);
                if (downloadCount == 0 || temp > downloadCount) {
                    downloadCount++;
                    // if (downloadCount == 0 || temp - 5 > downloadCount) {
                    // downloadCount += 5;
                    Message msg = handler.obtainMessage();
                    msg.what = DOWNLOAD_ING;
                    // 模拟下载中，给解压留时间
                    // if (downloadCount > 92) {
                    // downloadCount = 92;
                    // }
                    msg.arg1 = downloadCount;
                    handler.sendMessage(msg);
                }
            }
        } catch (Exception e) {
            if (URLConstant.FORCE_LOG) {
                e.printStackTrace();
            }
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            if (is != null) {
                is.close();
            }
            if (raf != null) {
                raf.close();
            }
        }

        return currentSize;
    }

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);

            Intent it = new Intent(ACTION_DOWNLOAD_GUIDE);
            switch (msg.what) {
                case DOWNLOAD_SUCCESS:
                    status = Status.COMPLETE;

                    it.putExtra(KEY_DOWNLOAD_STATUS, DOWNLOAD_SUCCESS);
                    Toast.makeText(context, "下载成功", Toast.LENGTH_SHORT).show();
                    if (util != null) {
                        util.doDownloadSuccess(100);
                    }
                    break;
                case DOWNLOAD_ING:
                    it.putExtra(KEY_UPDATE_PROGRESS, msg.arg1);
                    if (pb != null) {
                        pb.setProgress(msg.arg1);
                        if (util != null) {
                            util.setProgress(msg.arg1);
                        }
                    }
                    it.putExtra(KEY_DOWNLOAD_STATUS, DOWNLOAD_ING);
                    break;
                case DOWNLOAD_FAIL:
                    it.putExtra(KEY_DOWNLOAD_STATUS, DOWNLOAD_FAIL);
                    Toast.makeText(context, "下载失败，请检查网络后重试", Toast.LENGTH_SHORT).show();
                    if (util != null) {
                        util.doDownloadFail();
                    }
                    break;

                default:
                    break;
            }
            context.sendBroadcast(it);
        }

        ;
    };

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    private void deleteFile(File file) {
        if (file.isFile()) {
            deleteFileSafely(file);
            return;
        }
        if (file.isDirectory()) {
            File[] childFiles = file.listFiles();
            if (childFiles == null || childFiles.length == 0) {
                deleteFileSafely(file);
                return;
            }
            for (int i = 0; i < childFiles.length; i++) {
                deleteFile(childFiles[i]);
            }
            deleteFileSafely(file);
        }
    }

    /**
     * 安全删除文件.
     *
     * @param file
     * @return
     */
    public static boolean deleteFileSafely(File file) {
        if (file != null) {
            String tmpPath = file.getParent() + File.separator + System.currentTimeMillis();
            File tmp = new File(tmpPath);
            file.renameTo(tmp);
            return tmp.delete();
        }
        return false;
    }
}
