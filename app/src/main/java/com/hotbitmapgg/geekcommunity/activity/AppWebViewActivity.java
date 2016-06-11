package com.hotbitmapgg.geekcommunity.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.DownloadListener;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hotbitmapgg.geekcommunity.R;
import com.hotbitmapgg.geekcommunity.utils.StatusBarCompat;

public class AppWebViewActivity extends Activity implements DownloadListener
{

    private TextView title;

    private final Handler mHandler = new Handler();

    private ProgressBar progressBar;

    private WebView mWebView;

    private String url, mTitle;

    WebViewClientBase webViewClient = new WebViewClientBase();

    private String iconUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_web);

        StatusBarCompat.compat(this);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        url = bundle.getString("url");
        mTitle = bundle.getString("title");
        iconUrl = bundle.getString("picUrl");
        initTitle();
        initView();
        setupWebView();
    }

    private void initTitle()
    {

        View view = findViewById(R.id.web_top);
        ImageView mLeftBack = (ImageView) view.findViewById(R.id.left_btn);
        mLeftBack.setVisibility(View.VISIBLE);
        title = (TextView) view.findViewById(R.id.top_title);
        if (TextUtils.isEmpty(mTitle))
        {
            title.setText("详情");
        } else
        {
            title.setText(mTitle);
        }
        mLeftBack.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View v)
            {

                finish();
            }
        });
        ImageView mRightBtn = (ImageView) view.findViewById(R.id.right_btn);
        mRightBtn.setVisibility(View.VISIBLE);
        mRightBtn.setImageResource(R.drawable.action_button_transpond_pressed_light);
    }


    private void initView()
    {

        progressBar = (ProgressBar) findViewById(R.id.progressBar1);
        mWebView = (WebView) findViewById(R.id.webView1);
    }

    private void setupWebView()
    {

        final WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webSettings.setDomStorageEnabled(true);
        webSettings.setGeolocationEnabled(true);
        mWebView.getSettings().setRenderPriority(RenderPriority.HIGH);
        mWebView.getSettings().setBlockNetworkImage(true);
        mWebView.setWebViewClient(webViewClient);
        mWebView.requestFocus(View.FOCUS_DOWN);
        mWebView.setDownloadListener(this);
        mWebView.getSettings().setDefaultTextEncodingName("UTF-8");
        mWebView.setWebChromeClient(new WebChromeClient()
        {

            @Override
            public boolean onJsAlert(WebView view, String url, String message, final JsResult result)
            {

                AlertDialog.Builder b2 = new AlertDialog.Builder(AppWebViewActivity.this).setTitle(R.string.app_name).setMessage(message).setPositiveButton("确定", new AlertDialog.OnClickListener()
                {

                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {

                        result.confirm();
                    }
                });

                b2.setCancelable(false);
                b2.create();
                b2.show();
                return true;
            }
        });
        mWebView.loadUrl(url);
    }


    public class WebViewClientBase extends WebViewClient
    {

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon)
        {

            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url)
        {

            super.onPageFinished(view, url);
            progressBar.setVisibility(View.GONE);
            mWebView.getSettings().setBlockNetworkImage(false);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl)
        {

            super.onReceivedError(view, errorCode, description, failingUrl);
            String errorHtml = "<html><body><h2>找不到网页</h2></body></html>";
            view.loadDataWithBaseURL(null, errorHtml, "text/html", "UTF-8", null);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see android.webkit.DownloadListener#onDownloadStart(java.lang.String,
     * java.lang.String, java.lang.String, java.lang.String, long)
     */
    @Override
    public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength)
    {

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        try
        {
            startActivity(intent);
            return;
        } catch (ActivityNotFoundException e)
        {
            e.printStackTrace();
        }
    }

    public void initialize()
    {

        mHandler.post(new Runnable()
        {

            @Override
            public void run()
            {

                mWebView.loadUrl("javascript:initialize()");
            }
        });
    }
}
