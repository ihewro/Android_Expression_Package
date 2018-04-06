package com.ihewro.android_expression_package.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ihewro.android_expression_package.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ExpressionContentFragment extends Fragment {


    public ExpressionContentFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_expression_content, container, false);
    }

}
