//package davidmb.controllers;
//
//import java.awt.BorderLayout;
//import java.io.BufferedReader;
//import java.io.BufferedWriter;
//import java.io.EOFException;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.FileReader;
//import java.io.FileWriter;
//import java.io.IOException;
//import java.io.ObjectInputStream;
//import java.io.ObjectOutputStream;
//import java.util.HashMap;
//import java.util.Map;
//
//import javax.swing.JLabel;
//import javax.swing.JOptionPane;
//import javax.swing.JPanel;
//import javax.swing.JScrollPane;
//import javax.swing.JTable;
//import javax.swing.JTextField;
//import javax.swing.table.DefaultTableModel;
//import javax.xml.parsers.DocumentBuilder;
//import javax.xml.parsers.DocumentBuilderFactory;
//import javax.xml.parsers.ParserConfigurationException;
//
//import org.w3c.dom.Document;
//import org.w3c.dom.Element;
//import org.w3c.dom.Node;
//import org.w3c.dom.NodeList;
//import org.xml.sax.SAXException;
//
//import davidmb.main.Sesion;
//import davidmb.models.Carnet;
//import davidmb.models.Parada;
//import davidmb.models.Peregrino;
//import davidmb.models.Perfil;
//
///**
// * La clase Sistema se encarga de gestionar el sistema de registro y autenticación de credenciales,
// * así como el manejo de paradas y peregrinos. Proporciona métodos para validar, registrar usuarios,
// * y gestionar la información de peregrinos en el sistema.
// * 
// * Utiliza archivos de texto y objetos serializables para almacenar los datos de credenciales y
// * paradas, y utiliza un archivo XML para cargar la lista de países disponibles.
// */
//public class Sistema {
//
//	private String archivoCredenciales;
//	private String archivoParadas;
//
//	private Map<String, Sesion> credenciales = new HashMap<>();
//
//	private Map<String, Parada> paradas = new HashMap<>();
//
//	 /**
//     * Constructor que inicializa el sistema con los archivos de credenciales y paradas.
//     * @param archivoCredenciales Ruta del archivo de credenciales.
//     * @param archivoParadas Ruta del archivo de paradas.
//     */
//	public Sistema(String archivoCredenciales, String archivoParadas) {
//		this.archivoCredenciales = archivoCredenciales;
//		this.archivoParadas = archivoParadas;
//		cargarCredenciales(archivoCredenciales);
//		cargarParadas(archivoParadas);
//	}
//
//	  /**
//     * Obtiene el perfil asociado a un nombre de usuario.
//     * @param nombreUsuario Nombre de usuario.
//     * @return Perfil del usuario o null si no existe.
//     */
//	public Perfil obtenerPerfil(String nombreUsuario) {
//		Sesion s = credenciales.get(nombreUsuario);
//
//		System.out.print(s.getPerfil());
//		return (s != null) ? s.getPerfil() : null;
//	}
//
//	/**
//     * Obtiene una parada según su nombre.
//     * @param nombreParada Nombre de la parada.
//     * @return Objeto Parada asociado al nombre, o null si no existe.
//     */
//	public Parada obtenerParada(String nombreParada) {
//		return paradas.get(nombreParada);
//	}
//
//	/**
//     * Obtiene el ID asociado a un nombre de usuario.
//     * @param nombreUsuario Nombre de usuario.
//     * @return ID del usuario o null si no existe.
//     */
//	public Long getId(String nombreUsuario) {
//		Sesion u = credenciales.get(nombreUsuario);
//
//		return (u != null) ? u.getId() : null;
//	}
//
//	 /**
//     * Calcula el siguiente ID disponible para un tipo específico de usuario.
//     * @param archivoCredenciales Archivo de credenciales.
//     * @param esPeregrino Si es true, considera solo usuarios peregrinos; si no, paradas.
//     * @return El siguiente ID disponible.
//     */
//	public Long obtenerSiguienteId(String archivoCredenciales, boolean esPeregrino) {
//		long maxId = 0;
//
//		try (BufferedReader br = new BufferedReader(new FileReader(archivoCredenciales))) {
//			String linea;
//			while ((linea = br.readLine()) != null) {
//				String[] credencial = linea.split(" ");
//
//				if (esPeregrino && credencial[2].startsWith("peregrino")) {
//					Long id = Long.parseLong(credencial[3]);
//					if (id > maxId) {
//						maxId = id;
//					}
//				} else if (!esPeregrino && credencial[2].startsWith("parada")) {
//					Long id = Long.parseLong(credencial[3]);
//					if (id > maxId) {
//						maxId = id;
//					}
//				}
//			}
//		} catch (IOException ex) {
//			ex.printStackTrace();
//		}
//
//		return maxId + 1;
//	}
//
//	/**
//     * Carga las credenciales desde el archivo especificado.
//     * @param archivoCredenciales Ruta del archivo de credenciales.
//     */
//	private void cargarCredenciales(String archivoCredenciales) {
//		try (BufferedReader br = new BufferedReader(new FileReader(archivoCredenciales))) {
//			String linea;
//			while ((linea = br.readLine()) != null) {
//				String[] credencial = linea.split(" ");
//				String usuario = credencial[0];
//				String perfilString = credencial[2];
//				Perfil perfil = Perfil.valueOf(perfilString.toLowerCase());
//				Long id = Long.parseLong(credencial[3]);
//
//				Sesion nuevoUsuario = new Sesion(usuario, perfil, id);
//
//				credenciales.put(usuario, nuevoUsuario);
//			}
//		} catch (IOException ex) {
//			ex.printStackTrace();
//		}
//	}
//
//	 /**
//     * Valida las credenciales de un usuario.
//     * @param archivoCredenciales Archivo de credenciales.
//     * @param nombreUsuario Nombre de usuario.
//     * @param contrasenia Contraseña del usuario.
//     * @return true si las credenciales son válidas; false en caso contrario.
//     */
//	public boolean validarCredenciales(String archivoCredenciales, String nombreUsuario, String contrasenia) {
//		try (BufferedReader br = new BufferedReader(new FileReader(archivoCredenciales))) {
//			String linea;
//			while ((linea = br.readLine()) != null) {
//				String[] credencial = linea.split(" ");
//				String usuario = credencial[0];
//				String contraseniaGuardada = credencial[1];
//
//				if (usuario.equals(nombreUsuario)) {
//
//					return contraseniaGuardada.equals(contrasenia);
//				}
//			}
//		} catch (IOException ex) {
//			ex.printStackTrace();
//		}
//		return false;
//	}
//
//	 /**
//     * Registra un nuevo usuario en el sistema.
//     * @param archivoCredenciales Archivo de credenciales.
//     * @param nombre Nombre del usuario.
//     * @param contrasenia Contraseña del usuario.
//     * @param perfilString Perfil del usuario como cadena.
//     * @param esPeregrino true si es peregrino; false si es una parada.
//     * @return true si el registro fue exitoso; false en caso contrario.
//     */
//	public boolean registrarCredenciales(String archivoCredenciales, String nombre, String contrasenia,
//			String perfilString, boolean esPeregrino) {
//		try (BufferedWriter bw = new BufferedWriter(new FileWriter(archivoCredenciales, true))) {
//			if (credenciales.containsKey(nombre)) {
//				JOptionPane.showMessageDialog(null, "El usuario ya existe");
//				return false;
//			}
//
//			Perfil perfil = Perfil.valueOf(perfilString.toLowerCase());
//			Long id;
//			if (esPeregrino) {
//				id = obtenerSiguienteId(archivoCredenciales, true);
//				Sesion nuevoUsuario = new Sesion(nombre, perfil, id);
//				credenciales.put(nombre, nuevoUsuario);
//			} else {
//				id = obtenerSiguienteId(archivoCredenciales, false);
//				Sesion nuevoUsuario = new Sesion(nombre, perfil, id);
//				credenciales.put(nombre, nuevoUsuario);
//			}
//			String usuarioFormateado = String.format("%s %s %s %,d", nombre, contrasenia, perfil, id);
//			bw.write(usuarioFormateado);
//			bw.newLine();
//			JOptionPane.showMessageDialog(null, "Usuario registrado con éxito\n" + "Nombre: " + nombre
//					+ "\nContraseña: " + contrasenia + "\nPerfil: " + perfil + "\nID: " + id);
//			return true;
//		} catch (FileNotFoundException ex) {
//			System.out.println("Fichero no encontrado");
//			return false;
//		} catch (IOException ex) {
//			ex.printStackTrace();
//			return false;
//		}
//	}
//
//	/**
//     * Inicia el proceso de registro para un nuevo peregrino.
//     * @return Objeto Peregrino registrado o null si el proceso se cancela.
//     */
////	public Peregrino registrarPeregrino() {
////
////		return registrarPeregrino(archivoParadas, archivoCredenciales);
////	}
//
//	/**
//	 * 	
//	 * @param archivoParadas
//	 * @param archivoCredenciales
//	 * @return
//	 */
////	private Peregrino registrarPeregrino(String archivoParadas, String archivoCredenciales) {
////		Peregrino nuevoPeregrino = null;
////		JOptionPane.showMessageDialog(null, "Formulario de registro de nuevo peregrino");
////
////		// Obtener datos del usuario
////		String nombre = obtenerEntrada("Ingrese su nombre", "Nombre", false);
////		if (nombre == null)
////			return null;
////
////		String contrasenia = obtenerEntrada("Ingrese su contraseña", "Contraseña", false);
////		if (contrasenia == null)
////			return null;
////
////		String nacionalidad = "";
////		do {
////			nacionalidad = mostrarPaises();
////			if(!validarPais(nacionalidad)){
////				JOptionPane.showMessageDialog(null, "El país ingresado no es válido.");
////			}
////		} while(!validarPais(nacionalidad));
////		
////		String parada = "";
////		do {
////			parada = obtenerEntrada(mostrarParadas(archivoParadas, true), "Parada", false);
////			if(!paradaExiste(parada)){
////				JOptionPane.showMessageDialog(null, "La parada seleccionada no es válida.");
////			}
////		} while(!paradaExiste(parada));
////
////		
////		// Confirmar los datos
////		nuevoPeregrino = confirmarDatos(nombre, contrasenia, nacionalidad, parada, archivoCredenciales);
////
////		if (nuevoPeregrino == null) {
////			// Si la confirmación falla, pedir nuevos datos
////
////			nuevoPeregrino = obtenerDatosModificados(archivoCredenciales, archivoParadas, nombre, contrasenia, nacionalidad, parada);
////
////		}
////		Parada paradaEncontrada = obtenerParada(parada);
////		paradaEncontrada.getPeregrinos().add(nuevoPeregrino);
////
////		return nuevoPeregrino;
////	}
//
//	/**
//	 * Muestra un JOptionPane con los datos introducidos por el usuario
//	 * para que este los confirme
//	 * @param nombre
//	 * @param contrasenia
//	 * @param nacionalidad
//	 * @param parada
//	 * @param archivoCredenciales
//	 * @return Peregrino con los datos introducidos por el usuario
//	 */
////	private Peregrino confirmarDatos(String nombre, String contrasenia, String nacionalidad, String parada,
////			String archivoCredenciales) {
////		String mensajeFormateado = String.format("Verifica que los datos son correctos \n" + "Nombre: %s \n" + "Contraseña: %s\n"
////				+ "Nacionalidad: %s\n" + "Parada actual: %s\n", nombre, contrasenia, nacionalidad, parada);
////
////		int confirmacion = JOptionPane.showConfirmDialog(null, mensajeFormateado, "Confirma",
////				JOptionPane.YES_NO_OPTION);
////		if (confirmacion == JOptionPane.YES_OPTION) {
////			// Datos correctos --> Continuar
////			if (validarCredenciales(archivoCredenciales, nombre, contrasenia)) {
////				JOptionPane.showMessageDialog(null, "El usuario ya existe");
////				return null;
////			} else {
////				Long id = obtenerSiguienteId(archivoCredenciales, true);
////				Parada paradaObj = obtenerParada(parada);
////				Peregrino nuevoPeregrino = new Peregrino(id, nombre, nacionalidad, new Carnet(id, paradaObj));
////				nuevoPeregrino.getParadas().add(paradaObj);
////				paradaObj.getPeregrinos().add(nuevoPeregrino);
////				registrarCredenciales(archivoCredenciales, nombre, contrasenia,
////						Perfil.peregrino.toString().toLowerCase(), true);
////				return nuevoPeregrino;
////			}
////		}
////		return null; // Si el usuario cancela
////	}
//
//	/**
//	 * Vuelve a mostrar los formularios para que el usuario modifique
//	 * sus datos, solo se muestra si el usuario indica que los datos 
//	 * mostrados en confirmarDatos no son correctos
//	 * @param archivoCredenciales
//	 * @param nombre
//	 * @param contrasenia
//	 * @param nacionalidad
//	 * @param parada
//	 * @return Peregrino con los datos modificados
//	 */
////	private Peregrino obtenerDatosModificados(String archivoCredenciales, String archivoParadas, String nombre, String contrasenia,
////			String nacionalidad, String parada) {
////		// Pide los nuevos datos al usuario
////		String nuevoNombre = obtenerEntrada("Ingrese su nuevo nombre", nombre, false);
////		if (nuevoNombre == null)
////			return null;
////
////		String nuevaContrasenia = obtenerEntrada("Ingrese su nueva contraseña", contrasenia, false);
////		if (nuevaContrasenia == null)
////			return null;
////
////		String nuevaNacionalidad = mostrarPaises();
////		if (nuevaNacionalidad == null)
////			return null;
////
////		String nuevaParada = obtenerEntrada(mostrarParadas(archivoParadas, true), parada, false);
////		if (nuevaParada == null)
////			return null;
////
////		if (validarCredenciales(archivoCredenciales, nombre, contrasenia)) {
////			JOptionPane.showMessageDialog(null, "El usuario ya existe");
////			return null;
////		} else {
////			Long id = obtenerSiguienteId(archivoCredenciales, true);
////			Parada paradaObj = obtenerParada(parada);
////			Peregrino nuevoPeregrino = new Peregrino(id, nombre, nacionalidad, new Carnet(id, paradaObj));
////			nuevoPeregrino.getParadas().add(paradaObj);
////			paradaObj.getPeregrinos().add(nuevoPeregrino);
////			registrarCredenciales(archivoCredenciales, nombre, contrasenia,
////					Perfil.peregrino.toString().toLowerCase(), true);
////			return nuevoPeregrino;
////		}
////	}
//
//	/**
//	 * Carga en el sistema las paradas disponibles
//	 * @param archivoParadas
//	 */
//	@SuppressWarnings("unchecked")
//	private void cargarParadas(String archivoParadas) {
//		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(archivoParadas))) {
//			paradas = (Map<String, Parada>) ois.readObject();
//		} catch (FileNotFoundException ex) {
//			System.out.println("No se ha encontrado el archivo de paradas. Creando uno nuevo...");
//
//			paradas = new HashMap<>();
//		} catch (EOFException ex) {
//			System.out.println("El archivo de paradas está vacío. Iniciando con un mapa vacío.");
//
//			paradas = new HashMap<>();
//		} catch (IOException | ClassNotFoundException ex) {
//			ex.printStackTrace();
//		}
//	}
//
//	/**
//	 * Sobreescribe el archivo paradas con las nuevas paradas introducidas 
//	 * por el administrador del sistema, las cuales se pasan mediante un Map
//	 * @param archivoParadas
//	 */
//	private void guardarParadas(String archivoParadas) {
//		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(archivoParadas))) {
//			oos.writeObject(paradas);
//		} catch (IOException ex) {
//			ex.printStackTrace();
//		}
//	}
//
//	 /**
//     * Muestra las paradas registradas en el sistema.
//     * @param archivoParadas Archivo de paradas.
//     * @param isPeregrino Si es true, muestra solo el nombre y región.
//     * @return Lista de paradas formateada como cadena.
//     */
//	@SuppressWarnings("unchecked")
//	public String mostrarParadas(String archivoParadas, boolean isPeregrino) {
//		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(archivoParadas))) {
//			Map<String, Parada> paradasLeidas = (Map<String, Parada>) ois.readObject();
//
//			StringBuilder sb = new StringBuilder("Paradas registradas: \n");
//
//			for (Parada parada : paradasLeidas.values()) {
//				if (isPeregrino) {
//					sb.append("\nNombre: ").append(parada.getNombre());
//					sb.append("\nRegión: ").append(parada.getRegion());
//				} else {
//
//					sb.append("\nID: ").append(parada.getId());
//					sb.append("\nNombre: ").append(parada.getNombre());
//					sb.append("\nRegión: ").append(parada.getRegion());
//					sb.append("\nResponsable: ").append(parada.getResponsable());
//				}
//			}
//
//			return sb.toString();
//		} catch (FileNotFoundException ex) {
//			System.out.println("No se ha encontrado el archivo de paradas.");
//		} catch (EOFException ex) {
//			System.out.println("El archivo de paradas está vacío.");
//		} catch (IOException | ClassNotFoundException ex) {
//			ex.printStackTrace();
//		}
//		return "No se han encontrado paradas";
//	}
//
//	 /**
//     * Registra una nueva parada en el sistema.
//     * @param archivoParadas Archivo de paradas.
//     * @param nombre Nombre de la parada.
//     * @param region Región de la parada.
//     * @param responsable Responsable de la parada.
//     * @return true si el registro fue exitoso; false en caso contrario.
//     */
//	public boolean registrarParada(String archivoParadas, String nombre, char region, String responsable) {
//		if (paradas.containsKey(nombre)) {
//			JOptionPane.showMessageDialog(null, "La parada ya existe");
//			return false;
//		}
//		Long id = (long) paradas.size() + 1;
//		Parada nuevaParada = new Parada(id, nombre, region, responsable);
//		paradas.put(nombre, nuevaParada);
//		guardarParadas(archivoParadas);
//
//		JOptionPane.showMessageDialog(null, "Parada registrada con éxito");
//		return true;
//	}
//
//	/**
//     * Verifica si una parada existe en el sistema.
//     * @param nombre Nombre de la parada.
//     * @return true si la parada existe; false en caso contrario.
//     */
//	public boolean paradaExiste(String nombre) {
//		return paradas.containsKey(nombre);
//	}
//
//	 /**
//     * Muestra la lista de países disponibles cargada desde el archivo XML.
//     * @return ID del país seleccionado por el usuario o null si cancela
//     */
//	public String mostrarPaises() {
//	    try {
//	        // Crear un parser
//	        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
//	        Document documento = builder.parse(new File("src/main/resources/paises.xml"));
//	        documento.getDocumentElement().normalize();
//
//	        // Crear una lista con todos los nodos país
//	        NodeList paises = documento.getElementsByTagName("pais");
//	        String[] columnas = {"ID", "País"}; 
//
//	       
//	        DefaultTableModel modeloTabla = new DefaultTableModel(columnas, 0) {
//	            @Override
//	            public boolean isCellEditable(int row, int column) {
//	                return false; // Evita que el usuario pueda editar las celdas de la tabla
//	            }
//	        };
//
//
//	        // Rellenar el modelo con datos del archivo paises.xml
//	        for (int i = 0; i < paises.getLength(); i++) {
//	            Node pais = paises.item(i);
//	            if (pais.getNodeType() == Node.ELEMENT_NODE) {
//	                Element elemento = (Element) pais;
//	                String id = getNodo("id", elemento);
//	                String nombrePais = getNodo("nombre", elemento);
//	                modeloTabla.addRow(new Object[]{id, nombrePais}); 
//	            }
//	        }
//
//	       // Tabla
//	        JTable tablaPaises = new JTable(modeloTabla);
//	        tablaPaises.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
//
//	       
//	        JTextField inputField = new JTextField();
//	        inputField.setColumns(10); 
//
//	        // Panel Tabla
//	        JPanel panel = new JPanel(new BorderLayout(5, 5));
//	        panel.add(new JScrollPane(tablaPaises), BorderLayout.CENTER);
//	        
//	        // InputField para la inserción del ID
//	        JPanel inputPanel = new JPanel(new BorderLayout(5, 5));
//	        inputPanel.add(new JLabel("Introduce el ID de tu país:"), BorderLayout.WEST);
//	        inputPanel.add(inputField, BorderLayout.CENTER);
//	        panel.add(inputPanel, BorderLayout.SOUTH); 
//
//	        // Añadir el panel principal a un JOptionPane.showConfirmDialog
//	        int option = JOptionPane.showConfirmDialog(null, panel, "Países disponibles:", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);
//
//	        
//	        if (option == JOptionPane.OK_OPTION) {
//	            return inputField.getText().trim(); 
//	        } else {
//	            return null; // cancelado
//	        }
//
//	    } catch (ParserConfigurationException | SAXException | IOException ex) {
//	        System.err.println("Error: " + ex.getMessage());
//	    }
//	    return null;
//	}
//	
//	 /**
//     * Valida si una nacionalidad es válida según los datos del archivo XML.
//     * @param nacionalidad ID de la nacionalidad.
//     * @return true si es válida; false en caso contrario.
//     */
//	private boolean validarPais(String nacionalidad) {
//	    try {
//	        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
//	        Document documento = builder.parse(new File("src/main/resources/paises.xml"));
//	        documento.getDocumentElement().normalize();
//	        NodeList paises = documento.getElementsByTagName("pais");
//
//	        for (int i = 0; i < paises.getLength(); i++) {
//	            Element elemento = (Element) paises.item(i);
//	            String idPais = getNodo("id", elemento);
//	            if (idPais.equalsIgnoreCase(nacionalidad)) {
//	                return true; 
//	            }
//	        }
//	    } catch (ParserConfigurationException | SAXException | IOException ex) {
//	        System.err.println("Error: " + ex.getMessage());
//	    }
//	    return false; 
//	}
//
//	/**
//     * Valida un String según los criterios establecidos (no vacío, sin espacios iniciales, etc.).
//     * @param str String a validar.
//     * @param esMenu true si es una entrada de menú; false si es un nombre.
//     * @return true si es válido; false en caso contrario.
//     */
//	public boolean validarStr(String str, boolean esMenu) {
//
//		if (str.isEmpty()) {
//			return false;
//		}
//
//		if (str.trim().isEmpty()) {
//			return false;
//		}
//
//		if(!esMenu) {
//			if (Character.isDigit(str.charAt(0))) {
//				return false;
//			}
//		}
//
//		if (str.contains(" ")) {
//			return false;
//		}
//
//		return true;
//	}
//
//	 /**
//     * Solicita al usuario una entrada y valida el valor según los criterios establecidos.
//     * @param mensaje Mensaje a mostrar al usuario.
//     * @param titulo Título de la entrada.
//     * @param esMenu true si la entrada es una opción de menú; false si es un valor de campo.
//     * @return Entrada validada como cadena.
//     */
//	public String obtenerEntrada(String mensaje, String titulo, boolean esMenu) {
//		String entrada;
//		do {
//			entrada = JOptionPane.showInputDialog(null, mensaje, titulo);
//			if (entrada == null) {
//				JOptionPane.showMessageDialog(null, "Operación cancelada.");
//				return null; // Salir si se cancela
//			}
//			if (!validarStr(entrada, esMenu)) {
//				if(!esMenu) {
//					JOptionPane.showMessageDialog(null,
//							" El campo " + titulo + " no puede contener espacios ni empezar por números.");
//				} else {
//					JOptionPane.showMessageDialog(null, "Selecciona una opción entre las disponibles.");
//				}
//				
//			}
//			
//		} while (!validarStr(entrada, esMenu));
//		return entrada.trim();
//	}
//
//	 /**
//     * Obtiene el valor de un nodo XML específico.
//     * @param etiqueta Nombre de la etiqueta.
//     * @param elem Elemento XML.
//     * @return Valor del nodo como cadena.
//     */
//	private static String getNodo(String etiqueta, Element elem) {
//		NodeList nodo = elem.getElementsByTagName(etiqueta).item(0).getChildNodes();
//		Node valorNodo = nodo.item(0);
//		return valorNodo.getNodeValue();
//	}
//}
