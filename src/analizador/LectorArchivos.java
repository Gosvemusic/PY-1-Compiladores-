package analizador;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase encargada de la lectura de archivos .pas
 * Mantiene el formato original incluyendo espacios y líneas en blanco
 */
public class LectorArchivos {
    
    /**
     * Lee un archivo .pas completo manteniendo el formato original
     * @param nombreArchivo Nombre del archivo a leer
     * @return Contenido completo del archivo
     * @throws IOException Si hay error al leer el archivo
     */
    public String leerArchivo(String nombreArchivo) throws IOException {
        StringBuilder contenido = new StringBuilder();
        
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(nombreArchivo), StandardCharsets.UTF_8))) {
            
            String linea;
            while ((linea = reader.readLine()) != null) {
                contenido.append(linea).append("\n");
            }
        }
        
        return contenido.toString();
    }
    
    /**
     * Lee un archivo línea por línea para análisis detallado
     * @param nombreArchivo Nombre del archivo a leer
     * @return Lista de líneas del archivo
     * @throws IOException Si hay error al leer el archivo
     */
    public List<String> leerLineas(String nombreArchivo) throws IOException {
        List<String> lineas = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(nombreArchivo), StandardCharsets.UTF_8))) {
            
            String linea;
            while ((linea = reader.readLine()) != null) {
                lineas.add(linea);
            }
        }
        
        return lineas;
    }
    
    /**
     * Extrae el nombre base del archivo sin extensión
     * @param nombreArchivo Nombre completo del archivo
     * @return Nombre sin extensión
     */
    public String extraerNombreBase(String nombreArchivo) {
        int ultimoPunto = nombreArchivo.lastIndexOf('.');
        if (ultimoPunto > 0) {
            return nombreArchivo.substring(0, ultimoPunto);
        }
        return nombreArchivo;
    }
    
    /**
     * Escribe contenido a un archivo
     * @param nombreArchivo Nombre del archivo de salida
     * @param contenido Contenido a escribir
     * @throws IOException Si hay error al escribir el archivo
     */
    public void escribirArchivo(String nombreArchivo, String contenido) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(nombreArchivo), StandardCharsets.UTF_8))) {
            writer.write(contenido);
        }
    }
}
