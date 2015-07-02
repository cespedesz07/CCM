package com.example.ccm.eventos;

import java.util.ArrayList;

import com.example.ccm.restclient.AreaListFragmentAsyncResponse;
import com.example.ccm.restclient.AreaListFragmentRestClientTask;

import android.content.Context;
import android.widget.ArrayAdapter;

public class AreaListFragmentArrayAdapter extends ArrayAdapter {
	
	
	
	public AreaListFragmentArrayAdapter( Context context ){
		super( context, android.R.layout.simple_list_item_activated_1, android.R.id.text1 );
		
		/*
		AreaListFragmentRestClientTask areaListFragmentRestClientTask = new AreaListFragmentRestClientTask(context, this);
		areaListFragmentRestClientTask.execute();
		*/
	}
	
	

}
