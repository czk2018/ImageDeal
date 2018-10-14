package com.jr.test.image;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.jr.R;

public class ShowActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);
    }

    //alpha 0 - 255
    public Bitmap getTranslateImage(Bitmap bitmap, int alpha) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        Bitmap bitmap2 = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        for (int i = 0; i < h; i++)
            for (int j = 0; j < w; j++) {
                int argb = bitmap.getPixel(j, i);
                int r = (argb >> 16) & 0xff;
                int g = (argb >> 8) & 0xff;
                int b = argb & 0xff;
                int a = (argb >> 24) & 0xff;
                System.out.println("a的值:" + a);
                int rgb = ((a * 256 + r) * 256 + g) * 256 + b;
                bitmap2.setPixel(j, i, rgb);
            }
        return bitmap2;
    }

    public Bitmap getTranslateImageByMv(Bitmap bitmap, int alpha) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        Bitmap bitmap2 = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        for (int i = 0; i < h; i++)
            for (int j = 0; j < w; j++) {
                int argb = bitmap.getPixel(j, i);
                int r = (argb >> 16) & 0xff;
                int g = (argb >> 8) & 0xff;
                int b = argb & 0xff;
                int a = (argb >> 24) & 0xff;

                int rgb = (a << 24) | (r << 16) | (g << 8) | b;
                bitmap2.setPixel(j, i, rgb);

            }
        return bitmap2;
    }
}
