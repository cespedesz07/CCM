package com.specializedti.ccm.eventos;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ListView;

import com.specializedti.ccm.R;
import com.specializedti.ccm.actionbar.CCMActionBarActivity;
import com.specializedti.ccm.restclient.CargaEventosRestClientTask;



public class TabFragment extends Fragment{	
	
	
	private String idTipoAreaActual; 
	private String pageTitle;
	
	private ListView listaEventos;
	
	
	public TabFragment( String idTipoAreaActual, String pageTitle ){
		this.idTipoAreaActual = idTipoAreaActual;
		this.pageTitle = pageTitle;
	}
	
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		super.onCreateView(inflater, container, savedInstanceState);
		//Log.i( "TabFragment", "OnCreateView" );
		View vistaFragment = inflater.inflate( R.layout.lista_eventos, container, false );
		listaEventos = (ListView) vistaFragment.findViewById( R.id.listview_lista_eventos );
		return vistaFragment;		
	}
	
	@Override
	public void onPause() {
		super.onPause();
		//Log.i( "TabFragment", "OnPause" );
		/*
		CargaEventosRestClientTask cargaEventosRestClientTask = new CargaEventosRestClientTask( getActivity(), listaEventos );
		cargaEventosRestClientTask.execute( idTipoAreaActual, pageTitle );
		*/
	}
	
	
	@Override
	public void onResume() {
		super.onResume();
		//Log.i( "TabFragment", "OnResume" );
		if ( hayInternet() ){
			CargaEventosRestClientTask cargaEventosRestClientTask = new CargaEventosRestClientTask( getActivity(), listaEventos );
			cargaEventosRestClientTask.execute( idTipoAreaActual, pageTitle );
		}
	}
	
	
    //Métdodo para verificar el estado de Internet
    //IMPORTANTE: Para verificar la conexion a Internet es necerario agregar el siguiente permiso en el Manifest:
    // <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    public boolean hayInternet(){
    	boolean respuesta = true;
    	ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService( Context.CONNECTIVITY_SERVICE );
 	    NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
 	    if ( netInfo == null ){
 	    	respuesta = false;
 	    }
 	    else if ( !netInfo.isConnected() ){
 	    	respuesta = false;
 	    }
 	    else if ( !netInfo.isAvailable() ){
 	    	respuesta = false;
 	    }
 	    else{
 	    	respuesta = true;
 	    }
 	    
 	    if ( !respuesta ){
 	    	new AlertDialog.Builder( getActivity() )
			.setMessage( getResources().getString(R.string.alert_no_internet) )
			.setPositiveButton( getResources().getString(R.string.alert_ok) , null)
			.show();
 	    }
 	    return respuesta;
    } 
	
}
















