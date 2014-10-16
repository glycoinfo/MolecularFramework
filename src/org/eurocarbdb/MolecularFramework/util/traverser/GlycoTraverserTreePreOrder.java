/**
 * 
 */
package org.eurocarbdb.MolecularFramework.util.traverser;

import java.util.ArrayList;
import java.util.Iterator;

import org.eurocarbdb.MolecularFramework.sugar.GlycoEdge;
import org.eurocarbdb.MolecularFramework.sugar.GlycoGraph;
import org.eurocarbdb.MolecularFramework.sugar.GlycoNode;
import org.eurocarbdb.MolecularFramework.sugar.GlycoconjugateException;
import org.eurocarbdb.MolecularFramework.util.visitor.GlycoVisitor;
import org.eurocarbdb.MolecularFramework.util.visitor.GlycoVisitorException;

/**
 * @author Logan
 *
 */
public class GlycoTraverserTreePreOrder extends GlycoTraverser
{
    public GlycoTraverserTreePreOrder(GlycoVisitor a_objVisitor) throws GlycoVisitorException
    {
        super(a_objVisitor);
    }

    /**
     * @see de.glycosciences.MolecularFrameWork.util.SugarTraverser#traverse(de.glycosciences.MolecularFrameWork.sugar.Residue)
     */
    @Override
    public void traverse(GlycoNode a_objResidue) throws GlycoVisitorException
    {
    	// callback before subtree
    	a_objResidue.accept(this.m_objVisitor);
    	// traverse subtree
    	for (Iterator<GlycoEdge> t_iterLinkages = a_objResidue.getChildEdges().iterator(); t_iterLinkages.hasNext();) 
    	{
    		this.traverse(t_iterLinkages.next());
    	}
    }

    /**
     * @see de.glycosciences.MolecularFrameWork.util.SugarTraverser#traverse(de.glycosciences.MolecularFrameWork.Linkage)
     */
    @Override
    public void traverse(GlycoEdge a_objLinkage) throws GlycoVisitorException
    {
        // callback of the function before subtree 
        a_objLinkage.accept(this.m_objVisitor);
        // traverse subtree
        this.traverse(a_objLinkage.getChild());
    }

    /**
     * @see de.glycosciences.MolecularFrameWork.util.SugarTraverser#traverseGraph(de.glycosciences.MolecularFrameWork.sugar.Sugar)
     */
    @Override
    public void traverseGraph(GlycoGraph a_objSugar) throws GlycoVisitorException
    {
        ArrayList<GlycoNode> t_aRoot;
        try
        {
            t_aRoot = a_objSugar.getRootNodes();
            Iterator<GlycoNode> t_objIterator = t_aRoot.iterator();
            while ( t_objIterator.hasNext() )
            {
                this.traverse(t_objIterator.next());
            }
        } 
        catch (GlycoconjugateException e)
        {
            throw new GlycoVisitorException(e.getMessage(),e);
        }
    }

	/**
	 * @param node
	 */
	public void traverseResidue(GlycoNode a_objNode) throws GlycoVisitorException 
	{
        this.traverse(a_objNode);
	}
}