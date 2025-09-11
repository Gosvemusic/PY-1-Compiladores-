package tokens;

/**
 * Enumeración que define los tipos de tokens en PASCAL
 */
public enum TipoToken {
    // Palabras reservadas del lenguaje
    PALABRA_RESERVADA,
    
    // Identificadores (variables, constantes, nombres de programa)
    IDENTIFICADOR,
    
    // Números enteros
    NUMERO,
    
    // Cadenas de texto entre comillas simples
    CADENA,
    
    // Operadores matemáticos y lógicos
    OPERADOR,
    
    // Delimitadores (paréntesis, punto y coma, etc.)
    DELIMITADOR,
    
    // Comentarios
    COMENTARIO,
    
    // Tokens que no se reconocen
    DESCONOCIDO,
    
    // Espacios en blanco
    ESPACIO,
    
    // Final de línea
    NUEVA_LINEA,
    
    // Final de archivo
    FIN_ARCHIVO;
    
    /**
     * Convierte el tipo de token a una representación legible
     * @return Descripción del tipo de token
     */
    @Override
    public String toString() {
        switch (this) {
            case PALABRA_RESERVADA:
                return "Palabra Reservada";
            case IDENTIFICADOR:
                return "Identificador";
            case NUMERO:
                return "Número";
            case CADENA:
                return "Cadena";
            case OPERADOR:
                return "Operador";
            case DELIMITADOR:
                return "Delimitador";
            case COMENTARIO:
                return "Comentario";
            case ESPACIO:
                return "Espacio";
            case NUEVA_LINEA:
                return "Nueva Línea";
            case FIN_ARCHIVO:
                return "Fin de Archivo";
            case DESCONOCIDO:
            default:
                return "Desconocido";
        }
    }
    
    /**
     * Verifica si el token es significativo para el análisis sintáctico
     * @return true si el token debe ser procesado sintácticamente
     */
    public boolean esSignificativo() {
        return this != ESPACIO && 
               this != NUEVA_LINEA && 
               this != COMENTARIO;
    }
    
    /**
     * Verifica si el token puede ser parte de una expresión
     * @return true si puede formar parte de expresiones
     */
    public boolean esExpresion() {
        return this == IDENTIFICADOR || 
               this == NUMERO || 
               this == CADENA || 
               this == OPERADOR;
    }
}