package prueba;

import kareltherobot.*;

public class MiPrimerRobot implements Directions {
    public static void main(String[] args) {
        // Usamos el archivo que creamos del mundo
        World.readWorld("Mundo.kwld");
        World.setVisible(true);

        // Coloca el primer robot (Karel) en la posición inicial del mundo (1,1),
        // mirando al Este, sin ninguna sirena.
        Racer first = new Racer(1, 1, East, 0);
        Racer second = new Racer(1, 1, East, 0);


        // Mover el robot 4 pasos
        first.move();
        second.move();
        first.move();
        second.move();
        first.move();
        second.move();
        first.move();
        second.move();
        

        // Recoger los 5 beepers
        first.pickBeeper();
        first.pickBeeper();
        first.pickBeeper();
        first.pickBeeper();
        first.pickBeeper();
        second.pickBeeper();
        second.pickBeeper();
        second.pickBeeper();
        second.pickBeeper();
        second.pickBeeper();

        // Girar a la izquierda y salir de los muros
        first.turnLeft();
        second.turnLeft();
        first.move();
        second.move();
        first.move();
        second.move();

        // Poner los beepers fuera de los muros
        first.putBeeper();
        second.putBeeper();
        first.putBeeper();
        second.putBeeper();
        first.putBeeper();
        second.putBeeper();
        first.putBeeper();
        second.putBeeper();
        first.putBeeper();
        second.putBeeper();

        // Ponerse en otra posición y apagar el robot
        first.move();
        second.move();
        first.turnOff();
        second.turnLeft();
        second.move();
        for(int i = 0; i < 3; i++){
            second.turnLeft();
        }
        second.turnOff();
    }
}


class Racer extends Robot
{
    public Racer(int Street, int Avenue, Direction direction, int beeps)
    {
        super(Street, Avenue, direction, beeps);
        World.setupThread(this);
    }
    public void race()
    {
        while(! nextToABeeper())
            move();
        pickBeeper();
        turnOff();
    }
    public void run()
    {
        race();
    }
}