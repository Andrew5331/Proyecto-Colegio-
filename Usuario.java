import java.util.regex.Pattern;
public class Usuario {
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
    public void setUid(String v){
        this.uid = (v == null || v.trim().isEmpty()) ? null : v;
    }
    public void setNombre(String v){
        if(v == null || v.trim().length()<2) throw new IllegalArgumentException("Nombre inválido");{
        this.nombre = v.trim();
        }
    }
    public void setEdad(int v){
        if(v < 0|| v > 120) throw new IllegalArgumentException("Edad inválida");
        this.edad = v;
    }
    public void setTelefono(String v){
        if(v == null || !v.matches("\\+?\\d{7,15}")) throw new IllegalArgumentException("Teléfono inválido");
        this.telefono = v.trim();
    }
    public void setEmail(String v){
        Pattern p  = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
        if(v == null || !p.matcher(v).matches()) throw new IllegalArgumentException("Email inválido");
        this.email = v.trim().toLowerCase();
    }
    public void setRole(String v){
        String r = (v==null ? "" : v.trim().toUpperCase());
        this.role = r.isEmpty() ? "USER" : r;
    }
 
    public void setActivo(boolean activo){this.activo = activo;}
    public void setCreatedAt(long createdAt){this.createdAt = createdAt;}
 
    public String getUid(){return this.uid;}
    public String getNombre(){return this.nombre;}
    public int getEdad(){return this.edad;}
    public String getTelefono(){return this.telefono;}
    public String getEmail(){return this.email;}
    public String getRole(){return this.role;}
    public boolean isActivo(){return this.activo;}
    public long getCreatedAt(){return this.createdAt;}
 
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
        if(value == null){
            sb.append("null");
        }
        if(isString){
            sb.append("\"").append(escapeJson(value)).append("\"");
        } else {
            sb.append(value);
        }
    }
    public static String escapeJson(String s){
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