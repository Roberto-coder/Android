package ipn.mx.cerraduraWEB.controller;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Clase principal que arranca la aplicación web de la cerradura de Kleene y estrella.
 * 
 * Esta clase utiliza el marco de trabajo Spring Boot para ejecutar una aplicación web.
 */
@SpringBootApplication
public class CerradurawebApplication {

    /**
     * Método principal que inicia la aplicación.
     * 
     * Este método utiliza SpringApplication.run para iniciar el contenedor de Spring
     * y ejecutar la aplicación.
     *
     * @param args Argumentos de línea de comandos (opcional).
     */
    public static void main(String[] args) {
        SpringApplication.run(CerradurawebApplication.class, args);
    }

}
