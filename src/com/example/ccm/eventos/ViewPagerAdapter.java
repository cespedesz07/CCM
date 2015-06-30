package com.example.ccm.eventos;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class ViewPagerAdapter extends FragmentPagerAdapter {
	
	

	public ViewPagerAdapter(FragmentManager childFramentManager) {
		super( childFramentManager );
	}
	

	@Override
	public Fragment getItem(int position) {
		return new TabFragment();
	}
	

	@Override
	public int getCount() {		
		return 5;
	}

}
