import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class URL extends JFrame {
    private final int WINDOW_WIDTH = 800, WINDOW_HEIGHT = 400;
    private JLabel urlLabel, recursiveLabel, iterativeLabel, cachedLabel;
    private JButton enterButton;
    private JPanel panel1, panel2;
    private JTextField textField;
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
        urlLabel = new JLabel("Enter URL");
        recursiveLabel = new JLabel("Recursive DNS output time: ");
        iterativeLabel = new JLabel("Iterative DNS output time: ");
        cachedLabel = new JLabel("Cached DNS output time: ");
        textField = new JTextField(50);
        panel1 = new JPanel();
        panel2 = new JPanel();
        enterButton = new JButton("Click to Enter");
        panel1.add(urlLabel);
        panel1.add(textField);
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
            recursiveLabel.setText("Recursive DNS output time: ");

        }
    }
}
