package davidmb.models;

import java.util.Objects;

public class Usuario {
	
	private Long id;
	private String nombreUsuario;
	private String password;
	private Perfil perfil;
	
	public Usuario() {
		super();
	}
	
	public Usuario(String nombreUsuario, Perfil perfil) {
		super();
		this.nombreUsuario = nombreUsuario;
		this.perfil = perfil;
	}
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getNombreUsuario() {
		return nombreUsuario;
	}
	
	public void setNombreUsuario(String nombreUsuario) {
		this.nombreUsuario = nombreUsuario;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public Perfil getPerfil() {
		return perfil;
	}
	
	public void setPerfil(Perfil perfil) {
		this.perfil = perfil;
	}
	
	@Override
	public String toString() {
		return "Usuario [id=" + id + ", nombreUsuario=" + nombreUsuario + ", password=" + password + ", perfil="
				+ perfil + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, nombreUsuario, password, perfil);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Usuario other = (Usuario) obj;
		return Objects.equals(id, other.id) && Objects.equals(nombreUsuario, other.nombreUsuario)
				&& Objects.equals(password, other.password) && Objects.equals(perfil, other.perfil);
	}
	
	
}
