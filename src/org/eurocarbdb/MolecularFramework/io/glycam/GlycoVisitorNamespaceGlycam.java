package org.eurocarbdb.MolecularFramework.io.glycam;

import java.util.ArrayList;
import java.util.List;

import org.eurocarbdb.MolecularFramework.sugar.GlycoEdge;
import org.eurocarbdb.MolecularFramework.sugar.GlycoNode;
import org.eurocarbdb.MolecularFramework.sugar.GlycoconjugateException;
import org.eurocarbdb.MolecularFramework.sugar.Linkage;
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
import org.eurocarbdb.MolecularFramework.util.traverser.GlycoTraverserSimple;
import org.eurocarbdb.MolecularFramework.util.visitor.GlycoVisitor;
import org.eurocarbdb.MolecularFramework.util.visitor.GlycoVisitorException;
import org.eurocarbdb.MolecularFramework.util.visitor.GlycoVisitorNodeType;

public class GlycoVisitorNamespaceGlycam implements GlycoVisitor 
{
	private List<GlycoNode> m_removeList = new ArrayList<GlycoNode>();

	public void clear() 
	{
		this.m_removeList.clear();
	}

	public GlycoTraverser getTraverser(GlycoVisitor a_objVisitor) throws GlycoVisitorException 
	{
		return new GlycoTraverserSimple(a_objVisitor);
	}

	public void visit(Monosaccharide aObjMonosaccharid) throws GlycoVisitorException 
	{
		throw new GlycoVisitorException("Monosaccharide is not supported for exporting: Use Namespace translation first.");		
	}

	public void visit(NonMonosaccharide aObjResidue) throws GlycoVisitorException 
	{
		throw new GlycoVisitorException("NonMonosaccharide is not supported for exporting: Use Namespace translation first.");
	}

	public void visit(Substituent aObjSubstituent) throws GlycoVisitorException 
	{
		throw new GlycoVisitorException("Substituent is not supported for exporting: Use Namespace translation first.");		
	}

	public void visit(SugarUnitCyclic aObjCyclic) throws GlycoVisitorException 
	{
		// nothing to do
	}

	public void visit(SugarUnitAlternative aObjAlternative) throws GlycoVisitorException 
	{
		throw new GlycoVisitorException("Alternative residues are not supported for exporting: Use Namespace translation first.");		
	}

	public void visit(GlycoEdge aObjLinkage) throws GlycoVisitorException
	{
		// nothing to do
	}

	public void visit(SugarUnitRepeat a_repeat) throws GlycoVisitorException 
	{
		GlycoVisitorNamespaceGlycam t_visitor = new GlycoVisitorNamespaceGlycam();
		t_visitor.start(a_repeat);
	}

	private void start(SugarUnitRepeat a_repeat) throws GlycoVisitorException 
	{
		GlycoTraverser t_traverser = this.getTraverser(this);
		t_traverser.traverseGraph(a_repeat);
		for (UnderdeterminedSubTree t_tree : a_repeat.getUndeterminedSubTrees())
		{
			t_traverser.traverseGraph(t_tree);
		}
		try 
		{
			for (GlycoNode t_node : this.m_removeList) 
			{
				a_repeat.removeNode(t_node);
			}
		} 
		catch (GlycoconjugateException e) 
		{
			throw new GlycoVisitorException(e.getMessage(),e);
		}
	}

	public void start(Sugar a_sugar) throws GlycoVisitorException 
	{
		GlycoTraverser t_traverser = this.getTraverser(this);
		t_traverser.traverseGraph(a_sugar);
		for (UnderdeterminedSubTree t_tree : a_sugar.getUndeterminedSubTrees())
		{
			t_traverser.traverseGraph(t_tree);
		}
		try 
		{
			for (GlycoNode t_node : this.m_removeList) 
			{
				a_sugar.removeNode(t_node);
			}
		} 
		catch (GlycoconjugateException e) 
		{
			throw new GlycoVisitorException(e.getMessage(),e);
		}
	}

	public void visit(UnvalidatedGlycoNode a_node) throws GlycoVisitorException 
	{
		String t_name = a_node.getName();
		String[] t_parts = t_name.split("-");
		try
		{
			if ( t_parts.length > 1 )
			{
				String t_sulfate = this.getSulfateString(a_node);
				if ( t_parts[0].toLowerCase().equals("a") || t_parts[0].toLowerCase().equals("b") )
				{
					if ( t_parts.length > 2 )
					{
						t_name = t_parts[1] + t_name.substring(4) + t_sulfate + t_parts[0];
						a_node.setName(t_name);
					}
					else
					{
						t_name = t_parts[0] + t_name.substring(2);
						a_node.setName(t_name);
					}
				}
				else if ( t_parts[0].toLowerCase().equals("d") || t_parts[0].toLowerCase().equals("l") )
				{
					t_name = t_parts[0] + t_name.substring(2) + t_sulfate + "?";
					a_node.setName(t_name);
				}
				else
				{
					if ( t_sulfate.length() != 0 )
					{
						throw new GlycoVisitorException("Sulfate that is not connected to monosaccharides are not supported.");
					}
				}
			}
		}
		catch (Exception e) 
		{
			throw new GlycoVisitorException(e.getMessage(),e);
		}
	}

	private String getSulfateString(UnvalidatedGlycoNode a_node) throws GlycoVisitorException 
	{
		String t_result = "";
		boolean t_firstTime = true;
		GlycoVisitorNodeType t_type = new GlycoVisitorNodeType();
		for ( GlycoNode t_node : a_node.getChildNodes() )
		{
			UnvalidatedGlycoNode t_nodeU = t_type.getUnvalidatedNode(t_node); 
			if ( t_nodeU != null )
			{
				if ( t_nodeU.getName().equals("sulfate") )
				{
					this.m_removeList.add(t_nodeU);
					if ( t_nodeU.getChildEdges().size() == 0 )
					{
						if ( !t_firstTime)
						{
							t_result += "," + this.getLinkageString(t_nodeU.getParentEdge()) + "S";
						}
						else
						{
							t_result = this.getLinkageString(t_nodeU.getParentEdge()) + "S";
							t_firstTime = false;
						}
					}
				}
			}
		}
		if ( t_result.length() > 0 )
		{
			t_result = "[" + t_result + "]";
		}
		return t_result;
	}

	private String getLinkageString(GlycoEdge a_edge) throws GlycoVisitorException 
	{
		if ( a_edge.getGlycosidicLinkages().size() != 1 )
		{
			throw new GlycoVisitorException("Multilinked sulfates are not supported.");
		}
		String t_result = "";
		for (Linkage t_linkage : a_edge.getGlycosidicLinkages()) 
		{
			if ( t_linkage.getParentLinkages().size() != 1 )
			{
				throw new GlycoVisitorException("Sulfates with alternative linkages are not supported.");
			}
			for (Integer t_position : t_linkage.getParentLinkages()) 
			{
				t_result += t_position.toString();
			}
		} 
		return t_result;
	}

}
