import java.awt.*;
import java.util.*;
import javax.swing.*;

public class NetworkDiagramPanel extends JPanel {
    public enum NodeId {
        HOST, CACHE, ROOT, TLD, AUTH
    }

    private static class Edge {
        final NodeId from;
        final NodeId to;

        Edge(NodeId from, NodeId to) {
            this.from = from;
            this.to = to;
        }

        boolean matches(NodeId a, NodeId b) {
            return (from == a && to == b) || (from == b && to == a);
        }
    }

    private final Map<NodeId, Rectangle> nodes = new EnumMap<>(NodeId.class);
    private final java.util.List<Edge> activeEdges = new ArrayList<>();
    private NodeId activeNode;

    public NetworkDiagramPanel() {
        setPreferredSize(new Dimension(900, 380));
        setBackground(Color.WHITE);
    }

    public void clearHighlights() {
        activeEdges.clear();
        activeNode = null;
        repaint();
    }

    public void highlightNode(NodeId nodeId) {
        activeNode = nodeId;
        repaint();
    }

    public void highlightEdge(NodeId from, NodeId to) {
        activeEdges.clear();
        activeEdges.add(new Edge(from, to));
        repaint();
    }

    public void highlightRoute(java.util.List<NodeId> route) {
        activeEdges.clear();
        for (int i = 0; i < route.size() - 1; i++) {
            activeEdges.add(new Edge(route.get(i), route.get(i + 1)));
        }
        if (!route.isEmpty()) {
            activeNode = route.get(route.size() - 1);
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int h = getHeight();

        Rectangle host = new Rectangle(70, h - 150, 120, 70);
        Rectangle cache = new Rectangle(260, h - 150, 140, 70);
        Rectangle root = new Rectangle(430, 45, 120, 70);
        Rectangle tld = new Rectangle(600, 70, 120, 70);
        Rectangle auth = new Rectangle(700, h - 150, 120, 70);

        nodes.put(NodeId.HOST, host);
        nodes.put(NodeId.CACHE, cache);
        nodes.put(NodeId.ROOT, root);
        nodes.put(NodeId.TLD, tld);
        nodes.put(NodeId.AUTH, auth);

        drawEdge(g2, NodeId.HOST, NodeId.CACHE);
        drawEdge(g2, NodeId.CACHE, NodeId.ROOT);
        drawEdge(g2, NodeId.CACHE, NodeId.TLD);
        drawEdge(g2, NodeId.ROOT, NodeId.TLD);
        drawEdge(g2, NodeId.TLD, NodeId.AUTH);
        drawEdge(g2, NodeId.AUTH, NodeId.CACHE);

        drawNode(g2, NodeId.HOST, "Host");
        drawNode(g2, NodeId.CACHE, "Cache Server");
        drawNode(g2, NodeId.ROOT, "Web Server 1\n(Root)");
        drawNode(g2, NodeId.TLD, "Web Server 2\n(TLD)");
        drawNode(g2, NodeId.AUTH, "Web Server 3\n(Auth)");

        g2.dispose();
    }

    private void drawNode(Graphics2D g2, NodeId nodeId, String label) {
        Rectangle r = nodes.get(nodeId);
        boolean isActive = nodeId == activeNode;

        g2.setColor(isActive ? new Color(255, 236, 179) : new Color(236, 239, 241));
        g2.fillRoundRect(r.x, r.y, r.width, r.height, 24, 24);
        g2.setColor(isActive ? new Color(245, 124, 0) : new Color(120, 144, 156));
        g2.setStroke(new BasicStroke(isActive ? 3f : 1.8f));
        g2.drawRoundRect(r.x, r.y, r.width, r.height, 24, 24);

        g2.setColor(Color.BLACK);
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 14f));
        FontMetrics fm = g2.getFontMetrics();
        String[] lines = label.split("\\n");
        int lineHeight = fm.getHeight();
        int totalHeight = lineHeight * lines.length;
        int y = r.y + (r.height - totalHeight) / 2 + fm.getAscent();
        for (String line : lines) {
            int textWidth = fm.stringWidth(line);
            int x = r.x + (r.width - textWidth) / 2;
            g2.drawString(line, x, y);
            y += lineHeight;
        }
    }

    private void drawEdge(Graphics2D g2, NodeId from, NodeId to) {
        Rectangle a = nodes.get(from);
        Rectangle b = nodes.get(to);
        Point p1 = centerOf(a);
        Point p2 = centerOf(b);
        boolean active = isEdgeActive(from, to);

        g2.setColor(active ? new Color(21, 101, 192) : new Color(189, 189, 189));
        g2.setStroke(new BasicStroke(active ? 4f : 2f));
        g2.drawLine(p1.x, p1.y, p2.x, p2.y);

        drawArrowHead(g2, p1, p2);
        drawArrowHead(g2, p2, p1);
    }

    private boolean isEdgeActive(NodeId from, NodeId to) {
        for (Edge edge : activeEdges) {
            if (edge.matches(from, to)) {
                return true;
            }
        }
        return false;
    }

    private Point centerOf(Rectangle r) {
        return new Point(r.x + r.width / 2, r.y + r.height / 2);
    }

    private void drawArrowHead(Graphics2D g2, Point tail, Point tip) {
        double phi = Math.toRadians(25);
        int barb = 12;
        double dy = tip.y - tail.y;
        double dx = tip.x - tail.x;
        double theta = Math.atan2(dy, dx);
        double x;
        double y;

        for (int j = 0; j < 2; j++) {
            double rho = theta + (j == 0 ? phi : -phi);
            x = tip.x - barb * Math.cos(rho);
            y = tip.y - barb * Math.sin(rho);
            g2.drawLine(tip.x, tip.y, (int) x, (int) y);
        }
    }
}
