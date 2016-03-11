# partner-sdk
###### Lastest SDK Version: 1.0.160309.01
Yesup Partner SDK for Android is the easiest way to integrate your Android app with Yesup.
## Installation
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[Step1: Download "yesuppartner.aar"](#step1)<br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[Step2: Copy yesuppartner.aar](#step2)<br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[Step3: Copy adconfigure.xml](#step3)<br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[Step4: Open the app's build.gradle file](#step4)<br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[step5: Add content in repositories" section](#step5)<br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[step6: Add content in dependencies section](#step6)<br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[step7: Modify your AndroidManifest.xml file](#step7)<br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[step8: Add content in AndroidManifest.xml](#step8)<br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[step9: Startup Yesup OfferWall](#step9)<br/>
<hr/>

<div id="step1"></div>
##### **Step 1** Download yesup partner library file **_"yesuppartner.aar"_** and config file "adconfigure.xml".<br/><br/>
![alt text](https://github.com/yesup/partner-sdk/raw/master/src/img/sdk-user-guide5.png "step1")<br/><br/><br/>

<div id="step2"></div>
##### **Step 2** Copy yesuppartner.aar file to your **"libs"** directory of project.<br/><br/>
![alt text](https://github.com/yesup/partner-sdk/raw/master/src/img/sdk-user-guide1.png "step2")<br/><br/><br/>

<div id="step3"></div>
#####**Step 3** Copy **_"adconfigure.xml"_** file to your "res/xml/adconfigure.xml" directory.
**_Note: Do not modify this file name!!!_**<br/><br/>
![alt text](https://github.com/yesup/partner-sdk/raw/master/src/img/sdk-user-guide7.png "step3")<br/><br/><br/>

<div id="step4"></div>
##### **Step 4** In **Android Studio**, open the app's** _"build.gradle"_** file in the editor.<br/><br/>
![alt text](https://github.com/yesup/partner-sdk/raw/master/src/img/sdk-user-guide6.png "step4")<br/><br/><br/>

<div id="step5"></div>
##### **step 5** Add **"repositories" section**, as follows:
```python
repositories {
    flatDir {
        dirs 'libs'
    }
}
```
![alt text](https://github.com/yesup/partner-sdk/raw/master/src/img/sdk-user-guide2.png "step3")<br/><br/><br/>
<div id="step6"></div>
##### **Step 6** Add in the following line to the dependencies section.

```python
compile (name: 'yesuppartner', ext: 'aar')
```
![alt text](https://github.com/yesup/partner-sdk/raw/master/src/img/sdk-user-guide8.jpg "step6")<br/>
Now the classes and methods in the Yesup Partner Library can be used in your app.<br/><br/><br/>

<div id="step7"></div>
##### **Step 7** Modify your AndroidManifest.xml file, add in the following content:

```python
    <uses-permission android:name="android.permission.INTERNET"/>
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
```
<br/><br/><br/>

<div id="step8"></div>
#####**Step 8** Add in the following line to your **_"AndroidManifest.xml"_** to declair the Activity:

<activity android:name="com.yesup.partner.OfferWallActivity" android:label="OfferWall" />
![alt text](https://github.com/yesup/partner-sdk/raw/master/src/img/sdk-user-guide3.png "step7")<br/><br/><br/>

<div id="step9"></div>
##### **Step 9** Now you can **startup Yesup OfferWall** using below code:<br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;9.1 import com.yesup.partner.OfferWall;<br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;9.2 In onCreate method, new a OfferWall instance<br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;9.3 In onResume method, call OfferWall's onResume method<br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;9.4 You use OfferWall's show method to startup OfferWall interface.<br/>

Example code:
```python
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

```
![alt text](https://github.com/yesup/partner-sdk/raw/master/src/img/sdk-user-guide4.png "step9")<br/><br/><br/>
