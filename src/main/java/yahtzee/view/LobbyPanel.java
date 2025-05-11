package yahtzee.view;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class LobbyPanel extends JPanel {
    private static final String SERVER_IP = "56.228.19.115";

    private JTextField ipField;
    private JTextField nickField;
    private JButton findGameButton;
    private JLabel statusLabel;
    private Timer animationTimer;

    public LobbyPanel() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        nickField = new JTextField("Player", 20);
        findGameButton = new JButton("Oyun Bul");
        statusLabel = new JLabel(" ", SwingConstants.CENTER);

        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.gridy = 0;
        add(new JLabel("Takma Ad:"), gbc);
        gbc.gridy = 1;
        add(nickField, gbc);
        gbc.gridy = 2;
        add(findGameButton, gbc);
        gbc.gridy = 3;
        add(statusLabel, gbc);


        animationTimer = new Timer(500, e -> {
            String current = statusLabel.getText();
            if (current.startsWith("Başka bir oyuncu bekleniyor")) {
                int dots = (current.length() - "Başka bir oyuncu bekleniyor".length()) % 4;
                statusLabel.setText("Başka bir oyuncu bekleniyor" + ".".repeat(dots + 1));
            }
        });
    }

    public String getIp() {
        return SERVER_IP;
    }

    public String getNick() {
        return nickField.getText().trim();
    }

    public void setStatus(String status) {
        statusLabel.setText(status);
        if (status.startsWith("Başka bir oyuncu bekleniyor")) {
            animationTimer.start();
        } else {
            animationTimer.stop();
        }
    }

    public void reset() {
        ipField.setEnabled(true);
        nickField.setEnabled(true);
        findGameButton.setEnabled(true);
        setStatus(" ");
    }

    public void addFindGameListener(ActionListener listener) {
        findGameButton.addActionListener(listener);
    }
}
