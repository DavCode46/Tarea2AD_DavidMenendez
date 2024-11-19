package davidmb.services;

import java.util.List;

import davidmb.dao.EstanciaDAO;
import davidmb.models.Estancia;

public class EstanciasService {
	private EstanciaDAO estanciaDAO;
	
	public EstanciasService() {
		estanciaDAO = new EstanciaDAO();
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
