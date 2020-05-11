package software.engineering.yatzy.overview;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import software.engineering.yatzy.R;
import software.engineering.yatzy.highscore.HighscoreFragment;
import software.engineering.yatzy.settings.SettingsFragment;

public class MainTabsAdapter extends FragmentStatePagerAdapter {


    public MainTabsAdapter(@NonNull FragmentManager fm) {

        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {

        switch (position){
            case 0:
                return new HomeFragment();
            case 1:
                return new SettingsFragment();

            case 2:
                return new HighscoreFragment();
            default:
            return null;

        }

    }

        @Nullable
    @Override
    public CharSequence getPageTitle(int position)
    {
        switch (position)
        {
            case 0:
                return "MyGames";

            case 1:
                return "Settings";

            case 2:
                return "Highscore";
            default:
                return null;

        }
    }

    @Override
    public int getCount() {
        return 3;
    }


}
