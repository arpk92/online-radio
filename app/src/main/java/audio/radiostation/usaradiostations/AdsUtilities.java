package audio.radiostation.usaradiostations;

import android.app.Activity;
import android.content.Context;

import com.google.android.gms.ads.MobileAds;
import com.huawei.hms.ads.AdParam;
import com.huawei.hms.ads.InterstitialAd;

public class AdsUtilities {

    public InterstitialAd mInterstitialAd;
    public static final int CLICK_COUNT = 2;
    public static AdsUtilities mAdsUtilities;


    private boolean mDisableInterstitialAd = false;

    public static int mClickCount = 0;

    public AdsUtilities(Context context) {
      //  MobileAds.initialize(context, context.getResources().getString(R.string.video_ad_id));
    }
    public static AdsUtilities CLICK_COUNT(Context mContext) {

        if (mAdsUtilities == null) {
            mAdsUtilities = new AdsUtilities(mContext);
        }
        return mAdsUtilities;
    }


    public void loadFullScreenAd(Activity activity) {
        if (!mDisableInterstitialAd) {
            mClickCount++;
            if (mClickCount >=CLICK_COUNT) {
                mInterstitialAd = new InterstitialAd(activity);
                mInterstitialAd.setAdId(activity.getResources().getString(R.string.video_ad_id));


                AdParam adParam = new AdParam.Builder().build();
                mInterstitialAd.loadAd(adParam);



            }
        }
    }

    // Finish
    public boolean showFullScreenAd() {
        if (!mDisableInterstitialAd) {
            if (mInterstitialAd != null) {
                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                    mClickCount = 0;
                    return true;
                }
            }
        }
        return false;
    }
    public void disableInterstitialAd() {
        this.mDisableInterstitialAd = true;
    }
}
