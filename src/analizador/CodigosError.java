package analizador;

/**
 * Clase que define los códigos de error estándar del analizador
 */
public class CodigosError {
    
    // Errores de estructura general (100-199)
    public static final int PROGRAM_NO_ENCONTRADO = 101;
    public static final int PROGRAM_NOMBRE_INCORRECTO = 102;
    public static final int USES_NO_ENCONTRADO = 103;
    public static final int ESTRUCTURA_INCORRECTA = 104;
    
    // Errores de identificadores/variables (200-299)
    public static final int IDENTIFICADOR_NUMERO_INICIAL = 201;
    public static final int IDENTIFICADOR_PALABRA_RESERVADA = 202;
    public static final int IDENTIFICADOR_CARACTER_INVALIDO = 203;
    public static final int VARIABLE_SIN_VAR = 204;
    public static final int VARIABLE_FORMATO_INCORRECTO = 205;
    public static final int VARIABLE_SIN_PUNTO_COMA = 206;
    public static final int VARIABLE_UBICACION_INCORRECTA = 207;
    public static final int VARIABLE_TIPO_INVALIDO = 208;
    
    // Errores de constantes (300-399)
    public static final int CONSTANTE_SIN_CONST = 301;
    public static final int CONSTANTE_FORMATO_INCORRECTO = 302;
    public static final int CONSTANTE_SIN_PUNTO_COMA = 303;
    public static final int CONSTANTE_UBICACION_INCORRECTA = 304;
    
    // Errores de begin/end (400-499)
    public static final int BEGIN_NO_SOLO = 401;
    public static final int BEGIN_UBICACION_INCORRECTA = 402;
    public static final int END_SIN_PUNTO = 403;
    public static final int END_NO_SOLO = 404;
    public static final int END_UBICACION_INCORRECTA = 405;
    
    // Errores de write/writeln (500-599)
    public static final int WRITE_SIN_PARENTESIS = 501;
    public static final int WRITE_VACIO = 502;
    public static final int WRITE_COMILLAS_MAL_CERRADAS = 503;
    public static final int WRITE_VARIABLE_NO_DECLARADA = 504;
    public static final int WRITE_SIN_PUNTO_COMA = 505;
    
    // Errores de comentarios (600-699)
    public static final int COMENTARIO_MAL_FORMADO = 601;
    public static final int COMENTARIO_UBICACION_INCORRECTA = 602;
    public static final int COMENTARIO_SIN_CIERRE = 603;
}
