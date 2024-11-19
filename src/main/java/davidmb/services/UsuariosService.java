package davidmb.services;

import davidmb.dao.UsuarioDAO;
import davidmb.models.Usuario;

public class UsuariosService {
	private UsuarioDAO usuarioDAO;
	
	public UsuariosService() {
		usuarioDAO = new UsuarioDAO();
	}
	
	public Long insertarUsuario(Usuario u) {
		return usuarioDAO.insertarUsuario(u);
	}
	
	public Usuario login(String nombreUsuario, String password) {
		return usuarioDAO.login(nombreUsuario, password);
	}
}
