package com.questio.projects.questiodevelopment.sections;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.questio.projects.questiodevelopment.R;

/**
 * Created by CHAKRIT on 16/2/2558.
 */
public class SectionAvatar extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_section_avatar, container, false);
        Bundle args = getArguments();

        return rootView;
    }
}