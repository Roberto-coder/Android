package ipn.mx.cerraduraWEB.controller;

import java.util.Set;
import java.util.HashSet;

public class Operacion {

    private int longitud;
    private String operacionn;
    private String tipoOperacion;

    public Operacion() {
        // Constructor por defecto
    }

    public Operacion(Integer longitud) {
        this.longitud = longitud;
    }

    public Integer getLongitud() {
        return longitud;
    }

    public void setLongitud(Integer longitud) {
        this.longitud = longitud;
    }

    public String getOperacionn() {
        return operacionn;
    }

    public void setOperacionn(String operacionn) {
        this.operacionn = operacionn;
    }

    public String getTipoOperacion() {
        return tipoOperacion;
    }

    public void setTipoOperacion(String tipoOperacion) {
        this.tipoOperacion = tipoOperacion;
    }

    public static Set<String> cerraduraKleene(Set<String> alfabeto, int limite) {
        Set<String> kleene = new HashSet<>();
        kleene.add("");  // Agregar cadena vacía para la cerradura de Kleene

        for (int i = 1; i <= limite; i++) {
            kleene.addAll(generarCombinaciones(alfabeto, i));
        }
        return kleene;
    }

    public static Set<String> cerraduraPositiva(Set<String> alfabeto, int limite) {
        Set<String> positiva = new HashSet<>();

        for (int i = 1; i <= limite; i++) {
            positiva.addAll(generarCombinaciones(alfabeto, i));
        }
        return positiva;
    }

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
