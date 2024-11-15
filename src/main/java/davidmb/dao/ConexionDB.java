package davidmb.dao;

import java.io.*;
import java.sql.*;
import java.util.Properties;
import java.util.logging.Logger;

public class ConexionDB implements AutoCloseable {

	private static ConexionDB instancia;
	private static final Logger logger = Logger.getLogger(ConexionDB.class.getName());

	private static String url;
	private static String user;
	private static String password;
	private static String driver;
	private static String archivoSQL;

	private Connection conexion;

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
			logger.severe("Error al inicializar la configuración: " + ex.getMessage());
		}
	}

	public static synchronized ConexionDB getInstancia() {
		if (instancia == null) {
			instancia = new ConexionDB();
		}
		return instancia;
	}

	public synchronized Connection getConexion() {
		if (conexion == null) {
			try {
				conexion = DriverManager.getConnection(url, user, password);
				logger.info("Conexión establecida con la base de datos");
			} catch (SQLException e) {
				logger.severe("Error al conectar a la base de datos: " + e.getMessage());
			}
		}
		return conexion;
	}

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
			logger.info("Base de datos creada con éxito");

		} catch (IOException | SQLException ex) {
			logger.severe("Error al ejecutar el script SQL: " + ex.getMessage());
		}
	}

	private Connection getServidorConexion() {
		try {
			String serverUrl = url.substring(0, url.lastIndexOf("/") + 1);
			return DriverManager.getConnection(serverUrl, user, password);
		} catch (SQLException e) {
			logger.severe("Error al conectar al servidor: " + e.getMessage());
			return null;
		}
	}

	public synchronized void cerrarConexion() {
		if (conexion != null) {
			try {
				conexion.close();
				conexion = null;
				logger.info("Conexión cerrada");
			} catch (SQLException e) {
				logger.severe("Error al cerrar la conexión: " + e.getMessage());
			}
		}
	}

	@Override
	public void close() {
		cerrarConexion();
	}

	public static void main(String[] args) {
		try (ConexionDB conexionDB = ConexionDB.getInstancia()) {
			// conexionDB.crearDB();
			Connection conn = conexionDB.getConexion();
			if (conn != null) {
				logger.info("Conexión establecida correctamente");
			} else {
				logger.severe("Error al conectar a la base de datos");
			}
		}
	}
}
