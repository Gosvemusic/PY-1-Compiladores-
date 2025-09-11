package utils;

import tokens.PalabrasReservadas;
import java.util.regex.Pattern;

/**
 * Clase utilitaria para validar identificadores en PASCAL
 */
public class ValidadorIdentificadores {
    
    // Patrones de expresiones regulares
    private static final Pattern PATRON_IDENTIFICADOR = Pattern.compile("^[a-zA-Z][a-zA-Z0-9_]*$");
    private static final Pattern PATRON_SOLO_LETRAS = Pattern.compile("^[a-zA-Z]+$");
    private static final Pattern PATRON_NUMERO = Pattern.compile("^\\d+$");
    private static final Pattern PATRON_NUMERO_DECIMAL = Pattern.compile("^\\d+\\.\\d+$");
    
    /**
     * Valida si un identificador es válido según las reglas de PASCAL
     * @param identificador Identificador a validar
     * @return ResultadoValidacion con el resultado y mensaje de error
     */
    public static ResultadoValidacion validarIdentificador(String identificador) {
        if (identificador == null || identificador.trim().isEmpty()) {
            return new ResultadoValidacion(false, "Identificador no puede estar vacío");
        }
        
        String id = identificador.trim();
        
        // Verificar longitud mínima
        if (id.length() < 1) {
            return new ResultadoValidacion(false, "Identificador debe tener al menos un carácter");
        }
        
        // Verificar que no empiece con número
        if (Character.isDigit(id.charAt(0))) {
            return new ResultadoValidacion(false, 
                "Identificador no puede comenzar con número: " + id);
        }
        
        // Verificar patrón general
        if (!PATRON_IDENTIFICADOR.matcher(id).matches()) {
            return new ResultadoValidacion(false, 
                "Identificador contiene caracteres inválidos: " + id + 
                ". Solo se permiten letras, números y guiones bajos");
        }
        
        // Verificar que no sea palabra reservada
        if (PalabrasReservadas.esPalabraReservada(id)) {
            return new ResultadoValidacion(false, 
                "No se puede usar palabra reservada como identificador: " + id);
        }
        
        return new ResultadoValidacion(true, "Identificador válido");
    }
    
    /**
     * Valida si una cadena es un número válido
     * @param numero Cadena a validar como número
     * @return true si es un número válido
     */
    public static boolean esNumeroValido(String numero) {
        if (numero == null || numero.trim().isEmpty()) {
            return false;
        }
        
        String num = numero.trim();
        return PATRON_NUMERO.matcher(num).matches() || 
               PATRON_NUMERO_DECIMAL.matcher(num).matches();
    }
    
    /**
     * Valida si una cadena es un identificador básico (solo letras)
     * @param identificador Identificador a validar
     * @return true si contiene solo letras
     */
    public static boolean esSoloLetras(String identificador) {
        if (identificador == null || identificador.trim().isEmpty()) {
            return false;
        }
        
        return PATRON_SOLO_LETRAS.matcher(identificador.trim()).matches();
    }
    
    /**
     * Valida el formato de una declaración de variable
     * @param declaracion Declaración completa de variable
     * @return ResultadoValidacion con el resultado
     */
    public static ResultadoValidacion validarDeclaracionVariable(String declaracion) {
        if (declaracion == null || declaracion.trim().isEmpty()) {
            return new ResultadoValidacion(false, "Declaración no puede estar vacía");
        }
        
        String decl = declaracion.trim();
        
        // Verificar que empiece con 'var'
        if (!decl.toLowerCase().startsWith("var ")) {
            return new ResultadoValidacion(false, 
                "Declaración de variable debe comenzar con 'var'");
        }
        
        // Verificar que termine con punto y coma
        if (!decl.endsWith(";")) {
            return new ResultadoValidacion(false, 
                "Declaración de variable debe terminar con punto y coma");
        }
        
        // Verificar que contenga dos puntos
        if (!decl.contains(":")) {
            return new ResultadoValidacion(false, 
                "Declaración de variable debe contener ':' para separar nombre y tipo");
        }
        
        // Extraer partes
        String sinVar = decl.substring(4); // Quitar "var "
        String sinPuntoComa = sinVar.substring(0, sinVar.length() - 1); // Quitar ";"
        
        String[] partes = sinPuntoComa.split(":");
        if (partes.length != 2) {
            return new ResultadoValidacion(false, 
                "Formato incorrecto: var identificador : tipo;");
        }
        
        String nombreVariable = partes[0].trim();
        String tipoVariable = partes[1].trim();
        
        // Validar identificador
        ResultadoValidacion resultadoId = validarIdentificador(nombreVariable);
        if (!resultadoId.esValido) {
            return resultadoId;
        }
        
        // Validar tipo
        if (!PalabrasReservadas.esTipoValido(tipoVariable)) {
            return new ResultadoValidacion(false, 
                "Tipo de variable inválido: " + tipoVariable + 
                ". Tipos válidos: integer, string, Word");
        }
        
        // Verificar espacio después de ':'
        if (!partes[1].startsWith(" ")) {
            return new ResultadoValidacion(false, 
                "Debe haber un espacio después de ':' en la declaración");
        }
        
        return new ResultadoValidacion(true, "Declaración de variable válida");
    }
    
    /**
     * Valida el formato de una declaración de constante
     * @param declaracion Declaración completa de constante
     * @return ResultadoValidacion con el resultado
     */
    public static ResultadoValidacion validarDeclaracionConstante(String declaracion) {
        if (declaracion == null || declaracion.trim().isEmpty()) {
            return new ResultadoValidacion(false, "Declaración no puede estar vacía");
        }
        
        String decl = declaracion.trim();
        
        // Verificar que empiece con 'const'
        if (!decl.toLowerCase().startsWith("const ")) {
            return new ResultadoValidacion(false, 
                "Declaración de constante debe comenzar con 'const'");
        }
        
        // Verificar que termine con punto y coma
        if (!decl.endsWith(";")) {
            return new ResultadoValidacion(false, 
                "Declaración de constante debe terminar con punto y coma");
        }
        
        // Para arrays, validación simplificada
        if (decl.contains("array")) {
            return new ResultadoValidacion(true, "Declaración de array válida");
        }
        
        // Verificar que contenga signo igual
        if (!decl.contains("=")) {
            return new ResultadoValidacion(false, 
                "Declaración de constante debe contener '=' para asignar valor");
        }
        
        // Extraer partes
        String[] partesIgual = decl.split("=");
        if (partesIgual.length != 2) {
            return new ResultadoValidacion(false, 
                "Formato incorrecto: const identificador = valor;");
        }
        
        String parteIzq = partesIgual[0].trim();
        String parteDer = partesIgual[1].trim();
        
        // Verificar parte izquierda: const identificador
        String[] partesIzq = parteIzq.split("\\s+");
        if (partesIzq.length != 2 || !partesIzq[0].equalsIgnoreCase("const")) {
            return new ResultadoValidacion(false, 
                "Formato incorrecto en la parte izquierda de la declaración");
        }
        
        String nombreConstante = partesIzq[1];
        
        // Validar identificador
        ResultadoValidacion resultadoId = validarIdentificador(nombreConstante);
        if (!resultadoId.esValido) {
            return resultadoId;
        }
        
        // Verificar parte derecha (valor)
        if (!parteDer.endsWith(";")) {
            return new ResultadoValidacion(false, 
                "Valor de constante debe terminar con punto y coma");
        }
        
        return new ResultadoValidacion(true, "Declaración de constante válida");
    }
    
    /**
     * Extrae el identificador de una declaración de variable
     * @param declaracion Declaración de variable
     * @return Nombre del identificador o null si no es válido
     */
    public static String extraerIdentificadorVariable(String declaracion) {
        ResultadoValidacion resultado = validarDeclaracionVariable(declaracion);
        if (!resultado.esValido) {
            return null;
        }
        
        try {
            String sinVar = declaracion.trim().substring(4); // Quitar "var "
            String sinPuntoComa = sinVar.substring(0, sinVar.length() - 1); // Quitar ";"
            String[] partes = sinPuntoComa.split(":");
            return partes[0].trim();
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * Extrae el identificador de una declaración de constante
     * @param declaracion Declaración de constante
     * @return Nombre del identificador o null si no es válido
     */
    public static String extraerIdentificadorConstante(String declaracion) {
        ResultadoValidacion resultado = validarDeclaracionConstante(declaracion);
        if (!resultado.esValido) {
            return null;
        }
        
        try {
            if (declaracion.contains("array")) {
                // Para arrays, extraer de forma simple
                String[] partes = declaracion.split("\\s+");
                if (partes.length >= 2) {
                    return partes[1];
                }
            } else {
                String[] partesIgual = declaracion.split("=");
                String parteIzq = partesIgual[0].trim();
                String[] partesIzq = parteIzq.split("\\s+");
                if (partesIzq.length >= 2) {
                    return partesIzq[1];
                }
            }
        } catch (Exception e) {
            return null;
        }
        
        return null;
    }
    
    /**
     * Clase para encapsular el resultado de una validación
     */
    public static class ResultadoValidacion {
        public final boolean esValido;
        public final String mensaje;
        
        public ResultadoValidacion(boolean esValido, String mensaje) {
            this.esValido = esValido;
            this.mensaje = mensaje;
        }
        
        @Override
        public String toString() {
            return String.format("ResultadoValidacion{válido=%s, mensaje='%s'}", 
                               esValido, mensaje);
        }
    }
}
