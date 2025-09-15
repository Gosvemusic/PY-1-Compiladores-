package main;

import analizador.*;
import java.io.File;

/**Javier Rojas Cordero
 * Clase principal del Analizador PASCAL
 * Compiladores - Proyecto 1
 * 
 * Uso: java -jar AnalizadorPascal.jar archivo.pas
 */
public class Main {
    
    public static void main(String[] args) {
        // Verifica argumentos de linea de comandos
        if (args.length != 1) {
            System.err.println("Error: Debe proporcionar el nombre del archivo .pas");
            System.err.println("Uso: java -jar AnalizadorPascal.jar archivo.pas");
            System.exit(1);
        }
        
        String nombreArchivo = args[0];
        
        // Verifica que el archivo tenga extension .pas
        if (!nombreArchivo.toLowerCase().endsWith(".pas")) {
            System.err.println("Error: El archivo debe tener extension .pas");
            System.exit(1);
        }
        
        // Verifica que el archivo exista
        File archivo = new File(nombreArchivo);
        if (!archivo.exists()) {
            System.err.println("Error: No se encontro el archivo " + nombreArchivo);
            System.exit(1);
        }
        
        try {
            System.out.println("Analizando archivo: " + nombreArchivo);
            
            // Crea componentes del analizador
            LectorArchivos lector = new LectorArchivos();
            ManejadorErrores manejadorErrores = new ManejadorErrores(nombreArchivo);
            AnalizadorLexico analizadorLexico = new AnalizadorLexico(manejadorErrores);
            AnalizadorSintactico analizadorSintactico = new AnalizadorSintactico(manejadorErrores);
            
            // Lee el archivo
            String contenido = lector.leerArchivo(nombreArchivo);
            
            // Realiza analisis lexico
            analizadorLexico.analizar(contenido);
            
            // Realiza analisis sintactico
            analizadorSintactico.analizar(contenido, analizadorLexico.getTokens());
            
            // Genera archivo de errores
            manejadorErrores.generarArchivoErrores(contenido);
            
            // Muestra un resumen
            int totalErrores = manejadorErrores.getTotalErrores();
            if (totalErrores == 0) {
                System.out.println("✓ Analisis completado sin errores");
            } else {
                System.out.println("✗ Aaálisis completado con " + totalErrores + " error(es)");
            }
            
            System.out.println("Archivo de errores generado: " + manejadorErrores.getNombreArchivoErrores());
            
        } catch (Exception e) {
            System.err.println("Error durante el analisis: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}