package com.questio.projects.questiodevelopment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by CHAKRIT on 16/2/2558.
 */
public  class SectionQuestmap extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_section_questmap, container, false);
        Bundle args = getArguments();

        return rootView;
    }
}
