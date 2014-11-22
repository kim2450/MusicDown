package kr.co.musicdown.app;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.tnkfactory.ad.TnkSession;


public class MainActivity extends Activity {
    private static final String AD_UNIT_ID = "ca-app-pub-8088027213352034/8537571907";
    WebView webview;
    Button btnSearch;
    Button btnLogin;

    EditText edtSearch;
    private com.google.android.gms.ads.AdView adView;

    String LOG_TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        TnkSession.prepareInterstitialAd(this, TnkSession.CPC);
        TnkSession.showInterstitialAd(this);

        btnSearch = (Button) findViewById(R.id.btn_search);
        btnLogin = (Button) findViewById(R.id.btn_login);
        edtSearch = (EditText) findViewById(R.id.edt_search);

        webview = (WebView) findViewById(R.id.webView);

        webview.getSettings().setJavaScriptEnabled(true);


        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
/*                .addTestDevice(deviceid)*/
                .build();

        // Create an ad.
        adView = new com.google.android.gms.ads.AdView(this);
        adView.setAdSize(AdSize.BANNER);
        adView.setAdUnitId(AD_UNIT_ID);

        // Add the AdView to the view hierarchy. The view will have no size
        // until the ad is loaded.
        FrameLayout frame = (FrameLayout) findViewById(R.id.frame);
        frame.addView(adView);

        // Start loading the ad in the background.
        adView.loadAd(adRequest);

        edtSearch.setOnKeyListener(new View.OnKeyListener() {

            @Override
            public boolean onKey(View view, int keyCode, KeyEvent event) {

                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    Search();
                }
                return false;
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://4shared.com/web/login"));
                startActivity(intent);
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Search();
            }
        });

        webview.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageFinished(WebView view, String url) {
                //Log.d(LOG_TAG, "onPageFinished-" + url);
                if (url.contains("http://")) {
                    url = url.replace("http://", "https://");
                    view.loadUrl(url);
                }
                super.onPageFinished(view, url);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                //Log.d(LOG_TAG, "onPageStarted-" + url);
                if (url.contains("http://")) {
                    url = url.replace("http://", "https://");
                    view.loadUrl(url);
                }
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.contains("http://")) {
                    url = url.replace("http://", "https://");
                    if (url.contains(".mp3?") || url.contains(".wav?")) {
                        // view.loadUrl(url);
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(url));
                        startActivity(intent);

                        webview.clearHistory();
                        webview.loadUrl("");

                    } else {
                        view.loadUrl(url);
                    }

                    return false;
                } else
                    return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onReceivedSslError(WebView view,
                                           SslErrorHandler handler, SslError error) {
                handler.proceed();
                super.onReceivedSslError(view, handler, error);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode,
                                        String description, String failingUrl) {
                try {

                    view.loadDataWithBaseURL(null, description, "text/plain",
                            "utf-8", null);
                } catch (Exception e) {
                    Log.e(LOG_TAG, e.toString());
                }
            }
        });

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    if (webview.canGoBack()) {
                        webview.goBack();

                    }
                    else{
                        finish();
                    }
                    return true;
            }

        }
        return super.onKeyDown(keyCode, event);
    }

    private void Search() {
        String search = edtSearch.getText().toString();

        if (search.length() == 0) {
            return;
        }
        else {
            webview.loadUrl("http://www.google.com/search?hl=en&client=ms-android-skt-kr&tbo=d&source=hp&site=webhp&q=site:4shared.com%20"
                    + search);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        if (adView != null) {
            adView.resume();
        }
    }

    @Override
    public void onPause() {
        if (adView != null) {
            adView.pause();
        }
        super.onPause();
    }

    //** Called before the activity is destroyed. *//*
    @Override
    public void onDestroy() {
        // Destroy the AdView.
        if (adView != null) {
            adView.destroy();
        }

        super.onDestroy();
    }
}
