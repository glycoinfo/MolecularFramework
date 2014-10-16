package org.eurocarbdb.MolecularFramework.util.validation;
import java.util.ArrayList;
import java.util.Iterator;

import org.eurocarbdb.MolecularFramework.sugar.GlycoEdge;
import org.eurocarbdb.MolecularFramework.sugar.GlycoNode;
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


public class GlycoVisitorSubstPosCorrector implements GlycoVisitor 
{
	private ArrayList<String> m_aWarnings = new ArrayList<String>();
	
	public ArrayList<String> getWarnings()
	{
		return this.m_aWarnings;
	}
	
	public void clear() 
	{
		this.m_aWarnings.clear();
	}

	public GlycoTraverser getTraverser(GlycoVisitor visitor) throws GlycoVisitorException 
	{
		return new GlycoTraverserSimple(visitor);
	}

	public void visit(Monosaccharide monosaccharid) throws GlycoVisitorException 
	{}

	public void visit(NonMonosaccharide residue) throws GlycoVisitorException 
	{}

	public void visit(SugarUnitCyclic cyclic) throws GlycoVisitorException 
	{}

	public void visit(UnvalidatedGlycoNode unvalidated) throws GlycoVisitorException 
	{}

	public void visit(GlycoEdge linkage) throws GlycoVisitorException 
	{}

	public void start(Sugar sugar) throws GlycoVisitorException 
	{
		GlycoTraverser t = this.getTraverser(this);
		t.traverseGraph(sugar);
		try 
		{
			GlycoVisitorNodeType nodeType = new GlycoVisitorNodeType();
			for (Iterator<UnderdeterminedSubTree> iterator = sugar.getUndeterminedSubTrees().iterator(); iterator.hasNext();) 
			{
				UnderdeterminedSubTree type = iterator.next();
				t.traverseGraph(type);
				if ( type.getRootNodes().size() != 1 )
				{
					throw new GlycoVisitorException("UND with with more than one root residues are not supported in GlycoVisitorSubstPosCorrector.");
				}
				for (Iterator<GlycoNode> iterator2 = type.getRootNodes().iterator(); iterator2.hasNext();) 
				{
					Substituent a = nodeType.getSubstituent(iterator2.next());
					if ( a != null )
					{
						String t_str = this.correctParentLinkage(a,type.getConnection());
						if ( t_str != null )
						{
							Integer t_iChild = 0;
							Integer t_iParent = type.getConnection().getGlycosidicLinkages().size();
							for (GlycoEdge t_edge : a.getChildEdges()) 
							{
								t_iChild += t_edge.getGlycosidicLinkages().size();
							}
							this.m_aWarnings.add("Correct UND special linkage for " 
									+ a.getSubstituentType().getName() 
									+ "( Parent : " + t_iParent.toString() + "| Child : " + t_iChild.toString() + ") replaced linkages " + t_str );
						}

					}				
				}
				// test the potential parents
				boolean t_bSubst = false;
				boolean t_bOthers = false;
				for (GlycoNode t_node : type.getParents() ) 
				{
					Substituent t_subst  = nodeType.getSubstituent(t_node);	
					if ( t_subst == null )
					{
						t_bOthers = true;
					}
					else
					{
						t_bSubst = true;
						String t_str = this.correctChildLinkage(t_subst, type.getConnection());
						if ( t_str != null )
						{
							Integer t_iChild = type.getConnection().getGlycosidicLinkages().size();
							Integer t_iParent = 0;
							for (GlycoEdge t_edge : t_subst.getChildEdges()) 
							{
								t_iChild += t_edge.getGlycosidicLinkages().size();
							}
							if ( t_subst.getParentEdge() != null )
							{
								t_iParent += t_subst.getParentEdge().getGlycosidicLinkages().size();
							}
							this.m_aWarnings.add("Correct UND special linkage parent for " 
									+ t_subst.getSubstituentType().getName() 
									+ "( Parent : " + t_iParent.toString() + "| Child : " + t_iChild.toString() + ") replaced linkages " + t_str );
						}
					}
					if ( t_bSubst && t_bOthers )
					{
						throw new GlycoVisitorException("UND parent mixture of subst and non-subst is not supported.");
					}
				}
			}
		} 
		catch (Exception e) 
		{
			throw new GlycoVisitorException(e.getMessage(),e);
		}
	}
	
	public void visit(SugarUnitRepeat repeat) throws GlycoVisitorException 
	{
		GlycoTraverser t = this.getTraverser(this);
		t.traverseGraph(repeat);	
		try 
		{
			// internal repeat linkage and incoming linkage
			GlycoVisitorNodeType nodeType = new GlycoVisitorNodeType();
			Substituent t_subst = nodeType.getSubstituent(repeat.getRepeatLinkage().getChild());
			if ( t_subst != null )
			{
				String t_str = this.correctParentLinkage(t_subst, repeat.getRepeatLinkage());
				if ( t_str != null )
				{
					Integer t_iParent = repeat.getRepeatLinkage().getGlycosidicLinkages().size();
					Integer t_iChild = 0;
					for (GlycoEdge t_edge : t_subst.getChildEdges()) 
					{
						t_iChild += t_edge.getGlycosidicLinkages().size();
					}
					this.m_aWarnings.add("Correct REP internal linkage (Child) for " 
							+ t_subst.getSubstituentType().getName() 
							+ "( Parent : " + t_iParent.toString() + "| Child : " + t_iChild.toString() + ") replaced linkages " + t_str );
				}
				if ( repeat.getParentEdge() != null )
				{
					t_str = this.correctParentLinkage(t_subst, repeat.getParentEdge() );
					if ( t_str != null )
					{
						Integer t_iParent = repeat.getParentEdge().getGlycosidicLinkages().size();
						Integer t_iChild = 0;
						for (GlycoEdge t_edge : t_subst.getChildEdges()) 
						{
							t_iChild += t_edge.getGlycosidicLinkages().size();
						}
						this.m_aWarnings.add("Correct REP incoming linkage for " 
								+ t_subst.getSubstituentType().getName() 
								+ "( Parent : " + t_iParent.toString() + "| Child : " + t_iChild.toString() + ") replaced linkages " + t_str );
					}
				}
			}			
			t_subst = nodeType.getSubstituent(repeat.getRepeatLinkage().getParent());
			if ( t_subst != null )
			{
				String t_str = this.correctChildLinkage(t_subst, repeat.getRepeatLinkage());
				if ( t_str != null )
				{
					Integer t_iChild = repeat.getRepeatLinkage().getGlycosidicLinkages().size();
					Integer t_iParent = 0;
					if ( t_subst.getParentEdge() != null )
					{
						t_iParent += t_subst.getParentEdge().getGlycosidicLinkages().size();
					}
					for (GlycoEdge t_edge : t_subst.getChildEdges()) 
					{
						t_iChild += t_edge.getGlycosidicLinkages().size();
					}
					this.m_aWarnings.add("Correct REP internal linkage (Parent) for " 
							+ t_subst.getSubstituentType().getName() 
							+ "( Parent : " + t_iParent.toString() + "| Child : " + t_iChild.toString() + ") replaced linkages " + t_str );
				}
				for (GlycoEdge t_childEdges : repeat.getChildEdges()) 
				{
					t_str = this.correctChildLinkage(t_subst, t_childEdges );
					if ( t_str != null )
					{
						Integer t_iChild = repeat.getRepeatLinkage().getGlycosidicLinkages().size();
						Integer t_iParent = 0;
						if ( t_subst.getParentEdge() != null )
						{
							t_iParent += t_subst.getParentEdge().getGlycosidicLinkages().size();
						}
						for (GlycoEdge t_edge : repeat.getChildEdges()) 
						{
							t_iChild += t_edge.getGlycosidicLinkages().size();
						}						
						this.m_aWarnings.add("Correct REP outgoing linkage for " 
								+ t_subst.getSubstituentType().getName() 
								+ "( Parent : " + t_iParent.toString() + "| Child : " + t_iChild.toString() + ") replaced linkages " + t_str );
					}
				}
			}
			
			for (Iterator<UnderdeterminedSubTree> iterator = repeat.getUndeterminedSubTrees().iterator(); iterator.hasNext();) 
			{
				UnderdeterminedSubTree type = iterator.next();
				t.traverseGraph(type);
				if ( type.getRootNodes().size() != 1 )
				{
					throw new GlycoVisitorException("UND with with more than one root residues are not supported in GlycoVisitorSubstPosCorrector.");
				}
				for (Iterator<GlycoNode> iterator2 = type.getRootNodes().iterator(); iterator2.hasNext();) 
				{
					Substituent a = nodeType.getSubstituent(iterator2.next());
					if ( a != null )
					{
						String t_str = this.correctParentLinkage(a,type.getConnection());
						if ( t_str != null )
						{
							Integer t_iChild = 0;
							Integer t_iParent = type.getConnection().getGlycosidicLinkages().size();
							for (GlycoEdge t_edge : a.getChildEdges()) 
							{
								t_iChild += t_edge.getGlycosidicLinkages().size();
							}
							this.m_aWarnings.add("Correct UND special linkage for " 
									+ a.getSubstituentType().getName() 
									+ "( Parent : " + t_iParent.toString() + "| Child : " + t_iChild.toString() + ") replaced linkages " + t_str );
						}
					}				
				}
				// test the potential parents
				boolean t_bSubst = false;
				boolean t_bOthers = false;
				for (GlycoNode t_node : type.getParents() ) 
				{
					t_subst  = nodeType.getSubstituent(t_node);	
					if ( t_subst == null )
					{
						t_bOthers = true;
					}
					else
					{
						t_bSubst = true;
						String t_str = this.correctChildLinkage(t_subst, type.getConnection());
						if ( t_str != null )
						{
							Integer t_iChild = type.getConnection().getGlycosidicLinkages().size();
							Integer t_iParent = 0;
							for (GlycoEdge t_edge : t_subst.getChildEdges()) 
							{
								t_iChild += t_edge.getGlycosidicLinkages().size();
							}
							if ( t_subst.getParentEdge() != null )
							{
								t_iParent += t_subst.getParentEdge().getGlycosidicLinkages().size();
							}
							this.m_aWarnings.add("Correct UND special linkage parent for " 
									+ t_subst.getSubstituentType().getName() 
									+ "( Parent : " + t_iParent.toString() + "| Child : " + t_iChild.toString() + ") replaced linkages " + t_str );
						}
					}
					if ( t_bSubst && t_bOthers )
					{
						throw new GlycoVisitorException("UND parent mixture of subst and non-subst is not supported.");
					}
				}
			}
		} 
		catch (Exception e) 
		{
			throw new GlycoVisitorException(e.getMessage(),e);
		}		
	}

	public void visit(Substituent substituent) throws GlycoVisitorException 
	{
		String t_str = null;
		if ( substituent.getParentEdge() != null )
		{
			t_str = this.correctParentLinkage(substituent,substituent.getParentEdge());
		}
		for (Iterator<GlycoEdge> iterator = substituent.getChildEdges().iterator(); iterator.hasNext();) 
		{
			String t_str2 = this.correctChildLinkage(substituent,iterator.next());
			if ( t_str2 != null )
			{
				if ( t_str == null )
				{
					t_str = t_str2;
				}
				else
				{
					t_str = t_str + "=" + t_str2;
				}
			}
		}
		if ( t_str != null )
		{
			Integer t_iLinkage = 0;
			Integer t_iParent = 0;
			if ( substituent.getParentEdge() != null )
			{
				t_iParent = substituent.getParentEdge().getGlycosidicLinkages().size();
			}
			for (GlycoEdge t_edge : substituent.getChildEdges()) 
			{
				t_iLinkage += t_edge.getGlycosidicLinkages().size();
			}
			this.m_aWarnings.add("Correct linkage for " 
					+ substituent.getSubstituentType().getName() 
					+ "( Parent : " + t_iParent.toString() + "| Child : " + t_iLinkage.toString() + ") replaced linkages " + t_str );
		}
	}

	private String correctParentLinkage(Substituent a_substituent, GlycoEdge a_edge) throws GlycoVisitorException 
	{
		boolean t_bCorrected = false;
		StringBuffer t_buffer = new StringBuffer();
		for (Iterator<Linkage> iterator = a_edge.getGlycosidicLinkages().iterator(); iterator.hasNext();) 
		{
			Linkage type = iterator.next();
			ArrayList<Integer> t_aChildLinkage = new ArrayList<Integer>();
			for (Iterator<Integer> iterator2 = type.getChildLinkages().iterator(); iterator2.hasNext();) 
			{
				Integer x = iterator2.next();
				if (  x != 1 )
				{
					t_buffer.append(x);
					t_bCorrected = true;
					t_aChildLinkage.add(1);
				}				
				else
				{
					t_aChildLinkage.add(x);
				}
			}
			try 
			{
				type.setChildLinkages(t_aChildLinkage);	
			} 
			catch (Exception e) 
			{
				throw new GlycoVisitorException(e.getMessage(),e);
			}
			t_buffer.append(":");
		}
		if ( t_bCorrected )
		{
			return t_buffer.toString();
		}
		else
		{
			return null;
		}
	}

	private String correctChildLinkage(Substituent a_substituent, GlycoEdge a_edge) throws GlycoVisitorException  
	{
		boolean t_bCorrected = false;
		StringBuffer t_buffer = new StringBuffer();
		for (Iterator<Linkage> iterator = a_edge.getGlycosidicLinkages().iterator(); iterator.hasNext();) 
		{
			Linkage type = iterator.next();
			ArrayList<Integer> t_aParentLinkage = new ArrayList<Integer>();
			for (Iterator<Integer> iterator2 = type.getParentLinkages().iterator(); iterator2.hasNext();) 
			{
				Integer x = iterator2.next();
				if (  x != 1 )
				{
					t_buffer.append(x);
					t_bCorrected = true;					
					t_aParentLinkage.add(1);
				}				
				else
				{
					t_aParentLinkage.add(x);
				}
			}
			try 
			{
				type.setParentLinkages(t_aParentLinkage);	
			} 
			catch (Exception e) 
			{
				throw new GlycoVisitorException(e.getMessage(),e);
			}			
			t_buffer.append(":");
		}
		if ( t_bCorrected )
		{
			return t_buffer.toString();
		}
		else
		{
			return null;
		}
	}

	public void visit(SugarUnitAlternative alternative)	throws GlycoVisitorException
	{
		throw new GlycoVisitorException("Alternative sugar Units are not supported in GlycoVisitorSubstPosCorrector.");
	}
}
