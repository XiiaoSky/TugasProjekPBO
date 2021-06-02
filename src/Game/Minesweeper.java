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
        setmenu();							// Tambah Menu

        sw = new Stopwatch();			// Tambah Stopwatch

        reset.addActionListener(new ActionListener() {		//Ketika Tombol Reset Diklik

            @Override
            public void actionPerformed(ActionEvent ae) {
                try {
                    sw.stop();	// Stopwatch Stop
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
    
     public void setmenu() {
        JMenuBar bar = new JMenuBar(); // Membuat Menu Bar

        JMenu game = new JMenu("GAME"); // Membuat Jmenu dengan judul " game

        JMenuItem menuitem = new JMenuItem("new game"); // Menu Pertama new game
        final JCheckBoxMenuItem beginner = new JCheckBoxMenuItem("Begineer"); // Membuat Item Pertama Mode Beginner
        final JCheckBoxMenuItem medium = new JCheckBoxMenuItem("Medium"); // Membuat Kedua Mode Medium
        final JCheckBoxMenuItem expart = new JCheckBoxMenuItem("Expert"); // Membuat Ketiga Mode Expert

        final JMenuItem exit = new JMenuItem("Exit"); // Membuat Menu Exit
        final JMenu help = new JMenu("Help"); // Membuat Menu Help
        final JMenuItem helpitem = new JMenuItem("Help"); // Membuat item help ketika diklik

        ButtonGroup status = new ButtonGroup(); // Membuat Button Group

        menuitem.addActionListener( // Ketika Menu Diklik
                new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {

                        //panelb.removeAll();
                        //reset();
                        setpanel(1, 0, 0, 0);
                        //panelb.revalidate();
                        //panelb.repaint();
                    }
                });

        beginner.addActionListener(  // Ketika klik mode beginner
                new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        panelb.removeAll();
                        reset();
                        setpanel(1, 0, 0, 0); // setpanel dengan paramter level 1
                        panelb.revalidate();
                        panelb.repaint();
                        beginner.setSelected(true);
                        savedlevel = 1;
                    }
                });
        medium.addActionListener(
                new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        panelb.removeAll();
                        reset();
                        setpanel(2, 0, 0, 0);  // setpanel dengan paramter level 2
                        panelb.revalidate();
                        panelb.repaint();
                        medium.setSelected(true);
                        savedlevel = 2;
                    }
                });
        expart.addActionListener(
                new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        panelb.removeAll();
                        reset();
                        setpanel(3, 0, 0, 0);  // setpanel dengan paramter level 3
                        panelb.revalidate();
                        panelb.repaint();
                        expart.setSelected(true);
                        savedlevel = 3;
                    }
                });

      
        exit.addActionListener(new ActionListener() { // Keluar Dari Game

            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        helpitem.addActionListener(new ActionListener() { // Help Menu

            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, "instruction");

            }
        });

        setJMenuBar(bar);  

        status.add(beginner); // Menambahkan element
        status.add(medium);
        status.add(expart);
        

        game.add(menuitem);
        game.addSeparator();
        game.add(beginner);
        game.add(medium);
        game.add(expart);     
        game.addSeparator();
        game.add(exit);
        help.add(helpitem);

        bar.add(game);
        bar.add(help);

    }

 @Override
    public void componentAdded(ContainerEvent ce) {
    }

    @Override
    public void componentRemoved(ContainerEvent ce) {
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
    }
    
      class MouseHendeler extends MouseAdapter {

        @Override
        public void mouseClicked(MouseEvent me) {
            if (check == true) { // Mengecek Apakah game sudah dimulai apa belum, true berarti belum
                for (int i = 0; i < blockr; i++) { // perulangan baris
                    for (int j = 0; j < blockc; j++) { // perulangan kolom
                        if (me.getSource() == blocks[i][j]) { // Mengarah ke kordinat yang diklik mouse
                            var1 = i; // memasukkan kordinat x(i) ke var1
                            var2 = j; // memasukkan krodinat y(j) ke var2
                            i = blockr; // memasukkan baris ke i
                            break;
                        }
                    }
                }

                setmine(); // fungsi memasang bomb
               calculation();
                check = false;

            }

           showvalue(me);
            winner();

            if (starttime == false) {
                sw.Start();
                starttime = true;
            }

        }
    }
      
        public void setmine() {
        int row = 0, col = 0; // memulai dari x = 0  dan y 0
        Boolean[][] flag = new Boolean[blockr][blockc]; // memberi tanda bomb


        for (int i = 0; i < blockr; i++) {
            for (int j = 0; j < blockc; j++) {
                flag[i][j] = true;
                countmine[i][j] = 0;
            } // Mengosongkan semua bomb
        }

        flag[var1][var2] = false; // mengosongkan bomb pada area yang diklik
        colour[var1][var2] = 'b'; // memberi warna abu

        for (int i = 0; i < num_of_mine; i++) { 
            row = ranr.nextInt(blockr); // mengacak baris yang ingin diberi bomb
            col = ranc.nextInt(blockc); // mengacak kolom yang ingin diberi bomb

            if (flag[row][col] == true) { // jika kordinat yang ditunjuk masi belum di "sentuh"

                countmine[row][col] = -1; // memberi bomb
                colour[row][col] = 'b';
                flag[row][col] = false; // memberi tahu sudah disentuh
            } else {
                i--; // mengulang kembali agar perulangan sesuai hingga max jumlah bomb
            }
        }
    }
        
        
        public void showvalue(MouseEvent e) {
        for (int i = 0; i < blockr; i++) {
            OUTER:
            for (int j = 0; j < blockc; j++) {
                if (e.getSource() == blocks[i][j]) { // mendapatkan kordeinat yang dipilih
                    if (e.isMetaDown() == false) { 
                        if (blocks[i][j].getIcon() == ic[10]) { //  memberi icon bomb
                            if (detectedmine < num_of_mine) { // jika bomb yang terdeteksi lebih kurang dari jumlah bomb
                                detectedmine++; // menambah bomb
                            }
                           tf_mine.setText("" + detectedmine);
                        }
                        switch (countmine[i][j]) {
                            case -1: //Jika  Bomb
                                for (int k = 0; k < blockr; k++) { //perulangan baris
                                    for (int l = 0; l < blockc; l++) { //perulangan kolom
                                        if (countmine[k][l] == -1) { // jika area yang diklik  bomb
                                            blocks[k][l].setIcon(ic[9]);
                                            blocks[k][l].removeMouseListener(mh); // menghilangkan efek dilik
                                        }
                                        blocks[k][l].removeMouseListener(mh); 
                                    }
                                }   sw.stop();
                                reset.setIcon(ic[12]); // mengubah wajah reset
                                JOptionPane.showMessageDialog(null, "Game Over, Wanna try again?"); //memberikan dialog
                                break;
                            case 0:
                                dfs(i, j); // mengacak area disekitar klik jika tidak ada game
                                break;
                            default:
                                blocks[i][j].setIcon(ic[countmine[i][j]]);
                                //blocks[i][j].setText(""+countmine[i][j]);
                                //blocks[i][j].setBackground(Color.pink);
                                //blocks[i][j].setFont(new Font("",Font.PLAIN,8));
                                colour[i][j] = 'b';
                                //blocks[i][j].setBackground(Color.pink);
                                break OUTER;
                        }
                    } else {
                        if (detectedmine != 0) { // jika bomb sudah habis
                            if (blocks[i][j].getIcon() == null) {
                                detectedmine--;
                                blocks[i][j].setIcon(ic[10]);
                            }
                            tf_mine.setText("" + detectedmine);
                        }
                    }
                }
            }
        }

    }

   

    public void dfs(int row, int col) {

        int R, C;
        colour[row][col] = 'b'; //memberiwarna

        blocks[row][col].setBackground(Color.GRAY); //memberiwarna

       // blocks[row][col].setIcon(ic[countmine[row][col]]);
        //blocks[row][col].setText("");
        for (int i = 0; i < 8; i++) { //mengacak area disekitar klik dan memberi angka sesuai "Count Mine"
            R = row + r[i]; 
            C = col + c[i];
            if (R >= 0 && R < blockr && C >= 0 && C < blockc && colour[R][C] == 'w') {
                if (countmine[R][C] == 0) { // Jika yang diklik Kosong (tidak ada angka)
                    dfs(R, C);
                } else {
                    blocks[R][C].setIcon(ic[countmine[R][C]]);
                    //blocks[R][C].setText(""+countmine[R][C]);

                    //blocks[R][C].setBackground(Color.pink);
                    //blocks[R][C].setFont(new Font("",Font.BOLD,));
                    colour[R][C] = 'b';

                }
            }


        }
    }

   
    public void setic() {
        String name;

        for (int i = 0; i <= 8; i++) {
            name = i + ".gif";
            ic[i] = new ImageIcon(name);
        }
        ic[9] = new ImageIcon("mine.gif");
        ic[10] = new ImageIcon("flag.gif");
        ic[11] = new ImageIcon("new game.gif");
        ic[12] = new ImageIcon("crape.gif");
    }
    
        public void winner() {
        int q = 0;
        for (int k = 0; k < blockr; k++) {
            for (int l = 0; l < blockc; l++) {
                if (colour[k][l] == 'w') {
                    q = 1;
                }
            }
        }


        if (q == 0) {
            //panelb.hide();
            for (int k = 0; k < blockr; k++) {
                for (int l = 0; l < blockc; l++) {
                    blocks[k][l].removeMouseListener(mh);
                }
            }

            sw.stop();
            JOptionPane.showMessageDialog(this, "You Are Amazing");
        }
    }
     
     public class Stopwatch extends JFrame implements Runnable {

        long startTime;
        //final static java.text.SimpleDateFormat timerFormat = new java.text.SimpleDateFormat("mm : ss :SSS");
        //final JButton startStopButton= new JButton("Start/stop");
        Thread updater;
        boolean isRunning = false;
        long a = 0;
        Runnable displayUpdater;

        public Stopwatch() {
            this.displayUpdater = new Runnable() {
                
                @Override
                public void run() {
                    displayElapsedTime(a); //update waktu timer
                    a++;
                }
            };
        }

        public void stop() {
            long elapsed = a; // memasukkan nilai ke elapsed
            isRunning = false;
            try {
                updater.join();
            } catch (InterruptedException ie) {
            }
            displayElapsedTime(elapsed); //set waktu timer
            a = 0; // mereset kembali
        }

        private void displayElapsedTime(long elapsedTime) { //memasukkan angka ke timer

            if (elapsedTime >= 0 && elapsedTime < 9) {
                tf_time.setText("00" + elapsedTime);
            } else if (elapsedTime > 9 && elapsedTime < 99) {
                tf_time.setText("0" + elapsedTime);
            } else if (elapsedTime > 99 && elapsedTime < 999) {
                tf_time.setText("" + elapsedTime);
            }
        }

        @Override
        public void run() { //ketika timer berjalan menggunakan fungsi ini
            try {
                while (isRunning) {
                    SwingUtilities.invokeAndWait(displayUpdater); // menggunakan fungsi dari pc
                    Thread.sleep(1000); // setara dengan 1 detik
                }
            } catch (java.lang.reflect.InvocationTargetException ite) {
                ite.printStackTrace(System.err);
            } catch (InterruptedException ie) {
            }
        }

        public void Start() {
            startTime = System.currentTimeMillis(); 
            isRunning = true;
            updater = new Thread(this);
            updater.start();
        }
    }

    
     public void calculation() {
        int row, column;

        for (int i = 0; i < blockr; i++) {
            for (int j = 0; j < blockc; j++) {
                int value = 0;
                int R, C;
                row = i;
                column = j;
                if (countmine[row][column] != -1) { // Jika yang diklik bukan bomb maka akan mengacak
                    for (int k = 0; k < 8; k++) {
                        R = row + r[k];
                        C = column + c[k];

                        if (R >= 0 && C >= 0 && R < blockr && C < blockc) {
                            if (countmine[R][C] == -1) { //Jika disekitar ada bomb maka akan memberi value 1 atau 2 dikotak tersebut
                                value++;
                            }

                        }

                    }
                    countmine[row][column] = value; //memasukkan angka ke kotak

                }
            }
        }
    }

    }
        
