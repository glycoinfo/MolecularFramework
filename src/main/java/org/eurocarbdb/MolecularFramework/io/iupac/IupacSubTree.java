/**
 * 
 */
package org.eurocarbdb.MolecularFramework.io.iupac;

import org.eurocarbdb.MolecularFramework.sugar.GlycoEdge;
import org.eurocarbdb.MolecularFramework.sugar.GlycoNode;
import org.eurocarbdb.MolecularFramework.sugar.GlycoconjugateException;
import org.eurocarbdb.MolecularFramework.sugar.Linkage;

/**
 * @author rene
 *
 */
public class IupacSubTree
{
    private GlycoNode m_objNode = null;
    private GlycoEdge m_objEdge = null;
    
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
    
    public void addLinkage(Linkage a_objLinkage) throws GlycoconjugateException
    {
        this.m_objEdge.addGlycosidicLinkage(a_objLinkage);
    }
}
