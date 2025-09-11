package main;

import analizador.*;
import java.io.File;

/**
 * Clase principal del Analizador PASCAL
 * Compiladores - Proyecto 1
 * 
 * Uso: java -jar AnalizadorPascal.jar archivo.pas
 */
public class Main {
    
    public static void main(String[] args) {
        // Verificar argumentos de línea de comandos
        if (args.length != 1) {
            System.err.println("Error: Debe proporcionar el nombre del archivo .pas");
            System.err.println("Uso: java -jar AnalizadorPascal.jar archivo.pas");
            System.exit(1);
        }
        
        String nombreArchivo = args[0];
        
        // Verificar que el archivo tenga extensión .pas
        if (!nombreArchivo.toLowerCase().endsWith(".pas")) {
            System.err.println("Error: El archivo debe tener extensión .pas");
            System.exit(1);
        }
        
        // Verificar que el archivo existe
        File archivo = new File(nombreArchivo);
        if (!archivo.exists()) {
            System.err.println("Error: No se encontró el archivo " + nombreArchivo);
            System.exit(1);
        }
        
        try {
            System.out.println("Analizando archivo: " + nombreArchivo);
            
            // Crear componentes del analizador
            LectorArchivos lector = new LectorArchivos();
            ManejadorErrores manejadorErrores = new ManejadorErrores(nombreArchivo);
            AnalizadorLexico analizadorLexico = new AnalizadorLexico(manejadorErrores);
            AnalizadorSintactico analizadorSintactico = new AnalizadorSintactico(manejadorErrores);
            
            // Leer el archivo
            String contenido = lector.leerArchivo(nombreArchivo);
            
            // Realizar análisis léxico
            analizadorLexico.analizar(contenido);
            
            // Realizar análisis sintáctico
            analizadorSintactico.analizar(contenido, analizadorLexico.getTokens());
            
            // Generar archivo de errores
            manejadorErrores.generarArchivoErrores(contenido);
            
            // Mostrar resumen
            int totalErrores = manejadorErrores.getTotalErrores();
            if (totalErrores == 0) {
                System.out.println("✓ Análisis completado sin errores");
            } else {
                System.out.println("✗ Análisis completado con " + totalErrores + " error(es)");
            }
            
            System.out.println("Archivo de errores generado: " + manejadorErrores.getNombreArchivoErrores());
            
        } catch (Exception e) {
            System.err.println("Error durante el análisis: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}