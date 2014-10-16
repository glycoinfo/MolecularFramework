package org.eurocarbdb.MolecularFramework.io.glycam;

import java.util.ArrayList;

import org.eurocarbdb.MolecularFramework.io.SugarExporter;
import org.eurocarbdb.MolecularFramework.io.SugarExporterException;
import org.eurocarbdb.MolecularFramework.sugar.GlycoEdge;
import org.eurocarbdb.MolecularFramework.sugar.GlycoconjugateException;
import org.eurocarbdb.MolecularFramework.sugar.Linkage;
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

public class SugarExporterGlycam extends SugarExporter implements GlycoVisitor
{
	private String m_sequence = null;
	private GlycoTraverser m_traverser =null;
	private String m_reducingEnd = "1-OH";

	public String export(Sugar a_objSugar) throws SugarExporterException
	{
		try
		{
			this.start(a_objSugar);    
		} 
		catch (Exception t_exception)
		{
			throw new SugarExporterException(t_exception.getMessage(),t_exception);
		}
		return this.m_sequence;
	}

	public void clear()
	{
		this.m_sequence = this.m_reducingEnd;
	}

	public GlycoTraverser getTraverser(GlycoVisitor a_objVisitor) throws GlycoVisitorException
	{
		return new GlycoTraverserIupacSequence(a_objVisitor);
	}

	public void visit(Monosaccharide a_objMonosaccharid) throws GlycoVisitorException
	{
		throw new GlycoVisitorException("Monosaccharide is not supported for exporting: Use Namespace translation first.");
	}

	public void visit(NonMonosaccharide a_objResidue) throws GlycoVisitorException
	{
		throw new GlycoVisitorException("NonMonosaccharide is not supported for exporting: Use Namespace translation first.");
	}

	public void visit(SugarUnitRepeat a_objRepeat) throws GlycoVisitorException
	{
		throw new GlycoVisitorException("Sugars with repeating units are not supported.");
	}

	public void visit(Substituent a_objSubstituent) throws GlycoVisitorException
	{
		throw new GlycoVisitorException("Substituent is not supported for exporting: Use Namespace translation first.");
	}

	public void visit(SugarUnitCyclic a_objCyclic) throws GlycoVisitorException
	{
		throw new GlycoVisitorException("Cyclic sugars are not supported.");
	}

	public void visit(SugarUnitAlternative a_objAlternative) throws GlycoVisitorException
	{
		throw new GlycoVisitorException("Sugar with alternative residues are not supported.");
	}

	public void start(Sugar a_objSugar) throws GlycoVisitorException
	{
		try
		{
			if ( !a_objSugar.isConnected() )
			{
				throw new GlycoVisitorException("Glycam does not support fragmented sugars.");
			}
		}
		catch (GlycoconjugateException e)
		{
			throw new GlycoVisitorException(e.getMessage(),e);
		}        
		this.clear();
		this.m_traverser = this.getTraverser(this);
		this.m_traverser.traverseGraph(a_objSugar);
		if ( a_objSugar.getUndeterminedSubTrees().size() > 0 )
		{
			throw new  GlycoVisitorException("Glycam does not support underdeterminded subtrees.");
		}
	}

	public void visit(UnvalidatedGlycoNode a_objUnvalidated) throws GlycoVisitorException
	{
		this.m_sequence = a_objUnvalidated.getName() + this.m_sequence;
	}

	// DManpa1-2DManpa1-3DManpa1-6[DManpa1-3]DManpb1-4DGlcpNAcb1-4DGlcpNAcb1-OH
	public void visit(GlycoEdge a_objLinkage) throws GlycoVisitorException
	{
		String t_linkageString = "";
		if ( this.m_traverser.getState() == GlycoTraverserIupacSequence.BRANCH_IN )
		{
			t_linkageString = "]";
		}
		else if ( this.m_traverser.getState() == GlycoTraverserIupacSequence.BRANCH_OUT )
		{
			this.m_sequence = "[" + this.m_sequence;
			return;
		}
		ArrayList<Linkage> t_linkages = a_objLinkage.getGlycosidicLinkages();
		if ( t_linkages.size() != 1 )
		{
			throw new GlycoVisitorException("Multi linked residues are not supported.");
		}
		for (Linkage t_linkage : t_linkages) 
		{
//			if ( t_linkage.getChildLinkages().size() != 1 || t_linkage.getParentLinkages().size() != 1 )
//			{
//				throw new GlycoVisitorException("Alternative linkage positions are not supported.");
//			}
		    Integer t_counter = t_linkage.getParentLinkages().size();
		    String t_part = "";
			for (Integer t_position : t_linkage.getParentLinkages()) 
			{
				if ( t_position.equals(Linkage.UNKNOWN_POSITION) )
				{
				    t_part = "?" + t_part;
				}
				else
				{
				    t_part = t_part + t_position.toString();
				}
				t_counter--;
				if ( t_counter > 0 )
				{
				    t_part = t_part + "/";
				}
			}
			t_linkageString = "-" + t_part + t_linkageString;
			t_counter = t_linkage.getChildLinkages().size();
			t_part = "";
			for (Integer t_position : t_linkage.getChildLinkages()) 
			{
				if ( t_position.equals(Linkage.UNKNOWN_POSITION) )
				{
				    t_part = "?" + t_part;
				}
				else
				{
				    t_part = t_part + t_position.toString();
				}
				t_counter--;
				if ( t_counter > 0 )
                {
                    t_part = t_part + "/";
                }
			}
			t_linkageString = t_part + t_linkageString;
		}
		this.m_sequence = t_linkageString + this.m_sequence;
	}

	public void setReducingEnd(String a_reducingEnd)
	{
		if ( a_reducingEnd != null )
		{
			this.m_reducingEnd = a_reducingEnd;
		}
	}

	public String getReducingEnd() 
	{
		return this.m_reducingEnd;
	}
}
