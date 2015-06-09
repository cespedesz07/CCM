package com.example.ccm.wsclient;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ccm.R;



public class MainActivityRestClient extends Activity {
	
	
	private EditText editText1;
	private Button button1;
	private TextView textView1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_activity_rest_client);
	}

	
	
	public void consultarUsuarios(View v) {
		
		editText1 = (EditText) findViewById(R.id.editText1);
		String urlWebService = String.valueOf( editText1.getText() );
		
		
		RestClientTask restClientTask = new RestClientTask( this );
		restClientTask.execute( urlWebService );
		
		//textView1 = (TextView) findViewById( R.id.textView1 );
		
		
	}
	
	
	
}
