package ipn.mx.cerraduraWEB.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Controller 
public class CerraduraController {

    
    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("operacion", new Operacion());
        return "index";  
    }

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
