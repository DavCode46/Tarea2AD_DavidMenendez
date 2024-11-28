package davidmb.dao;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * Clase para gestionar la conexión a la base de datos.
 * <p>Esta clase implementa el patrón de diseño Singleton para garantizar que haya solo una
 * instancia de la conexión a la base de datos a lo largo de la aplicación. También se encarga
 * de cargar la configuración desde un archivo de propiedades, crear la base de datos si es necesario
 * y gestionar la conexión con la base de datos.</p>
 * <p>La clase implementa la interfaz {@link AutoCloseable}, lo que permite cerrar la conexión de
 * manera automática cuando se usa en un bloque try-with-resources.</p>
 */
public class ConexionDB implements AutoCloseable {

	private static ConexionDB instancia;


	private static String url;
	private static String user;
	private static String password;
	private static String driver;
	private static String archivoSQL;

	private Connection conexion;

	 /**
     * Constructor privado que carga las propiedades necesarias desde el archivo `application.properties`.
     * <p>Este constructor carga las propiedades para establecer la conexión a la base de datos
     * (URL, usuario, contraseña, controlador JDBC y el archivo SQL) desde el archivo de configuración
     * <code>application.properties</code>.</p>
     * 
     * @throws IllegalArgumentException si faltan propiedades requeridas en el archivo de configuración.
     */
	private ConexionDB() {
		try (FileInputStream fis = new FileInputStream("src/main/resources/application.properties")) {
			Properties prop = new Properties();
			prop.load(fis);

			url = prop.getProperty("db.url");
			user = prop.getProperty("db.user");
			password = prop.getProperty("db.password");
			driver = prop.getProperty("db.driver");
			archivoSQL = prop.getProperty("scriptSql");

			if (url == null || user == null || password == null || driver == null || archivoSQL == null) {
				throw new IllegalArgumentException("Faltan propiedades requeridas en application.properties");
			}

			Class.forName(driver);
		} catch (IOException | ClassNotFoundException ex) {
			ex.printStackTrace();
		}
	}

	  /**
     * Obtiene la instancia única de la clase {@link ConexionDB}.
     * <p>Este método asegura que solo exista una instancia de la conexión a la base de datos.</p>
     * 
     * @return la instancia de {@link ConexionDB}.
     */
	public static synchronized ConexionDB getInstancia() {
		if (instancia == null) {
			instancia = new ConexionDB();
		}
		return instancia;
	}

	/**
     * Obtiene la conexión a la base de datos.
     * <p>Este método devuelve la conexión a la base de datos. Si la conexión actual está cerrada o no existe,
     * crea una nueva conexión.</p>
     * 
     * @return la conexión a la base de datos.
     */
	public synchronized Connection getConexion() {
		try {
			if (conexion == null || conexion.isClosed()) { 

				conexion = DriverManager.getConnection(url, user, password);
				
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		
		}
		return conexion;
	}

	  /**
     * Crea la base de datos utilizando un script SQL.
     * <p>Este método lee un archivo SQL desde la ruta configurada en <code>application.properties</code>
     * y ejecuta el script para crear la base de datos. El archivo debe contener instrucciones SQL válidas.</p>
     */
	public void crearDB() {
		try (Connection con = getServidorConexion();
				Statement stmt = con.createStatement();
				BufferedReader br = new BufferedReader(new FileReader(archivoSQL))) {

			StringBuilder scriptSQL = new StringBuilder();
			String linea;

			while ((linea = br.readLine()) != null) {
				linea = linea.trim();
				if (!linea.isEmpty() && !linea.startsWith("--")) {
					scriptSQL.append(linea);
					if (linea.endsWith(";")) {
						stmt.execute(scriptSQL.toString());
						scriptSQL.setLength(0);
					}
				}
			}
			
		} catch (IOException | SQLException ex) {
			ex.printStackTrace();
		}
	}

	/**
     * Obtiene la conexión al servidor de la base de datos (sin base de datos específica).
     * <p>Este método se usa para conectar al servidor de la base de datos antes de crear la base de datos.</p>
     * 
     * @return la conexión al servidor de la base de datos.
     */
	private Connection getServidorConexion() {
		try {
			String serverUrl = url.substring(0, url.lastIndexOf("/") + 1);
			return DriverManager.getConnection(serverUrl, user, password);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
     * Cierra la conexión a la base de datos.
     * <p>Este método cierra la conexión activa a la base de datos, si está abierta.</p>
     */
	public synchronized void cerrarConexion() {
		if (conexion != null) {
			try {
				conexion.close();
				conexion = null;
				
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	  /**
     * Cierra la conexión de manera automática al final del bloque try-with-resources.
     * <p>Este método implementa la interfaz {@link AutoCloseable}, lo que permite que la conexión se cierre
     * automáticamente cuando se usa en un bloque try-with-resources.</p>
     */
	@Override
	public void close() {
		cerrarConexion();
	}

	 /**
     * Método principal para probar la conexión a la base de datos.
     * <p>Este método se utiliza para probar la creación de la base de datos y la conexión a la base de datos.</p>
     * 
     * @param args los argumentos de la línea de comandos.
     */
	public static void main(String[] args) {
		try (ConexionDB conexionDB = ConexionDB.getInstancia()) {
			conexionDB.crearDB();
			Connection conn = conexionDB.getConexion();
			if (conn != null) {
				System.out.print("Conexión a BD");
			} else {
				System.out.print("Error al conectar a la base de datos");
			}
		}
	}
}
