package davidmb.services;

import java.util.Optional;

import davidmb.dao.UsuarioDAO;
import davidmb.models.Usuario;

/**
 * Servicio para gestionar las operaciones relacionadas con los usuarios.
 * Utiliza la capa de acceso a datos {@link UsuarioDAO} para realizar las operaciones.
 */
public class UsuariosService {
    
    private UsuarioDAO usuarioDAO;

    /**
     * Constructor por defecto que crea una instancia de {@link UsuarioDAO}.
     */
    public UsuariosService() {
        this(new UsuarioDAO());
    }

    /**
     * Constructor que permite inyectar un {@link UsuarioDAO} personalizado.
     * 
     * @param usuarioDAO Objeto {@link UsuarioDAO} que se utilizará para las operaciones de base de datos.
     */
    public UsuariosService(UsuarioDAO usuarioDAO) {
        super();
        this.usuarioDAO = usuarioDAO;
    }

    /**
     * Inserta un nuevo usuario en la base de datos.
     * 
     * @param u El objeto {@link Usuario} a insertar.
     * @return Un {@link Optional} que contiene el ID del usuario insertado si la operación es exitosa, 
     *         o un {@link Optional#empty()} si no se pudo insertar el usuario.
     */
    public Optional<Long> insertarUsuario(Usuario u) {
        return usuarioDAO.insertarUsuario(u);
    }

    /**
     * Realiza el proceso de inicio de sesión para un usuario utilizando su nombre de usuario y contraseña.
     * 
     * @param nombreUsuario El nombre de usuario para iniciar sesión.
     * @param password La contraseña del usuario para iniciar sesión.
     * @return Un {@link Optional} que contiene el objeto {@link Usuario} si las credenciales son válidas, 
     *         o un {@link Optional#empty()} si las credenciales son incorrectas.
     */
    public Optional<Usuario> login(String nombreUsuario, String password) {
        return usuarioDAO.login(nombreUsuario, password);
    }

    /**
     * Verifica si un usuario existe en el sistema utilizando su nombre de usuario.
     * 
     * @param usuario El nombre de usuario a verificar.
     * @return {@code true} si el usuario existe, {@code false} si no existe.
     */
    public boolean usuarioExiste(String usuario) {
        return usuarioDAO.usuarioExiste(usuario);
    }

    /**
     * Valida las credenciales de un usuario (nombre de usuario y contraseña).
     * 
     * @param nombre El nombre de usuario a validar.
     * @param password La contraseña del usuario.
     * @return {@code true} si las credenciales son válidas, {@code false} si son incorrectas.
     */
    public boolean validarCredenciales(String nombre, String password) {
        return usuarioDAO.validarCredenciales(nombre, password);
    }
}
