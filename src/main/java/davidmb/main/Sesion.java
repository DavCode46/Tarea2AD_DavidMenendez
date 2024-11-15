package davidmb.main;

import java.util.Objects;

import davidmb.models.Perfil;

/**
 * Clase que representa la sesion de un usuario en el sistema
 * 
 */
public class Sesion {
	
	private String nombreUsuario;
	private Perfil perfil;
	private Long id;
	
	public Sesion() {
		super();
	}
	
	public Sesion(String nombreUsuario, Perfil perfil, Long id) {
		super();
		this.nombreUsuario = nombreUsuario;
		this.perfil = perfil;
		this.id = id;
	}

	public String getNombreUsuario() {
		return nombreUsuario;
	}

	public void setNombreUsuario(String nombreUsuario) {
		this.nombreUsuario = nombreUsuario;
	}

	

	public Perfil getPerfil() {
		return perfil;
	}

	public void setPerfil(Perfil perfil) {
		this.perfil = perfil;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, nombreUsuario, perfil);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Sesion other = (Sesion) obj;
		return Objects.equals(id, other.id) && Objects.equals(nombreUsuario, other.nombreUsuario)
				&& Objects.equals(perfil, other.perfil);
	}

	@Override
	public String toString() {
		return "Usuario [nombreUsuario=" + nombreUsuario + ", perfil=" + perfil + ", id=" + id + "]";
	}

	
}
