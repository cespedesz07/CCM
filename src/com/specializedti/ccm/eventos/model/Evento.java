package com.specializedti.ccm.eventos.model;

public class Evento{
	
	
	public String idEvento;
	public String nombre;
	public String descripcion;
	public String tipoEvento;
	
	
	public Evento( String idEvento, String nombre, String descripcion, String tipoEvento ){
		this.idEvento = idEvento;
		this.nombre = nombre;
		this.descripcion = descripcion;
		this.tipoEvento = tipoEvento;
	}
	
	public String toString(){
		return "[" + idEvento + ", " + nombre + ", " + descripcion + ", " + tipoEvento + "]";
	}
	
	

}
