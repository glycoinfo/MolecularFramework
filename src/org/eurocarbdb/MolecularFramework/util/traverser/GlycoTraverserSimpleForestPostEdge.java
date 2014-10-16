package org.eurocarbdb.MolecularFramework.util.traverser;

import java.util.ArrayList;
import java.util.Iterator;

import org.eurocarbdb.MolecularFramework.sugar.GlycoEdge;
import org.eurocarbdb.MolecularFramework.sugar.GlycoGraph;
import org.eurocarbdb.MolecularFramework.sugar.GlycoNode;
import org.eurocarbdb.MolecularFramework.sugar.GlycoconjugateException;
import org.eurocarbdb.MolecularFramework.util.visitor.GlycoVisitor;
import org.eurocarbdb.MolecularFramework.util.visitor.GlycoVisitorException;

public class GlycoTraverserSimpleForestPostEdge extends GlycoTraverser
{
    public GlycoTraverserSimpleForestPostEdge(GlycoVisitor a_objVisitor) throws GlycoVisitorException
    {
        super(a_objVisitor);
    }
    
    public void traverse(GlycoNode a_objResidue) throws GlycoVisitorException
    {
        a_objResidue.accept(this.m_objVisitor);
        // traverse subtree
        for (Iterator<GlycoEdge> t_iterLinkages = a_objResidue.getChildEdges().iterator(); t_iterLinkages.hasNext();) 
        {
            GlycoEdge t_linkChild = t_iterLinkages.next();
            //t_linkChild.accept(this.m_objVisitor);
            this.traverse(t_linkChild);
        }
    }

    public void traverse(GlycoEdge a_objLinkage) throws GlycoVisitorException
    {
        // traverse subtree
        this.traverse(a_objLinkage.getChild());
        // callback of the function after subtree 
        a_objLinkage.accept(this.m_objVisitor);
    }

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

}
