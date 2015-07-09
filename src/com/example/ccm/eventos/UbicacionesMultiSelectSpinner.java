package com.example.ccm.eventos;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.example.ccm.R;
import com.example.ccm.preferences.CCMPreferences;
import com.example.ccm.restclient.GuardadoEventosUbicRestClientTask;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.util.AttributeSet;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class UbicacionesMultiSelectSpinner extends Spinner implements OnMultiChoiceClickListener, OnClickListener {
	
	
	private static final String ITEM_NO_ASISTIRE_DEFAULT = "No Asistiré";
	private static final String ITEM_ASISTIRE = "Asistiré a %d ubicación(es)";
	
	private Context context;
	private ArrayAdapter<String> adapter;
	private List<String> idItems;
	private String[] items;	
	private boolean[] selectedItems;	
	private String dialogTitle;
	
	
	

	public UbicacionesMultiSelectSpinner(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		this.adapter = new ArrayAdapter<String>( context, android.R.layout.simple_spinner_dropdown_item );
		setAdapter( adapter );
	}
	
	
	
	
	public void setItems( ArrayList< ArrayList<String> > items ){
		this.idItems = items.get(0);
		this.items = items.get(1).toArray( new String[idItems.size()] );
		this.selectedItems = new boolean[ items.size() ];
		Arrays.fill( selectedItems, false );					//Se indica que ningun elemento ha sido seleccionado...
		adapter.clear();
		adapter.add( getSelectedItemsString() );
		adapter.notifyDataSetChanged();
	}
	
	
	
	public void setDialogTitle( String dialogTitle ){
		this.dialogTitle = dialogTitle;
	}
	
	
	
	public void setSelectedItems( List<String> selection ){
		for ( String selectedItem : selection ){
			for ( int i=0; i<items.length; i++ ){
				if (  items[i].equals( selectedItem )  ){
					selectedItems[i] = true;
				}
			}
		}
		adapter.clear();
		adapter.add( getSelectedItemsString() );
		adapter.notifyDataSetChanged();
	}
	
	
	
	public String getSelectedItemsString(){
		int count = countSelectedItems(selectedItems);
		if ( count == 0 ){
			return ITEM_NO_ASISTIRE_DEFAULT;
		}
		else if ( count >= 1 ){
			return String.format( ITEM_ASISTIRE, count );
		}
		return "";
	}
	
	
	
	public int countSelectedItems( boolean[] selectedItems ){
		int count = 0;
		for ( boolean selectedItem : selectedItems ){
			if ( selectedItem ){
				count += 1;
			}
		}
		return count;
	}
	
	
	private ArrayList<String> getIdSelectedItems(){
		ArrayList<String> idSelectedItems = new ArrayList<String>();
		for ( int i=0; i<items.length; i++ ){
			if ( selectedItems[i] == true ){
				idSelectedItems.add( idItems.get(i) );
			}
		}
		return idSelectedItems;
	}
	
	//Método que captura las ubicaciones a las cuales va a asistir la persona
	//mediante el siguiente formato: [idUbicacion, docPersona, tipoPersona]
	//y las almacena en un ArrayList para ser procesadas por guardadoEventosUbicRestClientTask.agregarRegistrosPersonaUbicacion()
	/*
	private void almacenarRegistrosPersonaUbicacion(){
		ArrayList<String[]> registros = new ArrayList<String[]>();
		ArrayList<String> idUbicaciones = getIdSelectedItems();
		String docPersona = new CCMPreferences( this.context ).obtenerDocPersona();
		String tipoPersona = "4";
		for ( String idUbicacion : idUbicaciones ){
			String[] registro = { idUbicacion, docPersona, tipoPersona };
			registros.add( registro );
		}
		guardadoEventosUbicRestClientTask.agregarRegistrosPersonaUbicacion( registros );
		guardadoEventosUbicRestClientTask.execute( GuardadoEventosUbicRestClientTask.CREAR_PERSONA_UBICACION );
		
	}
	*/
	
	
	
	
	
	
	
	//Método que se llama cuando se presiona el Spinner para desplegar las Ubicaciones
	@Override
	public boolean performClick(){
		AlertDialog.Builder builder = new AlertDialog.Builder( getContext() ); 
		builder.setMultiChoiceItems( items, selectedItems, this );
		builder.setTitle( this.dialogTitle );
		/*
		builder.setPositiveButton( context.getResources().getString(R.string.guardar), new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch ( which ){
				case DialogInterface.BUTTON_POSITIVE:
					almacenarRegistrosPersonaUbicacion();
					break;
				case DialogInterface.BUTTON_NEGATIVE:
					break;
				}				
			}			
		} );
		builder.setNegativeButton( context.getResources().getString(R.string.cancelar) , null);
		*/
		builder.show();
		return false;
	}
	
	
	

	
	
	//=================================Métodos de OnMultiChoiceClickListener=========================
	//Método para controlar los eventos de Checkeado de Items
	@Override
	public void onClick(DialogInterface dialog, int which, boolean isChecked) {
		selectedItems[which] = isChecked;
		String docPersona = new CCMPreferences( this.context ).obtenerDocPersona();
		String tipoPersona = "4";
		String[] registro = { idItems.get(which), docPersona, tipoPersona };
		GuardadoEventosUbicRestClientTask guardadoEventosUbicRestClientTask = new GuardadoEventosUbicRestClientTask( context );
		if ( isChecked ){
			guardadoEventosUbicRestClientTask.agregarRegistroPersonaUbicacion( registro );
			guardadoEventosUbicRestClientTask.execute( GuardadoEventosUbicRestClientTask.CREAR_PERSONA_UBICACION );
		}
		else{
			guardadoEventosUbicRestClientTask.agregarRegistroPersonaUbicacion( registro );
			guardadoEventosUbicRestClientTask.execute( GuardadoEventosUbicRestClientTask.BORRAR_PERSONA_UBICACION );
		}
		adapter.clear();
		adapter.add( getSelectedItemsString() );
		adapter.notifyDataSetChanged();
	}


}
