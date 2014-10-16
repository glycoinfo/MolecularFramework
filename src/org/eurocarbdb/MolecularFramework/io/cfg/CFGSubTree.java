/**
 * 
 */
package org.eurocarbdb.MolecularFramework.io.cfg;

import org.eurocarbdb.MolecularFramework.sugar.GlycoEdge;
import org.eurocarbdb.MolecularFramework.sugar.GlycoNode;

/**
 * @author rene
 *
 */
public class CFGSubTree
{
    private GlycoNode m_objNode = null;
    private GlycoEdge m_objEdge = null;
    private Integer m_iId = null;
    
    public void setId(Integer a_iId)
    {
    	this.m_iId = a_iId;
    }
    
    public Integer getId()
    {
    	return this.m_iId;
    }
    
    public void setGlycoEdge(GlycoEdge a_objEdge)
    {
        this.m_objEdge = a_objEdge;
    }
    
    public GlycoEdge getGlycoEdge()
    {
        return this.m_objEdge;
    }

    public void setGlycoNode(GlycoNode a_objNode)
    {
        this.m_objNode = a_objNode;
    }
    
    public GlycoNode getGlycoNode()
    {
        return this.m_objNode;
    }
}
