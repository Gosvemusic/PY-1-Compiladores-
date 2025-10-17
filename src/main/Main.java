package main;

import analizador.*;
import java.io.File;

/**
 * Javier Rojas Cordero
 * Clase principal del Analizador PASCAL 
 * Compiladores - Proyecto 1
 * 
 */
public class Main {
    
    public static void main(String[] args) {
        System.out.println("=== ANALIZADOR PASCAL ===");
        System.out.println("Javier Rojas Cordero");
        System.out.println("Proyecto: Compiladores - Analisis Lexico y Sintactico\n");
        
        // Verifica argumentos de linea de comandos
        if (args.length != 1) {
            System.err.println("Error: Debe proporcionar el nombre del archivo .pas");
            System.err.println("Uso: java -jar JavierRojasCorderoPY1C.jar archivo.pas");
            System.err.println("Ejemplo: java -jar JavierRojasCorderoPY1C.jar calendario.pas");
            System.exit(1);
        }
        
        String nombreArchivo = args[0];
        
        // Verifica que el archivo tenga extension .pas
        if (!nombreArchivo.toLowerCase().endsWith(".pas")) {
            System.err.println("Error: El archivo debe tener extension .pas");
            System.err.println("Archivo proporcionado: " + nombreArchivo);
            System.exit(1);
        }
        
        // Verifica que el archivo exista
        File archivo = new File(nombreArchivo);
        if (!archivo.exists()) {
            System.err.println("Error: No se encontro el archivo " + nombreArchivo);
            System.err.println("Verifique que el archivo este en la misma carpeta que el .jar");
            System.exit(1);
        }
        
        // Verifica que el archivo sea legible
        if (!archivo.canRead()) {
            System.err.println("Error: No se puede leer el archivo " + nombreArchivo);
            System.err.println("Verifique los permisos del archivo");
            System.exit(1);
        }
        
        try {
            System.out.println("Iniciando analisis del archivo: " + nombreArchivo);
            System.out.println("Tamano del archivo: " + archivo.length() + " bytes");
            System.out.println();
            
            // Crea componentes del analizador
            LectorArchivos lector = new LectorArchivos();
            ManejadorErrores manejadorErrores = new ManejadorErrores(nombreArchivo);
            AnalizadorLexico analizadorLexico = new AnalizadorLexico(manejadorErrores);
            AnalizadorSintactico analizadorSintactico = new AnalizadorSintactico(manejadorErrores);
            
            // Lee el archivo
            System.out.println("1. Leyendo archivo...");
            String contenido = lector.leerArchivo(nombreArchivo);
            String[] lineas = contenido.split("\n");
            System.out.println("   Archivo leido correctamente (" + lineas.length + " lineas)");
            
            // Realiza analisis lexico
            System.out.println("2. Realizando analisis lexico...");
            analizadorLexico.analizar(contenido);
            int totalTokens = analizadorLexico.getTokens().size();
            System.out.println("   Analisis lexico completado (" + totalTokens + " tokens procesados)");
            
            // Realiza analisis sintactico
            System.out.println("3. Realizando analisis sintactico...");
            analizadorSintactico.analizar(contenido, analizadorLexico.getTokens());
            System.out.println("   Analisis sintactico completado");
            
            // Genera archivo de errores
            System.out.println("4. Generando archivo de errores...");
            manejadorErrores.generarArchivoErrores(contenido);
            System.out.println("   Archivo de errores generado: " + manejadorErrores.getNombreArchivoErrores());
            
            // Muestra resumen final
            System.out.println();
            System.out.println("=== RESUMEN DEL ANALISIS ===");
            int totalErrores = manejadorErrores.getTotalErrores();
            
            if (totalErrores == 0) {
                System.out.println("ANALISIS COMPLETADO SIN ERRORES");
                System.out.println("  El codigo PASCAL es sintacticamente correcto");
                System.out.println("  Total de lineas procesadas: " + lineas.length);
                System.out.println("  Total de tokens analizados: " + totalTokens);
            } else {
                System.out.println("ANALISIS COMPLETADO CON ERRORES");
                System.out.println("  Total de errores encontrados: " + totalErrores);
                System.out.println("  Total de lineas procesadas: " + lineas.length);
                System.out.println("  Total de tokens analizados: " + totalTokens);
                System.out.println();
                System.out.println("  Revise el archivo de errores para mas detalles:");
                System.out.println("  " + manejadorErrores.getNombreArchivoErrores());
            }
            
            System.out.println();
            System.out.println("=== ARCHIVOS GENERADOS ===");
            System.out.println("Archivo de errores: " + manejadorErrores.getNombreArchivoErrores());
            
            // Verifica que el archivo de errores se creo correctamente
            File archivoErrores = new File(manejadorErrores.getNombreArchivoErrores());
            if (archivoErrores.exists()) {
                System.out.println("  Archivo generado correctamente (" + archivoErrores.length() + " bytes)");
            } else {
                System.out.println("  Error al generar el archivo de errores");
            }
            
            System.out.println();
            System.out.println("=== ANALISIS FINALIZADO ===");
            
        } catch (java.io.FileNotFoundException e) {
            System.err.println("Error: Archivo no encontrado - " + nombreArchivo);
            System.err.println("Verifique que el archivo existe y esta en la ubicacion correcta");
            System.exit(1);
        } catch (java.io.IOException e) {
            System.err.println("Error de E/S al procesar el archivo: " + e.getMessage());
            System.err.println("Verifique que el archivo no este siendo usado por otra aplicacion");
            System.exit(1);
        } catch (Exception e) {
            System.err.println("Error inesperado durante el analisis: " + e.getMessage());
            System.err.println("Detalles del error:");
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    /**
     * Muestra informacion de ayuda sobre el uso del programa
     */
    private static void mostrarAyuda() {
        System.out.println("=== ANALIZADOR PASCAL - AYUDA ===");
        System.out.println();
        System.out.println("DESCRIPCION:");
        System.out.println("  Analizador lexico y sintactico para codigo fuente PASCAL");
        System.out.println("  Detecta errores de sintaxis y genera reportes detallados");
        System.out.println();
        System.out.println("USO:");
        System.out.println("  java -jar JavierRojasCorderoPY1C.jar <archivo.pas>");
        System.out.println();
        System.out.println("PARAMETROS:");
        System.out.println("  archivo.pas    Archivo fuente PASCAL a analizar");
        System.out.println();
        System.out.println("EJEMPLOS:");
        System.out.println("  java -jar JavierRojasCorderoPY1C.jar calendario.pas");
        System.out.println("  java -jar JavierRojasCorderoPY1C.jar programa.pas");
        System.out.println();
        System.out.println("ARCHIVOS DE SALIDA:");
        System.out.println("  <archivo>-errores.err    Reporte de errores encontrados");
        System.out.println();
        System.out.println("REQUISITOS:");
        System.out.println("  • El archivo .pas debe estar en la misma carpeta que el .jar");
        System.out.println("  • Java Runtime Environment 8 o superior");
        System.out.println("  • Permisos de lectura en el archivo fuente");
        System.out.println("  • Permisos de escritura en el directorio actual");
    }
}