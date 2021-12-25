package src.SGFX;

public interface Buf extends Resource, Bindable {
    public BufFmt getFmt();
    public int getTarget();
    public int getLen();

    public interface Storage extends Buf { 
        public void set_binding(int binding);
    }
    public interface Attrib extends Buf { }
    public interface Index extends Buf { }
    public interface Uniform extends Buf { }  
}