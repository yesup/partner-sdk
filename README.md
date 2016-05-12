# partner-sdk
###### The Lastest SDK Version: 1.1.4
Yesup Partner SDK for Android is the easiest way to integrate your Android app with Yesup.
## Installation
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[Step1: Download SDK adconfigure.xml](#step1)<br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[Step2: Copy adconfigure.xml](#step2)<br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[Step3: Open the project's build.gradle file](#step3)<br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[step4: Make sure that you have used the JCenter" section](#step4)<br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[step5: Add content in dependencies section](#step5)<br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[step6: Add content in AndroidManifest.xml](#step6)<br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[step7: Startup Yesup OfferWall](#step7)<br/>
<hr/>

<div id="step1"></div>
##### **Step 1** Download yesup partner config file "adconfigure.xml".<br/><br/>
![alt text](https://github.com/yesup/partner-sdk/raw/master/src/img/sdk-user-guide5.png "step1")<br/><br/><br/>

<div id="step2"></div>
#####**Step 2** Copy **_"adconfigure.xml"_** file to your "res/xml/adconfigure.xml" directory.
**_Note: Do not modify this file name!!!_**<br/><br/>
![alt text](https://github.com/yesup/partner-sdk/raw/master/src/img/sdk-user-guide7.png "step2")<br/><br/><br/>

<div id="step3"></div>
##### **Step 3** In **Android Studio**, open the project's _"build.gradle"_ file in the editor.<br/><br/>
![alt text](https://github.com/yesup/partner-sdk/raw/master/src/img/sdk-user-guide3.png "step3")<br/><br/><br/>

<div id="step4"></div>
##### **step 4** Make sure that you have used the JCenter, as follows:
```python
allprojects {
    repositories {
        jcenter()
    }
}
```
![alt text](https://github.com/yesup/partner-sdk/raw/master/src/img/sdk-user-guide2.png "step4")<br/><br/><br/>
<div id="step5"></div>
##### **Step 5** Add in the following line to the dependencies section.

```python
dependencies {
    compile 'com.yesup.partner:yesuppartner:1.1.4'
}
```
![alt text](https://github.com/yesup/partner-sdk/raw/master/src/img/sdk-user-guide8.png "step5")<br/>
Now the classes and methods in the Yesup Partner Library can be used in your app.<br/><br/><br/>

<div id="step6"></div>
#####**Step 6** Add in the following line to your **_"AndroidManifest.xml"_** to declare the Activity:
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;You can modify the android:label,this property will display on the title of activity.<br/>

<activity android:name="com.yesup.partner.OfferWallActivity" android:label="OfferWall" />
![alt text](https://github.com/yesup/partner-sdk/raw/master/src/img/sdk-user-guide4.png "step6")<br/><br/><br/>

<div id="step7"></div>
##### **Step 7** Now you can **startup Yesup OfferWall** using below code:<br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;7.1 import com.yesup.partner.YesupAd;<br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;7.2 In onCreate method, new a YesupAd instance.<br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;7.3 In onResume method, call YesupAd's onResume method.<br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;7.4 Call YesupAd.getAllZoneList() to get all ad zones which you can show, there are ad's type and id in the zone.<br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;7.5 new and set a OfferWallHelper object, you can control the view of rewards button(rewards and icon).<br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;7.6 Select one zone which type is Define.AD_TYPE_OFFER_WALL and call YesupAd.showOfferWall() method to startup OfferWall interface.<br/>

Example code:(Entire code in the DemoActivity.java)
```python
import com.yesup.partner.YesupAd;
import com.yesup.partner.module.Define;
import com.yesup.partner.module.PartnerAdConfig;

public class MainActivity extends AppCompatActivity {

    private YesupAd yesupAd;
    private ArrayList<PartnerAdConfig.Zone> zoneList;
    private OfferWallHelper offerWallHelper = new OfferWallHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // new yesup ad instance
        yesupAd = new YesupAd(this);
        yesupAd.setOfferWallPartnerHelper(offerWallHelper);
        zoneList = yesupAd.getAllZoneList();
    }

    @Override
    protected void onResume() {
        super.onResume();
        yesupAd.onResume();
    }

    protected void showOfferWall() {

        String subId = "123123";  // optional, you app user id
        String optValue1 = "";    // optional, additional event value you want to keep track
        String optValue2 = "";    // optional, additional event value you want to keep track

        for (int i=0; i<zoneList.size(); i++) {
            int zoneId = zoneList.get(i).id;
            int adType = yesupAd.getAdTypeByZoneId(zoneId);
            switch (adType) {
                case Define.AD_TYPE_OFFER_WALL:
                    yesupAd.showOfferWall(subId, zoneId, optValue1, optValue2);
                    break;
                default:
                    break;
            }
        }
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

```
![alt text](https://github.com/yesup/partner-sdk/raw/master/src/img/sdk-user-guide4.png "step7")<br/><br/><br/>
