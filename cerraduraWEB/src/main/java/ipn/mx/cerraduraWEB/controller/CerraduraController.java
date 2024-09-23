package ipn.mx.cerraduraWEB.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class CerraduraController {

  
    @GetMapping ("/")
    public String index(Model model) {
        model.addAttribute("operacion",new Operacion());
                return "index";
}
      @PostMapping("/operaciones")
    public String operar(@ModelAttribute Operacion operacion, Model model ){
        String cadena = operacion.getCadena();
        Integer longitud = operacion.getLongitud();

        String resultado = "Resultado de la cerradura de Kleene para la cadena: " + cadena + " con longitud: " + longitud;

        model.addAttribute("resultado", resultado);
        return "index"; 
    }  
}