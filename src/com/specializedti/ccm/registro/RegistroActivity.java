package com.specializedti.ccm.registro;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.specializedti.ccm.R;
import com.specializedti.ccm.actionbar.CCMActionBarActivity;
import com.specializedti.ccm.eventos.AreaListActivity;
import com.specializedti.ccm.login.LoginActivity;
import com.specializedti.ccm.preferences.CCMPreferences;
import com.specializedti.ccm.qrcode.QRCodeActivity;
import com.specializedti.ccm.restclient.CargaUbicacionesPersonaRestClientTask;
import com.specializedti.ccm.restclient.LoginRestClientTask;
import com.specializedti.ccm.restclient.RegistroRestClientTask;
import com.specializedti.ccm.restclient.SpinnerRestClientTask;


/**
 * Actividad que representa el registro nativo en la plataforma
 * IMPORTANTE: Si el usuario se loguea con las redes sociales, se capturan 
 * esos campos para no ingresarlos en el registro.
 * @author Santiago C�spedes Zapata - cespedesz07@gmail.com
 *
 */
public class RegistroActivity extends CCMActionBarActivity implements OnTouchListener, OnClickListener {
	
	
	private static final String FORMATO_FECHA = "yyyy-mm-dd";
	
	
	private ImageView logoCongreso;
	private TextView textViewTipoDocumento;
	private Spinner spinnerTipoDocumento;
	private EditText txtNumDocumento;
	private EditText txtNombre;
	private EditText txtApellidos;
	private TextView textViewGenero;
	private RadioGroup radioGroupGenero;
	private EditText txtEmail;
	private EditText txtTelefono;
	private TextView textViewPaisProcedencia;
	private Spinner spinnerPaisProcedencia;
	private TextView textViewInstitucion;
	private Spinner spinnerInstitucion;
	private TextView textViewFechaNacimiento;
	private DatePicker pickerFechaNacimiento;
	private View line2;
	private Button btnRegistro;	
	
	private SpinnerArrayAdapter spinnerTipoDocumentoAdapter;
	
	private String responseType;
	

	//Se inicializan todos los widgets del formulario de Registro de Usuarios
	// PARA OBTENER LOS DATOS DE COMPLETITUD DENTRO DE LOS SPINNERS:
	// Se crean Adapters espec�ficos (SpinnerArrayAdapter.java) enviando como parametros el Context y
	// la tabla de completitud respectiva, para luego desplegar el Spinner con estos datos
	// y finalmente proceder a la asignacion del SpinnerArrayAdapter en el Spinner respectivo
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.registro);
		
		logoCongreso = (ImageView) findViewById( R.id.logo_congreso );
		
		textViewTipoDocumento = (TextView) findViewById( R.id.textview_tipo_documento );
		
		spinnerTipoDocumento = (Spinner) findViewById(R.id.spinner_tipo_documento);
		spinnerTipoDocumentoAdapter = new SpinnerArrayAdapter( RegistroActivity.this, SpinnerRestClientTask.TABLA_TIPO_DOC );
		spinnerTipoDocumento.setAdapter( spinnerTipoDocumentoAdapter );
		
		txtNumDocumento = (EditText) findViewById( R.id.txt_num_documento );
		
		txtNombre = (EditText) findViewById( R.id.txt_nombre );
		
		txtApellidos = (EditText) findViewById( R.id.txt_apellidos );
		
		textViewGenero = (TextView) findViewById( R.id.textview_genero );
		
		radioGroupGenero = (RadioGroup) findViewById( R.id.radio_group_genero );
		
		txtEmail = (EditText) findViewById( R.id.txt_email );
		
		txtTelefono = (EditText) findViewById( R.id.txt_telefono );
		
		textViewPaisProcedencia = (TextView) findViewById( R.id.textview_pais_procedencia );
		
		spinnerPaisProcedencia = (Spinner) findViewById(R.id.spinner_pais_procedencia);
		SpinnerArrayAdapter spinnerPaisProcedenciaAdapter = new SpinnerArrayAdapter( RegistroActivity.this, SpinnerRestClientTask.TABLA_PAIS_PROCEDENCIA );
		spinnerPaisProcedencia.setAdapter( spinnerPaisProcedenciaAdapter );
		
		textViewInstitucion = (TextView) findViewById( R.id.textview_institucion );
		
		spinnerInstitucion = (Spinner) findViewById(R.id.spinner_institucion);
		SpinnerArrayAdapter spinnerInstitucionAdapter = new SpinnerArrayAdapter( RegistroActivity.this, SpinnerRestClientTask.TABLA_INSTITUCION );
		spinnerInstitucion.setAdapter( spinnerInstitucionAdapter );
		
		textViewFechaNacimiento = (TextView) findViewById( R.id.textview_fecha_nacimiento );
		
		pickerFechaNacimiento = (DatePicker) findViewById( R.id.picker_fecha_nacimiento );				
		
		line2 = findViewById( R.id.line2 );
		
		btnRegistro = (Button) findViewById( R.id.btn_registro_registro_activity );
		btnRegistro.setOnTouchListener( this );
		btnRegistro.setOnClickListener( this );	
		
		
		//Se capturan los datos proporcionados por el login con redes sociales
		//a partir de bundleParams que envia LoginActivity.inicioRegistro();
		Bundle bundleParamsLogin =  getIntent().getExtras();
		//Si el Bundle no tiene parametros, es porque se realiz� un response nativo (NATIVE_RESPONSE)
		//es decir se hizo click en el boton "Registrar"
		if ( bundleParamsLogin.isEmpty() ){
			
		}
		else{
			this.responseType = bundleParamsLogin.getString( CCMPreferences.CAMPO_LOGIN_TYPE );
			
			if ( !this.responseType.equals( LoginActivity.NATIVE_LOGIN ) ){
				obtenerDatosLoginRedesSociales( bundleParamsLogin );
				new AlertDialog.Builder(this)
				.setMessage( getResources().getString(R.string.alert_registro_redes_sociales) )
				.setPositiveButton(R.string.alert_ok, null)
				.show();
			}
		}
	}
	
	
	
	//M�todo para capturar los datos proporcionados por las redes sociales
	//en caso de que el usuario haya accedido a la app por estas opciones
	//Este m�todo es llamado 
	private void obtenerDatosLoginRedesSociales( Bundle bundleParams ){
		String nombre = "";
		String apellidos = "";
		String genero = "";
		String[] fechaNacimientoPartida = {};
		String email = "";
				
		if ( this.responseType.equals( LoginActivity.FACEBOOK_LOGIN ) ){
			nombre = bundleParams.getString( LoginActivity.FB_NOMBRE );
			apellidos = bundleParams.getString( LoginActivity.FB_APELLIDOS );
			genero = bundleParams.getString( LoginActivity.FB_GENERO );
			fechaNacimientoPartida = bundleParams.getStringArray( LoginActivity.FB_FECHA_NACIMIENTO ) != null ? bundleParams.getStringArray( LoginActivity.FB_FECHA_NACIMIENTO ) : new String[]{"01", "01", "1900"} ;
			email =  bundleParams.getString( LoginActivity.FB_EMAIL );
		}
		
	
			
		
		txtNombre.setText(  nombre  );
		//txtNombre.setEnabled(false);		
		
		txtApellidos.setText(  apellidos  );
		//txtApellidos.setEnabled(false);
		
		boolean radioButtonGeneroChecked = false;
		int numRadioButtons = radioGroupGenero.getChildCount();
		for ( int i=0; i<numRadioButtons; i++ ){
			RadioButton radioButton = (RadioButton) radioGroupGenero.getChildAt(i);
			if (  radioButton.getText().equals( genero )  ){
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
		
		pickerFechaNacimiento.updateDate(  Integer.valueOf(fechaNacimientoPartida[2]) , Integer.valueOf(fechaNacimientoPartida[0]) - 1, Integer.valueOf(fechaNacimientoPartida[1])  );
		//pickerFechaNacimiento.setEnabled(false);
		
		txtEmail.setText(  bundleParams.getString( email )  );
		//txtEmail.setEnabled( false );		
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
		//Cuando se va a regresar a la actividad de atras (LoginActivity) se llama el metodo
		//salir() del CCMActionBar para cerrar las sesiones abiertas (facebook, Google, Native)
		//y se limpian todas las preferencias
		//IMPORTANTE: Si se vacian primero las preferencias, al momento de llamar el metodo salir()
		//no se tendr�a el tipo de login almacenado en preferencias, por lo que no se podria verificar
		//por cu�l red social se hizo el login
		salir();
		new CCMPreferences(this).vaciar();
	}
	
	
	
	//M�todo para capturar el presionado del bot�n y cabiar el fondo del mismo
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


	
	
	//M�todo para capturar las pulsaciones de los botones. Provienen de la interfaz OnClickListener
	@Override
	public void onClick(View v) {
		if ( v.getId() == R.id.btn_registro_registro_activity ){
			boolean formularioOk = validarCampos();
			if ( formularioOk ){
				String numDocumentoCampo = txtNumDocumento.getText().toString();
				String nombreCampo = txtNombre.getText().toString();
				String apellidosCampo = txtApellidos.getText().toString();
				String correoCampo = txtEmail.getText().toString();
				String tipoResponse = this.responseType;
				String[] params = { numDocumentoCampo, nombreCampo, apellidosCampo, correoCampo, tipoResponse };
				LoginRestClientTask loginRestClientTask = new LoginRestClientTask( this, RegistroActivity.this );
				loginRestClientTask.setParams( params );
				loginRestClientTask.setMetodo( LoginRestClientTask.EXISTE_PERSONA );
				loginRestClientTask.execute();
			}
			else{
				AlertDialog dialogCamposVacios = new AlertDialog.Builder(this)
				.setTitle( "Error" )
				.setMessage( getResources().getString( R.string.err_campos_vacios ) )
				.setPositiveButton( getResources().getString(R.string.alert_ok), null)
				.setCancelable( true )
				.show();
			}
		}
		
	}
	
	
	private boolean validarCampos(){
		String numDocumentoCampo = txtNumDocumento.getText().toString(); 
		String nombreCampo = txtNombre.getText().toString();
		String apellidosCampo = txtApellidos.getText().toString();		
			int indiceGeneroCampo = radioGroupGenero.getCheckedRadioButtonId();
			String generoCampo = (  (RadioButton) radioGroupGenero.findViewById( indiceGeneroCampo )  ).getText().toString();		
		String emailCampo = txtEmail.getText().toString();		
		String telefonoCampo = txtTelefono.getText().toString();		
		String fechaNacimientoCampo = obtenerFechaCampo( pickerFechaNacimiento );
		
		if ( !numDocumentoCampo.equals("")  &&  !nombreCampo.equals("")  &&  !apellidosCampo.equals("")  &&
			 !emailCampo.equals("")  &&  !fechaNacimientoCampo.equals("")	){
			return true;
		}
		else{
			return false;
		}
	}
	
	
	//M�todo que captura los datos ingresados en el formulario de registro
	//y los almacena en el WebService mediante el llamado a la clase RestClientTask().execute
	// IMPORTANTE: para enviar los campos del formulario al WebService es necesario que los campos
	// coincidan con los mismos de la base de datos, por eso en el List<NameValuePair> parametros
	// las claves tienen el mismo nombre de los campos de la tabla Persona en la BD CCM_BD
	public void guardarDatosFormulario(){
		String numDocumentoCampo = txtNumDocumento.getText().toString(); 
		String nombreCampo = txtNombre.getText().toString();
		String apellidosCampo = txtApellidos.getText().toString();		
			int indiceGeneroCampo = radioGroupGenero.getCheckedRadioButtonId();   //Si este valor retorna -1, es porque no se ha seleccionado ningun campo
			String generoCampo = (  (RadioButton) radioGroupGenero.findViewById( indiceGeneroCampo )  ).getText().toString();		
		String emailCampo = txtEmail.getText().toString();		
		String telefonoCampo = txtTelefono.getText().toString();		
		String codigoQRCampo = null;
		String fechaNacimientoCampo = obtenerFechaCampo( pickerFechaNacimiento );
		String asistioCampo = RegistroRestClientTask.VALOR_ASISTIO_NO;
		int tipoDocumentoCampo =   obtenerIdSpinner(  ((String) spinnerTipoDocumento.getSelectedItem()).toString()    );		
		int paisProcedenciaCampo = obtenerIdSpinner(  ((String) spinnerPaisProcedencia.getSelectedItem()).toString()  );
		int institucionCampo =     obtenerIdSpinner(  ((String) spinnerInstitucion.getSelectedItem()).toString()      );
		
		List<NameValuePair> parametros = new ArrayList<NameValuePair>();
		parametros.add(  new BasicNameValuePair( RegistroRestClientTask.CAMPO_DOC_PERSONA, String.valueOf(numDocumentoCampo) )  									);
		parametros.add(  new BasicNameValuePair( RegistroRestClientTask.CAMPO_NOMBRE_PERSONA, nombreCampo )                     									);
		parametros.add(  new BasicNameValuePair( RegistroRestClientTask.CAMPO_APELLIDOS_PERSONA, apellidosCampo )               									);
		parametros.add(  new BasicNameValuePair( RegistroRestClientTask.CAMPO_GENERO_PERSONA, generoCampo )  														);
		parametros.add(  new BasicNameValuePair( RegistroRestClientTask.CAMPO_CORREO_ELECTRONICO_PERSONA, emailCampo )  											);
		parametros.add(  new BasicNameValuePair( RegistroRestClientTask.CAMPO_TELEFONO_PERSONA, telefonoCampo )  													);
		parametros.add(  new BasicNameValuePair( RegistroRestClientTask.CAMPO_TIPO_DOC_IDTIPO_DOC_PERSONA, String.valueOf(tipoDocumentoCampo)  ) 					);
		parametros.add(  new BasicNameValuePair( RegistroRestClientTask.CAMPO_PAIS_PROCEDENCIA_IDPAIS_PROCEDENCIA_PERSONA, String.valueOf(paisProcedenciaCampo)  ) 	);
		parametros.add(  new BasicNameValuePair( RegistroRestClientTask.CAMPO_INSTITUCION_IDINSTITUCION_PERSONA, String.valueOf(institucionCampo)  )  				);
		//parametros.add(  new BasicNameValuePair( RegistroRestClientTask.CAMPO_CODIGO_QR_PERSONA, codigoQRCampo )  													);
		parametros.add(  new BasicNameValuePair( RegistroRestClientTask.CAMPO_FECHA_NACIMIENTO_PERSONA, fechaNacimientoCampo )  									);
		parametros.add(  new BasicNameValuePair( RegistroRestClientTask.CAMPO_ASISTIO_PERSONA, asistioCampo )            	        								);
		
		
		
		new RegistroRestClientTask( this ).execute( parametros );
	}
	
	
	
	private int obtenerIdSpinner( String itemSeleccionado ){
		return Integer.valueOf(  itemSeleccionado.split( "\\." )[0]  );
	}
	
	
	
	
	private static String obtenerFechaCampo( DatePicker datePicker ){
		String fechaCapturada = "";		
		int dia = datePicker.getDayOfMonth();
		int mes = datePicker.getMonth() + 1;
		int a�o = datePicker.getYear();
		fechaCapturada = a�o + "-" + mes + "-" + dia;
		//Log.v( "Fecha Capturada: ", fechaCapturada );
		return fechaCapturada;
	}

	
	
	
}
