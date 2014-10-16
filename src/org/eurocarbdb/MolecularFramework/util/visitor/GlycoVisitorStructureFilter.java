package org.eurocarbdb.MolecularFramework.util.visitor;

import org.eurocarbdb.MolecularFramework.sugar.Anomer;
import org.eurocarbdb.MolecularFramework.sugar.BaseType;
import org.eurocarbdb.MolecularFramework.sugar.GlycoEdge;
import org.eurocarbdb.MolecularFramework.sugar.Linkage;
import org.eurocarbdb.MolecularFramework.sugar.LinkageType;
import org.eurocarbdb.MolecularFramework.sugar.Modification;
import org.eurocarbdb.MolecularFramework.sugar.ModificationType;
import org.eurocarbdb.MolecularFramework.sugar.Monosaccharide;
import org.eurocarbdb.MolecularFramework.sugar.NonMonosaccharide;
import org.eurocarbdb.MolecularFramework.sugar.Substituent;
import org.eurocarbdb.MolecularFramework.sugar.Sugar;
import org.eurocarbdb.MolecularFramework.sugar.SugarUnitAlternative;
import org.eurocarbdb.MolecularFramework.sugar.SugarUnitCyclic;
import org.eurocarbdb.MolecularFramework.sugar.SugarUnitRepeat;
import org.eurocarbdb.MolecularFramework.sugar.Superclass;
import org.eurocarbdb.MolecularFramework.sugar.UnderdeterminedSubTree;
import org.eurocarbdb.MolecularFramework.sugar.UnvalidatedGlycoNode;
import org.eurocarbdb.MolecularFramework.util.traverser.GlycoTraverser;
import org.eurocarbdb.MolecularFramework.util.traverser.GlycoTraverserNodes;
import org.eurocarbdb.MolecularFramework.util.visitor.GlycoVisitor;
import org.eurocarbdb.MolecularFramework.util.visitor.GlycoVisitorException;

public class GlycoVisitorStructureFilter implements GlycoVisitor
{
	private boolean m_bAllowUnknownAnomer = false;
	private boolean m_bAllowUnknownConfig = false;
	private boolean m_bAllowUnknownSuperclass = false;
	private boolean m_bAllowUnknownRingsize = false;
	private boolean m_bAllowUnknownSubstPos = false;
	private boolean m_bAllowReducingAlditol = true;
	private boolean m_bAllowUnknownLinkage = false;
	private boolean m_bAllowAlternativeLinkage = false;
	private boolean m_bAllowRepeatUnit = false;
	private boolean m_bAllowUnderdeterminded = false;
	private boolean m_bAllowCyclic = false;
	private boolean m_bIncludeFullyDefined = true;
	private boolean m_bAllowUnknownBasetype = false;
	private boolean m_bAllowUnknownRepeatCount = false;
	
	private boolean m_bUnknownAnomer = false;
	private boolean m_bUnknownConfig = false;
	private boolean m_bUnknownSuperclass = false;
	private boolean m_bUnknownRingsize = false;
	private boolean m_bUnknownSubstPos = false;
	private boolean m_bReducingAlditol = false;
	private boolean m_bUnknownLinkage = false;
	private boolean m_bAlternativeLinkage = false;
	private boolean m_bRepeatUnit = false;
	private boolean m_bUnderdeterminded = false;
	private boolean m_bCyclic = false;
    private boolean m_bUnknownBasetype = false;
    private boolean m_bUnknownRepeatCount = false;

    
	public boolean isReducingAlditol()
	{
	    return this.m_bReducingAlditol;
	}
	
	public boolean isValidSugar() 
	{
		if ( this.m_bUnknownAnomer && !this.m_bAllowUnknownAnomer )
		{
			return false;
		}
		if ( this.m_bUnknownConfig && !this.m_bAllowUnknownConfig )
		{
			return false;
		}
		if ( this.m_bUnknownSuperclass && !this.m_bAllowUnknownSuperclass )
		{
			return false;
		}
		if ( this.m_bUnknownBasetype && !this.m_bAllowUnknownBasetype )
		{
			return false;
		}
		if ( this.m_bUnknownRingsize && !this.m_bAllowUnknownRingsize )
		{
			return false;
		}
		if ( this.m_bUnknownSubstPos && !this.m_bAllowUnknownSubstPos )
		{
			return false;
		}
		if ( this.m_bReducingAlditol && !this.m_bAllowReducingAlditol )
		{
			return false;
		}
		if ( this.m_bUnknownLinkage && !this.m_bAllowUnknownLinkage )
		{
			return false;
		}
		if ( this.m_bAlternativeLinkage && !this.m_bAllowAlternativeLinkage )
		{
			return false;
		}
		if ( this.m_bRepeatUnit && !this.m_bAllowRepeatUnit )
		{
			return false;
		}
		if ( this.m_bUnderdeterminded && !this.m_bAllowUnderdeterminded )
		{
			return false;
		}
		if ( this.m_bUnknownRepeatCount && !this.m_bAllowUnknownRepeatCount )
        {
            return false;
        }
        if ( this.m_bCyclic && !this.m_bAllowCyclic )
		{
			return false;
		}
		if ( !this.m_bIncludeFullyDefined )
		{
			if ( this.m_bAlternativeLinkage || this.m_bUnknownAnomer || this.m_bUnknownConfig || this.m_bUnknownSuperclass || this.m_bUnknownRingsize || this.m_bUnknownSubstPos || this.m_bUnknownLinkage || this.m_bUnderdeterminded || this.m_bUnknownBasetype )
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		return true;
	}

	public void allowUnknownBasetype (boolean a_bValue)
	{
		this.m_bAllowUnknownBasetype = a_bValue;
	}
	
    public void allowUnknownRepeatcount (boolean a_bValue)
    {
        this.m_bAllowUnknownRepeatCount = a_bValue;
    }

    public void allowUnknownAnomer(boolean a_bValue)
	{
		this.m_bAllowUnknownAnomer = a_bValue;
	}
	
	public void allowUnknownConfig(boolean a_bValue)
	{
		this.m_bAllowUnknownConfig = a_bValue;
	}
	
	public void allowUnknownSuperclass(boolean a_bValue)
	{
		this.m_bAllowUnknownSuperclass = a_bValue;	
	}
	
	public void allowUnknownRingsize(boolean a_bValue)
	{
		this.m_bAllowUnknownRingsize = a_bValue;	
	}
	
	public void allowUnknownSubstPos(boolean a_bValue)
	{
		this.m_bAllowUnknownSubstPos = a_bValue;
	}
	
	public void allowReducingAlditol(boolean a_bValue)
	{
		this.m_bAllowReducingAlditol = a_bValue;	
	}
	
	public void allowUnknownLinkage(boolean a_bValue)
	{
		this.m_bAllowUnknownLinkage = a_bValue;	
	}
	
	public void allowRepeatUnit(boolean a_bValue)
	{
		this.m_bAllowRepeatUnit = a_bValue;	
	}
	
	public void allowUnderdeterminded(boolean a_bValue)
	{
		this.m_bAllowUnderdeterminded = a_bValue;	
	}
	
	public void allowCyclic(boolean a_bValue)
	{
		this.m_bAllowCyclic = a_bValue;	
	}
	
	public void includeFullyDefined(boolean a_bValue)
	{
		this.m_bIncludeFullyDefined = a_bValue;	
	}
	
	public void allowAlternativeLinkage(boolean a_bValue)
	{
		this.m_bAllowAlternativeLinkage = a_bValue;
	}
	
	public void clear() 
	{
		this.m_bUnknownAnomer = false;
		this.m_bUnknownConfig = false;
		this.m_bUnknownSuperclass = false;
		this.m_bUnknownRingsize = false;
		this.m_bUnknownSubstPos = false;
		this.m_bReducingAlditol = false;
		this.m_bUnknownLinkage = false;
		this.m_bRepeatUnit = false;
		this.m_bUnderdeterminded = false;
		this.m_bCyclic = false;
		this.m_bAlternativeLinkage = false;
		this.m_bUnknownBasetype = false;
		this.m_bUnknownRepeatCount = false;
	}

	public GlycoTraverser getTraverser(GlycoVisitor t_visitor) throws GlycoVisitorException 
	{
		return new GlycoTraverserNodes(t_visitor);
	}

	public void start(Sugar a_objSugar) throws GlycoVisitorException 
	{
		this.clear();
        GlycoTraverser t_objTraverser = this.getTraverser(this);
        t_objTraverser.traverseGraph(a_objSugar);
        for (UnderdeterminedSubTree t_tree : a_objSugar.getUndeterminedSubTrees()) 
        {
        	this.m_bUnderdeterminded = true;
        	t_objTraverser = this.getTraverser(this);
			t_objTraverser.traverseGraph(t_tree);	
			this.visit(t_tree.getConnection());
		}
    }

	public void visit(NonMonosaccharide arg0) throws GlycoVisitorException 
	{
		throw new GlycoVisitorException("SugarVisitorStructureFilter does not support NonMonosaccharide nodes.");
	}

	public void visit(SugarUnitAlternative arg0) throws GlycoVisitorException 
	{
		throw new GlycoVisitorException("SugarVisitorStructureFilter does not support SugarUnitAlternative nodes.");		
	}

	public void visit(UnvalidatedGlycoNode arg0) throws GlycoVisitorException 
	{
		throw new GlycoVisitorException("SugarVisitorStructureFilter does not support UnvalidatedGlycoNode nodes.");		
	}

	public void visit(GlycoEdge a_edge) throws GlycoVisitorException 
	{
		if ( a_edge != null )
		{
			for (Linkage t_linkage : a_edge.getGlycosidicLinkages()) 
			{
				LinkageType t_linkType = t_linkage.getChildLinkageType();
				if ( t_linkType.equals(LinkageType.UNKNOWN) || t_linkType.equals(LinkageType.UNVALIDATED) )
				{
					this.m_bUnknownLinkage = true;
				}
				t_linkType = t_linkage.getParentLinkageType();
				if ( t_linkType.equals(LinkageType.UNKNOWN) || t_linkType.equals(LinkageType.UNVALIDATED) )
				{
					this.m_bUnknownLinkage = true;
				}
				if ( t_linkage.getChildLinkages().size() != 1 || t_linkage.getParentLinkages().size() != 1 )
				{
					this.m_bAlternativeLinkage = true;
				}
				for (Integer t_pos : t_linkage.getChildLinkages()) 
				{
					if ( t_pos.equals(Linkage.UNKNOWN_POSITION) )
					{
						this.m_bUnknownLinkage = true;
					}
				}
				for (Integer t_pos : t_linkage.getParentLinkages()) 
				{
					if ( t_pos.equals(Linkage.UNKNOWN_POSITION) )
					{
						this.m_bUnknownLinkage = true;
					}
				}
			}
		}
	}

	public void visit(SugarUnitRepeat a_repeat) throws GlycoVisitorException 
	{
	    this.m_bRepeatUnit = true;
		this.visit(a_repeat.getParentEdge());
		this.visit(a_repeat.getRepeatLinkage());
        GlycoTraverser t_objTraverser = this.getTraverser(this);
        t_objTraverser.traverseGraph(a_repeat);
        if ( a_repeat.getMinRepeatCount() == SugarUnitRepeat.UNKNOWN || a_repeat.getMaxRepeatCount() == SugarUnitRepeat.UNKNOWN )
        {
            this.m_bUnknownRepeatCount = true;
        }
        for (UnderdeterminedSubTree t_tree : a_repeat.getUndeterminedSubTrees()) 
        {
        	this.m_bUnderdeterminded = true;
        	t_objTraverser = this.getTraverser(this);
			t_objTraverser.traverseGraph(t_tree);	
			this.visit(t_tree.getConnection());
		}		
	}

	public void visit(Substituent a_subst) throws GlycoVisitorException 
	{
		this.visit(a_subst.getParentEdge());
	}

	public void visit(SugarUnitCyclic a_cyclic) throws GlycoVisitorException 
	{
		this.visit(a_cyclic.getParentEdge());
		this.m_bCyclic = true;
	}

	public void visit(Monosaccharide a_ms) throws GlycoVisitorException 
	{
		boolean t_bAldi = false;
		this.visit(a_ms.getParentEdge());
		if ( a_ms.getAnomer().equals(Anomer.Unknown) )
		{
			this.m_bUnknownAnomer = true;
		}
		if ( a_ms.getBaseType().size() == 0 )
		{
			this.m_bUnknownBasetype = true;
		}
		for (BaseType t_basetype : a_ms.getBaseType() ) 
		{
			if ( t_basetype.getName().startsWith("x") )
			{
				this.m_bUnknownConfig = true;
			}
		}
		if ( a_ms.getSuperclass().equals(Superclass.SUG) )
		{
			this.m_bUnknownSuperclass = true;
		}
		if ( a_ms.getRingStart() == Monosaccharide.UNKNOWN_RING || a_ms.getRingEnd() == Monosaccharide.UNKNOWN_RING )
		{
			this.m_bUnknownRingsize = true;
		}
		for (Modification t_modi : a_ms.getModification() ) 
		{
			if ( t_modi.getPositionOne() == Modification.UNKNOWN_POSITION )
			{
				this.m_bUnknownSubstPos = true;
			}
			if ( t_modi.getPositionTwo() != null )
			{
				if ( t_modi.getPositionTwo().equals(Modification.UNKNOWN_POSITION) )
				{
					this.m_bUnknownSubstPos = true;
				}
			}
			if ( t_modi.getModificationType().equals(ModificationType.ALDI) )
			{
				t_bAldi = true;
			}
		}
		if ( a_ms.getParentEdge() == null && t_bAldi )
		{
			this.m_bReducingAlditol = true;
		}
	}
}