package ipn.mx.cerraduraWEB.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Set;
import java.util.HashSet;

@Controller
public class CerraduraController {

    // Muestra la página inicial con un formulario vacío
    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("operacion", new Operacion());
        return "index";  // Asegúrate de que index.html esté en la carpeta /templates
    }

    // Procesa el formulario enviado desde index.html
    @PostMapping("/operaciones")
    public String operar(@ModelAttribute Operacion operacion, Model model) {
        Integer longitud = operacion.getLongitud();
        String tipoOperacion = operacion.getTipoOperacion();

        Set<String> alfabeto = new HashSet<>();
        alfabeto.add("0");
        alfabeto.add("1");

        // Genera el resultado de la operación según el tipo de cerradura
        Operacion nuevaOperacion = new Operacion(longitud);
        String resultado = nuevaOperacion.generarTabla(alfabeto, longitud, tipoOperacion);
        
        model.addAttribute("resultado", resultado);
        model.addAttribute("operacion", operacion);  // Asegúrate de que 'operacion' esté en el modelo

        return "index";
    }

    // Endpoint para la Cerradura de Kleene (Estrella)
    @GetMapping("/operaciones/estrella/{limite}")
    public String generarCerraduraKleene(@PathVariable("limite") int limite, Model model) {
        Set<String> alfabeto = new HashSet<>();
        alfabeto.add("0");
        alfabeto.add("1");

        // Generar el resultado
        Operacion nuevaOperacion = new Operacion(limite);
        String resultado = nuevaOperacion.generarTabla(alfabeto, limite, "estrella");

        // Añadir el resultado y el objeto operación al modelo
        model.addAttribute("resultado", resultado);
        model.addAttribute("operacion", new Operacion());  // Siempre agregar el objeto Operacion al modelo

        return "index";  // Asegúrate de que index.html esté correctamente definido
    }

    // Endpoint para la Cerradura Positiva
    @GetMapping("/operaciones/cerradura/{limite}")
    public String generarCerraduraPositiva(@PathVariable("limite") int limite, Model model) {
        Set<String> alfabeto = new HashSet<>();
        alfabeto.add("0");
        alfabeto.add("1");

        // Generar el resultado
        Operacion nuevaOperacion = new Operacion(limite);
        String resultado = nuevaOperacion.generarTabla(alfabeto, limite, "cerradura");

        // Añadir el resultado y el objeto operación al modelo
        model.addAttribute("resultado", resultado);
        model.addAttribute("operacion", new Operacion());  // Siempre agregar el objeto Operacion al modelo

        return "index";  // Asegúrate de que index.html esté correctamente definido
    }
}
