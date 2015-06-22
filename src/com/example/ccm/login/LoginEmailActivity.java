package com.example.ccm.login;


import com.example.ccm.R;
import com.example.ccm.R.id;
import com.example.ccm.R.layout;
import com.example.ccm.R.string;
import com.example.ccm.application.CCMApplication;
import com.example.ccm.eventos.AreaListActivity;
import com.parse.LogInCallback;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;






/**
 * Actividad que corresponde al inicio de sesion nativo con el documento y el email del usuario
 * Implementa OnTuchListener para crear el efecto de presionado en los botones 
 * @author Santiago Céspedes Zapata - cespedesz07@gmail.com
 *
 */
public class LoginEmailActivity extends Activity implements OnClickListener, OnTouchListener{
	
	
	//CONSTANTES
	private static final int REQUEST_CODE = 10;

	
	//ATRIBUTOS
	private EditText email;
	private EditText documento;
	private Button btnLogin;
	private Button btnRegistro;
	private AlertDialog.Builder dialogCamposVacios;
	private ProgressDialog dialogEspera;
	
	

		
	//MÉTODOS
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_email);
		
		
		//Si el usuario actual es anónimo
		ParseUser usuarioActual = ParseUser.getCurrentUser();
		if ( usuarioActual != null) {
			if ( ParseAnonymousUtils.isLinked( usuarioActual ) ){
				//TODO: Hacer el intent para cuando el usuario sea anónimo
			}
			else{ 
				//Si el usuario no es anónimo
				//Abre directamente el AreaListActivity y el AreaDetailActivity para seleccionar los eventos
				//IMPORTANTE: Para abrir el master/detail, es necesario agregar en el Manifest lo siguiente:
				// <intent-filter>
                //     <action android:name="android.intent.action.MENUITEMLISTACTIVITY" />
                //     <category android:name="android.intent.category.DEFAULT" />
				// </intent-filter>
				Intent intent = new Intent( LoginEmailActivity.this, AreaListActivity.class  );
				startActivity( intent );
				finish();
			}
		}

		email = (EditText) findViewById( R.id.email );
		documento = (EditText) findViewById( R.id.documento );		
		
		btnLogin = (Button) findViewById( R.id.btnLogin );
		btnRegistro = (Button) findViewById( R.id.btnRegistro );
		btnLogin.setOnTouchListener( this );
		btnLogin.setOnClickListener( this );
		btnRegistro.setOnTouchListener( this );		
		btnRegistro.setOnClickListener( this );
		
		dialogCamposVacios = new AlertDialog.Builder(this);
		dialogCamposVacios.setTitle( "Error" );
		dialogCamposVacios.setPositiveButton( R.string.alert_ok, null);
		dialogCamposVacios.setCancelable( true );
		
		dialogEspera = new ProgressDialog( this );
		dialogEspera.setMessage( getString(R.string.alert_cargando) );
		
		
	}
	
	
	

	
	
	//Método que captura los eventos de los botones para ejecutar los Intents
	@Override
	public void onClick(View v){
		String emailString = email.getText().toString();
		String documentoString = documento.getText().toString();	
		Toast.makeText(this, emailString + ", " + documentoString, Toast.LENGTH_SHORT).show();	
		if ( !emailString.equals("") ){
			if ( !documentoString.equals("") ){
				switch ( v.getId() ){
				case R.id.btnLogin:
					dialogEspera.show();
					//login( emailString, documentoString );
					break;
				
				case R.id.btnRegistro:
					dialogEspera.show();
					//registrar( emailString, documentoString );
					break;
				}					
			}
			else{
				dialogCamposVacios.setMessage( R.string.err_doc_vacio );
				dialogCamposVacios.show();
			}
		}
		else{
			if ( !documentoString.equals("") ){
				dialogCamposVacios.setMessage( R.string.err_email_vacio );
				dialogCamposVacios.show();
			}
			else{
				dialogCamposVacios.setMessage( R.string.err_campos_vacios );
				dialogCamposVacios.show();
			}			
		}		
	}	
	
	/*
	private void login( String emailString, String documentoString ){
		dialogEspera.dismiss();
		ParseUser.logInInBackground( emailString, documentoString, new LogInCallback() {			
			@Override
			public void done(ParseUser user, ParseException exception) {
				if ( user != null ){
					Intent intent = new Intent( LoginEmailActivity.this, AreaListActivity.class  );
					startActivity( intent );
					finish();
				}
				else{
					dialogCamposVacios.setTitle( R.string.err_usuario_null );
					dialogCamposVacios.setMessage( exception.getMessage() );
					dialogCamposVacios.show();
				}
			}
		} );
	}
	
	
	private void registrar( String emailString, String documentoString ){
		dialogEspera.dismiss();
		ParseUser user = new ParseUser();
		user.setUsername( emailString );
		user.setPassword( documentoString );
		user.signUpInBackground( new SignUpCallback(){
			@Override
			public void done(ParseException exception) {
				if ( exception != null ){
					
				}
				else{
					dialogCamposVacios.setTitle( R.string.err_registro );
					dialogCamposVacios.setMessage( exception.getMessage() );
				}
			}			
		} );
	}
	*/
	
	
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 10 && resultCode == RESULT_OK)
			finish();
	}


	
	//Método para capturar el presionado del botón y cabiar el fondo del mismo
	//esto es para crear el efecto de presionado de los botones
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if ( event.getAction() == MotionEvent.ACTION_DOWN ){	//Si se presiona la pantalla...
			Drawable d = v.getBackground();						// se captura el background la vista presionada
			//d.mutate();
			d.setAlpha(150);									// se agrega transparencia al background
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
