package display;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class ModifiersFrame extends JDialog {

    public ModifiersFrame(JFrame parentFrame) {
        // Create a modal dialog attached to the parent frame
        super(parentFrame, "Modifiers settings", true);

        setIconImage(GameFrame.LOGO.getImage());
        setSize(500, 425);
        setLayout(null);
        setResizable(false);

        // Set the default close operation to dispose
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Add a WindowListener to detect when the frame is closed
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // Call your methods here when the frame is about to close
                Main.gameMenu.setGameMode();
                Main.gameMenu.setTopScores();
                Main.gameMenu.setPlayerStats();
            }
        });

        // Width modifier
        JButton width = new JButton();
        setMultipleOptionModifiers(width, "Width", 10, 10, 0);
        add(width);

        // Height modifier
        JButton height = new JButton();
        setMultipleOptionModifiers(height, "Height", 10, 80, 1);
        add(height);

        // More blocks modifier
        JButton moreBlocks = new JButton();
        setMultipleOptionModifiers(moreBlocks, "Blocks", 10, 150, 2);
        add(moreBlocks);

        // [(2 levels) wide, (2 levels) tall, (2 levels) more blocks, sticky(stick to the same color),
        // right/left bounds teleport, shrink power, flip power,
        // small blocks randomly going up, break a block power, falling blocks when destroyed level]

        // Sticky modifier
        JCheckBox sticky = new JCheckBox("Sticky mode");
        setOneOptionModifiers(sticky, 10, 220, 3);
        add(sticky);

        // Teleport modifier
        JCheckBox teleport = new JCheckBox("Teleport mode");
        setOneOptionModifiers(teleport, 10, 290, 4);
        add(teleport);

        // Reset button
        JButton reset = new JButton("Reset");
        reset.addActionListener(e -> {
            // Reset modifiers array
            Arrays.fill(Main.gameMenu.modifiers, 0);

            // Reset buttons
            width.setText("Default Width");
            height.setText("Default Height");
            moreBlocks.setText("Default Blocks");

            // Reset checkboxes
            sticky.setSelected(false);
            teleport.setSelected(false);
        });
        reset.setBounds(150, 360, 200, 30);
        add(reset);

        // Set position and make window visible
        setLocationRelativeTo(parentFrame);
        setVisible(true);
    }

    private void setOneOptionModifiers(JCheckBox checkBox, int x, int y, int modNr) {
        // Check if the box should be already selected
        if (Main.gameMenu.modifiers[modNr] == 1) {
            checkBox.setSelected(true);
        }

        // Add action listener to turn on/off the modifier
        checkBox.addChangeListener(e -> {
            if (checkBox.isSelected()) {
                Main.gameMenu.modifiers[modNr] = 1;
            } else {
                Main.gameMenu.modifiers[modNr] = 0;
            }
        });

        // Set other attributes
        checkBox.setBounds(x, y, 220, 60);
        checkBox.setFocusable(false);
        checkBox.setFont(new Font(null, Font.PLAIN, 25));
        checkBox.setBorder(BorderFactory.createLineBorder(Color.black));
        checkBox.setBorderPainted(true);
    }

    private void setMultipleOptionModifiers(JButton button, String name, int x, int y, int modNr) {
        switch(Main.gameMenu.modifiers[modNr]) {
            case 0: button.setText("Default " + name); break;
            case 1: button.setText("1,5x " + name); break;
            case 2: button.setText("2x " + name);
        }
        button.addActionListener(e -> {
            switch(Main.gameMenu.modifiers[modNr]) {
                case 0:
                    button.setText("1,5x " + name);
                    Main.gameMenu.modifiers[modNr] = 1;
                    break;
                case 1:
                    button.setText("2x " + name);
                    Main.gameMenu.modifiers[modNr] = 2;
                    break;
                case 2:
                    button.setText("Default " + name);
                    Main.gameMenu.modifiers[modNr] = 0;
            }
        });
        button.setBounds(x, y, 220, 60);
        button.setFocusable(false);
    }
}
