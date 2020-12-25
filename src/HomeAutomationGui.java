import jade.core.AID;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URL;

public class HomeAutomationGui extends JFrame{
    private JFrame frame;
    private JPanel mainPannel;
    private JLabel titleLabel;
    private JPanel userPannel;
    private JLabel userImageLabel;
    private JRadioButton lockDoorRadioButton;
    private JRadioButton unlockDoorRadioButton;
    private JButton getDoorStateButton;
    private JCheckBox brokeDoorCheckBox;
    private JTextArea outputStream;
    private JLabel doorIcon;
    private JCheckBox brokeFridgeCheckBox;
    private JButton getTemperatureButton;
    private JButton setTemperatureButton;
    private JCheckBox brokeWindowCheckBox;
    private JCheckBox brokeShutterCheckBox;
    private JButton openShutterButton;
    private JButton closeShutterButton;
    private JButton unlockDoorButton;
    private JButton lockDoorButton;
    private JButton startHeatButton;
    private JButton stopHeatButton;
    private JButton deactivateSunfilterButton;
    private JButton activateSunfilterButton;
    private ButtonGroup doorGroup;

    Runtime runtime;
    Profile configuration;
    ContainerController mainContainer;
    AgentController controller;
    AgentController mainDoor;
    AgentController heat;
    AgentController fridge;
    AgentController room;
    AgentController shutter;
    AgentController window;
    Controller controllerAgent;
    MainDoor mainDoorAgent;

    private void createRadioGroup(){
        doorGroup = new ButtonGroup();
        doorGroup.add(lockDoorRadioButton);
        doorGroup.add(unlockDoorRadioButton);
    }

    private void handleDoor(){
        lockDoorButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    controllerAgent.changeDoorStatus(new AID(mainDoor.getName()), HomeAutomation.DoorStates.LOCKED);
                    doorIcon.setIcon(new ImageIcon(getClass().getResource("./locked_door.png")));
                } catch (StaleProxyException staleProxyException) {
                    staleProxyException.printStackTrace();
                }
            }
        });

        unlockDoorButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    controllerAgent.changeDoorStatus(new AID(mainDoor.getName()), HomeAutomation.DoorStates.UNLOCKED);
                    doorIcon.setIcon(new ImageIcon(getClass().getResource("./unlocked_door.png")));
                } catch (StaleProxyException staleProxyException) {
                    staleProxyException.printStackTrace();
                }
            }
        });

        brokeDoorCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JCheckBox checkBox = (JCheckBox) e.getSource();
                if (checkBox.isSelected()){
                    mainDoorAgent.changeDoorState(HomeAutomation.DoorStates.BROKEN);
                    doorIcon.setIcon(new ImageIcon(getClass().getResource("./broken_door.png")));
                } else {
                    mainDoorAgent.changeDoorState(HomeAutomation.DoorStates.NOT_BROKEN);
                    doorIcon.setIcon(new ImageIcon(getClass().getResource("./locked_door.png")));
                }
            }
        });
        getDoorStateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controllerAgent.getDoorStatus();
            }
        });
    }

    public static void main(String[] args){ ;
        HomeAutomationGui homeAutomationGui = new HomeAutomationGui();
        homeAutomationGui.createRadioGroup();
        homeAutomationGui.handleDoor();

        homeAutomationGui.frame = new JFrame("HomeAutomation");
        homeAutomationGui.frame.setContentPane(homeAutomationGui.mainPannel);
        homeAutomationGui.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        homeAutomationGui.frame.setLocationRelativeTo(null);
        homeAutomationGui.frame.pack();
        homeAutomationGui.frame.setVisible(true);

        PrintStream printStream = new PrintStream(new CustomOutputStream(homeAutomationGui.outputStream));
        System.setOut(printStream);
        System.setErr(printStream);

        homeAutomationGui.runtime = Runtime.instance();
        homeAutomationGui.configuration = new ProfileImpl("localhost", 8888, null);
        homeAutomationGui.configuration.setParameter("gui", "true");
        homeAutomationGui.mainContainer =  homeAutomationGui.runtime.createMainContainer(homeAutomationGui.configuration);

        try {
            homeAutomationGui.controller = homeAutomationGui.mainContainer.createNewAgent("ControllerAgent", ControllerAgent.class.getName(), null);
            homeAutomationGui.mainDoor = homeAutomationGui.mainContainer.createNewAgent("MainDoorAgent", MainDoorAgent.class.getName(), null);
            homeAutomationGui.heat = homeAutomationGui.mainContainer.createNewAgent("HeatAgent", HeatAgent.class.getName(), null);
            homeAutomationGui.fridge = homeAutomationGui.mainContainer.createNewAgent("FridgeAgent", FridgeAgent.class.getName(), null);
            homeAutomationGui.room = homeAutomationGui.mainContainer.createNewAgent("RoomAgent", RoomAgent.class.getName(), null);
            homeAutomationGui.shutter = homeAutomationGui.mainContainer.createNewAgent("ShutterAgent", ShutterAgent.class.getName(), null);
            homeAutomationGui.window = homeAutomationGui.mainContainer.createNewAgent("WindowAgent", WindowAgent.class.getName(), null);


            homeAutomationGui.controller.start();
            homeAutomationGui.mainDoor.start();
            homeAutomationGui.heat.start();
            homeAutomationGui.fridge.start();
            homeAutomationGui.room.start();
            homeAutomationGui.shutter.start();
            homeAutomationGui.window.start();


//            Scanner scanner = new Scanner(System.in);
//            while (true) {
//                System.out.println(
//                        "Choose one of the options:\n" +
//                                "1. Change door state\n" +
//                                "2. Lock door\n" +
//                                "3. Unlock door\n"+
//                                "4. Fingerprint valid\n"+
//                                "5. Fingerprint invalid\n"+
//                                "6. Get door status"
//                );
//
//                int option = Integer.parseInt(scanner.next());
//
                homeAutomationGui.controllerAgent = homeAutomationGui.controller.getO2AInterface(Controller.class);
                homeAutomationGui.mainDoorAgent = homeAutomationGui.mainDoor.getO2AInterface(MainDoor.class);
//
//                switch (option) {
//                    case 1:
//                        Util.log("Pick one of the options: \n"+
//                                "0. Locked\n"+
//                                "1. Unlocked\n"+
//                                "2. Broken");
//                        int doorOption = Integer.parseInt(scanner.next());
//                        controllerAgent.changeDoorStatus(new AID(mainDoor.getName()), HomeAutomation.DoorStates.values()[doorOption]);
//                        break;
//                    case 2:
//                        controllerAgent.getDoorStatus();
//                        break;
//                    case 3:
//                        mainDoorAgent.changeDoorState(HomeAutomation.DoorStates.UNLOCKED);
//                        break;
//                    case 4:
//                        break;
//                    case 5:
//                        break;
//                    case 6:
//                        controllerAgent.getDoorStatus();
//                        break;
//                    default:
//                        System.out.println("Command not recognized");
//                }
//            }

        } catch (StaleProxyException e) {
            e.printStackTrace();
        }
    }

    /**
     * This class extends from OutputStream to redirect output to a JTextArrea
     * @author www.codejava.net
     *
     */
    private static class CustomOutputStream extends OutputStream {
        private JTextArea textArea;

        public CustomOutputStream(JTextArea textArea) {
            this.textArea = textArea;
        }

        @Override
        public void write(int b) throws IOException {
            // redirects data to the text area
            textArea.append(String.valueOf((char)b));
            // scrolls the text area to the end of data
            textArea.setCaretPosition(textArea.getDocument().getLength());
        }
    }
}
