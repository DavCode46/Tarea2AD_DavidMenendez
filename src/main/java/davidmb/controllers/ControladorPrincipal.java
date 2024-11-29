package davidmb.controllers;

import java.awt.BorderLayout;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import davidmb.models.Carnet;
import davidmb.models.Estancia;
import davidmb.models.Parada;
import davidmb.models.Peregrino;
import davidmb.models.Perfil;
import davidmb.models.Usuario;

/**
 * La clase Sistema se encarga de gestionar el sistema de registro y
 * autenticación de credenciales, así como el manejo de paradas y peregrinos.
 * Proporciona métodos para validar, registrar usuarios, y gestionar la
 * información de peregrinos en el sistema.
 * 
 * Utiliza archivos de texto y objetos serializables para almacenar los datos de
 * credenciales y paradas, y utiliza un archivo XML para cargar la lista de
 * países disponibles.
 */
public class ControladorPrincipal {

	private ControladorPrincipal controladorPrincipal;

	/**
	 * Constructor por defecto.
	 */
	public ControladorPrincipal() {
		super();
	}

	public ControladorPrincipal(ControladorPrincipal controladorPrincipal) {
		super();
		this.controladorPrincipal = controladorPrincipal;
	}


	////////////////////////////////////////////////////////////////////////////////
	//////                    SECCIÓN PEREGRINOS                             //////                                                    
	//////                                                                   //////
	///////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Obtiene un peregrino basado en su ID de usuario.
	 *
	 * @param id el ID del usuario asociado al peregrino.
	 * @return un {@code Optional} que contiene el peregrino si se encuentra, o vacío si no existe.
	 */
	public Optional<Peregrino> obtenerPeregrinoPorIdUsuario(Long id) {
		PeregrinosController pec = new PeregrinosController();
		return pec.obtenerPeregrinoPorIdUsuario(id);
	}

	/**
	 * Obtiene un peregrino basado en su ID único.
	 *
	 * @param id el ID único del peregrino.
	 * @return un {@code Optional} que contiene el peregrino si se encuentra, o vacío si no existe.
	 */
	public Optional<Peregrino> obtenerPeregrinoPorId(Long id) {
		PeregrinosController pec = new PeregrinosController();
		return pec.obtenerPeregrinoPorId(id);
	}
	
	/**
	 * Verifica si un peregrino existe en el sistema basado en su ID.
	 *
	 * @param id el ID único del peregrino.
	 * @return {@code true} si el peregrino existe, o {@code false} si no existe.
	 */
	public boolean peregrinoExiste(Long id) {
		PeregrinosController pc = new PeregrinosController();
		return pc.peregrinoExiste(id);
	}
	
	/**
	 * Registra un nuevo peregrino en el sistema solicitando los datos necesarios al usuario.
	 *
	 * @return el objeto {@code Peregrino} registrado si la operación tiene éxito,
	 *         o {@code null} si se cancela o hay errores en el proceso.
	 */
	public Peregrino registrarPeregrino() {
		ParadasController pc = new ParadasController();
		PeregrinosController pec = new PeregrinosController();
		CarnetsController cc = new CarnetsController();
		Peregrino nuevoPeregrino = null;
		Carnet carnet = null;
		String nombre;
		String nombreUsuario;
		do {
			JOptionPane.showMessageDialog(null, "Formulario de registro de nuevo peregrino");

			// Obtener datos del usuario
			nombre = obtenerEntrada("Ingrese su nombre", "Nombre", false, false);
			if (nombre == null)
				return null;

			nombreUsuario = obtenerEntrada("Ingrese su nombre de usuario", "Nombre de usuario", false, true);
			if (nombreUsuario == null)
				return null;

			String contrasenia = obtenerEntrada("Ingrese su contraseña", "Contraseña", false, true);
			if (contrasenia == null)
				return null;

			String nacionalidad = "";
			do {
				nacionalidad = mostrarPaises();
				if (!validarPais(nacionalidad)) {
					JOptionPane.showMessageDialog(null, "El país ingresado no es válido.");
				}
			} while (!validarPais(nacionalidad));

			String parada = "";
			do {
				parada = obtenerEntrada(mostrarParadas(true), "Parada", false, false);
				if (!pc.paradaExiste(parada, null, false)) {
					JOptionPane.showMessageDialog(null, "La parada seleccionada no es válida.");
				}
			} while (!pc.paradaExiste(parada, null, false));

			// Confirmar los datos
			nuevoPeregrino = confirmarDatos(nombre, nombreUsuario, contrasenia, nacionalidad, parada);

			do {
				if (nuevoPeregrino == null) {
					// Si la confirmación falla, pedir nuevos datos

					nuevoPeregrino = obtenerDatosModificados(nombre, nombreUsuario, contrasenia, nacionalidad, parada);

				}
			} while (nuevoPeregrino == null);
				Optional<Parada> paradaEncontradaOptional = pc.obtenerParadaPorNombre(parada);

				Parada paradaEncontrada = null;
				if (paradaEncontradaOptional.isPresent()) {
					paradaEncontrada = paradaEncontradaOptional.get();
				}
				carnet = new Carnet(paradaEncontrada);
				paradaEncontrada.getPeregrinos().add(nuevoPeregrino.getId());
				Optional<Long> idCarnetOptional = cc.insertarCarnet(carnet);

				if (idCarnetOptional.isPresent()) {
					JOptionPane.showMessageDialog(null, "Carnet creado correctamente");
					nuevoPeregrino.getCarnet().setId(idCarnetOptional.get());
				} else {
					JOptionPane.showMessageDialog(null, "Error al insertar el carnet");
				}
				Optional<Long> idPeregrinoOptional = pec.insertarPeregrino(nuevoPeregrino);

				if (idPeregrinoOptional.isPresent()) {
					JOptionPane.showMessageDialog(null, "Peregrino insertado correctamente");
					nuevoPeregrino.setId(idPeregrinoOptional.get());
					return nuevoPeregrino;
				} else {
					JOptionPane.showMessageDialog(null, "Error al insertar correctamente");
				}

		} while (pec.nombrePeregrinoExiste(nombre));
		return null;
	}
	
	////////////////////////////////////////////////////////////////////////////////
	//////                    SECCIÓN CARNETS                                 //////                                                    
	//////                                                                   //////
	///////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Modifica los datos de un carnet existente en el sistema.
	 * 
	 * @param carnet el objeto Carnet que contiene los datos actualizados.
	 * @return true si el carnet fue modificado exitosamente, false en caso contrario.
	 */
	public boolean modificarCarnet(Carnet carnet) {
		CarnetsController c = new CarnetsController();
		return c.modificarCarnet(carnet);
	}
	
	/**
	 * Realiza el proceso de sellado del carnet de un peregrino en una parada específica.
	 * Esto incluye registrar la parada en el carnet del peregrino, actualizar la distancia
	 * recorrida y opcionalmente registrar una estancia en la parada.
	 * 
	 * @param parada la parada donde se realizará el sellado del carnet.
	 */
	public void sellarCarnet(Parada parada) {
		Peregrino peregrino = null;
		Estancia nuevaEstancia = null;
		boolean ret = false;
		Long idPeregrinoLong = -1L;
		
		do {
			String idPeregrino = mostrarPeregrinos();
			try {
				idPeregrinoLong = Long.parseLong(idPeregrino);
			}catch(NumberFormatException ex) {
				JOptionPane.showMessageDialog(null, "El ID del peregrino es incorrecto", "Error", JOptionPane.ERROR_MESSAGE);
			}
		}while(!peregrinoExiste(idPeregrinoLong));
		Optional<Peregrino> peregrinoOptional = obtenerPeregrinoPorId(idPeregrinoLong);
	
		
		if(peregrinoOptional.isPresent()) {
			peregrino = peregrinoOptional.get();
		} else {
			JOptionPane.showMessageDialog(null, "Error al obtener el peregrino", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		String mensaje = String.format("¿Son correctos los datos?\n"
				+ "Peregrino: %s\n"
				+ "ID: %d\n"
				+ "Parada: %s\n"
				+ "Responsable: %s\n"
				+ "ID - Parada: %d\n", peregrino.getNombre(), peregrino.getId(), parada.getNombre(), parada.getResponsable(), parada.getId());
		
		int confirmacion = JOptionPane.showConfirmDialog(null, mensaje, "Confirmar", JOptionPane.YES_NO_OPTION);
		
		if (confirmacion == JOptionPane.NO_OPTION) {
			JOptionPane.showMessageDialog(null, "Ha cancelado el sellado del carnet", "Cancelado",
					JOptionPane.INFORMATION_MESSAGE);
			return;
		} 
		
		Optional<Long> idPeregrinosParadas = insertarPeregrinosParadas(idPeregrinoLong, parada.getId(), LocalDate.now());
		
		peregrino.getParadas().add(parada.getId());
		parada.getPeregrinos().add(peregrino.getId());
		if(idPeregrinosParadas.isPresent()) {
			JOptionPane.showMessageDialog(null, "Parada registrada correctamente", "Parada registrada", JOptionPane.INFORMATION_MESSAGE);
		} else {
			JOptionPane.showMessageDialog(null, "Error al registrar la parada", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		Random distanciaRandom = new Random();
		int numeroAleatorio = distanciaRandom.nextInt(500);
		
		peregrino.getCarnet().setDistancia(peregrino.getCarnet().getDistancia() + numeroAleatorio);
		
		int registrarEstancia = JOptionPane.showConfirmDialog(null, "¿Deseas realizar una estancia?","Realizar estancia", JOptionPane.YES_NO_OPTION);
		Optional<Long> isEstanciaOptional = null;
		if(registrarEstancia == JOptionPane.YES_OPTION) {
			int esVip = JOptionPane.showConfirmDialog(null, "¿La estancia será VIP?","Estancia VIP", JOptionPane.YES_NO_OPTION);
			if(esVip == JOptionPane.YES_OPTION) {
				peregrino.getCarnet().setnVips(peregrino.getCarnet().getnVips() + 1);
				nuevaEstancia = new Estancia(LocalDate.now(), true, peregrino.getId(), parada.getId());
				isEstanciaOptional = insertarEstancia(nuevaEstancia);
				if(isEstanciaOptional.isPresent()) {
					Long idEstancia = isEstanciaOptional.get();
					peregrino.getEstancias().add(idEstancia);
				}
			} else {
				nuevaEstancia = new Estancia(LocalDate.now(), false, peregrino.getId(), parada.getId());
				isEstanciaOptional = insertarEstancia(nuevaEstancia);	
			}
			if(isEstanciaOptional.isPresent()) {
				Long idEstancia = isEstanciaOptional.get();
				peregrino.getEstancias().add(idEstancia);
				JOptionPane.showMessageDialog(null, "Estancia realizada correctamente", "Estancias", JOptionPane.INFORMATION_MESSAGE);
			}
		} else {
			JOptionPane.showMessageDialog(null, "Ha elegido no realizar estancia");
		}
		ret = modificarCarnet(peregrino.getCarnet());
		if(ret) {
			JOptionPane.showMessageDialog(null, "Carnet sellado correctamente", "Sellado correcto", JOptionPane.INFORMATION_MESSAGE);
		} else {
			JOptionPane.showMessageDialog(null, "Error al sellar el carnet", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	
	////////////////////////////////////////////////////////////////////////////////
	//////                    SECCIÓN USUARIOS                               //////                                                    
	//////                                                                   //////
	///////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Valida las credenciales proporcionadas de un usuario.
	 * 
	 * @param nombre el nombre de usuario.
	 * @param contrasenia la contraseña del usuario.
	 * @return true si las credenciales son válidas, false en caso contrario.
	 */
	public boolean validarCredenciales(String nombre, String contrasenia) {
		UsuariosController uc = new UsuariosController();
		return uc.validarCredenciales(nombre, contrasenia);
	}

	/**
	 * Inicia sesión en el sistema con las credenciales proporcionadas.
	 * 
	 * @param nombre el nombre de usuario.
	 * @param contrasenia la contraseña del usuario.
	 * @return un Optional que contiene el objeto Usuario si el inicio de sesión fue exitoso,
	 *         o un Optional vacío si las credenciales son incorrectas.
	 */
	public Optional<Usuario> login(String nombre, String contrasenia) {
		UsuariosController uc = new UsuariosController();
		return uc.login(nombre, contrasenia);
	}

	/**
	 * Inserta un nuevo usuario en el sistema.
	 * 
	 * @param u el objeto Usuario que contiene los datos del nuevo usuario.
	 * @return un Optional que contiene el ID del usuario recién insertado si la operación fue exitosa,
	 *         o un Optional vacío si ocurrió un error.
	 */
	public Optional<Long> insertarUsuario(Usuario u) {
		UsuariosController uc = new UsuariosController();
		return uc.insertarUsuario(u);
	}
	
	////////////////////////////////////////////////////////////////////////////////
	//////                    SECCIÓN PARADAS                                 //////                                                    
	//////                                                                    //////
	///////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Obtiene la parada asociada al ID del usuario.
	 * 
	 * @param id el ID del usuario.
	 * @return un Optional que contiene el objeto Parada si se encuentra la parada asociada,
	 *         o un Optional vacío si no existe una parada asociada al ID del usuario.
	 */
	public Optional<Parada> obtenerParadaPorIdUsuario(Long id) {
		ParadasController pc = new ParadasController();
		return pc.obtenerParadaPorIdUsuario(id);
	}

	/**
	 * Obtiene una lista de paradas visitadas por un peregrino específico.
	 * 
	 * @param idPeregrino el ID del peregrino.
	 * @return una lista de objetos Parada que representan las paradas visitadas por el peregrino.
	 */
	public List<Parada> obtenerParadasPorIdPeregrino(Long idPeregrino) {
		ParadasController pc = new ParadasController();
		return pc.obtenerParadasPorIdPeregrino(idPeregrino);
	}
	
	/**
	 * Obtiene la parada asociada a un ID específico.
	 * 
	 * @param id el ID de la parada.
	 * @return un Optional que contiene el objeto Parada si se encuentra la parada,
	 *         o un Optional vacío si no existe una parada con el ID proporcionado.
	 */
	public Optional<Parada> obtenerParadaPorId(Long id) {
		ParadasController pc = new ParadasController();
		return pc.obtenerParadaPorId(id);
	}
	
	/**
	 * Verifica si una parada existe en el sistema.
	 * 
	 * @param nombre el nombre de la parada.
	 * @param region la región de la parada
	 * @param esAdmin boolean para verificar si es admin o no
	 * @return true si la parada existe, false en caso contrario.
	 */
	public boolean paradaExiste(String nombre, String region, boolean esAdmin) {
		ParadasController pc = new ParadasController();
		return pc.paradaExiste(nombre, region, esAdmin);
	}
	
	/**
	 * Registra una relación entre un peregrino y una parada con la fecha de visita.
	 * 
	 * @param idPeregrino el ID del peregrino.
	 * @param idParada el ID de la parada.
	 * @param fecha la fecha en que el peregrino visitó la parada.
	 * @return un Optional que contiene el ID de la relación si la operación fue exitosa,
	 *         o un Optional vacío si ocurrió un error.
	 */
	public Optional<Long> insertarPeregrinosParadas(Long idPeregrino, Long idParada, LocalDate fecha) {
		ParadasController pc = new ParadasController();
		return pc.insertarPeregrinosParadas(idPeregrino, idParada, fecha);
	}
	
	/**
	 * Genera una cadena con información detallada de una parada.
	 * 
	 * @param parada el objeto Parada cuyos detalles se mostrarán.
	 * @return una cadena con la información de la parada, incluyendo su ID, nombre, región y responsable.
	 */
	public String mostrarInformacionParada(Parada parada) {
		return "Parada:\nID: " + parada.getId() + "\nNombre: " + parada.getNombre() + "\nRegión: " + parada.getRegion()
				+ "\nResponsable: " + parada.getResponsable();
	}
	
	
	/**
	 * Registra una nueva parada en el sistema.
	 * 
	 * @return true si el registro fue exitoso; false en caso contrario.
	 */
	public boolean registrarParada(Parada parada) {
		ParadasController pc = new ParadasController();
		if (pc.paradaExiste(parada.getNombre(), String.valueOf(parada.getRegion()), true)) {
			JOptionPane.showMessageDialog(null, "La parada ya existe");
			return false;
		}

		Optional<Long> idParadaInsertada = pc.insertarParada(parada);
		if (idParadaInsertada.isPresent()) {
			JOptionPane.showMessageDialog(null, "Parada registrada con éxito");
			return true;
		}

		return false;
	}
	
	/**
	 * Muestra un menú para exportar información de una parada en un rango de fechas especificado.
	 * 
	 * <p>El método solicita al usuario que introduzca las fechas de inicio y fin para realizar la exportación, 
	 * valida las fechas introducidas y, si son correctas, continúa con el proceso de exportación. Permite al 
	 * usuario confirmar los datos antes de proceder, mostrando información sobre la parada y el rango de fechas.
	 * Si el usuario decide continuar exportando, se ejecuta el proceso de exportación de estancias de peregrinos 
	 * dentro del rango de fechas.</p>
	 * 
	 * @param parada el objeto {@code Parada} que representa la parada cuya información se desea exportar.
	 */
	public void mostrarMenuExportar(Parada parada) {
		LocalDate fechaInicio;
		LocalDate fechaFin;
		String informacionParada = mostrarInformacionParada(parada);
		boolean fechasCorrectas = false;
		boolean continuarExportando = true;
		do {
			do {
				JOptionPane.showMessageDialog(null, informacionParada);
				fechaInicio = obtenerEntradaFecha("Introduce la fecha de inicio", "Fecha de Inicio");
				fechaFin = obtenerEntradaFecha("Introduce la fecha de Fin", "Fecha de Fin");
				String mensaje = String.format("Fecha de inicio: %s\nFecha de fin: %s\n", fechaInicio, fechaFin);
				int confirmacion = JOptionPane.showConfirmDialog(null,
						"¿Son Correctos los datos?\n" + informacionParada + "\n" + mensaje, "Confirmar",
						JOptionPane.YES_NO_OPTION);

				if (confirmacion == JOptionPane.NO_OPTION) {

					continue;
				} else if (confirmacion == JOptionPane.YES_OPTION) {
					
					if (validarFechas(fechaInicio, fechaFin)) {
						fechasCorrectas = true;
					} 
				}

			} while (!fechasCorrectas);
			fechasCorrectas = false;
			continuarExportando = mostrarEstanciasPeregrinos(fechaInicio, fechaFin, parada);
		} while (continuarExportando );
	}
	

	/**
	 * Muestra las paradas registradas en el sistema.
	 * 
	 * @param isPeregrino Si es true, muestra solo el nombre y región.
	 * @return Lista de paradas formateada como cadena.
	 */
	public String mostrarParadas(boolean isPeregrino) {
		ParadasController controladorParada = new ParadasController();
		List<Parada> paradasLeidas = controladorParada.obtenerTodasParadas();

		StringBuilder sb = new StringBuilder("Paradas registradas: \n");

		for (Parada parada : paradasLeidas) {
			if (isPeregrino) {
				sb.append("\nNombre: ").append(parada.getNombre());
				sb.append("\nRegión: ").append(parada.getRegion());
			} else {
				sb.append("\nID: ").append(parada.getId());
				sb.append("\nNombre: ").append(parada.getNombre());
				sb.append("\nRegión: ").append(parada.getRegion());
				sb.append("\nResponsable: ").append(parada.getResponsable());
			}
		}

		return sb.toString();
	}
	
	/**
	 * Muestra una tabla interactiva con los peregrinos disponibles y permite al usuario
	 * seleccionar el ID de un peregrino introduciéndolo en un campo de texto.
	 * 
	 * <p>Este método utiliza un panel con una tabla que muestra todos los peregrinos obtenidos
	 * del controlador, y un campo de texto donde el usuario puede introducir el ID de un peregrino.</p>
	 * 
	 * @return el ID del peregrino introducido como una cadena de texto si el usuario confirma la selección,
	 *         o {@code null} si el usuario cancela la operación.
	 */
	public String mostrarPeregrinos() {
		PeregrinosController pec = new PeregrinosController();
		List<Peregrino> listaPeregrinos = pec.obtenerTodosPeregrinos();
		String[] columnas = { "ID", "Peregrino" };

		DefaultTableModel modeloTabla = new DefaultTableModel(columnas, 0) {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return false; // Evita que el usuario pueda editar las celdas de la tabla
			}
		};

		// Rellenar el modelo con datos del archivo paises.xml
		for (Peregrino p : listaPeregrinos) {
			modeloTabla.addRow(new Object[] { p.getId(), p.getNombre() });
		}

		// Tabla
		JTable tablaPeregrinos = new JTable(modeloTabla);
		tablaPeregrinos.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

		JTextField inputField = new JTextField();
		inputField.setColumns(10);

		// Panel Tabla
		JPanel panel = new JPanel(new BorderLayout(5, 5));
		panel.add(new JScrollPane(tablaPeregrinos), BorderLayout.CENTER);

		// InputField para la inserción del ID
		JPanel inputPanel = new JPanel(new BorderLayout(5, 5));
		inputPanel.add(new JLabel("Introduce el ID del peregrino:"), BorderLayout.WEST);
		inputPanel.add(inputField, BorderLayout.CENTER);
		panel.add(inputPanel, BorderLayout.SOUTH);

		// Añadir el panel principal a un JOptionPane.showConfirmDialog
		int option = JOptionPane.showConfirmDialog(null, panel, "Peregrinos disponibles:", JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.INFORMATION_MESSAGE);

		if (option == JOptionPane.OK_OPTION) {
			return inputField.getText().trim();
		} else {
			return null; // cancelado
		}
	}

	
	/**
	 * Muestra una tabla interactiva con las estancias de los peregrinos en una parada específica
	 * durante un rango de fechas, y permite exportar las estancias a un archivo XML.
	 * 
	 * <p>Este método genera una tabla con las estancias dentro del rango de fechas especificado,
	 * incluyendo información del peregrino, fecha de estancia, y si es VIP. Además, permite
	 * exportar la información a un archivo XML y brinda la opción de seleccionar otras fechas
	 * para repetir el proceso.</p>
	 * 
	 * @param fechaInicio la fecha de inicio del rango.
	 * @param fechaFin la fecha de fin del rango.
	 * @param parada la parada cuyas estancias se desean visualizar y exportar.
	 * @return {@code true} si el usuario decide seleccionar otras fechas y repetir el proceso;
	 *         {@code false} si el usuario cancela la operación.
	 */
	public boolean mostrarEstanciasPeregrinos(LocalDate fechaInicio, LocalDate fechaFin, Parada parada) {
		ExportarEstanciasPeregrinosXML exportarEstancias = new ExportarEstanciasPeregrinosXML(fechaInicio, fechaFin,
				parada);
		PeregrinosController pec = new PeregrinosController();
		List<Estancia> estanciasParada = obtenerEstanciasPorIdParada(parada.getId());
		String[] columnas = { "ID", "Peregrino", "Fecha", "VIP" };

		boolean ret = false;

		DefaultTableModel modeloTabla = new DefaultTableModel(columnas, 0) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return false; // Evita que el usuario pueda editar las celdas de la tabla
			}
		};

		// Rellenar el modelo con datos de las estancias
		for (Estancia e : estanciasParada) {
			Optional<Peregrino> peregrinoOptional = pec.obtenerPeregrinoPorId(e.getPeregrino());
			if (peregrinoOptional.isPresent()) {
				Peregrino p = peregrinoOptional.get();

				// Añadir la fila con la imagen en la columna "VIP"
				if (e.getFecha().isAfter(fechaInicio) && e.getFecha().isBefore(fechaFin)) {
					modeloTabla
							.addRow(new Object[] { e.getId(), p.getNombre(), e.getFecha(), e.isVip() ? "Sí" : "No" });
				}
			}
		}

		// Tabla
		JTable tablaPeregrinos = new JTable(modeloTabla);
		tablaPeregrinos.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

		// Panel Tabla
		JPanel panel = new JPanel(new BorderLayout(5, 5));
		panel.add(new JScrollPane(tablaPeregrinos), BorderLayout.CENTER);

		// InputField para la inserción del ID
		JPanel buttonPanel = new JPanel(new BorderLayout(5, 5));

		JButton exportButton = new JButton("Exportar en XML");	 

		exportButton.addActionListener(e -> {
			try {
				exportarEstancias.exportarEstancias();
			} catch (Exception ex) {

				JOptionPane.showMessageDialog(null, "Error al exportar las estancias", "Error",
						JOptionPane.ERROR_MESSAGE);
				ex.printStackTrace();

			}
		});
		buttonPanel.add(exportButton, BorderLayout.CENTER);

		panel.add(buttonPanel, BorderLayout.SOUTH);

		  int opcion2 = JOptionPane.showOptionDialog(
		      null, // Componente padre, en este caso null para centrarlo en la pantalla
		      panel, // Panel que contiene la información y botones
		      "Estancias de peregrinos", // Título de la ventana
		      JOptionPane.YES_NO_OPTION, // Opciones que se muestran (sí/no) 
		      JOptionPane.INFORMATION_MESSAGE, // Tipo de mensaje
		      null, // Icono --> nulo
		      new Object[] {"Seleccionar otras fechas", "Cancelar"}, // Opciones personalizadas para los botones
		      "Cancelar" // Opción por defecto
		  );
		  if(opcion2 == JOptionPane.YES_OPTION) {
			  ret = true;
		  }
		
		return ret;
	}



	////////////////////////////////////////////////////////////////////////////////
	//////                    SECCIÓN ESTANCIAS                               //////                                                    
	//////                                                                    //////
	///////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Obtiene una lista de estancias asociadas a un peregrino específico mediante su ID.
	 * 
	 * <p>Este método consulta al controlador de estancias para recuperar todas las estancias
	 * que pertenecen al peregrino identificado por el ID proporcionado.</p>
	 * 
	 * @param idPeregrino el ID del peregrino cuyas estancias se desean obtener.
	 * @return una lista de objetos {@link Estancia} correspondientes al peregrino con el ID dado.
	 */
	public List<Estancia> obtenerEstanciasPorIdPeregrino(Long idPeregrino) {
		EstanciasController ec = new EstanciasController();
		return ec.obtenerEstanciasPorIdPeregrino(idPeregrino);
	}

	/**
	 * Obtiene una lista de estancias asociadas a una parada específica mediante su ID.
	 * 
	 * <p>Este método consulta al controlador de estancias para recuperar todas las estancias
	 * que están asociadas con la parada identificada por el ID proporcionado.</p>
	 * 
	 * @param idParada el ID de la parada cuyas estancias se desean obtener.
	 * @return una lista de objetos {@link Estancia} correspondientes a la parada con el ID dado.
	 */
	public List<Estancia> obtenerEstanciasPorIdParada(Long idParada) {
		EstanciasController ec = new EstanciasController();
		return ec.obtenerEstanciasPorIdParada(idParada);
	}
	
	/**
	 * Inserta una nueva estancia en el sistema.
	 * 
	 * <p>Este método solicita al controlador de estancias que registre una nueva estancia
	 * en la base de datos o sistema, devolviendo el ID de la estancia recién insertada.</p>
	 * 
	 * @param estancia el objeto {@link Estancia} que representa la estancia a insertar.
	 * @return un {@link Optional} que contiene el ID de la nueva estancia si la inserción es exitosa,
	 *         o un valor vacío si ocurre algún error.
	 */
	public Optional<Long> insertarEstancia(Estancia estancia) {
		EstanciasController e = new EstanciasController();
		return e.insertarEstancia(estancia);
	}
	
	
	////////////////////////////////////////////////////////////////////////////////
	//////                    SECCIÓN ENTRADA DATOS                            /////                                                    
	//////                                                                    //////
	///////////////////////////////////////////////////////////////////////////////


	/**
	 * Muestra un JOptionPane con los datos introducidos por el usuario para que
	 * este los confirme
	 * 
	 * @param nombre
	 * @param contrasenia
	 * @param nacionalidad
	 * @param parada
	 * @return Peregrino con los datos introducidos por el usuario
	 */
	private Peregrino confirmarDatos(String nombre, String nombreUsuario, String contrasenia, String nacionalidad,
			String parada) {
		UsuariosController uc = new UsuariosController();
		ParadasController pc = new ParadasController();
		String mensajeFormateado = String.format("Verifica que los datos son correctos \n" + "Nombre: %s \n"
				+ "Contraseña: %s\n" + "Nacionalidad: %s\n" + "Parada actual: %s\n", nombre, contrasenia, nacionalidad,
				parada);

		int confirmacion = JOptionPane.showConfirmDialog(null, mensajeFormateado, "Confirma",
				JOptionPane.YES_NO_OPTION);
		if (confirmacion == JOptionPane.YES_OPTION) {
			// Datos correctos --> Continuar
			if (uc.usuarioExiste(nombreUsuario)) {
				JOptionPane.showMessageDialog(null, "El usuario ya existe", "Error", JOptionPane.ERROR_MESSAGE);
				return null;
			} else {

				Optional<Parada> paradaObjOptional = pc.obtenerParadaPorNombre(parada);
				Parada paradaObj = paradaObjOptional.orElse(null);
			
				Usuario u = new Usuario(nombreUsuario, Perfil.peregrino);
				u.setPassword(contrasenia);
				Optional<Long> idUsuario = uc.insertarUsuario(u);
				Long idUsuarioValue = -1L;
				if (idUsuario.isPresent()) {
					idUsuarioValue = idUsuario.orElse(null);
				}

				Peregrino nuevoPeregrino = new Peregrino(nombre, nacionalidad, new Carnet(paradaObj), idUsuarioValue);

				return nuevoPeregrino;
			}
		}
		return null; // Si el usuario cancela
	}

	/**
	 * Vuelve a mostrar los formularios para que el usuario modifique sus datos,
	 * solo se muestra si el usuario indica que los datos mostrados en
	 * confirmarDatos no son correctos
	 * 
	 * @param nombre
	 * @param contrasenia
	 * @param nacionalidad
	 * @param parada
	 * @return Peregrino con los datos modificados
	 */
	private Peregrino obtenerDatosModificados(String nombre, String nombreUsuario, String contrasenia,
			String nacionalidad, String parada) {
		UsuariosController uc = new UsuariosController();
		ParadasController pc = new ParadasController();
		// Pide los nuevos datos al usuario
		String nuevoNombre = obtenerEntrada("Ingrese su nuevo nombre", nombre, false, false);
		if (nuevoNombre == null)
			return null;

		String nuevoNombreUsuario = obtenerEntrada("Ingrese su nuevo nombre de usuario", nombreUsuario, false, true);
		if (nuevoNombreUsuario == null)
			return null;

		String nuevaContrasenia = obtenerEntrada("Ingrese su nueva contraseña", contrasenia, false, true);
		if (nuevaContrasenia == null)
			return null;

		String nuevaNacionalidad = mostrarPaises();
		if (nuevaNacionalidad == null)
			return null;

		String nuevaParada = obtenerEntrada(mostrarParadas(true), parada, false, false);
		if (nuevaParada == null)
			return null;

		if (uc.usuarioExiste(nuevoNombreUsuario)) {
			JOptionPane.showMessageDialog(null, "El usuario ya existe", "Error", JOptionPane.ERROR_MESSAGE);
			return null;
		} else {

			Optional<Parada> paradaObjOptional = pc.obtenerParadaPorNombre(parada);
			Parada paradaObj = paradaObjOptional.orElse(null);
		
			Usuario u = new Usuario(nuevoNombreUsuario, Perfil.peregrino);
			u.setPassword(nuevaContrasenia);
			Optional<Long> idUsuario = uc.insertarUsuario(u);
			Long idUsuarioValue = -1L;
			if (idUsuario.isPresent()) {
				idUsuarioValue = idUsuario.orElse(null);
			}
			Peregrino nuevoPeregrino = new Peregrino(nuevoNombre, nuevaNacionalidad, new Carnet(paradaObj),
					idUsuarioValue);
			nuevoPeregrino.getParadas().add(paradaObj.getId());
			paradaObj.getPeregrinos().add(nuevoPeregrino.getId());
			return nuevoPeregrino;
		}
	}
	
	/**
	 * Valida un String según los criterios establecidos (no vacío, sin espacios
	 * iniciales, etc.).
	 * 
	 * @param str    String a validar.
	 * @param esMenu true si es una entrada de menú; false si es un nombre.
	 * @return true si es válido; false en caso contrario.
	 */
	public boolean validarStr(String str, boolean esMenu, boolean esUsuario) {

		if (str.isEmpty()) {
			return false;
		}

		if (str.trim().isEmpty()) {
			return false;
		}

		if (!esMenu) {
			if (Character.isDigit(str.charAt(0))) {
				return false;
			}
		}

		if (esUsuario) {
			if (str.contains(" ")) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Solicita al usuario una entrada y valida el valor según los criterios
	 * establecidos.
	 * 
	 * @param mensaje Mensaje a mostrar al usuario.
	 * @param titulo  Título de la entrada.
	 * @param esMenu  true si la entrada es una opción de menú; false si es un valor
	 *                de campo.
	 * @return Entrada validada como cadena.
	 */
	public String obtenerEntrada(String mensaje, String titulo, boolean esMenu, boolean esUsuario) {
		String entrada;
		do {
			entrada = JOptionPane.showInputDialog(null, mensaje, titulo);
			if (entrada == null) {
				JOptionPane.showMessageDialog(null, "Operación cancelada.");
				return null; // Salir si se cancela
			}
			if (!validarStr(entrada, esMenu, esUsuario)) {
				if (!esMenu) {
					JOptionPane.showMessageDialog(null,
							" El campo " + titulo + " no puede contener espacios ni empezar por números.");
				} else {
					JOptionPane.showMessageDialog(null, "Selecciona una opción entre las disponibles.");
				}

			}

		} while (!validarStr(entrada, esMenu, esUsuario));
		return entrada.trim();
	}

	/**
	 * Solicita al usuario que ingrese una fecha mediante un cuadro de entrada de texto.
	 * 
	 * <p>Este método muestra un cuadro de diálogo en el que el usuario debe introducir una fecha
	 * en el formato "yyyy-MM-dd". Si la entrada no es válida o está vacía, se muestra un mensaje de error
	 * y se repite la solicitud hasta obtener una entrada válida.</p>
	 * 
	 * @param mensaje el mensaje que se mostrará en el cuadro de entrada, solicitando al usuario que ingrese la fecha.
	 * @param titulo el título del cuadro de diálogo que contiene la solicitud de la fecha.
	 * @return la fecha introducida por el usuario, como un objeto {@link LocalDate}.
	 */
	public LocalDate obtenerEntradaFecha(String mensaje, String titulo) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDate fecha = null;

		while (fecha == null) {
			try {
				String entrada = JOptionPane.showInputDialog(null, mensaje, titulo, JOptionPane.QUESTION_MESSAGE);

				if (entrada == null || entrada.trim().isEmpty()) {
					JOptionPane.showMessageDialog(null, "La fecha no puede estar vacía. Por favor, intente de nuevo.",
							"Error", JOptionPane.ERROR_MESSAGE);
					continue;
				}

				fecha = LocalDate.parse(entrada.trim(), formatter);
			} catch (DateTimeParseException e) {
				JOptionPane.showMessageDialog(null, "Formato de fecha inválido. Use el formato yyyy-MM-dd.", "Error",
						JOptionPane.ERROR_MESSAGE);
			}
		}

		return fecha;
	}

	/**
	 * Valida que la fecha de inicio no sea posterior a la fecha de fin.
	 * 
	 * <p>Este método verifica que la fecha de inicio no sea posterior a la fecha de fin. Si es el caso,
	 * se muestra un mensaje de error y se retorna {@code false}. Si la validación es correcta, se retorna {@code true}.</p>
	 * 
	 * @param fechaInicio la fecha de inicio a validar.
	 * @param fechaFin la fecha de fin a validar.
	 * @return {@code true} si la fecha de inicio no es posterior a la fecha de fin; {@code false} en caso contrario.
	 */
	public boolean validarFechas(LocalDate fechaInicio, LocalDate fechaFin) {
		if (fechaInicio.isAfter(fechaFin)) {
			JOptionPane.showMessageDialog(null,
					"La fecha de inicio no puede ser posterior a la fecha de fin. Por favor, intente de nuevo.",
					"Error", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		return true;
	}

		

	////////////////////////////////////////////////////////////////////////////////
	//////                    SECCIÓN PAISES                            	   /////                                                    
	//////                                                                    //////
	///////////////////////////////////////////////////////////////////////////////
	
	
	/**
	 * Muestra la lista de países disponibles cargada desde el archivo XML.
	 * 
	 * @return ID del país seleccionado por el usuario o null si cancela
	 */
	public String mostrarPaises() {
		try {
			// Crear un parser
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document documento = builder.parse(new File("src/main/resources/paises.xml"));
			documento.getDocumentElement().normalize();

			// Crear una lista con todos los nodos país
			NodeList paises = documento.getElementsByTagName("pais");
			String[] columnas = { "ID", "País" };

			DefaultTableModel modeloTabla = new DefaultTableModel(columnas, 0) {
				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
				public boolean isCellEditable(int row, int column) {
					return false; // Evita que el usuario pueda editar las celdas de la tabla
				}
			};

			// Rellenar el modelo con datos del archivo paises.xml
			for (int i = 0; i < paises.getLength(); i++) {
				Node pais = paises.item(i);
				if (pais.getNodeType() == Node.ELEMENT_NODE) {
					Element elemento = (Element) pais;
					String id = getNodo("id", elemento);
					String nombrePais = getNodo("nombre", elemento);
					modeloTabla.addRow(new Object[] { id, nombrePais });
				}
			}

			// Tabla
			JTable tablaPaises = new JTable(modeloTabla);
			tablaPaises.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

			JTextField inputField = new JTextField();
			inputField.setColumns(10);

			// Panel Tabla
			JPanel panel = new JPanel(new BorderLayout(5, 5));
			panel.add(new JScrollPane(tablaPaises), BorderLayout.CENTER);

			// InputField para la inserción del ID
			JPanel inputPanel = new JPanel(new BorderLayout(5, 5));
			inputPanel.add(new JLabel("Introduce tu nacionalidad:"), BorderLayout.WEST);
			inputPanel.add(inputField, BorderLayout.CENTER);
			panel.add(inputPanel, BorderLayout.SOUTH);

			// Añadir el panel principal a un JOptionPane.showConfirmDialog
			int option = JOptionPane.showConfirmDialog(null, panel, "Países disponibles:", JOptionPane.OK_CANCEL_OPTION,
					JOptionPane.INFORMATION_MESSAGE);

			if (option == JOptionPane.OK_OPTION) {
				return inputField.getText().trim();
			} else {
				return null; // cancelado
			}

		} catch (ParserConfigurationException | SAXException | IOException ex) {
			System.err.println("Error: " + ex.getMessage());
		}
		return null;
	}

	/**
	 * Valida si una nacionalidad es válida según los datos del archivo XML.
	 * 
	 * @param nacionalidad ID de la nacionalidad.
	 * @return true si es válida; false en caso contrario.
	 */
	private boolean validarPais(String nacionalidad) {
		try {
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document documento = builder.parse(new File("src/main/resources/paises.xml"));
			documento.getDocumentElement().normalize();
			NodeList paises = documento.getElementsByTagName("pais");

			for (int i = 0; i < paises.getLength(); i++) {
				Element elemento = (Element) paises.item(i);
				String pais = getNodo("nombre", elemento);
				if (pais.equalsIgnoreCase(nacionalidad)) {
					return true;
				}
			}
		} catch (ParserConfigurationException | SAXException | IOException ex) {
			System.err.println("Error: " + ex.getMessage());
		}
		return false;
	}

	
	/**
	 * Obtiene el valor de un nodo XML específico.
	 * 
	 * @param etiqueta Nombre de la etiqueta.
	 * @param elem     Elemento XML.
	 * @return Valor del nodo como cadena.
	 */
	private static String getNodo(String etiqueta, Element elem) {
		NodeList nodo = elem.getElementsByTagName(etiqueta).item(0).getChildNodes();
		Node valorNodo = nodo.item(0);
		return valorNodo.getNodeValue();
	}
}
