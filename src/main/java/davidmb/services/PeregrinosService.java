package davidmb.services;

import java.util.List;
import java.util.Optional;

import davidmb.dao.PeregrinoDAO;
import davidmb.models.Peregrino;

public class PeregrinosService {
	private PeregrinoDAO peregrinoDAO;

	public PeregrinosService() {
		peregrinoDAO = new PeregrinoDAO();
	}

	public Optional<Long> insertarPeregrino(Peregrino p) {
		return peregrinoDAO.insertarPeregrino(p);
	}

	public Peregrino obtenerPeregrinoPorId(Long id) {
		return peregrinoDAO.obtenerPeregrinoPorId(id);
	}

	public Peregrino obtenerPeregrinoPorIdUsuario(Long id) {
		return peregrinoDAO.obtenerPeregrinoPorIdUsuario(id);
	}
	
	public List<Peregrino> obtenerTodosPeregrinos() {
		return peregrinoDAO.obtenerTodosPeregrinos();
	}
}
