Integrar ArchivoManager en SesionService

Modifica SesionService.java para cargar al inicio y guardar cada vez que registrás:

import edu.cerp.checkin.persistencia.ArchivoManager;

public class SesionService {
    private final List<Inscripcion> inscripciones = new ArrayList<>();

    public SesionService() {
        // Cargar datos desde archivo al iniciar
        inscripciones.addAll(ArchivoManager.cargar());
        if (inscripciones.isEmpty()) {
            cargarDatosDemo(); // solo si no hay nada guardado
        }
    }

    public void registrar(String nombre, String documento, String curso) {
        if (nombre == null || nombre.isBlank()) nombre = "(sin nombre)";
        if (documento == null) documento = "";
        if (curso == null || curso.isBlank()) curso = "Prog 1";
        Inscripcion i = new Inscripcion(nombre.trim(), documento.trim(), curso.trim(), LocalDateTime.now());
        inscripciones.add(i);

        // Guardar inmediatamente
        ArchivoManager.guardar(inscripciones);
    }

    // ... resto de métodos sin cambios ...
}