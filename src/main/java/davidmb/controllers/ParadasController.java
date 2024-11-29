package davidmb.controllers;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import davidmb.models.Parada;
import davidmb.models.Peregrino;
import davidmb.services.ParadasService;

/**
 * Controlador para gestionar las operaciones relacionadas con las paradas.
 * 
 * <p>Este controlador interactúa con el servicio {@link ParadasService} para realizar
 * operaciones como insertar paradas, obtener paradas por diferentes criterios y verificar la existencia de paradas.</p>
 */
public class ParadasController {

    private ParadasService paradasService;

    /**
     * Constructor por defecto que inicializa el controlador con un servicio de paradas.
     */
    public ParadasController() {
        this(new ParadasService());
    }

    /**
     * Constructor que permite inicializar el controlador con un servicio específico de paradas.
     * 
     * @param paradasService el servicio de paradas a utilizar.
     */
    public ParadasController(ParadasService paradasService) {
        super();
        this.paradasService = paradasService;
    }

    /**
     * Inserta una nueva parada en el sistema.
     * 
     * <p>Este método interactúa con el servicio de paradas para realizar la inserción
     * de una parada y devuelve el ID de la parada insertada.</p>
     * 
     * @param parada la parada que se desea insertar.
     * @return un {@link Optional} que contiene el ID de la parada insertada si la operación es exitosa,
     *         o un {@link Optional} vacío si la inserción falla.
     */
    public Optional<Long> insertarParada(Parada parada) {
        return paradasService.insertarParada(parada);
    }

    /**
     * Inserta una relación entre un peregrino y una parada, registrando la fecha de la parada.
     * 
     * <p>Este método permite asociar un peregrino a una parada en una fecha específica.</p>
     * 
     * @param idPeregrino el ID del peregrino a asociar.
     * @param idParada el ID de la parada.
     * @param fecha la fecha de la parada.
     * @return un {@link Optional} que contiene el ID de la relación insertada si la operación es exitosa,
     *         o un {@link Optional} vacío si la inserción falla.
     */
    public Optional<Long> insertarPeregrinosParadas(Long idPeregrino, Long idParada, LocalDate fecha) {
        return paradasService.insertarPeregrinosParadas(idPeregrino, idParada, fecha);
    }

    /**
     * Obtiene una lista de paradas asociadas a un peregrino específico.
     * 
     * <p>Este método permite obtener todas las paradas realizadas por un peregrino dado su ID.</p>
     * 
     * @param idPeregrino el ID del peregrino cuyas paradas se desean obtener.
     * @return una lista de paradas asociadas al peregrino.
     */
    public List<Parada> obtenerParadasPorIdPeregrino(Long idPeregrino) {
        return paradasService.obtenerParadasPorIdPeregrino(idPeregrino);
    }

    /**
     * Obtiene una parada por su ID.
     * 
     * <p>Este método permite recuperar una parada del sistema utilizando su ID único.</p>
     * 
     * @param id el ID de la parada que se desea obtener.
     * @return un {@link Optional} que contiene la parada si se encuentra en el sistema,
     *         o un {@link Optional} vacío si no se encuentra.
     */
    public Optional<Parada> obtenerParadaPorId(Long id) {
        return paradasService.obtenerParadaPorId(id);
    }

    /**
     * Obtiene una parada asociada a un usuario específico mediante su ID de usuario.
     * 
     * <p>Este método permite obtener una parada utilizando el ID del usuario asociado.</p>
     * 
     * @param idUsuario el ID del usuario cuya parada se desea obtener.
     * @return un {@link Optional} que contiene la parada asociada al usuario si existe,
     *         o un {@link Optional} vacío si no se encuentra.
     */
    public Optional<Parada> obtenerParadaPorIdUsuario(Long idUsuario) {
        return paradasService.obtenerParadaPorIdUsuario(idUsuario);
    }

    /**
     * Obtiene una parada por su nombre.
     * 
     * <p>Este método permite obtener una parada utilizando su nombre.</p>
     * 
     * @param nombre el nombre de la parada que se desea obtener.
     * @return un {@link Optional} que contiene la parada si se encuentra en el sistema,
     *         o un {@link Optional} vacío si no se encuentra.
     */
    public Optional<Parada> obtenerParadaPorNombre(String nombre) {
        return paradasService.obtenerParadaPorNombre(nombre);
    }

    /**
     * Obtiene los peregrinos asociados a una parada específica.
     * 
     * <p>Este método devuelve una lista de peregrinos asociados a una parada dada su ID.</p>
     * 
     * @param idParada el ID de la parada cuyos peregrinos se desean obtener.
     * @return una lista de peregrinos asociados a la parada.
     */
    public List<Peregrino> obtenerPeregrinosParada(Long idParada) {
        return paradasService.obtenerPeregrinosParada(idParada);
    }

    /**
     * Obtiene todas las paradas disponibles en el sistema.
     * 
     * <p>Este método devuelve una lista con todas las paradas registradas en el sistema.</p>
     * 
     * @return una lista de todas las paradas.
     */
    public List<Parada> obtenerTodasParadas() {
        return paradasService.obtenerTodasParadas();
    }

    /**
     * Verifica si una parada existe en el sistema dado su nombre en caso de ser peregrino 
     * o su nombre y región en caso de ser admin.
     * 
     * <p>Este método permite verificar si una parada con un nombre específico existe en el sistema.</p>
     * 
     * @param nombre el nombre de la parada a verificar.
     * @param region La regrión de la parada a verificar
     * @esAdmin boolean para verificar si es admin o no
     * @return {@code true} si la parada existe, {@code false} si no.
     */
    public boolean paradaExiste(String nombre, String region, boolean esAdmin) {
        return paradasService.paradaExiste(nombre, region, esAdmin);
    }
}
