package com.example.ccm.registro;


import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.example.ccm.R;


/**
 * Actividad que representa el registro nativo en la plataforma
 * IMPORTANTE: Si el usuario se loguea con las redes sociales, se capturan 
 * esos campos para no ingresarlos en el registro.
 * @author Santiago Céspedes Zapata - cespedesz07@gmail.com
 *
 */
public class RegistroActivity extends ActionBarActivity implements OnTouchListener {
	
	
	
	private Spinner spinnerTipoDocumento;
	private EditText txtNumDocumento;
	private EditText txtNombre;
	private EditText txtApellidos;
	private RadioGroup radioGroupGenero;
	private DatePicker pickerFechaNacimiento;
	private EditText txtEmail;
	private EditText txtTelefono;
	private Spinner spinnerPaisProcedencia;
	private Spinner spinnerInstitucion;
	private Button btnRegistro;
	

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.registro);
		
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		spinnerTipoDocumento = (Spinner) findViewById(R.id.spinner_tipo_documento);
		SpinnerArrayAdapter spinnerTipoDocumentoAdapter = new SpinnerArrayAdapter( this, SpinnerArrayAdapter.TIPO_DOCUMENTO );
		spinnerTipoDocumentoAdapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );
		spinnerTipoDocumento.setAdapter( spinnerTipoDocumentoAdapter );
		
		spinnerPaisProcedencia = (Spinner) findViewById(R.id.spinner_pais_procedencia);
		SpinnerArrayAdapter spinnerPaisProcedenciaAdapter = new SpinnerArrayAdapter( this, SpinnerArrayAdapter.PAIS_PROCEDENCIA );
		spinnerPaisProcedenciaAdapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );
		spinnerPaisProcedencia.setAdapter( spinnerPaisProcedenciaAdapter );
		
		spinnerInstitucion = (Spinner) findViewById(R.id.spinner_institucion);
		SpinnerArrayAdapter spinnerInstitucionAdapter = new SpinnerArrayAdapter( this, SpinnerArrayAdapter.INSTITUCION );
		spinnerInstitucionAdapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );
		spinnerInstitucion.setAdapter( spinnerInstitucionAdapter );
		
		btnRegistro = (Button) findViewById( R.id.btnRegistro_registroActivity );
		btnRegistro.setOnTouchListener( this );
		
		
	}
	
	
	
	@Override
	protected void onStart(){
		super.onStart();
		overridePendingTransition( R.animator.first_close_translate, R.animator.second_close_scale);
	}
	
	
	
	@Override
	protected void onPause(){
		super.onPause();
	}
	
	
	
	@Override
	protected void onDestroy(){
		super.onDestroy();
	}
	

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.registro, menu);		
		return true;
	}
	
	

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
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
}
