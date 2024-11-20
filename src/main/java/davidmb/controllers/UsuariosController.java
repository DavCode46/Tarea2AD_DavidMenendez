package davidmb.controllers;

import davidmb.models.Usuario;
import davidmb.services.UsuariosService;

public class UsuariosController {
	UsuariosService usuariosService;
	
	public UsuariosController() {
		this.usuariosService = new UsuariosService();
	}
	
	
	public Long insertarUsuario(Usuario u) {
		return usuariosService.insertarUsuario(u);
	}
}
