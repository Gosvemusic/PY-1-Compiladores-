package tokens;

import java.util.HashSet;
import java.util.Set;

/**
 * Clase que maneja las palabras reservadas de PASCAL
 */
public class PalabrasReservadas {
    
    private static final Set<String> PALABRAS_RESERVADAS = new HashSet<>();
    
    static {
        // Inicializar palabras reservadas según el proyecto
        String[] palabras = {
            "ABSOLUTE", "DOWNTO", "BEGIN", "DESTRUCTOR", "MOD",
            "AND", "ELSE", "CASE", "EXTERNAL", "NOT",
            "ARRAY", "END", "CONST", "DIV", "PACKED",
            "ASM", "FILE", "CONSTRUCTOR", "DO", "PROCEDURE",
            "FOR", "FORWARD", "FUNCTION", "GOTO", "RECORD",
            "IF", "IN", "OR", "PRIVATE", "UNTIL",
            "PROGRAM", "REPEAT", "STRING", "THEN", "VAR",
            "WHILE", "XOR", "WITH", "TYPE", "OF",
            "USES", "SET", "OBJECT", "TO", "FOR"
        };
        
        for (String palabra : palabras) {
            PALABRAS_RESERVADAS.add(palabra.toUpperCase());
        }
    }
    
    /**
     * Verifica si una palabra es reservada
     * @param palabra Palabra a verificar
     * @return true si es palabra reservada, false en caso contrario
     */
    public static boolean esPalabraReservada(String palabra) {
        if (palabra == null) {
            return false;
        }
        return PALABRAS_RESERVADAS.contains(palabra.toUpperCase());
    }
    
    /**
     * Obtiene todas las palabras reservadas
     * @return Set con todas las palabras reservadas
     */
    public static Set<String> obtenerPalabrasReservadas() {
        return new HashSet<>(PALABRAS_RESERVADAS);
    }
    
    /**
     * Verifica si una cadena es un tipo de dato válido
     * @param tipo Tipo a verificar
     * @return true si es un tipo válido
     */
    public static boolean esTipoValido(String tipo) {
        if (tipo == null) {
            return false;
        }
        String tipoUpper = tipo.toUpperCase();
        return tipoUpper.equals("INTEGER") || 
               tipoUpper.equals("STRING") || 
               tipoUpper.equals("WORD");
    }
    
    /**
     * Verifica si una palabra es una palabra clave estructural
     * @param palabra Palabra a verificar
     * @return true si es palabra estructural (program, uses, const, var, begin, end)
     */
    public static boolean esPalabraEstructural(String palabra) {
        if (palabra == null) {
            return false;
        }
        String palabraUpper = palabra.toUpperCase();
        return palabraUpper.equals("PROGRAM") ||
               palabraUpper.equals("USES") ||
               palabraUpper.equals("CONST") ||
               palabraUpper.equals("VAR") ||
               palabraUpper.equals("BEGIN") ||
               palabraUpper.equals("END");
    }
}
