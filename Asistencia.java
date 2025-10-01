import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;

public class Asistencia extends JFrame {
    public Asistencia() {
        // Configuración básica de la ventana
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);

        // ---------------------------
        // Panel lateral izquierdo
        // ---------------------------
        JPanel panelIzquierdo = new JPanel(new BorderLayout());
        panelIzquierdo.setPreferredSize(new Dimension(200, getHeight()));
        panelIzquierdo.setBackground(Color.WHITE);

        // Panel de usuario
        JPanel panelUsuario = new JPanel(new GridLayout(2, 1));
        panelUsuario.setBackground(Color.WHITE);
        panelUsuario.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel lblUsuario = new JLabel("Javier Guzmán");
        lblUsuario.setFont(new Font("Arial", Font.BOLD, 14));
        JLabel lblRol = new JLabel("Docente");
        lblRol.setFont(new Font("Arial", Font.PLAIN, 12));

        panelUsuario.add(lblUsuario);
        panelUsuario.add(lblRol);

        // Panel Menu con botones
        JPanel panelMenu = new JPanel(new GridLayout(5, 1, 5, 5));
        panelMenu.setBackground(Color.WHITE);

        JButton btnInicio = new JButton("Inicio");
        JButton btnAsignaturas = new JButton("Asignaturas");
        JButton btnCalendario = new JButton("Calendario");
        JButton btnComunicados = new JButton("Comunicados");
        JButton btnObservador = new JButton("Observador");

        // Colores: Asignaturas resaltado (como en tu original)
        btnInicio.setBackground(Color.WHITE);
        btnAsignaturas.setBackground(new Color(173, 216, 230)); // Azul claro
        btnCalendario.setBackground(Color.WHITE);
        btnComunicados.setBackground(Color.WHITE);
        btnObservador.setBackground(Color.WHITE);

        panelMenu.add(btnInicio);
        panelMenu.add(btnAsignaturas);
        panelMenu.add(btnCalendario);
        panelMenu.add(btnComunicados);
        panelMenu.add(btnObservador);

        // Panel de opciones (abajo)
        JPanel panelOpciones = new JPanel(new GridLayout(2, 1, 5, 5));
        panelOpciones.setBackground(Color.WHITE);
        panelOpciones.setBorder(BorderFactory.createEmptyBorder(100, 10, 10, 10));

        JButton btnConfig = new JButton("Configuración");
        JButton btnCerrar = new JButton("Cerrar Sesión");

        panelOpciones.add(btnConfig);
        panelOpciones.add(btnCerrar);

        // Armar panel izquierdo
        panelIzquierdo.add(panelUsuario, BorderLayout.NORTH);
        panelIzquierdo.add(panelMenu, BorderLayout.CENTER);
        panelIzquierdo.add(panelOpciones, BorderLayout.SOUTH);

        add(panelIzquierdo, BorderLayout.WEST);

        // ---------------------------
        // Panel central (con pestañas, filtros y tabla)
        // ---------------------------
        JPanel panelCentral = new JPanel(new BorderLayout(15, 15));
        panelCentral.setBackground(Color.WHITE);
        panelCentral.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Panel superior compuesto: pestañas + botones Curso/Periodo centrados debajo
        JPanel panelSuperiorComp = new JPanel(new BorderLayout());
        panelSuperiorComp.setBackground(Color.WHITE);

        // Barra de pestañas (Clases | Notas | Asistencias)
        JPanel barraSuperior = new JPanel(new FlowLayout(FlowLayout.CENTER, 50, 5));
        barraSuperior.setBackground(Color.WHITE);

        JLabel lblClases = new JLabel("Clases");
        JLabel lblNotas = new JLabel("Notas");
        JLabel lblAsistencias = new JLabel("Asistencias");

        lblClases.setFont(new Font("Arial", Font.PLAIN, 14));
        lblNotas.setFont(new Font("Arial", Font.PLAIN, 14));
        lblAsistencias.setFont(new Font("Arial", Font.BOLD, 14)); // resaltada

        barraSuperior.add(lblClases);
        barraSuperior.add(lblNotas);
        barraSuperior.add(lblAsistencias);

        // Panel con botones "Curso" y "Periodo" centrados bajo las pestañas
        JPanel panelFiltros = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 8));
        panelFiltros.setBackground(Color.WHITE);

        JButton btnCurso = new JButton("Curso");
        JButton btnPeriodo = new JButton("Periodo");

        // estilo similar a los botones blancos (borde claro)
        JButton[] filtros = {btnCurso, btnPeriodo};
        for (JButton b : filtros) {
            b.setBackground(Color.WHITE);
            b.setFocusPainted(false);
            b.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
            b.setPreferredSize(new Dimension(120, 30));
        }

        panelFiltros.add(btnCurso);
        panelFiltros.add(btnPeriodo);

        panelSuperiorComp.add(barraSuperior, BorderLayout.NORTH);
        panelSuperiorComp.add(panelFiltros, BorderLayout.SOUTH);

        panelCentral.add(panelSuperiorComp, BorderLayout.NORTH);

        // ---------------------------
        // TABLA de Asistencia
        // ---------------------------
        String[] columnas = {"Estudiante", "28/07", "29/07", "30/07", "31/07", "1/08", "2/08"};
        Object[][] datos = {
                {"Rafael Diaz", "❌", "✅", "✅", "✅", "✅", "✅"},
                {"Edgar Gutierrez", "✅", "❌", "✅", "✅", "✅", "✅"},
                {"Ana Martinez", "✅", "✅", "❌", "✅", "✅", "✅"},
                {"Julian Neisa", "✅", "✅", "✅", "❌", "✅", "✅"},
                {"Camilo Fajardo", "✅", "✅", "✅", "✅", "✅", "✅"},
                {"Juan Campo", "✅", "✅", "✅", "✅", "✅", "✅"},
        };

        DefaultTableModel modelo = new DefaultTableModel(datos, columnas);
        JTable tabla = new JTable(modelo) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // no editable
            }
        };

        tabla.setRowHeight(30);
        tabla.setGridColor(new Color(220, 220, 220));
        tabla.setShowVerticalLines(false);
        tabla.setFillsViewportHeight(true);
        tabla.setFont(new Font("Arial", Font.PLAIN, 14));

        JTableHeader header = tabla.getTableHeader();
        header.setBackground(new Color(240, 240, 240));
        header.setFont(new Font("Arial", Font.BOLD, 13));

        // Render para centrar y poner colores en ✅ y ❌
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);

        for (int i = 1; i < tabla.getColumnCount(); i++) {
            tabla.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        JScrollPane scrollTabla = new JScrollPane(tabla);
        scrollTabla.setBorder(BorderFactory.createEmptyBorder(10, 80, 10, 80));
        panelCentral.add(scrollTabla, BorderLayout.CENTER);

        // ---------------------------
        // Panel inferior (Leyenda + Botón Guardar)
        // ---------------------------
        JPanel panelInferior = new JPanel(new BorderLayout());
        panelInferior.setBackground(Color.WHITE);

        JPanel leyenda = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 5));
        leyenda.setBackground(Color.WHITE);
        JLabel lblAsistio = new JLabel("✅ Asistió");
        JLabel lblNoAsistio = new JLabel("❌ No asistió");
        leyenda.add(lblAsistio);
        leyenda.add(lblNoAsistio);

        JButton btnGuardar = new JButton("Guardar cambios");
        btnGuardar.setBackground(new Color(220, 220, 220));
        btnGuardar.setFocusPainted(false);
        btnGuardar.setPreferredSize(new Dimension(160, 35));

        panelInferior.add(leyenda, BorderLayout.WEST);
        panelInferior.add(btnGuardar, BorderLayout.EAST);

        panelCentral.add(panelInferior, BorderLayout.SOUTH);

        add(panelCentral, BorderLayout.CENTER);

        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Asistencia::new);
    }
}
