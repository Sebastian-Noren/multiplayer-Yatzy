package software.engineering.yatzy.overview;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import software.engineering.yatzy.R;

public class MainFragment extends Fragment {

    private String tag = "Info";

    private Toolbar mainToolbar;
    //lets one slide between fragments
    private ViewPager viewPager;
    //holds all fragments
    private MainTabsAdapter fragmentTabsAdapter;

    private TabLayout tabLayout;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_fragment, container, false);
        getActivity().getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        Log.d(tag, "In the MainFragment");


        //main toolbar, included in main activity xml
     //   mainToolbar = (Toolbar) view.findViewById(R.id.main_toolbar);
     //   ((AppCompatActivity) getActivity()).setSupportActionBar(mainToolbar);

        //view pager widget
        viewPager = view.findViewById(R.id.main_view_pager);
        //holds all fragments
        fragmentTabsAdapter = new MainTabsAdapter(getActivity().getSupportFragmentManager());
        //include fragments to switch between
        viewPager.setAdapter(fragmentTabsAdapter);

        //TabLayout connected with viewPager
        tabLayout = view.findViewById(R.id.main_tabs);
        tabLayout.setupWithViewPager(viewPager);

        return view;
    }


}