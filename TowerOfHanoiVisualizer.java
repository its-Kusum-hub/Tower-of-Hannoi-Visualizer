import javax.swing.*;
import java.awt.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

public class TowerOfHanoiVisualizer extends JPanel {
   
    private final int numDisks = 3;
    
    // Explicitly tracking disks using Stacks
    private final List<Stack<Integer>> pegs = new ArrayList<>();
    private final List<int[]> moveSequence = new ArrayList<>();
    private int currentMoveIndex = 0;
    private Timer animationTimer;

    public TowerOfHanoiVisualizer() {
        // Initialize 3 pegs
        for (int i = 0; i < 3; i++) {
            pegs.add(new Stack<>());
        }
        // Populate the first peg with disks (largest at bottom)
        for (int i = numDisks; i > 0; i--) {
            pegs.get(0).push(i);
        }

        // Precompute the recursive algorithm move mapping
        computeHanoiMoves(numDisks, 0, 2, 1);

        // Timer fires every 1000ms to animate the sequence safely
        animationTimer = new Timer(1000, e -> executeNextMove());
        animationTimer.start();
    }

    private void computeHanoiMoves(int n, int src, int dest, int aux) {
        if (n > 0) {
            computeHanoiMoves(n - 1, src, aux, dest);
            moveSequence.add(new int[]{src, dest});
            computeHanoiMoves(n - 1, aux, dest, src);
        }
    }

    private void executeNextMove() {
        if (currentMoveIndex < moveSequence.size()) {
            int[] move = moveSequence.get(currentMoveIndex);
            int src = move[0];
            int dest = move[1];
            
            // Pop from source and push to destination
            int disk = pegs.get(src).pop();
            pegs.get(dest).push(disk);
            
            currentMoveIndex++;
            repaint(); // Force GUI layout refresh
        } else {
            animationTimer.stop();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();

        // 1. Draw Background
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, width, height);

        // 2. Draw Title "Initial State"
        g2.setColor(Color.BLACK);
        g2.setFont(new Font("Arial", Font.BOLD, 28));
        String title = "Tower of Hanoi Visualization in JAVA";
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(title, (width - fm.stringWidth(title)) / 2, 50);

        // 3. Define Layout Dimensions (Adjusted for spacing)
        int baseY = height - 150; // Moved base up to add space below
        int pegHeight = 250;
        
        // Calculate spacing to leave padding on left and right sides
        int leftMargin = 80;
        int rightMargin = 80;
        int availableWidth = width - leftMargin - rightMargin;
        int pegSpacing = availableWidth / 3; 
        
        // 4. Draw Base
        g2.setColor(new Color(102, 51, 0)); // Dark Brown
        g2.fillRect(leftMargin - 20, baseY, availableWidth + 40, 20);

        // 5. Draw Pegs, Disks, and Labels
        for (int i = 0; i < 3; i++) {
            // Center the pegs evenly within the available width
            int pegX = leftMargin + (pegSpacing / 2) + (i * pegSpacing);

            // Draw Peg Rod (Thick with rounded top)
            g2.setColor(new Color(255, 102, 0)); // Orange
            g2.setStroke(new BasicStroke(12, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.drawLine(pegX, baseY, pegX, baseY - pegHeight);

            // Draw Peg Labels (Added more vertical space here)
            g2.setColor(Color.BLACK);
            g2.setFont(new Font("Arial", Font.BOLD, 16));
            String label1 = "", label2 = "";
            if (i == 0) { label1 = "SOURCE"; label2 = "(A)"; }
            else if (i == 1) { label1 = "AUXILIARY"; label2 = "(B)"; }
            else { label1 = "DESTINATION"; label2 = "(C)"; }

            // Increased labelY offset to create distance from the brown rod
            int labelY = baseY + 50; 
            g2.drawString(label1, pegX - fm.stringWidth(label1)/2, labelY);
            g2.drawString(label2, pegX - fm.stringWidth(label2)/2, labelY + 25);

            // Draw Disks
            Stack<Integer> currentPeg = pegs.get(i);
            for (int j = 0; j < currentPeg.size(); j++) {
                int diskSize = currentPeg.get(j); // 1 is smallest, numDisks is largest
                int diskWidth = diskSize * 50 + 40;
                int diskHeight = 40;
                int diskX = pegX - (diskWidth / 2);
                int diskY = baseY - ((j + 1) * diskHeight);

                // Determine Disk Color based on size
                Color diskColor = Color.GRAY;
                if (diskSize == 1) diskColor = new Color(0, 102, 204); // Blue
                else if (diskSize == 2) diskColor = new Color(255, 204, 0); // Yellow/Orange
                else if (diskSize == 3) diskColor = new Color(204, 0, 0); // Red
                else if (diskSize >= 4) diskColor = new Color(0, 153, 0); // Green (fallback)

                // Draw Disk Body
                g2.setColor(diskColor);
                g2.fillRoundRect(diskX, diskY, diskWidth, diskHeight, 15, 15);

                // Draw Disk Text
                g2.setColor(Color.WHITE); 
                // Yellow disk needs black text for readability
                if (diskSize == 2) g2.setColor(Color.BLACK); 
                
                g2.setFont(new Font("Arial", Font.BOLD, 14));
                String diskText = "Disk " + diskSize;
                FontMetrics fmDisk = g2.getFontMetrics();
                int textX = diskX + (diskWidth - fmDisk.stringWidth(diskText)) / 2;
                int textY = diskY + (diskHeight + fmDisk.getAscent()) / 2 - 2;
                g2.drawString(diskText, textX, textY);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Tower of Hanoi Visualizer");
            TowerOfHanoiVisualizer visualizer = new TowerOfHanoiVisualizer();
            frame.add(visualizer);
            // Increased width and height for better spacing
            frame.setSize(900, 550); 
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}