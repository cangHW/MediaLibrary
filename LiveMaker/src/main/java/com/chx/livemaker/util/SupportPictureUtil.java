package com.chx.livemaker.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 兼容类，负责保存图片
 * Created by cangHX
 * on 2019/01/09  9:59
 */
public class SupportPictureUtil {

    public static boolean savePicture(byte[] data, int rotation, String path) {
        if (data.length == 0) {
            return false;
        }
        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
        if (bitmap == null) {
            return false;
        }
        Bitmap newBitmap = null;
        try {
            Matrix matrix = new Matrix();
            if (rotation == 90 || rotation == 270) {
                newBitmap = Bitmap.createBitmap(bitmap.getHeight(), bitmap.getWidth(), bitmap.getConfig());
                matrix.postTranslate(-bitmap.getWidth() / 2, -bitmap.getHeight() / 2);
                matrix.postRotate(rotation);
                matrix.postTranslate(bitmap.getHeight() / 2, bitmap.getWidth() / 2);
            } else if (rotation == 180) {
                newBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());
                matrix.postTranslate(-bitmap.getWidth() / 2, -bitmap.getHeight() / 2);
                matrix.postRotate(rotation);
                matrix.postTranslate(bitmap.getWidth() / 2, bitmap.getHeight() / 2);
            }
            BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(new File(path)));
            if (newBitmap == null) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                outputStream.flush();
                outputStream.close();
                return true;
            }
            Canvas canvas = new Canvas(newBitmap);
            Paint paint = new Paint();
            canvas.drawBitmap(bitmap, matrix, paint);
            newBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            bitmap.recycle();
            if (newBitmap != null) {
                newBitmap.recycle();
            }
        }
        return false;
    }

}
