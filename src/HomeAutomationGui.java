import agents.*;
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

import interfaces.*;

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
    private JButton getListButton;
    private JLabel fridgeIcon;
    private JButton sendBuyListButton;
    private JLabel windowIcon;
    private JLabel shutterIcon;
    private ButtonGroup doorGroup;

    Runtime runtime;
    Profile configuration;
    ContainerController mainContainer;
    AgentController controller;
    AgentController mainDoor;
    AgentController heat;
    AgentController fridge;
    AgentController shutter;
    AgentController window;
    Controller controllerAgent;
    MainDoor mainDoorAgent;
    Fridge fridgeAgent;
    Window windowAgent;
    Shutter shutterAgent;

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
                    controllerAgent.changeDoorState(new AID(mainDoor.getName()), HomeAutomation.DoorStates.LOCKED);
                    doorIcon.setIcon(new ImageIcon(getClass().getResource("img/locked_door.png")));
                } catch (StaleProxyException staleProxyException) {
                    staleProxyException.printStackTrace();
                }
            }
        });

        unlockDoorButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    controllerAgent.changeDoorState(new AID(mainDoor.getName()), HomeAutomation.DoorStates.UNLOCKED);
                    doorIcon.setIcon(new ImageIcon(getClass().getResource("img/unlocked_door.png")));
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
                    doorIcon.setIcon(new ImageIcon(getClass().getResource("img/broken_door.png")));
                } else {
                    mainDoorAgent.changeDoorState(HomeAutomation.DoorStates.NOT_BROKEN);
                    doorIcon.setIcon(new ImageIcon(getClass().getResource("img/locked_door.png")));
                }
            }
        });

        getDoorStateButton.addActionListener(e -> {
            try {
                AID agent = new AID(mainDoor.getName());
                controllerAgent.getDoorState(agent);
            } catch (StaleProxyException staleProxyException) {
                staleProxyException.printStackTrace();
            }
        });
    }

    private void handleFridge(){
        getListButton.addActionListener(e -> {
            try {
                controllerAgent.getFridgeList(new AID(fridge.getName()));
            } catch (StaleProxyException staleProxyException) {
                staleProxyException.printStackTrace();
            }
        });
        brokeFridgeCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JCheckBox checkBox = (JCheckBox) e.getSource();
                if (checkBox.isSelected()){
                    fridgeAgent.changeFridgeState(HomeAutomation.FridgeStates.BROKEN);
                    fridgeIcon.setIcon(new ImageIcon(getClass().getResource("img/broken_fridge.png")));
                } else{
                    fridgeAgent.changeFridgeState(HomeAutomation.FridgeStates.RUNNING);
                    fridgeIcon.setIcon(new ImageIcon(getClass().getResource("img/ok_fridge.png")));
                }
            }
        });

        sendBuyListButton.addActionListener(e -> fridgeAgent.buyList());
    }

    private void handleHeat(){
        startHeatButton.addActionListener(e -> {
            try {
                controllerAgent.startHeat(new AID(heat.getName()));
            } catch (StaleProxyException staleProxyException) {
                staleProxyException.printStackTrace();
            }
        });

        stopHeatButton.addActionListener(e -> {
            try {
                controllerAgent.stopHeat(new AID(heat.getName()));
            } catch (StaleProxyException staleProxyException) {
                staleProxyException.printStackTrace();
            }
        });

        setTemperatureButton.addActionListener(e -> {
            try {
                controllerAgent.setHeatTemperature(new AID(heat.getName()), 28);
            } catch (StaleProxyException staleProxyException) {
                staleProxyException.printStackTrace();
            }
        });

        getTemperatureButton.addActionListener(e -> {
            try {
                controllerAgent.getHeatTemperature(new AID(heat.getName()));
            } catch (StaleProxyException staleProxyException) {
                staleProxyException.printStackTrace();
            }

        });
    }

    public void handleWindow() {
        activateSunfilterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    controllerAgent.startSunFilter(new AID(window.getName()));
                    windowIcon.setIcon(new ImageIcon(getClass().getResource("img/open_window.png")));
                } catch (StaleProxyException staleProxyException) {
                    staleProxyException.printStackTrace();
                }
            }
        });

        deactivateSunfilterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    controllerAgent.stopSunFilter(new AID(window.getName()));
                    windowIcon.setIcon(new ImageIcon(getClass().getResource("img/close_window.png")));
                } catch (StaleProxyException staleProxyException) {
                    staleProxyException.printStackTrace();
                }
            }
        });

        brokeWindowCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JCheckBox checkBox = (JCheckBox) e.getSource();
                if (checkBox.isSelected()){
                    windowAgent.changeWindowStatus(HomeAutomation.WindowStates.BROKEN);
                    windowIcon.setIcon(new ImageIcon(getClass().getResource("img/broken_window.png")));
                } else{
                    windowAgent.changeWindowStatus(HomeAutomation.WindowStates.CLOSED);
                    windowIcon.setIcon(new ImageIcon(getClass().getResource("img/close_window.png")));
                }
            }
        });
    }

    public void handleShutter(){
        openShutterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    controllerAgent.openShutter(new AID(shutter.getName()));
                } catch (StaleProxyException staleProxyException) {
                    staleProxyException.printStackTrace();
                }
            }
        });

        closeShutterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    controllerAgent.closeShutter(new AID(shutter.getName()));
                } catch (StaleProxyException staleProxyException) {
                    staleProxyException.printStackTrace();
                }
            }
        });

        brokeShutterCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JCheckBox checkBox = (JCheckBox) e.getSource();
                if (checkBox.isSelected()){
                    shutterAgent.changeShutterStatus(HomeAutomation.ShutterStates.BROKEN);
                    shutterIcon.setIcon(new ImageIcon(getClass().getResource("img/broken_shutter.png")));
                } else{
                    shutterAgent.changeShutterStatus(HomeAutomation.ShutterStates.UP);
                    shutterIcon.setIcon(new ImageIcon(getClass().getResource("img/closed_shutter.png")));
                }
            }
        });
    }
    public static void main(String[] args){ ;
        HomeAutomationGui homeAutomationGui = new HomeAutomationGui();
        homeAutomationGui.createRadioGroup();
        homeAutomationGui.handleDoor();
        homeAutomationGui.handleFridge();
        homeAutomationGui.handleHeat();
        homeAutomationGui.handleWindow();
        homeAutomationGui.handleShutter();

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
            homeAutomationGui.shutter = homeAutomationGui.mainContainer.createNewAgent("ShutterAgent", ShutterAgent.class.getName(), null);
            homeAutomationGui.window = homeAutomationGui.mainContainer.createNewAgent("WindowAgent", WindowAgent.class.getName(), null);


            homeAutomationGui.controller.start();
            homeAutomationGui.mainDoor.start();
            homeAutomationGui.heat.start();
            homeAutomationGui.fridge.start();
            homeAutomationGui.shutter.start();
            homeAutomationGui.window.start();

            homeAutomationGui.controllerAgent = homeAutomationGui.controller.getO2AInterface(Controller.class);
            homeAutomationGui.mainDoorAgent = homeAutomationGui.mainDoor.getO2AInterface(MainDoor.class);
            homeAutomationGui.fridgeAgent = homeAutomationGui.fridge.getO2AInterface(Fridge.class);
            homeAutomationGui.windowAgent = homeAutomationGui.window.getO2AInterface(Window.class);
            homeAutomationGui.shutterAgent = homeAutomationGui.shutter.getO2AInterface(Shutter.class);
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
