import javax.swing.*;
import java.awt.*;
import javax.swing.border.*;
 
public class PanelDeInicioDocente extends JFrame {
 
    public PanelDeInicioDocente() {
        setTitle("Panel de inicio docente");
        setSize(1100, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
 
    
        // Barra lateral izquierda
    
        JPanel panelIzquierdo = new JPanel();
        panelIzquierdo.setLayout(new BorderLayout());
        panelIzquierdo.setPreferredSize(new Dimension(250, getHeight()));
        panelIzquierdo.setBackground(Color.WHITE);
 
        // Panel de información del usuario
        JPanel panelUsuario = new JPanel(new GridLayout(2, 1));
        panelUsuario.setBackground(Color.WHITE);
        JLabel lblNombre = new JLabel("Javier Guzmán", SwingConstants.CENTER);
        lblNombre.setFont(new Font("Arial", Font.BOLD, 16));
        JLabel lblRol = new JLabel("Docente", SwingConstants.CENTER);
        lblRol.setFont(new Font("Arial", Font.PLAIN, 12));
        panelUsuario.add(lblNombre);
        panelUsuario.add(lblRol);
 
        // Botones del menú
        JPanel panelMenu = new JPanel(new GridLayout(6, 1, 5, 5));
        panelMenu.setBackground(Color.WHITE);
        String[] opcionesMenu = {"Inicio", "Asignaturas", "Calendario", "Comunicados", "Observador"};
        for (String opcion : opcionesMenu) {
            JButton btnMenu = new JButton(opcion);
            btnMenu.setBackground(Color.WHITE);
            btnMenu.setBorder(new LineBorder(Color.LIGHT_GRAY));
            btnMenu.setFocusPainted(false);
            panelMenu.add(btnMenu);
        }
 
        // Opciones inferiores
        JPanel panelOpcionesInferiores = new JPanel(new GridLayout(2, 1));
        panelOpcionesInferiores.setBackground(Color.WHITE);
        JButton btnConfiguracion = new JButton("Configuración");
        JButton btnCerrarSesion = new JButton("Cerrar Sesión");
        panelOpcionesInferiores.add(btnConfiguracion);
        panelOpcionesInferiores.add(btnCerrarSesion);
 
        panelIzquierdo.add(panelUsuario, BorderLayout.NORTH);
        panelIzquierdo.add(panelMenu, BorderLayout.CENTER);
        panelIzquierdo.add(panelOpcionesInferiores, BorderLayout.SOUTH);
        add(panelIzquierdo, BorderLayout.WEST);
 
    
        // Panel principal (área central)
    
        JPanel panelPrincipal = new JPanel();
        panelPrincipal.setLayout(new BorderLayout(10, 10));
        panelPrincipal.setBackground(Color.WHITE);
 
        // Panel de notificaciones
        JPanel panelNotificaciones = new JPanel(new BorderLayout());
        panelNotificaciones.setBackground(Color.YELLOW);
        JLabel lblNotificacion = new JLabel("Recordatorio: Clases - Próxima Clase para el 30 de septiembre a las 12:30 AM.");
        panelNotificaciones.add(lblNotificacion, BorderLayout.CENTER);
        panelNotificaciones.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panelPrincipal.add(panelNotificaciones, BorderLayout.NORTH);
 
        
        // Contenido central
    
        JPanel panelContenidoCentral = new JPanel(new BorderLayout(10, 10));
        panelContenidoCentral.setBackground(Color.WHITE);
 
        // Sección de Asignaturas (arriba del calendario)
        JPanel panelAsignaturas = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panelAsignaturas.setBackground(Color.LIGHT_GRAY);
 
        JPanel asignatura1 = new JPanel();
        asignatura1.setBackground(Color.GRAY);
        asignatura1.setPreferredSize(new Dimension(100, 100));
 
        JPanel asignatura2 = new JPanel();
        asignatura2.setBackground(Color.GRAY);
        asignatura2.setPreferredSize(new Dimension(100, 100));
 
        JPanel asignatura3 = new JPanel();
        asignatura3.setBackground(Color.GRAY);
        asignatura3.setPreferredSize(new Dimension(100, 100));
 
        panelAsignaturas.add(asignatura1);
        panelAsignaturas.add(asignatura2);
        panelAsignaturas.add(asignatura3);
 
        panelContenidoCentral.add(panelAsignaturas, BorderLayout.NORTH);
 
        // Sección del calendario
        JPanel panelCalendario = new JPanel(new BorderLayout());
        panelCalendario.setBackground(Color.WHITE);
        JLabel lblTituloCalendario = new JLabel("Calendario", SwingConstants.CENTER);
        panelCalendario.add(lblTituloCalendario, BorderLayout.NORTH);
 
        JPanel panelCalendarioGrid = new JPanel();
        panelCalendarioGrid.setLayout(new GridLayout(6, 7, 1, 1));
        String[] dias = {"Dom", "Lun", "Mar", "Mié", "Jue", "Vie", "Sáb"};
        for (String dia : dias) {
            panelCalendarioGrid.add(new JLabel(dia, SwingConstants.CENTER));
        }
        for (int i = 1; i <= 30; i++) {
            panelCalendarioGrid.add(new JLabel(String.valueOf(i), SwingConstants.CENTER));
        }
        panelCalendario.add(panelCalendarioGrid, BorderLayout.CENTER);
 
        panelContenidoCentral.add(panelCalendario, BorderLayout.CENTER);
 
        // Sección de recordatorios (derecha)
        JPanel panelRecordatorios = new JPanel(new GridLayout(2, 1, 5, 5));
        panelRecordatorios.setBackground(Color.WHITE);
        panelRecordatorios.setPreferredSize(new Dimension(200, 150));
 
        JButton btnReunionDirectivos = new JButton("Reunión directivos");
        JButton btnSubirMaterial = new JButton("Subir material de apoyo");
        panelRecordatorios.add(btnReunionDirectivos);
        panelRecordatorios.add(btnSubirMaterial);
 
        panelContenidoCentral.add(panelRecordatorios, BorderLayout.EAST);
 
        panelPrincipal.add(panelContenidoCentral, BorderLayout.CENTER);
 
        // Añadir el panel principal al marco
        add(panelPrincipal, BorderLayout.CENTER);
 
        setVisible(true);
    }
 
    public static void main(String[] args) {
        SwingUtilities.invokeLater(PanelDeInicioDocente::new);
    }
}
