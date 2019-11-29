package io.github.senthilganeshs.bdmcicd;

public final class ClientException extends Exception {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public ClientException (Throwable e) {
        super(e);
    }
    
    ClientException (final String msg) {
        super(msg);
    }
}