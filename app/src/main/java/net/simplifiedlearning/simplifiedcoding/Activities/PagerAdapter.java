package net.simplifiedlearning.simplifiedcoding.Activities;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by truongnm on 3/31/18.
 */

public class PagerAdapter extends FragmentStatePagerAdapter {
    PagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }
    @Override
    public Fragment getItem(int position) {
        Fragment frag=null;
        switch (position){
            case 0:
                frag = new FragmentEgg();
                break;
            case 1:
                frag = new FragmentProfile();
                break;
        }
        return frag;
    }

    @Override
    public int getCount() {
        return 2;
    }
    @Override
    public CharSequence getPageTitle(int position) {
        String title = "";
        switch (position){
            case 0:
                title = "EGG";
                break;
            case 1:
                title = "Profile";
                break;
        }
        return title;
    }

}
