package davidmb.dao;

import java.util.ArrayList;
import java.util.List;

import davidmb.models.Parada;

public class ParadaDAO  {
	
	public void insertar(Parada parada) {
		System.out.println("Insertando parada");
	}

	public void modificar(Parada parada) {
		System.out.println("Modificando parada");
	}

	public Parada obtenerPorId(int id) {
		System.out.println("Obteniendo parada por id");
		return new Parada();
	}

	public List<Parada> obtenerTodos() {
		System.out.println("Obteniendo todas las paradas");
		return new ArrayList<Parada>();
	}

}
