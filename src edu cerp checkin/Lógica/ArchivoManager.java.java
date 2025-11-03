package edu.cerp.checkin.persistencia;

import edu.cerp.checkin.model.Inscripcion;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/** Persistencia simple usando CSV */
public class ArchivoManager {

    private static final String RUTA = "data/inscripciones.csv";
    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    /** Guarda todas las inscripciones en CSV */
    public static void guardar(List<Inscripcion> inscripciones) {
        File carpeta = new File("data");
        if (!carpeta.exists()) carpeta.mkdir();

        try (PrintWriter pw = new PrintWriter(new FileWriter(RUTA))) {
            for (Inscripcion i : inscripciones) {
                pw.println(String.join("|",
                        i.getNombre(),
                        i.getDocumento(),
                        i.getCurso(),
                        i.getFechaHora().format(FORMATO)
                ));
            }
        } catch (IOException e) {
            System.err.println("Error guardando archivo: " + e.getMessage());
        }
    }

    /** Carga inscripciones desde CSV */
    public static List<Inscripcion> cargar() {
        List<Inscripcion> lista = new ArrayList<>();
        File f = new File(RUTA);
        if (!f.exists()) return lista;

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] partes = linea.split("\\|");
                if (partes.length != 4) continue;
                String nombre = partes[0];
                String documento = partes[1];
                String curso = partes[2];
                LocalDateTime fechaHora = LocalDateTime.parse(partes[3], FORMATO);
                lista.add(new Inscripcion(nombre, documento, curso, fechaHora));
            }
        } catch (IOException e) {
            System.err.println("Error cargando archivo: " + e.getMessage());
        }
        return lista;
    }
}
