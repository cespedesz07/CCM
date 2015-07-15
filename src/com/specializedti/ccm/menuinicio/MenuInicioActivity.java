package com.specializedti.ccm.menuinicio;

import java.util.List;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.TextView;

import com.specializedti.ccm.R;
import com.specializedti.ccm.actionbar.CCMActionBarActivity;
import com.specializedti.ccm.eventos.AreaListActivity;
import com.specializedti.ccm.mapa.MapaActivity;
import com.specializedti.ccm.preferences.CCMPreferences;
import com.specializedti.ccm.qrcode.QRCodeActivity;
import com.specializedti.ccm.restclient.CargaUbicacionesPersonaRestClientTask;

public class MenuInicioActivity extends CCMActionBarActivity implements OnClickListener, OnTouchListener {
	
	
	private TextView textViewPersona;
	private Button btnEventos;
	private TextView textViewMisEventos;
	private Button btnMapaCampus;
	private TextView textViewMapaCampus;
	private Button btnCodigoQR;
	private TextView textViewCodigoQR;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menu_inicio);
		
		textViewPersona = (TextView) findViewById( R.id.textview_nombre_persona );
		
		btnEventos = (Button) findViewById( R.id.btn_eventos );
		textViewMisEventos = (TextView) findViewById( R.id.textview_mis_eventos );
		
		btnMapaCampus = (Button) findViewById( R.id.btn_mapa_campus );
		textViewMapaCampus = (TextView) findViewById( R.id.textview_mapa_campus );
		
		btnCodigoQR = (Button) findViewById( R.id.btn_codigo_qr );
		textViewCodigoQR = (TextView) findViewById( R.id.textview_codigo_qr );
		
		String nombrePersona = new CCMPreferences(this).obtenerNombrePersona();
		String textoBienvenida = getResources().getString( R.string.bienvenida_usuario ) + " " + nombrePersona + "!";
		textViewPersona.setText( textoBienvenida );
		
		btnCodigoQR.setOnClickListener(this);
		btnCodigoQR.setOnTouchListener(this);
		btnEventos.setOnClickListener(this);
		btnEventos.setOnTouchListener(this);
		btnMapaCampus.setOnClickListener(this);
		btnMapaCampus.setOnTouchListener(this);
		
		//Se deshabilita la capacidad de regresar a una actividad anterior
		disableHome();
		
	}
	
	@Override
	protected void onRestart() {
		
		super.onRestart();
	}
	
	
	
	
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if ( event.getAction() == MotionEvent.ACTION_DOWN ){	//Si se presiona la pantalla...
			Drawable d = v.getBackground();						// se captura el background la vista presionada
			//d.mutate();
			d.setAlpha(150);									    // se agrega transparencia al background
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
		if ( hayInternet() ){
			switch ( view.getId() ){		
			case R.id.btn_eventos:
				/*
				CCMPreferences preferences = new CCMPreferences(this);
				List<String> idUbicaciones = preferences.obtenerIdRegistrosUbicacion();
				if ( idUbicaciones.isEmpty() ){
					String docPersona = preferences.obtenerDocPersona();		
					new CargaUbicacionesPersonaRestClientTask(this).execute(docPersona);
				}
				else{
					startActivity( new Intent(this, AreaListActivity.class) );
				}*/
				startActivity( new Intent(this, AreaListActivity.class) );
				break;
			
			case R.id.btn_mapa_campus:
				startActivity( new Intent(this, MapaActivity.class) );
				break;
				
			case R.id.btn_codigo_qr:
				startActivity( new Intent(this, QRCodeActivity.class) );
				break;
				
			default:
				break;			
			}
		}
	}
}
