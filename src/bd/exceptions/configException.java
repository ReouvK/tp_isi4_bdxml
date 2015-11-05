/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bd.exceptions;

/**
 *
 * @author pulpito
 */
public class configException extends Exception {

    /**
     * Creates a new instance of <code>configException</code> without detail
     * message.
     */
    public configException() {
    }

    /**
     * Constructs an instance of <code>configException</code> with the specified
     * detail message.
     *
     * @param msg the detail message.
     */
    public configException(String msg) {
        super(msg);
    }
}
