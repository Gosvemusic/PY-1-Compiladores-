package tokens;

/**
 * Enumeracion que define los tipos de tokens en PASCAL
 */
public enum TipoToken {
    // Palabras reservadas del lenguaje
    PALABRA_RESERVADA,
    
    // Identificadores (variables, constantes, nombres de programa)
    IDENTIFICADOR,
    
    // Numeros enteros
    NUMERO,
    
    // Cadenas de texto entre comillas simples
    CADENA,
    
    // Operadores matematicos y logicos
    OPERADOR,
    
    // Delimitadores (parentesis, punto y coma, etc.)
    DELIMITADOR,
    
    // Comentarios
    COMENTARIO,
    
    // Tokens que no se reconocen
    DESCONOCIDO,
    
    // Espacios en blanco
    ESPACIO,
    
    // Final de linea
    NUEVA_LINEA,
    
    // Final de archivo
    FIN_ARCHIVO;
    
    /**
     * Convierte el tipo de token a una representacion legible
     * @return Descripcion del tipo de token
     */
    @Override
    public String toString() {
        switch (this) {
            case PALABRA_RESERVADA:
                return "Palabra Reservada";
            case IDENTIFICADOR:
                return "Identificador";
            case NUMERO:
                return "Numero";
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
                return "Nueva Linea";
            case FIN_ARCHIVO:
                return "Fin de Archivo";
            case DESCONOCIDO:
            default:
                return "Desconocido";
        }
    }
    
    /**
     * Verifica si el token es significativo para el analisis sintactico
     * @return true si el token debe ser procesado sintacticamente
     */
    public boolean esSignificativo() {
        return this != ESPACIO && 
               this != NUEVA_LINEA && 
               this != COMENTARIO;
    }
    
    /**
     * Verifica si el token puede ser parte de una expresion
     * @return true si puede formar parte de expresiones
     */
    public boolean esExpresion() {
        return this == IDENTIFICADOR || 
               this == NUMERO || 
               this == CADENA || 
               this == OPERADOR;
    }
}