Check in Aula – Proyecto Java

📍 Contexto

Este proyecto es un sistema simple de registro de inscripciones al inicio de clase. Permite:

•	Registrar inscripciones (Nombre, Documento, Curso, Hora automática).
•	Listar todas las inscripciones y buscar por texto.
•	Mostrar un resumen básico por curso.

En esta versión, se agrega persistencia simple usando un archivo CSV, de modo que los datos se guardan entre ejecuciones. Además, se mantiene interfaz por consola y se agrega una GUI mínima usando JFrame.
________________________________________
🗂 Estructura del proyecto
src/
└─ edu/cerp/checkin/
   ├─ model/
   │   └─ Inscripcion.java
   ├─ logic/
   │   └─ SesionService.java
   ├─ persistencia/
   │   └─ ArchivoManager.java
   ├─ console/
   │   └─ MainConsole.java
   ├─ ui/
   │   └─ CheckInGUI.java
   └─ App.java
data/
└─ inscripciones.csv   # archivo de persistencia generado automáticamente

________________________________________
🧾 Código principal

Inscripcion.java
package edu.cerp.checkin.model;

import java.time.LocalDateTime;

public class Inscripcion {
    private final String nombre;
    private final String documento;
    private final String curso;
    private final LocalDateTime fechaHora;

    public Inscripcion(String nombre, String documento, String curso, LocalDateTime fechaHora) {
        this.nombre = nombre;
        this.documento = documento;
        this.curso = curso;
        this.fechaHora = fechaHora;
    }

    public String getNombre() { return nombre; }
    public String getDocumento() { return documento; }
    public String getCurso() { return curso; }
    public LocalDateTime getFechaHora() { return fechaHora; }
}
________________________________________
ArchivoManager.java
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
________________________________________
SesionService.java
package edu.cerp.checkin.logic;

import edu.cerp.checkin.model.Inscripcion;
import edu.cerp.checkin.persistencia.ArchivoManager;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class SesionService {
    private final List<Inscripcion> inscripciones = new ArrayList<>();

    public SesionService() {
        // Cargar datos desde archivo al iniciar
        inscripciones.addAll(ArchivoManager.cargar());
        if (inscripciones.isEmpty()) cargarDatosDemo();
    }

    public void registrar(String nombre, String documento, String curso) {
        if (nombre == null || nombre.isBlank()) nombre = "(sin nombre)";
        if (documento == null) documento = "";
        if (curso == null || curso.isBlank()) curso = "Prog 1";
        Inscripcion i = new Inscripcion(nombre.trim(), documento.trim(), curso.trim(), LocalDateTime.now());
        inscripciones.add(i);
        ArchivoManager.guardar(inscripciones);
    }

    public List<Inscripcion> listar() { return Collections.unmodifiableList(inscripciones); }

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

    /** Datos de prueba para arrancar la app */
    public void cargarDatosDemo() {
        registrar("Ana Pérez", "51234567", "Prog 2");
        registrar("Luis Gómez", "49887766", "Prog 1");
        registrar("Camila Díaz", "53422110", "Base de Datos");
    }
}
________________________________________
MainConsole.java
package edu.cerp.checkin.console;

import edu.cerp.checkin.logic.SesionService;
import edu.cerp.checkin.model.Inscripcion;
import java.util.List;
import java.util.Scanner;

public class MainConsole {
    public static void run(SesionService service){
        Scanner sc = new Scanner(System.in);
        int op = -1;
        while (op != 0) {
            System.out.println("\n== Check-in Aula (Consola) ==");
            System.out.println("1) Registrar  2) Listar  3) Buscar  4) Resumen  0) Salir");
            System.out.print("> ");
            String s = sc.nextLine().trim();
            if (s.isEmpty()) continue;
            try { op = Integer.parseInt(s); } catch(Exception e){ op = -1; }
            switch (op) {
                case 1 -> {
                    System.out.print("Nombre: "); String n = sc.nextLine();
                    System.out.print("Documento: "); String d = sc.nextLine();
                    System.out.print("Curso [Prog 1/Prog 2/Base de Datos]: "); String c = sc.nextLine();
                    service.registrar(n,d,c);
                    System.out.println("✔ Registrado");
                }
                case 2 -> listar(service.listar());
                case 3 -> { System.out.print("Buscar: "); String q = sc.nextLine(); listar(service.buscar(q)); }
                case 4 -> System.out.println(service.resumen());
                case 0 -> System.out.println("Adiós");
                default -> System.out.println("Opción inválida");
            }
        }
    }

    private static void listar(List<Inscripcion> ls){
        System.out.println("Nombre | Documento | Curso | Hora");
        for (Inscripcion i: ls) {
            System.out.println(i.getNombre()+" | "+i.getDocumento()+" | "+i.getCurso()+" | "+i.getFechaHora());
        }
    }
}
________________________________________
CheckInGUI.java
package edu.cerp.checkin.ui;

import edu.cerp.checkin.logic.SesionService;
import edu.cerp.checkin.model.Inscripcion;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class CheckInGUI {

    public static void show(SesionService service){
        JFrame frame = new JFrame("Check-in Aula");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500,400);
        frame.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridLayout(4,2));
        JTextField tfNombre = new JTextField();
        JTextField tfDocumento = new JTextField();
        JTextField tfCurso = new JTextField("Prog 1");
        JButton btnRegistrar = new JButton("Registrar");

        form.add(new JLabel("Nombre:")); form.add(tfNombre);
        form.add(new JLabel("Documento:")); form.add(tfDocumento);
        form.add(new JLabel("Curso:")); form.add(tfCurso);
        form.add(btnRegistrar);

        DefaultListModel<String> listModel = new DefaultListModel<>();
        JList<String> lista = new JList<>(listModel);
        JScrollPane scroll = new JScrollPane(lista);

        btnRegistrar.addActionListener(e -> {
            service.registrar(tfNombre.getText(), tfDocumento.getText(), tfCurso.getText());
            actualizarLista(service, listModel);
            tfNombre.setText(""); tfDocumento.setText(""); tfCurso.setText("Prog 1");
        });

        frame.add(form, BorderLayout.NORTH);
        frame.add(scroll, BorderLayout.CENTER);

        actualizarLista(service, listModel);

        frame.setVisible(true);
    }

    private static void actualizarLista(SesionService service, DefaultListModel<String> listModel){
        listModel.clear();
        List<Inscripcion> ls = service.listar();
        for (Inscripcion i: ls) {
            listModel.addElement(i.getNombre()+" | "+i.getDocumento()+" | "+i.getCurso()+" | "+i.getFechaHora());
        }
    }
}
________________________________________
App.java
package edu.cerp.checkin;

import edu.cerp.checkin.console.MainConsole;
import edu.cerp.checkin.logic.SesionService;
import edu.cerp.checkin.ui.CheckInGUI;

public class App {
    public static void main(String[] args){
        boolean usarGui = false;
        for (String a : args) if ("--gui".equalsIgnoreCase(a)) usarGui = true;

        SesionService service = new SesionService();

        if (usarGui) {
            CheckInGUI.show(service);
        } else {
            MainConsole.run(service);
        }
    }
}
________________________________________
💾 Persistencia
•	Archivo: data/inscripciones.csv
•	Formato: CSV con separador |
•	Justificación: CSV es simple, fácil de leer y suficiente para mantener datos entre ejecuciones en un proyecto educativo.
________________________________________
💻 Cómo compilar y ejecutar
Por consola:
# Compilar
javac -d out src/edu/cerp/checkin/**/*.java

# Ejecutar consola
java -cp out edu.cerp.checkin.App

# Ejecutar GUI
java -cp out edu.cerp.checkin.App --gui
La carpeta data/ se genera automáticamente al guardar la primera inscripción.
________________________________________
🔖 Notas
•	Se mantiene funcionalidad completa por consola y GUI mínima.
•	Cada inscripción se guarda automáticamente en el CSV.
•	Si el archivo existe, se cargan los datos al inicio, manteniendo la persistencia entre sesiones.

