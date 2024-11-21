package davidmb.main;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;
import java.util.Properties;

import javax.swing.JOptionPane;

import davidmb.controllers.ControladorPrincipal;
import davidmb.controllers.ExportarCarnetXML;
import davidmb.controllers.ExportarEstanciasPeregrinosXML;
import davidmb.models.Parada;
import davidmb.models.Peregrino;
import davidmb.models.Perfil;
import davidmb.models.Usuario;

/**
 * Clase principal del sistema de gestión de peregrinos y paradas. Proporciona
 * el menú principal y las opciones para iniciar sesión, registrarse como
 * peregrino, y gestionar funcionalidades de usuario según el perfil. Se integra
 * con la clase {@link Sistema} para la gestión de usuarios y paradas.
 */
public class Principal {

	static Sesion userActivo = new Sesion("Invitado", Perfil.invitado, 1L);

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
		Properties prop = new Properties();

		try (FileInputStream input = new FileInputStream("src/main/resources/application.properties")) {
			prop.load(input);

		} catch (IOException e) {
			e.printStackTrace();
		}

		String usuarioAdmin = prop.getProperty("usuarioAdmin");
		String contraseniaAdmin = prop.getProperty("passwordAdmin");
		String opcion = "";

		do {
			String menu = "1. Login\n" + "2. Registrarse como peregrino\n" + "3. Salir\n";

			opcion = sistema.obtenerEntrada(menu, "Selecciona una opción", true);

			if (opcion == null) {
				JOptionPane.showMessageDialog(null, "Selecciona una opción.");
				continue;
			}

			switch (opcion) {
			case "1": {

				String nombreUsuario = "";
				String contrasenia = "";

				MENU: do {

					nombreUsuario = sistema.obtenerEntrada("Ingrese su nombre de usuario", "Nombre de usuario", false);

					if (nombreUsuario == null) {
						break;
					}

					contrasenia = sistema.obtenerEntrada("Ingrese su contraseña", "Contraseña", false);
					if (contrasenia == null) {
						break;
					}

					if (nombreUsuario.equals(usuarioAdmin) && contrasenia.equals(contraseniaAdmin)) {
						userActivo = new Sesion(usuarioAdmin, Perfil.administrador, 1L);

					} else if (sistema.validarCredenciales(nombreUsuario, contrasenia)) {

						Optional<Usuario> usuarioOptional = sistema.login(nombreUsuario, contrasenia);
						if (usuarioOptional.isPresent()) {
							if (usuarioOptional.get().getPerfil().equalsIgnoreCase("peregrino")) {
								Optional<Peregrino> peregrinoOptional = sistema
										.obtenerPeregrinoPorIdUsuario(usuarioOptional.get().getId());
								if (peregrinoOptional.isPresent()) {
									p = peregrinoOptional.get();
								}
							} else if (usuarioOptional.get().getPerfil().equalsIgnoreCase("parada")) {
								Optional<Parada> paradaOptional = sistema
										.obtenerParadaPorIdUsuario(usuarioOptional.get().getId());
								if (paradaOptional.isPresent()) {
									parada = paradaOptional.get();
								}
							}

							Usuario u = usuarioOptional.get();
							userActivo = new Sesion(u.getNombreUsuario(), Perfil.valueOf(u.getPerfil()), u.getId());
						}

						JOptionPane.showMessageDialog(null, "Bienvenido " + nombreUsuario + "!\nPerfil: "
								+ userActivo.getPerfil() + "\nID: " + userActivo.getId());
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
						JOptionPane.showMessageDialog(null, "Sección en desarrollo...");
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
				System.out.println(p);

				if (p != null) {
					userActivo = new Sesion(p.getNombre(), Perfil.peregrino, p.getId());
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
					userActivo = new Sesion("Invitado", Perfil.invitado, 1L);
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
		do {
			String menu = "1. Exportar carnet\n" + "2. Sellar carnet (No disponible)\n" + "0. Cerrar sesión\n";

			opcion = sistema.obtenerEntrada(menu, "Selecciona una opción", true);
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
			case "2": {
				// Sellar carnet
				JOptionPane.showMessageDialog(null, "Sección en desarrollo...");
				break;
			}
			case "0": {
				int respuesta = JOptionPane.showConfirmDialog(null, "¿Estás seguro que quieres cerrar sesión?",
						"Confirmar", JOptionPane.YES_NO_OPTION);
				if (respuesta == JOptionPane.YES_OPTION) {
					JOptionPane.showMessageDialog(null, "Has cerrado sesión.");
					userActivo = new Sesion("Invitado", Perfil.invitado, 1L);
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
				"Bienvenido " + nombreUsuario + "!\nPerfil: " + userActivo.getPerfil() + "\nID: " + userActivo.getId());
		do {
			String menu = "1. Registrar parada\n" + "0. Cerrar sesión\n";
			opcion = sistema.obtenerEntrada(menu, "Selecciona una opción", true);
			if (opcion == null) {
				JOptionPane.showMessageDialog(null, "Selecciona una opción.");
				continue;
			}
			switch (opcion) {
			case "1": {
				String nombreParada = sistema.obtenerEntrada("Ingrese el nombre de la parada", "Nombre de la parada",
						false);
				if (nombreParada == null) {
					return;
				}
				char region = sistema.obtenerEntrada("¿Cual es la region de la parada?", "Region de la parada", false)
						.charAt(0);
				if (region == '0') {
					return;
				}

				if (sistema.paradaExiste(nombreParada)) {
					JOptionPane.showMessageDialog(null, "La parada ya existe.");
					continue;
				}

				String responsable = sistema.obtenerEntrada("¿Quien es el responsable de la parada?", "Responsable",
						false);

				if (responsable == null) {
					return;
				}
				String contraseniaResponsable = sistema.obtenerEntrada("Ingrese la contraseña del responsable",
						"Contraseña del responsable", false);
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

						sistema.registrarParada(nombreParada, region, responsable);
						Usuario u = new Usuario(responsable, contraseniaResponsable, "responsable");
						sistema.insertarUsuario(u);
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
					userActivo = new Sesion("Invitado", Perfil.invitado, 1L);
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
		/**
		 * CU3: Exportar datos Parada: el responsable de una parada podrá exportar los
		 * datos de las estancias de los peregrinos de su parada, en un rango de fechas
		 * concreto. Se reflejarán los datos de la parada (id, nombre, región), el rango
		 * de fechas seleccionado y posteriormente la lista de las estancias de los
		 * peregrinos en ese periodo, indicando el id de la estancia, el nombre del
		 * peregrino, la fecha en que se realizó y si fue de tipo VIP o no.
		 */

		JOptionPane.showMessageDialog(null, "Bienvenido " + parada.getNombre() + "!\nPerfil: " + userActivo.getPerfil()
				+ "\nID: " + userActivo.getId());
		String opcion = "";
		MENU: do {
			String menu = "1. Exportar estancias\n" + "2. Sellar carnet\n" + "0. Cerrar sesión\n";
			opcion = sistema.obtenerEntrada(menu, "Selecciona una opción", true);
			if (opcion == null) {
				JOptionPane.showMessageDialog(null, "Selecciona una opción.");
				continue;
			}
			switch (opcion) {
			case "1": {
				LocalDate fechaInicio = sistema.obtenerEntradaFecha("Introduce la fecha de inicio", "Fecha de Inicio");
				LocalDate fechaFin = sistema.obtenerEntradaFecha("Introduce la fecha de Fin", "Fecha de Fin");

				ExportarEstanciasPeregrinosXML exportarEstancias = new ExportarEstanciasPeregrinosXML(fechaInicio,
						fechaFin, parada);

				try {
					exportarEstancias.exportarEstancias();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			}
			case "2": {
				// Sellar carnet
				break;
			}
			case "0": {
				JOptionPane.showMessageDialog(null, "Has cerrado sesión");
				break MENU;
			}
			}

		} while (true);

		/**
		 * CU5: Sellar/Alojarse: un administrador de parada recibe a un peregrino que
		 * llega en su ruta a su parada. Le pide su carnet de peregrino, se sella su
		 * paso por esa parada y se le pregunta si desea una estancia (VIP o no) en esa
		 * parada para el día actual. Se registran los datos en a BD, y si es el caso,
		 * también para una nueva estancia de ese peregrino en esa parada en esa fecha.
		 * Consecuentemente, esta información pasará a estar disponible también en los
		 * datos de la parada y del peregrino, actualizando los datos de éste relativos
		 * a la distancia recorrida (sumando los kms. desde la parada anterior) y al nº
		 * total de estancias VIP.
		 */
	}

}
