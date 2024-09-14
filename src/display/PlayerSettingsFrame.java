package display;

import data.Player;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class PlayerSettingsFrame extends JDialog {

    public PlayerSettingsFrame(ArrayList<Player> players, int playerPos, JFrame parentFrame) {
        // Create a modal dialog attached to the parent frame
        super(parentFrame, "Modifiers settings", true);

        setIconImage(GameFrame.LOGO.getImage());
        setSize(500, 475);
        setLayout(null);
        setResizable(false);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        JCheckBox showGuideBlock = new JCheckBox("Show block landing point");
        if (players.get(playerPos).getShowGuideBlock()) showGuideBlock.setSelected(true);
        showGuideBlock.addChangeListener(e -> {
            players.get(playerPos).flipShowGuideBlock();
            MenuFrame.serialize(players);
        });
        showGuideBlock.setBounds(10, 10, 220, 60);
        giveAttributes(showGuideBlock);
        add(showGuideBlock);

        JCheckBox showNextBlock = new JCheckBox("Show next block");
        if (players.get(playerPos).getShowNextBlock()) showNextBlock.setSelected(true);
        showNextBlock.addChangeListener(e -> {
            players.get(playerPos).flipShowNextBlock();
            MenuFrame.serialize(players);
        });
        showNextBlock.setBounds(10, 80, 220, 60);
        giveAttributes(showNextBlock);
        add(showNextBlock);

        JSlider musicVolume = new JSlider(JSlider.HORIZONTAL, 0, 100, (int) (((players.get(playerPos).getMusicVolume() + 80.0f) / 86.0f) * 100));
        musicVolume.setBounds(10, 190, 220, 40);
        musicVolume.setMajorTickSpacing(10);
        musicVolume.setMinorTickSpacing(1);
        musicVolume.setPaintTicks(true);
        musicVolume.setPaintLabels(true);

        musicVolume.addChangeListener(e -> {
            int volume = musicVolume.getValue();
            // Save the volume setting
            players.get(playerPos).setMusicVolume(volume);
            MenuFrame.serialize(players);
        });
        add(musicVolume);

        JCheckBox musicOnOff = new JCheckBox("Music");
        if (players.get(playerPos).getMusicOn()) musicOnOff.setSelected(true);
        musicOnOff.addChangeListener(e -> {
            players.get(playerPos).flipMusicOn();
            MenuFrame.serialize(players);
            musicVolume.setEnabled(musicOnOff.isSelected());
        });
        musicOnOff.setBounds(10, 150, 220, 60);
        giveAttributes(musicOnOff);
        add(musicOnOff);

        musicVolume.setEnabled(musicOnOff.isSelected());

        // Ok button
        JButton ok = new JButton("Ok");
        ok.addActionListener(e -> dispose());
        ok.setBounds(50, 390, 400, 50);
        add(ok);

        // sliding on/off
        // graphics
        // sound effects
        // show layer deletion

        setLocationRelativeTo(parentFrame);
        setVisible(true);
    }

    private static void giveAttributes(JCheckBox checkBox) {
        checkBox.setFocusable(false);
        checkBox.setFont(new Font(null, Font.PLAIN, 15));
        checkBox.setBorder(BorderFactory.createLineBorder(Color.black));
        checkBox.setBorderPainted(true);
    }
}
