package com.example.ccm.registro;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.AlertDialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.example.ccm.R;
import com.example.ccm.actionbar.CCMActionBarActivity;
import com.example.ccm.wsclient.RestClientTask;


/**
 * Actividad que representa el registro nativo en la plataforma
 * IMPORTANTE: Si el usuario se loguea con las redes sociales, se capturan 
 * esos campos para no ingresarlos en el registro.
 * @author Santiago Céspedes Zapata - cespedesz07@gmail.com
 *
 */
public class RegistroActivity extends CCMActionBarActivity implements OnTouchListener, OnClickListener {
	
	
	
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
		
		spinnerTipoDocumento = (Spinner) findViewById(R.id.spinner_tipo_documento);
		SpinnerArrayAdapter spinnerTipoDocumentoAdapter = new SpinnerArrayAdapter( this, SpinnerArrayAdapter.TIPO_DOCUMENTO );
		spinnerTipoDocumentoAdapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );
		spinnerTipoDocumento.setAdapter( spinnerTipoDocumentoAdapter );
		
		txtNumDocumento = (EditText) findViewById( R.id.txt_num_documento );
		
		txtNombre = (EditText) findViewById( R.id.txt_nombre );
		
		txtApellidos = (EditText) findViewById( R.id.txt_apellidos );
		
		radioGroupGenero = (RadioGroup) findViewById( R.id.radio_group_genero );
		
		txtEmail = (EditText) findViewById( R.id.txt_email );
		
		txtTelefono = (EditText) findViewById( R.id.txt_telefono );
		
		pickerFechaNacimiento = (DatePicker) findViewById( R.id.picker_fecha_nacimiento );		
		
		
		spinnerPaisProcedencia = (Spinner) findViewById(R.id.spinner_pais_procedencia);
		SpinnerArrayAdapter spinnerPaisProcedenciaAdapter = new SpinnerArrayAdapter( this, SpinnerArrayAdapter.PAIS_PROCEDENCIA );
		spinnerPaisProcedenciaAdapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );
		spinnerPaisProcedencia.setAdapter( spinnerPaisProcedenciaAdapter );
		
		spinnerInstitucion = (Spinner) findViewById(R.id.spinner_institucion);
		SpinnerArrayAdapter spinnerInstitucionAdapter = new SpinnerArrayAdapter( this, SpinnerArrayAdapter.INSTITUCION );
		spinnerInstitucionAdapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );
		spinnerInstitucion.setAdapter( spinnerInstitucionAdapter );
		
		btnRegistro = (Button) findViewById( R.id.btn_registro_registro_activity );
		btnRegistro.setOnTouchListener( this );
		btnRegistro.setOnClickListener( this );	
		
		
		//Se capturan los datos proporcionados por el login con redes sociales
		//a partir de bundleParams que envia LoginActivity.inicioRegistro();
		Bundle bundleParamsLogin =  getIntent().getExtras();
		//Si el Bundle no tiene parametros, es porque se realizó un response nativo (NATIVE_RESPONSE)
		//es decir se hizo click en el boton "Registrar"
		if ( bundleParamsLogin.isEmpty() ){
			
		}
		else{
			obtenerDatosLoginRedesSociales( bundleParamsLogin );
			new AlertDialog.Builder(this)
			.setMessage( getResources().getString(R.string.alert_registro_redes_sociales) )
			.setPositiveButton(R.string.alert_ok, null)
			.show();
		}
				
	}	
	
	
	
	
	//Método para capturar los datos proporcionados por las redes sociales
	//en caso de que el usuario haya accedido a la app por estas opciones
	//Este método es llamado 
	private void obtenerDatosLoginRedesSociales( Bundle bundleParams ){
		if ( !bundleParams.isEmpty() ){
			txtNombre.setText(  bundleParams.getString( "nombre" )  );
			txtNombre.setEnabled(false);		
			
			txtApellidos.setText(  bundleParams.getString( "apellidos" )  );
			txtApellidos.setEnabled(false);
			
			boolean radioButtonGeneroChecked = false;
			int numRadioButtons = radioGroupGenero.getChildCount();
			for ( int i=0; i<numRadioButtons; i++ ){
				RadioButton radioButton = (RadioButton) radioGroupGenero.getChildAt(i);
				if (  radioButton.getText().equals( bundleParams.getString("genero") )   ){
					radioButton.setChecked( true );		
					radioButtonGeneroChecked = true;
					break;
				}
			}
			if ( radioButtonGeneroChecked ){
				for ( int i=0; i<numRadioButtons; i++ ){
					radioGroupGenero.getChildAt(i).setEnabled(false);
				}
			}
			
			
			String[] fechaNacimientoPartida = bundleParams.getStringArray( "fecha_nacimiento" );
			pickerFechaNacimiento.updateDate(  Integer.valueOf(fechaNacimientoPartida[2]) , Integer.valueOf(fechaNacimientoPartida[0]) - 1, Integer.valueOf(fechaNacimientoPartida[1])  );
			pickerFechaNacimiento.setEnabled(false);
			
			bundleParams.getString( "correo_electronico" );
			txtEmail.setText(  bundleParams.getString( "correo_electronico" )  );
			txtNombre.setEnabled(false);
		}
	}
	
	
	
	@Override
	protected void onStart(){
		super.onStart();
		//overridePendingTransition( R.animator.first_close_translate, R.animator.second_close_scale);
	}
	
	
	
	@Override
	protected void onPause(){
		super.onPause();
	}
	
	
	
	@Override
	protected void onDestroy(){
		super.onDestroy();
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


	
	
	//Método para capturar las pulsaciones de los botones. Provienen de la interfaz OnClickListener
	@Override
	public void onClick(View v) {
		if ( v.getId() == R.id.btn_registro_registro_activity ){
			guardarDatosFormulario();
		}
		
	}
	
	
	//Método que captura los datos ingresados en el formulario de registro
	//y los almacena en el WebService mediante el llamado a la clase RestClientTask().execute
	// IMPORTANTE: para enviar los campos del formulario al WebService es necesario que los campos
	// coincidan con los mismos de la base de datos, por eso en el List<NameValuePair> parametros
	// las claves tienen el mismo nombre de los campos de la tabla Persona en la BD CCM_BD
	private void guardarDatosFormulario(){
		//String tipoDocumentoCampo = (  (String) spinnerTipoDocumento.getSelectedItem()  ).toString();
		int numDocumentoCampo = Integer.valueOf( txtNumDocumento.getText().toString() ); 
		String nombreCampo = txtNombre.getText().toString();
		String apellidosCampo = txtApellidos.getText().toString();		
		int indiceGeneroCampo = radioGroupGenero.getCheckedRadioButtonId();   //Si este valor retorna -1, es porque no se ha seleccionado ningun campo
		String generoCampo = (  (RadioButton) radioGroupGenero.findViewById( indiceGeneroCampo )  ).getText().toString();		
		String fechaNacimientoCampo = obtenerFechaCampo( pickerFechaNacimiento );
		String emailCampo = txtEmail.getText().toString();		
		String telefonoCampo = txtTelefono.getText().toString();		
		String codigoQRCampo = "";
		int tipoDocumentoCampo = 1;		
		String paisProcedenciaCampo = (  (String) spinnerPaisProcedencia.getSelectedItem()  ).toString();		
		//String institucionCampo = (  (String) spinnerTipoDocumento.getSelectedItem()  ).toString();
		int institucionCampo = 1;
		int tipoPersonaCampo = 1;
		
		List<NameValuePair> parametros = new ArrayList<NameValuePair>();
		parametros.add(  new BasicNameValuePair( "docPersona", String.valueOf(numDocumentoCampo) )  );
		parametros.add(  new BasicNameValuePair( "nombre", nombreCampo )  );
		parametros.add(  new BasicNameValuePair( "apellidos", apellidosCampo )  );
		parametros.add(  new BasicNameValuePair( "genero", generoCampo )  );
		parametros.add(  new BasicNameValuePair( "fecha_nacimiento", fechaNacimientoCampo )  );
		parametros.add(  new BasicNameValuePair( "correo_electronico", emailCampo )  );
		parametros.add(  new BasicNameValuePair( "telefono", telefonoCampo )  );
		parametros.add(  new BasicNameValuePair( "codigo_qr", codigoQRCampo )  );
		parametros.add(  new BasicNameValuePair( "tipo_doc_idtipo_doc", String.valueOf(tipoDocumentoCampo)  ) );
		parametros.add(  new BasicNameValuePair( "pais_procedencia_idpais_procedencia", String.valueOf(tipoDocumentoCampo)  ) );
		parametros.add(  new BasicNameValuePair( "institucion_idinstitucion", String.valueOf(tipoDocumentoCampo)  )  );
		parametros.add(  new BasicNameValuePair( "tipo_persona_idtipo_persona", String.valueOf(tipoPersonaCampo)  )  );
		
		new RestClientTask( this ).execute( parametros );
	}
	
	
	
	private static String obtenerFechaCampo( DatePicker datePicker ){
		String fechaCapturada = "";		
		int dia = datePicker.getDayOfMonth();
		int mes = datePicker.getMonth();
		int año = datePicker.getYear();		
		Calendar calendar = Calendar.getInstance();
		calendar.set( año, mes, dia );
		SimpleDateFormat dateFormat = new SimpleDateFormat( "dd/M/yyyy" );
		fechaCapturada = dateFormat.format( calendar.getTime() );
		return fechaCapturada;
	}

	
	
	
}
