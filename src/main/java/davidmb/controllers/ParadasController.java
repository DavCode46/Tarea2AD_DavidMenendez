package davidmb.controllers;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import davidmb.models.Parada;
import davidmb.models.Peregrino;
import davidmb.services.ParadasService;

public class ParadasController {

	private ParadasService paradasService;
	
	public ParadasController() {
		this(new ParadasService());
	}
	
	public ParadasController(ParadasService paradasService) {
		super();
		this.paradasService = paradasService;
	}
	
	public Optional<Long> insertarParada(Parada parada) {
		return paradasService.insertarParada(parada);
	}
	
	public Optional<Long> insertarPeregrinosParadas(Long idPeregrino, Long idParada, LocalDate fecha) {
		return paradasService.insertarPeregrinosParadas(idPeregrino, idParada, fecha);
	}
	
	public List<Parada> obtenerParadasPorIdPeregrino(Long idPeregrino) {
        return paradasService.obtenerParadasPorIdPeregrino(idPeregrino);
    }
	
	public Optional<Parada> obtenerParadaPorId(Long id) {
		return paradasService.obtenerParadaPorId(id);
	}
	
	public Optional<Parada> obtenerParadaPorIdUsuario(Long idUsuario) {
		return paradasService.obtenerParadaPorIdUsuario(idUsuario);
	}
	
	public Optional<Parada> obtenerParadaPorNombre(String nombre) {
		return paradasService.obtenerParadaPorNombre(nombre);
	}
	
	// No utilizado
	public List<Peregrino> obtenerPeregrinosParada(Long idParada) {
		return paradasService.obtenerPeregrinosParada(idParada);
	}
	
	public List<Parada> obtenerTodasParadas() {
		return paradasService.obtenerTodasParadas();
	}
	
	public boolean paradaExiste(String nombre) {
		return paradasService.paradaExiste(nombre);
	}
}
