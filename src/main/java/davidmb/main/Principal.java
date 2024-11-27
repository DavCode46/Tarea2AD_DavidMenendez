package davidmb.main;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;
import java.util.Properties;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

import davidmb.controllers.ControladorPrincipal;
import davidmb.controllers.ExportarCarnetXML;
import davidmb.controllers.ExportarEstanciasPeregrinosXML;
import davidmb.models.Estancia;
import davidmb.models.Parada;
import davidmb.models.Peregrino;
import davidmb.models.Perfil;
import davidmb.models.Usuario;
import davidmb.utils.MyLogger;

/**
 * Clase principal del sistema de gestión de peregrinos y paradas. Proporciona
 * el menú principal y las opciones para iniciar sesión, registrarse como
 * peregrino, y gestionar funcionalidades de usuario según el perfil. Se integra
 * con la clase {@link ControladorPrincipal} para la gestión de usuarios y paradas.
 */
public class Principal {

	static Sesion userActivo = new Sesion("Invitado", Perfil.invitado, 1L);

	/**
	 * Método principal que inicia el programa mostrando el menú principal.
	 *
	 * @param args Argumentos de la línea de comandos (no se utilizan).
	 */
	public static void main(String[] args) {
		MyLogger.init();
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

				if (sistema.paradaExiste(nombreParada)) {
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

						Usuario u = new Usuario(responsable, contraseniaResponsable, "parada");
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
				Peregrino peregrino = null;
				Estancia nuevaEstancia = null;
				boolean ret = false;
				Long idPeregrinoLong = -1L;
				do {
					String idPeregrino = sistema.mostrarPeregrinos();
					try {
						idPeregrinoLong = Long.parseLong(idPeregrino);
					}catch(NumberFormatException ex) {
						JOptionPane.showMessageDialog(null, "El ID del peregrino es incorrecto", "Error", JOptionPane.ERROR_MESSAGE);
					}
				}while(!sistema.peregrinoExiste(idPeregrinoLong));
				Optional<Peregrino> peregrinoOptional = sistema.obtenerPeregrinoPorId(idPeregrinoLong);
				
				if(peregrinoOptional.isPresent()) {
					peregrino = peregrinoOptional.get();
				} else {
					JOptionPane.showMessageDialog(null, "Error al obtener el peregrino", "Error", JOptionPane.ERROR_MESSAGE);
				}
				Optional<Long> idPeregrinosParadas = sistema.insertarPeregrinosParadas(idPeregrinoLong, parada.getId(), LocalDate.now());
				peregrino.getParadas().add(parada.getId());
				parada.getPeregrinos().add(peregrino.getId());
				if(idPeregrinosParadas.isPresent()) {
					JOptionPane.showMessageDialog(null, "Parada registrada correctamente", "Parada registrada", JOptionPane.INFORMATION_MESSAGE);
				} else {
					JOptionPane.showMessageDialog(null, "Error al registrar la parada", "Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				peregrino.getCarnet().setDistancia(50);
				
				int registrarEstancia = JOptionPane.showConfirmDialog(null, "¿Deseas realizar una estancia?","Realizar estancia", JOptionPane.YES_NO_OPTION);
				Optional<Long> isEstanciaOptional = null;
				if(registrarEstancia == JOptionPane.YES_OPTION) {
					int esVip = JOptionPane.showConfirmDialog(null, "¿La estancia será VIP?","Estancia VIP", JOptionPane.YES_NO_OPTION);
					if(esVip == JOptionPane.YES_OPTION) {
						peregrino.getCarnet().setnVips(peregrino.getCarnet().getnVips() + 1);
						nuevaEstancia = new Estancia(LocalDate.now(), true, peregrino.getId(), parada.getId());
						isEstanciaOptional = sistema.insertarEstancia(nuevaEstancia);
						if(isEstanciaOptional.isPresent()) {
							Long idEstancia = isEstanciaOptional.get();
							peregrino.getEstancias().add(idEstancia);
						}
					} else {
						nuevaEstancia = new Estancia(LocalDate.now(), false, peregrino.getId(), parada.getId());
						isEstanciaOptional = sistema.insertarEstancia(nuevaEstancia);	
					}
					if(isEstanciaOptional.isPresent()) {
						Long idEstancia = isEstanciaOptional.get();
						peregrino.getEstancias().add(idEstancia);
						JOptionPane.showMessageDialog(null, "Estancia realizada correctamente", "Estancias", JOptionPane.INFORMATION_MESSAGE);
					}
				} else {
					JOptionPane.showMessageDialog(null, "Ha elegido no realizar estancia");
				}
				ret = sistema.modificarCarnet(peregrino.getCarnet());
				if(ret) {
					JOptionPane.showMessageDialog(null, "Carnet sellado correctamente", "Sellado correcto", JOptionPane.INFORMATION_MESSAGE);
				} else {
					JOptionPane.showMessageDialog(null, "Error al sellar el carnet", "Error", JOptionPane.ERROR_MESSAGE);
				}
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
