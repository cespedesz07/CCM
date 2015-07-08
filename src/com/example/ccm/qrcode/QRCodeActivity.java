package com.example.ccm.qrcode;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;

import com.example.ccm.R;
import com.example.ccm.actionbar.CCMActionBarActivity;
import com.example.ccm.eventos.AreaListActivity;
import com.example.ccm.restclient.QRCodeHttpClientTask;
import com.example.ccm.restclient.RegistroRestClientTask;


/**
 * Actividad en donde se genera el Código QR de acuerdo al documento de la persona
 *  IMPORTANTE: Para generar el Código QR se hace una peticion GET a la API de QR
 *  que 
 * @author Santiago Céspedes Zapata - cespedesz07@gmail.com
 *
 */
public class QRCodeActivity extends CCMActionBarActivity implements OnClickListener, OnTouchListener {	
	
	
	private String documentoPersona;
	private ImageView codigoQR;
	private Button btnSiguiente;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.qrcode);
		
		codigoQR = (ImageView) findViewById( R.id.codigo_qr );
		
		btnSiguiente = (Button) findViewById( R.id.btn_siguiente);		
		btnSiguiente.setOnTouchListener( this );
		btnSiguiente.setOnClickListener( this );
		
		Bundle bundleParams = getIntent().getExtras();
		documentoPersona = bundleParams.getString( RegistroRestClientTask.CAMPO_DOC_PERSONA );		
		new QRCodeHttpClientTask(this, codigoQR).execute( documentoPersona );
	}


	//Método para capturar el presionado del botón y cabiar el fondo del mismo
	//esto es para crear el efecto de presionado de los botones
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if ( event.getAction() == MotionEvent.ACTION_DOWN ){	//Si se presiona la pantalla...
			Drawable d = v.getBackground();						// se captura el background la vista presionada
			//d.mutate();
			d.setAlpha(50);									    // se agrega transparencia al background
			v.setBackgroundDrawable(d);							// A la vista presionada se agrega el nuevo background transparente
		}
		else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL){
			Drawable d = v.getBackground();
			d.setAlpha(255);
			v.setBackgroundDrawable(d);
		}
		return false;
	}



	@Override
	public void onClick(View view) {
		if ( view.getId() == R.id.btn_siguiente ){
			Intent i = new Intent( QRCodeActivity.this, AreaListActivity.class );
			startActivity( i );	
		}		
	}
	
	
	
	
}
