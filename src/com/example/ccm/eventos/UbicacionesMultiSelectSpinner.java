package com.example.ccm.eventos;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.support.v4.app.FragmentManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.ccm.R;
import com.example.ccm.preferences.CCMPreferences;
import com.example.ccm.restclient.GuardadoEventosUbicRestClientTask;

public class UbicacionesMultiSelectSpinner extends Spinner implements OnMultiChoiceClickListener {
	
	
	private static final String ITEM_NO_ASISTIRE_DEFAULT = "No Asistir�";
	private static final String ITEM_ASISTIRE = "Asistir� a %d ubicaci�n(es)";
	
	private Context context;
	private ArrayAdapter<String> adapter;
	private View itemView;
	private List<String> idItems;
	private String[] items;
	private ArrayList<String> cuposDisponibles;	
	private boolean[] selectedItems;	
	private String dialogTitle;
	
	private AlertDialog.Builder builder;
	private AlertDialog dialog;
	
	

	public UbicacionesMultiSelectSpinner(Context context, AttributeSet attrs ) {
		super(context, attrs);
		this.context = context;
		this.adapter = new ArrayAdapter<String>( context, android.R.layout.simple_spinner_dropdown_item );
		setAdapter( adapter );
	}
	
	
	public void setParentView( View parentView ){
		this.itemView = parentView;
	}
	
	
	
	//Metodo para asignar los Items que ser�n mostrados con su respectivo CheckBox
	public void setItems( ArrayList< ArrayList<String> > items ){
		this.idItems = items.get(0);
		this.items = items.get(1).toArray( new String[idItems.size()] );
		
		this.selectedItems = new boolean[ items.size() ];		
		Arrays.fill( selectedItems, false );					//Se indica que ningun elemento ha sido seleccionado...
		
		adapter.clear();
		adapter.add( getSelectedItemsString() );
		adapter.notifyDataSetChanged();		
	}
	
	
	//M�todo para asignar el T�tulo del Di�logo que se despliega al dar 
	//click en el Spinner ( EL d��logo se muestra en performClick() )
	public void setDialogTitle( String dialogTitle ){
		this.dialogTitle = dialogTitle;
	}
	
	
	//M�todo para asignar los items que tendran los checkboxes marcados
	public void setSelectedItems( List<String> idSelectedItems ){
		for ( String id : idSelectedItems ){
			for ( int i=0; i<items.length; i++ ){
				if (  idItems.get(i).equals( id )  ){
					selectedItems[i] = true;
				}
			}
		}
		adapter.clear();
		adapter.add( getSelectedItemsString() );
		adapter.notifyDataSetChanged();
	}
	
	
	//M�todo para mostrar en el spinner el numero de items seleccionados
	//del dialogo desplegado en performClick()
	public String getSelectedItemsString(){
		int count = countSelectedItems(selectedItems);
		if ( count == 0 ){
			this.itemView.setBackgroundResource( R.drawable.list_item_unselected );
			return ITEM_NO_ASISTIRE_DEFAULT;
		}
		else if ( count >= 1 ){
			this.itemView.setBackgroundResource( R.drawable.list_item_selected );
			return String.format( ITEM_ASISTIRE, count );
		}
		return "";
	}
	
	
	//Cuenta el n�mero de items seleccionados
	public int countSelectedItems( boolean[] selectedItems ){
		int count = 0;
		for ( boolean selectedItem : selectedItems ){
			if ( selectedItem ){
				count += 1;
			}
		}
		return count;
	}
	
	
	//Obtiene el ID de las ubicaciones que han sido seleccionadas
	private ArrayList<String> getIdSelectedItems(){
		ArrayList<String> idSelectedItems = new ArrayList<String>();
		for ( int i=0; i<items.length; i++ ){
			if ( selectedItems[i] == true ){
				idSelectedItems.add( idItems.get(i) );
			}
		}
		return idSelectedItems;
	}
	
	
	
	
	
	
	
	//M�todo que se llama cuando se presiona el Spinner para desplegar las Ubicaciones
	@SuppressLint("NewApi")
	@Override
	public boolean performClick(){
		this.builder = new AlertDialog.Builder( this.context ); 
		builder.setMultiChoiceItems( items, selectedItems, new DialogInterface.OnMultiChoiceClickListener() {
			
			@Override
			public void onClick(DialogInterface dialogInterface, int which, boolean isChecked) {
				//Si la ubicacion a seleccionar tiene 0 cupos disponibles
				//se desabilita la opcion de seleccionado
				//Log.v( "checked", String.valueOf(which) + ": " + isChecked);
				String itemRef = dialog.getListView().getItemAtPosition( which ).toString();
				String[] itemRefPartido = itemRef.split(" - ");
				String cuposLibres = itemRefPartido[ itemRefPartido.length - 1 ];
				Log.v( "Cupos Libres", cuposLibres );
				int cuposRef =  Integer.valueOf( cuposLibres.split(" ")[0] );
				Log.v( "Cupos Ref", String.valueOf( cuposRef ) );
				if ( cuposRef == 0  &&  isChecked ){
					dialog.getListView().getChildAt( which ).setEnabled( false );
					dialog.getListView().setItemChecked(which, false);
				}
				else{
					selectedItems[which] = isChecked;
					String docPersona = new CCMPreferences( context ).obtenerDocPersona();
					String tipoPersona = "4";
					String[] registro = { docPersona, idItems.get(which), tipoPersona };
					GuardadoEventosUbicRestClientTask guardadoEventosUbicRestClientTask = new GuardadoEventosUbicRestClientTask( context );
					if ( isChecked ){
						//Log.v( "a ingresar: " , Arrays.toString(registro));
						guardadoEventosUbicRestClientTask.setRegistroPersonaUbicacion( registro );
						guardadoEventosUbicRestClientTask.execute( GuardadoEventosUbicRestClientTask.CREAR_PERSONA_UBICACION );
						cuposRef -= 1;
					}
					else{
						//Log.v( "a eliminar: " , Arrays.toString(registro));
						guardadoEventosUbicRestClientTask.setRegistroPersonaUbicacion( registro );
						guardadoEventosUbicRestClientTask.execute( GuardadoEventosUbicRestClientTask.BORRAR_PERSONA_UBICACION );
						cuposRef += 1;
					}
					actualizarRegistroUbicacionListView(which, isChecked, cuposRef);
					adapter.clear();
					adapter.add( getSelectedItemsString() );
					adapter.notifyDataSetChanged();
				}
				
			}
		} );
		builder.setTitle( this.dialogTitle );
		
		this.dialog = builder.create();
		dialog.show();
		return false;
	}
	
	
	

	
	
	//=================================M�todos de OnMultiChoiceClickListener=========================
	//M�todo para controlar los eventos de Checkeado de Items
	@Override
	public void onClick(DialogInterface dialog, int which, boolean isChecked) {
		//Si la ubicacion a seleccionar tiene 0 cupos disponibles
		//se desabilita la opcion de seleccionado
		Log.v( "checked", String.valueOf(which) + ": " + isChecked);
		String itemRef = this.dialog.getListView().getItemAtPosition( which ).toString();
		String cuposLibres = itemRef.split(" - ")[2];
		int cuposRef =  Integer.valueOf( cuposLibres.split(" ")[0] );
		if ( cuposRef == 0  &&  isChecked ){
			this.dialog.getListView().getChildAt( which ).setEnabled( false );
			this.dialog.getListView().setItemChecked(which, false);
		}
		else{
			selectedItems[which] = isChecked;
			String docPersona = new CCMPreferences( this.context ).obtenerDocPersona();
			String tipoPersona = "4";
			String[] registro = { docPersona, idItems.get(which), tipoPersona };
			GuardadoEventosUbicRestClientTask guardadoEventosUbicRestClientTask = new GuardadoEventosUbicRestClientTask( context );
			if ( isChecked ){
				//Log.v( "a ingresar: " , Arrays.toString(registro));
				guardadoEventosUbicRestClientTask.setRegistroPersonaUbicacion( registro );
				guardadoEventosUbicRestClientTask.execute( GuardadoEventosUbicRestClientTask.CREAR_PERSONA_UBICACION );
				cuposRef -= 1;
			}
			else{
				//Log.v( "a eliminar: " , Arrays.toString(registro));
				guardadoEventosUbicRestClientTask.setRegistroPersonaUbicacion( registro );
				guardadoEventosUbicRestClientTask.execute( GuardadoEventosUbicRestClientTask.BORRAR_PERSONA_UBICACION );
				cuposRef += 1;
			}
			actualizarRegistroUbicacionListView(which, isChecked, cuposRef);
			adapter.clear();
			adapter.add( getSelectedItemsString() );
			adapter.notifyDataSetChanged();
		}
	}
	
	
	public void actualizarRegistroUbicacionListView( int position, boolean checkeado, int nuevosCupos ){
		String actual = this.dialog.getListView().getItemAtPosition(position).toString();		
		String[] partido = actual.split( " - " );
		String nuevoString = String.format("%s - %s - %s Libres", partido[0], partido[1], nuevosCupos );
		this.items[position] = nuevoString;
		adapter.clear();
		adapter.notifyDataSetChanged();
		
		dialog.dismiss();
		
		this.builder = new AlertDialog.Builder( this.context ); 
		builder.setMultiChoiceItems( items, selectedItems, this );
		builder.setTitle( this.dialogTitle );		
		this.dialog = builder.create();
		dialog.show();
	}


}
