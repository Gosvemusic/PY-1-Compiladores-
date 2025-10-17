package analizador;

import tokens.PalabrasReservadas;
import tokens.Token;
import tokens.TipoToken;
import utils.Utilidades;
import utils.ValidadorIdentificadores;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Analizador lexico para codigo PASCAL 
 * Identifica tokens y realiza validaciones basicas
 */
public class AnalizadorLexico {
    
    private final ManejadorErrores manejadorErrores;
    private final List<Token> tokens;
    private final Set<String> variablesDeclaradas;
    private final Set<String> constantesDeclaradas;
    
    // Patrones de expresiones regulares 
    private static final Pattern PATRON_IDENTIFICADOR = Pattern.compile("^[a-zA-Z][a-zA-Z0-9_]*$");
    private static final Pattern PATRON_NUMERO = Pattern.compile("^\\d+$");
    private static final Pattern PATRON_CADENA = Pattern.compile("^'.*'$");
    private static final Pattern PATRON_CARACTER_ESPECIAL = Pattern.compile("^#\\d+$");
    private static final Pattern PATRON_CADENA_COMPLEJA = Pattern.compile("^'[^']*'(#\\d+('[^']*')?)*$|^#\\d+('[^']*'(#\\d+)?)*$");
    
    public AnalizadorLexico(ManejadorErrores manejadorErrores) {
        this.manejadorErrores = manejadorErrores;
        this.tokens = new ArrayList<>();
        this.variablesDeclaradas = new HashSet<>();
        this.constantesDeclaradas = new HashSet<>();
        
        // Inicia variables conocidas del programa calendario
        inicializarVariablesConocidas();
    }
    
    /**
     * Inicia variables conocidas del programa calendario
     */
    private void inicializarVariablesConocidas() {
        // Variables del calendario.pas
        variablesDeclaradas.addAll(Arrays.asList(
            "meses", "tecla", "i", "dia", "modulo", "day", "year", "mes", "dayofweek"
        ));
        
        // Constantes del calendario.pas
        constantesDeclaradas.addAll(Arrays.asList(
            "borde", "months", "year_regular", "year_bisiesto", "nombres"
        ));
    }
    
    /**
     * Realiza el analisis lexico del contenido
     * @param contenido Contenido del archivo a analizar
     */
    public void analizar(String contenido) {
        String[] lineas = contenido.split("\n");
        
        for (int i = 0; i < lineas.length; i++) {
            int numeroLinea = i + 1;
            String linea = lineas[i];
            analizarLinea(linea, numeroLinea);
        }
    }
    
    /**
     * Analiza una linea individual
     * @param linea Linea a analizar
     * @param numeroLinea Numero de linea
     */
    private void analizarLinea(String linea, int numeroLinea) {
        // Elimina comentarios antes del analisis
        String lineaSinComentarios = eliminarComentarios(linea, numeroLinea);
        
        if (lineaSinComentarios.trim().isEmpty()) {
            return; // Linea vacia o solo comentarios
        }
        
        // Tokeniza la linea mejorada
        List<String> palabras = tokenizarMejorado(lineaSinComentarios);
        
        for (int i = 0; i < palabras.size(); i++) {
            String palabra = palabras.get(i);
            analizarToken(palabra, numeroLinea, i, palabras);
        }
    }
    
    /**
     * Tokenizador que maneja mejor los tokens complejos
     * @param linea Linea a tokenizar
     * @return Lista de tokens
     */
    private List<String> tokenizarMejorado(String linea) {
        List<String> tokens = new ArrayList<>();
        StringBuilder tokenActual = new StringBuilder();
        boolean enCadena = false;
        char caracterAnterior = ' ';
        
        for (int i = 0; i < linea.length(); i++) {
            char c = linea.charAt(i);
            
            if (c == '\'' && caracterAnterior != '\\') {
                if (!enCadena) {
                    // Comenzando una cadena
                    if (tokenActual.length() > 0) {
                        tokens.add(tokenActual.toString());
                        tokenActual.setLength(0);
                    }
                    enCadena = true;
                    tokenActual.append(c);
                } else {
                    // Terminando una cadena
                    tokenActual.append(c);
                    
                    // Verifica si hay caracteres especiales despues
                    int j = i + 1;
                    while (j < linea.length() && linea.charAt(j) == '#') {
                        // Consume caracter especial #nnn
                        while (j < linea.length() && (linea.charAt(j) == '#' || Character.isDigit(linea.charAt(j)))) {
                            tokenActual.append(linea.charAt(j));
                            j++;
                        }
                        // Verifica si hay otra cadena despues
                        if (j < linea.length() && linea.charAt(j) == '\'') {
                            while (j < linea.length() && linea.charAt(j) != '\'' && j > i + 1) {
                                tokenActual.append(linea.charAt(j));
                                j++;
                            }
                            if (j < linea.length() && linea.charAt(j) == '\'') {
                                tokenActual.append(linea.charAt(j));
                                j++;
                            }
                        }
                    }
                    
                    tokens.add(tokenActual.toString());
                    tokenActual.setLength(0);
                    enCadena = false;
                    i = j - 1; // Ajusta indice
                }
            } else if (enCadena) {
                tokenActual.append(c);
            } else if (c == '#' && !enCadena) {
                // Maneja caracteres especiales #nnn
                if (tokenActual.length() > 0) {
                    tokens.add(tokenActual.toString());
                    tokenActual.setLength(0);
                }
                
                tokenActual.append(c);
                int j = i + 1;
                while (j < linea.length() && Character.isDigit(linea.charAt(j))) {
                    tokenActual.append(linea.charAt(j));
                    j++;
                }
                
                // Verifica si hay una cadena despues del caracter especial
                StringBuilder tokenComplejo = new StringBuilder(tokenActual.toString());
                while (j < linea.length() && linea.charAt(j) == '\'') {
                    // Agrega la cadena que sigue
                    while (j < linea.length() && linea.charAt(j) != '\'' && j > i + 1) {
                        tokenComplejo.append(linea.charAt(j));
                        j++;
                    }
                    if (j < linea.length() && linea.charAt(j) == '\'') {
                        tokenComplejo.append(linea.charAt(j));
                        j++;
                    }
                    
                    // Verifica si hay otro caracter especial
                    if (j < linea.length() && linea.charAt(j) == '#') {
                        while (j < linea.length() && (linea.charAt(j) == '#' || Character.isDigit(linea.charAt(j)))) {
                            tokenComplejo.append(linea.charAt(j));
                            j++;
                        }
                    } else {
                        break;
                    }
                }
                
                tokens.add(tokenComplejo.toString());
                tokenActual.setLength(0);
                i = j - 1;
            } else if (Character.isWhitespace(c)) {
                if (tokenActual.length() > 0) {
                    tokens.add(tokenActual.toString());
                    tokenActual.setLength(0);
                }
            } else if (esDelimitadorCompuesto(linea, i)) {
                // Maneja operadores compuestos
                if (tokenActual.length() > 0) {
                    tokens.add(tokenActual.toString());
                    tokenActual.setLength(0);
                }
                
                String operador = extraerOperadorCompuesto(linea, i);
                tokens.add(operador);
                i += operador.length() - 1;
            } else if (esDelimitador(c)) {
                if (tokenActual.length() > 0) {
                    tokens.add(tokenActual.toString());
                    tokenActual.setLength(0);
                }
                tokens.add(String.valueOf(c));
            } else {
                tokenActual.append(c);
            }
            
            caracterAnterior = c;
        }
        
        if (tokenActual.length() > 0) {
            tokens.add(tokenActual.toString());
        }
        
        return tokens;
    }
    
    /**
     * Verifica si hay un operador compuesto en la posicion
     */
    private boolean esDelimitadorCompuesto(String linea, int pos) {
        if (pos + 1 < linea.length()) {
            String doble = linea.substring(pos, pos + 2);
            return doble.equals("<=") || doble.equals(">=") || doble.equals("<>") || 
                   doble.equals(":=") || doble.equals("..");
        }
        return false;
    }
    
    /**
     * Extrae el operador compuesto desde la posicion
     */
    private String extraerOperadorCompuesto(String linea, int pos) {
        if (pos + 1 < linea.length()) {
            String doble = linea.substring(pos, pos + 2);
            if (doble.equals("<=") || doble.equals(">=") || doble.equals("<>") || 
                doble.equals(":=") || doble.equals("..")) {
                return doble;
            }
        }
        return String.valueOf(linea.charAt(pos));
    }
    
    /**
     * Elimina comentarios de una linea y valida su formato
     * @param linea Linea original
     * @param numeroLinea Numero de linea
     * @return Línea sin comentarios
     */
    private String eliminarComentarios(String linea, int numeroLinea) {
        String resultado = linea;
        
        // Verifica comentarios con //
        int posicionSlash = linea.indexOf("//");
        if (posicionSlash != -1) {
            // Verifica que no haya espacios entre los slashes
            if (posicionSlash > 0 && linea.charAt(posicionSlash - 1) == '/') {
                manejadorErrores.agregarError(numeroLinea,
                    CodigosError.COMENTARIO_MAL_FORMADO,
                    "Comentario mal formado: espacios entre // no permitidos");
            }
            
            // Verifica que no este despues de punto y coma
            String antesComentario = linea.substring(0, posicionSlash).trim();
            if (antesComentario.endsWith(";")) {
                manejadorErrores.agregarError(numeroLinea,
                    CodigosError.COMENTARIO_UBICACION_INCORRECTA,
                    "No se permiten comentarios despues de punto y coma");
            }
            
            resultado = linea.substring(0, posicionSlash);
        }
        
        // Verifica comentarios con {}
        int inicioLlave = resultado.indexOf("{");
        int finLlave = resultado.indexOf("}");
        
        if (inicioLlave != -1) {
            if (finLlave == -1) {
                manejadorErrores.agregarError(numeroLinea,
                    CodigosError.COMENTARIO_SIN_CIERRE,
                    "Comentario con llaves sin cerrar");
            } else {
                // Verifica que no este despues de punto y coma
                String antesComentario = resultado.substring(0, inicioLlave).trim();
                if (antesComentario.endsWith(";")) {
                    manejadorErrores.agregarError(numeroLinea,
                        CodigosError.COMENTARIO_UBICACION_INCORRECTA,
                        "No se permiten comentarios despues de punto y coma");
                }
                
                resultado = resultado.substring(0, inicioLlave) + 
                           (finLlave + 1 < resultado.length() ? resultado.substring(finLlave + 1) : "");
            }
        } else if (finLlave != -1) {
            manejadorErrores.agregarError(numeroLinea,
                CodigosError.COMENTARIO_MAL_FORMADO,
                "Comentario con llave de cierre sin apertura");
        }
        
        return resultado;
    }
    
    /**
     * Verifica si un caracter es un delimitador
     * @param c Caracter a verificar
     * @return true si es delimitador
     */
    private boolean esDelimitador(char c) {
        return c == ';' || c == ':' || c == '(' || c == ')' || c == '=' || 
               c == ',' || c == '[' || c == ']' || c == '.' || c == '+' || 
               c == '-' || c == '*' || c == '/' || c == '<' || c == '>' ||
               c == '!';
    }
    
    /**
     * Analiza un token individual
     * @param token Token a analizar
     * @param numeroLinea Numero de linea
     * @param posicion Posicion en la linea
     * @param todosTokens Todos los tokens de la linea
     */
    private void analizarToken(String token, int numeroLinea, int posicion, List<String> todosTokens) {
        if (token.trim().isEmpty()) {
            return;
        }
        
        TipoToken tipo = determinarTipoTokenMejorado(token);
        Token tokenObj = new Token(tipo, token, numeroLinea, posicion);
        tokens.add(tokenObj);
        
        // Validaciones especificas segun el tipo
        switch (tipo) {
            case IDENTIFICADOR:
                validarIdentificador(token, numeroLinea, posicion, todosTokens);
                break;
            case PALABRA_RESERVADA:
                // Las palabras reservadas son validas por definicion
                break;
            case NUMERO:
                // Los numeros son validos si siguen el patron
                break;
            case CADENA:
                validarCadena(token, numeroLinea);
                break;
            case OPERADOR:
            case DELIMITADOR:
                // Operadores y delimitadores son validos por contexto
                break;
            case DESCONOCIDO:
                // Solo marca como desconocido si realmente no se puede clasificar
                if (!esTokenComplejo(token)) {
                    manejadorErrores.agregarError(numeroLinea,
                        CodigosError.IDENTIFICADOR_CARACTER_INVALIDO,
                        "Token no reconocido: " + token);
                }
                break;
        }
    }
    
    /**
     * Verifica si un token es complejo pero valido (como caracteres especiales de PASCAL)
     */
    private boolean esTokenComplejo(String token) {
        // Caracteres especiales de PASCAL como #201, #186, etc.
        if (PATRON_CARACTER_ESPECIAL.matcher(token).matches()) {
            return true;
        }
        
        // Cadenas complejas con caracteres especiales
        if (PATRON_CADENA_COMPLEJA.matcher(token).matches()) {
            return true;
        }
        
        // Expresiones como dia<1, mes<11, etc.
        if (token.matches("^[a-zA-Z][a-zA-Z0-9_]*[<>=]+\\d*[a-zA-Z0-9_]*$")) {
            return true;
        }
        
        return false;
    }
    
    /**
     * Determina el tipo de un token de manera mejorada
     * @param token Token a clasificar
     * @return Tipo del token
     */
    private TipoToken determinarTipoTokenMejorado(String token) {
        if (token == null || token.isEmpty()) {
            return TipoToken.DESCONOCIDO;
        }
        
        // Caracteres especiales de PASCAL
        if (PATRON_CARACTER_ESPECIAL.matcher(token).matches()) {
            return TipoToken.CADENA; // Los caracteres especiales se tratan como cadenas
        }
        
        // Cadenas complejas
        if (PATRON_CADENA_COMPLEJA.matcher(token).matches()) {
            return TipoToken.CADENA;
        }
        
        // Usa el metodo original para otros casos
        return Utilidades.determinarTipoToken(token);
    }
    
    /**
     * Valida un identificador usando la clase ValidadorIdentificadores
     * @param identificador Identificador a validar
     * @param numeroLinea Numero de linea
     * @param posicion Posicion en la linea
     * @param todosTokens Todos los tokens de la linea
     */
    private void validarIdentificador(String identificador, int numeroLinea, int posicion, List<String> todosTokens) {
        ValidadorIdentificadores.ResultadoValidacion resultado = 
            ValidadorIdentificadores.validarIdentificador(identificador);
        
        if (!resultado.esValido) {
            // Determina el codigo de error especifico
            int codigoError;
            if (Character.isDigit(identificador.charAt(0))) {
                codigoError = CodigosError.IDENTIFICADOR_NUMERO_INICIAL;
            } else if (PalabrasReservadas.esPalabraReservada(identificador)) {
                codigoError = CodigosError.IDENTIFICADOR_PALABRA_RESERVADA;
            } else {
                codigoError = CodigosError.IDENTIFICADOR_CARACTER_INVALIDO;
            }
            
            manejadorErrores.agregarError(numeroLinea, codigoError, resultado.mensaje);
        }
    }
    
    /**
     * Valida una cadena de texto
     * @param cadena Cadena a validar
     * @param numeroLinea Numero de linea
     */
    private void validarCadena(String cadena, int numeroLinea) {
        // Para cadenas simples
        if (cadena.startsWith("'") && cadena.endsWith("'")) {
            return; // Valida
        }
        
        // Para caracteres especiales
        if (PATRON_CARACTER_ESPECIAL.matcher(cadena).matches()) {
            return; // Valida
        }
        
        // Para cadenas complejas
        if (PATRON_CADENA_COMPLEJA.matcher(cadena).matches()) {
            return; // Valida
        }
        
        // Si no cumple ningun patron, es invalida
        manejadorErrores.agregarError(numeroLinea,
            CodigosError.WRITE_COMILLAS_MAL_CERRADAS,
            "Cadena mal formada: " + cadena);
    }
    
    /**
     * Registra una variable como declarada
     * @param nombreVariable Nombre de la variable
     */
    public void registrarVariable(String nombreVariable) {
        variablesDeclaradas.add(nombreVariable.toLowerCase());
    }
    
    /**
     * Registra una constante como declarada
     * @param nombreConstante Nombre de la constante
     */
    public void registrarConstante(String nombreConstante) {
        constantesDeclaradas.add(nombreConstante.toLowerCase());
    }
    
    /**
     * Verifica si una variable esta declarada
     * @param nombreVariable Nombre de la variable
     * @return true si esta declarada
     */
    public boolean estaVariableDeclarada(String nombreVariable) {
        return variablesDeclaradas.contains(nombreVariable.toLowerCase());
    }
    
    /**
     * Verifica si una constante esta declarada
     * @param nombreConstante Nombre de la constante
     * @return true si esta declarada
     */
    public boolean estaConstanteDeclarada(String nombreConstante) {
        return constantesDeclaradas.contains(nombreConstante.toLowerCase());
    }
    
    /**
     * Obtiene la lista de tokens generados
     * @return Lista de tokens
     */
    public List<Token> getTokens() {
        return new ArrayList<>(tokens);
    }
}
