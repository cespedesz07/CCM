package com.specializedti.ccm.login;


import com.specializedti.ccm.R;
import com.parse.LogInCallback;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;
import com.specializedti.ccm.actionbar.CCMActionBarActivity;
import com.specializedti.ccm.application.CCMApplication;
import com.specializedti.ccm.eventos.AreaListActivity;
import com.specializedti.ccm.restclient.LoginRestClientTask;

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
public class LoginNativoActivity extends CCMActionBarActivity implements OnClickListener, OnTouchListener{
	
	
	//CONSTANTES
	private static final int REQUEST_CODE = 10;

	
	//ATRIBUTOS
	private EditText email;
	private EditText documento;
	private Button btnLogin;
	private AlertDialog.Builder dialogCamposVacios;
	private ProgressDialog dialogEspera;
	
	

		
	//MÉTODOS
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_email);

		email = (EditText) findViewById( R.id.email );
		documento = (EditText) findViewById( R.id.documento );		
		
		btnLogin = (Button) findViewById( R.id.btnLogin );
		btnLogin.setOnTouchListener( this );
		btnLogin.setOnClickListener( this );
		
		dialogCamposVacios = new AlertDialog.Builder(this);
		dialogCamposVacios.setTitle( "Error" );
		dialogCamposVacios.setPositiveButton( R.string.alert_ok, null);
		dialogCamposVacios.setCancelable( true );		
	}
	
	
	

	
	
	//Método que captura los eventos de los botones para ejecutar los Intents
	@Override
	public void onClick(View v){
		if ( hayInternet() ){
			String emailString = email.getText().toString();
			String documentoString = documento.getText().toString();	
			if ( !emailString.equals("") ){
				if ( !documentoString.equals("") ){
					switch ( v.getId() ){
					case R.id.btnLogin:
						String[] params = { documentoString, emailString, LoginActivity.NATIVE_LOGIN };
						LoginRestClientTask loginRestClientTask = new LoginRestClientTask( this, null );
						loginRestClientTask.setParams( params );
						loginRestClientTask.setMetodo( LoginRestClientTask.LOGIN_PERSONA );
						loginRestClientTask.execute();
						break;				
					default:
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
	}	
	
	
	
	
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
