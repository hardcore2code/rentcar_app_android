package com.static4u.netcar.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.media.ExifInterface;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.static4u.netcar.R;
import com.static4u.netcar.constant.URLConstant;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import static android.graphics.BitmapFactory.decodeFile;

/**
 * 图片加载统一控制
 * Created by static on 21/02/2017.
 */
public class ImageLoadUtil {
    /**
     * 默认占位图ID
     */
    private static int IMAGE_PLACE_HOLDER_ID = R.drawable.ic_empty;

    /**
     * 清除图片缓存，必须在主线程调用
     */
    public static void clearMemoryCache(@NonNull Context context) {
        // 该方法必须在主线程调用
        Glide.get(context).clearMemory();
    }

    /**
     * 清除外部缓存，必须在异步线程调用
     *
     * @param context
     */
    public static void clearSDCache(@NonNull Context context) {
        Glide.get(context).clearDiskCache();
    }

    /**
     * ********************************************************   图片加载  ************************************************************************************
     */

    /**
     * 图片加载
     *
     * @param url 图片url
     * @param iv  容器视图
     */
    public static void loadImage(@NonNull Context context, @NonNull String url, @NonNull ImageView iv) {
        try {
            Glide.with(context).load(url).placeholder(IMAGE_PLACE_HOLDER_ID).into(iv);
        } catch (Exception e) {
            if (URLConstant.FORCE_LOG) {
                e.printStackTrace();
            }
        }
    }

    public static void loadImage(@NonNull Context context, @NonNull Bitmap bitmap, @NonNull ImageView iv) {
        try {
            Glide.with(context).load(bitmap).placeholder(IMAGE_PLACE_HOLDER_ID).into(iv);
        } catch (Exception e) {
            if (URLConstant.FORCE_LOG) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 加载图片
     */
    public static void loadImage(@NonNull Context context, @NonNull String url, @NonNull final OnImageSavedListener listener) {
        try {
            Glide.with(context).load(url).asBitmap().into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    if (resource != null && resource.getWidth() * resource.getHeight() > 10) {
                        listener.onImageSavedSuccess(resource);
                    } else {
                        listener.onImageSavedFailed();
                    }
                }
            });
        } catch (Exception e) {
            if (URLConstant.FORCE_LOG) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 图片加载
     *
     * @param url 图片url
     * @param iv  容器视图
     */
    public static void loadImage(@NonNull Context context, @NonNull String url, @NonNull ImageView iv, int placeHolder) {
        try {
            Glide.with(context).load(url).dontAnimate().placeholder(placeHolder).into(iv);
        } catch (Exception e) {
            if (URLConstant.FORCE_LOG) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 图片加载
     *
     * @param img 图片url
     * @param iv  容器视图
     */
    public static void loadImage(@NonNull Context context, @NonNull byte[] img, @NonNull ImageView iv, int placeHolder) {
        try {
            Glide.with(context).load(img).dontAnimate().placeholder(placeHolder).into(iv);
        } catch (Exception e) {
            if (URLConstant.FORCE_LOG) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 加载不带缓存的图片
     *
     * @param url         图片url
     * @param iv          容器
     * @param placeHolder 占位图
     */
    public static void loadImageWithoutCache(@NonNull Context context, @NonNull String url, @NonNull ImageView iv, int placeHolder) {
        // 此处不缓存加载图片，因为重复覆盖同一位置的图片时因缓存无法更新图片
        try {
            Glide.with(context)
                    .load(url)
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .placeholder(placeHolder)
                    .centerCrop()
                    .into(iv);
        } catch (Exception e) {
            if (URLConstant.FORCE_LOG) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 指定质量压缩
     *
     * @param path    图片路径
     * @param quality 质量 0-100,100表示原图
     */
    public static Bitmap losslessScaleByQuality(String path, int quality) {
        Bitmap bitmap = decodeFile(path);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        SLog.e("ImageLoadUtil", "原始大小：" + baos.toByteArray().length);
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
        SLog.e("ImageLoadUtil", "最终大小" + baos.toByteArray().length + "   quality:" + quality);
        return BitmapFactory.decodeByteArray(baos.toByteArray(), 0, baos.toByteArray().length);
    }

    /**
     * 指定大小压缩
     *
     * @param path    图片路径
     * @param maxSize 最终文件大小，单位为KB
     */
    public static Bitmap losslessScaleByMaxsize(String path, int maxSize) {
        Bitmap bitmap = decodeFile(path);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int quality = 100;
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
        SLog.e("ImageLoadUtil", "原始大小：" + baos.toByteArray().length);
        while (baos.toByteArray().length / 1024 > maxSize) {
            quality -= 10;
            baos.reset();
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
            SLog.e("ImageLoadUtil", "过程中大小为：" + baos.toByteArray().length + "   quality:" + quality);
        }

        return BitmapFactory.decodeByteArray(baos.toByteArray(), 0, baos.toByteArray().length);
    }

    /**
     * 保存bitmap到本地
     *
     * @param bitmap 图片源
     * @param path   保存路径
     */
    public static void saveBitmap(Bitmap bitmap, String path, String name) {

        try {
            File dirFile = new File(path);
            if (!dirFile.exists()) {
                dirFile.mkdirs();
            }
            File file = new File(path, name);
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);

            fos.flush();
            fos.close();
        } catch (IOException e) {
            if (URLConstant.FORCE_LOG) {
                e.printStackTrace();
            }
            SLog.e("图片保存失败 ：" + path + name);
        }
    }

    public static void saveImageByUrl(final Context context, String url, final String path, final String name, final OnImageSavedListener listener) {
        Glide.with(context).load(url).asBitmap().toBytes().into(new SimpleTarget<byte[]>() {
            @Override
            public void onResourceReady(final byte[] resource, GlideAnimation<? super byte[]> glideAnimation) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        File dirFile = new File(path);
                        if (!dirFile.exists()) {
                            dirFile.mkdirs();
                        }
                        File file = new File(path, name);
                        try {
                            FileOutputStream out = new FileOutputStream(file);
                            out.write(resource);
                            out.flush();
                            out.close();
                            MediaStore.Images.Media.insertImage(context.getContentResolver(), path + name, name, "");
                            listener.onImageSavedSuccess(resource);
                        } catch (Exception e) {
                            e.printStackTrace();
                            listener.onImageSavedFailed();
                        }
                    }
                }).start();

            }
        });
    }

    public static byte[] loadImage(String path) {
        try {
            FileInputStream in = new FileInputStream(path);
            byte[] data = new byte[in.available()];
            in.read(data);
            in.close();

            return data;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "".getBytes();
    }

    public interface OnImageSavedListener {
        void onImageSavedSuccess(byte[] img);

        void onImageSavedSuccess(Bitmap img);

        void onImageSavedFailed();
    }

    /**
     * 压缩并保存到指定目录
     *
     * @param fromPath
     * @param maxSize
     * @param toPath
     * @param name
     */
    public static boolean compressAndSaveTo(String fromPath, int maxSize, String toPath, String name) {
        boolean result;
        try {
            Bitmap bitmap = getResizeBitmap(fromPath, 5000, 5000);

//            rotateBitmapByDegree(bitmap, 360 - getBitmapDegree(toPath + name));

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
//            SLog.e("ImageLoadUtil", "原始大小：" + baos.toByteArray().length);
            while (baos.toByteArray().length / 1024 > maxSize) {
                quality -= 10;
                baos.reset();
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
//                SLog.e("ImageLoadUtil", "过程中大小为：" + baos.toByteArray().length + "   quality:" + quality);
            }

            File dirFile = new File(toPath);
            if (!dirFile.exists()) {
                // 先创建文件夹
                dirFile.mkdirs();
            }
            File file = new File(toPath, name);
            FileOutputStream fos = new FileOutputStream(file);
            baos.writeTo(fos);

            fos.flush();
            fos.close();

            result = true;
        } catch (Exception e) {
            if (URLConstant.FORCE_LOG) {
                e.printStackTrace();
            }
            result = false;
            SLog.e("ImageLoadUtil", "图片压缩保存失败 ：" + toPath + name);
        }

        return result;
    }

    /**
     * 读取图片的旋转的角度
     *
     * @param path 图片绝对路径
     * @return 图片的旋转角度
     */
    public static int getBitmapDegree(String path) {
        int degree = 0;
        try {
            // 从指定路径下读取图片，并获取其EXIF信息
            ExifInterface exifInterface = new ExifInterface(path);
            // 获取图片的旋转信息
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            if (URLConstant.FORCE_LOG) {
                e.printStackTrace();
            }
        }
        SLog.i("ImageLoadUtil", "degree : " + degree);
        return degree;
    }

    /**
     * 将图片按照某个角度进行旋转
     *
     * @param bm     需要旋转的图片
     * @param degree 旋转角度
     * @return 旋转后的图片
     */
    public static Bitmap rotateBitmapByDegree(Bitmap bm, int degree) {
        Bitmap returnBm = null;

        // 根据旋转角度，生成旋转矩阵
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        try {
            // 将原始图片按照旋转矩阵进行旋转，并得到新的图片
            returnBm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
        } catch (OutOfMemoryError e) {
        }
        if (returnBm == null) {
            returnBm = bm;
        }
        if (bm != returnBm) {
            bm.recycle();
        }
        return returnBm;
    }

    private static Bitmap getResizeBitmap(String path) {
        Bitmap map = null;
        BitmapFactory.Options opts = null;
        try {
            File file = new File(path);
            if (file.exists()) {
                opts = new BitmapFactory.Options();
                opts.inPreferredConfig = Bitmap.Config.RGB_565;
                opts.inSampleSize = 2;
                Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(path), null, opts);
                // 计算图片缩放比例
                final int minSideLength = Math.min(bitmap.getWidth(), bitmap.getHeight());
                opts.inSampleSize = computeSampleSize(opts, minSideLength, bitmap.getWidth() * bitmap.getHeight());
                opts.inJustDecodeBounds = false;
                opts.inInputShareable = true;
                opts.inPurgeable = true;
                bitmap.recycle();
            }

            map = BitmapFactory.decodeStream(new FileInputStream(path), null, opts);
        } catch (Exception e) {
            if (URLConstant.FORCE_LOG) {
                e.printStackTrace();
            }
        }

        return map;
    }

    private static Bitmap getResizeBitmap(String path, int maxW, int maxH) {
        Bitmap map = null;
        try {
            File file = new File(path);
            if (file.exists()) {
                map = decodeFile(path);
                int width = map.getWidth();
                int height = map.getHeight();
                if (width > maxW || height > maxH) {
                    Matrix matrix = new Matrix();
                    float scaleW = (float) maxW / (float) width;
                    float scaleH = (float) maxH / (float) height;
                    matrix.postScale(Math.min(scaleW, scaleH), Math.min(scaleW, scaleH));
                    Bitmap resizeBitmap = Bitmap.createBitmap(map, 0, 0, width, height, matrix, true);
                    map.recycle();
                    return resizeBitmap;
                } else {
                    return map;
                }
            }
        } catch (Exception e) {
            if (URLConstant.FORCE_LOG) {
                e.printStackTrace();
            }
        }
        return map;
    }

    public static int computeSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(options, minSideLength, maxNumOfPixels);

        int roundedSize;
        if (initialSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }

        return roundedSize;
    }

    private static int computeInitialSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
        double w = options.outWidth;
        double h = options.outHeight;

        int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math
                .sqrt(w * h / maxNumOfPixels));
        int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(Math
                .floor(w / minSideLength), Math.floor(h / minSideLength));

        if (upperBound < lowerBound) {
            // return the larger one when there is no overlapping zone.
            return lowerBound;
        }

        if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
            return 1;
        } else if (minSideLength == -1) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }

    /**
     * 获取圆形图片
     */
    public static Bitmap createCircleImage(Bitmap source) {
        int length = source.getWidth() < source.getHeight() ? source.getWidth() : source.getHeight();
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        Bitmap target = Bitmap.createBitmap(length, length, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(target);
        canvas.drawCircle(length / 2, length / 2, length / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(source, 0, 0, paint);
        return target;
    }
}
