package davidmb.main;



import java.util.Optional;


import javax.swing.JOptionPane;

import davidmb.controllers.ControladorPrincipal;
import davidmb.controllers.ExportarCarnetXML;

import davidmb.models.Parada;
import davidmb.models.Peregrino;
import davidmb.models.Perfil;
import davidmb.models.Usuario;


/**
 * Clase principal del sistema de gestión de peregrinos y paradas. Proporciona
 * el menú principal y las opciones para iniciar sesión, registrarse como
 * peregrino, y gestionar funcionalidades de usuario según el perfil. Se integra
 * con la clase {@link ControladorPrincipal} para la gestión de usuarios y paradas.
 */
public class Principal {

	static Usuario userActivo = new Usuario("Invitado", Perfil.invitado);

	/**
	 * Método principal que inicia el programa mostrando el menú principal.
	 *
	 * @param args Argumentos de la línea de comandos (no se utilizan).
	 */
	public static void main(String[] args) {
		
		mostrarMenu();
	}

	/**
	 * Muestra el menú principal, permitiendo a los usuarios iniciar sesión,
	 * registrarse como peregrino, o salir del sistema. Dependiendo del perfil, se
	 * habilitan opciones específicas para cada usuario.
	 */
	private static void mostrarMenu() {

		ControladorPrincipal sistema = new ControladorPrincipal();

		Peregrino p = null;
		Parada parada = null;
			
		String opcion = "";

		do {
			String menu = "1. Login\n" + "2. Registrarse como peregrino\n" + "3. Salir\n";

			opcion = sistema.obtenerEntrada(menu, "Selecciona una opción", true, false);

			if (opcion == null) {
				JOptionPane.showMessageDialog(null, "Selecciona una opción.");
				continue;
			}

			switch (opcion) {
			case "1": {

				String nombreUsuario = "";
				String contrasenia = "";

				MENU: do {

					nombreUsuario = sistema.obtenerEntrada("Ingrese su nombre de usuario", "Nombre de usuario", false, true);

					if (nombreUsuario == null) {
						break;
					}

					contrasenia = sistema.obtenerEntrada("Ingrese su contraseña", "Contraseña", false, true);
					if (contrasenia == null) {
						break;
					}

					 if (sistema.validarCredenciales(nombreUsuario, contrasenia)) {

						Optional<Usuario> usuarioOptional = sistema.login(nombreUsuario, contrasenia);
						if (usuarioOptional.isPresent()) {
							if (usuarioOptional.get().getPerfil() == Perfil.peregrino) {
								Optional<Peregrino> peregrinoOptional = sistema
										.obtenerPeregrinoPorIdUsuario(usuarioOptional.get().getId());
								if (peregrinoOptional.isPresent()) {
									p = peregrinoOptional.get();
								}
							} else if (usuarioOptional.get().getPerfil() == Perfil.parada) {
								Optional<Parada> paradaOptional = sistema
										.obtenerParadaPorIdUsuario(usuarioOptional.get().getId());
								if (paradaOptional.isPresent()) {
									parada = paradaOptional.get();
								}
							}

							Usuario u = usuarioOptional.get();
							userActivo = new Usuario(u.getNombreUsuario(), u.getPerfil());
							userActivo.setId(u.getId());
						}

					} else {
						JOptionPane.showMessageDialog(null, "Credenciales incorrectas.");
						continue;
					}
					switch (userActivo.getPerfil()) {
					case peregrino: {
						// Opciones peregrino --> sellar parada, exportar carnet.. etc
						mostrarOpcionesPeregrino(p, sistema);
						break;
					}
					case administrador: {
						mostrarOpcionesAdmin(sistema, nombreUsuario);
						break MENU;
					}
					case parada: {
						// Implementación futura --> Opciones responsable de parada...
						mostrarOpcionesParada(parada, sistema);
						break;
					}
					default: {
						JOptionPane.showMessageDialog(null, "Perfil no encontrado.");
					}
					}

				} while (!sistema.validarCredenciales(nombreUsuario, contrasenia));
				break;
			}
			case "2": {
				// Registrar peregrino
				p = sistema.registrarPeregrino();

				if (p != null) {
					userActivo = new Usuario(p.getNombre(), Perfil.peregrino);
					String mensajeBienvenida = String.format(
							"Sus datos:\n" + "ID: %s\n" + "Nombre: %s\n" + "Nacionalidad: %s\n"
									+ "Fecha de expedición del carnet: %s\n" + "Parada inicial: %s\n"
									+ "Distancia recorrida: %.2f km\n" + "Número de VIPS: %d\n",
							p.getId(), p.getNombre(), p.getNacionalidad(), p.getCarnet().getFechaExp(),
							p.getCarnet().getParadaInicial().getNombre(), p.getCarnet().getDistancia(),
							p.getCarnet().getnVips());
					JOptionPane.showMessageDialog(null, mensajeBienvenida);
					ExportarCarnetXML exportar = new ExportarCarnetXML();
					try {
						exportar.exportarCarnet(p);
					} catch (NullPointerException ex) {
						JOptionPane.showMessageDialog(null,
								"No tiene carnet de peregrino disponible, cree uno en la parada inicial.");
					} catch (Exception ex) {
						ex.printStackTrace();
					}
					mostrarOpcionesPeregrino(p, sistema);
				}
				break;
			}
			case "3": {
				int respuesta = JOptionPane.showConfirmDialog(null, "¿Estás seguro que quieres salir?", "Confirmar",
						JOptionPane.YES_NO_OPTION);
				if (respuesta == JOptionPane.YES_OPTION) {
					JOptionPane.showMessageDialog(null, "Has salido del sistema.");
					userActivo = new Usuario("Invitado", Perfil.invitado);
					break;
				}
				JOptionPane.showMessageDialog(null, "Volviendo al menú...");

				continue;
			}
			default: {
				JOptionPane.showMessageDialog(null, "Opción no válida.");
			}
			}
		} while (!"3".equals(opcion));
	}

	/**
	 * Muestra las opciones específicas para un peregrino, como exportar carnet y
	 * sellar el carnet.
	 *
	 * @param p       El objeto Peregrino que representa al usuario actual.
	 * @param sistema El objeto Sistema que gestiona la lógica de negocio.
	 */
	private static void mostrarOpcionesPeregrino(Peregrino p, ControladorPrincipal sistema) {
		ExportarCarnetXML exportar = new ExportarCarnetXML();
		String opcion = "";
		JOptionPane.showMessageDialog(null,
				"Bienvenido " + p.getNombre() + "!\nPerfil: " + userActivo.getPerfil());
		do {
			String menu = "1. Exportar carnet\n" + "0. Cerrar sesión\n";

			opcion = sistema.obtenerEntrada(menu, "Selecciona una opción", true, false);
			if (opcion == null) {
				JOptionPane.showMessageDialog(null, "Selecciona una opción");
				continue;
			}

			switch (opcion) {
			case "1": {
				// Exportar carnet
				try {
					exportar.exportarCarnet(p);
				} catch (NullPointerException ex) {
					JOptionPane.showMessageDialog(null,
							"No tiene carnet de peregrino disponible, cree uno en la parada inicial.");
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				break;
			}
			case "0": {
				int respuesta = JOptionPane.showConfirmDialog(null, "¿Estás seguro que quieres cerrar sesión?",
						"Confirmar", JOptionPane.YES_NO_OPTION);
				if (respuesta == JOptionPane.YES_OPTION) {
					JOptionPane.showMessageDialog(null, "Has cerrado sesión.");
					userActivo = new Usuario("Invitado", Perfil.invitado);
					return;
				}
				JOptionPane.showMessageDialog(null, "Volviendo al menú...");

				continue;
			}

			default: {
				JOptionPane.showMessageDialog(null, "Opción no válida.");
			}
			}
		} while (true);
	}

	/**
	 * Muestra las opciones específicas para un administrador, como registrar un
	 * nuevo responsable de parada y cerrar sesión.
	 *
	 * @param sistema             El objeto Sistema que gestiona la lógica de
	 *                            negocio.
	 * @param archivoCredenciales Ruta del archivo de credenciales.
	 * @param archivoParadas      Ruta del archivo de paradas.
	 * @param nombreUsuario       Nombre del usuario administrador actual.
	 */
	private static void mostrarOpcionesAdmin(ControladorPrincipal sistema, String nombreUsuario) {
		String opcion = "";
		JOptionPane.showMessageDialog(null,
				"Bienvenido " + nombreUsuario + "!\nPerfil: " + userActivo.getPerfil() + "\nID Usuario:" + userActivo.getId());
		do {
			String menu = "1. Registrar parada\n" + "0. Cerrar sesión\n";
			opcion = sistema.obtenerEntrada(menu, "Selecciona una opción", true, false);
			if (opcion == null) {
				JOptionPane.showMessageDialog(null, "Selecciona una opción.");
				continue;
			}
			switch (opcion) {
			case "1": {
				String nombreParada = sistema.obtenerEntrada("Ingrese el nombre de la parada", "Nombre de la parada",
						false, false);
				if (nombreParada == null) {
					return;
				}
				char region = sistema.obtenerEntrada("¿Cual es la region de la parada?", "Region de la parada", false, false)
						.charAt(0);
				if (region == '0') {
					return;
				}

				if (sistema.paradaExiste(nombreParada, String.valueOf(region), true)) {
					JOptionPane.showMessageDialog(null, "La parada ya existe.");
					continue;
				}

				String responsable = sistema.obtenerEntrada("¿Quien es el responsable de la parada?", "Responsable",
						false, true);

				if (responsable == null) {
					return;
				}
				String contraseniaResponsable = sistema.obtenerEntrada("Ingrese la contraseña del responsable",
						"Contraseña del responsable", false, true);
				if (contraseniaResponsable == null) {
					return;
				}

				String confirmarDatos = String.format(
						"Datos de la nueva parada:\n Nombre: %s\nRegión: %s\nResponsable: %s\nContraseña: %s\n",
						nombreParada, region, responsable, contraseniaResponsable);
				int confirmacion = JOptionPane.showConfirmDialog(null, confirmarDatos, "Confirmar",
						JOptionPane.YES_NO_OPTION);
				if (confirmacion == JOptionPane.NO_OPTION) {
					JOptionPane.showMessageDialog(null, "Volviendo al menú...");
					continue;
				} else {
					if (!sistema.validarCredenciales(responsable, contraseniaResponsable)) {

						Usuario u = new Usuario(responsable, Perfil.parada);
						u.setPassword(contraseniaResponsable);
						Optional<Long> idUsuarioOptional = sistema.insertarUsuario(u);
						if(idUsuarioOptional.isPresent()) {
							Parada nuevaParada = new Parada(nombreParada, region, responsable);
							nuevaParada.setIdUsuario(idUsuarioOptional.get());
							sistema.registrarParada(nuevaParada);
						}
						

					} else {
						JOptionPane.showMessageDialog(null,
								"Error al registrar la parada. El responsable ya está ocupado.");
					}
				}

				break;
			}
			case "0": {
				int respuesta = JOptionPane.showConfirmDialog(null, "¿Estás seguro que quieres cerrar sesión?",
						"Confirmar", JOptionPane.YES_NO_OPTION);
				if (respuesta == JOptionPane.YES_OPTION) {
					JOptionPane.showMessageDialog(null, "Has cerrado sesión.");
					userActivo = new Usuario("Invitado", Perfil.invitado);
					return;
				}
				JOptionPane.showMessageDialog(null, "Volviendo al menú...");

				continue;
			}
			default:
				JOptionPane.showMessageDialog(null, "Opción no válida.");
			}
		} while (true);
	}

	public static void mostrarOpcionesParada(Parada parada, ControladorPrincipal sistema) {
	
		JOptionPane.showMessageDialog(null, "Bienvenido " + parada.getNombre() + "!\nPerfil: " + userActivo.getPerfil()
				+ "\nID Usuario:" + userActivo.getId());
		String opcion = "";
		MENU: do {
			String menu = "1. Exportar estancias\n" + "2. Sellar carnet\n" + "0. Cerrar sesión\n";
			opcion = sistema.obtenerEntrada(menu, "Selecciona una opción", true, false);
			if (opcion == null) {
				JOptionPane.showMessageDialog(null, "Selecciona una opción.");
				continue;
			}
			switch (opcion) {
			case "1": {
			    sistema.mostrarMenuExportar(parada);
			    break;
			}

			case "2": {
				// Sellar carnet
				sistema.sellarCarnet(parada);
				break;
			}
			case "0": {
				JOptionPane.showMessageDialog(null, "Has cerrado sesión");
				break MENU;
			}
			}

		} while (true);	
		
	}

}
