package com.yesup.ad.offerwall;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import com.yesup.partner.R;
import com.yesup.ad.framework.DataCenter;
import com.yesup.ad.framework.Define;


/**
 * Created by derek on 2/22/16.
 */
public class DetailFragment extends Fragment {

    private MessageHandler msgHandler = new MessageHandler();

    // container Activity must implement this interface
    OnUserOperationListener mCallback;
    public interface OnUserOperationListener {
        void onUserOperate(int opt);
    }

    private DataCenter dataCenter = DataCenter.getInstance();
    private int offerIndex = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        Log.v("Meshbean", "DetailFragment OnCreateView");
        View view = inflater.inflate(R.layout.yesup_fragment_detail, container, false);
        ScrollView sv = (ScrollView)view.findViewById(R.id.DetailScrollView);
        sv.smoothScrollTo(0, 0);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.v("Meshbean", "DetailFragment onAttach");

        // This make sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnUserOperationListener)getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString()
                    +" must implement OnUserOperationListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        int pos = getArguments().getInt("Index");
        setOfferIndex(pos);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    public void setOfferIndex(int index) {
        offerIndex = index;
        showOfferDetail();
    }

    protected void showOfferDetail() {
        /*OfferModel offer = dataCenter.getOfferWallAd().getOfferAt(offerIndex);
        if (offer == null) {
            return;
        }
        ImageView ivIcon = (ImageView)getView().findViewById(R.id.DetailAppIcon);
        TextView tvName = (TextView)getView().findViewById(R.id.DetailTitle);
        TextView tvDesc = (TextView)getView().findViewById(R.id.DetailRemark);
        EditText evDetail = (EditText)getView().findViewById(R.id.EditDetailDesc);
        EditText evTerms = (EditText)getView().findViewById(R.id.EditDetailTerms);
        TextView tvSystem = (TextView)getView().findViewById(R.id.DetailPlatform);
        TextView tvCost = (TextView)getView().findViewById(R.id.DetailFree);

        File imageFile = new File(offer.getLocalIconPath());
        if (imageFile.exists()){
            Bitmap bmp = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
            ivIcon.setImageBitmap(bmp);
        }
        tvName.setText( offer.getTitle() );
        tvDesc.setText( offer.getShortDesc() );
        evDetail.setText( offer.getShortDesc() );
        evTerms.setText( offer.getTc() );
        tvSystem.setText( offer.getAppType() );
        if (offer.getAppStorePrice() == 0) {
            tvCost.setText("FREE");
        }else{
            tvCost.setText("Cost: "+(float)offer.getAppStorePrice()/(float)100.0);
        }*/
    }

    public class MessageHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Define.MSG_AD_REQUEST_SUCCESSED:
                    if (msg.arg1 == offerIndex) {
                        showOfferDetail();
                    }
                    break;
                default:
                    break;
            }
        }
    }
}
