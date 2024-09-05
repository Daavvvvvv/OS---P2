
import kareltherobot.*;

public class GestionSemaforos implements Directions {
    public static void main(String[] args) {
        // Crear los semáforos en las intersecciones clave
        Semaforo semaforoCalle12Avenida4 = new Semaforo();
        Semaforo semaforoCalle19Avenida4 = new Semaforo();

        // Crear los controladores de los semáforos
        ControlSemaforo controlSemaforo1 = new ControlSemaforo(semaforoCalle12Avenida4);
        ControlSemaforo controlSemaforo2 = new ControlSemaforo(semaforoCalle19Avenida4);
        controlSemaforo1.start();
        controlSemaforo2.start();

        // Crear el mundo
        World.readWorld("wordl.kwld");
        World.setVisible(true);

        // Crear los robots
        RobotConSemaforo robot1 = new RobotConSemaforo(12, 1, East, 5, semaforoCalle12Avenida4);
        RobotConSemaforo robot2 = new RobotConSemaforo(19, 7, West, 5, semaforoCalle19Avenida4);

        // Ejecutar los robots en hilos separados
        Thread hiloRobot1 = new Thread(() -> {
            // Robot 1 se mueve hacia la avenida 4, verificando el semáforo en la intersección
            robot1.moverseConSemaforoEnInterseccion(4, 12); // Calle 12, intersección con Avenida 4
        });

        Thread hiloRobot2 = new Thread(() -> {
            // Robot 2 se mueve hacia la avenida 4, verificando el semáforo en la intersección
            robot2.moverseConSemaforoEnInterseccion(4, 19); // Calle 19, intersección con Avenida 4
        });

        // Iniciar ambos hilos
        hiloRobot1.start();
        hiloRobot2.start();
    }
}

class Semaforo {
    private boolean luzVerde;

    public Semaforo() {
        this.luzVerde = false;
    }

    public synchronized void cambiarLuz() {
        luzVerde = !luzVerde;
        System.out.println(luzVerde ? "El semaforo esta en VERDE." : "El semaforo esta en ROJO.");
    }

    public synchronized boolean puedeCruzar() {
        return luzVerde;
    }
}

class ControlSemaforo extends Thread {
    private Semaforo semaforo;

    public ControlSemaforo(Semaforo semaforo) {
        this.semaforo = semaforo;
    }

    @Override
    public void run() {
        try {
            while (true) {
                Thread.sleep(5000); // Cambia el semáforo cada 5 segundos
                semaforo.cambiarLuz();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

class RobotConSemaforo extends Robot {
    private int avenue; // Registro de la avenida
    private int street; // Registro de la calle
    private Direction direccion; // Nueva variable para la dirección
    private Semaforo semaforo;

    public RobotConSemaforo(int street, int avenue, Direction direction, int beeps, Semaforo semaforo) {
        super(street, avenue, direction, beeps);
        this.street = street;
        this.avenue = avenue;
        this.direccion = direction; // Inicializamos la dirección
        this.semaforo = semaforo;
    }

    // Método para moverse y solo detenerse en intersecciones
    public void moverseConSemaforoEnInterseccion(int interseccionAvenue, int interseccionStreet) {
        try {
            // Mientras no estemos en la intersección, seguimos moviéndonos
            while (street != interseccionStreet || avenue != interseccionAvenue) {
                move();
                System.out.println("Robot en la calle: " + street + ", avenida: " + avenue);

                // Actualizar las posiciones del robot dependiendo de la dirección
                if (direccion == East) {
                    avenue++;
                } else if (direccion == West) {
                    avenue--;
                }

                // Cuando lleguemos a la intersección, verificamos el semáforo
                if (street == interseccionStreet && avenue == interseccionAvenue) {
                    while (!semaforo.puedeCruzar()) {
                        System.out.println("Robot espera en la interseccion (Calle: " + street + ", Avenida: " + avenue + ").");
                        Thread.sleep(1000); // Espera mientras el semáforo esté en rojo
                    }
                    System.out.println("Robot cruza la interseccion (Calle: " + street + ", Avenida: " + avenue + ").");
                    move(); // Cruza la intersección cuando el semáforo está en verde
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // Método para cambiar de dirección (al girar)
    public void turnLeft() {
        super.turnLeft();
        // Actualizar la dirección al girar a la izquierda
        if (direccion == East) {
            direccion = North;
        } else if (direccion == North) {
            direccion = West;
        } else if (direccion == West) {
            direccion = South;
        } else if (direccion == South) {
            direccion = East;
        }
    }
}

