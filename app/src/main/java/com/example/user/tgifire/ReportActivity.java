package com.example.user.tgifire;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.media.Image;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class ReportActivity extends AppCompatActivity implements View.OnClickListener{

    Button camera;
    ImageView resultImageView;

    File filePath;
    int reqWidth;
    int reqHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        TextView textView=(TextView)findViewById(R.id.titleName);
        Typeface typeface=Typeface.createFromAsset(getAssets(), "a아메리카노B.ttf");
        textView.setTypeface(typeface);
        TextView textView2=(TextView)findViewById(R.id.addressOption);
        Typeface typeface2=Typeface.createFromAsset(getAssets(), "a아메리카노M.ttf");
        textView2.setTypeface(typeface2);
        TextView textView3=(TextView)findViewById(R.id.contentOption);
        textView3.setTypeface(typeface2);
        TextView textView4=(TextView)findViewById(R.id.pictureOption);
        textView4.setTypeface(typeface2);

        camera = (Button)findViewById(R.id.camera);
        resultImageView = (ImageView)findViewById(R.id.resultImageView);

        camera.setOnClickListener(this);
        reqWidth = getResources().getDimensionPixelSize(R.dimen.request_image_width);
        reqHeight = getResources().getDimensionPixelSize(R.dimen.request_image_height);
    }

    @Override
    public void onClick(View v) {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED){
            try{
                String dirPath= Environment.getExternalStorageDirectory().getAbsolutePath()+"/myApp";
                File dir=new File(dirPath);
                if(!dir.exists())
                    dir.mkdir();
                filePath=File.createTempFile("IMG",".jpg", dir);
                if(!filePath.exists())
                    filePath.createNewFile();

                Uri photoURI= FileProvider.getUriForFile(ReportActivity.this, BuildConfig.APPLICATION_ID+".provider", filePath);
                Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(intent, 40);
            }catch (Exception e){
                e.printStackTrace();
            }
        }else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(filePath != null){
            BitmapFactory.Options options=new BitmapFactory.Options();
            options.inJustDecodeBounds=true;
            try{
                InputStream in=new FileInputStream(filePath);
                BitmapFactory.decodeStream(in, null, options);
                in.close();
                in=null;
            }catch (Exception e){
                e.printStackTrace();
            }
            final int height=options.outHeight;
            final int width=options.outWidth;
            int inSampleSize=1;
            if(height>reqHeight || width>reqWidth){
                final int heightRatio=Math.round((float)height/(float)reqHeight);
                final int widthtRatio=Math.round((float)width/(float)reqWidth);

                inSampleSize=heightRatio<widthtRatio ? heightRatio : widthtRatio;
            }

            BitmapFactory.Options imgOptions=new BitmapFactory.Options();
            imgOptions.inSampleSize=inSampleSize;
            Bitmap bitmap=BitmapFactory.decodeFile(filePath.getAbsolutePath(), imgOptions);
            resultImageView.setImageBitmap(bitmap);
        }
    }
}
