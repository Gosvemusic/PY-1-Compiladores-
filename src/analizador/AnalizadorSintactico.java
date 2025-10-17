package analizador;

import tokens.Token;
import tokens.TipoToken;
import tokens.PalabrasReservadas;
import utils.Utilidades;
import utils.ValidadorIdentificadores;
import java.util.*;

public class AnalizadorSintactico {
    
    private final ManejadorErrores manejadorErrores;
    private String nombreArchivo;
    private boolean programEncontrado = false;
    private boolean usesEncontrado = false;
    private boolean constEncontrado = false;
    private boolean varEncontrado = false;
    private boolean beginEncontrado = false;
    private boolean endEncontrado = false;
    private boolean enSeccionVariables = false;
    
    public AnalizadorSintactico(ManejadorErrores manejadorErrores) {
        this.manejadorErrores = manejadorErrores;
    }
    
    public void analizar(String contenido, List<Token> tokens) {
        extraerNombreArchivo();
        String[] lineas = contenido.split("\n");
        
        for (int i = 0; i < lineas.length; i++) {
            int numeroLinea = i + 1;
            String linea = lineas[i].trim();
            
            if (linea.isEmpty() || esComentario(linea)) {
                continue;
            }
            
            analizarEstructura(linea, numeroLinea, i == 0, i == lineas.length - 1);
        }
        
        validarEstructuraCompleta();
    }
    
    private void extraerNombreArchivo() {
        String nombreCompleto = manejadorErrores.getNombreArchivoOriginal();
        if (nombreCompleto != null && nombreCompleto.endsWith(".pas")) {
            this.nombreArchivo = nombreCompleto.substring(0, nombreCompleto.length() - 4);
        } else {
            this.nombreArchivo = "archivo";
        }
    }
    
    private boolean esComentario(String linea) {
        return linea.startsWith("//") || (linea.startsWith("{") && linea.endsWith("}"));
    }
    
    private void analizarEstructura(String linea, int numeroLinea, boolean esPrimeraLinea, boolean esUltimaLinea) {
        String lineaLimpia = linea.trim();
        
        if (esPrimeraLinea && !lineaLimpia.toLowerCase().startsWith("program")) {
            manejadorErrores.agregarError(numeroLinea, CodigosError.PROGRAM_NO_ENCONTRADO,
                "El archivo debe comenzar con 'program'");
        }
        
        if (lineaLimpia.isEmpty() || esComentario(lineaLimpia)) {
            return;
        }
        
        if (numeroLinea == 2 && programEncontrado && !lineaLimpia.toLowerCase().startsWith("uses")) {
            if (lineaLimpia.matches(".*[a-zA-Z]+.*,.*[a-zA-Z]+.*") || lineaLimpia.matches(".*[a-zA-Z]+.*")) {
                manejadorErrores.agregarError(numeroLinea, CodigosError.USES_NO_ENCONTRADO,
                    "Falta palabra reservada 'uses' antes de las unidades");
            }
        }
        
        if (lineaLimpia.toLowerCase().startsWith("program")) {
            analizarProgram(lineaLimpia, numeroLinea);
        } else if (lineaLimpia.toLowerCase().startsWith("uses")) {
            analizarUses(lineaLimpia, numeroLinea);
        } else if (lineaLimpia.toLowerCase().startsWith("const")) {
            analizarConst(lineaLimpia, numeroLinea);
        } else if (lineaLimpia.toLowerCase().startsWith("var")) {
            analizarVar(lineaLimpia, numeroLinea);
            enSeccionVariables = true;
        } else if (lineaLimpia.toLowerCase().equals("begin")) {
            analizarBegin(lineaLimpia, numeroLinea);
            enSeccionVariables = false;
        } else if (lineaLimpia.toLowerCase().startsWith("begin")) {
            analizarBeginConComentario(lineaLimpia, numeroLinea);
            enSeccionVariables = false;
        } else if (lineaLimpia.toLowerCase().equals("end.")) {
            analizarEnd(lineaLimpia, numeroLinea, esUltimaLinea);
        } else if (lineaLimpia.toLowerCase().equals("end")) {
            analizarEndSinPunto(lineaLimpia, numeroLinea, esUltimaLinea);
        } else if (lineaLimpia.toLowerCase().startsWith("write")) {
            analizarWrite(lineaLimpia, numeroLinea);
        } else {
            if (lineaLimpia.contains("=") && !constEncontrado && usesEncontrado && !varEncontrado && !lineaLimpia.toLowerCase().contains("const")) {
                manejadorErrores.agregarError(numeroLinea, CodigosError.CONSTANTE_SIN_CONST,
                    "Falta palabra reservada 'const' antes de la declaracion de constante");
            }
            
            if (enSeccionVariables && lineaLimpia.matches(".*\\s*:\\s*(word|integer|byte|string|real|boolean).*")) {
                validarDeclaracionVariableSinVar(lineaLimpia, numeroLinea);
                validarPuntoComaVariable(lineaLimpia, numeroLinea);
                validarIdentificadorVariableReservada(lineaLimpia, numeroLinea);
            }
            
            if (varEncontrado && !beginEncontrado && lineaLimpia.matches(".*\\s*:\\s*(word|integer|byte|string|real|boolean).*") && !lineaLimpia.toLowerCase().startsWith("var")) {
                validarPuntoComaVariable(lineaLimpia, numeroLinea);
                validarIdentificadorVariableReservada(lineaLimpia, numeroLinea);
            }
        }
    }
    
    private void analizarProgram(String linea, int numeroLinea) {
        if (programEncontrado) {
            manejadorErrores.agregarError(numeroLinea, CodigosError.PROGRAM_NO_ENCONTRADO,
                "Program ya fue declarado anteriormente");
            return;
        }
        
        if (!linea.endsWith(";")) {
            manejadorErrores.agregarError(numeroLinea, CodigosError.ESTRUCTURA_INCORRECTA,
                "Program debe terminar con punto y coma");
        }
        
        String[] partes = linea.split("\\s+");
        if (partes.length > 1) {
            String identificador = partes[1].replace(";", "");
            if (!identificador.isEmpty() && !identificador.equalsIgnoreCase(nombreArchivo)) {
                manejadorErrores.agregarError(numeroLinea, CodigosError.PROGRAM_NOMBRE_INCORRECTO,
                    "El nombre del programa '" + identificador + "' no coincide con el archivo '" + nombreArchivo + "'");
            }
        }
        
        programEncontrado = true;
    }
    
    private void analizarUses(String linea, int numeroLinea) {
        if (!programEncontrado) {
            manejadorErrores.agregarError(numeroLinea, CodigosError.USES_NO_ENCONTRADO,
                "Uses debe venir despues de program");
            return;
        }
        
        if (usesEncontrado) {
            manejadorErrores.agregarError(numeroLinea, CodigosError.USES_NO_ENCONTRADO,
                "Uses ya fue declarado anteriormente");
            return;
        }
        
        if (!linea.endsWith(";")) {
            manejadorErrores.agregarError(numeroLinea, CodigosError.ESTRUCTURA_INCORRECTA,
                "Uses debe terminar con punto y coma");
        }
        
        usesEncontrado = true;
    }
    
    private void analizarConst(String linea, int numeroLinea) {
        if (!usesEncontrado) {
            manejadorErrores.agregarError(numeroLinea, CodigosError.CONSTANTE_UBICACION_INCORRECTA,
                "Const debe venir despues de uses");
            return;
        }
        
        if (varEncontrado) {
            manejadorErrores.agregarError(numeroLinea, CodigosError.CONSTANTE_UBICACION_INCORRECTA,
                "Const debe venir antes de var");
            return;
        }
        
        if (beginEncontrado) {
            manejadorErrores.agregarError(numeroLinea, CodigosError.CONSTANTE_UBICACION_INCORRECTA,
                "Const no puede venir despues de begin");
            return;
        }
        
        validarFormatoConstante(linea, numeroLinea);
        constEncontrado = true;
    }
    
    private void validarFormatoConstante(String linea, int numeroLinea) {
        if (linea.trim().equalsIgnoreCase("const")) {
            return;
        }
        
        if (!linea.contains("=")) {
            manejadorErrores.agregarError(numeroLinea, CodigosError.CONSTANTE_FORMATO_INCORRECTO,
                "Declaracion de constante debe tener formato: nombre = valor;");
            return;
        }
        
        if (!linea.endsWith(";")) {
            manejadorErrores.agregarError(numeroLinea, CodigosError.CONSTANTE_SIN_PUNTO_COMA,
                "Declaracion de constante debe terminar con punto y coma");
        }
    }
    
    private void analizarVar(String linea, int numeroLinea) {
        if (!usesEncontrado && !constEncontrado) {
            manejadorErrores.agregarError(numeroLinea, CodigosError.VARIABLE_UBICACION_INCORRECTA,
                "Var debe venir despues de uses o const");
            return;
        }
        
        if (beginEncontrado) {
            manejadorErrores.agregarError(numeroLinea, CodigosError.VARIABLE_UBICACION_INCORRECTA,
                "Var debe venir antes de begin");
            return;
        }
        
        if (linea.trim().equalsIgnoreCase("var")) {
            varEncontrado = true;
            return;
        }
        
        if (linea.contains(":") && !linea.trim().equalsIgnoreCase("var")) {
            validarDeclaracionVariable(linea, numeroLinea);
        }
        
        varEncontrado = true;
    }
    
    private void validarDeclaracionVariable(String linea, int numeroLinea) {
        String patron = "\\s*([a-zA-Z_][a-zA-Z0-9_]*)\\s*:\\s*(word|integer|byte|string|real|boolean)\\s*;?";
        if (linea.matches(patron)) {
            String[] partes = linea.split(":");
            if (partes.length > 0) {
                String identificador = partes[0].trim();
                validarIdentificadorVariable(identificador, numeroLinea);
                validarTipoVariable(partes.length > 1 ? partes[1].trim() : "", numeroLinea);
            }
        } else {
            manejadorErrores.agregarError(numeroLinea, CodigosError.VARIABLE_FORMATO_INCORRECTO,
                "Formato de declaracion de variable incorrecto. Formato esperado: nombre : tipo;");
        }
    }
    
    private void validarTipoVariable(String tipo, int numeroLinea) {
        tipo = tipo.replace(";", "").trim();
        String[] tiposValidos = {"word", "integer", "byte", "string", "real", "boolean"};
        boolean tipoValido = false;
        
        for (String tipoVal : tiposValidos) {
            if (tipo.equalsIgnoreCase(tipoVal)) {
                tipoValido = true;
                break;
            }
        }
        
        if (!tipoValido && !tipo.isEmpty()) {
            manejadorErrores.agregarError(numeroLinea, CodigosError.VARIABLE_TIPO_INVALIDO,
                "Tipo de variable invalido: " + tipo + ". Tipos validos: integer, string, Word");
        }
    }
    
    private void validarIdentificadorVariable(String identificador, int numeroLinea) {
        if (identificador.isEmpty()) {
            manejadorErrores.agregarError(numeroLinea, CodigosError.VARIABLE_FORMATO_INCORRECTO,
                "Variable debe tener un identificador");
            return;
        }
        
        if (Character.isDigit(identificador.charAt(0))) {
            manejadorErrores.agregarError(numeroLinea, CodigosError.IDENTIFICADOR_NUMERO_INICIAL,
                "Identificador de variable no puede comenzar con numero: " + identificador);
        }
        
        if (PalabrasReservadas.esPalabraReservada(identificador)) {
            manejadorErrores.agregarError(numeroLinea, CodigosError.IDENTIFICADOR_PALABRA_RESERVADA,
                "No se puede usar palabra reservada como identificador de variable: " + identificador);
        }
    }
    
    private void validarDeclaracionVariableSinVar(String linea, int numeroLinea) {
        if (!linea.toLowerCase().startsWith("var") && varEncontrado) {
            String patron = "\\s*([a-zA-Z_][a-zA-Z0-9_]*)\\s*:\\s*(word|integer|byte|string|real|boolean)\\s*;?";
            if (linea.matches(patron)) {
                return;
            }
        }
    }
    
    private void validarPuntoComaVariable(String linea, int numeroLinea) {
        if (linea.contains(":") && !linea.trim().endsWith(";") && linea.matches(".*\\s*:\\s*(word|integer|byte|string|real|boolean)\\s*$")) {
            manejadorErrores.agregarError(numeroLinea, CodigosError.VARIABLE_SIN_PUNTO_COMA,
                "La declaracion de variable debe terminar con punto y coma");
        }
    }
    
    private void validarIdentificadorVariableReservada(String linea, int numeroLinea) {
        String patron = "\\s*([a-zA-Z_][a-zA-Z0-9_]*)\\s*:\\s*(word|integer|byte|string|real|boolean)";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(patron, java.util.regex.Pattern.CASE_INSENSITIVE);
        java.util.regex.Matcher m = p.matcher(linea);
        
        if (m.find()) {
            String identificador = m.group(1);
            if (PalabrasReservadas.esPalabraReservada(identificador)) {
                manejadorErrores.agregarError(numeroLinea, CodigosError.IDENTIFICADOR_PALABRA_RESERVADA,
                    "No se puede usar la palabra reservada '" + identificador + "' como identificador de variable");
            }
        }
    }
    
    private void analizarBegin(String linea, int numeroLinea) {
        if (!linea.trim().equalsIgnoreCase("begin")) {
            manejadorErrores.agregarError(numeroLinea, CodigosError.BEGIN_NO_SOLO,
                "Begin debe estar solo en su linea sin otros elementos");
            return;
        }
        
        if (beginEncontrado) {
            manejadorErrores.agregarError(numeroLinea, CodigosError.BEGIN_UBICACION_INCORRECTA,
                "Begin ya fue declarado anteriormente");
            return;
        }
        
        beginEncontrado = true;
    }
    
    private void analizarBeginConComentario(String linea, int numeroLinea) {
        String lineaLimpia = linea.trim();
        String despuesDeBegin = lineaLimpia.replaceFirst("(?i)begin", "").trim();
        
        if (!despuesDeBegin.isEmpty()) {
            if (despuesDeBegin.startsWith("//") || despuesDeBegin.startsWith("{")) {
                manejadorErrores.agregarError(numeroLinea, CodigosError.BEGIN_NO_SOLO,
                    "Begin debe estar solo en su linea, sin comentarios");
            }
        }
        
        if (!beginEncontrado) {
            beginEncontrado = true;
        }
    }
    
    private void analizarEnd(String linea, int numeroLinea, boolean esUltimaLinea) {
        if (!linea.trim().equals("end.")) {
            manejadorErrores.agregarError(numeroLinea, CodigosError.END_SIN_PUNTO,
                "End debe terminar con punto: 'end.'");
            return;
        }
        
        if (!beginEncontrado) {
            manejadorErrores.agregarError(numeroLinea, CodigosError.END_UBICACION_INCORRECTA,
                "End. no puede aparecer antes de begin");
            return;
        }
        
        if (!esUltimaLinea) {
            manejadorErrores.agregarError(numeroLinea, CodigosError.END_UBICACION_INCORRECTA,
                "End. debe ser la ultima linea del archivo");
            return;
        }
        
        endEncontrado = true;
    }
    
    private void analizarEndSinPunto(String linea, int numeroLinea, boolean esUltimaLinea) {
        if (esUltimaLinea && linea.trim().equalsIgnoreCase("end")) {
            manejadorErrores.agregarError(numeroLinea, CodigosError.END_SIN_PUNTO,
                "El programa debe terminar con 'end.' (end seguido de punto)");
        }
    }
    
    private void analizarWrite(String linea, int numeroLinea) {
        if (!beginEncontrado) {
            manejadorErrores.agregarError(numeroLinea, CodigosError.WRITE_SIN_BEGIN,
                "La instruccion write/writeln debe estar despues de 'begin'");
            return;
        }
        
        if (!linea.contains("(") || !linea.contains(")")) {
            manejadorErrores.agregarError(numeroLinea, CodigosError.WRITE_SIN_PARENTESIS,
                "Write/Writeln debe tener parentesis: write(...)");
            return;
        }
        
        int inicio = linea.indexOf("(");
        int fin = linea.lastIndexOf(")");
        
        if (inicio >= fin) {
            manejadorErrores.agregarError(numeroLinea, CodigosError.WRITE_SIN_PARENTESIS,
                "Parentesis mal formados en write/writeln");
            return;
        }
        
        String contenido = linea.substring(inicio + 1, fin).trim();
        if (contenido.isEmpty()) {
            manejadorErrores.agregarError(numeroLinea, CodigosError.WRITE_VACIO,
                "Write/Writeln no puede estar vacio");
            return;
        }
        
        if (!linea.endsWith(";")) {
            manejadorErrores.agregarError(numeroLinea, CodigosError.WRITE_SIN_PUNTO_COMA,
                "Write/Writeln debe terminar con punto y coma");
        }
    }
    
    private String eliminarComentarios(String linea) {
        String resultado = linea;
        
        int posSlash = resultado.indexOf("//");
        if (posSlash != -1) {
            resultado = resultado.substring(0, posSlash);
        }
        
        int posInicioLlave = resultado.indexOf("{");
        int posFinLlave = resultado.indexOf("}");
        if (posInicioLlave != -1 && posFinLlave != -1 && posFinLlave > posInicioLlave) {
            resultado = resultado.substring(0, posInicioLlave) + resultado.substring(posFinLlave + 1);
        }
        
        return resultado;
    }
    
    private void validarEstructuraCompleta() {
        if (!programEncontrado) {
            manejadorErrores.agregarError(CodigosError.PROGRAM_NO_ENCONTRADO,
                "No se encontro la declaración 'program' al inicio del archivo");
        }
        
        if (!usesEncontrado) {
            manejadorErrores.agregarError(CodigosError.USES_NO_ENCONTRADO,
                "No se encontro la declaración 'uses' después de program");
        }
        
        if (!beginEncontrado) {
            manejadorErrores.agregarError(CodigosError.BEGIN_UBICACION_INCORRECTA,
                "No se encontro la declaración 'begin'");
        }
        
        if (!endEncontrado) {
            manejadorErrores.agregarError(CodigosError.END_UBICACION_INCORRECTA,
                "No se encontro la declaración 'end.' al final del archivo");
        }
    }
}