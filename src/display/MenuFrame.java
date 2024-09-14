package display;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import data.*;

public class MenuFrame implements ActionListener {

    private boolean fileExists = true;

    private JFrame frame;

    private JPanel centerPanel;

    private JPanel playerPanel;
    private JTextField playerName;
    private JButton loadPlayerButton;
    private JButton createPlayer;
    private JButton changeDifficulty;
    private int level = 1;
    private JButton start;

    private JPanel leaderboard;
    private JLabel[] topFive = new JLabel[5];

    private JPanel statsPanel;
    private JLabel playerStats;
    private JLabel highScore;
    private JLabel averageScore;
    private JLabel gamesPlayed;
    private JButton playerSettings;
    JTextField changeUsernameField;

    private Player player;
    private int playerPos;
    private int gameMode;
    protected int[] modifiers = new int[10];

    public MenuFrame() {}

    public void run() {
        frame = new JFrame("Main Menu");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setLayout(null);
        frame.getContentPane().setBackground(Color.black);
        frame.setIconImage(GameFrame.LOGO.getImage());
        frame.setSize(1000, 600);

        createPanels();
        createCenterComponents();
        createLeaderboardComponents();
        createStatsComponents();

        setGameMode();

        if (!new File("Player.ser").exists()) {
            fileExists = false;
        }

        setTopScores();

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    protected void setGameMode() {
        for (int activated : modifiers) {
            if (activated >= 1) {
                gameMode = 1;
                return;
            }
        }
        gameMode = 0;
    }

    protected void setTopScores() {
        if (!fileExists) {
            return;
        }

        ArrayList<Player> players = deserialize();
        players.sort(((score1, score2) -> Integer.compare(score2.getHighScore(level, gameMode), score1.getHighScore(level, gameMode))));
        int size = players.size();
        if (size > 5) { size = 5; }
        for (int j = 0; j < size; j++) {
            topFive[j].setText(j + 1 + ". " + players.get(j).getHighScore(level, gameMode) + " (" + players.get(j).getName() + ")");
        }
    }

    protected void setPlayerStats() {
        if (player == null) {
            return;
        }

        playerStats.setText(player.getName() + " Stats");
        highScore.setText("High score: " + player.getHighScore(level, gameMode));
        averageScore.setText("Average score: " + player.getAverageScore(level, gameMode));
        gamesPlayed.setText("Games played: " + player.getGamesPlayed(level, gameMode));
    }

    protected void refreshGUI() {
        setTopScores();
        setPlayerStats();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == start) {
            if (player != null) {
                ArrayList<Player> players = deserialize();
                new GameFrame(level, gameMode, players, playerPos);
                frame.setVisible(false);
                return;
            }
            new GameFrame(level, gameMode, null, -1);
            frame.setVisible(false);
        }
        else if (e.getSource() == changeDifficulty) {
            switch(level) {
                case 1:
                    changeDifficulty.setText("MEDIUM");
                    changeDifficulty.setForeground(Color.orange);
                    level = 2;
                    break;
                case 2:
                    changeDifficulty.setText("HARD");
                    changeDifficulty.setForeground(Color.red);
                    level = 3;
                    break;
                case 3:
                    changeDifficulty.setText("EASY");
                    changeDifficulty.setForeground(Color.green);
                    level = 1;
                    break;
            }
            refreshGUI();
        }

        // run loadPlayer function when loadPlayerButton is clicked
        else if (e.getSource() == loadPlayerButton) {
            loadPlayer();
        }

        // Try to create a new player if createPlayer button is clicked
        else if (e.getSource() == createPlayer) {
            // Retrieve all saved players or empty arraylist if no players saved
            ArrayList<Player> players = deserialize();

            // Save new player name temporary
            String newUsername = playerName.getText();

            // If there are some players saved, check if 'new player' already exists (same name)
            if (fileExists) {

                // Go through all players to check if the name is valid
                for (Player p : players) {
                    // If player with that name already exists, show message and exit the function
                    if (p.getName().equalsIgnoreCase(newUsername)) {
                        JOptionPane.showMessageDialog(null, "Player with username " + newUsername + " already exists, please change your username to create a new player", "Player already exists", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
            }

            // Ask user if he is sure that he wants to create a new player
            int response = JOptionPane.showConfirmDialog(null, "Are you sure you want to create a new player with the name " + newUsername + "?", "Confirmation", JOptionPane.YES_NO_OPTION);

            // If user didn't want to proceed, return
            if (response == JOptionPane.NO_OPTION) {
                return;
            }

            // Add, save and load new player
            players.add(new Player(playerName.getText()));
            serialize(players);
            loadPlayer();

            // Refresh top scores
            setTopScores();
        }
    }

    protected void loadPlayer() {
        /*
          checking if file with player accounts exists
          if it doesn't - inform that player does not exist + exit the method
        */
        if (!fileExists) {
            JOptionPane.showMessageDialog(null, "Player does not exist", "Player does not exist", JOptionPane.ERROR_MESSAGE);
            return;
        }
        // making an ArrayList with player accounts that are saved on this pc
        ArrayList<Player> players = deserialize();

        // checking if player account exists - loading the account to the current game if true
        int i = 0;
        for (Player p : players) {
            if (p.getName().equalsIgnoreCase(playerName.getText())) {
                playerPos = i;
                player = p;
                loadPlayerButton.setEnabled(false);
                createPlayer.setEnabled(false);
                createPlayer.setSelected(false);
                changeUsernameField.setEnabled(true);
                playerSettings.setEnabled(true);
                setPlayerStats();
                return;
            }
            i++;
        }
        // informing that player doesn't exist
        JOptionPane.showMessageDialog(null, "Player does not exist", "Player does not exist", JOptionPane.ERROR_MESSAGE);
    }

    protected static void serialize(ArrayList<Player> players) {
        try {
            FileOutputStream fileOut = new FileOutputStream("Player.ser");
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(players);

            out.close();
            fileOut.close();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private ArrayList<Player> deserialize() {
        if (!fileExists) {
            return new ArrayList<>();
        }
        ArrayList<Player> players;
        try {
            FileInputStream fileIn = new FileInputStream("Player.ser");
            ObjectInputStream in = new ObjectInputStream(fileIn);

            players = (ArrayList<Player>) in.readObject();

            in.close();
            fileIn.close();
        } catch (ClassNotFoundException | IOException ex) {
            throw new RuntimeException(ex);
        }
        return players;
    }


    public void rerun() {
        frame.setVisible(true);
        refreshGUI();
    }


    // Creating JFrame components
    private void createPanels() {
        JLabel title = new JLabel("TETRIS");
        title.setFont(new Font("Serif", Font.BOLD, 75));
        title.setForeground(Color.green);
        title.setBounds(360, 22, 280, 60);
        frame.add(title);

        centerPanel = new JPanel();
        centerPanel.setBounds(300, 100, 400, 400);
        centerPanel.setLayout(new BorderLayout());
        frame.add(centerPanel);

        playerPanel = new JPanel();
        playerPanel.setBackground(Color.black);
        playerPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED, Color.cyan, Color.cyan));
        playerPanel.setPreferredSize(new Dimension(400, 300));
        playerPanel.setLayout(null);
        centerPanel.add(playerPanel, BorderLayout.NORTH);

        leaderboard = new JPanel();
        leaderboard.setBackground(Color.black);
        leaderboard.setBounds(25, 100, 250, 400);
        leaderboard.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED, Color.yellow, Color.yellow));
        frame.add(leaderboard);

        statsPanel = new JPanel();
        statsPanel.setBackground(Color.black);

        statsPanel.setLayout(new BorderLayout());

        statsPanel.setBounds(725, 100, 250, 400);
        statsPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED, Color.red, Color.red));
        frame.add(statsPanel);
    }

    private void createLeaderboardComponents() {
        JLabel topScores = new JLabel("TOP SCORES");
        topScores.setFont(new Font(null, Font.BOLD, 35));
        topScores.setForeground(new Color(250, 250, 0));
        topScores.setBorder(BorderFactory.createMatteBorder(0, 0, 4, 0, Color.yellow));
        //topScores.setPreferredSize(new Dimension(250, 100));
        leaderboard.add(topScores);

        for (int i = 0; i < 5; i++) {
            topFive[i] = new JLabel(i + 1 + ". [score] ([player])");
            topFive[i].setFont(new Font(null, Font.PLAIN, 25));
            topFive[i].setPreferredSize(new Dimension(250, 40));
            topFive[i].setForeground(Color.yellow);
            topFive[i].setHorizontalAlignment(JLabel.CENTER);
            leaderboard.add(topFive[i]);
        }
    }

    private void createCenterComponents() {
        start = new JButton("START");
        start.setFocusable(false);
        start.setPreferredSize(new Dimension(400, 100));
        start.setFont(new Font("Arial Black", Font.BOLD, 60));
        start.addActionListener(this);
        start.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
        centerPanel.add(start, BorderLayout.SOUTH);

        playerName = new JTextField("Player");
        playerName.addActionListener(e -> loadPlayer());
        playerName.setBounds(125, 50, 150, 50);
        playerName.setFont(new Font(null, Font.PLAIN, 30));
        playerName.setHorizontalAlignment(JTextField.CENTER);
        playerName.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {}
            public void removeUpdate(DocumentEvent e) {
                createPlayer.setEnabled(true);
                loadPlayerButton.setEnabled(true);
            }
            public void insertUpdate(DocumentEvent e) {
                createPlayer.setEnabled(true);
                loadPlayerButton.setEnabled(true);
            }});
        playerPanel.add(playerName);

        JLabel usernameLabel = new JLabel("Username");
        usernameLabel.setFont(new Font(null, Font.PLAIN, 30));
        usernameLabel.setForeground(Color.cyan);
        usernameLabel.setBounds(125, 10, 150, 50);
        playerPanel.add(usernameLabel);

        loadPlayerButton = new JButton("Load player account");
        loadPlayerButton.setBounds(125, 105, 150, 30);
        loadPlayerButton.setFocusable(false);
        loadPlayerButton.addActionListener(this);
        playerPanel.add(loadPlayerButton);

        createPlayer = new JButton("Create new player");
        createPlayer.setBounds(125, 140, 150, 25);
        createPlayer.setFocusable(false);
        createPlayer.addActionListener(this);
        playerPanel.add(createPlayer);

        changeDifficulty = new JButton("EASY");
        changeDifficulty.setBounds(75, 180, 250, 90);
        changeDifficulty.setForeground(Color.green);
        changeDifficulty.setFocusable(false);
        changeDifficulty.setFont(new Font("Arial Black", Font.BOLD, 40));
        changeDifficulty.addActionListener(this);
        playerPanel.add(changeDifficulty);

        JButton changeModifiers = new JButton("Change Modifiers");
        changeModifiers.setBounds(125, 267, 150, 30);
        changeModifiers.setFocusable(false);
        changeModifiers.addActionListener(e -> new ModifiersFrame(frame));
        playerPanel.add(changeModifiers);

    }

    private void createStatsComponents() {
        JPanel stats = new JPanel();
        stats.setBackground(Color.black);
        stats.setLayout(new GridLayout(5, 1));

        playerStats = new JLabel("[player] Stats");
        playerStats.setForeground(Color.red);
        playerStats.setHorizontalAlignment(JLabel.CENTER);
        playerStats.setBorder(BorderFactory.createMatteBorder(0, 0, 4, 0, Color.red));
        playerStats.setFont(new Font(null, Font.BOLD, 35));
        stats.add(playerStats);

        highScore = new JLabel("High score: [score]");
        giveAttributes(highScore, stats);

        averageScore = new JLabel("Average score: [score]");
        giveAttributes(averageScore, stats);

        gamesPlayed = new JLabel("Games played: [n]");
        giveAttributes(gamesPlayed, stats);

        playerSettings = new JButton("Player Settings");
        playerSettings.addActionListener(e -> new PlayerSettingsFrame(deserialize(), playerPos, frame));
        playerSettings.setFocusable(false);
        playerSettings.setPreferredSize(new Dimension(150, 50));
        playerSettings.setFont(new Font(null, Font.BOLD, 15));
        playerSettings.setEnabled(false);
        stats.add(playerSettings);

        statsPanel.add(stats, BorderLayout.NORTH);

        // Create components for changing username
        JLabel changeUsernameLabel = new JLabel("Change username:");
        changeUsernameLabel.setForeground(Color.red);
        changeUsernameLabel.setHorizontalAlignment(JLabel.CENTER);
        changeUsernameLabel.setFont(new Font(null, Font.PLAIN, 20));

        changeUsernameField = new JTextField(15);
        changeUsernameField.addActionListener(e -> changePlayerName(changeUsernameField.getText()));
        changeUsernameField.setFont(new Font(null, Font.PLAIN, 20));
        changeUsernameField.setEnabled(false);

        // Create a panel for the username change components
        JPanel usernamePanel = new JPanel();
        usernamePanel.setLayout(new BorderLayout());
        usernamePanel.setBackground(Color.black);
        usernamePanel.add(changeUsernameLabel, BorderLayout.NORTH);
        usernamePanel.add(changeUsernameField, BorderLayout.SOUTH);

        // Add the panel to the bottom of statsPanel
        statsPanel.add(usernamePanel, BorderLayout.SOUTH);
    }

    private void changePlayerName(String name) {
        ArrayList<Player> players = deserialize();

        for (Player p : players) {
            if (p.getName().equalsIgnoreCase(name)) {
                JOptionPane.showMessageDialog(null, "Player with username " + name + " already exists, please choose another username.", "Player already exists", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        int response = JOptionPane.showConfirmDialog(null, "Are you sure you want to change your username to " + name + "?", "Confirmation", JOptionPane.YES_NO_OPTION);

        // If user didn't want to proceed, return
        if (response == JOptionPane.NO_OPTION) {
            return;
        }

        playerName.setText(name);
        players.get(playerPos).setName(name);
        player = players.get(playerPos);
        serialize(players);
        setTopScores();
        setPlayerStats();
    }

    private void giveAttributes(JLabel label, JPanel panel) {
//        label.setPreferredSize(new Dimension(250, 30));
        label.setForeground(Color.red);
        label.setFont(new Font(null, Font.PLAIN, 20));
        label.setHorizontalAlignment(JLabel.CENTER);
        label.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
        panel.add(label);
    }
}