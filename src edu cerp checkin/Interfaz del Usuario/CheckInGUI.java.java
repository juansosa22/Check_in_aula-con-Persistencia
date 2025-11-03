package edu.cerp.checkin.logic;

import edu.cerp.checkin.model.Inscripcion;
import edu.cerp.checkin.persistencia.ArchivoManager;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class SesionService {

    private final List<Inscripcion> inscripciones = new ArrayList<>();
    private final ArchivoManager archivoManager;

    public SesionService() {
        this.archivoManager = null;
    }

    public SesionService(ArchivoManager archivoManager) {
        this.archivoManager = archivoManager;
        if (archivoManager != null) {
            List<Inscripcion> cargadas = archivoManager.cargar();
            if (cargadas != null) inscripciones.addAll(cargadas);
        }
    }

    public void registrar(String nombre, String documento, String curso) {
        if (nombre == null || nombre.isBlank()) nombre = "(sin nombre)";
        if (documento == null) documento = "";
        if (curso == null || curso.isBlank()) curso = "Prog 1";

        Inscripcion i = new Inscripcion(nombre.trim(), documento.trim(), curso.trim(), LocalDateTime.now());
        inscripciones.add(i);

        if (archivoManager != null) archivoManager.guardar(inscripciones);
    }

    public List<Inscripcion> listar() {
        return Collections.unmodifiableList(inscripciones);
    }

    public List<Inscripcion> buscar(String q) {
        if (q == null || q.isBlank()) return listar();
        String s = q.toLowerCase();
        return inscripciones.stream()
                .filter(i -> i.getNombre().toLowerCase().contains(s) || i.getDocumento().toLowerCase().contains(s))
                .collect(Collectors.toList());
    }

    public String resumen() {
        Map<String, Long> porCurso = inscripciones.stream()
                .collect(Collectors.groupingBy(Inscripcion::getCurso, LinkedHashMap::new, Collectors.counting()));
        StringBuilder sb = new StringBuilder();
        sb.append("Total: ").append(inscripciones.size()).append("\nPor curso:\n");
        for (var e : porCurso.entrySet()) sb.append(" - ").append(e.getKey()).append(": ").append(e.getValue()).append("\n");
        return sb.toString();
    }

    public void cargarDatosDemo() {
        registrar("Ana Pérez", "51234567", "Prog 2");
        registrar("Luis Gómez", "49887766", "Prog 1");
        registrar("Camila Díaz", "53422110", "Base de Datos");
    }
}
