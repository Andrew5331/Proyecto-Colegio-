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
 
public class Firebase extends JFrame {
 
    // ------- UI -------
    private final JTextField tfNom   = new JTextField(18);
    private final JTextField tfEdad  = new JTextField(5);
    private final JTextField tfTel   = new JTextField(14);
    private final JTextField tfEmail = new JTextField(20);
    private final JPasswordField pfPass = new JPasswordField(20);
    private final JComboBox<String> cbRole =
            new JComboBox<>(new String[]{"USER", "ADMIN", "DOCENTE", "ACUDIENTE", "ESTUDIANTE"});
    private final JButton btnRegistrar = new JButton("Registrar");
 
    // ------- Config -------
    private String apiKey;
    private String databaseUrl;
 
    public Firebase() {
        super("Registro Firebase (Auth+RTDB)");
        setLayout(new BorderLayout(8,8));
 
        cargarConfig(); // lee conexion.txt
 
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5,5,5,5);
        c.fill = GridBagConstraints.HORIZONTAL;
 
        int r=0;
        addRow(form, c, r++, "Nombre:", tfNom);
        addRow(form, c, r++, "Edad:", tfEdad);
        addRow(form, c, r++, "Teléfono:", tfTel);
        addRow(form, c, r++, "Email:", tfEmail);
        addRow(form, c, r++, "Password:", pfPass);
        addRow(form, c, r++, "Rol:", cbRole);
 
        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        south.add(btnRegistrar);
 
        add(form, BorderLayout.CENTER);
        add(south, BorderLayout.SOUTH);
 
        btnRegistrar.addActionListener(this::registrar);
 
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
    }
 
    private void cargarConfig() {
        try {
            Map<String,String> cfg = leerConfig("conexion.txt");
            apiKey = cfg.get("apiKey");
            databaseUrl = cfg.get("databaseUrl");
            if (apiKey == null || databaseUrl == null)
                throw new IllegalStateException("Faltan apiKey o databaseUrl en conexion.txt");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error leyendo conexion.txt: " + ex.getMessage(),
                    "Configuración", JOptionPane.ERROR_MESSAGE);
        }
    }
 
    private void addRow(JPanel p, GridBagConstraints c, int row, String label, JComponent field){
        c.gridx=0; c.gridy=row; c.weightx=0; p.add(new JLabel(label), c);
        c.gridx=1; c.gridy=row; c.weightx=1; p.add(field, c);
    }
 
    private void registrar(ActionEvent e) {
        try {
            // 1) Validar campos simples
            String nom = tfNom.getText().trim();
            String sEdad = tfEdad.getText().trim();
            String tel = tfTel.getText().trim();
            String email = tfEmail.getText().trim();
            String pass = new String(pfPass.getPassword());
            String role = cbRole.getSelectedItem().toString();
 
            if (nom.length() < 2) throw new IllegalArgumentException("Nombre inválido");
            if (sEdad.isEmpty()) throw new IllegalArgumentException("Edad requerida");
            int edad = Integer.parseInt(sEdad);
            if (edad < 0 || edad > 120) throw new IllegalArgumentException("Edad inválida");
            if (!tel.matches("\\+?\\d{7,15}")) throw new IllegalArgumentException("Teléfono inválido");
            if (!Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$").matcher(email).matches())
                throw new IllegalArgumentException("Email inválido");
            if (pass.length() < 6) throw new IllegalArgumentException("Password mínimo 6 caracteres");
 
            // 2) SignUp
            String signUpUrl = "https://identitytoolkit.googleapis.com/v1/accounts:signUp?key=" + apiKey;
            String bodySignUp = "{\"email\":\"" + escapeJson(email) + "\",\"password\":\"" + escapeJson(pass) + "\",\"returnSecureToken\":true}";
 
            String respSignUp = httpPost(signUpUrl, bodySignUp);
            String idToken = extraerCampo(respSignUp, "idToken");
            String uid     = extraerCampo(respSignUp, "localId");
 
            if (idToken == null || uid == null)
                throw new RuntimeException("No se obtuvo idToken/uid: " + respSignUp);
 
            // 3) Construir perfil y guardar en RTDB
            Usuario u = new Usuario(nom, edad, tel, email.toLowerCase(), role);
            u.setUid(uid);
            String json = u.toJson();
 
            String base = databaseUrl.endsWith("/") ? databaseUrl : databaseUrl + "/";
            String putUrl = base + "usuarios/" + uid + ".json?auth=" + idToken;
 
            // Debug en consola
            System.out.println("PUT URL: " + putUrl);
            System.out.println("JSON a enviar:\n" + json);
 
            String respRtdb = httpPut(putUrl, json);
            System.out.println("Respuesta RTDB: " + respRtdb);
 
            JOptionPane.showMessageDialog(this,
                    "Usuario creado con UID:\n" + uid,
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
 
            limpiar();
 
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "Edad debe ser numérica", "Validación", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error: " + ex.getMessage(),
                    "Registro", JOptionPane.ERROR_MESSAGE);
        }
    }
 
    private void limpiar() {
        tfNom.setText("");
        tfEdad.setText("");
        tfTel.setText("");
        tfEmail.setText("");
        pfPass.setText("");
        cbRole.setSelectedIndex(0);
    }
 
    // ------------ Helpers -------------
    private static String httpPost(String urlStr, String jsonBody) throws IOException {
        return httpWrite("POST", urlStr, jsonBody);
    }
    private static String httpPut(String urlStr, String jsonBody) throws IOException {
        return httpWrite("PUT", urlStr, jsonBody);
    }
    private static String httpWrite(String method, String urlStr, String jsonBody) throws IOException {
        URL url = new URL(urlStr);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setConnectTimeout(15000);
        con.setReadTimeout(15000);
        con.setRequestMethod(method);
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
        if (code < 200 || code >= 300) throw new IOException("HTTP " + code + ": " + resp);
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
 
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Firebase().setVisible(true));
    }
}
