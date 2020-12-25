import jade.core.*;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import sun.applet.Main;

import java.util.Scanner;


public class Launcher implements HomeAutomation{
    public static void main(String[] args){
        Runtime runtime = Runtime.instance();
        Profile configuration = new ProfileImpl("localhost", 8888, null);
        configuration.setParameter("gui", "true");
        ContainerController mainContainer =  runtime.createMainContainer(configuration);
        AgentController controller;
        AgentController mainDoor;
        AgentController heat;
        AgentController fridge;
        AgentController room;
        AgentController shutter;
        AgentController window;

        try {
            controller = mainContainer.createNewAgent("ControllerAgent", ControllerAgent.class.getName(), null);
            mainDoor = mainContainer.createNewAgent("MainDoorAgent", MainDoorAgent.class.getName(), null);
            heat = mainContainer.createNewAgent("HeatAgent", HeatAgent.class.getName(), null);
            fridge = mainContainer.createNewAgent("FridgeAgent", FridgeAgent.class.getName(), null);
            room = mainContainer.createNewAgent("RoomAgent", RoomAgent.class.getName(), null);
            shutter = mainContainer.createNewAgent("ShutterAgent", ShutterAgent.class.getName(), null);
            window = mainContainer.createNewAgent("WindowAgent", WindowAgent.class.getName(), null);

            controller.start();
            mainDoor.start();
            heat.start();
            fridge.start();
            room.start();
            shutter.start();
            window.start();

            Scanner scanner = new Scanner(System.in);
            while (true) {
                System.out.println(
                        "Choose one of the options:\n" +
                                "1. Change door state\n" +
                                "2. Lock door\n" +
                                "3. Unlock door\n"+
                                "4. Fingerprint valid\n"+
                                "5. Fingerprint invalid\n"+
                                "6. Get door status"
                );

                int option = Integer.parseInt(scanner.next());

                Controller controllerAgent = controller.getO2AInterface(Controller.class);
                MainDoor mainDoorAgent = mainDoor.getO2AInterface(MainDoor.class);

                switch (option) {
                    case 1:
                        Util.log("Pick one of the options: \n"+
                                "0. Locked\n"+
                                "1. Unlocked\n"+
                                "2. Broken");
                        int doorOption = Integer.parseInt(scanner.next());
                        controllerAgent.changeDoorStatus(new AID(mainDoor.getName()), DoorStates.values()[doorOption]);
                        break;
                    case 2:
                        controllerAgent.getDoorStatus();
                        break;
                    case 3:
                        mainDoorAgent.changeDoorState(DoorStates.UNLOCKED);
                        break;
                    case 4:
                        break;
                    case 5:
                        break;
                    case 6:
                        controllerAgent.getDoorStatus();
                        break;
                    default:
                        System.out.println("Command not recognized");
                }
            }

        } catch (StaleProxyException e) {
            e.printStackTrace();
        }
    }
}
