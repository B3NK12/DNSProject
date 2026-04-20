import java.awt.*;
import java.awt.event.*;
import java.util.List;
import javax.swing.*;

public class URL extends JFrame {
    private static final int WINDOW_WIDTH = 980;
    private static final int WINDOW_HEIGHT = 620;

    private JComboBox<String> urlComboBox;
    private JLabel recursiveLabel, iterativeLabel, enhancedLabel;
    private JButton runRecursiveButton, runIterativeButton, runEnhancedButton, runAllButton;
    private JPanel topPanel, resultsPanel, buttonPanel;
    private NetworkDiagramPanel diagramPanel;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(URL::new);
    }

    public URL() {
        super("Computer Network Fundamentals Project");
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
        buildFrame();
        setVisible(true);
    }

    private void buildFrame() {
        String[] urlOptions = {
            "maps.google.com",
            "docs.google.com",
            "mail.google.com",
            "drive.google.com",
            "photos.google.com"
        };

        urlComboBox = new JComboBox<>(urlOptions);
        recursiveLabel = new JLabel("Recursive DNS output time: ");
        iterativeLabel = new JLabel("Iterative DNS output time: ");
        enhancedLabel = new JLabel("Enhanced DNS output time: ");

        recursiveLabel.setHorizontalAlignment(JLabel.CENTER);
        iterativeLabel.setHorizontalAlignment(JLabel.CENTER);
        enhancedLabel.setHorizontalAlignment(JLabel.CENTER);

        runRecursiveButton = new JButton("Run Recursive");
        runIterativeButton = new JButton("Run Iterative");
        runEnhancedButton = new JButton("Run Enhanced");
        runAllButton = new JButton("Run All");

        topPanel = new JPanel(new BorderLayout(10, 10));
        JPanel selectorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        selectorPanel.add(new JLabel("Choose URL:"));
        selectorPanel.add(urlComboBox);

        buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(runIterativeButton);
        buttonPanel.add(runRecursiveButton);
        buttonPanel.add(runEnhancedButton);
        buttonPanel.add(runAllButton);

        topPanel.add(selectorPanel, BorderLayout.NORTH);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);

        resultsPanel = new JPanel(new GridLayout(3, 1, 0, 6));
        resultsPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        resultsPanel.add(recursiveLabel);
        resultsPanel.add(iterativeLabel);
        resultsPanel.add(enhancedLabel);

        diagramPanel = new NetworkDiagramPanel();
        diagramPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        add(topPanel, BorderLayout.NORTH);
        add(diagramPanel, BorderLayout.CENTER);
        add(resultsPanel, BorderLayout.SOUTH);

        runIterativeButton.addActionListener(e -> runSingleProtocol("iterative"));
        runRecursiveButton.addActionListener(e -> runSingleProtocol("recursive"));
        runEnhancedButton.addActionListener(e -> runSingleProtocol("enhanced"));
        runAllButton.addActionListener(e -> runAllProtocols());
    }

    private void runSingleProtocol(String protocol) {
        String url = (String) urlComboBox.getSelectedItem();
        disableButtons();
        diagramPanel.clearHighlights();

        SwingWorker<Long, String> worker = new SwingWorker<>() {
            @Override
            protected Long doInBackground() throws Exception {
                DNSSimulator.RouteHighlighter highlighter = new DNSSimulator.RouteHighlighter() {
                    @Override
                    public void highlight(List<NetworkDiagramPanel.NodeId> route, String message) {
                        publish(message);
                        SwingUtilities.invokeLater(() -> diagramPanel.highlightRoute(route));
                    }
                };

                return switch (protocol) {
                    case "iterative" -> DNSSimulator.simulateIterativeDNS(url, highlighter);
                    case "recursive" -> DNSSimulator.simulateRecursiveDNS(url, highlighter);
                    default -> DNSSimulator.simulateEnhancedDNS(url, highlighter);
                };
            }

            @Override
            protected void done() {
                try {
                    long time = get();
                    if ("iterative".equals(protocol)) {
                        iterativeLabel.setText("Iterative DNS output time: " + time + " ms");
                    } else if ("recursive".equals(protocol)) {
                        recursiveLabel.setText("Recursive DNS output time: " + time + " ms");
                    } else {
                        enhancedLabel.setText("Enhanced DNS output time: " + time + " ms");
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                } finally {
                    enableButtons();
                }
            }
        };

        worker.execute();
    }

    private void runAllProtocols() {
        String url = (String) urlComboBox.getSelectedItem();
        disableButtons();
        diagramPanel.clearHighlights();

        SwingWorker<long[], Void> worker = new SwingWorker<>() {
            @Override
            protected long[] doInBackground() {
                long iterativeTime = DNSSimulator.simulateIterativeDNS(url, null);
                long recursiveTime = DNSSimulator.simulateRecursiveDNS(url, null);
                long enhancedTime = DNSSimulator.simulateEnhancedDNS(url, null);
                return new long[]{iterativeTime, recursiveTime, enhancedTime};
            }

            @Override
            protected void done() {
                try {
                    long[] times = get();
                    iterativeLabel.setText("Iterative DNS output time: " + times[0] + " ms");
                    recursiveLabel.setText("Recursive DNS output time: " + times[1] + " ms");
                    enhancedLabel.setText("Enhanced DNS output time: " + times[2] + " ms");
                } catch (Exception ex) {
                    ex.printStackTrace();
                } finally {
                    enableButtons();
                }
            }
        };

        worker.execute();
    }

    private void disableButtons() {
        runIterativeButton.setEnabled(false);
        runRecursiveButton.setEnabled(false);
        runEnhancedButton.setEnabled(false);
        runAllButton.setEnabled(false);
    }

    private void enableButtons() {
        runIterativeButton.setEnabled(true);
        runRecursiveButton.setEnabled(true);
        runEnhancedButton.setEnabled(true);
        runAllButton.setEnabled(true);
    }
}
