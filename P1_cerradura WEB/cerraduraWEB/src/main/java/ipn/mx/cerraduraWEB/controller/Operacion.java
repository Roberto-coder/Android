package ipn.mx.cerraduraWEB.controller;

import java.util.Set;
import java.util.HashSet;

/**
 * Clase que representa una operación sobre el conjunto de símbolos de un alfabeto.
 * Esta clase puede realizar operaciones de cerradura de Kleene y cerradura positiva.
 */
public class Operacion {

    private int longitud;
    private String operacionn;
    private String tipoOperacion;

    /**
     * Constructor por defecto.
     */
    public Operacion() {
        // Constructor por defecto
    }

    /**
     * Constructor que inicializa la longitud de la operación.
     * 
     * @param longitud La longitud de la operación.
     */
    public Operacion(Integer longitud) {
        this.longitud = longitud;
    }

    /**
     * Obtiene la longitud de la operación.
     * 
     * @return La longitud de la operación.
     */
    public Integer getLongitud() {
        return longitud;
    }

    /**
     * Establece la longitud de la operación.
     * 
     * @param longitud La nueva longitud de la operación.
     */
    public void setLongitud(Integer longitud) {
        this.longitud = longitud;
    }

    /**
     * Obtiene la cadena de operación.
     * 
     * @return La operación en formato String.
     */
    public String getOperacionn() {
        return operacionn;
    }

    /**
     * Establece la cadena de operación.
     * 
     * @param operacionn La operación a establecer.
     */
    public void setOperacionn(String operacionn) {
        this.operacionn = operacionn;
    }

    /**
     * Obtiene el tipo de operación (estrella o cerradura).
     * 
     * @return El tipo de operación.
     */
    public String getTipoOperacion() {
        return tipoOperacion;
    }

    /**
     * Establece el tipo de operación (estrella o cerradura).
     * 
     * @param tipoOperacion El tipo de operación a establecer.
     */
    public void setTipoOperacion(String tipoOperacion) {
        this.tipoOperacion = tipoOperacion;
    }

    /**
     * Genera la cerradura de Kleene para un alfabeto dado y un límite de longitud.
     * La cerradura de Kleene incluye todas las combinaciones posibles de longitud 0 hasta el límite especificado.
     * 
     * @param alfabeto El conjunto de símbolos del alfabeto.
     * @param limite El límite de longitud.
     * @return Un conjunto que contiene todas las combinaciones posibles de la cerradura de Kleene.
     */
    public static Set<String> cerraduraKleene(Set<String> alfabeto, int limite) {
        Set<String> kleene = new HashSet<>();
        kleene.add("λ");  // La cadena vacía forma parte de la cerradura de Kleene.

        for (int i = 1; i <= limite; i++) {
            kleene.addAll(generarCombinaciones(alfabeto, i));
        }
        return kleene;
    }

    /**
     * Genera la cerradura positiva para un alfabeto dado y un límite de longitud.
     * La cerradura positiva incluye todas las combinaciones posibles de longitud 1 hasta el límite especificado.
     * 
     * @param alfabeto El conjunto de símbolos del alfabeto.
     * @param limite El límite de longitud.
     * @return Un conjunto que contiene todas las combinaciones posibles de la cerradura positiva.
     */
    public static Set<String> cerraduraPositiva(Set<String> alfabeto, int limite) {
        Set<String> positiva = new HashSet<>();

        for (int i = 1; i <= limite; i++) {
            positiva.addAll(generarCombinaciones(alfabeto, i));
        }
        return positiva;
    }

    /**
     * Genera todas las combinaciones posibles de un alfabeto para una longitud dada.
     * 
     * @param alfabeto El conjunto de símbolos del alfabeto.
     * @param longitud La longitud de las combinaciones que se desean generar.
     * @return Un conjunto con todas las combinaciones posibles de la longitud especificada.
     */
    private static Set<String> generarCombinaciones(Set<String> alfabeto, int longitud) {
        Set<String> combinaciones = new HashSet<>();

        if (longitud == 1) {
            combinaciones.addAll(alfabeto);
        } else {
            Set<String> subCombinaciones = generarCombinaciones(alfabeto, longitud - 1);
            for (String subCombinacion : subCombinaciones) {
                for (String simbolo : alfabeto) {
                    combinaciones.add(subCombinacion + simbolo);
                }
            }
        }
        return combinaciones;
    }

    /**
     * Genera una tabla HTML que muestra las combinaciones de la cerradura de Kleene o la cerradura positiva 
     * en función del tipo de operación y del límite de longitud.
     * 
     * @param alfabeto El conjunto de símbolos del alfabeto.
     * @param limite El límite de longitud para generar las combinaciones.
     * @param tipoOperacion El tipo de operación ("estrella" para cerradura de Kleene o "cerradura" para cerradura positiva).
     * @return Una cadena que contiene el código HTML de la tabla con las combinaciones generadas.
     */
    public String generarTabla(Set<String> alfabeto, int limite, String tipoOperacion) {
        StringBuilder resultado = new StringBuilder();
        resultado.append("<table class='table table-bordered'><thead><tr><th>Longitud</th><th>Combinaciones</th></tr></thead><tbody>");

        if ("estrella".equals(tipoOperacion)) {
            for (int i = 1; i <= limite; i++) {
                Set<String> kleene = cerraduraKleene(alfabeto, i);
                resultado.append("<tr><td>").append(i).append("</td><td>Σ^* = ").append(kleene).append("</td></tr>");
            }
        } else if ("cerradura".equals(tipoOperacion)) {
            for (int i = 1; i <= limite; i++) {
                Set<String> positiva = cerraduraPositiva(alfabeto, i);
                resultado.append("<tr><td>").append(i).append("</td><td>Σ^+ = ").append(positiva).append("</td></tr>");
            }
        }

        resultado.append("</tbody></table>");
        return resultado.toString();
    }
}
