/**
 * 
 */
package org.eurocarbdb.MolecularFramework.sugar;

/**
 * @author rene
 *
 */
public class GlycoconjugateException extends Exception
{
    public GlycoconjugateException(String a_strMessage,Throwable a_objThrow)
    {
        super(a_strMessage,a_objThrow);
    }

    public GlycoconjugateException(String a_strMessage)
    {
        super(a_strMessage);
    }

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

}
