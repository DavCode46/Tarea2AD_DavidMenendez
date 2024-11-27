package davidmb.controllers;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
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

	// private Map<String, Sesion> credenciales = new HashMap<>();

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

	/**
	 * Obtiene el ID asociado a un nombre de usuario.
	 * 
	 * @param nombreUsuario Nombre de usuario.
	 * @return ID del usuario o null si no existe.
	 */
//	public Long getId(String nombreUsuario) {
//		Sesion u = credenciales.get(nombreUsuario);
//
//		return (u != null) ? u.getId() : null;
//	}

	public boolean validarCredenciales(String nombre, String contrasenia) {
		UsuariosController uc = new UsuariosController();
		return uc.validarCredenciales(nombre, contrasenia);
	}

	public Optional<Usuario> login(String nombre, String contrasenia) {
		UsuariosController uc = new UsuariosController();
		return uc.login(nombre, contrasenia);
	}

	public Optional<Long> insertarUsuario(Usuario u) {
		UsuariosController uc = new UsuariosController();
		return uc.insertarUsuario(u);
	}

	public Optional<Peregrino> obtenerPeregrinoPorIdUsuario(Long id) {
		PeregrinosController pec = new PeregrinosController();
		return pec.obtenerPeregrinoPorIdUsuario(id);
	}

	public Optional<Peregrino> obtenerPeregrinoPorId(Long id) {
		PeregrinosController pec = new PeregrinosController();
		return pec.obtenerPeregrinoPorId(id);
	}

	public Optional<Parada> obtenerParadaPorIdUsuario(Long id) {
		ParadasController pc = new ParadasController();
		return pc.obtenerParadaPorIdUsuario(id);
	}

	public List<Parada> obtenerParadasPorIdPeregrino(Long idPeregrino) {
		ParadasController pc = new ParadasController();
		return pc.obtenerParadasPorIdPeregrino(idPeregrino);
	}

	public List<Estancia> obtenerEstanciasPorIdPeregrino(Long idPeregrino) {
		EstanciasController ec = new EstanciasController();
		return ec.obtenerEstanciasPorIdPeregrino(idPeregrino);
	}

	public List<Estancia> obtenerEstanciasPorIdParada(Long idParada) {
		EstanciasController ec = new EstanciasController();
		return ec.obtenerEstanciasPorIdParada(idParada);
	}

	public Optional<Parada> obtenerParadaPorId(Long id) {
		ParadasController pc = new ParadasController();
		return pc.obtenerParadaPorId(id);
	}

	public boolean modificarCarnet(Carnet carnet) {
		CarnetsController c = new CarnetsController();
		return c.modificarCarnet(carnet);
	}

	public Optional<Long> insertarEstancia(Estancia estancia) {
		EstanciasController e = new EstanciasController();
		return e.insertarEstancia(estancia);
	}

	public String mostrarInformacionParada(Parada parada) {
		return "Parada:\nID: " + parada.getId() + "\nNombre: " + parada.getNombre() + "\nRegión: " + parada.getRegion()
				+ "\nResponsable: " + parada.getResponsable();
	}

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
				if (!pc.paradaExiste(parada)) {
					JOptionPane.showMessageDialog(null, "La parada seleccionada no es válida.");
				}
			} while (!pc.paradaExiste(parada));

			// Confirmar los datos
			nuevoPeregrino = confirmarDatos(nombre, nombreUsuario, contrasenia, nacionalidad, parada);

			do {
				if (nuevoPeregrino == null) {
					// Si la confirmación falla, pedir nuevos datos

					nuevoPeregrino = obtenerDatosModificados(nombre, nombreUsuario, contrasenia, nacionalidad, parada);

				}
			} while (nuevoPeregrino == null);
			if (!pec.nombrePeregrinoExiste(nombre)) {
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
			} else {
				JOptionPane.showMessageDialog(null, "El peregrino ya existe", "Error", JOptionPane.ERROR_MESSAGE);
			}

		} while (pec.nombrePeregrinoExiste(nombre));
		return null;
	}

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
					} else {
						JOptionPane.showMessageDialog(null, "Las fechas no son válidas. Inténtalo de nuevo.");
					}
				}

			} while (!fechasCorrectas);

			continuarExportando = mostrarEstanciasPeregrinos(fechaInicio, fechaFin, parada);
		} while (continuarExportando);
	}

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
				Usuario u = new Usuario(nombreUsuario, contrasenia, "peregrino");
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
			Usuario u = new Usuario(nuevoNombreUsuario, nuevaContrasenia, "peregrino");
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
	 * Muestra las paradas registradas en el sistema.
	 * 
	 * @param isPeregrino Si es true, muestra solo el nombre y región.
	 * @return Lista de paradas formateada como cadena.
	 */
	@SuppressWarnings("unchecked")
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
	 * Registra una nueva parada en el sistema.
	 * 
	 * @return true si el registro fue exitoso; false en caso contrario.
	 */
	public boolean registrarParada(Parada parada) {
		ParadasController pc = new ParadasController();
		if (pc.paradaExiste(parada.getNombre())) {
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

	public boolean paradaExiste(String nombre) {
		ParadasController pc = new ParadasController();
		return pc.paradaExiste(nombre);
	}

	public boolean peregrinoExiste(Long id) {
		PeregrinosController pc = new PeregrinosController();
		return pc.peregrinoExiste(id);
	}

	public Optional<Long> insertarPeregrinosParadas(Long idPeregrino, Long idParada, LocalDate fecha) {
		ParadasController pc = new ParadasController();
		return pc.insertarPeregrinosParadas(idPeregrino, idParada, fecha);
	}

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

	// Mostrar estancias de peregrinos

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
		// buttonPanel.add(new JLabel("¿Deseas exportar las estancias?"),
		// BorderLayout.WEST);
		JButton exportButton = new JButton("Exportar en XML");
//		JPanel labelPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
//		JLabel labelOpciones = new JLabel("¿Deseas introducir otras fechas?");
//		labelPanel.add(labelOpciones);
//		buttonPanel.add(labelPanel, BorderLayout.SOUTH);
	 

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

	public boolean validarFechas(LocalDate fechaInicio, LocalDate fechaFin) {
		if (fechaInicio.isAfter(fechaFin)) {
			JOptionPane.showMessageDialog(null,
					"La fecha de inicio no puede ser posterior a la fecha de fin. Por favor, intente de nuevo.",
					"Error", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		return true;
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
