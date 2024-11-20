package davidmb.services;

import java.util.List;

import davidmb.dao.EstanciaDAO;
import davidmb.models.Estancia;

public class EstanciasService {
	private EstanciaDAO estanciaDAO;
	
	public EstanciasService() {
        this(new EstanciaDAO());
	}
	
	public EstanciasService(EstanciaDAO estanciaDAO) {
		super();
		this.estanciaDAO = estanciaDAO;
	}
	
	public boolean insertarEstancia(Estancia estancia) {
		return estanciaDAO.insertarEstancia(estancia);
	}
	
	public List<Estancia> obtenerEstanciaPorIdPeregrino(Long idPeregrino) {
		return estanciaDAO.obtenerEstanciaPorIdPeregrino(idPeregrino);
	}
	
	public List<Estancia> obtenerEstanciasPorIdParada(Long idParada) {
		return estanciaDAO.obtenerEstanciasPorIdParada(idParada);
	}
}
