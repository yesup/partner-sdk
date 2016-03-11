# partner-sdk
###### Lastest SDK Version: 1.0.160309.01
Yesup Partner SDK for Android is the easiest way to integrate your Android app with Yesup.
## Installation
[Step1: Download "yesuppartner.aar"](#step1)<br/>
[Step2: Copy yesuppartner.aar](#step2)<br/>
[Step3: Copy adconfigure.xml](#step3)<br/>
[Step4: Open the app's build.gradle file](#step4)<br/>
[step5: Add content in repositories" section](#step5)<br/>
[step6: Add content in dependencies section](#step6)<br/>
[step7: Modify your AndroidManifest.xml file](#step7)<br/>
[step8: Add content in AndroidManifest.xml](#step8)<br/>
[step9: Startup Yesup OfferWall](#step9)<br/>


1 Download yesup partner library file "yesuppartner.aar" and config file "adconfigure.xml".<br/><br/>
![alt text](https://github.com/yesup/partner-sdk/raw/master/src/img/sdk-user-guide5.png "step5")<br/>


2 Copy yesuppartner.aar file to your "libs" directory of project.

3 Copy adconfigure.xml file to your "res/xml/adconfigure.xml" directory.
Note: Do not modify this file name!!!

4 In Android Studio, open the app's build.gradle file in the editor.

5 Add "repositories" section, as follows:

repositories {
    flatDir {
        dirs 'libs'
    }
}

6 Add in the following line to the dependencies section.

compile (name: 'yesuppartner', ext: 'aar')

Now the classes and methods in the Yesup Partner Library can be used in your app.

<polymer-element attributes="id step7">
7 Modify your AndroidManifest.xml file, add in the following content:

    <uses-permission android:name="android.permission.INTERNET"/>
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>

8 Add in the following line to your AndroidManifest.xml to declair the Activity:

<activity android:name="com.yesup.partner.OfferWallActivity" android:label="OfferWall" />

9 Now you can startup Yesup OfferWall using below code:
9.1 import com.yesup.partner.OfferWall;
9.2 In onCreate method, new a OfferWall instance
9.3 In onResume method, call OfferWall's onResume method
9.4 You use OfferWall's show method to startup OfferWall interface.

Example code:
import com.yesup.partner.OfferWall;

public class MainActivity extends AppCompatActivity {

    private OfferWall offerWall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // new OfferWall instance
        offerWall = new OfferWall(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String subId = "123123";
                offerWall.show(subId);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        offerWall.onResume();
    }

}


