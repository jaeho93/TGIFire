package com.example.user.tgifire;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.util.Linkify;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReportActivity extends AppCompatActivity implements View.OnClickListener {

    Button camera;
    Button gallery;
    Button submit;

    TextView reportWeb;
    EditText address;
    EditText content;

    ImageView resultImageView;
    TextView resultView;

    File filePath;
    int reqWidth;
    int reqHeight;

    String current_address;

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
        gallery = (Button)findViewById(R.id.gallery);
        submit = (Button)findViewById(R.id.submit);

        reportWeb = (TextView)findViewById(R.id.linkToReport);
        reportWeb.setTypeface(typeface2);
        address = (EditText)findViewById(R.id.addressEdit);
        content = (EditText)findViewById(R.id.contentEdit);

        resultImageView = (ImageView)findViewById(R.id.resultImageView);
        resultView=(TextView)findViewById(R.id.resultView);

        Intent addressIntent = new Intent(this.getIntent());
        current_address = addressIntent.getStringExtra("address");
        address.setText(current_address);


        camera.setOnClickListener(this);
        gallery.setOnClickListener(this);
        submit.setOnClickListener(this);
        reqWidth = getResources().getDimensionPixelSize(R.dimen.request_image_width);
        reqHeight = getResources().getDimensionPixelSize(R.dimen.request_image_height);

        Linkify.TransformFilter mTransform = new Linkify.TransformFilter() {
            @Override
            public String transformUrl(Matcher matcher, String s) {
                return "http://www.119.go.kr/Center119/regist.do?certify=R";
            }
        };

        Pattern pattern = Pattern.compile("웹페이지에서 신고하기");
        Linkify.addLinks(reportWeb, pattern, "", null, mTransform);
    }

    private void showToast(String message){
        Toast toast=Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    public void onClick(View v) {
        if(v == camera) {
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
        }else if(v == gallery) {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
            intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, 20);
        }else if(v == submit) {
            if (address.getText().toString().length() != 0 && content.getText().toString().length() != 0) {
                showToast("신고가 정상적으로 접수되었습니다.");
                Intent intent = new Intent(getApplicationContext(), WebViewActivity.class);
                BitmapDrawable d = (BitmapDrawable)((ImageView) findViewById(R.id.resultImageView)).getDrawable();
                Bitmap b = d.getBitmap();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                b.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                intent.putExtra("image", byteArray);
                intent.putExtra("address", address.getText().toString());
                intent.putExtra("content", content.getText().toString());
                startActivity(intent); }
            else
                showToast("빠진 양식이 없는지 확인해주십시오.");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 20 && resultCode == RESULT_OK) {
            String[] projection={MediaStore.Images.Media.DATA};
            Cursor cursor=getContentResolver().query(data.getData(), projection, null, null, null);
            cursor.moveToFirst();
            String filePath=cursor.getString(0);
            insertImageView(filePath);
        } else if (requestCode == 40 && resultCode == RESULT_OK) {
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

    private void insertImageView(String filePath) {
        if (!filePath.equals("")) {
            File file = new File(filePath);

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            try {
                InputStream in = new FileInputStream(filePath);
                BitmapFactory.decodeStream(in, null, options);
                in.close();
                in = null;
            } catch (Exception e) {
                e.printStackTrace();
            }

            final int width = options.outWidth;
            int inSampleSize = 1;

            if(width>reqWidth){
                int widthRatio=Math.round((float)width / (float)reqWidth);
                inSampleSize=widthRatio;
            }

            BitmapFactory.Options imgOptions=new BitmapFactory.Options();
            imgOptions.inSampleSize=inSampleSize;
            Bitmap bitmap=BitmapFactory.decodeFile(filePath, imgOptions);
            resultImageView.setImageBitmap(bitmap);
        }
    }

    private String getFilePathFromUriSegment(Uri uri){
        String selection=MediaStore.Images.Media._ID+"=?";
        String[] selectionArgs=new String[]{uri.getLastPathSegment()};

        String column="_data";
        String[] projection={column};

        Cursor cursor=getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, selection, selectionArgs, null);
        String filePath=null;
        if(cursor != null && cursor.moveToFirst()){
            int column_index=cursor.getColumnIndexOrThrow(column);
            filePath=cursor.getString(column_index);
        }
        cursor.close();
        return filePath;
    }
}
