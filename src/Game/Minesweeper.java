package Game;


import java.awt.*;
import java.awt.Dimension;
import javax.swing.*;
import java.awt.event.*;
import java.util.*;

final class Minesweeper extends JFrame implements ActionListener, ContainerListener {

    int fw, fh, blockr, blockc, var1, var2, num_of_mine, detectedmine = 0, savedlevel = 1,
            savedblockr, savedblockc, savednum_of_mine = 10;     // Menginilisiasi Variable
    int[] r = {-1, -1, -1, 0, 1, 1, 1, 0};						// Menginilisasi Row
    int[] c = {-1, 0, 1, 1, 1, 0, -1, -1};						// Menginilisiasi Column
    JButton[][] blocks;											// Pencetan
    int[][] countmine;											// Jumlah Bomb
    int[][] colour;												// Warna
    ImageIcon[] ic = new ImageIcon[14];							// Icon
    JPanel panelb = new JPanel();								// Window Button/Tempat Bermain
    JPanel panelmt = new JPanel();								// Tempat Utama
    JTextField tf_mine, tf_time;								// Jumlah Bomb dan waktu
    JButton reset = new JButton("");							// Reset Button
    Random ranr = new Random();									// Acak Data
    Random ranc = new Random();
    boolean check = true, starttime = false;
    Point framelocation;
    Stopwatch sw;								// Fungsi StopWatch
    MouseHendeler mh;							// Fungsi Action Ketika mouse diklik
    Point p;									// Menyimpan Lokasi

    Minesweeper() {
        super("Minesweeper");					// SetJudul
        setLocation(400, 300);					// Set Location Default

        setic();							// Set Icon
        setpanel(1, 0, 0, 0);					// Set Level Game yang mempengaruhi ukuran
        setmanue();							// Tambah Menu

        sw = new Stopwatch();			// Tambah Stopwatch

        reset.addActionListener(new ActionListener() {		//Ketika Tombol Reset Diklik

            @Override
            public void actionPerformed(ActionEvent ae) {
                try {
                    // sw.stop();	// Stopwatch Stop
                    setpanel(savedlevel, savedblockr, savedblockc, savednum_of_mine);	// Set Level Terakhir
                } catch (Exception ex) {
                    setpanel(savedlevel, savedblockr, savedblockc, savednum_of_mine);	// Set Level Terakhir
                }
                reset();	// Mereset Stream

            }
        });
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        show();
    }

    public void reset() {
        check = true;	// memberitahu ngeset belum dikli sama sekali
        starttime = false;	// memberitahu ngeset stopwatch belum menyala
        for (int i = 0; i < blockr; i++) {	// Mereset Warna Kotak
            for (int j = 0; j < blockc; j++) {
                colour[i][j] = 'w';
            }
        }
    }

    public void setpanel(int level, int setr, int setc, int setm) {		// Set Panel berdasarkan Level
        switch (level) {
            case 1:
                fw = 300;	// Ukuran Lebar
                fh = 400;	// Ukuran Tinggi
                blockr = 15;	// Jumlah Baris
                blockc = 15;	// Jumlah Kolom
                num_of_mine = 10;	//Jumlah Bomb
                break;
            case 2:
                fw = 410;
                fh = 500;
                blockr = 18;
                blockc = 18;
                num_of_mine = 70;
                break;
            case 3:
                fw = 510;
                fh = 600;
                blockr = 21;
                blockc = 21;
                num_of_mine = 150;
                break;
            case 4:
                fw = (20 * setc);
                fh = (24 * setr);
                blockr = setr;
                blockc = setc;
                num_of_mine = setm;
                break;
            default:
                break;
        }

        savedblockr = blockr;	// Memasukkan Jumlah baris ke saved jika ingin mereset
        savedblockc = blockc;	// sama
        savednum_of_mine = num_of_mine; // sama

        setSize(fw, fh);										// Set Tinggi dan Lebar
        setResizable(false);									// Tidak bisa diubah
        detectedmine = num_of_mine;								// Jumlah Bomb
        p = this.getLocation();										//Menambilkan Kordinat X + Y

        blocks = new JButton[blockr][blockc];					// Membuat Kotak yang bisa diklik sejumlah blockr dan blockc
        countmine = new int[blockr][blockc];					// Membuat Bomb
        colour = new int[blockr][blockc];						// Membuat Warna

        mh = new MouseHendeler();			// Membuat Event Handler ketika user ngeklik button

        getContentPane().removeAll();
        panelb.removeAll();

        tf_mine = new JTextField("" + num_of_mine, 3);	// Set Textfield Berisi Jumlah Bomb
        tf_mine.setEditable(false);							// Tidak bisa diubah
        tf_mine.setFont(new Font("DigtalFont.TTF", Font.BOLD, 25));		//Set Font
        tf_mine.setBackground(Color.BLACK);		// Set BG
        tf_mine.setForeground(Color.RED);		// Set warna Text
        tf_mine.setBorder(BorderFactory.createLoweredBevelBorder());	// Memberi border

        tf_time = new JTextField("000", 3);		// Memberi Timer Default
        tf_time.setEditable(false);		// Tidak Bisa diubah
        tf_time.setFont(new Font("DigtalFont.TTF", Font.BOLD, 25));	//font
        tf_time.setBackground(Color.BLACK);	// Memberi BG
        tf_time.setForeground(Color.RED);	// Memberi Warna
        tf_time.setBorder(BorderFactory.createLoweredBevelBorder());	// Memberi Border

        reset.setIcon(ic[11]);	// Memberi Icon Reset
        reset.setBorder(BorderFactory.createLoweredBevelBorder());	// Memberi Border

        panelmt.removeAll();			// Mengosongkan Kotak
        panelmt.setLayout(new BorderLayout());	// Memberi Layout Agar Rapih
        panelmt.add(tf_mine, BorderLayout.WEST);	// Meletakkan Jumlah Bomb di Kiri/Barat
        panelmt.add(reset, BorderLayout.CENTER);	// Meletakkan Tombol Reset Ditengah
        panelmt.add(tf_time, BorderLayout.EAST);	// Meletakkan Waktu di Timur/Kanan

        panelmt.setBorder(BorderFactory.createLoweredBevelBorder());	// Memberikan Border

        panelb.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10), BorderFactory.createLoweredBevelBorder()));  // Memberikan efek border
        panelb.setPreferredSize(new Dimension(fw, fh));	// Mengatur ukuran tempat bermain
        panelb.setLayout(new GridLayout(0, blockc));	// Memberikan Layout Grid dengan kolom sejumlah block c
        panelb.addContainerListener(this);

        for (int i = 0; i < blockr; i++) {	// Menambah Kotak Menggunakan Perulangan
            for (int j = 0; j < blockc; j++) {
                blocks[i][j] = new JButton("");	// Mengkosongkan Isinya

                //blocks[i][j].addActionListener(this);
                blocks[i][j].addMouseListener(mh);	// Memberikan Listener yang mengeksekusi mh pada setiap button

                panelb.add(blocks[i][j]); // Meletakkan Button pada panel utama

            }
        }
        reset();	// Mereset Kotak

        panelb.revalidate();	//Memastikan posisi yang benar dengan fungsi ini
        panelb.repaint();	// Memastikan warna dengan fungsi ini
        //getcontentpane().setOpaque(true);

        getContentPane().setLayout(new BorderLayout());	//Memberi BorderLayout pada panutama
        getContentPane().addContainerListener(this);		// Memberi Listener
        //getContentPane().revalidate();
        getContentPane().repaint();
        getContentPane().add(panelb, BorderLayout.CENTER);		// Meletakkan panelbutton di center
        getContentPane().add(panelmt, BorderLayout.NORTH);		// meletakkan menu di atas
        setVisible(true);										// membuat visible
    }

}
