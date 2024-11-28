package davidmb.controllers;

import java.util.List;
import java.util.Optional;

import davidmb.models.Estancia;
import davidmb.services.EstanciasService;

/**
 * Controlador para gestionar las operaciones relacionadas con las estancias.
 * 
 * <p>Este controlador interactúa con el servicio {@link EstanciasService} para realizar
 * operaciones como insertar estancias y obtener estancias según diferentes criterios.</p>
 */
public class EstanciasController {

    private EstanciasService estanciasService;
    
    /**
     * Constructor por defecto que inicializa el controlador con un servicio de estancias.
     */
    public EstanciasController() {
        this(new EstanciasService());
    }
    
    /**
     * Constructor que permite inicializar el controlador con un servicio específico de estancias.
     * 
     * @param estanciasService el servicio de estancias a utilizar.
     */
    public EstanciasController(EstanciasService estanciasService) {
        super();
        this.estanciasService = estanciasService;
    }
    
    /**
     * Inserta una nueva estancia en el sistema.
     * 
     * <p>Este método llama al servicio {@link EstanciasService#insertarEstancia(Estancia)} para insertar
     * una estancia en la base de datos o almacenamiento.</p>
     * 
     * @param estancia la estancia a insertar.
     * @return un {@link Optional} que contiene el ID de la estancia insertada si la operación es exitosa,
     *         o {@link Optional#empty()} si no se pudo insertar la estancia.
     */
    public Optional<Long> insertarEstancia(Estancia estancia) {
        return estanciasService.insertarEstancia(estancia);
    }
    
    /**
     * Obtiene una lista de estancias asociadas a un peregrino según su ID.
     * 
     * <p>Este método llama al servicio {@link EstanciasService#obtenerEstanciasPorIdPeregrino(Long)} para obtener
     * las estancias asociadas al ID de un peregrino específico.</p>
     * 
     * @param idPeregrino el ID del peregrino cuyas estancias se desean obtener.
     * @return una lista de estancias asociadas al peregrino.
     */
    public List<Estancia> obtenerEstanciasPorIdPeregrino(Long idPeregrino) {
        return estanciasService.obtenerEstanciasPorIdPeregrino(idPeregrino);
    }
    
    /**
     * Obtiene una lista de estancias asociadas a una parada según su ID.
     * 
     * <p>Este método llama al servicio {@link EstanciasService#obtenerEstanciasPorIdParada(Long)} para obtener
     * las estancias asociadas al ID de una parada específica.</p>
     * 
     * @param idParada el ID de la parada cuyas estancias se desean obtener.
     * @return una lista de estancias asociadas a la parada.
     * 
     * <p>Si no se encuentran estancias para la parada, se debe devolver una lista vacía en lugar de {@code null}.
     * Esto asegura que nunca se retorne un valor nulo, lo que previene posibles errores de ejecución.</p>
     */
    public List<Estancia> obtenerEstanciasPorIdParada(Long idParada) {
        return estanciasService.obtenerEstanciasPorIdParada(idParada);
    }
}
