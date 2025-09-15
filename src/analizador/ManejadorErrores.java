package analizador;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase encargada del manejo y reporte de errores
 * Genera el archivo .err con formato especifico
 */
public class ManejadorErrores {
    
    private final String nombreArchivoOriginal;
    private final String nombreArchivoErrores;
    private final List<ErrorInfo> errores;
    
    /**
     * Constructor del manejador de errores
     * @param nombreArchivoPas Nombre del archivo .pas original
     */
    public ManejadorErrores(String nombreArchivoPas) {
        this.nombreArchivoOriginal = nombreArchivoPas;
        this.nombreArchivoErrores = generarNombreArchivoErrores(nombreArchivoPas);
        this.errores = new ArrayList<>();
    }
    
    /**
     * Genera el nombre del archivo de errores
     * @param nombreOriginal Nombre del archivo original
     * @return Nombre del archivo de errores
     */
    private String generarNombreArchivoErrores(String nombreOriginal) {
        int ultimoPunto = nombreOriginal.lastIndexOf('.');
        String nombreBase;
        if (ultimoPunto > 0) {
            nombreBase = nombreOriginal.substring(0, ultimoPunto);
        } else {
            nombreBase = nombreOriginal;
        }
        return nombreBase + "-errores.err";
    }
    
    /**
     * Agrega un error a la lista
     * @param numeroLinea Número de línea donde ocurre el error (1-based)
     * @param numeroError Codigo del error
     * @param descripcion Descripcion del error
     */
    public void agregarError(int numeroLinea, int numeroError, String descripcion) {
        errores.add(new ErrorInfo(numeroLinea, numeroError, descripcion));
    }
    
    /**
     * Agrega un error sin numero de linea especifico
     * @param numeroError Codigo del error
     * @param descripcion Descripcion del error
     */
    public void agregarError(int numeroError, String descripcion) {
        errores.add(new ErrorInfo(-1, numeroError, descripcion));
    }
    
    /**
     * Genera el archivo de errores con el formato requerido
     * @param contenidoOriginal Contenido original del archivo .pas
     * @throws IOException Si hay error al escribir el archivo
     */
    public void generarArchivoErrores(String contenidoOriginal) throws IOException {
        StringBuilder contenido = new StringBuilder();
        String[] lineas = contenidoOriginal.split("\n");
        
        // Agrega contenido original con numeracion
        for (int i = 0; i < lineas.length; i++) {
            String numeroLinea = String.format("%04d", i + 1);
            contenido.append(numeroLinea).append(" ").append(lineas[i]).append("\n");
        }
        
        // Agrega errores al final
        if (!errores.isEmpty()) {
            contenido.append("\n");
            contenido.append("ERRORES ENCONTRADOS:\n");
            contenido.append("====================\n");
            
            for (ErrorInfo error : errores) {
                if (error.numeroLinea > 0) {
                    contenido.append(String.format("Error %d. Linea %04d. %s\n", 
                        error.numeroError, error.numeroLinea, error.descripcion));
                } else {
                    contenido.append(String.format("Error %d. %s\n", 
                        error.numeroError, error.descripcion));
                }
            }
        }
        
        // Escribe un archivo
        LectorArchivos lector = new LectorArchivos();
        lector.escribirArchivo(nombreArchivoErrores, contenido.toString());
    }
    
    /**
     * Obtiene el total de errores encontrados
     * @return Numero total de errores
     */
    public int getTotalErrores() {
        return errores.size();
    }
    
    /**
     * Obtiene el nombre del archivo de errores
     * @return Nombre del archivo .err
     */
    public String getNombreArchivoErrores() {
        return nombreArchivoErrores;
    }
    
    /**
     * Obtiene el nombre del archivo original
     * @return Nombre del archivo .pas original
     */
    public String getNombreArchivoOriginal() {
        return nombreArchivoOriginal;
    }
    
    /**
     * Verifica si hay errores
     * @return true si hay errores, false en caso contrario
     */
    public boolean hayErrores() {
        return !errores.isEmpty();
    }
    
    /**
     * Clase interna para almacenar informacion de errores
     */
    private static class ErrorInfo {
        final int numeroLinea;
        final int numeroError;
        final String descripcion;
        
        public ErrorInfo(int numeroLinea, int numeroError, String descripcion) {
            this.numeroLinea = numeroLinea;
            this.numeroError = numeroError;
            this.descripcion = descripcion;
        }
    }
}
