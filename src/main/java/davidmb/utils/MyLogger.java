package davidmb.utils;

import java.util.logging.FileHandler;
import java.util.logging.Filter;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class MyLogger {

    private static Logger logger = Logger.getLogger(MyLogger.class.getName());

    // Formato personalizado para el logger --> [LEVEL] [Clase.Método()]: Mensaje
    public static class MyCustomFormatter extends SimpleFormatter {
        @Override
        public String format(java.util.logging.LogRecord record) {
            StringBuilder sb = new StringBuilder();
            sb.append("[").append(record.getLevel()).append("] ");
            sb.append("[").append(record.getSourceClassName()).append(".");
            sb.append(record.getSourceMethodName()).append("()]: ");
            sb.append(record.getMessage()).append("\n");

            // Si es una excepción, agrega la pila de la excepción
            if (record.getThrown() != null) {
                sb.append(record.getThrown().toString()).append("\n");
                for (StackTraceElement element : record.getThrown().getStackTrace()) {
                    sb.append("\tat ").append(element).append("\n");
                }
            }
            return sb.toString();
        }
    }

    public static void init() {
        try {
            // Crear FileHandler para los logs de información 
            FileHandler infoHandler = new FileHandler("logs/infoLogs.log", true);
            infoHandler.setLevel(Level.INFO);  
            MyCustomFormatter formatter = new MyCustomFormatter();  // Usar el formato personalizado de la clase anidada
            infoHandler.setFormatter(formatter);

            // Filtro para solo permitir logs de nivel INFO y menores
            infoHandler.setFilter(new Filter() {
                @Override
                public boolean isLoggable(java.util.logging.LogRecord record) {
                    return record.getLevel().intValue() <= Level.INFO.intValue();
                }
            });

            // Crear FileHandler para los logs de errores (SEVERE)
            FileHandler errorHandler = new FileHandler("logs/errorLogs.log", true);
            errorHandler.setLevel(Level.SEVERE);  
            errorHandler.setFormatter(formatter); 

            // Filtro para solo permitir logs de nivel SEVERE
            errorHandler.setFilter(new Filter() {
                @Override
                public boolean isLoggable(java.util.logging.LogRecord record) {
                    return record.getLevel().intValue() >= Level.SEVERE.intValue();
                }
            });

            // Añadir los handlers al logger
            logger.addHandler(infoHandler);
            logger.addHandler(errorHandler);

            logger.info("Logger inicializado.");
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Error al inicializar el logger.", ex);
        }
    }
}
