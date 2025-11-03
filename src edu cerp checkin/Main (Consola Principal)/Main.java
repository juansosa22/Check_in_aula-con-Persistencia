public class Main {
    public static void main(String[] args) {
        SesionService s = new SesionService();
        s.cargarDatosDemo();

        if (args.length > 0 && args[0].equals("--gui")) {
            CheckInGUI.show(s);
        } else {
            MainConsole.run(s);
        }
    }
}

class MainConsole {
    public static void run(SesionService s) {
        System.out.println("Consola funcionando...");
    }
}

class SesionService {
    public void cargarDatosDemo() {
        System.out.println("Datos de demo cargados.");
    }
}

class CheckInGUI {
    public static void show(SesionService s) {
        System.out.println("Interfaz gráfica abierta.");
    }
}
