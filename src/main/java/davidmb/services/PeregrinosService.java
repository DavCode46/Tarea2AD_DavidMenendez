package davidmb.services;

import java.util.List;
import java.util.Optional;

import davidmb.dao.PeregrinoDAO;
import davidmb.models.Peregrino;

public class PeregrinosService {
	private PeregrinoDAO peregrinoDAO;
	
	public PeregrinosService() {
        this(new PeregrinoDAO());
	}

	public PeregrinosService(PeregrinoDAO peregrinoDAO) {
		super();
		this.peregrinoDAO = peregrinoDAO;
	}

	public Optional<Long> insertarPeregrino(Peregrino p) {
		return peregrinoDAO.insertarPeregrino(p);
	}

	public Optional<Peregrino> obtenerPeregrinoPorId(Long id) {
		return peregrinoDAO.obtenerPeregrinoPorId(id);
	}

	public Optional<Peregrino> obtenerPeregrinoPorIdUsuario(Long id) {
		return peregrinoDAO.obtenerPeregrinoPorIdUsuario(id);
	}
	
	public List<Peregrino> obtenerTodosPeregrinos() {
		return peregrinoDAO.obtenerTodosPeregrinos();
	}
	
	public boolean peregrinoExiste(Long id) {
		return peregrinoDAO.peregrinoExiste(id);
	}
	
	public boolean nombrePeregrinoExiste(String nombre) {
		return peregrinoDAO.nombrePeregrinoExiste(nombre);
	}
}
