package davidmb.dao;

import java.util.ArrayList;
import java.util.List;

import davidmb.models.Estancia;

public class EstanciaDAO{
	
	public void insertar(Estancia estancia) {
		System.out.println("Insertando estancia");
	}

	public void modificar(Estancia estancia) {
		System.out.println("Modificando estancia");
	}

	public Estancia obtenerPorId(int id) {
		System.out.println("Obteniendo estancia por id");
		return new Estancia();
	}

	public List<Estancia> obtenerTodos() {
		System.out.println("Obteniendo todas las estancias");
		return new ArrayList<Estancia>();
	}
}
