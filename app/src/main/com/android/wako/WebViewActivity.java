package com.android.wako;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.android.wako.common.Constants;
import com.android.wako.util.LogUtil;
import com.android.wako.util.StringUtil;

/**
 * webview
 * Created by duanmulirui
 */
public class WebViewActivity extends CommonTitleActivity{
    private static final String TAG = "WebViewActivity";

    private WebSettings webSettings = null;
    private WebView webView = null;
    String mBaseUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web_main);
        initViews();

        setTitle(getIntent().getStringExtra("title"));
        mBaseUrl = getIntent().getStringExtra("url");
        if (StringUtil.isEmpty(mBaseUrl))
            finish();

        final StringBuilder url = new StringBuilder(mBaseUrl);
        if (mBaseUrl.contains("?")) {
            url.append("&ver=").append(Constants.VERSION_NAME)
                    .append("&platform=").append(Constants.appID);
        } else {
            url.append("?ver=").append(Constants.VERSION_NAME)
                    .append("&platform=").append(Constants.appID);
        }

        LogUtil.d(TAG, "url: " + url.toString());
        setWebViewAttribute();
        WebChromeClient wvcc = new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
//                LogUtil.d(TAG, "title: " + title + ", url: " + url.toString());
//                setTitle(title);
            }

        };
        // 设置setWebChromeClient对象
        webView.setWebChromeClient(wvcc);
        webView.loadUrl(url.toString());
    }

    // 设置webView相关
    protected void setWebViewAttribute() {
        // 设置支持JavaScript脚本
        webSettings = webView.getSettings();
        // 设置支持缩放
        webSettings.setUseWideViewPort(true);
        webSettings.setBuiltInZoomControls(false);
        webSettings.setUseWideViewPort(true);
        webSettings.setSupportZoom(true);
        webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        webSettings
                .setPluginState(android.webkit.WebSettings.PluginState.ON_DEMAND);
        webSettings.setDomStorageEnabled(false);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webSettings.setAppCacheEnabled(true);
        webSettings.setJavaScriptEnabled(true);
        webView.setVisibility(View.VISIBLE);
        webView.setVerticalScrollBarEnabled(true);
        webView.setHorizontalScrollBarEnabled(true);
        webView.setWebViewClient(loadUrlWebViewClient);
        webView.setWebChromeClient(loadUrlWebViewChromeClient);
    }

    private void initViews() {
        loadTitleViews();
        setLeftVisibily(View.VISIBLE);

        webView = (WebView) findViewById(R.id.web_view);
    }

    // webview监听
    private WebViewClient loadUrlWebViewClient = new WebViewClient() {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            LogUtil.d(TAG, "---shouldOverrideUrlLoading---url=" + url);
//            if (url.startsWith("tel:")) {// Intent.ACTION_DIAL有些手机不支持打电话
//                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
//                startActivity(intent);
//                return true;
//            }

            return super.shouldOverrideUrlLoading(view, url);
        }
    };

    // 滚动条监听
    private WebChromeClient loadUrlWebViewChromeClient = new WebChromeClient() {

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);

        }

        @Override
        public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
            // TODO Auto-generated method stub
            LogUtil.d(TAG,
                    consoleMessage.message() + " -- From line "
                            + consoleMessage.lineNumber() + " of "
                            + consoleMessage.sourceId());
            return true;
        }

    };

    @Override
    protected void onResume() {
        super.onResume();
        LogUtil.e(TAG, "WebView  onResume");
        webView.resumeTimers();
        webView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        LogUtil.e(TAG, "WebView  onPause");
        webView.pauseTimers();
        webView.onPause();
        try {
            webView.getClass().getMethod("onPause")
                    .invoke(webView, (Object[]) null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (isFinishing()) {
            webView.loadUrl("about:blank");
        }
    }

    @Override
    protected void onDestroy() {
        LogUtil.e(TAG, "WebView  onDestroy");
        if (webView != null) {
            ViewGroup parent = (ViewGroup) webView.getParent();
            if (parent != null) {
                parent.removeView(webView);
            }
            webView.removeAllViews();
            webView.destroy();
        }
        super.onDestroy();
    }

}
