package ipn.mx.cerraduraWEB.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Controlador para manejar las operaciones de cerradura de Kleene y cerradura positiva.
 * 
 * Este controlador gestiona tanto las operaciones realizadas en el frontend como las respuestas en formato JSON 
 * que pueden ser solicitadas a través de la API.
 */
@Controller 
public class CerraduraController {

    /**
     * Maneja las solicitudes GET al endpoint raíz ("/").
     * 
     * Este método carga la página principal y pasa un objeto de Operacion vacío al modelo.
     *
     * @param model El modelo utilizado para pasar datos a la vista.
     * @return El nombre de la vista "index".
     */
    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("operacion", new Operacion());
        return "index";  
    }

    /**
     * Maneja las solicitudes POST al endpoint "/operaciones".
     * 
     * Este método realiza las operaciones basadas en el tipo de operación y la longitud
     * que el usuario define, luego devuelve el resultado a la vista.
     * 
     * @param operacion Objeto que contiene los detalles de la operación (tipo y longitud).
     * @param model El modelo utilizado para pasar los resultados a la vista.
     * @return El nombre de la vista "index" con los resultados.
     */
    @PostMapping("/operaciones")
    public String operar(@ModelAttribute Operacion operacion, Model model) {
        Integer longitud = operacion.getLongitud();
        String tipoOperacion = operacion.getTipoOperacion();

        Set<String> alfabeto = new HashSet<>();
        alfabeto.add("0");
        alfabeto.add("1");

        Operacion nuevaOperacion = new Operacion(longitud);
        String resultado = nuevaOperacion.generarTabla(alfabeto, longitud, tipoOperacion);
        
        model.addAttribute("resultado", resultado);
        model.addAttribute("operacion", operacion);  

        return "index";
    }

    /**
     * Genera la cerradura de Kleene para un alfabeto dado y un límite, y devuelve los resultados en formato JSON.
     * 
     * Este método maneja solicitudes GET al endpoint "/api/operaciones/estrella/{limite}".
     * 
     * @param limite El límite de longitud para la operación de cerradura de Kleene.
     * @return Un ResponseEntity que contiene un mapa con el resultado de la operación, tipo de operación, y longitud.
     */
    @GetMapping("/api/operaciones/estrella/{limite}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> generarCerraduraKleeneJson(@PathVariable("limite") int limite) {
        Set<String> alfabeto = new HashSet<>();
        alfabeto.add("0");
        alfabeto.add("1");

        Set<String> resultado = Operacion.cerraduraKleene(alfabeto, limite);

        Map<String, Object> response = new HashMap<>();
        response.put("resultado", resultado);
        response.put("tipoOperacion", "estrella");
        response.put("longitud", limite);

        // Devuelve la respuesta JSON
        return ResponseEntity.ok(response);
    }

    /**
     * Genera la cerradura positiva para un alfabeto dado y un límite, y devuelve los resultados en formato JSON.
     * 
     * Este método maneja solicitudes GET al endpoint "/api/operaciones/cerradura/{limite}".
     * 
     * @param limite El límite de longitud para la operación de cerradura positiva.
     * @return Un ResponseEntity que contiene un mapa con el resultado de la operación, tipo de operación, y longitud.
     */
    @GetMapping("/api/operaciones/cerradura/{limite}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> generarCerraduraPositivaJson(@PathVariable("limite") int limite) {
        Set<String> alfabeto = new HashSet<>();
        alfabeto.add("0");
        alfabeto.add("1");

        Set<String> resultado = Operacion.cerraduraPositiva(alfabeto, limite);

        Map<String, Object> response = new HashMap<>();
        response.put("resultado", resultado);
        response.put("tipoOperacion", "cerradura");
        response.put("longitud", limite);

        return ResponseEntity.ok(response);
    }
}
