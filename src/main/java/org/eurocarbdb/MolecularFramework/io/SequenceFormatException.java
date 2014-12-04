/**
 * 
 */
package org.eurocarbdb.MolecularFramework.io;

/**
 * @author Logan
 *
 */
public class SequenceFormatException extends Exception 
{
    public SequenceFormatException(String a_strMessage,Throwable a_objThrow)
    {
        super(a_strMessage,a_objThrow);
    }

    public SequenceFormatException(String a_strMessage)
    {
        super(a_strMessage);
    }

    /**
     * 
     */
    private static final long serialVersionUID = 1L;


}
