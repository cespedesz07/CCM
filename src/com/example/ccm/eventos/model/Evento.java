package com.example.ccm.eventos.model;

public class Evento{
	
	
	public String idEvento;
	public String nombre;
	public String descripcion;
	
	
	public Evento( String idEvento, String nombre, String descripcion ){
		this.idEvento = idEvento;
		this.nombre = nombre;
		this.descripcion = descripcion;
	}
	
	public String toString(){
		return "[" + idEvento + ", " + nombre + ", " + descripcion + "]";
	}
	
	

}
