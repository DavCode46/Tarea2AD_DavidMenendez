package davidmb.controllers;

import java.awt.BorderLayout;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

import davidmb.main.Sesion;
import davidmb.models.Carnet;
import davidmb.models.Parada;
import davidmb.models.Peregrino;
import davidmb.models.Perfil;
import davidmb.models.Usuario;

/**
 * La clase Sistema se encarga de gestionar el sistema de registro y autenticación de credenciales,
 * así como el manejo de paradas y peregrinos. Proporciona métodos para validar, registrar usuarios,
 * y gestionar la información de peregrinos en el sistema.
 * 
 * Utiliza archivos de texto y objetos serializables para almacenar los datos de credenciales y
 * paradas, y utiliza un archivo XML para cargar la lista de países disponibles.
 */
public class ControladorPrincipal {
	
	private ControladorPrincipal controladorPrincipal;

	private Map<String, Sesion> credenciales = new HashMap<>();

	private Map<String, Parada> paradas = new HashMap<>();

	 /**
     * Constructor que inicializa el sistema con los archivos de credenciales y paradas.
     * @param archivoCredenciales Ruta del archivo de credenciales.
     * @param archivoParadas Ruta del archivo de paradas.
     */
	public ControladorPrincipal() {
		super();		
	}
	
	public ControladorPrincipal(ControladorPrincipal controladorPrincipal) {
		super();
		this.controladorPrincipal = controladorPrincipal;
	}

	  /**
     * Obtiene el perfil asociado a un nombre de usuario.
     * @param nombreUsuario Nombre de usuario.
     * @return Perfil del usuario o null si no existe.
     */
	public Perfil obtenerPerfil(String nombreUsuario) {
		Sesion s = credenciales.get(nombreUsuario);

		System.out.print(s.getPerfil());
		return (s != null) ? s.getPerfil() : null;
	}


	/**
     * Obtiene el ID asociado a un nombre de usuario.
     * @param nombreUsuario Nombre de usuario.
     * @return ID del usuario o null si no existe.
     */
	public Long getId(String nombreUsuario) {
		Sesion u = credenciales.get(nombreUsuario);

		return (u != null) ? u.getId() : null;
	}
	
	public boolean usuarioExiste(String usuario) {
		UsuariosController uc = new UsuariosController();
		return uc.usuarioExiste(usuario);
	}
	
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


	/**
	 * 	
	 * @param archivoParadas
	 * @param archivoCredenciales
	 * @return
	 */
	public Peregrino registrarPeregrino() {
		ParadasController pc = new ParadasController();
		PeregrinosController pec = new PeregrinosController();
		Peregrino nuevoPeregrino = null;
		JOptionPane.showMessageDialog(null, "Formulario de registro de nuevo peregrino");

		// Obtener datos del usuario
		String nombre = obtenerEntrada("Ingrese su nombre", "Nombre", false);
		if (nombre == null)
			return null;

		String contrasenia = obtenerEntrada("Ingrese su contraseña", "Contraseña", false);
		if (contrasenia == null)
			return null;

		String nacionalidad = "";
		do {
			nacionalidad = mostrarPaises();
			if(!validarPais(nacionalidad)){
				JOptionPane.showMessageDialog(null, "El país ingresado no es válido.");
			}
		} while(!validarPais(nacionalidad));
		
		String parada = "";
		do {
			parada = obtenerEntrada(mostrarParadas(true), "Parada", false);
			if(!pc.paradaExiste(parada)){
				JOptionPane.showMessageDialog(null, "La parada seleccionada no es válida.");
			}
		} while(!pc.paradaExiste(parada));

		
		// Confirmar los datos
		nuevoPeregrino = confirmarDatos(nombre, contrasenia, nacionalidad, parada);

		if (nuevoPeregrino == null) {
			// Si la confirmación falla, pedir nuevos datos

			nuevoPeregrino = obtenerDatosModificados(nombre, contrasenia, nacionalidad, parada);

		}
		Optional<Parada> paradaEncontradaOptional = pc.obtenerParadaPorNombre(nombre);
		Parada paradaEncontrada = paradaEncontradaOptional.orElse(null);
		
		paradaEncontrada.getPeregrinos().add(nuevoPeregrino.getId());
		Optional<Long> idPeregrinoOptional = pec.insertarPeregrino(nuevoPeregrino);
		if(idPeregrinoOptional.isPresent()) {
			JOptionPane.showMessageDialog(null, "Peregrino insertado correctamente");
		} else {
			JOptionPane.showMessageDialog(null, "Error al insertar correctamente");
		}
		return nuevoPeregrino;
	}

	/**
	 * Muestra un JOptionPane con los datos introducidos por el usuario
	 * para que este los confirme
	 * @param nombre
	 * @param contrasenia
	 * @param nacionalidad
	 * @param parada
	 * @param archivoCredenciales
	 * @return Peregrino con los datos introducidos por el usuario
	 */
	private Peregrino confirmarDatos(String nombre, String contrasenia, String nacionalidad, String parada) {
		UsuariosController uc = new UsuariosController();
		ParadasController pc = new ParadasController();
		String mensajeFormateado = String.format("Verifica que los datos son correctos \n" + "Nombre: %s \n" + "Contraseña: %s\n"
				+ "Nacionalidad: %s\n" + "Parada actual: %s\n", nombre, contrasenia, nacionalidad, parada);

		int confirmacion = JOptionPane.showConfirmDialog(null, mensajeFormateado, "Confirma",
				JOptionPane.YES_NO_OPTION);
		if (confirmacion == JOptionPane.YES_OPTION) {
			// Datos correctos --> Continuar
			if (usuarioExiste(nombre)) {
				JOptionPane.showMessageDialog(null, "El usuario ya existe");
				return null;
			} else {
				
				Optional<Parada> paradaObjOptional = pc.obtenerParadaPorNombre(parada);
				Parada paradaObj = paradaObjOptional.orElse(null);
				Usuario u = new Usuario(nombre, contrasenia, "peregrino");
				Optional<Long> idUsuario = uc.insertarUsuario(u);
				Long idUsuarioValue = idUsuario.orElse(null);
				Peregrino nuevoPeregrino = new Peregrino(nombre, nacionalidad, new Carnet(paradaObj), idUsuarioValue);
				nuevoPeregrino.getParadas().add(paradaObj.getId());
				paradaObj.getPeregrinos().add(nuevoPeregrino.getId());
				return nuevoPeregrino;
			}
		}
		return null; // Si el usuario cancela
	}

	/**
	 * Vuelve a mostrar los formularios para que el usuario modifique
	 * sus datos, solo se muestra si el usuario indica que los datos 
	 * mostrados en confirmarDatos no son correctos
	 * @param archivoCredenciales
	 * @param nombre
	 * @param contrasenia
	 * @param nacionalidad
	 * @param parada
	 * @return Peregrino con los datos modificados
	 */
	private Peregrino obtenerDatosModificados(String nombre, String contrasenia, String nacionalidad, String parada) {
		UsuariosController uc = new UsuariosController();
		ParadasController pc = new ParadasController();
		// Pide los nuevos datos al usuario
		String nuevoNombre = obtenerEntrada("Ingrese su nuevo nombre", nombre, false);
		if (nuevoNombre == null)
			return null;

		String nuevaContrasenia = obtenerEntrada("Ingrese su nueva contraseña", contrasenia, false);
		if (nuevaContrasenia == null)
			return null;

		String nuevaNacionalidad = mostrarPaises();
		if (nuevaNacionalidad == null)
			return null;

		String nuevaParada = obtenerEntrada(mostrarParadas(true), parada, false);
		if (nuevaParada == null)
			return null;

		if (usuarioExiste(nombre)) {
			JOptionPane.showMessageDialog(null, "El usuario ya existe");
			return null;
		} else {
			
			Optional<Parada> paradaObjOptional = pc.obtenerParadaPorNombre(parada);
			Parada paradaObj = paradaObjOptional.orElse(null);
			Usuario u = new Usuario(nombre, contrasenia, "peregrino");
			Optional<Long> idUsuario = uc.insertarUsuario(u);
			Long idUsuarioValue = idUsuario.orElse(null);
			Peregrino nuevoPeregrino = new Peregrino(nombre, nacionalidad, new Carnet(paradaObj), idUsuarioValue);
			nuevoPeregrino.getParadas().add(paradaObj.getId());
			paradaObj.getPeregrinos().add(nuevoPeregrino.getId());
			return nuevoPeregrino;
		}
	}


	 /**
     * Muestra las paradas registradas en el sistema.
     * @param archivoParadas Archivo de paradas.
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
     * @param archivoParadas Archivo de paradas.
     * @param nombre Nombre de la parada.
     * @param region Región de la parada.
     * @param responsable Responsable de la parada.
     * @return true si el registro fue exitoso; false en caso contrario.
     */
	public boolean registrarParada(String nombre, char region, String responsable) {
		ParadasController pc = new ParadasController();
		if (pc.paradaExiste(nombre)) {
			JOptionPane.showMessageDialog(null, "La parada ya existe");
			return false;
		}
		
		Parada nuevaParada = new Parada(nombre, region, responsable);
		Optional<Long> idParadaInsertada = pc.insertarParada(nuevaParada);
		if(idParadaInsertada.isPresent()) {
			JOptionPane.showMessageDialog(null, "Parada registrada con éxito");
			return true;
		}

		return false;
	}
	
	public boolean paradaExiste(String nombre) {
		ParadasController pc = new ParadasController();
		return pc.paradaExiste(nombre);
	}

	/**
     * Verifica si una parada existe en el sistema.
     * @param nombre Nombre de la parada.
     * @return true si la parada existe; false en caso contrario.
     */
//	public boolean paradaExiste(String nombre) {
//		ParadasController pc = new ParadasController();
//		return pc.paradaExiste(nombre);
//	}

	 /**
     * Muestra la lista de países disponibles cargada desde el archivo XML.
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
	        String[] columnas = {"ID", "País"}; 

	       
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
	                modeloTabla.addRow(new Object[]{id, nombrePais}); 
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
	        inputPanel.add(new JLabel("Introduce el ID de tu país:"), BorderLayout.WEST);
	        inputPanel.add(inputField, BorderLayout.CENTER);
	        panel.add(inputPanel, BorderLayout.SOUTH); 

	        // Añadir el panel principal a un JOptionPane.showConfirmDialog
	        int option = JOptionPane.showConfirmDialog(null, panel, "Países disponibles:", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);

	        
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
	            String idPais = getNodo("id", elemento);
	            if (idPais.equalsIgnoreCase(nacionalidad)) {
	                return true; 
	            }
	        }
	    } catch (ParserConfigurationException | SAXException | IOException ex) {
	        System.err.println("Error: " + ex.getMessage());
	    }
	    return false; 
	}

	/**
     * Valida un String según los criterios establecidos (no vacío, sin espacios iniciales, etc.).
     * @param str String a validar.
     * @param esMenu true si es una entrada de menú; false si es un nombre.
     * @return true si es válido; false en caso contrario.
     */
	public boolean validarStr(String str, boolean esMenu) {

		if (str.isEmpty()) {
			return false;
		}

		if (str.trim().isEmpty()) {
			return false;
		}

		if(!esMenu) {
			if (Character.isDigit(str.charAt(0))) {
				return false;
			}
		}

		if (str.contains(" ")) {
			return false;
		}

		return true;
	}

	 /**
     * Solicita al usuario una entrada y valida el valor según los criterios establecidos.
     * @param mensaje Mensaje a mostrar al usuario.
     * @param titulo Título de la entrada.
     * @param esMenu true si la entrada es una opción de menú; false si es un valor de campo.
     * @return Entrada validada como cadena.
     */
	public String obtenerEntrada(String mensaje, String titulo, boolean esMenu) {
		String entrada;
		do {
			entrada = JOptionPane.showInputDialog(null, mensaje, titulo);
			if (entrada == null) {
				JOptionPane.showMessageDialog(null, "Operación cancelada.");
				return null; // Salir si se cancela
			}
			if (!validarStr(entrada, esMenu)) {
				if(!esMenu) {
					JOptionPane.showMessageDialog(null,
							" El campo " + titulo + " no puede contener espacios ni empezar por números.");
				} else {
					JOptionPane.showMessageDialog(null, "Selecciona una opción entre las disponibles.");
				}
				
			}
			
		} while (!validarStr(entrada, esMenu));
		return entrada.trim();
	}

	 /**
     * Obtiene el valor de un nodo XML específico.
     * @param etiqueta Nombre de la etiqueta.
     * @param elem Elemento XML.
     * @return Valor del nodo como cadena.
     */
	private static String getNodo(String etiqueta, Element elem) {
		NodeList nodo = elem.getElementsByTagName(etiqueta).item(0).getChildNodes();
		Node valorNodo = nodo.item(0);
		return valorNodo.getNodeValue();
	}
}



