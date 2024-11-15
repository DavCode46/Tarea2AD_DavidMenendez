package davidmb.dao;

import java.util.ArrayList;
import java.util.List;

import davidmb.models.Carnet;

public class CarnetDAO {

	public void insertar(Carnet carnet) {
		System.out.println("Insertando carnet");
	}

	public void modificar(Carnet carnet) {
		System.out.println("Modificando carnet");
	}

	public Carnet obtenerPorId(int id) {
		System.out.println("Obteniendo carnet por id");
		return new Carnet();
	}

	public List<Carnet> obtenerTodos() {
		System.out.println("Obteniendo todos los carnets");
		return new ArrayList<Carnet>();
	}
}
