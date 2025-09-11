package utils;

import tokens.Token;
import tokens.TipoToken;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Clase con funciones utilitarias para el analizador PASCAL
 */
public class Utilidades {
    
    // Patrones comunes
    private static final Pattern PATRON_ESPACIO = Pattern.compile("\\s+");
    private static final Pattern PATRON_NUMERO_ENTERO = Pattern.compile("^-?\\d+$");
    private static final Pattern PATRON_NUMERO_REAL = Pattern.compile("^-?\\d+\\.\\d+$");
    private static final Pattern PATRON_CADENA_PASCAL = Pattern.compile("^'.*'$");
    
    // Caracteres especiales de PASCAL
    private static final Set<Character> DELIMITADORES = new HashSet<>(Arrays.asList(
        ';', ':', '(', ')', '[', ']', '.', ',', '=', '+', '-', '*', '/', '<', '>', '!', '&', '|'
    ));
    
    private static final Set<String> OPERADORES_COMPUESTOS = new HashSet<>(Arrays.asList(
        ":=", "<=", ">=", "<>", ".."
    ));
    
    /**
     * Formatea un número de línea con ceros a la izquierda
     * @param numeroLinea Número de línea
     * @return String formateado (ej: 0001, 0042, 1234)
     */
    public static String formatearNumeroLinea(int numeroLinea) {
        return String.format("%04d", numeroLinea);
    }
    
    /**
     * Limpia una línea eliminando espacios extras y tabulaciones
     * @param linea Línea a limpiar
     * @return Línea limpia
     */
    public static String limpiarLinea(String linea) {
        if (linea == null) {
            return "";
        }
        
        // Reemplazar tabs por espacios
        String lineaLimpia = linea.replace('\t', ' ');
        
        // Mantener espacios significativos pero limpiar extremos
        return lineaLimpia;
    }
    
    /**
     * Normaliza espacios en una línea (reemplaza múltiples espacios por uno)
     * @param linea Línea a normalizar
     * @return Línea con espacios normalizados
     */
    public static String normalizarEspacios(String linea) {
        if (linea == null) {
            return "";
        }
        
        return PATRON_ESPACIO.matcher(linea.trim()).replaceAll(" ");
    }
    
    /**
     * Verifica si una línea está vacía o contiene solo espacios
     * @param linea Línea a verificar
     * @return true si está vacía
     */
    public static boolean esLineaVacia(String linea) {
        return linea == null || linea.trim().isEmpty();
    }
    
    /**
     * Verifica si una línea es un comentario
     * @param linea Línea a verificar
     * @return true si es comentario
     */
    public static boolean esComentario(String linea) {
        if (linea == null) {
            return false;
        }
        
        String lineaTrimmed = linea.trim();
        return lineaTrimmed.startsWith("//") || 
               (lineaTrimmed.startsWith("{") && lineaTrimmed.endsWith("}"));
    }
    
    /**
     * Elimina comentarios de una línea
     * @param linea Línea original
     * @return Línea sin comentarios
     */
    public static String eliminarComentarios(String linea) {
        if (linea == null) {
            return "";
        }
        
        String resultado = linea;
        
        // Eliminar comentarios de línea (//)
        int posSlash = resultado.indexOf("//");
        if (posSlash != -1) {
            resultado = resultado.substring(0, posSlash);
        }
        
        // Eliminar comentarios de bloque ({})
        int posInicioLlave = resultado.indexOf("{");
        int posFinLlave = resultado.indexOf("}");
        
        if (posInicioLlave != -1 && posFinLlave != -1 && posFinLlave > posInicioLlave) {
            resultado = resultado.substring(0, posInicioLlave) + 
                       resultado.substring(posFinLlave + 1);
        } else if (posInicioLlave != -1 && posFinLlave == -1) {
            // Comentario sin cerrar
            resultado = resultado.substring(0, posInicioLlave);
        }
        
        return resultado;
    }
    
    /**
     * Verifica si un carácter es un delimitador
     * @param c Carácter a verificar
     * @return true si es delimitador
     */
    public static boolean esDelimitador(char c) {
        return DELIMITADORES.contains(c);
    }
    
    /**
     * Verifica si una cadena es un operador compuesto
     * @param operador Cadena a verificar
     * @return true si es operador compuesto
     */
    public static boolean esOperadorCompuesto(String operador) {
        return OPERADORES_COMPUESTOS.contains(operador);
    }
    
    /**
     * Divide una línea en tokens considerando cadenas y comentarios
     * @param linea Línea a tokenizar
     * @return Lista de tokens como strings
     */
    public static List<String> tokenizarLinea(String linea) {
        List<String> tokens = new ArrayList<>();
        
        if (linea == null || linea.isEmpty()) {
            return tokens;
        }
        
        StringBuilder tokenActual = new StringBuilder();
        boolean dentroDeComillas = false;
        boolean dentroDeComentario = false;
        char caracterAnterior = ' ';
        
        for (int i = 0; i < linea.length(); i++) {
            char c = linea.charAt(i);
            
            // Manejar comillas simples
            if (c == '\'' && caracterAnterior != '\\') {
                dentroDeComillas = !dentroDeComillas;
                tokenActual.append(c);
            }
            // Si estamos dentro de comillas, agregar todo
            else if (dentroDeComillas) {
                tokenActual.append(c);
            }
            // Manejar comentarios
            else if (c == '/' && i + 1 < linea.length() && linea.charAt(i + 1) == '/') {
                // Agregar token actual si existe
                if (tokenActual.length() > 0) {
                    tokens.add(tokenActual.toString());
                    tokenActual.setLength(0);
                }
                // El resto es comentario
                break;
            }
            else if (c == '{') {
                dentroDeComentario = true;
                // Agregar token actual si existe
                if (tokenActual.length() > 0) {
                    tokens.add(tokenActual.toString());
                    tokenActual.setLength(0);
                }
            }
            else if (c == '}') {
                dentroDeComentario = false;
            }
            else if (dentroDeComentario) {
                // Ignorar contenido de comentarios
                continue;
            }
            // Manejar espacios
            else if (Character.isWhitespace(c)) {
                if (tokenActual.length() > 0) {
                    tokens.add(tokenActual.toString());
                    tokenActual.setLength(0);
                }
            }
            // Manejar delimitadores
            else if (esDelimitador(c)) {
                if (tokenActual.length() > 0) {
                    tokens.add(tokenActual.toString());
                    tokenActual.setLength(0);
                }
                
                // Verificar operadores compuestos
                if (i + 1 < linea.length()) {
                    String posibleOperador = "" + c + linea.charAt(i + 1);
                    if (esOperadorCompuesto(posibleOperador)) {
                        tokens.add(posibleOperador);
                        i++; // Saltar el siguiente carácter
                        caracterAnterior = linea.charAt(i);
                        continue;
                    }
                }
                
                tokens.add(String.valueOf(c));
            }
            // Carácter normal
            else {
                tokenActual.append(c);
            }
            
            caracterAnterior = c;
        }
        
        // Agregar último token si existe
        if (tokenActual.length() > 0) {
            tokens.add(tokenActual.toString());
        }
        
        return tokens;
    }
    
    /**
     * Determina el tipo de un token basado en su valor
     * @param valor Valor del token
     * @return Tipo del token
     */
    public static TipoToken determinarTipoToken(String valor) {
        if (valor == null || valor.isEmpty()) {
            return TipoToken.DESCONOCIDO;
        }
        
        // Verificar si es palabra reservada
        if (tokens.PalabrasReservadas.esPalabraReservada(valor)) {
            return TipoToken.PALABRA_RESERVADA;
        }
        
        // Verificar si es número
        if (PATRON_NUMERO_ENTERO.matcher(valor).matches() || 
            PATRON_NUMERO_REAL.matcher(valor).matches()) {
            return TipoToken.NUMERO;
        }
        
        // Verificar si es cadena
        if (PATRON_CADENA_PASCAL.matcher(valor).matches()) {
            return TipoToken.CADENA;
        }
        
        // Verificar si es identificador
        if (ValidadorIdentificadores.validarIdentificador(valor).esValido) {
            return TipoToken.IDENTIFICADOR;
        }
        
        // Verificar si es operador compuesto
        if (esOperadorCompuesto(valor)) {
            return TipoToken.OPERADOR;
        }
        
        // Verificar si es delimitador simple
        if (valor.length() == 1 && esDelimitador(valor.charAt(0))) {
            return TipoToken.DELIMITADOR;
        }
        
        // Si es un operador simple
        if (valor.length() == 1 && "+-*/=<>!&|".contains(valor)) {
            return TipoToken.OPERADOR;
        }
        
        return TipoToken.DESCONOCIDO;
    }
    
    /**
     * Convierte una lista de strings en tokens
     * @param palabras Lista de palabras
     * @param numeroLinea Número de línea
     * @return Lista de tokens
     */
    public static List<Token> convertirATokens(List<String> palabras, int numeroLinea) {
        List<Token> tokens = new ArrayList<>();
        
        for (int i = 0; i < palabras.size(); i++) {
            String palabra = palabras.get(i);
            TipoToken tipo = determinarTipoToken(palabra);
            Token token = new Token(tipo, palabra, numeroLinea, i + 1);
            tokens.add(token);
        }
        
        return tokens;
    }
    
    /**
     * Filtra tokens significativos (elimina espacios y comentarios)
     * @param tokens Lista de tokens original
     * @return Lista de tokens significativos
     */
    public static List<Token> filtrarTokensSignificativos(List<Token> tokens) {
        List<Token> tokensSignificativos = new ArrayList<>();
        
        for (Token token : tokens) {
            if (token.esSignificativo()) {
                tokensSignificativos.add(token);
            }
        }
        
        return tokensSignificativos;
    }
    
    /**
     * Busca un token específico en una lista
     * @param tokens Lista de tokens
     * @param valor Valor a buscar
     * @param tipo Tipo de token a buscar
     * @return Token encontrado o null
     */
    public static Token buscarToken(List<Token> tokens, String valor, TipoToken tipo) {
        for (Token token : tokens) {
            if (token.getTipo() == tipo && token.esValor(valor)) {
                return token;
            }
        }
        return null;
    }
    
    /**
     * Cuenta ocurrencias de un token específico
     * @param tokens Lista de tokens
     * @param valor Valor a contar
     * @param tipo Tipo de token
     * @return Número de ocurrencias
     */
    public static int contarTokens(List<Token> tokens, String valor, TipoToken tipo) {
        int contador = 0;
        for (Token token : tokens) {
            if (token.getTipo() == tipo && token.esValor(valor)) {
                contador++;
            }
        }
        return contador;
    }
    
    /**
     * Valida si una cadena es un nombre de archivo válido
     * @param nombreArchivo Nombre del archivo
     * @return true si es válido
     */
    public static boolean esNombreArchivoValido(String nombreArchivo) {
        if (nombreArchivo == null || nombreArchivo.trim().isEmpty()) {
            return false;
        }
        
        String nombre = nombreArchivo.trim();
        
        // Verificar extensión .pas
        if (!nombre.toLowerCase().endsWith(".pas")) {
            return false;
        }
        
        // Verificar que no contenga caracteres inválidos
        String caracteresInvalidos = "<>:\"|?*";
        for (char c : caracteresInvalidos.toCharArray()) {
            if (nombre.contains(String.valueOf(c))) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Extrae el nombre base de un archivo (sin extensión)
     * @param nombreArchivo Nombre completo del archivo
     * @return Nombre base sin extensión
     */
    public static String extraerNombreBase(String nombreArchivo) {
        if (nombreArchivo == null || nombreArchivo.isEmpty()) {
            return "";
        }
        
        String nombre = nombreArchivo;
        
        // Eliminar ruta si existe
        int ultimaBarra = Math.max(nombre.lastIndexOf('/'), nombre.lastIndexOf('\\'));
        if (ultimaBarra != -1) {
            nombre = nombre.substring(ultimaBarra + 1);
        }
        
        // Eliminar extensión
        int ultimoPunto = nombre.lastIndexOf('.');
        if (ultimoPunto != -1) {
            nombre = nombre.substring(0, ultimoPunto);
        }
        
        return nombre;
    }
    
    /**
     * Capitaliza la primera letra de una palabra
     * @param palabra Palabra a capitalizar
     * @return Palabra capitalizada
     */
    public static String capitalizar(String palabra) {
        if (palabra == null || palabra.isEmpty()) {
            return palabra;
        }
        
        return palabra.substring(0, 1).toUpperCase() + 
               palabra.substring(1).toLowerCase();
    }
    
    /**
     * Verifica si dos identificadores son equivalentes (case-insensitive)
     * @param id1 Primer identificador
     * @param id2 Segundo identificador
     * @return true si son equivalentes
     */
    public static boolean sonIdentificadoresEquivalentes(String id1, String id2) {
        if (id1 == null && id2 == null) {
            return true;
        }
        
        if (id1 == null || id2 == null) {
            return false;
        }
        
        return id1.equalsIgnoreCase(id2);
    }
    
    /**
     * Convierte un texto a formato de error
     * @param codigo Código del error
     * @param descripcion Descripción del error
     * @return String formateado para error
     */
    public static String formatearError(int codigo, String descripcion) {
        return String.format("Error %d. %s", codigo, descripcion);
    }
    
    /**
     * Convierte un texto a formato de error con línea
     * @param codigo Código del error
     * @param numeroLinea Número de línea
     * @param descripcion Descripción del error
     * @return String formateado para error
     */
    public static String formatearErrorConLinea(int codigo, int numeroLinea, String descripcion) {
        return String.format("Error %d. Línea %04d. %s", codigo, numeroLinea, descripcion);
    }
    
    /**
     * Valida el formato de una instrucción Write/WriteLn
     * @param instruccion Instrucción completa
     * @return true si el formato es válido
     */
    public static boolean esFormatoWriteValido(String instruccion) {
        if (instruccion == null || instruccion.trim().isEmpty()) {
            return false;
        }
        
        String inst = instruccion.trim().toLowerCase();
        
        // Debe empezar con write o writeln
        if (!inst.startsWith("write(") && !inst.startsWith("writeln(")) {
            return false;
        }
        
        // Debe terminar con );
        if (!inst.endsWith(");")) {
            return false;
        }
        
        // Verificar paréntesis balanceados
        int contadorParentesis = 0;
        for (char c : instruccion.toCharArray()) {
            if (c == '(') contadorParentesis++;
            if (c == ')') contadorParentesis--;
        }
        
        return contadorParentesis == 0;
    }
    
    /**
     * Extrae el contenido entre paréntesis de una instrucción Write/WriteLn
     * @param instruccion Instrucción completa
     * @return Contenido entre paréntesis o null si no es válido
     */
    public static String extraerContenidoWrite(String instruccion) {
        if (!esFormatoWriteValido(instruccion)) {
            return null;
        }
        
        int inicioParentesis = instruccion.indexOf('(');
        int finParentesis = instruccion.lastIndexOf(')');
        
        if (inicioParentesis != -1 && finParentesis != -1 && finParentesis > inicioParentesis) {
            return instruccion.substring(inicioParentesis + 1, finParentesis).trim();
        }
        
        return null;
    }
    
    /**
     * Genera estadísticas básicas de un archivo
     * @param contenido Contenido del archivo
     * @return Map con estadísticas
     */
    public static Map<String, Integer> generarEstadisticas(String contenido) {
        Map<String, Integer> stats = new HashMap<>();
        
        if (contenido == null) {
            return stats;
        }
        
        String[] lineas = contenido.split("\n");
        
        stats.put("lineas_totales", lineas.length);
        stats.put("lineas_vacias", 0);
        stats.put("lineas_comentario", 0);
        stats.put("lineas_codigo", 0);
        stats.put("caracteres_totales", contenido.length());
        
        for (String linea : lineas) {
            if (esLineaVacia(linea)) {
                stats.put("lineas_vacias", stats.get("lineas_vacias") + 1);
            } else if (esComentario(linea)) {
                stats.put("lineas_comentario", stats.get("lineas_comentario") + 1);
            } else {
                stats.put("lineas_codigo", stats.get("lineas_codigo") + 1);
            }
        }
        
        return stats;
    }
    
    /**
     * Clase para manejar pares de valores
     */
    public static class Par<T, U> {
        public final T primero;
        public final U segundo;
        
        public Par(T primero, U segundo) {
            this.primero = primero;
            this.segundo = segundo;
        }
        
        @Override
        public String toString() {
            return String.format("Par{%s, %s}", primero, segundo);
        }
        
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            
            Par<?, ?> par = (Par<?, ?>) obj;
            return Objects.equals(primero, par.primero) && 
                   Objects.equals(segundo, par.segundo);
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(primero, segundo);
        }
    }
}