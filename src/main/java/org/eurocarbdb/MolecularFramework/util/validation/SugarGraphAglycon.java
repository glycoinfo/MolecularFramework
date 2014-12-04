/**
 * 
 */
package org.eurocarbdb.MolecularFramework.util.validation;

import org.eurocarbdb.MolecularFramework.sugar.GlycoEdge;
import org.eurocarbdb.MolecularFramework.sugar.GlycoNode;

/**
 * @author rene
 *
 */
public class SugarGraphAglycon
{
    private GlycoNode m_objAglyca = null;
    private GlycoNode m_objConnectedNode = null;
    private GlycoEdge m_objAglycaEdge = null;
    
    public SugarGraphAglycon( GlycoNode a_objAglyca , GlycoNode a_objReducing, GlycoEdge a_objAglycaEdge)
    {
        super();
        this.m_objAglyca = a_objAglyca;
        this.m_objConnectedNode = a_objReducing;
        this.m_objAglycaEdge = a_objAglycaEdge;
    }

    public void setAglyca(GlycoNode a_strAglyca)
    {
        this.m_objAglyca = a_strAglyca;
    }
    
    public GlycoNode getAglyca()
    {
        return this.m_objAglyca;
    }
    
    public void setAglycaEdge(GlycoEdge a_objEdge)
    {
        this.m_objAglycaEdge = a_objEdge;
    }
    
    public GlycoEdge getAglycaEdge()
    {
        return this.m_objAglycaEdge;
    }
    
    public void setConnectedGlycoNode(GlycoNode a_objNode)
    {
    	this.m_objConnectedNode = a_objNode;
    }
    
    public GlycoNode getConnectedGlycoNode()
    {
    	return this.m_objConnectedNode;
    }
}
