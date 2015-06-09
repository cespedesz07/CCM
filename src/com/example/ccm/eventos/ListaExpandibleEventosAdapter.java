package com.example.ccm.eventos;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import com.example.ccm.R;
import com.example.ccm.R.id;
import com.example.ccm.R.layout;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ListaExpandibleEventosAdapter extends BaseExpandableListAdapter {
	
	
	private Context context;
	private ArrayList<String> grupos;
	private ArrayList<String[]> contenidos;
	private LayoutInflater inflater;
	
	
	
	/*
	 * Clase estática para almacenar los componentes del item_evento_child.xml e item_evento_group.xml
	 * Estos componentes son el nombre del evento y la hora del evento
	 * ESTA CLASE ES IMPORTANTE PARA EVITAR SOBRECARGAR LA BUSQUEDA POR findViewById() CADA VEZ
	 * QUE SE MUESTRA UNO DE ESTOS ITEMS EN PANTALLA AL MOVER EL LIST VIEW
	 */
	static class ViewHolder{
		TextView nombreEvento;
		TextView horaEvento;
		TextView descripcionEvento;
	}
	
	
	
	public ListaExpandibleEventosAdapter( Context context, LayoutInflater inflater, 
			ArrayList<String> grupos, ArrayList<String[]> contenidos){
		super();		
		this.context = context;
		this.inflater = inflater;		
		this.grupos = grupos;
		this.contenidos = contenidos;
	}

	
	
	
	
	
	
	
	
	
	/*
	 * Métodos para los elementos Child (Los que se despliegan)
	 */
	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return contenidos.get(groupPosition)[childPosition];
	}

	
	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	
	/*
	 * Siempre que exista algún layout que pueda ser reutilizado éste se va a recibir a través del parámetro convertView 
	 * del método getView(). De esta forma, en los casos en que éste no sea null podremos obviar el trabajo de inflar el 
	 * layout.
	 */
	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
		View itemEventoChild = convertView;			//ConvertView es la vista o layout del item_evento_child que se
													// ha desplegado previamente, (la primera vez esta va a ser null)
		
		ViewHolder holderChild;							//ViewHolder es la clase estática que va a almacenar la descripcion del
														// evento cuando se "infle" el layout del item_evento_child
		if ( itemEventoChild == null ){
			itemEventoChild = this.inflater.inflate( R.layout.item_evento_child, parent, false );
			
			holderChild = new ViewHolder();
			holderChild.descripcionEvento = (TextView) itemEventoChild.findViewById( R.id.textViewDescripcionEvento );
			
			itemEventoChild.setTag( holderChild );
		}
		else{
			holderChild = (ViewHolder) itemEventoChild.getTag();
		}		
		holderChild.descripcionEvento.setText( this.contenidos.get(groupPosition)[childPosition] );
		
		return itemEventoChild;
	}
	

	@Override
	public int getChildrenCount(int groupPosition) {
		return contenidos.get(groupPosition).length;
	}

	
	
	
	
	
	
	
	/*
	 * Métodos para los elementos Group (Los Titulos principales)
	 */
	@Override
	public Object getGroup(int groupPosition) {
		return grupos.get(groupPosition);		
	}

	@Override
	public int getGroupCount() {
		return grupos.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	
	/*
	 * Siempre que exista algún layout que pueda ser reutilizado éste se va a recibir a través del parámetro convertView 
	 * del método getView(). De esta forma, en los casos en que éste no sea null podremos obviar el trabajo de inflar el 
	 * layout.
	 */
	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
		View itemEventoGroup = convertView;
		ViewHolder holderGroup;
		if ( itemEventoGroup == null ){
			itemEventoGroup = this.inflater.inflate( R.layout.item_evento_group, parent, false );
			
			holderGroup = new ViewHolder();
			holderGroup.nombreEvento = (TextView) itemEventoGroup.findViewById( R.id.textViewNombreEvento );
			holderGroup.horaEvento = (TextView) itemEventoGroup.findViewById( R.id.textViewHoraEvento );
			
			itemEventoGroup.setTag( holderGroup );
		}
		else{
			holderGroup = (ViewHolder) itemEventoGroup.getTag();
		}		
		
		holderGroup.nombreEvento.setText( this.grupos.get( groupPosition ) );
		holderGroup.horaEvento.setText( "12:00 PM" );
		
		return itemEventoGroup;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

}
