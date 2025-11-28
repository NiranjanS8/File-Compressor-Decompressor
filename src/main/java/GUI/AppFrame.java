package GUI;

import comp_decomp.compressor;
import comp_decomp.decompressor;
import comp_decomp.CompressionType;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Objects;

public class AppFrame extends JFrame implements ActionListener {

    private final JButton compressButton;
    private final JButton decompressButton;
    private final JButton openCompressedButton;
    private final JComboBox<String> algoCombo;

    private final JTable fileTable;
    private final DefaultTableModel tableModel;
    private final JLabel statusLabel;

    private static final int COL_ORIGINAL_FILE = 0;
    private static final int COL_ORIGINAL_SIZE = 1;
    private static final int COL_COMPRESSED_FILE = 2;
    private static final int COL_COMPRESSED_SIZE = 3;
    private static final int COL_RATIO = 4;
    private static final int COL_DIRECTORY = 5;

    public AppFrame() {
        setTitle("Compressor / Decompressor");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        setMinimumSize(new Dimension(900, 420));
        setLocationRelativeTo(null);

        // ---------- HEADER ----------
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JLabel titleLabel = new JLabel("File Compressor & Decompressor");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        JLabel subtitleLabel = new JLabel("Choose algorithm, compress files, see details, open and decompress easily.");
        subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.add(titleLabel);
        textPanel.add(Box.createVerticalStrut(4));
        textPanel.add(subtitleLabel);
        textPanel.setOpaque(false);

        headerPanel.add(textPanel, BorderLayout.WEST);
        add(headerPanel, BorderLayout.NORTH);

        // ---------- TABLE ----------
        String[] columns = {
                "Original File",
                "Original Size (KB)",
                "Compressed File",
                "Compressed Size (KB)",
                "Ratio (%)",
                "Directory"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        fileTable = new JTable(tableModel);
        fileTable.setFillsViewportHeight(true);
        fileTable.setRowHeight(22);
        fileTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(fileTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

        add(scrollPane, BorderLayout.CENTER);

        // ---------- BOTTOM ----------
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));

        compressButton = new JButton("Compress File");
        decompressButton = new JButton("Decompress");
        openCompressedButton = new JButton("Open");

        compressButton.addActionListener(this);
        decompressButton.addActionListener(this);
        openCompressedButton.addActionListener(this);

        algoCombo = new JComboBox<>(new String[]{
                "GZIP - Fast (DEFLATE)",
                "XZ - Better Compression (LZMA2)"
        });
        algoCombo.setSelectedIndex(0);

        buttonPanel.add(compressButton);
        buttonPanel.add(decompressButton);
        buttonPanel.add(openCompressedButton);
        buttonPanel.add(new JLabel("Method:"));
        buttonPanel.add(algoCombo);

        statusLabel = new JLabel("Ready.");
        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));

        bottomPanel.add(buttonPanel, BorderLayout.NORTH);
        bottomPanel.add(statusLabel, BorderLayout.SOUTH);

        add(bottomPanel, BorderLayout.SOUTH);

        getContentPane().setBackground(new Color(245, 247, 250));
        setVisible(true);
        pack();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();

        if (src == compressButton) {
            handleCompress();
        } else if (src == decompressButton) {
            handleDecompress();
        } else if (src == openCompressedButton) {
            handleOpenCompressed();
        }
    }

    // --- Compress logic ---
    private void handleCompress() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Select file to compress");
        int result = chooser.showOpenDialog(this);

        if (result != JFileChooser.APPROVE_OPTION) {
            setStatus("Compression cancelled.");
            return;
        }

        File originalFile = chooser.getSelectedFile();
        if (originalFile == null || !originalFile.exists()) {
            showError("Selected file does not exist.");
            return;
        }

        // Decide algorithm
        CompressionType type = (algoCombo.getSelectedIndex() == 0)
                ? CompressionType.GZIP
                : CompressionType.XZ;

        try {
            compressor.method(originalFile, type);

            File compressedFile;
            if (type == CompressionType.GZIP) {
                compressedFile = new File(originalFile.getParent(), originalFile.getName() + ".gz");
            } else {
                compressedFile = new File(originalFile.getParent(), originalFile.getName() + ".xz");
            }

            if (!compressedFile.exists()) {
                showError("Compression finished but compressed file not found.");
                return;
            }

            long originalSize = originalFile.length();
            long compressedSize = compressedFile.length();
            double ratio = originalSize == 0
                    ? 0.0
                    : (compressedSize * 100.0 / originalSize);

            DecimalFormat df = new DecimalFormat("#0.0");

            tableModel.addRow(new Object[]{
                    originalFile.getName(),
                    toKB(originalSize),
                    compressedFile.getName(),
                    toKB(compressedSize),
                    df.format(ratio),
                    originalFile.getParent()
            });

            setStatus("Compressed using " + type + ": " + originalFile.getName() + " → " + compressedFile.getName());
            JOptionPane.showMessageDialog(
                    this,
                    "File compressed successfully.\n\nAlgorithm: " + type +
                            "\nOriginal: " + originalFile.getName() +
                            "\nCompressed: " + compressedFile.getName(),
                    "Compression Success",
                    JOptionPane.INFORMATION_MESSAGE
            );

        } catch (IOException ex) {
            showError("Error compressing file: " + ex.getMessage());
        }
    }

    // --- Decompress logic ---
    private void handleDecompress() {
        File compressedFile = getSelectedCompressedFile();

        if (compressedFile == null) {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Select .gz or .xz file to decompress");
            int result = chooser.showOpenDialog(this);

            if (result != JFileChooser.APPROVE_OPTION) {
                setStatus("Decompression cancelled.");
                return;
            }
            compressedFile = chooser.getSelectedFile();
        }

        if (!compressedFile.exists()) {
            showError("Compressed file does not exist: " + compressedFile.getAbsolutePath());
            return;
        }

        try {
            File outputFile = decompressor.method(compressedFile);

            setStatus("Decompressed to: " + outputFile.getAbsolutePath());
            JOptionPane.showMessageDialog(
                    this,
                    "File decompressed successfully.\n\nOutput: " + outputFile.getAbsolutePath(),
                    "Decompression Success",
                    JOptionPane.INFORMATION_MESSAGE
            );
        } catch (IOException ex) {
            showError("Error decompressing file: " + ex.getMessage());
        }
    }

    // --- Open compressed file with default app / explorer ---
    private void handleOpenCompressed() {
        File compressedFile = getSelectedCompressedFile();
        if (compressedFile == null) {
            showError("Select a row corresponding to a compressed file first.");
            return;
        }

        if (!compressedFile.exists()) {
            showError("Compressed file not found on disk: " + compressedFile.getAbsolutePath());
            return;
        }

        if (!Desktop.isDesktopSupported()) {
            showError("Opening files is not supported on this platform.");
            return;
        }

        try {
            Desktop.getDesktop().open(compressedFile);
            setStatus("Opened: " + compressedFile.getAbsolutePath());
        } catch (IOException ex) {
            showError("Could not open file: " + ex.getMessage());
        }
    }

    // --- Helpers ---

    private File getSelectedCompressedFile() {
        int row = fileTable.getSelectedRow();
        if (row < 0) {
            return null;
        }

        String fileName = Objects.toString(tableModel.getValueAt(row, COL_COMPRESSED_FILE), "");
        String directory = Objects.toString(tableModel.getValueAt(row, COL_DIRECTORY), "");

        if (fileName.isEmpty() || directory.isEmpty()) {
            return null;
        }
        return new File(directory, fileName);
    }

    private String toKB(long bytes) {
        double kb = bytes / 1024.0;
        DecimalFormat df = new DecimalFormat("#0.0");
        return df.format(kb);
    }

    private void setStatus(String msg) {
        statusLabel.setText(msg);
    }

    private void showError(String msg) {
        setStatus(msg);
        JOptionPane.showMessageDialog(
                this,
                msg,
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }
}
