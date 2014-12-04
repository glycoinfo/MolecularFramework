package org.eurocarbdb.MolecularFramework.util.visitor;

import org.eurocarbdb.MolecularFramework.sugar.GlycoEdge;
import org.eurocarbdb.MolecularFramework.sugar.GlycoNode;
import org.eurocarbdb.MolecularFramework.sugar.Monosaccharide;
import org.eurocarbdb.MolecularFramework.sugar.NonMonosaccharide;
import org.eurocarbdb.MolecularFramework.sugar.Substituent;
import org.eurocarbdb.MolecularFramework.sugar.Sugar;
import org.eurocarbdb.MolecularFramework.sugar.SugarUnitAlternative;
import org.eurocarbdb.MolecularFramework.sugar.SugarUnitCyclic;
import org.eurocarbdb.MolecularFramework.sugar.SugarUnitRepeat;
import org.eurocarbdb.MolecularFramework.sugar.UnvalidatedGlycoNode;
import org.eurocarbdb.MolecularFramework.util.traverser.GlycoTraverser;
import org.eurocarbdb.MolecularFramework.util.visitor.GlycoVisitor;
import org.eurocarbdb.MolecularFramework.util.visitor.GlycoVisitorException;

/**
 * @author Logan
 *
 */
public class GlycoVisitorResidueName implements GlycoVisitor
{
    private String m_strName = "";
    private boolean m_bReducing = true;
    private boolean m_bMonosaccharide = false;
    /**
     * @see org.eurocarbdb.MolecularFramework.util.visitor.GlycoVisitor#visit(org.eurocarbdb.MolecularFramework.sugar.Monosaccharide)
     */
    public void visit(Monosaccharide arg0) throws GlycoVisitorException 
    {
        this.m_strName = arg0.getGlycoCTName();
        this.m_bMonosaccharide = true;
    }

    /**
     * @see org.eurocarbdb.MolecularFramework.util.visitor.GlycoVisitor#visit(org.eurocarbdb.MolecularFramework.sugar.NonMonosaccharide)
     */
    public void visit(NonMonosaccharide arg0) throws GlycoVisitorException 
    {
        this.m_strName = arg0.getName();
    }

    /**
     * @see org.eurocarbdb.MolecularFramework.util.visitor.GlycoVisitor#visit(org.eurocarbdb.MolecularFramework.sugar.SugarUnitRepeat)
     */
    public void visit(SugarUnitRepeat arg0) throws GlycoVisitorException 
    {
        if ( this.m_bReducing )
        {
            arg0.getRepeatLinkage().getChild().accept(this);
        }
        else
        {
            arg0.getRepeatLinkage().getParent().accept(this);
        }
    }

    /**
     * @see org.eurocarbdb.MolecularFramework.util.visitor.GlycoVisitor#visit(org.eurocarbdb.MolecularFramework.sugar.Substituent)
     */
    public void visit(Substituent arg0) throws GlycoVisitorException 
    {
        this.m_strName = arg0.getSubstituentType().getName();       
    }

    /**
     * @see org.eurocarbdb.MolecularFramework.util.visitor.GlycoVisitor#visit(org.eurocarbdb.MolecularFramework.sugar.SugarUnitCyclic)
     */
    public void visit(SugarUnitCyclic arg0) throws GlycoVisitorException 
    {
        throw new GlycoVisitorException("SugarUnitCyclic is not supported by GlycoVisitorResidueName.");        
    }

    /**
     * @see org.eurocarbdb.MolecularFramework.util.visitor.GlycoVisitor#visit(org.eurocarbdb.MolecularFramework.sugar.SugarUnitAlternative)
     */
    public void visit(SugarUnitAlternative arg0) throws GlycoVisitorException 
    {
        throw new GlycoVisitorException("SugarUnitAlternative is not supported by GlycoVisitorResidueName.");       
    }

    /**
     * @see org.eurocarbdb.MolecularFramework.util.visitor.GlycoVisitor#visit(org.eurocarbdb.MolecularFramework.sugar.UnvalidatedGlycoNode)
     */
    public void visit(UnvalidatedGlycoNode arg0) throws GlycoVisitorException 
    {
        this.m_strName = arg0.getName();
    }

    /**
     * @see org.eurocarbdb.MolecularFramework.util.visitor.GlycoVisitor#visit(org.eurocarbdb.MolecularFramework.sugar.GlycoEdge)
     */
    public void visit(GlycoEdge arg0) throws GlycoVisitorException 
    {
        // nothing to do
    }

    /**
     * @see org.eurocarbdb.MolecularFramework.util.visitor.GlycoVisitor#start(org.eurocarbdb.MolecularFramework.sugar.Sugar)
     */
    public void start(Sugar arg0) throws GlycoVisitorException 
    {
        throw new GlycoVisitorException("GlycoVisitorResidueName works only for GlycoNodes use .start(GlycoNode).");
    }

    /**
     * @see org.eurocarbdb.MolecularFramework.util.visitor.GlycoVisitor#getTraverser(org.eurocarbdb.MolecularFramework.util.visitor.GlycoVisitor)
     */
    public GlycoTraverser getTraverser(GlycoVisitor arg0) throws GlycoVisitorException 
    {
        return null;
    }

    /**
     * @see org.eurocarbdb.MolecularFramework.util.visitor.GlycoVisitor#clear()
     */
    public void clear() 
    {
        this.m_strName = null;
    }

    public String start(GlycoNode a_objNode, boolean a_bReducing) throws GlycoVisitorException
    {
        this.m_bMonosaccharide = false;
        this.m_bReducing = a_bReducing;
        a_objNode.accept(this);
        return this.m_strName;
    }

    /**
     * @return
     */
    public boolean isMonosaccharide()
    {
        return this.m_bMonosaccharide;
    }
}
