package org.eurocarbdb.MolecularFramework.util.analytical.GraphFragmenter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;
import java.util.Map.Entry;

import org.eurocarbdb.MolecularFramework.sugar.GlycoEdge;
import org.eurocarbdb.MolecularFramework.sugar.GlycoNode;
import org.eurocarbdb.MolecularFramework.sugar.GlycoconjugateException;
import org.eurocarbdb.MolecularFramework.sugar.Linkage;
import org.eurocarbdb.MolecularFramework.sugar.Sugar;
import org.eurocarbdb.MolecularFramework.util.similiarity.SearchEngine.MatrixDataObject;
import org.eurocarbdb.MolecularFramework.util.similiarity.SearchEngine.SearchVisitor;
import org.eurocarbdb.MolecularFramework.util.visitor.GlycoVisitorException;
import org.eurocarbdb.MolecularFramework.util.visitor.GlycoVisitorNodeType;

public class SugarFragmenter {
	
	private Vector<MatrixDataObject> v_SugarGraphFlat = new Vector<MatrixDataObject>();
	
	private ArrayList<Sugar> result = new ArrayList<Sugar>();
	private ArrayList<MDOList> connectedEntitiesMDO = new ArrayList<MDOList>();
	private Integer upperBound = 3;
	private Integer lowerBound = 0;
	private HashMap<GlycoNode, GlycoNode> lookupHash = new HashMap<GlycoNode, GlycoNode>();
	private ArrayList<GlycoNode> sugArray = new ArrayList<GlycoNode>();
	
	public ArrayList<Sugar> fragment(Sugar sugar) {
		
		this.clear();
		// traverse graphs and vector with MDO "flat tree" data structure
		SearchVisitor sv = new SearchVisitor();
		try {
			sv.start(sugar);
		} catch (GlycoVisitorException e) {
			e.printStackTrace();
		}
		// all putative starting points in vector
		this.v_SugarGraphFlat = sv.getVector();
		
		// Fragment recursivly
		FragmentationRoutine();
		
		// Size exclusion
		ArrayList<MDOList> temp = new ArrayList<MDOList>();
		for (MDOList m : this.connectedEntitiesMDO) {
			if (((m.getMSCount() < upperBound) && (m.getMSCount() > lowerBound))) {
				temp.add(m);
			}
		}
		this.connectedEntitiesMDO = temp;
		// Make sugars
		GenerateSugars();
		return this.result;
	}
	
	private void clear() {
		this.connectedEntitiesMDO.clear();
		this.lookupHash.clear();
		this.result.clear();
		this.sugArray.clear();
		this.v_SugarGraphFlat.clear();
	}
	
	private void GenerateSugars() {
		// Iteriere ueber Loesungsmenge
		for (MDOList MDOSugar : this.connectedEntitiesMDO) {
			
			if (MDOSugar.MDOList.size() == 0) {
				continue;
			}
			ArrayList<MatrixDataObject> connectedEntities = MDOSugar.MDOList;
			
			// Fill Hash for lookup of original vs. solution nodes + put copy of
			// nodes in sugar
			Sugar sug = new Sugar();
			this.lookupHash.clear();
			this.sugArray.clear();
			
			for (MatrixDataObject t_mdo : connectedEntities) {
				GlycoNode t_node;
				try {
					t_node = t_mdo.getNode().copy();
					sug.addNode(t_node);
					this.lookupHash.put(t_node, t_mdo.getNode());
					this.sugArray.add(t_node);
				} catch (GlycoconjugateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			HashSet<GlycoNode> usedParents = new HashSet<GlycoNode>();
			HashSet<GlycoNode> usedChildren = new HashSet<GlycoNode>();
			usedChildren.add(this.sugArray.get(0));
			// get connectivities right
			for (GlycoNode nodeProcessed : this.sugArray) {
				MatrixDataObject t_corresponding = new MatrixDataObject();
				// finde korrespondierendes Original in connected entities
				for (MatrixDataObject t_mdo : connectedEntities) {
					if (t_mdo.getNode() == this.lookupHash.get(nodeProcessed)) {
						t_corresponding = t_mdo;
						break;
					}
				}
				// get all children of unprocessed t_mdo and add them correctly
				// to g. Add to list of used Nodes
				for (GlycoEdge t_edgeConnectedEntities : t_corresponding
						.getChildren()) {
					
					ArrayList<Linkage> lin = t_edgeConnectedEntities
					.getGlycosidicLinkages();
					GlycoEdge t_edge = new GlycoEdge();
					GlycoNode t_sugChild = null;
					
					// order as defined in this.sugArray; if not, then
					// optimisation problem
					testfornull: for (GlycoNode g : this.sugArray) {
						for (Entry<GlycoNode, GlycoNode> t_node : this.lookupHash
								.entrySet()) {
							if ((t_node.getKey() == g)
									&& (t_node.getValue() == t_edgeConnectedEntities
											.getChild())
											&& (t_node.getKey() != nodeProcessed)
											&& !usedChildren.contains(t_node.getKey())) {
								t_sugChild = t_node.getKey();
								usedChildren.add(t_sugChild);
								break testfornull;
							}
						}
					}
					try {
						
						t_edge.setGlycosidicLinkages(lin);
						t_edge.setChild(t_sugChild);
						t_edge.setParent(nodeProcessed);
						if ((t_sugChild != null) && (t_edge != null)) {
							sug.addEdge(nodeProcessed, t_sugChild, t_edge);
							usedParents.add(nodeProcessed);
							
						}
					} catch (GlycoconjugateException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			this.result.add(sug);
		}
	}
	
	private void FragmentationRoutine() {
		// every Node is a potential starting point
		for (MatrixDataObject startingPoint : this.v_SugarGraphFlat) {
			
			MDOList t_resultListMDO = new MDOList();
			t_resultListMDO.addMDO(startingPoint);
			
			ArrayList<GlycoNode> children = new ArrayList<GlycoNode>();
			
			for (GlycoEdge edge : startingPoint.getChildren()){
				children.add(edge.getChild());				
			}
			
			permutate(t_resultListMDO, children);
			
		}
	}
	
	private void permutate(MDOList startingListWithParent,ArrayList<GlycoNode> startingNodes) {
		
		// permutiere alle Kinderlinkages und addiere nodes in Abhaengigkeit von SubstFlag.
		// Falls node > 1 in vector, dupliziere, da Degeneration. Falls keine weiteren Monosaccharidekinder,
		// setze terminal flag
		
		ArrayList<GlycoNode> ChildNodesCombinatorics = new ArrayList<GlycoNode>();	
		for (int a = 1; a <= startingNodes.size(); a++) {
			CombinationGenerator x = new CombinationGenerator(startingNodes.size(), a);
			int[] indices;
			while (x.hasMore()) {
				ChildNodesCombinatorics.clear();
				indices = x.getNext();
				for (int element : indices) {
					ChildNodesCombinatorics.add(startingNodes
							.get(element));
				}
				
				// Kinderknoten permutiert in Combinatorics vorliegend
				// Finde passende Knoten in main graph und addiere. Duplikate wg. REP!				
				ArrayList <MDOList> t_result = new ArrayList<MDOList> ();
				t_result.add(startingListWithParent.copy());				
				ArrayList <GlycoNode> history = new ArrayList<GlycoNode>();
				ArrayList<MatrixDataObject> altVerzweigung = new ArrayList<MatrixDataObject>();
				
				for (MatrixDataObject t_mdo : this.v_SugarGraphFlat) {
					for (GlycoNode node : ChildNodesCombinatorics){
						if (t_mdo.getNode()==node){
							// nachpruefen, ob schon mal referenziert.
							// wenn nein, addieren an alle Elemente
							if (!history.contains(t_mdo.getNode())){
								for (MDOList t_element : t_result){
									t_element.addMDO(t_mdo);	
									history.add(t_mdo.getNode());									
									for (GlycoEdge t_edge : t_mdo.getChildren()){									
										t_element.NodeList.add(t_edge.getChild());										
									}									
								}								
							}	
							// Alternativerzweigung des Graphen! Merken in altVerzweigung
							else {				
								altVerzweigung.add(t_mdo);								
							}
						}
					}
				}			 
				
				for (MDOList t_element : t_result){
					this.connectedEntitiesMDO.add(t_element);
					
					// Rekursion mit Abbruch
					if (t_element.getMSCount() < this.upperBound) {
						permutate(t_element,t_element.NodeList );
					}			
				}
				
				for (MatrixDataObject altMdo : altVerzweigung){
					// Entferne mdo und ersetze durch altMdo
					MDOList temp=t_result.get(0).clone();
					// TODO PROBLEM::: Aufloesen der Verknuepfungen ist falsch!!!! Loeschen funkt nicht, cave endless loop
					for (MatrixDataObject mdo : t_result.get(0).MDOList){
						if (mdo.getNode()==altMdo.getNode() && history.contains(mdo.getNode())){							
							temp.remove(mdo);
							// Update der start liste: Loesche Referenz auf die alten Tochterknoten
							for (GlycoEdge edge : mdo.getChildren()){
								temp.remove(edge.getChild());								
							}		
							
						}
					}	
					//Addiere neue kinder
					for (GlycoEdge edge : altMdo.getChildren()){
						t_result.get(0).NodeList.add(edge.getChild());
					}					
					t_result.get(0).MDOList.add(altMdo);
					
					for (MDOList t_element : t_result){
						this.connectedEntitiesMDO.add(t_element);
						
						// Rekursion mit Abbruch
						if (t_element.getMSCount() < this.upperBound) {
							permutate(t_element,t_element.NodeList );
						}					
					}			
				}
				
										
			}
		}			
	}
	
	
	
	
	
	public void setSize(Integer size) {
		this.lowerBound = 0;
		this.upperBound = size; 
	}
	
	public void setUpperSizeLimit(Integer size) {
		this.upperBound = size;
	}
	
	public void setLowerSizeLimit(Integer size) {
		this.lowerBound = size;
	}
	
	public class MDOList implements Cloneable{
		
		public ArrayList<MatrixDataObject> MDOList = new ArrayList<MatrixDataObject>();
		public ArrayList<GlycoNode> NodeList = new ArrayList<GlycoNode>();
		
		public MDOList clone (){
			try {
				super.clone();
			} catch (CloneNotSupportedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			MDOList temp = new MDOList (); 
			temp.MDOList= (ArrayList<MatrixDataObject>) this.MDOList.clone();
			temp.NodeList= (ArrayList<GlycoNode>) this.NodeList.clone();
			return temp;
			
		}
		
		public void addMDO(MatrixDataObject mdo) {
			
			this.MDOList.add(mdo);
			
		}			
		
		public void remove(MatrixDataObject mdo){
			this.MDOList.remove(mdo);
		}
		
		public void remove(GlycoNode node) {
			ArrayList <MatrixDataObject> candidates = new ArrayList<MatrixDataObject> ();
			
			for (MatrixDataObject object : this.MDOList) {
				if (object.getNode()==node){
					candidates.add(object);
				}
			}			
			
			this.MDOList.removeAll(candidates);			
		}
		public Integer getMSCount (){
			Integer counter =0;
			GlycoVisitorNodeType gType= new GlycoVisitorNodeType ();
			
			for (MatrixDataObject m : this.MDOList){
				gType.clear();
				try {
					if (gType.isMonosaccharide(m.getNode())){
						counter++;
					}
				} catch (GlycoVisitorException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return counter;
		}
		
		
		public MatrixDataObject getMDO(GlycoNode node) {
			
			for (MatrixDataObject mdo : MDOList) {
				if (mdo.getNode() == node) {
					return (mdo);
				}
			}
			return null;
		}
		
		public MDOList copy() {
			MDOList copy = new MDOList();
			
			for (MatrixDataObject m : this.MDOList) {		
				
				copy.MDOList.add(m.copy());
			}
			
			for (GlycoNode node:this.NodeList){
				
					copy.NodeList.add(node);
				
				
			}	
			return copy;
		}
	}
}
