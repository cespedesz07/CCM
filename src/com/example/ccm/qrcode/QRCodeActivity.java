package com.example.ccm.qrcode;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import com.example.ccm.R;
import com.example.ccm.actionbar.CCMActionBarActivity;
import com.example.ccm.restclient.QRCodeHttpClientTask;
import com.example.ccm.restclient.RegistroRestClientTask;


/**
 * Actividad en donde se genera el Código QR de acuerdo al documento de la persona
 *  IMPORTANTE: Para generar el Código QR se hace una peticion GET a la API de QR
 *  que 
 * @author Santiago Céspedes Zapata - cespedesz07@gmail.com
 *
 */
public class QRCodeActivity extends CCMActionBarActivity {	
	
	
	private String documentoPersona;
	private ImageView codigoQR;
	private Button btnSiguiente;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.qrcode);
		
		codigoQR = (ImageView) findViewById( R.id.codigo_qr );		
		btnSiguiente = (Button) findViewById( R.id.btn_siguiente);
		
		Bundle bundleParams = getIntent().getExtras();
		documentoPersona = bundleParams.getString( RegistroRestClientTask.CAMPO_DOC_PERSONA );		
		new QRCodeHttpClientTask(this, codigoQR).execute( documentoPersona );
	}
	
	
	
	@Override
	protected void onStart(){
		super.onStart();
	}
	
	
	
	@Override
	protected void onResume(){
		super.onResume();
	}
	
	
	
	@Override
	protected void onPause(){
		super.onPause();
	}
	
	
	
	@Override
	protected void onStop(){
		super.onDestroy();
	}
	
	
	@Override
	protected void onDestroy(){
		super.onDestroy();
	}
	
	
	
	
}
