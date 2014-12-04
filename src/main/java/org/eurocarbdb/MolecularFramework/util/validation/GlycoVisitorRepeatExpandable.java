package org.eurocarbdb.MolecularFramework.util.validation;

import java.util.Iterator;

import org.eurocarbdb.MolecularFramework.sugar.GlycoEdge;
import org.eurocarbdb.MolecularFramework.sugar.GlycoGraphAlternative;
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
 * @author rene
 */
public class GlycoVisitorRepeatExpandable implements GlycoVisitor
{
	private boolean m_bExpanable = false;
	private int m_iMinRepeatCount = 7;
	
	public void setMinRepeatCount(int a_iNumber)
	{
		this.m_iMinRepeatCount = a_iNumber;
	}
	
	public int getMinRepeatCount()
	{
		return this.m_iMinRepeatCount;
	}
	
	public boolean isExpandable()
	{
		return this.m_bExpanable;
	}
	
    public void visit(Monosaccharide arg0) throws GlycoVisitorException
    {}

    public void visit(NonMonosaccharide arg0) throws GlycoVisitorException
    {}

    public void visit(GlycoEdge arg0) throws GlycoVisitorException
    {}

    public void visit(SugarUnitRepeat a_objRepeat) throws GlycoVisitorException
    {
    	if ( a_objRepeat.getMinRepeatCount() == a_objRepeat.getMaxRepeatCount() )
    	{
    		if ( a_objRepeat.getMinRepeatCount() != SugarUnitRepeat.UNKNOWN && a_objRepeat.getMinRepeatCount() < this.m_iMinRepeatCount )
    		{
    			this.m_bExpanable = true;
    		}
    	}   	
    	GlycoTraverser t_objTraverser = this.getTraverser(this);
    	t_objTraverser.traverseGraph(a_objRepeat);
        for (Iterator<UnderdeterminedSubTree> t_iterUnder = a_objRepeat.getUndeterminedSubTrees().iterator(); t_iterUnder.hasNext();)
        {
        	t_objTraverser = this.getTraverser(this);
        	t_objTraverser.traverseGraph(t_iterUnder.next());
        }
    }

    public GlycoTraverser getTraverser(GlycoVisitor a_objVisitor) throws GlycoVisitorException
    {
        return new GlycoTraverserNodes(a_objVisitor);
    }

    public void clear()
    {
        this.m_bExpanable = false;
    }

	public void visit(Substituent a_objSubstituent) throws GlycoVisitorException 
	{}

	public void visit(SugarUnitCyclic a_objCyclic) throws GlycoVisitorException 
	{}

	public void visit(SugarUnitAlternative a_objAlternative) throws GlycoVisitorException 
	{
		GlycoTraverser t_objTraverser = this.getTraverser(this);
		for (Iterator<GlycoGraphAlternative> t_iterAlt = a_objAlternative.getAlternatives().iterator(); t_iterAlt.hasNext();) 
		{
			t_objTraverser.traverseGraph(t_iterAlt.next());			
		}
	}

	public void visit(UnvalidatedGlycoNode a_objUnvalidated) throws GlycoVisitorException 
	{}
    
    public void start(Sugar a_objSugar) throws GlycoVisitorException
    {
        this.clear();
        GlycoTraverser t_objTraverser = this.getTraverser(this);
        t_objTraverser.traverseGraph(a_objSugar);      
        for (Iterator<UnderdeterminedSubTree> t_iterUnder = a_objSugar.getUndeterminedSubTrees().iterator(); t_iterUnder.hasNext();)
        {
        	t_objTraverser = this.getTraverser(this);
        	t_objTraverser.traverseGraph(t_iterUnder.next());
        }
    }
}