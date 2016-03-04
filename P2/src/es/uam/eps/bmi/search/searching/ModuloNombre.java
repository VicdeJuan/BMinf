package es.uam.eps.bmi.search.searching;

/**
 *
 * @author e267044
 */
public class ModuloNombre {
    String nombre;
    double modulo;
    
    public ModuloNombre(String nombre, double modulo){
        this.nombre = nombre;
        this.modulo = modulo;
    }

    public String getNombre() {
        return nombre;
    }

    public double getModulo() {
        return modulo;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setModulo(double modulo) {
        this.modulo = modulo;
    }

    public void updateModulo(double module) {
        this.modulo += module;
    }
    
    
    
}
