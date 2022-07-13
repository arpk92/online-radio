package audio.radiostation.usaradiostations;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import audio.radiostation.usaradiostations.constant.Constant;

public class Policies extends AppCompatActivity {

    String url;
    WebView webview;
    ImageView back;
    ProgressBar progress;
    TextView tooltext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_PROGRESS);
        setContentView(R.layout.activity_privacy__policy);


        webview = findViewById(R.id.webview);
        tooltext = findViewById(R.id.tooltext);
        back = findViewById(R.id.back);
        progress = findViewById(R.id.progress);
        url = Constant.privacy_policy_url;

        tooltext.setText("Privacy Policy");

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onBackPressed();
            }
        });

        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setBuiltInZoomControls(false);
        webview.getSettings().setDomStorageEnabled(true);
        webview.getSettings().setSupportZoom(true);
        webview.getSettings().setBuiltInZoomControls(true);
        webview.getSettings().setDisplayZoomControls(false);
        webview.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);


        webview.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress1) {
                setTitle("Loading...");
                setProgress(progress1 * 100);
                progress.setProgress(progress1);
                Log.d("Loading", "" + progress1);
                if (progress1 == 100) {
                    progress.setVisibility(View.GONE);
                } else {
                    progress.setVisibility(View.VISIBLE);
                }
            }
        });
        webview.setWebViewClient(new myWebClient());
        webview.getSettings().setJavaScriptEnabled(true);
        webview.loadUrl(url);
    }

    public class myWebClient extends WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            int progres = view.getProgress();
            progress.setProgress(progres);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);

            int progres = view.getProgress();
            progress.setProgress(progres);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            int progres = view.getProgress();
            progress.setProgress(progres);
            progress.setVisibility(View.GONE);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            Toast.makeText(Policies.this, "no_internet_connection", Toast.LENGTH_LONG).show();
            view.loadUrl("about:blank");

        }

    }

    @Override
    public void onBackPressed() {
        if (webview.canGoBack()) {
            webview.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
