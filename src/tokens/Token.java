package tokens;

/**
 * Clase que representa un token individual en el analisis lexico
 */
public class Token {
    private final TipoToken tipo;
    private final String valor;
    private final int numeroLinea;
    private final int posicionColumna;
    private final int posicionInicial;
    private final int posicionFinal;
    
    /**
     * Constructor completo del token
     * @param tipo Tipo del token
     * @param valor Valor textual del token
     * @param numeroLinea Numero de linea (1-based)
     * @param posicionColumna Posicion en la columna (1-based)
     * @param posicionInicial Posicion inicial en el texto
     * @param posicionFinal Posicion final en el texto
     */
    public Token(TipoToken tipo, String valor, int numeroLinea, int posicionColumna, 
                 int posicionInicial, int posicionFinal) {
        this.tipo = tipo;
        this.valor = valor;
        this.numeroLinea = numeroLinea;
        this.posicionColumna = posicionColumna;
        this.posicionInicial = posicionInicial;
        this.posicionFinal = posicionFinal;
    }
    
    /**
     * Constructor simplificado del token
     * @param tipo Tipo del token
     * @param valor Valor textual del token
     * @param numeroLinea Numero de linea
     * @param posicionColumna Posicion en la columna
     */
    public Token(TipoToken tipo, String valor, int numeroLinea, int posicionColumna) {
        this(tipo, valor, numeroLinea, posicionColumna, -1, -1);
    }
    
    /**
     * Constructor basico del token
     * @param tipo Tipo del token
     * @param valor Valor textual del token
     */
    public Token(TipoToken tipo, String valor) {
        this(tipo, valor, -1, -1, -1, -1);
    }
    
    // Getters
    public TipoToken getTipo() {
        return tipo;
    }
    
    public String getValor() {
        return valor;
    }
    
    public int getNumeroLinea() {
        return numeroLinea;
    }
    
    public int getPosicionColumna() {
        return posicionColumna;
    }
    
    public int getPosicionInicial() {
        return posicionInicial;
    }
    
    public int getPosicionFinal() {
        return posicionFinal;
    }
    
    /**
     * Obtiene la longitud del token
     * @return Longitud del valor del token
     */
    public int getLongitud() {
        return valor != null ? valor.length() : 0;
    }
    
    /**
     * Verifica si el token es de un tipo específico
     * @param tipo Tipo a verificar
     * @return true si coincide el tipo
     */
    public boolean esTipo(TipoToken tipo) {
        return this.tipo == tipo;
    }
    
    /**
     * Verifica si el token tiene un valor específico (case-insensitive)
     * @param valor Valor a verificar
     * @return true si coincide el valor
     */
    public boolean esValor(String valor) {
        return this.valor != null && this.valor.equalsIgnoreCase(valor);
    }
    
    /**
     * Verifica si el token es una palabra reservada específica
     * @param palabraReservada Palabra reservada a verificar
     * @return true si es la palabra reservada especificada
     */
    public boolean esPalabraReservada(String palabraReservada) {
        return tipo == TipoToken.PALABRA_RESERVADA && esValor(palabraReservada);
    }
    
    /**
     * Verifica si el token es un delimitador específico
     * @param delimitador Delimitador a verificar
     * @return true si es el delimitador especificado
     */
    public boolean esDelimitador(String delimitador) {
        return tipo == TipoToken.DELIMITADOR && valor.equals(delimitador);
    }
    
    /**
     * Verifica si el token es significativo para el analisis
     * @return true si debe ser procesado
     */
    public boolean esSignificativo() {
        return tipo.esSignificativo();
    }
    
    /**
     * Crea una copia del token con un nuevo tipo
     * @param nuevoTipo Nuevo tipo para el token
     * @return Nueva instancia de Token
     */
    public Token conTipo(TipoToken nuevoTipo) {
        return new Token(nuevoTipo, valor, numeroLinea, posicionColumna, 
                        posicionInicial, posicionFinal);
    }
    
    /**
     * Crea una copia del token con un nuevo valor
     * @param nuevoValor Nuevo valor para el token
     * @return Nueva instancia de Token
     */
    public Token conValor(String nuevoValor) {
        return new Token(tipo, nuevoValor, numeroLinea, posicionColumna, 
                        posicionInicial, posicionFinal);
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Token{");
        sb.append("tipo=").append(tipo);
        sb.append(", valor='").append(valor).append('\'');
        if (numeroLinea > 0) {
            sb.append(", línea=").append(numeroLinea);
        }
        if (posicionColumna > 0) {
            sb.append(", columna=").append(posicionColumna);
        }
        sb.append('}');
        return sb.toString();
    }
    
    /**
     * Representacion detallada del token para debugging
     * @return String con informacion completa del token
     */
    public String toStringDetallado() {
        return String.format("Token{tipo=%s, valor='%s', linea=%d, columna=%d, pos=[%d-%d]}", 
                           tipo, valor, numeroLinea, posicionColumna, 
                           posicionInicial, posicionFinal);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Token token = (Token) obj;
        return tipo == token.tipo &&
               numeroLinea == token.numeroLinea &&
               posicionColumna == token.posicionColumna &&
               (valor != null ? valor.equals(token.valor) : token.valor == null);
    }
    
    @Override
    public int hashCode() {
        int result = tipo != null ? tipo.hashCode() : 0;
        result = 31 * result + (valor != null ? valor.hashCode() : 0);
        result = 31 * result + numeroLinea;
        result = 31 * result + posicionColumna;
        return result;
    }
}
