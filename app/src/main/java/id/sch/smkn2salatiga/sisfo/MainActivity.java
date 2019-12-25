package id.sch.smkn2salatiga.sisfo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.http.SslError;
import android.net.wifi.hotspot2.pps.HomeSp;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
implements View.OnClickListener{

    private final static String HOME_URL = "http://725207295a31.sn.mynetname.net:801/sikadu/aplikasi";

    private WebView webClient;
    private ImageView btnGoBack, btnGoForward, btnRefresh, btnClose;
    private ProgressBar loading;
    private TextView titleBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        setUpWebView();
        setListeners();
    }

    private void setListeners() {
        btnGoBack.setOnClickListener(this);
        btnGoForward.setOnClickListener(this);
        btnRefresh.setOnClickListener(this);
        btnClose.setOnClickListener(this);
    }

    private void setUpWebView() {
        webClient.setWebViewClient(new MyWebViewClient());
        webClient.getSettings().setJavaScriptEnabled(true);
        loadWebViewUrl(HOME_URL);
    }

    private void loadWebViewUrl(String url) {
        if(isInternetConnected()){
            if(loading.isShown())loading.setVisibility(View.VISIBLE);
            btnRefresh.setVisibility(View.GONE);
            webClient.loadUrl(url);
        }else{
            btnRefresh.setVisibility(View.VISIBLE);
            Toast.makeText(this,"Oops!! There is no internet connection. Please enable your internet connection.", Toast.LENGTH_LONG).show();
        }
    }

    private boolean isInternetConnected() {
        // At activity startup we manually check the internet status and change
        // the text status
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }

    private void initViews() {
        webClient = findViewById(R.id.webContainer);
        btnGoForward = findViewById(R.id.btnGoForward);
        btnGoBack = findViewById(R.id.btnGoBack);
        btnRefresh = findViewById(R.id.btnRefresh);
        titleBar = findViewById(R.id.titleBar);
        loading = findViewById(R.id.loading);
        btnClose = findViewById(R.id.btnClose);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        isWebViewCanGoBack();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            isWebViewCanGoBack();
            return true;
        }else{
            return super.onKeyDown(keyCode, event);
        }
    }

    private void isWebViewCanGoBack(){
        if(webClient.canGoBack()){
            webClient.goBack();
        }else{
            finish();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnGoBack:
                isWebViewCanGoBack();
                break;
            case R.id.btnGoForward:
                if(webClient.canGoForward()) webClient.goForward();
                break;
            case R.id.btnRefresh:
                String url = webClient.getUrl();
                loadWebViewUrl(url);
                break;
            case R.id.btnClose:
                finish();
                break;
            default: break;
        }
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            btnRefresh.setVisibility(View.GONE);
            if(!loading.isShown()){
                loading.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            btnRefresh.setVisibility(View.VISIBLE);
            if(loading.isShown()){
                loading.setVisibility(View.GONE);
            }
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
            btnRefresh.setVisibility(View.VISIBLE);
            if (loading.isShown()) loading.setVisibility(View.GONE);
            Toast.makeText(MainActivity.this, "Unexpected error occurred.Reload page again.", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
            super.onReceivedHttpError(view, request, errorResponse);
            btnRefresh.setVisibility(View.VISIBLE);
            if(loading.isShown()) loading.setVisibility(View.GONE);
            Toast.makeText(MainActivity.this, "Unexpected error occurred.Reload page again.", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            super.onReceivedSslError(view, handler, error);
            btnRefresh.setVisibility(View.VISIBLE);
            if(loading.isShown()) loading.setVisibility(View.GONE);
            Toast.makeText(MainActivity.this, "Unexpected SSL error occurred.Reload page again.", Toast.LENGTH_SHORT).show();
        }
    }
}
