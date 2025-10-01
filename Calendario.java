import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

public class Calendario extends JFrame {

    public Calendario() {
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); 
        setLayout(new BorderLayout()); 

        // ---------------------------
        // Panel Lateral
        // ---------------------------
        JPanel panelLateral = new JPanel();
        panelLateral.setLayout(new BorderLayout());
        panelLateral.setPreferredSize(new Dimension(200, getHeight()));
        panelLateral.setBackground(Color.WHITE);

        JPanel panelPerfil = new JPanel(new GridLayout(2, 1));
        JLabel lblNombre = new JLabel("Javier Guzmán");
        lblNombre.setFont(new Font("Arial", Font.BOLD, 14));
        JLabel lblRol = new JLabel("Docente");
        lblRol.setFont(new Font("Arial", Font.PLAIN, 12));
        panelPerfil.add(lblNombre);
        panelPerfil.add(lblRol);

        JPanel panelMenu = new JPanel();
        panelMenu.setLayout(new GridLayout(5, 1, 5, 5));
        panelMenu.setBackground(Color.WHITE);
        panelMenu.add(new JButton("Inicio"));
        panelMenu.add(new JButton("Asignaturas"));
        panelMenu.add(new JButton("Calendario"));
        panelMenu.add(new JButton("Comunicados"));
        panelMenu.add(new JButton("Observador"));

        JPanel panelSeparador = new JPanel();
        panelSeparador.setPreferredSize(new Dimension(200, 120));
        panelSeparador.setBackground(Color.WHITE);

        JPanel panelOpciones = new JPanel();
        panelOpciones.setLayout(new GridLayout(2, 1, 5, 5));
        panelOpciones.setBackground(Color.WHITE);
        panelOpciones.add(new JButton("Configuración"));
        panelOpciones.add(new JButton("Cerrar Sesión"));

        JPanel panelCentroLateral = new JPanel(new BorderLayout());
        panelCentroLateral.add(panelMenu, BorderLayout.CENTER);
        panelCentroLateral.add(panelSeparador, BorderLayout.SOUTH);

        panelLateral.add(panelPerfil, BorderLayout.NORTH);
        panelLateral.add(panelCentroLateral, BorderLayout.CENTER);
        panelLateral.add(panelOpciones, BorderLayout.SOUTH);


        // ---------------------------
        // Panel Central
        // ---------------------------
        JPanel panelCentral = new JPanel(new BorderLayout());
        panelCentral.setBackground(Color.WHITE);

        // Título a la izquierda
        JLabel lblTitulo = new JLabel("Calendario", SwingConstants.LEFT);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 0));
        panelCentral.add(lblTitulo, BorderLayout.NORTH);

        // Calendario
        JPanel panelCalendario = new JPanel(new GridLayout(6, 7, 2, 2));
        String[] dias = {"Do","Lu","Ma","Mi","Ju","Vi","Sa"};
        for(String d : dias){
            JLabel lblDia = new JLabel(d, SwingConstants.CENTER);
            lblDia.setFont(new Font("Arial", Font.BOLD, 11));
            panelCalendario.add(lblDia);
        }

        int[] diasMes = {
            31,
            1,2,3,4,5,6,
            7,8,9,10,11,12,13,
            14,15,16,17,18,19,20,
            21,22,23,24,25,26,27,
            28,29,30,1,2,3,4
        };

        for (int i = 0; i < diasMes.length; i++) {
            JButton btnDia = new JButton(String.valueOf(diasMes[i]));
            btnDia.setFont(new Font("Arial", Font.PLAIN, 9));
            btnDia.setMargin(new Insets(1, 1, 1, 1)); 
            panelCalendario.add(btnDia);
        }

        // Cuadro con título centrado
        TitledBorder bordeCalendario = BorderFactory.createTitledBorder("Septiembre 2025");
        bordeCalendario.setTitleJustification(TitledBorder.CENTER); 
        JPanel panelCalendarioBorde = new JPanel(new BorderLayout());
        panelCalendarioBorde.setBorder(bordeCalendario);
        panelCalendarioBorde.add(panelCalendario, BorderLayout.CENTER);

        // ---------------------------
        // Cuadro de horario (fondo blanco)
        // ---------------------------
        JPanel panelHorario = new JPanel(new GridLayout(3,1,5,5));
        panelHorario.setBackground(Color.WHITE); 
        TitledBorder bordeHorario = BorderFactory.createTitledBorder("Horario del día");
        bordeHorario.setTitleJustification(TitledBorder.LEFT);
        panelHorario.setBorder(bordeHorario);

        panelHorario.add(new JLabel("7:00 AM - 8:00 AM  |  Matemáticas - 6B"));
        panelHorario.add(new JLabel("8:00 AM - 9:00 AM  |  Álgebra - 8A"));
        panelHorario.add(new JLabel("9:00 AM - 10:00 AM |  Geometría - 9C"));

        JPanel panelCentroContenido = new JPanel(new BorderLayout(10, 10));
        panelCentroContenido.setBackground(Color.WHITE);
        panelCentroContenido.add(panelCalendarioBorde, BorderLayout.CENTER);
        panelCentroContenido.add(panelHorario, BorderLayout.SOUTH);

        panelCentral.add(panelCentroContenido, BorderLayout.CENTER);

        // ---------------------------
        // Panel Derecho
        // ---------------------------
        JPanel panelEventos = new JPanel();
        panelEventos.setLayout(new GridLayout(5, 1, 10, 10)); 
        panelEventos.setBorder(BorderFactory.createTitledBorder("Eventos Próximos"));
        panelEventos.setBackground(Color.WHITE);

        panelEventos.add(crearEvento("📌 Reunión Profesores - 30 Sept"));
        panelEventos.add(crearEvento("📌 Comité de área - 2 Oct"));
        panelEventos.add(crearEvento("📌 Entrega de notas - 10 Oct"));
        panelEventos.add(crearEvento("📌 Consejo académico - 15 Oct"));
        panelEventos.add(crearEvento("📌 Día institucional - 20 Oct"));

        JPanel panelDerecho = new JPanel(new BorderLayout());
        panelDerecho.setPreferredSize(new Dimension(220, getHeight()));
        panelDerecho.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panelDerecho.add(panelEventos, BorderLayout.CENTER);

        // ---------------------------
        // Agregar todo
        // ---------------------------
        add(panelLateral, BorderLayout.WEST);
        add(panelCentral, BorderLayout.CENTER);
        add(panelDerecho, BorderLayout.EAST);

        setVisible(true);
    }

    private JPanel crearEvento(String texto) {
        JPanel evento = new JPanel();
        evento.setLayout(new BorderLayout());
        evento.setBorder(new LineBorder(Color.GRAY, 1, true));
        evento.setBackground(Color.WHITE);
        JLabel lbl = new JLabel(texto);
        lbl.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        evento.add(lbl, BorderLayout.CENTER);
        return evento;
    }

    public static void main(String[] args) {
        Calendario calendario = new Calendario();
    }
}
