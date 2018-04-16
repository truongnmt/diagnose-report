package net.simplifiedlearning.simplifiedcoding.Activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.simplifiedlearning.simplifiedcoding.Models.User;
import net.simplifiedlearning.simplifiedcoding.R;
import net.simplifiedlearning.simplifiedcoding.Utils.SharedPrefManager;

/**
 * Created by truongnm on 3/31/18.
 */

public class FragmentProfile extends Fragment {
    public FragmentProfile() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        User user = SharedPrefManager.getInstance(getActivity()).getUser();
        ((TextView) getActivity().findViewById(R.id.textViewUsername)).setText(user.getUsername());
        ((TextView) getActivity().findViewById(R.id.textViewEmail)).setText(user.getEmail());
        ((TextView) getActivity().findViewById(R.id.textViewGender)).setText(user.getGender());
    }
}