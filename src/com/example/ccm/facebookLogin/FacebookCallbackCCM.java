package com.example.ccm.facebookLogin;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;


/**
 * Callback de Facebook SDK v4 que se encarga de procesar el inicio de sesion exitoso, cancelado o con errores
 * @author Santiago Céspedes Zapata - cespedesz07@gmail.com
 *
 */
public class FacebookCallbackCCM implements FacebookCallback<LoginResult> {

	
	@Override
	public void onSuccess(LoginResult result) {
		
	}
	

	@Override
	public void onCancel() {
		// TODO Auto-generated method stub
		
	}
	

	@Override
	public void onError(FacebookException error) {
		// TODO Auto-generated method stub
		
	}

}
