package analizador;

import tokens.Token;
import tokens.TipoToken;
import tokens.PalabrasReservadas;
import utils.Utilidades;
import utils.ValidadorIdentificadores;
import java.util.*;

/**
 * Analizador sintáctico para código PASCAL
 * Valida la estructura y sintaxis del programa
 */
public class AnalizadorSintactico {
    
    private final ManejadorErrores manejadorErrores;
    private String nombreArchivo;
    private boolean programEncontrado = false;
    private boolean usesEncontrado = false;
    private boolean constEncontrado = false;
    private boolean varEncontrado = false;
    private boolean beginEncontrado = false;
    private boolean endEncontrado = false;
    
    public AnalizadorSintactico(ManejadorErrores manejadorErrores) {
        this.manejadorErrores = manejadorErrores;
    }
    
    /**
     * Realiza el análisis sintáctico completo
     * @param contenido Contenido del archivo
     * @param tokens Lista de tokens del análisis léxico
     */
    public void analizar(String contenido, List<Token> tokens) {
        // Extraer nombre del archivo para validación
        extraerNombreArchivo();
        
        String[] lineas = contenido.split("\n");
        
        // Análisis línea por línea
        for (int i = 0; i < lineas.length; i++) {
            int numeroLinea = i + 1;
            String linea = lineas[i].trim();
            
            if (linea.isEmpty() || esComentario(linea)) {
                continue;
            }
            
            analizarEstructura(linea, numeroLinea, i == 0, i == lineas.length - 1);
        }
        
        // Validaciones finales de estructura
        validarEstructuraCompleta();
    }
    
    /**
     * Extrae el nombre del archivo desde el manejador de errores
     */
    private void extraerNombreArchivo() {
        // Extraer nombre base del archivo original
        String nombreCompleto = manejadorErrores.getNombreArchivoOriginal();
        if (nombreCompleto != null && nombreCompleto.endsWith(".pas")) {
            this.nombreArchivo = nombreCompleto.substring(0, nombreCompleto.length() - 4);
        } else {
            this.nombreArchivo = "archivo";
        }
    }
    
    /**
     * Verifica si una línea es un comentario
     * @param linea Línea a verificar
     * @return true si es comentario
     */
    private boolean esComentario(String linea) {
        return linea.startsWith("//") || 
               (linea.startsWith("{") && linea.endsWith("}"));
    }
    
    /**
     * Analiza la estructura de una línea
     * @param linea Línea a analizar
     * @param numeroLinea Número de línea
     * @param esPrimeraLinea Si es la primera línea del archivo
     * @param esUltimaLinea Si es la última línea del archivo
     */
    private void analizarEstructura(String linea, int numeroLinea, boolean esPrimeraLinea, boolean esUltimaLinea) {
        String lineaLimpia = eliminarComentarios(linea);
        
        if (lineaLimpia.trim().isEmpty()) {
            return;
        }
        
        // Análisis según palabra clave
        if (lineaLimpia.toLowerCase().startsWith("program ")) {
            analizarProgram(lineaLimpia, numeroLinea, esPrimeraLinea);
        } else if (lineaLimpia.toLowerCase().startsWith("uses ")) {
            analizarUses(lineaLimpia, numeroLinea);
        } else if (lineaLimpia.toLowerCase().startsWith("const ")) {
            analizarConst(lineaLimpia, numeroLinea);
        } else if (lineaLimpia.toLowerCase().startsWith("var ")) {
            analizarVar(lineaLimpia, numeroLinea);
        } else if (lineaLimpia.toLowerCase().equals("begin")) {
            analizarBegin(lineaLimpia, numeroLinea);
        } else if (lineaLimpia.toLowerCase().equals("end.")) {
            analizarEnd(lineaLimpia, numeroLinea, esUltimaLinea);
        } else if (lineaLimpia.toLowerCase().startsWith("write(") || 
                   lineaLimpia.toLowerCase().startsWith("writeln(")) {
            analizarWrite(lineaLimpia, numeroLinea);
        }
    }
    
    /**
     * Analiza la declaración program
     * @param linea Línea con program
     * @param numeroLinea Número de línea
     * @param esPrimeraLinea Si es la primera línea
     */
    private void analizarProgram(String linea, int numeroLinea, boolean esPrimeraLinea) {
        if (!esPrimeraLinea) {
            manejadorErrores.agregarError(numeroLinea,
                CodigosError.PROGRAM_NO_ENCONTRADO,
                "Program debe ser la primera declaración del archivo");
            return;
        }
        
        if (programEncontrado) {
            manejadorErrores.agregarError(numeroLinea,
                CodigosError.PROGRAM_NO_ENCONTRADO,
                "Program ya fue declarado anteriormente");
            return;
        }
        
        // Validar formato: program identificador;
        String[] partes = linea.split("\\s+");
        if (partes.length < 2) {
            manejadorErrores.agregarError(numeroLinea,
                CodigosError.ESTRUCTURA_INCORRECTA,
                "Program debe tener un identificador");
            return;
        }
        
        String identificadorConPuntoComa = partes[1];
        if (!identificadorConPuntoComa.endsWith(";")) {
            manejadorErrores.agregarError(numeroLinea,
                CodigosError.ESTRUCTURA_INCORRECTA,
                "Program debe terminar con punto y coma");
            return;
        }
        
        String identificador = identificadorConPuntoComa.substring(0, identificadorConPuntoComa.length() - 1);
        
        // Verificar que el identificador coincida con el nombre del archivo
        if (!identificador.equalsIgnoreCase(nombreArchivo)) {
            manejadorErrores.agregarError(numeroLinea,
                CodigosError.PROGRAM_NOMBRE_INCORRECTO,
                "El nombre del programa '" + identificador + "' no coincide con el archivo '" + nombreArchivo + "'");
        }
        
        programEncontrado = true;
    }
    
    /**
     * Analiza la declaración uses
     * @param linea Línea con uses
     * @param numeroLinea Número de línea
     */
    private void analizarUses(String linea, int numeroLinea) {
        if (!programEncontrado) {
            manejadorErrores.agregarError(numeroLinea,
                CodigosError.USES_NO_ENCONTRADO,
                "Uses debe venir después de program");
            return;
        }
        
        if (usesEncontrado) {
            manejadorErrores.agregarError(numeroLinea,
                CodigosError.USES_NO_ENCONTRADO,
                "Uses ya fue declarado anteriormente");
            return;
        }
        
        if (!linea.endsWith(";")) {
            manejadorErrores.agregarError(numeroLinea,
                CodigosError.ESTRUCTURA_INCORRECTA,
                "Uses debe terminar con punto y coma");
        }
        
        usesEncontrado = true;
    }
    
    /**
     * Analiza declaraciones de constantes
     * @param linea Línea con const
     * @param numeroLinea Número de línea
     */
    private void analizarConst(String linea, int numeroLinea) {
        if (!usesEncontrado) {
            manejadorErrores.agregarError(numeroLinea,
                CodigosError.CONSTANTE_UBICACION_INCORRECTA,
                "Const debe venir después de uses");
            return;
        }
        
        if (varEncontrado) {
            manejadorErrores.agregarError(numeroLinea,
                CodigosError.CONSTANTE_UBICACION_INCORRECTA,
                "Const debe venir antes de var");
            return;
        }
        
        if (beginEncontrado) {
            manejadorErrores.agregarError(numeroLinea,
                CodigosError.CONSTANTE_UBICACION_INCORRECTA,
                "Const no puede venir después de begin");
            return;
        }
        
        // Validar formato de constante
        validarFormatoConstante(linea, numeroLinea);
        constEncontrado = true;
    }
    
    /**
     * Valida el formato de una declaración de constante
     * @param linea Línea con const
     * @param numeroLinea Número de línea
     */
    private void validarFormatoConstante(String linea, int numeroLinea) {
        // Para arrays especiales, solo validar estructura básica
        if (linea.contains("array")) {
            if (!linea.endsWith(";")) {
                manejadorErrores.agregarError(numeroLinea,
                    CodigosError.CONSTANTE_SIN_PUNTO_COMA,
                    "Declaración de constante debe terminar con punto y coma");
            }
            return;
        }
        
        // Formato normal: const identificador = valor;
        String[] partes = linea.split("=");
        if (partes.length != 2) {
            manejadorErrores.agregarError(numeroLinea,
                CodigosError.CONSTANTE_FORMATO_INCORRECTO,
                "Constante debe tener formato: const identificador = valor;");
            return;
        }
        
        String parteIzq = partes[0].trim();
        String parteDer = partes[1].trim();
        
        // Validar parte izquierda: const identificador
        String[] partesIzq = parteIzq.split("\\s+");
        if (partesIzq.length != 2 || !partesIzq[0].equalsIgnoreCase("const")) {
            manejadorErrores.agregarError(numeroLinea,
                CodigosError.CONSTANTE_FORMATO_INCORRECTO,
                "Formato incorrecto en declaración de constante");
            return;
        }
        
        String identificador = partesIzq[1];
        validarIdentificadorConstante(identificador, numeroLinea);
        
        // Validar parte derecha: valor;
        if (!parteDer.endsWith(";")) {
            manejadorErrores.agregarError(numeroLinea,
                CodigosError.CONSTANTE_SIN_PUNTO_COMA,
                "Declaración de constante debe terminar con punto y coma");
        }
    }
    
    /**
     * Valida un identificador de constante
     * @param identificador Identificador a validar
     * @param numeroLinea Número de línea
     */
    private void validarIdentificadorConstante(String identificador, int numeroLinea) {
        if (identificador.isEmpty()) {
            manejadorErrores.agregarError(numeroLinea,
                CodigosError.CONSTANTE_FORMATO_INCORRECTO,
                "Constante debe tener un identificador");
            return;
        }
        
        if (Character.isDigit(identificador.charAt(0))) {
            manejadorErrores.agregarError(numeroLinea,
                CodigosError.IDENTIFICADOR_NUMERO_INICIAL,
                "Identificador de constante no puede comenzar con número: " + identificador);
        }
        
        if (PalabrasReservadas.esPalabraReservada(identificador)) {
            manejadorErrores.agregarError(numeroLinea,
                CodigosError.IDENTIFICADOR_PALABRA_RESERVADA,
                "No se puede usar palabra reservada como identificador de constante: " + identificador);
        }
    }
    
    /**
     * Analiza declaraciones de variables
     * @param linea Línea con var
     * @param numeroLinea Número de línea
     */
    private void analizarVar(String linea, int numeroLinea) {
        if (!usesEncontrado) {
            manejadorErrores.agregarError(numeroLinea,
                CodigosError.VARIABLE_UBICACION_INCORRECTA,
                "Var debe venir después de uses");
            return;
        }
        
        if (beginEncontrado) {
            manejadorErrores.agregarError(numeroLinea,
                CodigosError.VARIABLE_UBICACION_INCORRECTA,
                "Var no puede venir después de begin");
            return;
        }
        
        // Validar formato de variable
        validarFormatoVariable(linea, numeroLinea);
        varEncontrado = true;
    }
    
    /**
     * Valida el formato de una declaración de variable
     * @param linea Línea con var
     * @param numeroLinea Número de línea
     */
    private void validarFormatoVariable(String linea, int numeroLinea) {
        // Para arrays, solo validar estructura básica
        if (linea.contains("array")) {
            if (!linea.endsWith(";")) {
                manejadorErrores.agregarError(numeroLinea,
                    CodigosError.VARIABLE_SIN_PUNTO_COMA,
                    "Declaración de variable debe terminar con punto y coma");
            }
            return;
        }
        
        // Formato normal: var identificador : tipo;
        if (!linea.contains(":")) {
            manejadorErrores.agregarError(numeroLinea,
                CodigosError.VARIABLE_FORMATO_INCORRECTO,
                "Variable debe tener formato: var identificador : tipo;");
            return;
        }
        
        String[] partes = linea.split(":");
        if (partes.length != 2) {
            manejadorErrores.agregarError(numeroLinea,
                CodigosError.VARIABLE_FORMATO_INCORRECTO,
                "Variable debe tener formato: var identificador : tipo;");
            return;
        }
        
        String parteIzq = partes[0].trim();
        String parteDer = partes[1].trim();
        
        // Validar parte izquierda: var identificador
        String[] partesIzq = parteIzq.split("\\s+");
        if (partesIzq.length != 2 || !partesIzq[0].equalsIgnoreCase("var")) {
            manejadorErrores.agregarError(numeroLinea,
                CodigosError.VARIABLE_SIN_VAR,
                "Declaración debe comenzar con 'var'");
            return;
        }
        
        String identificador = partesIzq[1];
        validarIdentificadorVariable(identificador, numeroLinea);
        
        // Validar parte derecha: tipo;
        if (!parteDer.endsWith(";")) {
            manejadorErrores.agregarError(numeroLinea,
                CodigosError.VARIABLE_SIN_PUNTO_COMA,
                "Declaración de variable debe terminar con punto y coma");
            return;
        }
        
        // Verificar espacio después de ':'
        if (!partes[1].startsWith(" ")) {
            manejadorErrores.agregarError(numeroLinea,
                CodigosError.VARIABLE_FORMATO_INCORRECTO,
                "Debe haber un espacio después de ':' en declaración de variable");
        }
        
        String tipo = parteDer.substring(0, parteDer.length() - 1).trim();
        if (!PalabrasReservadas.esTipoValido(tipo)) {
            manejadorErrores.agregarError(numeroLinea,
                CodigosError.VARIABLE_TIPO_INVALIDO,
                "Tipo de variable inválido: " + tipo + ". Tipos válidos: integer, string, Word");
        }
    }
    
    /**
     * Valida un identificador de variable
     * @param identificador Identificador a validar
     * @param numeroLinea Número de línea
     */
    private void validarIdentificadorVariable(String identificador, int numeroLinea) {
        if (identificador.isEmpty()) {
            manejadorErrores.agregarError(numeroLinea,
                CodigosError.VARIABLE_FORMATO_INCORRECTO,
                "Variable debe tener un identificador");
            return;
        }
        
        if (Character.isDigit(identificador.charAt(0))) {
            manejadorErrores.agregarError(numeroLinea,
                CodigosError.IDENTIFICADOR_NUMERO_INICIAL,
                "Identificador de variable no puede comenzar con número: " + identificador);
        }
        
        if (PalabrasReservadas.esPalabraReservada(identificador)) {
            manejadorErrores.agregarError(numeroLinea,
                CodigosError.IDENTIFICADOR_PALABRA_RESERVADA,
                "No se puede usar palabra reservada como identificador de variable: " + identificador);
        }
    }
    
    /**
     * Analiza la declaración begin
     * @param linea Línea con begin
     * @param numeroLinea Número de línea
     */
    private void analizarBegin(String linea, int numeroLinea) {
        if (!linea.trim().equalsIgnoreCase("begin")) {
            manejadorErrores.agregarError(numeroLinea,
                CodigosError.BEGIN_NO_SOLO,
                "Begin debe estar solo en su línea sin otros elementos");
            return;
        }
        
        if (beginEncontrado) {
            manejadorErrores.agregarError(numeroLinea,
                CodigosError.BEGIN_UBICACION_INCORRECTA,
                "Begin ya fue declarado anteriormente");
            return;
        }
        
        beginEncontrado = true;
    }
    
    /**
     * Analiza la declaración end
     * @param linea Línea con end
     * @param numeroLinea Número de línea
     * @param esUltimaLinea Si es la última línea
     */
    private void analizarEnd(String linea, int numeroLinea, boolean esUltimaLinea) {
        if (!linea.trim().equals("end.")) {
            manejadorErrores.agregarError(numeroLinea,
                CodigosError.END_SIN_PUNTO,
                "End debe terminar con punto: 'end.'");
            return;
        }
        
        if (!beginEncontrado) {
            manejadorErrores.agregarError(numeroLinea,
                CodigosError.END_UBICACION_INCORRECTA,
                "End. no puede aparecer antes de begin");
            return;
        }
        
        if (!esUltimaLinea) {
            manejadorErrores.agregarError(numeroLinea,
                CodigosError.END_UBICACION_INCORRECTA,
                "End. debe ser la última línea del archivo");
        }
        
        endEncontrado = true;
    }
    
    /**
     * Analiza instrucciones write/writeln
     * @param linea Línea con write
     * @param numeroLinea Número de línea
     */
    private void analizarWrite(String linea, int numeroLinea) {
        if (!beginEncontrado) {
            manejadorErrores.agregarError(numeroLinea,
                CodigosError.WRITE_SIN_PARENTESIS,
                "Write/WriteLn debe estar después de begin");
            return;
        }
        
        if (endEncontrado) {
            manejadorErrores.agregarError(numeroLinea,
                CodigosError.WRITE_SIN_PARENTESIS,
                "Write/WriteLn debe estar antes de end");
            return;
        }
        
        // Validar formato de write
        validarFormatoWrite(linea, numeroLinea);
    }
    
    /**
     * Valida el formato de una instrucción write/writeln
     * @param linea Línea con write
     * @param numeroLinea Número de línea
     */
    private void validarFormatoWrite(String linea, int numeroLinea) {
        String lineaLimpia = linea.trim();
        
        // Verificar que termine con punto y coma
        if (!lineaLimpia.endsWith(";")) {
            manejadorErrores.agregarError(numeroLinea,
                CodigosError.WRITE_SIN_PUNTO_COMA,
                "Write/WriteLn debe terminar con punto y coma");
        }
        
        // Remover punto y coma para análisis
        if (lineaLimpia.endsWith(";")) {
            lineaLimpia = lineaLimpia.substring(0, lineaLimpia.length() - 1);
        }
        
        // Verificar paréntesis
        if (!lineaLimpia.contains("(") || !lineaLimpia.contains(")")) {
            manejadorErrores.agregarError(numeroLinea,
                CodigosError.WRITE_SIN_PARENTESIS,
                "Write/WriteLn debe tener paréntesis de apertura y cierre");
            return;
        }
        
        // Extraer contenido entre paréntesis
        int inicioParentesis = lineaLimpia.indexOf("(");
        int finParentesis = lineaLimpia.lastIndexOf(")");
        
        if (inicioParentesis >= finParentesis) {
            manejadorErrores.agregarError(numeroLinea,
                CodigosError.WRITE_SIN_PARENTESIS,
                "Paréntesis mal formados en Write/WriteLn");
            return;
        }
        
        String contenido = lineaLimpia.substring(inicioParentesis + 1, finParentesis).trim();
        
        // Verificar que no esté vacío
        if (contenido.isEmpty()) {
            manejadorErrores.agregarError(numeroLinea,
                CodigosError.WRITE_VACIO,
                "Write/WriteLn no puede estar vacío");
            return;
        }
        
        // Validar contenido (cadena o variable)
        validarContenidoWrite(contenido, numeroLinea);
    }
    
    /**
     * Valida el contenido de write/writeln
     * @param contenido Contenido entre paréntesis
     * @param numeroLinea Número de línea
     */
    private void validarContenidoWrite(String contenido, int numeroLinea) {
        contenido = contenido.trim();
        
        // Si es una cadena
        if (contenido.startsWith("'")) {
            if (!contenido.endsWith("'")) {
                manejadorErrores.agregarError(numeroLinea,
                    CodigosError.WRITE_COMILLAS_MAL_CERRADAS,
                    "Cadena en Write/WriteLn mal cerrada: falta comilla final");
            }
        } else if (!contenido.startsWith("'") && !contenido.endsWith("'")) {
            // Es una variable o expresión - aquí se podría validar si existe
            // Por simplicidad, asumimos que las variables básicas existen
            if (!esVariableValida(contenido)) {
                manejadorErrores.agregarError(numeroLinea,
                    CodigosError.WRITE_VARIABLE_NO_DECLARADA,
                    "Variable no declarada en Write/WriteLn: " + contenido);
            }
        } else {
            manejadorErrores.agregarError(numeroLinea,
                CodigosError.WRITE_COMILLAS_MAL_CERRADAS,
                "Formato incorrecto en Write/WriteLn: " + contenido);
        }
    }
    
    /**
     * Verifica si una variable es válida (simplificado)
     * @param variable Variable a verificar
     * @return true si es válida
     */
    private boolean esVariableValida(String variable) {
        // Verificar que sea un identificador válido
        if (variable.matches("^[a-zA-Z][a-zA-Z0-9_]*$")) {
            // Variables comunes del ejemplo
            Set<String> variablesComunes = Set.of(
                "meses", "tecla", "i", "dia", "modulo", "day", "year", "mes", "dayofweek"
            );
            return variablesComunes.contains(variable.toLowerCase());
        }
        return false;
    }
    
    /**
     * Elimina comentarios de una línea
     * @param linea Línea original
     * @return Línea sin comentarios
     */
    private String eliminarComentarios(String linea) {
        String resultado = linea;
        
        // Eliminar comentarios //
        int posSlash = resultado.indexOf("//");
        if (posSlash != -1) {
            resultado = resultado.substring(0, posSlash);
        }
        
        // Eliminar comentarios {}
        int posInicioLlave = resultado.indexOf("{");
        int posFinLlave = resultado.indexOf("}");
        if (posInicioLlave != -1 && posFinLlave != -1 && posFinLlave > posInicioLlave) {
            resultado = resultado.substring(0, posInicioLlave) + 
                       resultado.substring(posFinLlave + 1);
        }
        
        return resultado;
    }
    
    /**
     * Validaciones finales de estructura completa
     */
    private void validarEstructuraCompleta() {
        if (!programEncontrado) {
            manejadorErrores.agregarError(CodigosError.PROGRAM_NO_ENCONTRADO,
                "No se encontró la declaración 'program' al inicio del archivo");
        }
        
        if (!usesEncontrado) {
            manejadorErrores.agregarError(CodigosError.USES_NO_ENCONTRADO,
                "No se encontró la declaración 'uses' después de program");
        }
        
        if (!beginEncontrado) {
            manejadorErrores.agregarError(CodigosError.BEGIN_UBICACION_INCORRECTA,
                "No se encontró la declaración 'begin'");
        }
        
        if (!endEncontrado) {
            manejadorErrores.agregarError(CodigosError.END_UBICACION_INCORRECTA,
                "No se encontró la declaración 'end.' al final del archivo");
        }
    }
}