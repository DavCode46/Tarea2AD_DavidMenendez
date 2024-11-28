package davidmb.controllers;

import java.util.List;
import java.util.Optional;

import davidmb.models.Peregrino;
import davidmb.services.PeregrinosService;

/**
 * Controlador para gestionar las operaciones relacionadas con los peregrinos.
 * 
 * <p>Este controlador interactúa con el servicio {@link PeregrinosService} para realizar
 * operaciones como insertar peregrinos, obtener peregrinos por diferentes criterios y verificar la existencia de peregrinos.</p>
 */
public class PeregrinosController {

    private PeregrinosService peregrinosService;

    /**
     * Constructor por defecto que inicializa el controlador con un servicio de peregrinos.
     */
    public PeregrinosController() {
        this(new PeregrinosService());
    }

    /**
     * Constructor que permite inicializar el controlador con un servicio específico de peregrinos.
     * 
     * @param peregrinosService el servicio de peregrinos a utilizar.
     */
    public PeregrinosController(PeregrinosService peregrinosService) {
        super();
        this.peregrinosService = peregrinosService;
    }

    /**
     * Inserta un nuevo peregrino en el sistema.
     * 
     * <p>Este método interactúa con el servicio de peregrinos para realizar la inserción
     * de un peregrino y devuelve el ID del peregrino insertado.</p>
     * 
     * @param p el peregrino que se desea insertar.
     * @return un {@link Optional} que contiene el ID del peregrino insertado si la operación es exitosa,
     *         o un {@link Optional} vacío si la inserción falla.
     */
    public Optional<Long> insertarPeregrino(Peregrino p) {
        return peregrinosService.insertarPeregrino(p);
    }

    /**
     * Obtiene un peregrino por su ID.
     * 
     * <p>Este método permite recuperar un peregrino del sistema utilizando su ID único.</p>
     * 
     * @param id el ID del peregrino que se desea obtener.
     * @return un {@link Optional} que contiene el peregrino si se encuentra en el sistema,
     *         o un {@link Optional} vacío si no se encuentra.
     */
    public Optional<Peregrino> obtenerPeregrinoPorId(Long id) {
        return peregrinosService.obtenerPeregrinoPorId(id);
    }

    /**
     * Obtiene un peregrino asociado a un usuario específico mediante su ID de usuario.
     * 
     * <p>Este método permite obtener un peregrino utilizando el ID del usuario asociado.</p>
     * 
     * @param id el ID del usuario cuyo peregrino se desea obtener.
     * @return un {@link Optional} que contiene el peregrino asociado al usuario si existe,
     *         o un {@link Optional} vacío si no se encuentra.
     */
    public Optional<Peregrino> obtenerPeregrinoPorIdUsuario(Long id) {
        return peregrinosService.obtenerPeregrinoPorIdUsuario(id);
    }

    /**
     * Obtiene todos los peregrinos registrados en el sistema.
     * 
     * <p>Este método devuelve una lista con todos los peregrinos que están registrados en el sistema.</p>
     * 
     * @return una lista de todos los peregrinos.
     */
    public List<Peregrino> obtenerTodosPeregrinos() {
        return peregrinosService.obtenerTodosPeregrinos();
    }

    /**
     * Verifica si un peregrino existe en el sistema dado su ID.
     * 
     * <p>Este método permite verificar si un peregrino con un ID específico existe en el sistema.</p>
     * 
     * @param id el ID del peregrino a verificar.
     * @return {@code true} si el peregrino existe, {@code false} si no.
     */
    public boolean peregrinoExiste(Long id) {
        return peregrinosService.peregrinoExiste(id);
    }

    /**
     * Verifica si un peregrino existe en el sistema dado su nombre.
     * 
     * <p>Este método permite verificar si un peregrino con un nombre específico existe en el sistema.</p>
     * 
     * @param nombre el nombre del peregrino a verificar.
     * @return {@code true} si el peregrino existe, {@code false} si no.
     */
    public boolean nombrePeregrinoExiste(String nombre) {
        return peregrinosService.nombrePeregrinoExiste(nombre);
    }
}