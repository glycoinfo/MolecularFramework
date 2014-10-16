package org.eurocarbdb.MolecularFramework.util.validation;

import java.util.HashMap;
import java.util.Iterator;

import org.eurocarbdb.MolecularFramework.sugar.GlycoEdge;
import org.eurocarbdb.MolecularFramework.sugar.GlycoGraph;
import org.eurocarbdb.MolecularFramework.sugar.GlycoGraphAlternative;
import org.eurocarbdb.MolecularFramework.sugar.GlycoNode;
import org.eurocarbdb.MolecularFramework.sugar.GlycoconjugateException;
import org.eurocarbdb.MolecularFramework.sugar.Linkage;
import org.eurocarbdb.MolecularFramework.sugar.LinkageType;
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
import org.eurocarbdb.MolecularFramework.util.traverser.GlycoTraverserTreeWood;
import org.eurocarbdb.MolecularFramework.util.visitor.GlycoVisitor;
import org.eurocarbdb.MolecularFramework.util.visitor.GlycoVisitorException;
import org.eurocarbdb.MolecularFramework.util.visitor.GlycoVisitorNodeType;

/**
 * @author rene
 */
public class GlycoVisitorRepeatExpand implements GlycoVisitor
{
	private GlycoGraph m_objGraph = null;	
	private int m_iMinRepeatCount = 7;
	private HashMap<GlycoNode, GlycoNode> m_hNodes = new HashMap<GlycoNode, GlycoNode>();
	private HashMap<SugarUnitAlternative, SugarUnitAlternative> m_hAlternatives = new HashMap<SugarUnitAlternative, SugarUnitAlternative>(); 
	private Sugar m_objSugar = null;
	private GlycoTraverser m_objTraverser = null;
	private HashMap<SugarUnitRepeat, GlycoNode> m_hExpandRepeatIn = new HashMap<SugarUnitRepeat, GlycoNode>();
	private HashMap<SugarUnitRepeat, GlycoNode> m_hExpandRepeatOut = new HashMap<SugarUnitRepeat, GlycoNode>();
	private boolean m_bExpanding = false;
	private HashMap<SugarUnitRepeat, LinkageType> m_hRepeatOutLinkageType = new HashMap<SugarUnitRepeat, LinkageType>();	
	private HashMap<SugarUnitRepeat, Boolean> m_hRepeatExpanded = new HashMap<SugarUnitRepeat, Boolean>();
	
	public Sugar getExpandedSugar()
	{
		return this.m_objSugar;
	}

	public void setMinRepeatCount(int a_iNumber)
	{
		this.m_iMinRepeatCount = a_iNumber;
	}

	public int getMinRepeatCount()
	{
		return this.m_iMinRepeatCount;
	}

	public void visit(Monosaccharide a_objMS) throws GlycoVisitorException
	{
		if ( this.m_objTraverser.getState() == GlycoTraverser.ENTER )
		{
			try 
			{
				Monosaccharide t_objMS = a_objMS.copy();
				this.m_hNodes.put(a_objMS, t_objMS);
				this.m_objGraph.addNode(t_objMS);
				this.copyParentEdge(a_objMS,t_objMS);
			} 
			catch (GlycoconjugateException e) 
			{
				throw new GlycoVisitorException(e.getMessage(),e);
			}
		}
	}

	private void copyParentEdge(GlycoNode a_objMS, GlycoNode a_objCopy) throws GlycoconjugateException, GlycoVisitorException 
	{
		GlycoEdge t_objEdgeOrg = a_objMS.getParentEdge();
		if ( t_objEdgeOrg != null )
		{
			GlycoEdge t_objEdge = t_objEdgeOrg.copy();
			if ( this.m_hRepeatOutLinkageType.containsKey(t_objEdgeOrg.getParent()) )
			{
				if ( t_objEdge.getGlycosidicLinkages().size() > 1 )
				{
					throw new GlycoVisitorException("Expanding of multi linked (out) repeat unit is not supported.");
				}
				for (Iterator<Linkage> t_iterLinkage = t_objEdge.getGlycosidicLinkages().iterator(); t_iterLinkage.hasNext();) 
				{
					t_iterLinkage.next().setParentLinkageType(this.m_hRepeatOutLinkageType.get(t_objEdgeOrg.getParent()));					
				}
			}
			GlycoNode t_objParent = this.m_hNodes.get(t_objEdgeOrg.getParent());
			if ( t_objParent == null )
			{
				throw new GlycoVisitorException("Error in coping parent residue during repeat expansion.");
			}
			this.m_objGraph.addEdge(t_objParent, a_objCopy, t_objEdge);
		}
	}

	public void visit(NonMonosaccharide a_objNonMS) throws GlycoVisitorException
	{
		if ( this.m_objTraverser.getState() == GlycoTraverser.ENTER )
		{
			try 
			{
				NonMonosaccharide t_objNonMS = a_objNonMS.copy();
				this.m_hNodes.put(a_objNonMS, t_objNonMS);
				this.m_objGraph.addNode(t_objNonMS);
				this.copyParentEdge(a_objNonMS,t_objNonMS);
			} 
			catch (GlycoconjugateException e) 
			{
				throw new GlycoVisitorException(e.getMessage(),e);
			}
		}
	}

	public void visit(GlycoEdge arg0) throws GlycoVisitorException
	{}

	public GlycoTraverser getTraverser(GlycoVisitor a_objVisitor) throws GlycoVisitorException
	{
		return new GlycoTraverserTreeWood(a_objVisitor);
	}

	public void clear()
	{
		this.m_hNodes.clear();
		this.m_hExpandRepeatIn.clear();
		this.m_hExpandRepeatOut.clear();
		this.m_hAlternatives.clear();
		this.m_objGraph = null;
		this.m_bExpanding = false;
		this.m_hRepeatExpanded.clear();
	}

	public void visit(Substituent a_objSubstituent) throws GlycoVisitorException 
	{
		if ( this.m_objTraverser.getState() == GlycoTraverser.ENTER )
		{
			try 
			{
				Substituent t_objSubst = a_objSubstituent.copy();
				this.m_hNodes.put(a_objSubstituent, t_objSubst);
				this.m_objGraph.addNode(t_objSubst);
				this.copyParentEdge(a_objSubstituent,t_objSubst);
			} 
			catch (GlycoconjugateException e) 
			{
				throw new GlycoVisitorException(e.getMessage(),e);
			}
		}
	}

	public void visit(SugarUnitCyclic a_objCyclic) throws GlycoVisitorException 
	{
		if ( this.m_objTraverser.getState() == GlycoTraverser.ENTER )
		{
			try 
			{
				GlycoNode t_objNode = this.m_hNodes.get(a_objCyclic.getCyclicStart());
				if ( t_objNode == null)
				{
					throw new GlycoVisitorException("Error while coping cyclic unit during repeat unit expansion.");
				}
				GlycoEdge t_objEdgeOrg = a_objCyclic.getParentEdge();
				if ( t_objEdgeOrg != null )
				{
					GlycoEdge t_objEdge = t_objEdgeOrg.copy();
					GlycoNode t_objParent = this.m_hNodes.get(t_objEdgeOrg.getParent());
					if ( t_objParent == null )
					{
						throw new GlycoVisitorException("Error in coping parent residue during repeat expansion.");
					}
					if ( this.m_objGraph != this.m_objSugar )
					{
						throw new GlycoVisitorException("Cyclic feature can only be added to a sugar object.");
					}
					this.m_objSugar.addCyclic(t_objParent, t_objEdge, t_objNode);
				}
				else
				{
					throw new GlycoVisitorException("Cyclic unit without parent linkage is not possible.");
				}
			} 
			catch (GlycoconjugateException e) 
			{
				throw new GlycoVisitorException(e.getMessage(),e);
			}
		}
	}

	public void visit(UnvalidatedGlycoNode a_objUnvalidated) throws GlycoVisitorException 
	{	
		if ( this.m_objTraverser.getState() == GlycoTraverser.ENTER )
		{
			try 
			{
				UnvalidatedGlycoNode t_objNode = a_objUnvalidated.copy();
				this.m_hNodes.put(a_objUnvalidated, t_objNode);
				this.m_objGraph.addNode(t_objNode);
				this.copyParentEdge(a_objUnvalidated,t_objNode);
			} 
			catch (GlycoconjugateException e) 
			{
				throw new GlycoVisitorException(e.getMessage(),e);
			}
		}
	}

	public void start(Sugar a_objSugar) throws GlycoVisitorException
	{
		this.clear();
		GlycoVisitorRepeatExpandable t_visExpandable = new GlycoVisitorRepeatExpandable();
		t_visExpandable.setMinRepeatCount(this.m_iMinRepeatCount);
		t_visExpandable.start(a_objSugar);
		this.m_objSugar = a_objSugar;
		Sugar t_objSugar = a_objSugar;
		while(t_visExpandable.isExpandable())
		{
			this.m_objSugar = new Sugar();
			this.m_objGraph = this.m_objSugar;
			this.m_objTraverser = this.getTraverser(this);
			this.m_objTraverser.traverseGraph(t_objSugar);      
			try 
			{
				for (Iterator<UnderdeterminedSubTree> t_iterUnder = t_objSugar.getUndeterminedSubTrees().iterator(); t_iterUnder.hasNext();)
				{
					UnderdeterminedSubTree t_objTree = t_iterUnder.next();
					UnderdeterminedSubTree t_objTreeCopy = new UnderdeterminedSubTree(); 
					this.m_objGraph = t_objTreeCopy;
					this.m_objTraverser = this.getTraverser(this);
					this.m_objTraverser.traverseGraph(t_objTree);
					t_objTreeCopy.setConnection( t_objTree.getConnection().copy());
					t_objTreeCopy.setProbability(t_objTree.getProbabilityLower(), t_objTree.getProbabilityUpper());
					this.m_objSugar.addUndeterminedSubTree(t_objTreeCopy);
					// parents
					for (Iterator<GlycoNode> t_iterParent = t_objTree.getParents().iterator(); t_iterParent.hasNext();) 
					{
						GlycoNode t_objNode = t_iterParent.next();
						if ( this.m_hRepeatExpanded.get(t_objNode) == null )
						{
							GlycoNode t_objParentNew = this.m_hNodes.get(t_objNode);
							if ( t_objParentNew == null )
							{
								throw new GlycoVisitorException("Error while coping parent residues of sugar UND during repeat unit expansion.");
							}
							this.m_objSugar.addUndeterminedSubTreeParent(t_objTreeCopy, t_objParentNew);
						}
						else
						{
							throw new GlycoVisitorException("An expanded repeat unit can not be a parent of a UND unit.");								
						}
					}
				}
			}
			catch (GlycoconjugateException e) 
			{
				throw new GlycoVisitorException(e.getMessage(),e);
			}
			t_objSugar = this.m_objSugar;
			t_visExpandable.start(t_objSugar);
		}
	}

	public void visit(SugarUnitAlternative a_objAlternative) throws GlycoVisitorException 
	{
		try 
		{
			if ( this.m_objTraverser.getState() == GlycoTraverser.ENTER )
			{
				SugarUnitAlternative t_objAltCopyAlternative = new SugarUnitAlternative();
				this.m_hNodes.put(a_objAlternative, t_objAltCopyAlternative);
				this.m_hAlternatives.put(a_objAlternative, t_objAltCopyAlternative);
				this.copyParentEdge(a_objAlternative,t_objAltCopyAlternative);
			}
			else if ( this.m_objTraverser.getState() == GlycoTraverser.LEAVE )
			{
				GlycoTraverser t_objTraverser = this.m_objTraverser;
				GlycoGraph t_objOldGraph = this.m_objGraph;
				SugarUnitAlternative t_objAltCopyAlternative = new SugarUnitAlternative();
				GlycoGraphAlternative t_objOld;
				GlycoGraphAlternative t_objNew;
				for (Iterator<GlycoGraphAlternative> t_iterAlt = a_objAlternative.getAlternatives().iterator(); t_iterAlt.hasNext();) 
				{
					t_objOld = t_iterAlt.next();
					t_objNew = new GlycoGraphAlternative();
					this.m_objGraph = t_objNew;
					this.m_objTraverser = this.getTraverser(this);
					this.m_objTraverser.traverseGraph(t_objOld);
					t_objAltCopyAlternative.addAlternative(t_objNew);
					// lead in
					GlycoNode t_objNode = this.m_hNodes.get(t_objOld.getLeadInNode());
					if ( t_objNode == null )
					{
						throw new GlycoVisitorException("Error while LeadInNode translation during repeat unit expansion.");
					}
					t_objAltCopyAlternative.setLeadInNode( t_objNode, t_objNew);
					// lead out
					HashMap<GlycoNode, GlycoNode> t_hNodes = t_objOld.getLeadOutNodeToNode();
					for (Iterator<GlycoNode> t_iterOut = t_hNodes.keySet().iterator(); t_iterOut.hasNext();) 
					{
						GlycoNode t_objAussen = t_iterOut.next();
						GlycoNode t_objInnen = t_hNodes.get(t_objAussen);
						GlycoNode t_objAussenNew = this.m_hNodes.get(t_objAussen);
						GlycoNode t_objInnenNew = this.m_hNodes.get(t_objInnen);
						if ( t_objAussenNew == null || t_objInnenNew == null )
						{
							throw new GlycoVisitorException("Error while LeadOutNode translation during repeat unit expansion.");
						}
						t_objAltCopyAlternative.addLeadOutNodeToNode(t_objInnenNew, t_objNew, t_objAussenNew);
					}
				}
				this.m_objGraph = t_objOldGraph;
				this.m_objTraverser = t_objTraverser;
			}
		}
		catch (GlycoconjugateException e) 
		{
			throw new GlycoVisitorException(e.getMessage(),e);
		}
	}

	public void visit(SugarUnitRepeat a_objRepeat) throws GlycoVisitorException
	{
		try
		{
			if ( this.m_objTraverser.getState() == GlycoTraverser.ENTER )
			{
				GlycoTraverser t_objTraverser = this.m_objTraverser;
				GlycoGraph t_objGraph = this.m_objGraph;
				if ( a_objRepeat.getMinRepeatCount() == a_objRepeat.getMaxRepeatCount() 
						&& a_objRepeat.getMinRepeatCount() != SugarUnitRepeat.UNKNOWN 
						&& a_objRepeat.getMinRepeatCount() < this.m_iMinRepeatCount 
						&& !this.m_bExpanding )
				{
					this.m_hRepeatExpanded.put(a_objRepeat, true);
					this.m_bExpanding = true;
					// expand it
					if ( a_objRepeat.getRootNodes().size() > 1 && (a_objRepeat.getParentEdge() != null || a_objRepeat.getChildEdges().size() > 0 ) )							
					{
						throw new GlycoVisitorException("Can not expand fragmented repeat units.");
					}
					GlycoEdge t_objParentEdge = a_objRepeat.getParentEdge();
					LinkageType t_enumLinkageType = null;
					GlycoNode t_objNewParent = null;
					if ( t_objParentEdge != null )
					{
						t_objNewParent = this.m_hNodes.get(t_objParentEdge.getParent());
						if ( t_objNewParent == null )
						{
							throw new GlycoVisitorException("Error missing repeat unit parent translation during repeat unit expansion.");
						}
						t_enumLinkageType = this.getRepeatInLinkageType(a_objRepeat);
					}
					GlycoNode t_objRepeatStart = a_objRepeat.getRepeatLinkage().getChild();
					GlycoNode t_objRepeatEnd = a_objRepeat.getRepeatLinkage().getParent();
					GlycoNode t_objNodeTemp = null;
					boolean t_bFirst = true;
					if ( t_objRepeatStart == null || t_objRepeatEnd == null )
					{
						throw new GlycoVisitorException("Error missing repeat residues during repeat unit expansion.");
					}
					for (int t_iRepeat = 0; t_iRepeat < a_objRepeat.getMinRepeatCount(); t_iRepeat++) 
					{
						this.m_objTraverser = this.getTraverser(this);
						this.m_objTraverser.traverseGraph(a_objRepeat);
						t_objNodeTemp = this.m_hNodes.get(t_objRepeatStart);
						if ( t_objNewParent != null )
						{							
							if ( t_objNodeTemp == null )
							{
								throw new GlycoVisitorException("Error in node tranlslation during repeat unit expansion.");
							}
							GlycoEdge t_objEdge = t_objParentEdge.copy();
							if ( t_enumLinkageType != null )
							{
								this.setInLinkageType(t_objEdge,t_enumLinkageType);
								t_enumLinkageType = null;
							}
							this.m_objGraph.addEdge(t_objNewParent, t_objNodeTemp, t_objEdge);
						}
						t_objNewParent = this.m_hNodes.get(t_objRepeatEnd);
						if ( t_objNewParent == null )
						{
							throw new GlycoVisitorException("Error in repeat node tranlslation during repeat unit expansion.");
						}
						if ( t_bFirst )
						{
							t_bFirst = false;
							this.m_hExpandRepeatIn.put(a_objRepeat, t_objNodeTemp);
						}
						t_objParentEdge = a_objRepeat.getRepeatLinkage();
					}					
					// child edge
					t_objNodeTemp = this.m_hNodes.get(t_objRepeatEnd);
					if ( t_objNodeTemp == null )
					{
						throw new GlycoVisitorException("Error in end node tranlslation during repeat unit expansion.");
					}
					this.m_hNodes.put(a_objRepeat, t_objNodeTemp);
					this.m_hExpandRepeatOut.put(a_objRepeat, t_objNodeTemp);
					if ( a_objRepeat.getUndeterminedSubTrees().size() > 0 )
					{
						throw new GlycoVisitorException("Repeat units with UnderdeterminedSubTree can not be expanded.");
					}
					this.m_bExpanding = false;
					this.m_hRepeatOutLinkageType.put(a_objRepeat, this.getRepeatOutLinkageType(a_objRepeat));
				}  
				else
				{
					// simply copy it			
					this.m_objTraverser = this.getTraverser(this);
					SugarUnitRepeat t_objNewRepeat = new SugarUnitRepeat();
					this.m_objGraph.addNode(t_objNewRepeat);
					this.m_objGraph = t_objNewRepeat;
					this.m_objTraverser.traverseGraph(a_objRepeat);
					// copy repeat information
					t_objNewRepeat.setMinRepeatCount(a_objRepeat.getMinRepeatCount());
					t_objNewRepeat.setMaxRepeatCount(a_objRepeat.getMaxRepeatCount());
					// copy UND
					for (Iterator<UnderdeterminedSubTree> t_iterUnder = a_objRepeat.getUndeterminedSubTrees().iterator(); t_iterUnder.hasNext();)
					{
						UnderdeterminedSubTree t_objTree = t_iterUnder.next();
						UnderdeterminedSubTree t_objTreeCopy = new UnderdeterminedSubTree(); 
						this.m_objGraph = t_objTreeCopy;
						this.m_objTraverser = this.getTraverser(this);
						this.m_objTraverser.traverseGraph(t_objTree);
						t_objTreeCopy.setConnection( t_objTree.getConnection().copy());
						t_objTreeCopy.setProbability(t_objTree.getProbabilityLower(), t_objTree.getProbabilityUpper());
						t_objNewRepeat.addUndeterminedSubTree(t_objTreeCopy);
						// parents
						for (Iterator<GlycoNode> t_iterParent = t_objTree.getParents().iterator(); t_iterParent.hasNext();) 
						{
							GlycoNode t_objNode = t_iterParent.next();
							if ( this.m_hRepeatExpanded.get(t_objNode) == null )
							{
								GlycoNode t_objParentNew = this.m_hNodes.get(t_objNode);
								if ( t_objParentNew == null )
								{
									throw new GlycoVisitorException("Error while coping parent residues of sugar UND during repeat unit expansion.");
								}
								t_objNewRepeat.addUndeterminedSubTreeParent(t_objTreeCopy, t_objParentNew);
							}
							else
							{
								throw new GlycoVisitorException("An expanded repeat unit can not be a parent of a UND unit.");								
							}
						}
					}
					this.m_hNodes.put(a_objRepeat, t_objNewRepeat);
					this.m_objGraph = t_objGraph;
					this.copyParentEdge(a_objRepeat, t_objNewRepeat);
					GlycoEdge t_objEdgeRepeat = a_objRepeat.getRepeatLinkage();
					GlycoEdge t_objEdgeRepeatNew = t_objEdgeRepeat.copy();
					GlycoNode t_objRepeatChild = this.m_hNodes.get(t_objEdgeRepeat.getChild());
					if ( this.m_hExpandRepeatIn.get(a_objRepeat.getRepeatLinkage().getChild()) != null )
					{
						// repeat was expanded
						t_objRepeatChild = this.m_hExpandRepeatIn.get(a_objRepeat.getRepeatLinkage().getChild());
						GlycoVisitorNodeType t_objNodeType = new GlycoVisitorNodeType();
						this.setInRepeatLinkageType(t_objNodeType.getSugarUnitRepeat(a_objRepeat.getRepeatLinkage().getChild()),t_objEdgeRepeatNew);
					}
					GlycoNode t_objRepeatParent = this.m_hNodes.get(t_objEdgeRepeat.getParent());
					if ( this.m_hExpandRepeatOut.get(a_objRepeat.getRepeatLinkage().getParent()) != null )
					{
						// repeat was expanded
						t_objRepeatChild = this.m_hExpandRepeatOut.get(a_objRepeat.getRepeatLinkage().getParent());
						GlycoVisitorNodeType t_objNodeType = new GlycoVisitorNodeType();
						this.setOutRepeatLinkageType(t_objNodeType.getSugarUnitRepeat(a_objRepeat.getRepeatLinkage().getParent()),t_objEdgeRepeatNew);
					}
					if ( t_objRepeatChild == null || t_objRepeatParent == null )
					{
						throw new GlycoVisitorException("Error while coping repeat linkage during repeat unit expansion.");						
					}
					t_objNewRepeat.setRepeatLinkage(t_objEdgeRepeatNew, t_objRepeatParent, t_objRepeatChild);					
				}
				this.m_objTraverser = t_objTraverser;
				this.m_objGraph = t_objGraph;
			}
		}
		catch (GlycoconjugateException e) 
		{
			throw new GlycoVisitorException(e.getMessage(),e);
		}
	}

	/**
	 * @param sugarUnitRepeat
	 * @param edgeRepeatNew
	 * @throws GlycoVisitorException 
	 * @throws GlycoconjugateException 
	 */
	private void setInRepeatLinkageType(SugarUnitRepeat a_objRepeat, GlycoEdge a_objEdge) throws GlycoVisitorException, GlycoconjugateException 
	{
		LinkageType t_objType = this.getRepeatInLinkageType(a_objRepeat);
		if (  a_objEdge.getGlycosidicLinkages().size() != 1 )
		{
			throw new GlycoVisitorException("Expanding of multi linked (new internal) repeat unit is not supported.");
		}
		for (Iterator<Linkage> t_iterLinkage = a_objEdge.getGlycosidicLinkages().iterator(); t_iterLinkage.hasNext();) 
		{
			t_iterLinkage.next().setChildLinkageType(t_objType);			
		}
	}

	private void setOutRepeatLinkageType(SugarUnitRepeat a_objRepeat, GlycoEdge a_objEdge) throws GlycoVisitorException, GlycoconjugateException 
	{
		LinkageType t_objType = this.getRepeatOutLinkageType(a_objRepeat);
		if (  a_objEdge.getGlycosidicLinkages().size() != 1 )
		{
			throw new GlycoVisitorException("Expanding of multi linked (new internal) repeat unit is not supported.");
		}
		for (Iterator<Linkage> t_iterLinkage = a_objEdge.getGlycosidicLinkages().iterator(); t_iterLinkage.hasNext();) 
		{
			t_iterLinkage.next().setParentLinkageType(t_objType);			
		}
	}

	/**
	 * @param edge
	 * @param linkageType
	 * @throws GlycoVisitorException 
	 * @throws GlycoconjugateException 
	 */
	private void setInLinkageType(GlycoEdge a_objEdge, LinkageType a_enumLinkageType) throws GlycoVisitorException, GlycoconjugateException 
	{
		if ( a_objEdge.getGlycosidicLinkages().size() > 1 )
		{
			throw new GlycoVisitorException("Expanding of multi linked (in) repeat unit is not supported.");
		}
		for (Iterator<Linkage> t_iterLinkage = a_objEdge.getGlycosidicLinkages().iterator(); t_iterLinkage.hasNext();) 
		{
			t_iterLinkage.next().setChildLinkageType(a_enumLinkageType);			
		}
	}

	/**
	 * @param repeat
	 * @return
	 * @throws GlycoVisitorException 
	 */
	private LinkageType getRepeatInLinkageType(SugarUnitRepeat a_objRepeat) throws GlycoVisitorException 
	{
		LinkageType t_objType = null;
		if ( a_objRepeat.getRepeatLinkage().getGlycosidicLinkages().size() != 1 )
		{
			throw new GlycoVisitorException("Expanding of multi linked (internal) repeat unit is not supported.");
		}
		for (Iterator<Linkage> t_iterLinkage = a_objRepeat.getRepeatLinkage().getGlycosidicLinkages().iterator(); t_iterLinkage.hasNext();) 
		{
			t_objType = t_iterLinkage.next().getChildLinkageType();			
		}
		return t_objType;
	}

	/**
	 * @param repeat
	 * @return
	 * @throws GlycoVisitorException 
	 */
	private LinkageType getRepeatOutLinkageType(SugarUnitRepeat a_objRepeat) throws GlycoVisitorException 
	{
		LinkageType t_objType = null;
		if ( a_objRepeat.getRepeatLinkage().getGlycosidicLinkages().size() != 1 )
		{
			throw new GlycoVisitorException("Expanding of multi linked (internal) repeat unit is not supported.");
		}
		for (Iterator<Linkage> t_iterLinkage = a_objRepeat.getRepeatLinkage().getGlycosidicLinkages().iterator(); t_iterLinkage.hasNext();) 
		{
			t_objType = t_iterLinkage.next().getParentLinkageType();			
		}
		return t_objType;
	}
}