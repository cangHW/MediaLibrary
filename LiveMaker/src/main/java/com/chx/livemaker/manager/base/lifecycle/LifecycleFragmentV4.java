package com.chx.livemaker.manager.base.lifecycle;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.chx.livemaker.manager.base.helpr.LifecycleHelper;
import com.chx.livemaker.constant.LiveMakerConstant;

/**
 * Created by cangHX
 * on 2018/12/26  12:01
 */
public class LifecycleFragmentV4 extends Fragment {

    private int mKey;

    public static LifecycleFragmentV4 create(int key) {
        LifecycleFragmentV4 fragmentApp = new LifecycleFragmentV4();
        Bundle bundle = new Bundle();
        bundle.putInt(LiveMakerConstant.LIFECYCLE_TAG, key);
        fragmentApp.setArguments(bundle);
        return fragmentApp;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle=getArguments();
        if (bundle == null) {
            return;
        }
        mKey = bundle.getInt(LiveMakerConstant.LIFECYCLE_TAG, 0);
        LifecycleHelper.getInstance().onCreate(getActivity(), mKey);
    }

    @Override
    public void onStop() {
        super.onStop();
        LifecycleHelper.getInstance().onStop(getActivity(), mKey);
    }

    @Override
    public void onResume() {
        super.onResume();
        LifecycleHelper.getInstance().onResume(getActivity(), mKey);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LifecycleHelper.getInstance().onDestroy(getActivity(), mKey);
    }
}
