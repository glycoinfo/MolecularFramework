package org.eurocarbdb.MolecularFramework.util.similiarity.SearchEngine;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

import org.eurocarbdb.MolecularFramework.io.GlycoCT.GlycoCTLinkageComparator;
import org.eurocarbdb.MolecularFramework.sugar.GlycoEdge;
import org.eurocarbdb.MolecularFramework.sugar.GlycoGraphAlternative;
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
import org.eurocarbdb.MolecularFramework.sugar.UnvalidatedGlycoNode;
import org.eurocarbdb.MolecularFramework.util.analytical.mass.GlycoVisitorRepeatLinkType;
import org.eurocarbdb.MolecularFramework.util.traverser.GlycoTraverser;
import org.eurocarbdb.MolecularFramework.util.traverser.GlycoTraverserSimple;
import org.eurocarbdb.MolecularFramework.util.visitor.GlycoVisitor;
import org.eurocarbdb.MolecularFramework.util.visitor.GlycoVisitorException;
import org.eurocarbdb.MolecularFramework.util.visitor.GlycoVisitorNodeType;



public class SearchVisitor implements GlycoVisitor {

	Vector <MatrixDataObject> v_Graph = new Vector <MatrixDataObject> ();
	private GlycoVisitorNodeType m_visNodeType = new GlycoVisitorNodeType();	
	private ArrayList <GlycoNode> rootRes = new ArrayList <GlycoNode> ();
	
	public void clear() {
		v_Graph.removeAllElements();
		this.rootRes.clear();
	}

	public GlycoTraverser getTraverser(GlycoVisitor a_objVisitor) throws GlycoVisitorException {
		return new GlycoTraverserSimple(a_objVisitor);
	}

	public void start(Sugar a_objSugar) throws GlycoVisitorException {
		this.clear();
		
		try {
			this.rootRes=a_objSugar.getRootNodes();
		} catch (GlycoconjugateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		GlycoTraverser t_objTraverser = this.getTraverser(this);
		t_objTraverser.traverseGraph(a_objSugar);

		
		
		// Undetermined elements ignored


	}

	public void visit(Monosaccharide a_objMonosaccharid) throws GlycoVisitorException {

		// put monosaccharide to vector if all children are normal nodes	
		Boolean normalNode=true;		
		for (GlycoEdge edge : a_objMonosaccharid.getChildEdges()){
			if (this.m_visNodeType.getNodeType(edge.getChild())==GlycoVisitorNodeType.ALTERNATIVE)
			{normalNode=false;}
			if (this.m_visNodeType.getNodeType(edge.getChild())==GlycoVisitorNodeType.CYCLIC)
			{normalNode=false;}
			if (this.m_visNodeType.getNodeType(edge.getChild())==GlycoVisitorNodeType.REPEAT)
			{normalNode=false;}
			if (this.m_visNodeType.getNodeType(edge.getChild())==GlycoVisitorNodeType.CYCLIC)
			{normalNode=false;}
		}		
		if (normalNode ){
			MatrixDataObject MDO = new MatrixDataObject ();
			MDO.setNode(a_objMonosaccharid);
			MDO.setChildren(a_objMonosaccharid.getChildEdges());	
			
			
			if (this.rootRes.contains(a_objMonosaccharid)){
				MDO.reducingEnd=true;
			}			
			this.v_Graph.add(MDO);	
		}
	}




	public void visit(NonMonosaccharide a_objResidue) throws GlycoVisitorException {
		
	}

	public void visit(SugarUnitRepeat a_objRepeat) throws GlycoVisitorException {
		//traverse normal Repeating Unit
		GlycoTraverser t_traverser = this.getTraverser(this);
		if (a_objRepeat.getNodes().size()>1){
		t_traverser.traverseGraph(a_objRepeat);
		}

		// Add to vector: IN Linkage 
		GlycoNode t_objNode = a_objRepeat.getParentNode();
		if ( t_objNode != null )
		{
			// recursive parent determination
			GlycoNode t_objParent = this.getSimpleNode(t_objNode,true);
			if ( t_objParent != null )
			{
				MatrixDataObject MDO = new MatrixDataObject ();
				MDO.setNode(t_objNode);

				ArrayList<GlycoEdge> t_aFinal = new ArrayList<GlycoEdge>();

				// copy and add new altered IN EDGE
				try {
					GlycoEdge t_edge = a_objRepeat.getParentEdge().copy();
					ArrayList <Linkage> linkages = t_edge.getGlycosidicLinkages();
					
					// change 'n' linkage flag			
					GlycoVisitorRepeatLinkType t_vis = new GlycoVisitorRepeatLinkType ();
					t_vis.setRepeatIn(true);
					t_vis.visit(a_objRepeat);
					GlycoEdge CorrectedEge = t_vis.getEdge();
					
					for (Linkage lin : linkages){
											
						for (Linkage linCorrected : CorrectedEge.getGlycosidicLinkages()){
							
							if (lin.getChildLinkages().containsAll(linCorrected.getChildLinkages())){
								
								lin.setChildLinkageType(linCorrected.getChildLinkageType());								
							}							
						}						
					}
					
					GlycoEdge t_newEdge = new GlycoEdge();
					t_newEdge.setGlycosidicLinkages(linkages);
					t_newEdge.setChild(this.getSimpleNode(a_objRepeat.getRepeatLinkage().getChild(),true));
					t_newEdge.setParent(t_objNode);
					t_aFinal.add(t_newEdge);

				} catch (GlycoconjugateException e) {
					e.printStackTrace();
				}
				// add "normal" other linkages
				for ( GlycoEdge t_edge : t_objNode.getChildEdges()){
					if (this.m_visNodeType.isMonosaccharide(t_edge.getChild())||
							this.m_visNodeType.isNonMonosaccharide(t_edge.getChild()))
					{
						t_aFinal.add(t_edge);
					}

				}
				MDO.setChildren(t_aFinal);
				this.v_Graph.add(MDO);

			}
		}

		// Add to vector: OUT 

		GlycoNode Parent = a_objRepeat.getRepeatLinkage().getParent();		
		ArrayList <GlycoEdge> EdgesOut = a_objRepeat.getChildEdges();

		if ( EdgesOut.size()!=0 )
		{
			MatrixDataObject MDO = new MatrixDataObject ();
			MDO.setNode(Parent);

			ArrayList<GlycoEdge> t_aFinal = new ArrayList<GlycoEdge>();

			for (GlycoEdge t_outgoing : EdgesOut){				
				GlycoEdge t_edge;
				try {
					t_edge = t_outgoing.copy();
					ArrayList <Linkage> linkages = t_edge.getGlycosidicLinkages();	
					
					// change 'n' linkage flag			
					GlycoVisitorRepeatLinkType t_vis = new GlycoVisitorRepeatLinkType ();
					t_vis.setRepeatIn(false);
					t_vis.visit(a_objRepeat);
					GlycoEdge CorrectedEge = t_vis.getEdge();
					
					for (Linkage lin : linkages){
											
						for (Linkage linCorrected : CorrectedEge.getGlycosidicLinkages()){
							
							if (lin.getParentLinkages().containsAll(linCorrected.getParentLinkages())){
								
								lin.setParentLinkageType(linCorrected.getParentLinkageType());								
							}							
						}						
					}
					
					GlycoEdge t_newEdge = new GlycoEdge();
					t_newEdge.setGlycosidicLinkages(linkages);
					t_newEdge.setChild(t_outgoing.getChild());
					t_newEdge.setParent(Parent);
					t_aFinal.add(t_newEdge);
				} catch (GlycoconjugateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
			MDO.setChildren(t_aFinal);
			this.v_Graph.add(MDO);

		}

		// Add to vector: Internal linkage		
		GlycoEdge internal = a_objRepeat.getRepeatLinkage();
		MatrixDataObject MDO = new MatrixDataObject ();
		MDO.setNode(internal.getParent());

		ArrayList<GlycoEdge> t_Edges = new ArrayList<GlycoEdge>();
		t_Edges.add(internal);
		t_Edges.addAll(internal.getParent().getChildEdges());
		
		MDO.setChildren(t_Edges);
		this.v_Graph.add(MDO);



	}

	public void visit(Substituent a_objSubstituent)	throws GlycoVisitorException {
		Boolean normalNode=true;		
		for (GlycoEdge edge : a_objSubstituent.getChildEdges()){
			if (this.m_visNodeType.getNodeType(edge.getChild())==GlycoVisitorNodeType.ALTERNATIVE)
			{normalNode=false;}
			if (this.m_visNodeType.getNodeType(edge.getChild())==GlycoVisitorNodeType.CYCLIC)
			{normalNode=false;}
			if (this.m_visNodeType.getNodeType(edge.getChild())==GlycoVisitorNodeType.REPEAT)
			{normalNode=false;}
			if (this.m_visNodeType.getNodeType(edge.getChild())==GlycoVisitorNodeType.CYCLIC)
			{normalNode=false;}
		}		
		if (normalNode ){
			MatrixDataObject MDO = new MatrixDataObject ();
			MDO.setNode(a_objSubstituent);
			MDO.setChildren(a_objSubstituent.getChildEdges());
			this.v_Graph.add(MDO);	
		}
	}


	

	public void visit(SugarUnitCyclic a_objCyclic) throws GlycoVisitorException {

		GlycoNode t_objParent = this.getSimpleNode(a_objCyclic.getParentNode(),false);
		if ( t_objParent != null )
		{

			GlycoNode t_objChild = this.getSimpleNode(a_objCyclic.getCyclicStart(),true);
			if ( t_objChild != null )
			{
				MatrixDataObject MDO = new MatrixDataObject ();
				MDO.setNode(t_objParent);

				ArrayList<GlycoEdge> t_Edges = new ArrayList<GlycoEdge>();
				try {
					GlycoEdge t_edge = a_objCyclic.getParentEdge().copy();
					t_edge.setParent(t_objParent);
					t_edge.setChild(t_objChild);
					t_Edges.add(t_edge);					
					t_Edges.addAll(t_objParent.getChildEdges());				
					t_Edges.remove(a_objCyclic.getParentEdge());	
					MDO.setChildren(t_Edges);
					this.v_Graph.add(MDO);
					
				} catch (GlycoconjugateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				

				
			}
		}

	}

	public void visit(SugarUnitAlternative a_objAlternative){


		try {
			GlycoTraverser t_traverser;
			t_traverser = this.getTraverser(this);
			for (GlycoGraphAlternative t_objAlt : a_objAlternative.getAlternatives()){				
				t_traverser.traverseGraph(t_objAlt);				
				// LEAD IN
				GlycoNode Parent = a_objAlternative.getParentNode();
				GlycoNode LeadInNode = t_objAlt.getLeadInNode();
				GlycoEdge t_edge;
				try {
					t_edge = a_objAlternative.getParentEdge().copy();
					t_edge.setParent(Parent);
					t_edge.setChild(LeadInNode);				
					ArrayList<GlycoEdge> t_Edges = new ArrayList<GlycoEdge>();
					t_Edges.add(t_edge);
					t_Edges.addAll(Parent.getChildEdges());
					t_Edges.remove(a_objAlternative.getParentEdge());					
					MatrixDataObject MDO = new MatrixDataObject ();
					MDO.setNode(Parent);
					MDO.setChildren(t_Edges);
					this.v_Graph.add(MDO);				

				} catch (GlycoconjugateException e) {
					throw new GlycoVisitorException(e.getMessage(),e);
				}


				// LEAD OUT		
				for (Iterator <GlycoNode> iter = t_objAlt.getLeadOutNodeToNode().keySet().iterator(); iter.hasNext();) {
					Parent = iter.next();
					GlycoNode Child = t_objAlt.getLeadOutNodeToNode().get(Parent);

					for (GlycoEdge t_edges : a_objAlternative.getChildEdges()){
						if (t_edges.getChild()==Child){
							try {
								t_edge = t_edges.copy();
								t_edge.setParent(Parent);
								t_edge.setChild(Child);				
								ArrayList<GlycoEdge> t_Edges = new ArrayList<GlycoEdge>();
								t_Edges.add(t_edge);
								t_Edges.addAll(Parent.getChildEdges());

								MatrixDataObject MDO = new MatrixDataObject ();
								MDO.setNode(Parent);
								MDO.setChildren(t_Edges);
								this.v_Graph.add(MDO);
							} catch (GlycoconjugateException e) {
								throw new GlycoVisitorException(e.getMessage(),e);
							}							 
						}
					}

				}



			}
		} catch (GlycoVisitorException e) {

		}	

	}

	public void visit(UnvalidatedGlycoNode a_objUnvalidated) throws GlycoVisitorException{
	//	 put monosaccharide to vector if all children are monosaccharides	
		Boolean normalNode=true;		
		for (GlycoEdge edge : a_objUnvalidated.getChildEdges()){
			if (this.m_visNodeType.getNodeType(edge.getChild())==GlycoVisitorNodeType.ALTERNATIVE)
			{normalNode=false;}
			if (this.m_visNodeType.getNodeType(edge.getChild())==GlycoVisitorNodeType.CYCLIC)
			{normalNode=false;}
			if (this.m_visNodeType.getNodeType(edge.getChild())==GlycoVisitorNodeType.REPEAT)
			{normalNode=false;}
			if (this.m_visNodeType.getNodeType(edge.getChild())==GlycoVisitorNodeType.CYCLIC)
			{normalNode=false;}
		}		
	if (normalNode){
		MatrixDataObject MDO = new MatrixDataObject ();
		MDO.setNode(a_objUnvalidated);
		MDO.setChildren(a_objUnvalidated.getChildEdges());
		this.v_Graph.add(MDO);	
	}
		
	}

	public void visit(GlycoEdge a_objLinkage) throws GlycoVisitorException {
		// Do nothing

	}

	public Vector<MatrixDataObject> getVector() {
		//TODO process underdetermined objects
		return v_Graph;
	}

	private GlycoNode getSimpleNode(GlycoNode a_objResidue, boolean a_bRepeatInGoingLinkage) throws GlycoVisitorException 
	{
		int t_iNodeType = this.m_visNodeType.getNodeType(a_objResidue);
		
		if ( t_iNodeType == GlycoVisitorNodeType.REPEAT )
		{
			if ( a_bRepeatInGoingLinkage )
			{
				return this.getSimpleNode(
						this.m_visNodeType.getSugarUnitRepeat(a_objResidue).getRepeatLinkage().getChild(),
						a_bRepeatInGoingLinkage);
			}
			else
			{
				return this.getSimpleNode(
						this.m_visNodeType.getSugarUnitRepeat(a_objResidue).getRepeatLinkage().getParent(),
						a_bRepeatInGoingLinkage);
			}
		}
		
		else if ( t_iNodeType == GlycoVisitorNodeType.CYCLIC )
		{			
			this.getSimpleNode(this.m_visNodeType.getSugarUnitCyclic(a_objResidue).getCyclicStart(), a_bRepeatInGoingLinkage);
		}
		
		else if ( t_iNodeType == GlycoVisitorNodeType.ALTERNATIVE )
		{
			this.getSimpleNode(this.m_visNodeType.getSugarUnitAlternative(a_objResidue), a_bRepeatInGoingLinkage);
		}
		
		return a_objResidue;
	}




}
