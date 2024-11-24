package davidmb.services;

import java.util.Optional;

import davidmb.dao.UsuarioDAO;
import davidmb.models.Usuario;

public class UsuariosService {
	private UsuarioDAO usuarioDAO;
	
	public UsuariosService() {
        this(new UsuarioDAO());
	}
	
	public UsuariosService(UsuarioDAO usuarioDAO) {
		super();
		this.usuarioDAO = usuarioDAO;
	}
	
	public Optional<Long> insertarUsuario(Usuario u) {
		return usuarioDAO.insertarUsuario(u);
	}
	
	public Optional<Usuario> login(String nombreUsuario, String password) {
		return usuarioDAO.login(nombreUsuario, password);
	}
	
	public boolean usuarioExiste(String usuario) {
		return usuarioDAO.usuarioExiste(usuario);
	}
	
	public boolean validarCredenciales(String nombre, String password) {
		return usuarioDAO.validarCredenciales(nombre, password);
	}
	

}
