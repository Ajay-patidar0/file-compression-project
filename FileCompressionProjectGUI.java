import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;


class FileUtil {
    public static String readFile(String filePath) throws IOException {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        }
        return content.toString();
    }

    public static void writeFile(String filePath, String content) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(content);
        }
    }
}


class HuffmanCoding {
    static HashMap<Character, String> huffmanCodes = new HashMap<>();

    public static void buildHuffmanTree(String text) {
        HashMap<Character, Integer> frequencies = new HashMap<>();
        for (char c : text.toCharArray()) {
            frequencies.put(c, frequencies.getOrDefault(c, 0) + 1);
        }

        PriorityQueue<HuffmanNode> pq = new PriorityQueue<>();
        for (char c : frequencies.keySet()) {
            pq.offer(new HuffmanNode(c, frequencies.get(c)));
        }

        while (pq.size() > 1) {
            HuffmanNode left = pq.poll();
            HuffmanNode right = pq.poll();

            HuffmanNode mergedNode = new HuffmanNode('-', left.frequency + right.frequency);
            mergedNode.left = left;
            mergedNode.right = right;

            pq.offer(mergedNode);
        }

        if (!pq.isEmpty()) {
            HuffmanNode root = pq.poll();
            generateCodes(root, "");
        }
    }

    public static void generateCodes(HuffmanNode root, String code) {
        if (root == null) {
            return;
        }
        if (root.left == null && root.right == null) {
            huffmanCodes.put(root.data, code);
        }

        generateCodes(root.left, code + "0");
        generateCodes(root.right, code + "1");
    }

    public static String compress(String text) {
        StringBuilder compressed = new StringBuilder();
        for (char c : text.toCharArray()) {
            compressed.append(huffmanCodes.get(c));
        }
        return compressed.toString();
    }

    static class HuffmanNode implements Comparable<HuffmanNode> {
        int frequency;
        char data;
        HuffmanNode left, right;

        public HuffmanNode(char data, int frequency) {
            this.data = data;
            this.frequency = frequency;
            left = right = null;
        }

        @Override
        public int compareTo(HuffmanNode node) {
            return this.frequency - node.frequency;
        }
    }
}


class RunLengthEncoding {
    public static String compress(String text) {
        StringBuilder compressed = new StringBuilder();
        int count = 1;
        for (int i = 1; i < text.length(); i++) {
            if (text.charAt(i) == text.charAt(i - 1)) {
                count++;
            } else {
                compressed.append(text.charAt(i - 1)).append(count);
                count = 1;
            }
        }
        compressed.append(text.charAt(text.length() - 1)).append(count);
        return compressed.toString();
    }
}

public class FileCompressionProjectGUI extends JFrame implements ActionListener {
    private JButton selectFileButton;
    private JTextArea statusTextArea;

    public FileCompressionProjectGUI() {
        setTitle("File Compression Project");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the frame
        setLayout(null);

        selectFileButton = new JButton("Select File");
        selectFileButton.setBounds(20, 20, 120, 30);
        selectFileButton.addActionListener(this);
        add(selectFileButton);

        statusTextArea = new JTextArea();
        statusTextArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(statusTextArea);
        scrollPane.setBounds(20, 70, 340, 80);
        add(scrollPane);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == selectFileButton) {
            JFileChooser fileChooser = new JFileChooser();
            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                String filePath = selectedFile.getAbsolutePath();
                try {
                    // Read file content
                    String originalText = FileUtil.readFile(filePath);

                    // Compression algorithms...
                    String rleCompressed = RunLengthEncoding.compress(originalText);
                    String rleFilePath = selectedFile.getParent() + "/rle_compressed.txt";
                    FileUtil.writeFile(rleFilePath, rleCompressed);

                    HuffmanCoding.buildHuffmanTree(originalText);
                    String huffmanCompressed = HuffmanCoding.compress(originalText);
                    String huffmanFilePath = selectedFile.getParent() + "/huffman_compressed.txt";
                    FileUtil.writeFile(huffmanFilePath, huffmanCompressed);

                    statusTextArea.setText("Compression complete. Compressed files saved to:\n" +
                            rleFilePath + "\n" +
                            huffmanFilePath);
                } catch (IOException ex) {
                    statusTextArea.setText("Error: " + ex.getMessage());
                }
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FileCompressionProjectGUI());
    }
}
