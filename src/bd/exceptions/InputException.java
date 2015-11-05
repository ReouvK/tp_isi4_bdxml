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
public class InputException extends Exception {

    /**
     * Creates a new instance of <code>configException</code> without detail
     * message.
     */
    public InputException() {
    }

    /**
     * Constructs an instance of <code>configException</code> with the specified
     * detail message.
     *
     * @param msg the detail message.
     */
    public InputException(String msg) {
        super(msg);
    }

}
