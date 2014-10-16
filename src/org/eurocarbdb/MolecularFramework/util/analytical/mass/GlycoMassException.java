package org.eurocarbdb.MolecularFramework.util.analytical.mass;

import org.eurocarbdb.MolecularFramework.util.visitor.GlycoVisitorException;



/**
 * @author rene
 *
 */
public class GlycoMassException extends GlycoVisitorException
{

    /**
     * @param message
     */
    public GlycoMassException(String a_strMessage)
    {
        super(a_strMessage);
        this.m_strMessage = a_strMessage;
    }

    /**
     * @param message
     */
    public GlycoMassException(String a_strMessage,Throwable a_objThrowable)
    {
        super(a_strMessage,a_objThrowable);
        this.m_strMessage = a_strMessage;
    }

    public String getErrorMessage()
    {
        return this.m_strMessage;
    }
    
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

}
