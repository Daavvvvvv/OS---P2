import kareltherobot.*;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Simulacion implements Directions {
    private static final Lock salidaLock = new ReentrantLock();  // Lock para la salida de los robots
    private static final Lock movimientoLock = new ReentrantLock();  // Lock para controlar el movimiento de los robots

    public static void main(String[] args) {
        // Configuración inicial del mundo
        World.readWorld("PracticaOperativos.kwld");
        World.setVisible(true);

        // Crear 8 robots en el parqueadero en diferentes posiciones
        RobotOp[] robots = new RobotOp[8];  // Definimos un array de tamaño 8 para los 8 robots

        // Primeros 6 robots en las posiciones desde (2, 12) hasta (7, 12)
        for (int i = 0; i < 6; i++) {
            robots[i] = new RobotOp(i + 2, 12, East, 0);  // Posiciones en el parqueadero mirando al este
        }

        // Los dos robots adicionales en las posiciones (4, 18) y (5, 18) mirando hacia el sur
        robots[6] = new RobotOp(4, 18, West, 0);  // Robot en (4, 18) mirando hacia el sur
        robots[7] = new RobotOp(5, 18, West, 0);  // Robot en (5, 18) mirando hacia el sur

        // Simular la salida uno por uno
        for (RobotOp robot : robots) {
            new Thread(() -> {
                try {
                    // Controlar la salida con un lock para que solo un robot salga a la vez
                    salidaLock.lock();

                    // Finalmente, moverse hacia la puerta en (3, 18)
                    moverRobotAPuerta(robot);

                } finally {
                    // Liberar el lock después de que el robot haya salido
                    salidaLock.unlock();
                }

            }).start();
        }
    }

    // Método para mover el robot hacia la avenida 17
    public static void moverRobotAAvenida17(RobotOp robot) {
        // Mover el robot hacia la avenida 17
        while (robot.getAvenue() < 17) {
            avanzarSiPosicionLibre(robot);
        }
    }

    public static void EvitarParedes(RobotOp robot) {
        while (robot.frontIsClear()) {
            avanzarSiPosicionLibre(robot);  // Avanzar solo si está libre la posición
            if (!robot.frontIsClear()) {
                robot.turnLeft();
                break;
            }
        }
    }

    public static void moverRobotACalle8(RobotOp robot) {
        while (robot.getStreet() < 8) {
            avanzarSiPosicionLibre(robot);  // Moverse solo si la posición está libre
        }
        robot.pickBeeper();
    }

    // Método para mover el robot hacia la calle 3 desde la avenida 17
    public static void moverRobotACalle3(RobotOp robot) {
        // Si el robot está en una calle superior a la 3, moverse hacia el norte
        if (robot.getStreet() > 3) {
            if (robot.facingWest()) {
                robot.turnLeft();
            } else {
                robot.turnRight();
            }
            while (robot.getStreet() > 3) {
                avanzarSiPosicionLibre(robot);
            }
            robot.turnLeft();
            EvitarParedes(robot);
        }
        // Si el robot está en una calle inferior a la 3, moverse hacia el sur
        else if (robot.getStreet() < 3) {
            robot.turnLeft();
            avanzarSiPosicionLibre(robot);  // Moverse solo si la posición está libre
            robot.turnRight();
            EvitarParedes(robot);
        }
        avanzarSiPosicionLibre(robot);  // Moverse solo si la posición está libre
    }

    public static void moverRobotAAvenida11(RobotOp robot) {
        robot.turnLeft();
        while (robot.getAvenue() < 11) {
            avanzarSiPosicionLibre(robot);
        }
    }

    // Método para mover el robot hacia la puerta en (3, 18)
    public static void moverRobotAPuerta(RobotOp robot) {
        moverRobotAAvenida17(robot);
        moverRobotACalle3(robot);
        moverRobotACalle8(robot);
        moverRobotAAvenida11(robot);
    }

    // Método para avanzar solo si la posición frente al robot está libre
    public static void avanzarSiPosicionLibre(RobotOp robot) {
        movimientoLock.lock();  // Bloquear el movimiento para que ningún otro robot avance

        try {
            // Verificar si el robot puede avanzar
            if (robot.frontIsClear()) {
                robot.move();  // Avanzar hacia la siguiente posición si está libre
            } else {
                robot.turnLeft();
                try {
                    Thread.sleep(500);  // Esperar antes de intentar moverse nuevamente
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } finally {
            movimientoLock.unlock();  // Liberar el lock para que otros robots puedan avanzar
        }
    }
}

// Clase que extiende a Robot y nos permite obtener la calle y avenida del robot
class RobotOp extends Robot {
    public RobotOp(int street, int avenue, Direction direction, int beeps) {
        super(street, avenue, direction, beeps);
    }

    public int getStreet() {
        String robotInfo = this.toString();
        int streetIndex = robotInfo.indexOf("street: ");
        int streetEndIndex = robotInfo.indexOf(")", streetIndex);
        return Integer.parseInt(robotInfo.substring(streetIndex + 8, streetEndIndex));
    }

    public int getAvenue() {
        String robotInfo = this.toString();
        int avenueIndex = robotInfo.indexOf("avenue: ");
        int avenueEndIndex = robotInfo.indexOf(")", avenueIndex);
        return Integer.parseInt(robotInfo.substring(avenueIndex + 8, avenueEndIndex));
    }

    // Método adicional para girar a la derecha
    public void turnRight() {
        turnLeft();
        turnLeft();
        turnLeft();
    }
}
