import javax.swing.*;
import javax.swing.border.*;
import javax.swing.text.JTextComponent;

import java.awt.*;

/**
 * UI visual (sin lógica) que replica el layout con "píldoras":
 *  - Cada fila es una cápsula gris que contiene el texto (label) y a la derecha
 *    un campo redondeado blanco.
 *  - Incluye placeholder "DD/MM/AA" en Fecha.
 */
public class TicketHelpdesk extends JFrame {

    public TicketHelpdesk() {
        setTitle("Mesa de ayuda - Crear nuevo Ticket");
        setSize(1200, 740);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);

        // ====== Barra lateral (similar a la tuya) ======
        add(buildSidebar(), BorderLayout.WEST);

        // ====== Centro: columna chips + formulario ======
        JPanel center = new JPanel(new BorderLayout());
        center.setBackground(Color.WHITE);

        JPanel cols = new JPanel(new GridLayout(1, 2, 18, 0));
        cols.setBackground(Color.WHITE);
        cols.setBorder(new EmptyBorder(12, 12, 12, 12));
        cols.add(buildShortcuts());
        cols.add(buildForm());
        center.add(cols, BorderLayout.CENTER);

        add(center, BorderLayout.CENTER);
        setVisible(true);
    }

    // =============== Sidebar =================
    private JPanel buildSidebar() {
        JPanel left = new JPanel(new BorderLayout());
        left.setPreferredSize(new Dimension(260, getHeight()));
        left.setBackground(Color.WHITE);
        left.setBorder(new MatteBorder(0, 0, 0, 1, new Color(232,232,232)));

        // Usuario
        JPanel user = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 14));
        user.setBackground(Color.WHITE);
        user.setBorder(new EmptyBorder(12, 16, 8, 16));

        JLabel avatar = new JLabel("\u25CF");
        avatar.setForeground(new Color(210,210,210));
        avatar.setFont(avatar.getFont().deriveFont(Font.BOLD, 54f));

        JPanel info = new JPanel(new GridLayout(2,1));
        info.setOpaque(false);
        JLabel name = new JLabel("Pedro Sanchez");
        name.setFont(new Font("Arial", Font.BOLD, 16));
        JLabel role = new JLabel("Administrativo");
        role.setForeground(new Color(120,120,120));
        role.setFont(new Font("Arial", Font.PLAIN, 12));
        info.add(name); info.add(role);

        user.add(avatar); user.add(info);

        // Menú
        JPanel menu = new JPanel();
        menu.setOpaque(false);
        menu.setLayout(new BoxLayout(menu, BoxLayout.Y_AXIS));
        menu.setBorder(new EmptyBorder(8, 16, 8, 16));
        menu.add(menuBtn("Configurar estructura"));
        menu.add(Box.createVerticalStrut(10));
        menu.add(menuBtn("Calendario"));
        menu.add(Box.createVerticalStrut(10));
        menu.add(menuBtn("Comunicados"));
        menu.add(Box.createVerticalStrut(10));
        menu.add(menuBtn("Observador"));
        menu.add(Box.createVerticalStrut(10));
        menu.add(menuBtn("Gestion de usuario"));

        JPanel submenu = new JPanel();
        submenu.setOpaque(false);
        submenu.setLayout(new BoxLayout(submenu, BoxLayout.Y_AXIS));
        submenu.setBorder(new EmptyBorder(16, 16, 16, 16));
        submenu.add(selectedBtn("Mesa de ayuda"));
        submenu.add(Box.createVerticalStrut(10));
        submenu.add(menuBtn("Gestion de curso"));

        JPanel bottom = new JPanel();
        bottom.setOpaque(false);
        bottom.setLayout(new BoxLayout(bottom, BoxLayout.Y_AXIS));
        bottom.setBorder(new EmptyBorder(16, 16, 24, 16));
        bottom.add(menuBtn("Configuración"));
        bottom.add(Box.createVerticalStrut(10));
        bottom.add(menuBtn("Cerrar Sesión"));

        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.add(menu);
        center.add(submenu);

        left.add(user, BorderLayout.NORTH);
        left.add(center, BorderLayout.CENTER);
        left.add(bottom, BorderLayout.SOUTH);
        return left;
    }

    private JComponent menuBtn(String text){
        JButton b = new JButton(text);
        b.setHorizontalAlignment(SwingConstants.LEFT);
        b.setBackground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorder(new CompoundBorder(new LineBorder(new Color(235,235,235)),
                         new EmptyBorder(10,12,10,12)));
        return b;
    }
    private JComponent selectedBtn(String text){
        JButton b = (JButton) menuBtn(text);
        b.setBackground(new Color(235,245,250));
        b.setBorder(new CompoundBorder(new LineBorder(new Color(210,230,240)),
                         new EmptyBorder(10,12,10,12)));
        return b;
    }

    // =============== Columna izquierda (chips) ===============
    private JPanel buildShortcuts(){
        JPanel p = new JPanel();
        p.setBackground(Color.WHITE);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(new CompoundBorder(
                new MatteBorder(0,1,0,1,new Color(235,235,235)),
                new EmptyBorder(16,16,16,16)
        ));
        p.add(chip("Crear nuevo ticket", true));
        p.add(Box.createVerticalStrut(16));
        p.add(chip("Seguimiento al estado", false));
        p.add(Box.createVerticalStrut(16));
        p.add(chip("Resolución y cierre", false));
        return p;
    }
    private JComponent chip(String text, boolean main){
        JButton b = new JButton(text);
        b.setHorizontalAlignment(SwingConstants.LEFT);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBackground(main ? new Color(230,243,251) : Color.WHITE);
        b.setBorder(new CompoundBorder(new LineBorder(new Color(210,225,235)),
                        new EmptyBorder(12,14,12,14)));
        b.setFont(new Font("Arial", Font.BOLD, 14));
        return b;
    }

    // =============== Columna derecha (formulario con "píldoras") ===============
    private JPanel buildForm(){
        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setBackground(Color.WHITE);

        JPanel card = new JPanel(new GridBagLayout()){
            @Override protected void paintComponent(Graphics g){
                super.paintComponent(g);
                // fondo azul claro como en la imagen
                g.setColor(new Color(206, 232, 240));
                g.fillRoundRect(0,0,getWidth(),getHeight(),16,16);
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(18,18,18,18));

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(10, 10, 10, 10);
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.weightx = 1;

        JLabel title = new JLabel("Crear nuevo Ticket", SwingConstants.LEFT);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        gc.gridx=0; gc.gridy=0; gc.gridwidth=2;
        card.add(title, gc);

        // ---- Fila 1: Ticket No / Fecha ----
        gc.gridwidth = 1;
        gc.gridy = 1;
        gc.gridx = 0;
        card.add(pillField("Ticket No.", new RoundTextField(14)), gc);

        PlaceholderTextField fecha = new PlaceholderTextField("DD/MM/AA", 10);
        gc.gridx = 1;
        card.add(pillField("Fecha", fecha), gc);

        // ---- Fila 2: Nombre / Rol ----
        gc.gridy = 2; gc.gridx = 0;
        card.add(pillField("Nombre", new RoundTextField(18)), gc);
        gc.gridx = 1;
        card.add(pillField("Rol", new RoundTextField(14)), gc);

        // ---- Fila 3: Asunto (ancho completo) ----
        gc.gridy = 3; gc.gridx = 0; gc.gridwidth = 2;
        card.add(pillField("Asunto", new RoundTextField(30)), gc);

        // ---- Fila 4: Categoria / Prioridad ----
        gc.gridwidth = 1; gc.gridy = 4; gc.gridx = 0;
        card.add(pillField("Categoria", new RoundTextField(16)), gc);
        gc.gridx = 1;
        card.add(pillField("Prioridad", new RoundTextField(16)), gc);

        // ---- Fila 5: Tipo (ancho completo) ----
        gc.gridy = 5; gc.gridx = 0; gc.gridwidth = 2;
        card.add(pillField("Tipo", new RoundTextField(28)), gc);

        // ---- Fila 6: Descripción (área grande con etiqueta) ----
        gc.gridy = 6; gc.gridx = 0; gc.gridwidth = 2; gc.fill = GridBagConstraints.BOTH;
        gc.weighty = 1;
        card.add(descriptionBlock(), gc);

        wrap.add(card, BorderLayout.CENTER);
        return wrap;
    }

    /** Construye una "píldora": cápsula gris con label y campo redondeado blanco */
    private JComponent pillField(String label, JComponent field){
        RoundedPanel capsule = new RoundedPanel(new Color(204, 204, 204, 120), 24);
        capsule.setLayout(new GridBagLayout());
        capsule.setBorder(new EmptyBorder(10, 14, 10, 14));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(0,0,0,10);
        c.gridx=0; c.gridy=0; c.anchor = GridBagConstraints.WEST;

        JLabel l = new JLabel(label);
        l.setFont(new Font("Arial", Font.BOLD, 14));
        capsule.add(l, c);

        c.gridx=1; c.weightx=1; c.fill=GridBagConstraints.HORIZONTAL;

        // Los campos tienen borde redondeado blanco
        if (field instanceof JTextComponent) {
            field.setFont(new Font("Arial", Font.PLAIN, 14));
        }
        field.setBorder(new CompoundBorder(
                new LineBorder(new Color(210,210,210), 2, true),
                new EmptyBorder(8,10,8,10)
        ));
        field.setBackground(Color.WHITE);
        capsule.add(field, c);
        return capsule;
    }

    /** Bloque de descripción con etiqueta "pegada" arriba-izquierda y área grande */
    private JComponent descriptionBlock(){
        JPanel holder = new JPanel(new GridBagLayout()){
            @Override protected void paintComponent(Graphics g){
                super.paintComponent(g);
                // Caja grande con esquinas redondeadas
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(204, 204, 204, 120));
                g2.fillRoundRect(0, 8, getWidth(), getHeight()-8, 24, 24);
                g2.dispose();
            }
        };
        holder.setOpaque(false);
        holder.setBorder(new EmptyBorder(10, 10, 10, 10));

        GridBagConstraints c = new GridBagConstraints();
        c.gridx=0; c.gridy=0; c.anchor=GridBagConstraints.WEST;
        JLabel tag = new JLabel("Descripción");
        tag.setFont(new Font("Arial", Font.BOLD, 15));
        holder.add(tag, c);

        c.gridy=1; c.weightx=1; c.weighty=1; c.fill=GridBagConstraints.BOTH;
        JTextArea area = new JTextArea();
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBorder(new CompoundBorder(
                new EmptyBorder(12,12,12,12),
                new EmptyBorder(0,0,0,0)
        ));
        JScrollPane sp = new JScrollPane(area);
        sp.setBorder(new EmptyBorder(14,14,14,14));
        holder.add(sp, c);

        return holder;
    }

    // ---------- Utilidades UI ----------
    /** Panel redondeado para los chips/cápsulas */
    static class RoundedPanel extends JPanel {
        private final int arc;
        private final Color bg;

        RoundedPanel(Color bg, int arc){
            this.bg = bg; this.arc = arc;
            setOpaque(false);
        }
        @Override protected void paintComponent(Graphics g){
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bg);
            g2.fillRoundRect(0,0,getWidth(),getHeight(),arc,arc);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    /** JTextField con placeholder opcional */
    static class PlaceholderTextField extends RoundTextField {
        private String placeholder;
        PlaceholderTextField(String placeholder, int cols){
            super(cols);
            this.placeholder = placeholder;
        }
        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (getText().isEmpty() && !isFocusOwner()){
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(new Color(150, 160, 170));
                g2.setFont(getFont().deriveFont(Font.ITALIC));
                Insets ins = getInsets();
                g2.drawString(placeholder, ins.left + 2, getHeight()/2 + getFont().getSize()/2 - 3);
                g2.dispose();
            }
        }
    }

    /** Campo texto con esquinas redondeadas (simple) */
    static class RoundTextField extends JTextField {
        RoundTextField(int cols){ super(cols); setOpaque(true); }
    }

    // -------- Main --------
    public static void main(String[] args) {
        SwingUtilities.invokeLater(TicketHelpdesk::new);
    }
}