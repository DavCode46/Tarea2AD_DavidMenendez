package davidmb.controllers;

import java.util.Optional;

import davidmb.models.Carnet;
import davidmb.services.CarnetsService;

/**
 * Controlador para gestionar las operaciones relacionadas con los carnets.
 * 
 * <p>Este controlador se encarga de interactuar con el servicio {@link CarnetsService} para realizar
 * operaciones como insertar y modificar carnets.</p>
 */
public class CarnetsController {

    private CarnetsService carnetService;
    
    /**
     * Constructor por defecto que inicializa el controlador con un servicio de carnets.
     */
    public CarnetsController() {
        this(new CarnetsService());
    }
    
    /**
     * Constructor que permite inicializar el controlador con un servicio específico de carnets.
     * 
     * @param carnetService el servicio de carnets a utilizar.
     */
    public CarnetsController(CarnetsService carnetService) {
        super();
        this.carnetService = carnetService;
    }
    
    /**
     * Inserta un nuevo carnet en el sistema.
     * 
     * <p>Este método llama al servicio {@link CarnetsService#insertarCarnet(Carnet)} para insertar
     * un carnet en la base de datos o almacenamiento.</p>
     * 
     * @param carnet el carnet a insertar.
     * @return un {@link Optional} que contiene el ID del carnet insertado si la operación es exitosa,
     *         o {@link Optional#empty()} si no se pudo insertar el carnet.
     */
    public Optional<Long> insertarCarnet(Carnet carnet) {
        return carnetService.insertarCarnet(carnet);
    }
    
    /**
     * Modifica un carnet existente en el sistema.
     * 
     * <p>Este método llama al servicio {@link CarnetsService#modificarCarnet(Carnet)} para modificar
     * un carnet ya existente en la base de datos o almacenamiento.</p>
     * 
     * @param carnet el carnet con los datos actualizados.
     * @return {@code true} si la modificación fue exitosa, {@code false} en caso contrario.
     */
    public boolean modificarCarnet(Carnet carnet) {
        return carnetService.modificarCarnet(carnet);
    }
}
