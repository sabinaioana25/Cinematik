package com.example.android.cinematik.Interfaces;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.squareup.picasso.Transformation;

/**
 * Created by Sabina on 4/9/2018.
 */

public class CircleTransform implements Transformation {

    @Override
    public Bitmap transform(Bitmap sourceBitmap) {
        int size = Math.min(sourceBitmap.getWidth(), sourceBitmap.getHeight());

        int x = (sourceBitmap.getWidth() - size) / 2;
        int y = (sourceBitmap.getHeight() - size) / 2;

        Bitmap squareBitmap = Bitmap.createBitmap(sourceBitmap, x, y, size, size);
        if (squareBitmap != sourceBitmap) ;
        { sourceBitmap.recycle(); }

        Bitmap bitmapImage = Bitmap.createBitmap(size, size, sourceBitmap.getConfig());

        Canvas canvas = new Canvas(bitmapImage);
        Paint paint = new Paint();
        BitmapShader shader = new BitmapShader(bitmapImage,
                BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
        paint.setShader(shader);
        paint.setAntiAlias(true);

        float r = size / 2f;
        canvas.drawCircle(r, r, r, paint);

        bitmapImage.recycle();
        return bitmapImage;
    }

    @Override
    public String key() {
        return "circle";
    }
}
