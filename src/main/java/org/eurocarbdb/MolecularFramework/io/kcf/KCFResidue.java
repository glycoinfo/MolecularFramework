package org.eurocarbdb.MolecularFramework.io.kcf;

import org.eurocarbdb.MolecularFramework.sugar.UnvalidatedGlycoNode;

/**
 * @author rene
 *
 */
public class KCFResidue
{
    private UnvalidatedGlycoNode m_objResidue = null;
    private double m_dX = 0;
    private double m_dY = 0;
    private int m_iID = 0;
    
    public void init( UnvalidatedGlycoNode a_objResidue, double a_dX , double a_dY , int a_iId)
    {
        this.m_objResidue = a_objResidue;
        this.m_dX = a_dX;
        this.m_dY = a_dY;
        this.m_iID = a_iId;
    }
    
    public double getX()
    {
        return this.m_dX;
    }
    
    public double getY()
    {
        return this.m_dY;
    }
    
    public UnvalidatedGlycoNode getResidue()
    {
        return this.m_objResidue;
    }
    
    public int getId()
    {
    	return this.m_iID;
    }
}
