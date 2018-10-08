package com.jr.image;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.SyncStateContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.jr.R;
import com.jr.util.PictureUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class PhotoActivity extends AppCompatActivity {
    private static final String TAG = "PhotoActivity";

    private static final int REQUEST_IMAGE_GET = 0;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_SMALL_IMAGE_CUTTING = 2;

    private ImageView picture;
    private Uri imageUri;

    private static final String IMAGE_FILE_NAME = "user_head_icon.jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        picture = (ImageView) findViewById(R.id.id_picture);
    }

    public void onLoadCapture() {
        // 拍照及文件权限申请
        if (ContextCompat.checkSelfPermission(PhotoActivity.this,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(PhotoActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // 权限还没有授予，进行申请
            ActivityCompat.requestPermissions(PhotoActivity.this,
                    new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 300);
        } else {
            // 权限已经申请，直接拍照
            imageCapture();
        }
    }

    private void imageCapture() {
        Intent intent;
        Uri pictureUri;
        //getMyPetRootDirectory()得到的是Environment.getExternalStorageDirectory() + File.separator+"MyPet"
        //也就是我之前创建的存放头像的文件夹（目录）
        File pictureFile = new File(PictureUtil.getMyRootDirectory(), IMAGE_FILE_NAME);
        // 判断当前系统
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            //这一句非常重要
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            //""中的内容是随意的，但最好用package名.provider名的形式，清晰明了
            pictureUri = FileProvider.getUriForFile(this, "com.jr.fileprovider", pictureFile);
        } else {
            intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            pictureUri = Uri.fromFile(pictureFile);
        }
        // 去拍照,拍照的结果存到oictureUri对应的路径中
        intent.putExtra(MediaStore.EXTRA_OUTPUT, pictureUri);
        Log.e(TAG,"before take photo"+pictureUri.toString());
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
    }

    private void startPhotoZoom(Uri uri) {
        Log.d(TAG,"Uri = "+uri.toString());
        //保存裁剪后的图片
        File cropFile=new File(PictureUtil.getMyRootDirectory(),"crop.jpg");
        try{
            if(cropFile.exists()){
                cropFile.delete();
                Log.e(TAG,"delete");
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        Uri cropUri;
        cropUri = Uri.fromFile(cropFile);

        Intent intent = new Intent("com.android.camera.action.CROP");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //添加这一句表示对目标应用临时授权该Uri所代表的文件
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1); // 裁剪框比例
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 300); // 输出图片大小
        intent.putExtra("outputY", 300);
        intent.putExtra("scale", true);
        intent.putExtra("return-data", false);

        Log.e(TAG,"cropUri = "+cropUri.toString());

        intent.putExtra(MediaStore.EXTRA_OUTPUT, cropUri);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true); // no face detection
        startActivityForResult(intent, REQUEST_SMALL_IMAGE_CUTTING);
    }

    public void setPicToView(Uri uri)  {
        if (uri != null) {
            Bitmap photo = null;
            try {
                photo = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
            }catch (FileNotFoundException e){
                e.printStackTrace();
            }
            // 创建 smallIcon 文件夹
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                //String storage = Environment.getExternalStorageDirectory().getPath();
                File dirFile = new File(PictureUtil.getMyRootDirectory(),  "Icon");
                if (!dirFile.exists()) {
                    if (!dirFile.mkdirs()) {
                        Log.d(TAG, "in setPicToView->文件夹创建失败");
                    } else {
                        Log.d(TAG, "in setPicToView->文件夹创建成功");
                    }
                }
                File file = new File(dirFile, IMAGE_FILE_NAME);
                //InfoPrefs.setData(SyncStateContract.Constants.UserInfo.HEAD_IMAGE,file.getPath());
                // 保存图片
                FileOutputStream outputStream = null;
                try {
                    outputStream = new FileOutputStream(file);
                    photo.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                    outputStream.flush();
                    outputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            // 在视图中显示图片
        }
    }

    public void onLoadImage() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] { android.Manifest.permission.WRITE_EXTERNAL_STORAGE }, 200);
        } else {
            openAlbum();
        }
    }

    private void openAlbum() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_IMAGE_GET);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {

            // 切割
            case REQUEST_SMALL_IMAGE_CUTTING:
                Log.e(TAG,"before show");
                File cropFile=new File(PictureUtil.getMyRootDirectory(),"crop.jpg");
                Uri cropUri = Uri.fromFile(cropFile);
                setPicToView(cropUri);
                break;

            // 拍照
            case REQUEST_IMAGE_CAPTURE:
                if (resultCode == RESULT_OK) {
                    File pictureFile = new File(PictureUtil.getMyRootDirectory(), IMAGE_FILE_NAME);
                    Uri pictureUri;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        pictureUri = FileProvider.getUriForFile(this, "com.jr.fileprovider", pictureFile);
                        Log.e(TAG,"picURI="+pictureUri.toString());
                    } else {
                        pictureUri = Uri.fromFile(pictureFile);
                    }
                    startPhotoZoom(pictureUri);
                }
                break;

            // 相册选取
            case REQUEST_IMAGE_GET:
                if (resultCode == RESULT_OK) {
                    if (Build.VERSION.SDK_INT >= 19) {
                        handleImageOnKitKat(data);
                    } else {
                        handleImageBeforeKitKat(data);
                    }
                }
                break;

            default:
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openAlbum();
                } else {
                    Toast.makeText(this, "You denied the permission", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    @TargetApi(19)
    private void handleImageOnKitKat(Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
        if (DocumentsContract.isDocumentUri(this, uri)) {
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1];
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.androdi.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://download/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            imagePath = getImagePath(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            imagePath = uri.getPath();
        }
        displayImage(imagePath);
    }

    private void handleImageBeforeKitKat(Intent data) {
        Uri uri = data.getData();
        String imagePath = getImagePath(uri, null);
        displayImage(imagePath);
    }

    private String getImagePath(Uri uri, String selection) {
        String path = null;
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    private Bitmap mBitmap;

    private void displayImage(String imagePath) {
        if (imagePath != null) {
            //mBitmap = getTranslateImageByMv(BitmapFactory.decodeFile(imagePath), 0);
            mBitmap = BitmapFactory.decodeFile(imagePath);
            picture.setImageBitmap(mBitmap);
        } else {
            Toast.makeText(this, "failed to get image", Toast.LENGTH_SHORT).show();
        }
    }

    public Bitmap getTranslateImageByMv(Bitmap bitmap, int alpha) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        Bitmap bitmap2 = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        for (int i = 0; i < h; i++) {
            if (i % 2 == 0) continue;
            for (int j = 0; j < w; j++) {
                if (j % 2 == 0) continue;

                int argb = bitmap.getPixel(j, i);
                int r = (argb >> 16) & 0xff;
                int g = (argb >> 8) & 0xff;
                int b = argb & 0xff;
                int a = (argb >> 24) & 0xff;

                int rgb = (a << 24) | (r << 16) | (g << 8) | b;
                bitmap2.setPixel(j, i, rgb);

            }
        }
        return bitmap2;
    }

    public void onTrans() {
        Bitmap bitmap = translateImage();
        picture.setImageBitmap(bitmap);
    }

    public Bitmap translateImage() {
        int pWidth = picture.getWidth();
        int pHeight = picture.getHeight();

        if (pWidth > pHeight) {
            pWidth = pHeight;
        }

        int w = mBitmap.getWidth();
        int h = mBitmap.getHeight();
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);

        int led = 32;// led的数量
        int space = 5;// 中间空的led的数量
        int fb = 180;// 圆圈的等分数量

        int total_space = led + space;

        int r = pWidth / 2;// 半径
        int y_base = (pHeight - pWidth) / 2;

        double x_space = 1.0 * r / total_space;//小圆的直径
        double per_hudu = 1.0 * 360 / fb;//平均角度

        for (int i = 0; i < fb; i++) {
            for (int j = 0; j < total_space; j++) {
                if (j < space) {
                    continue;
                }

                double tmp_r = x_space * j;

                double c = Math.cos(Math.PI * per_hudu * i / 180);
                double s = Math.sin(Math.PI * per_hudu * i / 180);
                int x = (int) (tmp_r * c + r);
                int y = (int) (r - tmp_r * s) + y_base;

                //Log.i("czk", "add(new Point(" + x + ", " + y + "));");

                if (x > 0 && x < mBitmap.getWidth() && y > 0 && y < mBitmap.getHeight())
                {
                    int argb = mBitmap.getPixel((int)x, (int)y);

                    int color_r = (argb >> 16) & 0xff;
                    int color_g = (argb >> 8) & 0xff;
                    int color_b = argb & 0xff;
                    int color_a = (argb >> 24) & 0xff;

                    int rgb = (color_a << 24) | (color_r << 16) | (color_g << 8) | color_b;
                    bitmap.setPixel((int)x, (int)y, rgb);
                }
            }
        }

        return bitmap;
    }


    //重写onCreateOptionMenu(Menu menu)方法，当菜单第一次被加载时调用
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //填充选项菜单（读取XML文件、解析、加载到Menu组件上）
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    //重写OptionsItemSelected(MenuItem item)来响应菜单项(MenuItem)的点击事件（根据id来区分是哪个item）
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.id_src_capture:
                onLoadCapture();
                break;
            case R.id.id_src_image:
                onLoadImage();
                break;
            case R.id.id_src_gif:
                break;
            case R.id.id_translate:
                onTrans();
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
