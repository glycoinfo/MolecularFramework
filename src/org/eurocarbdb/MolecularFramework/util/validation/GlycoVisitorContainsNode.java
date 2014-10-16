package org.eurocarbdb.MolecularFramework.util.validation;

import java.util.Iterator;

import org.eurocarbdb.MolecularFramework.sugar.GlycoEdge;
import org.eurocarbdb.MolecularFramework.sugar.GlycoGraphAlternative;
import org.eurocarbdb.MolecularFramework.sugar.GlycoNode;
import org.eurocarbdb.MolecularFramework.sugar.Monosaccharide;
import org.eurocarbdb.MolecularFramework.sugar.NonMonosaccharide;
import org.eurocarbdb.MolecularFramework.sugar.Substituent;
import org.eurocarbdb.MolecularFramework.sugar.Sugar;
import org.eurocarbdb.MolecularFramework.sugar.SugarUnitAlternative;
import org.eurocarbdb.MolecularFramework.sugar.SugarUnitCyclic;
import org.eurocarbdb.MolecularFramework.sugar.SugarUnitRepeat;
import org.eurocarbdb.MolecularFramework.sugar.UnderdeterminedSubTree;
import org.eurocarbdb.MolecularFramework.sugar.UnvalidatedGlycoNode;
import org.eurocarbdb.MolecularFramework.util.traverser.GlycoTraverser;
import org.eurocarbdb.MolecularFramework.util.traverser.GlycoTraverserNodes;
import org.eurocarbdb.MolecularFramework.util.visitor.GlycoVisitor;
import org.eurocarbdb.MolecularFramework.util.visitor.GlycoVisitorException;

/**
 * Count all types of residues. 
 * 
 * @author rene
 */
public class GlycoVisitorContainsNode implements GlycoVisitor
{
    private int m_iMonosaccharide = 0;
    private int m_iNonMonosaccharide = 0;
    private int m_iNonTerminalNonMonosaccharide = 0;
    private int m_iSubstituent = 0;
    private int m_iRepeat = 0;
    private int m_iUnvalidated = 0;
    private int m_iUnderdetermined = 0;
    private int m_iAlternative = 0;
    private int m_iCyclic = 0;
    private boolean m_bDescent = true;
    private boolean m_bDescentUnderdeterminded = true;
    private String m_strNonMSnames = "";
    private SugarUnitRepeat m_objRepeat = null;
    
    public String getNonMsNames()
    {
        return this.m_strNonMSnames;
    }
    
    public void setDescent(boolean a_bDescent)
    {
    	this.m_bDescent = a_bDescent;
    }
    
    public void setDescentUnderdeterminded(boolean a_bDescent)
    {
    	this.m_bDescentUnderdeterminded = a_bDescent;
    }

    public int getMonosaccharideCount()
    {
    	return this.m_iMonosaccharide;
    }
    
    public int getNonMonosaccharideCount()
    {
    	return this.m_iNonMonosaccharide;
    }
    
    public int getNonTerminalNonMonosaccharideCount()
    {
    	return this.m_iNonTerminalNonMonosaccharide;
    }
    
    public int getSubstituentCount()
    {
    	return this.m_iSubstituent;
    }
    
    public int getRepeatCount()
    {
    	return this.m_iRepeat;
    }
    
    public int getUnvalidatedCount()
    {
    	return this.m_iUnvalidated;
    }
    
    public int getUnderdetermindedCount()
    {
    	return this.m_iUnderdetermined;
    }
    
    public int getAlternativeCount()
    {
    	return this.m_iAlternative;
    }
    
    public int getCyclicCount()
    {
    	return this.m_iCyclic;
    }
    
    public void visit(Monosaccharide arg0) throws GlycoVisitorException
    {
        this.m_iMonosaccharide++;
    }

    public void visit(NonMonosaccharide a_objNonMS) throws GlycoVisitorException
    {
        this.m_iNonMonosaccharide++;
        if ( this.m_objRepeat != null )
        {
        	GlycoEdge t_objEdge = this.m_objRepeat.getRepeatLinkage();
        	if ( a_objNonMS == t_objEdge.getParent() || a_objNonMS == t_objEdge.getChild() )
        	{
        		this.m_iNonTerminalNonMonosaccharide++;
        	}
        	else
        	{
        		// test if child is in repeat linkage
        		if ( this.childInRepeat(a_objNonMS,t_objEdge.getParent()) )
        		{
        			this.m_iNonTerminalNonMonosaccharide++;
        		}
        	}
        }
    }

    /**
	 * @param nonMS
	 * @param repeat
	 * @return
	 */
	private boolean childInRepeat(GlycoNode a_objResidue, GlycoNode a_objRepeatOut) 
	{
		for (Iterator<GlycoEdge> t_iterChild = a_objResidue.getChildEdges().iterator(); t_iterChild.hasNext();) 
		{
			GlycoEdge t_objEdge = t_iterChild.next();
			if ( t_objEdge.getChild() == a_objRepeatOut )
			{
				return true;
			}
			else
			{
				if ( this.childInRepeat(t_objEdge.getChild(), a_objRepeatOut) )
				{
					return true;
				}
			}
		}
		return false;
	}

	public void visit(GlycoEdge arg0) throws GlycoVisitorException
    {
    	// do nothing
    }

    public void visit(SugarUnitRepeat a_objRepeat) throws GlycoVisitorException
    {
        this.m_iRepeat++;
        this.m_iUnderdetermined += a_objRepeat.getUndeterminedSubTrees().size();
        if ( this.m_bDescent )
        {
        	SugarUnitRepeat t_objBefore = this.m_objRepeat;
	        GlycoTraverser t_objTraverser = this.getTraverser(this);
	        this.m_objRepeat = a_objRepeat;
	        t_objTraverser.traverseGraph(a_objRepeat);
	        this.m_objRepeat = t_objBefore;
	        if ( this.m_bDescentUnderdeterminded )
	        {
	        	for (Iterator<UnderdeterminedSubTree> t_iterUnder = a_objRepeat.getUndeterminedSubTrees().iterator(); t_iterUnder.hasNext();)
	        	{
	        		t_objTraverser = this.getTraverser(this);
	        		t_objTraverser.traverseGraph(t_iterUnder.next());
	        	}
	        }
        }
    }

    public GlycoTraverser getTraverser(GlycoVisitor a_objVisitor) throws GlycoVisitorException
    {
        return new GlycoTraverserNodes(a_objVisitor);
    }

    public void clear()
    {
        this.m_iMonosaccharide = 0;
        this.m_iNonMonosaccharide = 0;
        this.m_iRepeat = 0;
        this.m_iSubstituent = 0;
        this.m_iUnvalidated = 0;
        this.m_iUnderdetermined = 0;
        this.m_iAlternative = 0;
        this.m_iCyclic = 0;
        this.m_iNonTerminalNonMonosaccharide = 0;
        this.m_strNonMSnames = "";
        this.m_objRepeat = null;
    }

	/**
	 * @see org.eurocarbdb.MolecularFramework.util.visitor.GlycoVisitor#visit(org.eurocarbdb.MolecularFramework.sugar.Substituent)
	 */
	public void visit(Substituent a_objSubstituent) throws GlycoVisitorException 
	{
		this.m_iSubstituent++;
	}

	/**
	 * @see org.eurocarbdb.MolecularFramework.util.visitor.GlycoVisitor#visit(org.eurocarbdb.MolecularFramework.sugar.SugarUnitCyclic)
	 */
	public void visit(SugarUnitCyclic a_objCyclic) throws GlycoVisitorException 
	{
		this.m_iCyclic++;
	}

	/**
	 * @see org.eurocarbdb.MolecularFramework.util.visitor.GlycoVisitor#visit(org.eurocarbdb.MolecularFramework.sugar.SugarUnitAlternative)
	 */
	public void visit(SugarUnitAlternative a_objAlternative) throws GlycoVisitorException 
	{
        this.m_iAlternative++;
		if ( this.m_bDescent )
		{
			GlycoTraverser t_objTraverser;
			for (Iterator<GlycoGraphAlternative> t_iterAlt = a_objAlternative.getAlternatives().iterator(); t_iterAlt.hasNext();)
			{
				GlycoGraphAlternative t_objAlternative = t_iterAlt.next();
				t_objTraverser = this.getTraverser(this);
				t_objTraverser.traverseGraph(t_objAlternative);        
			}
		}
	}

	/**
	 * @see org.eurocarbdb.MolecularFramework.util.visitor.GlycoVisitor#visit(org.eurocarbdb.MolecularFramework.sugar.UnvalidatedGlycoNode)
	 */
	public void visit(UnvalidatedGlycoNode a_objUnvalidated) throws GlycoVisitorException 
	{
		this.m_iUnvalidated++;
	}
    
    public void start(Sugar a_objSugar) throws GlycoVisitorException
    {
        this.clear();
        this.m_iUnderdetermined += a_objSugar.getUndeterminedSubTrees().size();
        GlycoTraverser t_objTraverser = this.getTraverser(this);
        t_objTraverser.traverseGraph(a_objSugar);       
        if ( this.m_bDescentUnderdeterminded )
        {
        	for (Iterator<UnderdeterminedSubTree> t_iterUnder = a_objSugar.getUndeterminedSubTrees().iterator(); t_iterUnder.hasNext();)
        	{
        		t_objTraverser = this.getTraverser(this);
        		t_objTraverser.traverseGraph(t_iterUnder.next());
        	}
        }
    }
    
    public void start(SugarUnitRepeat a_objRepeat) throws GlycoVisitorException
    {
        this.clear();
        this.m_objRepeat = a_objRepeat;
        GlycoTraverser t_objTraverser = this.getTraverser(this);
        t_objTraverser.traverseGraph(a_objRepeat);        
        this.m_iUnderdetermined += a_objRepeat.getUndeterminedSubTrees().size();
        if ( this.m_bDescentUnderdeterminded )
        {
        	for (Iterator<UnderdeterminedSubTree> t_iterUnder = a_objRepeat.getUndeterminedSubTrees().iterator(); t_iterUnder.hasNext();)
        	{
        		t_objTraverser = this.getTraverser(this);
        		t_objTraverser.traverseGraph(t_iterUnder.next());
        	}
        }
    }

    public void start(SugarUnitAlternative a_objAlternative) throws GlycoVisitorException
    {
        GlycoTraverser t_objTraverser;
        this.clear();
        for (Iterator<GlycoGraphAlternative> t_iterAlt = a_objAlternative.getAlternatives().iterator(); t_iterAlt.hasNext();)
        {
            GlycoGraphAlternative t_objAlternative = t_iterAlt.next();
            t_objTraverser = this.getTraverser(this);
            t_objTraverser.traverseGraph(t_objAlternative);        
        }
    }
}