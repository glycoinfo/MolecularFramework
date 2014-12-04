/**
 * 
 */
package org.eurocarbdb.MolecularFramework.sugar;

import java.util.ArrayList;

import org.eurocarbdb.MolecularFramework.util.visitor.GlycoVisitor;
import org.eurocarbdb.MolecularFramework.util.visitor.GlycoVisitorException;

/**
 * @author rene
 *
 */
public class SugarUnitCyclic extends GlycoNode
{
    private GlycoNode m_objCyclicStart = null;    
    
    protected SugarUnitCyclic(GlycoNode a_objStart)
    {
        this.m_objCyclicStart = a_objStart;
    }
    
    /**
     * @see org.eurocarbdb.MolecularFramework.util.visitor.Visitable#accept(org.eurocarbdb.MolecularFramework.util.visitor.GlycoVisitor)
     */
    public void accept(GlycoVisitor a_objVisitor) throws GlycoVisitorException
    {
        a_objVisitor.visit(this);        
    }

    public GlycoNode getCyclicStart()
    {
        return this.m_objCyclicStart;
    }
    
    public void setCyclicStart(GlycoNode a_objNode) throws GlycoconjugateException
    {
        if ( a_objNode == null )
        {
            throw new GlycoconjugateException("Invalide value for cyclic residue.");
        }
        this.m_objCyclicStart = a_objNode;
    }
    
    protected void setChildEdge(ArrayList<GlycoEdge> a_aChilds) throws GlycoconjugateException
    {
        throw new GlycoconjugateException("Cyclic objects can not have childs.");
    }
    
    protected boolean addChildEdge(GlycoEdge a_linkSubStructure) throws GlycoconjugateException 
    {
        throw new GlycoconjugateException("Cyclic objects can not have childs.");
    }

    /**
     * Copy the SugarUnitCyclic. Start residue is the SAME residue
     */
    public SugarUnitCyclic copy() throws GlycoconjugateException
    {
        return new SugarUnitCyclic( this.m_objCyclicStart );
    }    
}
