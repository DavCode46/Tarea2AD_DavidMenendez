package davidmb.controllers;

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
	
	public Optional<Parada> obtenerParadaPorIdUsuario(Long idUsuario) {
		return paradasService.obtenerParadaPorIdUsuario(idUsuario);
	}
	
	public Optional<Parada> obtenerParadaPorNombre(String nombre) {
		return paradasService.obtenerParadaPorNombre(nombre);
	}
	
	
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