import java.awt.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.*;

public class InicioSesionyRegistro {

    public static String API_KEY = null;
    public static String DATABASE_URL = null;
    public static final int WIDTH = 1000;
    public static final int HEIGHT = 600;

    public static void main(String[] args) {
        try {
            Map<String, String> cfg = leerConfig("conexion.txt");
            API_KEY = cfg.get("apiKey");
            DATABASE_URL = cfg.get("databaseUrl");
            if (API_KEY == null || DATABASE_URL == null) {
                JOptionPane.showMessageDialog(null,
                        "Falta apiKey o databaseUrl en conexion.txt",
                        "Configuración", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null,
                    "Error leyendo conexion.txt: " + ex.getMessage(),
                    "Configuración", JOptionPane.ERROR_MESSAGE);
            return;
        }
        SwingUtilities.invokeLater(() -> {
            InicioSesionFrame f = new InicioSesionFrame();
            f.setVisible(true);
        });
    }

    // ---------------- Usuario ----------------
    public static class Usuario {
        private String uid;
        private String nombre;
        private int edad;
        private String telefono;
        private String email;
        private String role;
        private boolean activo;
        private long createdAt;

        public Usuario() {}

        public Usuario(String nombre, int edad, String telefono, String email, String role) {
            setNombre(nombre);
            setEdad(edad);
            setTelefono(telefono);
            setEmail(email);
            setRole((role == null || role.trim().isEmpty()) ? "USER" : role.trim().toUpperCase());
            this.activo = true;
            this.createdAt = System.currentTimeMillis();
        }

        public void setUid(String v){ this.uid = (v == null || v.trim().isEmpty()) ? null : v.trim(); }
        public void setNombre(String v){ if (v == null || v.trim().length() < 2) throw new IllegalArgumentException("Nombre inválido"); this.nombre = v.trim(); }
        public void setEdad(int v){ if (v < 0 || v > 120) throw new IllegalArgumentException("Edad inválida"); this.edad = v; }
        public void setTelefono(String v){ if (v == null || !v.matches("\\+?\\d{7,15}")) throw new IllegalArgumentException("Teléfono inválido"); this.telefono = v.trim(); }
        public void setEmail(String v){ Pattern p  = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"); if (v == null || !p.matcher(v).matches()) throw new IllegalArgumentException("Email inválido"); this.email = v.trim().toLowerCase(); }
        public void setRole(String v){ String r = (v == null ? "" : v.trim().toUpperCase()); this.role = r.isEmpty() ? "USER" : r; }
        public void setActivo(boolean activo){ this.activo = activo; }
        public void setCreatedAt(long createdAt){ this.createdAt = createdAt; }

        public String getUid(){ return this.uid; }
        public String getNombre(){ return this.nombre; }
        public int getEdad(){ return this.edad; }
        public String getTelefono(){ return this.telefono; }
        public String getEmail(){ return this.email; }
        public String getRole(){ return this.role; }
        public boolean isActivo(){ return this.activo; }
        public long getCreatedAt(){ return this.createdAt; }

        public String toJson(){
            StringBuilder sb = new StringBuilder();
            sb.append("{");
            appendKV(sb, "uid", uid, true); sb.append(",");
            appendKV(sb, "nombre", nombre, true); sb.append(",");
            appendKV(sb, "edad", String.valueOf(edad), false); sb.append(",");
            appendKV(sb, "telefono", telefono, true); sb.append(",");
            appendKV(sb, "email", email, true); sb.append(",");
            appendKV(sb, "role", role, true); sb.append(",");
            appendKV(sb, "activo", String.valueOf(activo), false); sb.append(",");
            appendKV(sb, "createdAt", String.valueOf(createdAt), false);
            sb.append("}");
            return sb.toString();
        }

        public static void appendKV(StringBuilder sb, String key, String value, boolean isString){
            sb.append("\"").append(key).append("\":");
            if (value == null) { sb.append("null"); return; }
            if (isString) { sb.append("\"").append(escapeJson(value)).append("\""); } else { sb.append(value); }
        }

        public static String escapeJson(String s){
            if (s == null) return "";
            StringBuilder out = new StringBuilder();
            for (int i = 0; i < s.length(); i++){
                char c = s.charAt(i);
                switch(c){
                    case '"': out.append("\\\""); break;
                    case '\\': out.append("\\\\"); break;
                    case '\b': out.append("\\b"); break;
                    case '\f': out.append("\\f"); break;
                    case '\n': out.append("\\n"); break;
                    case '\r': out.append("\\r"); break;
                    case '\t': out.append("\\t"); break;
                    default:
                        if(c < 0x20 || c > 0x7E){
                            out.append(String.format("\\u%04x", (int)c));
                        } else {
                            out.append(c);
                        }
                }
            }
            return out.toString();
        }
    }

    // ---------------- Helpers HTTP & JSON ----------------
    private static String httpWrite(String method, String urlStr, String jsonBody) throws IOException {
        URL url = new URL(urlStr);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setConnectTimeout(20000);
        con.setReadTimeout(20000);
        con.setRequestMethod(method);
        con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        con.setDoOutput(true);
        if (jsonBody != null && !jsonBody.isEmpty()) {
            try (OutputStream os = con.getOutputStream()) {
                byte[] out = jsonBody.getBytes(StandardCharsets.UTF_8);
                os.write(out);
                os.flush();
            }
        }
        int code = con.getResponseCode();
        InputStream is = (code >= 200 && code < 300) ? con.getInputStream() : con.getErrorStream();
        String resp = readAll(is);
        if (code < 200 || code >= 300) throw new IOException("HTTP " + code + ": " + resp);
        return resp;
    }

    private static String httpPost(String urlStr, String jsonBody) throws IOException { return httpWrite("POST", urlStr, jsonBody); }
    private static String httpPut(String urlStr, String jsonBody) throws IOException { return httpWrite("PUT", urlStr, jsonBody); }

    private static String readAll(InputStream is) throws IOException {
        if (is == null) return "";
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder(); String line;
            while ((line = br.readLine()) != null) sb.append(line);
            return sb.toString();
        }
    }

    private static String escapeJson(String s) {
        if (s == null) return "";
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '\"': out.append("\\\""); break;
                case '\\': out.append("\\\\"); break;
                case '\b': out.append("\\b");  break;
                case '\f': out.append("\\f");  break;
                case '\n': out.append("\\n");  break;
                case '\r': out.append("\\r");  break;
                case '\t': out.append("\\t");  break;
                default:
                    if (c < 0x20) out.append(String.format("\\u%04x", (int)c));
                    else out.append(c);
            }
        }
        return out.toString();
    }

    private static String extraerCampo(String json, String campo) {
        if (json == null) return null;
        Pattern p = Pattern.compile("\"" + Pattern.quote(campo) + "\"\\s*:\\s*\"(.*?)\"");
        Matcher m = p.matcher(json);
        return m.find() ? m.group(1) : null;
    }

    private static Map<String,String> leerConfig(String path) throws IOException {
        Map<String,String> map = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            while((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                int eq = line.indexOf('=');
                if (eq > 0) {
                    String k = line.substring(0, eq).trim();
                    String v = line.substring(eq+1).trim();
                    map.put(k, v);
                }
            }
        }
        return map;
    }

    private static String traducirError(String firebaseMessage) {
        if (firebaseMessage == null) return "Error desconocido";
        switch (firebaseMessage) {
            case "EMAIL_NOT_FOUND": return "Email no registrado";
            case "INVALID_PASSWORD": return "Contraseña incorrecta";
            case "USER_DISABLED": return "Usuario deshabilitado";
            case "TOO_MANY_ATTEMPTS_TRY_LATER": return "Demasiados intentos. Intenta más tarde";
            case "WEAK_PASSWORD": return "Contraseña débil";
            case "INVALID_EMAIL": return "Email inválido";
            default: return firebaseMessage;
        }
    }

    // ---------------- UI UTILITIES (para alargar y organizar) --------------
    private static JLabel makeTitle(String text, int size) {
        JLabel l = new JLabel(text, SwingConstants.CENTER);
        l.setFont(new Font("SansSerif", Font.BOLD, size));
        return l;
    }
    private static JButton makeBigButton(String text) {
        JButton b = new JButton(text);
        b.setPreferredSize(new Dimension(240, 52));
        b.setFont(new Font("SansSerif", Font.BOLD, 16));
        return b;
    }
    private static JTextField makeField(int cols) {
        JTextField f = new JTextField(cols);
        f.setPreferredSize(new Dimension(360, 40));
        f.setFont(new Font("SansSerif", Font.PLAIN, 14));
        return f;
    }
    private static JPasswordField makePass(int cols) {
        JPasswordField p = new JPasswordField(cols);
        p.setPreferredSize(new Dimension(360, 40));
        p.setFont(new Font("SansSerif", Font.PLAIN, 14));
        return p;
    }
    private static JPanel makeCardPanel() {
        JPanel p = new JPanel();
        p.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY,10));
        p.setBackground(Color.WHITE);
        return p;
    }
    private static void addEmptySpace(Container c, int h) {
        JPanel p = new JPanel();
        p.setPreferredSize(new Dimension(10, h));
        p.setOpaque(false);
        c.add(p);
    }

    // ================== FRAME: INICIO SESIÓN ====================
    static class InicioSesionFrame extends JFrame {

        private final JTextField tfEmail = makeField(25);
        private final JPasswordField pfPass = makePass(25);
        private final JButton btnLogin = makeBigButton("Ingresar");
        private final JButton btnRegistro = makeBigButton("Registrarse");
        private final JButton btnOlvido = makeBigButton("¿Olvidó su contraseña?");
        private final JButton btnRoles = makeBigButton("Registrarse seleccionando rol");
        private final JLabel lblMsg = new JLabel(" ", SwingConstants.CENTER);

        public InicioSesionFrame() {
            super("Inicio de Sesión");
            setSize(WIDTH, HEIGHT);
            setResizable(false);
            setExtendedState(JFrame.MAXIMIZED_BOTH);
            setLocationRelativeTo(null);
            build();
        }

        private void build() {
            setSize(WIDTH, HEIGHT);
            setDefaultCloseOperation(EXIT_ON_CLOSE);
            setLocationRelativeTo(null);
            getContentPane().setLayout(new BorderLayout(12,12));

            JPanel top = new JPanel(new BorderLayout());
            top.setBackground(Color.WHITE);
            JLabel title = makeTitle("INICIO DE SESIÓN", 28);
            top.add(title, BorderLayout.CENTER);
            getContentPane().add(top, BorderLayout.CENTER);

            JPanel center = new JPanel(new GridBagLayout());
            center.setBackground(new Color(245,245,245));
            center.setBorder(BorderFactory.createEmptyBorder(20, 80, 20, 80));
            GridBagConstraints c = new GridBagConstraints();
            c.insets = new Insets(10,10,10,10);
            c.fill = GridBagConstraints.HORIZONTAL;

            int row=0;
            c.gridx=0; c.gridy=row; c.gridwidth=2; c.weightx=1;
            JLabel sub = new JLabel("Accede con tu correo y contraseña", SwingConstants.CENTER);
            sub.setFont(new Font("SansSerif", Font.PLAIN, 16));
            center.add(sub, c);

            row++;
            c.gridwidth=1;
            c.gridx=0; c.gridy=row; c.weightx=0; center.add(new JLabel("Correo:"), c);
            c.gridx=1; c.gridy=row; c.weightx=1; center.add(tfEmail, c);

            row++;
            c.gridx=0; c.gridy=row; c.weightx=0; center.add(new JLabel("Contraseña:"), c);
            c.gridx=1; c.gridy=row; c.weightx=1; center.add(pfPass, c);

            row++;
            c.gridx=0; c.gridy=row; c.gridwidth=2; c.anchor = GridBagConstraints.CENTER;
            JPanel botones = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 8));
            botones.add(btnLogin); botones.add(btnRegistro); botones.add(btnOlvido);
            center.add(botones, c);

            row++;
            c.gridy=row; c.gridx=0; c.gridwidth=2; center.add(lblMsg, c);

            getContentPane().add(center, BorderLayout.CENTER);

            JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER));
            JButton btnDemo = new JButton("Ingresar como demo");
            btnDemo.setPreferredSize(new Dimension(220,46));
            bottom.add(btnDemo);
            bottom.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
            getContentPane().add(bottom, BorderLayout.SOUTH);

            btnLogin.addActionListener(e -> doLogin());
            btnRegistro.addActionListener(e -> {
                dispose();
                SeleccionarRolFrame s = new SeleccionarRolFrame();
                s.setVisible(true);
            });
            btnOlvido.addActionListener(e -> {
                dispose();
                RecuperarClaveFrame r = new RecuperarClaveFrame();
                r.setVisible(true);
            });
            btnDemo.addActionListener(e -> {
                tfEmail.setText("demo@ejemplo.com");
                pfPass.setText("demopass");
                lblMsg.setText("Rellenado demo. Presiona Ingresar.");
            });
        }

        private void doLogin() {
            String email = tfEmail.getText().trim();
            String pass = new String(pfPass.getPassword());
            if (email.isEmpty() || pass.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Completa correo y contraseña", "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (pass.length() < 6) {
                JOptionPane.showMessageDialog(this, "Contraseña mínima 6 caracteres", "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                String url = "https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=" + API_KEY;
                String body = "{\"email\":\"" + escapeJson(email) + "\",\"password\":\"" + escapeJson(pass) + "\",\"returnSecureToken\":true}";
                String resp = httpPost(url, body);
                String idToken = extraerCampo(resp, "idToken");
                String uid = extraerCampo(resp, "localId");
                if (idToken == null || uid == null) throw new RuntimeException("Respuesta inesperada: " + resp);
                SwingUtilities.invokeLater(() -> {
                    BienvenidaFrame b = new BienvenidaFrame(email);
                    b.setVisible(true);
                    dispose();
                });
            } catch (IOException io) {
                String msg = io.getMessage();
                String firebaseMsg = extraerCampo(msg, "message");
                if ("EMAIL_NOT_FOUND".equals(firebaseMsg)) {
                    int opt = JOptionPane.showConfirmDialog(this,
                            "Ese email no está registrado.\n¿Deseas crear la cuenta ahora?",
                            "Usuario no encontrado", JOptionPane.YES_NO_OPTION);
                    if (opt == JOptionPane.YES_OPTION) {
                        dispose();
                        new SeleccionarRolFrame().setVisible(true);
                        return;
                    } else {
                        return;
                    }
                }
                JOptionPane.showMessageDialog(this, "Error de autenticación: " + traducirError(firebaseMsg), "Login", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Login", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // ================== FRAME: SELECCIONAR ROL ====================
    static class SeleccionarRolFrame extends JFrame {
        public SeleccionarRolFrame() {
            super("Seleccionar Rol");
                    setSize(WIDTH, HEIGHT);
            setResizable(false);
            setExtendedState(JFrame.MAXIMIZED_BOTH);
            setLocationRelativeTo(null);
            build();
        }

        private void build() {
            setSize(WIDTH, HEIGHT);
            setDefaultCloseOperation(EXIT_ON_CLOSE);
            setLocationRelativeTo(null);
            getContentPane().setLayout(new BorderLayout(10,10));
            getContentPane().add(makeTitle("SELECCIONAR ROL", 26), BorderLayout.NORTH);

            JPanel center = new JPanel(new GridLayout(2,2,30,30));
            center.setBorder(BorderFactory.createEmptyBorder(60,120,60,120));
            JButton bEst = makeBigButton("ESTUDIANTE");
            JButton bDoc = makeBigButton("DOCENTE");
            JButton bAdm = makeBigButton("ADMINISTRATIVO");
            JButton bAcu = makeBigButton("ACUDIENTE");
            center.add(wrapCard(bEst));
            center.add(wrapCard(bDoc));
            center.add(wrapCard(bAdm));
            center.add(wrapCard(bAcu));
            getContentPane().add(center, BorderLayout.CENTER);

            JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER));
            JButton btnBack = makeBigButton("VOLVER");
            btnBack.setPreferredSize(new Dimension(300,50));
            bottom.add(btnBack);
            getContentPane().add(bottom, BorderLayout.SOUTH);

            bEst.addActionListener(e -> {
                dispose();
                RegistroFrame r = new RegistroFrame("ESTUDIANTE");
                r.setVisible(true);
            });
            bDoc.addActionListener(e -> {
                dispose();
                RegistroFrame r = new RegistroFrame("DOCENTE");
                r.setVisible(true);
            });
            bAdm.addActionListener(e -> {
                dispose();
                RegistroFrame r = new RegistroFrame("ADMINISTRATIVO");
                r.setVisible(true);
            });
            bAcu.addActionListener(e -> {
                dispose();
                RegistroFrame r = new RegistroFrame("ACUDIENTE");
                r.setVisible(true);
            });
            btnBack.addActionListener(e -> {
                dispose();
                InicioSesionFrame s = new InicioSesionFrame();
                s.setVisible(true);
            });
        }

        private JPanel wrapCard(JComponent comp) {
            JPanel p = new JPanel(new BorderLayout());
            p.setBorder(BorderFactory.createEmptyBorder(6,6,6,6));
            p.add(comp, BorderLayout.CENTER);
            return p;
        }
    }

    // ================== FRAME: REGISTRO ====================
    static class RegistroFrame extends JFrame {

        private final JTextField tfNombre = makeField(20);
        private final JTextField tfEdad = makeField(6);
        private final JTextField tfTelefono = makeField(14);
        private final JTextField tfEmail = makeField(20);
        private final JPasswordField pfPass = makePass(20);
        private final JPasswordField pfPass2 = makePass(20);
        private final JComboBox<String> cbRole = new JComboBox<>(new String[]{"USER","ADMIN","DOCENTE","ACUDIENTE","ESTUDIANTE"});
        private final JCheckBox cbTerms = new JCheckBox("Acepto los términos y condiciones");
        private final JButton btnRegistrar = makeBigButton("REGISTRAR");
        private final JButton btnCancelar = makeBigButton("CANCELAR");

        private final String roleInitial;

        public RegistroFrame(String role) {
            super("Registro - " + role);
            this.roleInitial = (role == null ? "USER" : role);
            setSize(WIDTH, HEIGHT);
            setResizable(false);
            setExtendedState(JFrame.MAXIMIZED_BOTH);
            setLocationRelativeTo(null);
            build();
        }

        private void build() {
            setSize(WIDTH, HEIGHT);
            setDefaultCloseOperation(EXIT_ON_CLOSE);
            setLocationRelativeTo(null);
            getContentPane().setLayout(new BorderLayout(10,10));
            getContentPane().add(makeTitle("REGISTRO DE USUARIO", 24), BorderLayout.NORTH);

            JPanel center = new JPanel(new GridBagLayout());
            center.setBorder(BorderFactory.createEmptyBorder(20, 100, 20, 100));
            GridBagConstraints c = new GridBagConstraints();
            c.insets = new Insets(8,8,8,8);
            c.fill = GridBagConstraints.HORIZONTAL;

            int row = 0;

            c.gridx = 0; c.gridy = row; center.add(new JLabel("Nombre completo:"), c);
            c.gridx = 1; center.add(tfNombre, c);
            row++;

            c.gridx = 0; c.gridy = row; center.add(new JLabel("Edad:"), c);
            c.gridx = 1; center.add(tfEdad, c);
            row++;

            c.gridx = 0; c.gridy = row; center.add(new JLabel("Teléfono (2345...):"), c);
            c.gridx = 1; center.add(tfTelefono, c);
            row++;

            c.gridx = 0; c.gridy = row; center.add(new JLabel("Correo:"), c);
            c.gridx = 1; center.add(tfEmail, c);
            row++;

            c.gridx = 0; c.gridy = row; center.add(new JLabel("Contraseña:"), c);
            c.gridx = 1; center.add(pfPass, c);
            row++;

            c.gridx = 0; c.gridy = row; center.add(new JLabel("Confirmar contraseña:"), c);
            c.gridx = 1; center.add(pfPass2, c);
            row++;

            c.gridx = 0; c.gridy = row; center.add(new JLabel("Rol:"), c);
            cbRole.setSelectedItem(roleInitial.toUpperCase());
            c.gridx = 1; center.add(cbRole, c);
            row++;

            c.gridx = 0; c.gridy = row; c.gridwidth = 2; center.add(cbTerms, c);
            row++;

            JPanel panelBtns = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 6));
            panelBtns.add(btnRegistrar); panelBtns.add(btnCancelar);
            c.gridx = 0; c.gridy = row; c.gridwidth = 2; center.add(panelBtns, c);

            getContentPane().add(center, BorderLayout.CENTER);

            btnRegistrar.addActionListener(e -> doRegister());
            btnCancelar.addActionListener(e -> {
                dispose();
                InicioSesionFrame s = new InicioSesionFrame();
                s.setVisible(true);
            });
        }

        private void doRegister() {
            try {
                String nombre = tfNombre.getText().trim();
                String sEdad = tfEdad.getText().trim();
                String telefono = tfTelefono.getText().trim();
                String email = tfEmail.getText().trim().toLowerCase();
                String pass = new String(pfPass.getPassword());
                String pass2 = new String(pfPass2.getPassword());
                String role = cbRole.getSelectedItem().toString();

                if (nombre.length() < 2) { JOptionPane.showMessageDialog(this, "Nombre inválido", "Validación", JOptionPane.WARNING_MESSAGE); return; }
                if (sEdad.isEmpty()) { JOptionPane.showMessageDialog(this, "Edad requerida", "Validación", JOptionPane.WARNING_MESSAGE); return; }
                int edad;
                try { edad = Integer.parseInt(sEdad); } catch (NumberFormatException nfe) { JOptionPane.showMessageDialog(this, "Edad debe ser numérica", "Validación", JOptionPane.WARNING_MESSAGE); return; }
                if (edad < 0 || edad > 120) { JOptionPane.showMessageDialog(this, "Edad inválida", "Validación", JOptionPane.WARNING_MESSAGE); return; }
                if (!telefono.matches("\\+?\\d{7,15}")) { JOptionPane.showMessageDialog(this, "Teléfono inválido", "Validación", JOptionPane.WARNING_MESSAGE); return; }
                if (!Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$").matcher(email).matches()) { JOptionPane.showMessageDialog(this, "Email inválido", "Validación", JOptionPane.WARNING_MESSAGE); return; }
                if (pass.length() < 6) { JOptionPane.showMessageDialog(this, "Password mínimo 6 caracteres", "Validación", JOptionPane.WARNING_MESSAGE); return; }
                if (!pass.equals(pass2)) { JOptionPane.showMessageDialog(this, "Las contraseñas no coinciden", "Validación", JOptionPane.WARNING_MESSAGE); return; }
                if (!cbTerms.isSelected()) { JOptionPane.showMessageDialog(this, "Debe aceptar los términos y condiciones", "Validación", JOptionPane.WARNING_MESSAGE); return; }

                String signUpUrl = "https://identitytoolkit.googleapis.com/v1/accounts:signUp?key=" + API_KEY;
                String bodySignUp = "{\"email\":\"" + escapeJson(email) + "\",\"password\":\"" + escapeJson(pass) + "\",\"returnSecureToken\":true}";
                String respSignUp = httpPost(signUpUrl, bodySignUp);

                String idToken = extraerCampo(respSignUp, "idToken");
                String uid = extraerCampo(respSignUp, "localId");

                if (idToken == null || uid == null) throw new RuntimeException("No se obtuvo idToken/uid: " + respSignUp);

                Usuario u = new Usuario(nombre, edad, telefono, email, role);
                u.setUid(uid);
                String json = u.toJson();

                String base = DATABASE_URL.endsWith("/") ? DATABASE_URL : DATABASE_URL + "/";
                String putUrl = base + "usuarios/" + uid + ".json?auth=" + idToken;

                String respRtdb = httpPut(putUrl, json);

                JOptionPane.showMessageDialog(this, "Registro exitoso", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                dispose();
                InicioSesionFrame f = new InicioSesionFrame();
                f.setVisible(true);
            } catch (IOException io) {
                String msg = io.getMessage();
                String firebaseMsg = extraerCampo(msg, "message");
                JOptionPane.showMessageDialog(this, "Error en registro: " + traducirError(firebaseMsg), "Registro", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Registro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // ================== FRAME: RECUPERAR CONTRASEÑA ====================
    static class RecuperarClaveFrame extends JFrame {
        private final JTextField tfEmail = makeField(24);
        private final JButton btnEnviar = makeBigButton("ENVIAR EMAIL DE RECUPERACIÓN");
        private final JButton btnVolver = makeBigButton("VOLVER");
        public RecuperarClaveFrame() {
            super("Recuperar contraseña");
            setSize(WIDTH, HEIGHT);
            setResizable(false);
            setExtendedState(JFrame.MAXIMIZED_BOTH);
            setLocationRelativeTo(null);
            build();
        }
        private void build() {
            setSize(WIDTH, HEIGHT);
            setDefaultCloseOperation(EXIT_ON_CLOSE);
            setLocationRelativeTo(null);
            getContentPane().setLayout(new BorderLayout(12,12));
            getContentPane().add(makeTitle("RECUPERAR CONTRASEÑA", 24), BorderLayout.NORTH);

            JPanel center = new JPanel(new GridBagLayout());
            center.setBorder(BorderFactory.createEmptyBorder(40,220,40,220));
            GridBagConstraints c = new GridBagConstraints();
            c.insets = new Insets(12,12,12,12);
            c.fill = GridBagConstraints.HORIZONTAL;

            c.gridx=0;c.gridy=0;center.add(new JLabel("Correo:"),c);
            c.gridx=1;c.gridy=0;center.add(tfEmail,c);
            c.gridx=0;c.gridy=1;c.gridwidth=2;center.add(btnEnviar,c);

            getContentPane().add(center, BorderLayout.CENTER);
            JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER)); bottom.add(btnVolver);
            getContentPane().add(bottom, BorderLayout.SOUTH);

            btnEnviar.addActionListener(e -> doEnviar());
            btnVolver.addActionListener(e -> { dispose(); new InicioSesionFrame().setVisible(true); });
        }
        private void doEnviar() {
            String email = tfEmail.getText().trim();
            if (!Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$").matcher(email).matches()) {
                JOptionPane.showMessageDialog(this, "Email inválido", "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                String url = "https://identitytoolkit.googleapis.com/v1/accounts:sendOobCode?key=" + API_KEY;
                String body = "{\"requestType\":\"PASSWORD_RESET\",\"email\":\"" + escapeJson(email) + "\"}";
                String resp = httpPost(url, body);
                JOptionPane.showMessageDialog(this, "Se ha enviado un correo de recuperación a: " + email, "Enviado", JOptionPane.INFORMATION_MESSAGE);
                dispose(); new InicioSesionFrame().setVisible(true);
            } catch (IOException io) {
                String msg = io.getMessage();
                String firebaseMsg = extraerCampo(msg, "message");
                if ("EMAIL_NOT_FOUND".equals(firebaseMsg)) {
                    JOptionPane.showMessageDialog(this, "Correo no encontrado", "Error", JOptionPane.ERROR_MESSAGE);
                    dispose(); new InicioSesionFrame().setVisible(true);
                    return;
                }
                JOptionPane.showMessageDialog(this, "Error al enviar correo de recuperación: " + traducirError(firebaseMsg), "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // ================== FRAME: BIENVENIDA ====================
    static class BienvenidaFrame extends JFrame {
        public BienvenidaFrame(String email) {
            super("Bienvenido");
            setSize(WIDTH, HEIGHT);
            setResizable(false);
            setExtendedState(JFrame.MAXIMIZED_BOTH);
            setLocationRelativeTo(null);
            setDefaultCloseOperation(EXIT_ON_CLOSE);
            setLocationRelativeTo(null);
            setLayout(new BorderLayout(10,10));
            JLabel h = makeTitle("BIENVENIDO", 30);
            add(h, BorderLayout.NORTH);
            JLabel lbl = new JLabel("<html><div style='text-align:center;'>Has iniciado sesión como:<br><b>" + email + "</b></div></html>", SwingConstants.CENTER);
            lbl.setFont(new Font("SansSerif", Font.PLAIN, 20));
            add(lbl, BorderLayout.CENTER);
            JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER));
            JButton btnCerrar = makeBigButton("CERRAR SESIÓN");
            btnCerrar.setPreferredSize(new Dimension(260,54));
            p.add(btnCerrar);
            add(p, BorderLayout.SOUTH);
            btnCerrar.addActionListener(e -> {
                dispose();
                InicioSesionFrame f = new InicioSesionFrame();
                f.setVisible(true);
            });
        }
    }
}