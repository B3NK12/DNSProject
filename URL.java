import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class URL extends JFrame {
    private final int WINDOW_WIDTH = 800, WINDOW_HEIGHT = 400;
    private JComboBox urlLabel;
	private JLabel recursiveLabel, iterativeLabel, cachedLabel;
    private JButton enterButton;
    private JPanel panel1, panel2;
    private ButtonListener buttonListener;

    public static void main(String[] args){
        new URL();
    }

    public URL(){
        super("Computer Network Fundamentals Project");
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        buildFrame();
        setVisible(true);
    }

    private void buildFrame(){
        buttonListener = new ButtonListener();
        String[] urlOptions = {"maps.Google.com", "docs.Google.com", "mail.google.com", "drive.google.com", "photos.google.com"};
        urlLabel = new JComboBox<String>(urlOptions);
        recursiveLabel = new JLabel("Recursive DNS output time: ");
        iterativeLabel = new JLabel("Iterative DNS output time: ");
        cachedLabel = new JLabel("Enhanced DNS output time: ");
        panel1 = new JPanel();
        panel2 = new JPanel();
        enterButton = new JButton("Click to Enter");
        panel1.add(urlLabel);
        panel1.add(enterButton);
        panel2.setLayout(new GridLayout(3,1));
        panel2.add(recursiveLabel);
        panel2.add(iterativeLabel);
        panel2.add(cachedLabel);
        recursiveLabel.setHorizontalAlignment(JLabel.CENTER);
        iterativeLabel.setHorizontalAlignment(JLabel.CENTER);
        cachedLabel.setHorizontalAlignment(JLabel.CENTER);
        enterButton.addActionListener(buttonListener);
        add(panel1, BorderLayout.NORTH);
        add(panel2, BorderLayout.CENTER);
    }
    private class ButtonListener implements ActionListener{
        public void actionPerformed(ActionEvent e){
        	String url = (String) urlLabel.getSelectedItem();

            long recursiveTime = DNSSimulator.simulateRecursiveDNS(url);
            long iterativeTime = DNSSimulator.simulateIterativeDNS(url);
            long cachedTime = DNSSimulator.simulateEnhancedDNS(url);

            recursiveLabel.setText("Recursive DNS output time: " + recursiveTime + " ms");
            iterativeLabel.setText("Iterative DNS output time: " + iterativeTime + " ms");
            cachedLabel.setText("Enhanced DNS output time: " + cachedTime + " ms");
        }
    }
}
