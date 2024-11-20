package davidmb.controllers;

import java.util.Optional;

import davidmb.models.Usuario;
import davidmb.services.UsuariosService;

public class UsuariosController {
	UsuariosService usuariosService;
	
	public UsuariosController() {
		this(new UsuariosService());
	}
	
	public UsuariosController(UsuariosService usuariosService) {
		super();
		this.usuariosService = usuariosService;
	}
	
	
	public Optional<Long> insertarUsuario(Usuario u) {
		return usuariosService.insertarUsuario(u);
	}
	
	public Optional<Usuario> login(String nombreUsuario, String password) {
		return usuariosService.login(nombreUsuario, password);
	}
	
	public boolean usuarioExiste(String usuario) {
		return usuariosService.usuarioExiste(usuario);
	}
}
