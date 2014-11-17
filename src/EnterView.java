import common.CellType;
import common.Config;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class EnterView extends JFrame {

    private static final long serialVersionUID = 1L;
    private JButton jb1, jb2, jb3, jb4;
    private int[][] mazeData, buffer;
    private JRadioButton border, border2, border4, border3, border5;
    private JTextField jf1;
    private ButtonGroup group;
    private int targetData;
    private int[][] checks;
    private JLabel jl;

    public EnterView(MazeUtils mazeUtils) {
        int[][] mazeData = mazeUtils.Maze(Config.mazeCount, Config.mazeCount);
        checks = mazeUtils.getChecks();
        this.mazeData = mazeData;
        buffer = MazeUtils.arrCopy(mazeData);
        this.setLayout(null);// 设置当前的布局格式为精确布局格式

        group = new ButtonGroup();
        border = new JRadioButton();
        border2 = new JRadioButton();
        border3 = new JRadioButton();
        border4 = new JRadioButton();
        border5 = new JRadioButton();
        border2.setSelected(true);

        border.setText("green ");
        border2.setText("white ");
        border3.setText("blue ");
        border4.setText("black ");
        border5.setText("red ");

        jb1 = new JButton("save ");
        jb2 = new JButton("reset ");
        jb3 = new JButton("refresh ");
        jb4 = new JButton("random ");

        jf1 = new JTextField();
        jf1.setText(String.valueOf("2~14"));

        jl = new JLabel();
        jl.setText("maze size:");
        MyListener ml = new MyListener();
        MkeyListener mkl = new MkeyListener();
        jb1.addActionListener(ml);
        jb2.addActionListener(ml);
        jb3.addActionListener(ml);
        jb4.addActionListener(ml);

        jf1.addKeyListener(mkl);

        border.addActionListener(ml);
        border2.addActionListener(ml);
        border3.addActionListener(ml);
        border4.addActionListener(ml);
        border5.addActionListener(ml);

        this.add(this.jb1);
        this.add(this.jb2);
        this.add(this.jb3);
        this.add(this.jb4);
        this.add(this.jf1);
        this.add(this.jl);

        this.add(border);
        this.add(border2);
        this.add(border3);
        this.add(border4);
        this.add(border5);
        group.add(border);
        group.add(border2);
        group.add(border3);
        group.add(border4);
        group.add(border5);

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);// 设置关闭当前窗体时关闭程序
        this.setBounds(10, 10, 800, 700);
        this.setVisible(true);
        jb1.setBounds(400 - 80 - 20, 700 - 80, 80, 40);
        jb2.setBounds(400 + 20, 700 - 80, 80, 40);
        jb3.setBounds(400 + 120, 700 - 80, 80, 40);
        jb4.setBounds(400 - 80 - 120, 700 - 80, 80, 40);

        border.setBounds(650, 100, 120, 40);
        border2.setBounds(650, 150, 120, 40);
        border3.setBounds(650, 200, 120, 40);
        border4.setBounds(650, 250, 120, 40);
        border5.setBounds(650, 300, 120, 40);

        jl.setBounds(650, 420, 120, 40);
        jf1.setBounds(650, 450, 120, 40);
        this.addMouseListener(new MMouseListener());
        this.addMouseMotionListener(new MMouseListener());
    }

    @Override
    public void paint(Graphics g) {
        paintComponents(g);
        refresh();
        for (int i = 0; i < mazeData.length; i++) {
            for (int j = 0; j < mazeData[i].length; j++) {
                switch (mazeData[i][j]) {
                    case CellType.blank:
                        g.setColor(Color.white);
                        break;
                    case CellType.enemy:
                        g.setColor(Color.blue);
                        break;
                    case CellType.hero:
                        g.setColor(Color.black);
                        break;
                    case CellType.item:
                        g.setColor(Color.red);
                        break;
                    case CellType.wall:
                        g.setColor(Color.green);
                        break;
                    default:
                        break;
                }

                g.fillRect(Config.startX + j * Config.cellW, Config.startY + i
                        * Config.cellW, Config.cellW, Config.cellW);
                g.setColor(Color.black);
                g.drawRect(Config.startX + j * Config.cellW, Config.startY + i
                        * Config.cellW, Config.cellW, Config.cellW);
            }
        }
        if (checks != null)
            for (int i = 0; i < checks.length; i++) {
                for (int j = 0; j < checks[i].length; j++) {
                    if (checks[i][j] == 1) {
                        g.setColor(Color.black);
                        // g.drawString("0", Config.startX + (2*j +1)*
                        // Config.cellW,
                        // Config.startY + (2*i+1) * Config.cellW);
                        // g.fillRect(Config.startX + (2*j +1)* Config.cellW,
                        // Config.startY + (2*i+1) * Config.cellW, Config.cellW,
                        // Config.cellW);
                        g.drawArc(Config.startX + j * Config.cellW,
                                Config.startY + i * Config.cellW,
                                Config.cellW, Config.cellW, 0, 360);
                    }
                }
            }
    }

    class MyListener implements ActionListener {
        public void actionPerformed(ActionEvent a) {
            if (a.getSource() == jb1) {
                IOUtils.save(mazeData, Config.fileName);
                // JOptionPane.showMessageDialog(EnterView.this, "asd");
            } else if (a.getSource() == jb2) {
                mazeData = MazeUtils.arrCopy(buffer);
                repaint();
            } else if (a.getSource() == jb3) {
                repaint();
            } else if (a.getSource() == jb4) {
                generate();
            } else if (a.getSource() == border) {
                targetData = CellType.wall;
            } else if (a.getSource() == border2) {
                targetData = CellType.blank;
            } else if (a.getSource() == border3) {
                targetData = CellType.enemy;
            } else if (a.getSource() == border4) {
                targetData = CellType.hero;
            } else if (a.getSource() == border5) {
                targetData = CellType.item;
            }
        }
    }

    private void generate() {
        MazeUtils mazeUtils = new MazeUtils();
        mazeData = mazeUtils.Maze(Config.mazeCount,
                Config.mazeCount);
        checks = mazeUtils.getChecks();
        buffer = MazeUtils.arrCopy(mazeData);
        repaint();
    }

    class MMouseListener extends MouseAdapter implements MouseMotionListener {

        @Override
        public void mouseDragged(MouseEvent e) {
            int clickX = (e.getX() - Config.startX) / Config.cellW;
            int clickY = (e.getY() - Config.startY) / Config.cellW;
            if (clickX < mazeData.length && clickY < mazeData.length
                    && clickX >= 0 && clickY >= 0) {
                mazeData[clickY][clickX] = targetData;
                repaint();
            }
        }

        @Override
        public void mouseMoved(MouseEvent e) {

        }

        @Override
        public void mousePressed(MouseEvent e) {
            int clickX = (e.getX() - Config.startX) / Config.cellW;
            int clickY = (e.getY() - Config.startY) / Config.cellW;
            if (clickX < mazeData.length && clickY < mazeData.length
                    && clickX >= 0 && clickY >= 0) {
                mazeData[clickY][clickX] = targetData;
                repaint();
            }
        }
    }

    public void refresh() {
        border.setText("green " + MazeUtils.getCount(mazeData, CellType.wall));
        border2.setText("white " + MazeUtils.getCount(mazeData, CellType.blank));
        border3.setText("blue " + MazeUtils.getCount(mazeData, CellType.enemy));
        border4.setText("black " + MazeUtils.getCount(mazeData, CellType.hero));
        border5.setText("red " + MazeUtils.getCount(mazeData, CellType.item));
    }

    class MkeyListener implements KeyListener {

        @Override
        public void keyTyped(KeyEvent e) {

        }

        @Override
        public void keyPressed(KeyEvent e) {

        }

        @Override
        public void keyReleased(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                String text = jf1.getText();
                if (text.matches("\\d+")) {
                    int count = Integer.parseInt(text);
                    if (count <= 14) {
                        Config.mazeCount = count;
                        generate();
                    }
                }
            }
        }
    }

}
