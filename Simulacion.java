import kareltherobot.*;

import javax.xml.crypto.dsig.spec.HMACParameterSpec;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.List;

public class Simulacion implements Directions {
    private static final Lock salidaLock = new ReentrantLock();  // Lock para la salida de los robots
    private static final Lock movimientoLock = new ReentrantLock();  // Lock para controlar el movimiento de los robots


    public static void main(String[] args) {

        HashMap<String, List<Posicion>> mapaDeParadas = new HashMap<>();
//      PARADA 1 POSICIONES
        List<Posicion> parada1 = new ArrayList<>();
        parada1.add(new Posicion(17, 7));
        parada1.add(new Posicion(17, 8));
        parada1.add(new Posicion(16, 8));
        parada1.add(new Posicion(15, 8));
        parada1.add(new Posicion(15, 7));
        parada1.add(new Posicion(15, 6));
        parada1.add(new Posicion(15, 5));
        parada1.add(new Posicion(15, 4));
        parada1.add(new Posicion(15, 3));
        parada1.add(new Posicion(16, 3));
        parada1.add(new Posicion(17, 3));
        parada1.add(new Posicion(17, 4));
        parada1.add(new Posicion(17, 5));
        parada1.add(new Posicion(18, 5));

//      PARADA 2 POSICIONES
        List<Posicion> parada2 = new ArrayList<>();
        parada2.add(new Posicion(12, 6));
        parada2.add(new Posicion(12, 5));
        parada2.add(new Posicion(12, 4));
        parada2.add(new Posicion(13, 4));
        parada2.add(new Posicion(13, 5));
        parada2.add(new Posicion(13, 6));
        parada2.add(new Posicion(13, 7));
        parada2.add(new Posicion(13, 8));
        parada2.add(new Posicion(13, 9));
        parada2.add(new Posicion(12, 9));
        parada2.add(new Posicion(11, 9));
        parada2.add(new Posicion(11, 8));
        parada2.add(new Posicion(12, 8));

        mapaDeParadas.put("Parada 1", parada1);
        mapaDeParadas.put("Parada 2", parada2);




        // Configuración inicial del mundo
        World.readWorld("PracticaOperativos.kwld");
        World.setVisible(true);
        World.showSpeedControl(true, true); //Needed to make them start

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
                moverRobotFueraDelParqueadero(robot);
                robot.setFueraDelParqueadero(true);
                while (hayBeepersEnPosicion(robot, 8, 19)) {
                    moverRobotACalle8(robot);
                    asignarRutaAleatoria(robot, mapaDeParadas);
                    Regreso(robot);
                }
            }).start();
        }
    }

    public static void recorrerParada(RobotOp robot, String parada, HashMap<String, List<Posicion>> mapaDeParadas) {
        List<Posicion> posiciones = mapaDeParadas.get(parada);  // Obtener las posiciones de la parada

        if (posiciones != null) {
            for (Posicion posicion : posiciones) {
                moverRobotAPosicion(robot, posicion.getStreet(), posicion.getAvenue());
                System.out.println("Robot llegó a la posición: " + posicion);
            }
        } else {
            System.out.println("Parada no encontrada: " + parada);
        }
    }

    public static boolean hayBeepersEnPosicion(RobotOp robot, int street, int avenue) {
        moverRobotAPosicion(robot, street, avenue);
        return robot.nextToABeeper();
    }

    public static void moverRobotAAvenida17(RobotOp robot) {
        // Mover el robot hacia la avenida 17
        while (robot.getAvenue() < 17) {
            avanzarSiPosicionLibre(robot);
        }
    }

    public static void Regreso(RobotOp robot) {
        if (robot.getStreet() == 17 || robot.getAvenue() == 6) {
            retorno1(robot);
            Camino3(robot);
        } else if (robot.getStreet() == 11 || robot.getAvenue() == 8) {
            retorno2(robot);
            Camino2(robot);
            Camino3(robot);
        } else if (robot.getStreet() == 7 || robot.getAvenue() == 9) {
            retorno3(robot);
        } else if (robot.getStreet() == 19 || robot.getAvenue() == 18) {
            retorno4(robot);
            Camino3(robot);
        }
    }

    public static void retorno1(RobotOp robot) {
        moverRobotAPosicion(robot, 15, 6);
        robot.turnLeft();
        moverRobotAPosicion(robot, 18, 6);
        robot.turnRight();
    }

    public static void retorno2(RobotOp robot) {
        moverRobotAPosicion(robot, 13, 7);
        robot.turnLeft();
        moverRobotAPosicion(robot, 10, 7);
        robot.turnRight();
    }

    public static void retorno3(RobotOp robot) {
        moverRobotAPosicion(robot, 8, 8);
        robot.turnLeft();
    }

    public static void retorno4(RobotOp robot) {
        if (robot.facingWest()) {
            robot.turnLeft();
            robot.turnLeft();
        }
        moverRobotAPosicion(robot, 11, 15);
        robot.turnRight();
        avanzarSiPosicionLibre(robot);
        robot.turnRight();
    }


    public static void asignarRutaAleatoria(RobotOp robot, HashMap<String, List<Posicion>> mapaDeParadas) {
        Random random = new Random();
        int rutaSeleccionada = random.nextInt(4) + 1;  // Generar un número entre 1 y 4
        switch (rutaSeleccionada) {
            case 1:
                RutaAParada1(robot, mapaDeParadas);
                break;
            case 2:
                RutaAParada2(robot, mapaDeParadas);
                break;
            case 3:
                RutaAParada3(robot);
                break;
            case 4:
                RutaAParada4(robot);
                break;
        }
    }

    public static void Camino1(RobotOp robot) {
        moverRobotAPosicion(robot, 9, 6);
        robot.turnRight();
    }

    public static void Camino2(RobotOp robot) {
        moverRobotAPosicion(robot, 9, 10);
        robot.turnRight();
    }

    public static void Camino3(RobotOp robot) {
        moverRobotAPosicion(robot, 10, 10);
        robot.turnRight();
    }

    public static void RutaAParada1(RobotOp robot, HashMap<String, List<Posicion>> mapaDeParadas) {
        // Usar el método que recorre la parada
        moverRobotAPosicion(robot, 18, 6);  // Moverse a la posición inicial
        robot.turnRight();
        robot.move();
        robot.move();
        robot.turnLeft();
        robot.move();
        robot.turnLeft();
        recorrerParada(robot, "Parada 1", mapaDeParadas);  // Pasar directamente "Parada 1"
    }

    public static void RutaAParada2(RobotOp robot, HashMap<String, List<Posicion>> mapaDeParadas) {
        moverRobotAPosicion(robot, 10, 7);
        robot.turnRight();
        robot.move();
        robot.move();
        robot.turnLeft();
        recorrerParada(robot, "Parada 2", mapaDeParadas);
    }

    public static void RutaAParada3(RobotOp robot) {
        Camino1(robot);
        moverRobotAPosicion(robot, 6, 8);
        robot.turnLeft();
        robot.move();
        robot.move();
        robot.turnRight();
        robot.move();
        robot.turnRight();
        robot.move();
        robot.putBeeper();
    }

    public static void RutaAParada4(RobotOp robot) {
        moverRobotAPosicion(robot, 10, 15);
        robot.turnRight();
        robot.move();
        robot.turnLeft();
        moverRobotAPosicion(robot, 15, 15);
        robot.turnRight();
        moverRobotAPosicion(robot, 16, 12);
        robot.turnRight();
        moverRobotAPosicion(robot, 19, 18);
        robot.putBeeper();
    }

    public static void moverRobotAPosicion(RobotOp robot, int targetStreet, int targetAvenue) {
        while (robot.getStreet() != targetStreet || robot.getAvenue() != targetAvenue) {
            avanzarSiPosicionLibre(robot);
        }

        // Al llegar a la posición (18, 6), el robot se detendrá
        System.out.println("Robot llegó a la posición (" + targetStreet + ", " + targetAvenue + ")");
    }

    public static void moverRobotACalle8(RobotOp robot) {
        moverRobotAPosicion(robot, 8, 19);
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
        }
        // Si el robot está en una calle inferior a la 3, moverse hacia el sur
        else if (robot.getStreet() < 3) {
            robot.turnLeft();
            avanzarSiPosicionLibre(robot);  // Moverse solo si la posición está libre
            robot.turnRight();
            avanzarSiPosicionLibre(robot);
        }
    }

    // Método para mover el robot hacia la puerta en (3, 18)
    public static void moverRobotFueraDelParqueadero(RobotOp robot) {
        try {
            salidaLock.lock();
            if(robot.getPosition().equals("(4, 18)") || robot.getPosition().equals("(5, 18)")){
                robot.move();
                robot.turnLeft();
                moverRobotAAvenida17(robot);
                robot.turnLeft();
                moverRobotACalle3(robot);
            }
            moverRobotAAvenida17(robot);
            moverRobotACalle3(robot);
        } finally {
            salidaLock.unlock();
        }
    }


    // Método para avanzar solo si la posición frente al robot está libre
    public static void avanzarSiPosicionLibre(RobotOp robot) {
        // Verificar si el robot puede avanzar
        if (robot.frontIsClear()) {
            robot.move();  // Avanzar hacia la siguiente posición si está libre
        } else {
            robot.turnLeft();
            if (!robot.frontIsClear()) {
                for (int i = 0; i < 2; i++) {
                    robot.turnLeft();
                }
            }
        }
    }
}

// Clase que extiende a Robot y nos permite obtener la calle y avenida del robot
class RobotOp extends Robot {

    private static List<RobotOp> allRobots = new ArrayList<>();
    private boolean fueraDelParqueadero = false;

    public RobotOp(int street, int avenue, Direction direction, int beeps) {
        super(street, avenue, direction, beeps);
        allRobots.add(this);
    }


    public void setFueraDelParqueadero(boolean value) {
        this.fueraDelParqueadero = value;
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

    public String getPosition() {
        return "(" + getStreet() + ", " + getAvenue() + ")";
    }


    // Método adicional para girar a la derecha
    public void turnRight() {
        turnLeft();
        turnLeft();
        turnLeft();
    }
}

class Posicion {
    int street;
    int avenue;

    public Posicion(int street, int avenue) {
        this.street = street;
        this.avenue = avenue;
    }

    public int getStreet() {
        return street;
    }

    public int getAvenue() {
        return avenue;
    }

    @Override
    public String toString() {
        return "(" + street + ", " + avenue + ")";
    }
}