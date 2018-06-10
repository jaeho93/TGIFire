package com.example.user.tgifire;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

public class WebViewActivity extends AppCompatActivity {
    private static final String ENTRY_URL = "file:///android_asset/www/index.html";

    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        mWebView = (WebView)findViewById(R.id.idwebview);

        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        Intent intent = getIntent();
        final String data1 = intent.getStringExtra("address");
        final String data2 = intent.getStringExtra("content");
        byte[] arr = intent.getByteArrayExtra("image");

        Bitmap bimage = BitmapFactory.decodeByteArray(arr, 0, arr.length);
        ImageView myimage = (ImageView)findViewById(R.id.idimage);
        myimage.setImageBitmap(bimage);

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                if (url.equals(ENTRY_URL)) {
                    String keyword = data1;
                    String keyword2 = data2;

                    String script = "javascript:function afterLoad() {"
                            + "document.getElementById('keyword').value = '" + keyword + "';"
                            + "};"
                            + "afterLoad();";
                    String script2 = "javascript:function afterLoad() {"
                            + "document.getElementById('keyword2').value = '" + keyword2 + "';"
                            + "};"
                            + "afterLoad();";

                    view.loadUrl(script);
                    view.loadUrl(script2);
                }
            }
        });

        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
                new AlertDialog.Builder(view.getContext())
                        .setTitle("Alert")
                        .setMessage(message)
                        .setPositiveButton(android.R.string.ok,
                                new AlertDialog.OnClickListener(){
                                    public void onClick(DialogInterface dialog, int which) {
                                        result.confirm();
                                    }
                                })
                        .setCancelable(false)
                        .create()
                        .show();
                return true;
            }

            @Override
            public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {
                new AlertDialog.Builder(view.getContext())
                        .setTitle("Confirm")
                        .setMessage(message)
                        .setPositiveButton("Yes",
                                new AlertDialog.OnClickListener(){
                                    public void onClick(DialogInterface dialog, int which) {
                                        result.confirm();
                                    }
                                })
                        .setNegativeButton("No",
                                new AlertDialog.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        result.cancel();
                                    }
                                })
                        .setCancelable(false)
                        .create()
                        .show();
                return true;
            }
        });

        mWebView.loadUrl(ENTRY_URL);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mWebView.canGoBack()) {
                mWebView.goBack();
                return false;
            }
        }

        return super.onKeyDown(keyCode, event);
    }
}
