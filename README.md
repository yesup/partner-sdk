# Yesup Partner Sdk for Android
###### The Latest SDK Version: 1.2.5
Yesup Partner SDK for Android is the easiest way to integrate your Android app with Yesup.<br/>
#####&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[1 Install Yesup AD SDK](#step1)<br/>
#####&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[2 Set your custom information](#step2)<br/>
#####&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[3 Use OfferWall](#step3)<br/>
#####&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[4 Use Intersitial](#step4)<br/>
#####&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[5 Use Banner](#step5)<br/>
Learn more about the provided sample at https://github.com/yesup/house-reminder<br/>
<hr/>

<div id="step1"></div>
### **1** Install Yesup AD SDK<br/>
  a. Download yesup partner config file "adconfigure.xml".<br/>
  b. Copy adconfigure.xml file to your "res/xml/adconfigure.xml" directory.<br/>
     Note: Do not modify this file name!!!<br/>
  c. In Android Studio, open the build.gradle file which is in your project's root directory,
     make sure that you have used the JCenter like below:<br/>
```python
     allprojects {
         repositories {
             jcenter()
         }
     }
```
  d. In Android Studio, open the app's build.gradle file in the editor.<br/>
     Add a "dependencies" source, as follows:<br/>
```python
     dependencies {
         compile 'com.yesup.partner:yesuppartner:1.2.5'
     }
```
  Now the classes and methods in the Yesup Partner Library can be used in your app.<br/><br/>

<div id="step2"></div>
### **2** Set your custom information<br/>
  a. Import package.<br/>
```python
    import com.yesup.partner.YesupAd;
```
  b. Set custom information like below:<br/>
```python
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String subId = "123123";  // optional, you app user id
        String optValue1 = "";    // optional, additional event value you want to keep track
        String optValue2 = "";    // optional, additional event value you want to keep track
        String optValue3 = "";    // optional, additional event value you want to keep track
        YesupAd.setSubId(subId);
        YesupAd.setOption(optValue1, optValue2, optValue3);
    }
```

<div id="step3"></div>
### **3** Use OfferWall<br/>
  a. Add in the following line to your AndroidManifest.xml to declare the Activity:<br/>
     You can modify the android:label, this property will display on the title of activity.<br/>
```python
    <activity android:name="com.yesup.ad.offerwall.OfferWallActivity" android:label="Offer Wall" />
```
  b. new and set a OfferWallHelper object, you can control the view of rewards button(rewards and icon).<br/>
  c. Write code below to use OfferWall:<br/>
```java
import com.yesup.partner.YesupOfferWall;

public class MainActivity extends AppCompatActivity {
    private YesupOfferWall offerwallAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        offerwallAd = new YesupOfferWall(this);
        offerwallAd.setOfferWallPartnerHelper(new OfferWallHelper(this));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (null != offerwallAd) {
            offerwallAd.onResume();
        }
    }

    public class OfferWallHelper extends OfferWallPartnerHelper {
        public OfferWallHelper(Context context) {
            super(context);
        }

        @Override
        public String calculateReward(int payout, int incentRate) {
            String result = "0";
            double reward = (double)payout * (double)incentRate / 100000.0D;
            if(0.0D == reward) {
                result = "";
            } else {
                result = (new DecimalFormat("#.##")).format(reward);
            }

            return result;
        }

        @Override
        public Drawable getRewardIcon() {
            Drawable drawable = context.getResources().getDrawable(R.drawable.coins);
            return drawable;
        }
    }
}
```
  c. Use code "offerwallAd.showDefaultOfferWall();" to start an OfferWall activity.


<div id="step4"></div>
### **4** Use Intersitial<br/>
  a. Import package.<br/>
```java
import com.yesup.partner.YesupInterstitial;
```
  b. In order to use Interstitial AD, you must implement IInterstitialListener interface in your activity which need to call the interstitial.<br/>
  c. In onCreate method, new an YesupAd and MyStatusView instance, MyStatusView object can control the view that below the intersitial ad.<br/>
  c. Example code:<br/>
```java
public class MainActivity extends AppCompatActivity
        implements IInterstitialListener {
    private YesupInterstitial interstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        interstitialAd = new YesupInterstitial(this);
        partnerView = new InterstitialPartnerView(this);
    }
    @Override
    public void onInterstitialShown() {
        Log.i(TAG, "On Interstitial Shown");
    }

    @Override
    public void onInterstitialCredited() {
        Log.i(TAG, "On Interstitial Credited");
    }

    @Override
    public void onInterstitialClosed() {
        Log.i(TAG, "On Interstitial Closed");
    }

    @Override
    public void onInterstitialError() {
        Log.i(TAG, "On Interstitial Error");
        interstitialAd.closeNow();
    }

    public void startInterstitial() {
        interstitialAd.showDefaultInterstitial(false, true, partnerView);
    }
}
```

<div id="step5"></div>
### **5** Use Banner<br/>
  a. Add Banner in your layout file.<br/>
```java
<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    xmlns:yesupad="http://schemas.android.com/apk/res-auto/com.yesup.partner">

    <com.yesup.ad.banner.BannerView
        android:layout_alignParentBottom="true"
        android:layout_width="320dp"
        android:layout_height="50dp"
        android:layout_centerHorizontal="true"
        yesupad:yesup_banner_zone_id="72586"
        android:id="@+id/yesupBannerAdBottom" />
</RelativeLayout>
```
  b. Add code to show Banner ad.<br/>
```java
public class MainActivity extends AppCompatActivity {
    private BannerView bannerAdBottom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        bannerAdBottom = (BannerView)findViewById(R.id.yesupBannerAdBottom);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (null != bannerAdBottom) {
            bannerAdBottom.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (null != bannerAdBottom) {
            bannerAdBottom.onPause();
        }
    }
}
```
