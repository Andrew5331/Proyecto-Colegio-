import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.*;
 
public class LoginFirebase extends JFrame {
 
    // --- UI ---
    private final JTextField tfEmail = new JTextField(22);
    private final JPasswordField pfPass = new JPasswordField(22);
    private final JButton btnLogin = new JButton("Ingresar");
    private final JButton btnIrRegistro = new JButton("Crear cuenta");
 
    // --- Config ---
    private String apiKey; // leído de conexion.txt
 
    public LoginFirebase() {
        super("Login - Firebase Auth");
        setLayout(new BorderLayout(8, 8));
 
        cargarConfig(); // lee conexion.txt
 
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.fill = GridBagConstraints.HORIZONTAL;
 
        int r = 0;
        addRow(form, c, r++, "Email:", tfEmail);
        addRow(form, c, r++, "Password:", pfPass);
 
        // Barra inferior con botón a la izquierda (registro) y a la derecha (login)
        JPanel south = new JPanel(new BorderLayout());
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        left.add(btnIrRegistro);
        right.add(btnLogin);
        south.add(left, BorderLayout.WEST);
        south.add(right, BorderLayout.EAST);
 
        add(form, BorderLayout.CENTER);
        add(south, BorderLayout.SOUTH);
 
        btnLogin.addActionListener(this::login);
        btnIrRegistro.addActionListener(ev -> {
            // Abre el formulario de registro
            new Firebase().setVisible(true);
           
            // dispose();
        });
 
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
    }
 
    private void addRow(JPanel p, GridBagConstraints c, int row, String label, JComponent field) {
        c.gridx = 0; c.gridy = row; c.weightx = 0; p.add(new JLabel(label), c);
        c.gridx = 1; c.gridy = row; c.weightx = 1; p.add(field, c);
    }
 
    private void cargarConfig() {
        try {
            Map<String, String> cfg = leerConfig("conexion.txt");
            apiKey = cfg.get("apiKey");
            if (apiKey == null) {
                throw new IllegalStateException("Falta apiKey en conexion.txt");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error leyendo conexion.txt: " + ex.getMessage(),
                    "Config", JOptionPane.ERROR_MESSAGE);
        }
    }
 
    private void login(ActionEvent e) {
        try {
            String email = tfEmail.getText().trim();
            String pass = new String(pfPass.getPassword());
 
            if (!Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$").matcher(email).matches())
                throw new IllegalArgumentException("Email inválido");
            if (pass.length() < 6) throw new IllegalArgumentException("Password mínimo 6 caracteres");
 
            // Endpoint de login (signInWithPassword)
            String url = "https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=" + apiKey;
            String body = "{\"email\":\"" + escapeJson(email) + "\",\"password\":\"" + escapeJson(pass) + "\",\"returnSecureToken\":true}";
 
            String resp = httpPost(url, body);
            String idToken = extraerCampo(resp, "idToken");
            String uid     = extraerCampo(resp, "localId");
 
            if (idToken == null || uid == null) {
                throw new RuntimeException("No se obtuvo idToken/uid. Respuesta: " + resp);
            }
 
            // Abrir ventana de bienvenida
            SwingUtilities.invokeLater(() -> {
                new BienvenidaFrame(email).setVisible(true);
                dispose(); // cierra el login
            });
 
        } catch (IOException io) {
            String msg = io.getMessage();
            String firebaseMsg = extraerCampo(msg, "message");
            // Si el email no existe, ofrece abrir el registro
            if ("EMAIL_NOT_FOUND".equals(firebaseMsg)) {
                int opt = JOptionPane.showConfirmDialog(this,
                        "Ese email no está registrado.\n¿Deseas crear la cuenta ahora?",
                        "Usuario no encontrado",
                        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                if (opt == JOptionPane.YES_OPTION) {
                    new Firebase().setVisible(true);
                   
                }
                return;
            }
            JOptionPane.showMessageDialog(this,
                    "Error de autenticación: " + traducirError(firebaseMsg),
                    "Login", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error: " + ex.getMessage(),
                    "Login", JOptionPane.ERROR_MESSAGE);
        }
    }
 
    // =========== Ventana de Bienvenida ===========
    static class BienvenidaFrame extends JFrame {
        BienvenidaFrame(String email) {
            super("Bienvenidos");
            setLayout(new BorderLayout());
            JLabel title = new JLabel("¡Bienvenidos!", SwingConstants.CENTER);
            title.setFont(title.getFont().deriveFont(Font.BOLD, 22f));
            JLabel user = new JLabel(email, SwingConstants.CENTER);
            user.setFont(user.getFont().deriveFont(Font.PLAIN, 16f));
            add(title, BorderLayout.CENTER);
            add(user, BorderLayout.SOUTH);
            setSize(320, 180);
            setLocationRelativeTo(null);
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        }
    }
 
    // =========== Helpers HTTP/JSON/CFG ===========
    private static String httpPost(String urlStr, String jsonBody) throws IOException {
        URL url = new URL(urlStr);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setConnectTimeout(15000);
        con.setReadTimeout(15000);
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        con.setDoOutput(true);
        try (OutputStream os = con.getOutputStream()) {
            byte[] out = jsonBody.getBytes(StandardCharsets.UTF_8);
            os.write(out);
            os.flush();
        }
        int code = con.getResponseCode();
        InputStream is = (code >= 200 && code < 300) ? con.getInputStream() : con.getErrorStream();
        String resp = readAll(is);
        if (code < 200 || code >= 300) {
            throw new IOException("HTTP " + code + ": " + resp);
        }
        return resp;
    }
 
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
        Pattern p = Pattern.compile("\"" + Pattern.quote(campo) + "\"\\s*:\\s*\"(.*?)\"");
        Matcher m = p.matcher(json);
        return m.find() ? m.group(1) : null;
    }
 
    private static Map<String, String> leerConfig(String path) throws IOException {
        Map<String, String> map = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                int eq = line.indexOf('=');
                if (eq > 0) {
                    String k = line.substring(0, eq).trim();
                    String v = line.substring(eq + 1).trim();
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
 
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFirebase().setVisible(true));
    }
}