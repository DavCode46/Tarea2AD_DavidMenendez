package davidmb.services;

import java.util.List;
import java.util.Optional;

import davidmb.dao.EstanciaDAO;
import davidmb.models.Estancia;

/**
 * Servicio para gestionar las operaciones relacionadas con las estancias.
 * Utiliza la capa de acceso a datos {@link EstanciaDAO} para realizar las operaciones.
 */
public class EstanciasService {

    private EstanciaDAO estanciaDAO;

    /**
     * Constructor por defecto que crea una instancia de {@link EstanciaDAO}.
     */
    public EstanciasService() {
        this(new EstanciaDAO());
    }

    /**
     * Constructor que permite inyectar un {@link EstanciaDAO} personalizado.
     * 
     * @param estanciaDAO Objeto {@link EstanciaDAO} que se utilizará para las operaciones de base de datos.
     */
    public EstanciasService(EstanciaDAO estanciaDAO) {
        super();
        this.estanciaDAO = estanciaDAO;
    }

    /**
     * Inserta una nueva estancia en la base de datos.
     * 
     * @param estancia El objeto {@link Estancia} a insertar.
     * @return Un {@link Optional} que contiene el ID de la estancia insertada si la operación es exitosa, 
     *         o un {@link Optional#empty()} si no se pudo insertar la estancia.
     */
    public Optional<Long> insertarEstancia(Estancia estancia) {
        return estanciaDAO.insertarEstancia(estancia);
    }

    /**
     * Obtiene una lista de estancias asociadas a un peregrino específico.
     * 
     * @param idPeregrino El ID del peregrino para el cual se desean obtener las estancias.
     * @return Una lista de estancias asociadas al peregrino, o una lista vacía si no hay estancias para el peregrino.
     */
    public List<Estancia> obtenerEstanciasPorIdPeregrino(Long idPeregrino) {
        return estanciaDAO.obtenerEstanciasPorIdPeregrino(idPeregrino);
    }

    /**
     * Obtiene una lista de estancias asociadas a una parada específica.
     * 
     * @param idParada El ID de la parada para la cual se desean obtener las estancias.
     * @return Una lista de estancias asociadas a la parada, o una lista vacía si no hay estancias para la parada.
     */
    public List<Estancia> obtenerEstanciasPorIdParada(Long idParada) {
        return estanciaDAO.obtenerEstanciasPorIdParada(idParada);
    }
}
