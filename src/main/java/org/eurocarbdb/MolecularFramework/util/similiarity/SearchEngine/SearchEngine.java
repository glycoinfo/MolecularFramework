package org.eurocarbdb.MolecularFramework.util.similiarity.SearchEngine;

import java.util.Vector;

import org.eurocarbdb.MolecularFramework.sugar.GlycoEdge;
import org.eurocarbdb.MolecularFramework.sugar.GlycoNode;
import org.eurocarbdb.MolecularFramework.sugar.GlycoconjugateException;
import org.eurocarbdb.MolecularFramework.sugar.Sugar;
import org.eurocarbdb.MolecularFramework.util.visitor.GlycoVisitorCountNodeType;
import org.eurocarbdb.MolecularFramework.util.visitor.GlycoVisitorException;
/**
 * A search engine for sugar sequences.
 * Implementing MCS, substructure and exact match functionality.
 *
 * @author Stephan Herget
 * @version 1.0
 */


public class SearchEngine {

	private Vector <MatrixDataObject> v_query = new Vector <MatrixDataObject> ();
	private Vector <MatrixDataObject> v_queried = new Vector <MatrixDataObject> ();

	private Boolean OnlyReducingEnd = false;
	private NodeComparator NodeComparator = new StandardNodeComparator();
	private EdgeComparator EdgeComparator= new StandardEdgeComparator();
	private Sugar queried = null;
	private Sugar query = null;
	Sugar result = new Sugar();
	private int[][] m_aMatrix;
	private int score=0;
	private MatrixDataObject queryMax = new MatrixDataObject ();
	private MatrixDataObject queriedMax = new MatrixDataObject ();

	/**
	 * Set structure to be queried. 
	 */
	public void setQueriedStructure ( Sugar queriedStructure){
		this.queried=queriedStructure;
	}
	/**
	 * Set query structure. 
	 */
	public void setQueryStructure ( Sugar queryStructure) throws GlycoVisitorException{
		GlycoVisitorCountNodeType gvnt = new GlycoVisitorCountNodeType ();		
		gvnt.start(queryStructure);
		if (gvnt.getRepeatCount()>0){
			throw new GlycoVisitorException ("No Repeats as queries. Expand Repeat query!");
		}		
		this.query=queryStructure;
	}
	/**
	 * Set comparator for the residues. 
	 */
	public void setNodeComparator ( NodeComparator NodeComparator){
		this.NodeComparator = NodeComparator;
	}
	/**
	 * Set comparator for the linkages. 
	 */
	public void setEdgeComparator ( EdgeComparator EdgeComparator){
		this.EdgeComparator = EdgeComparator;
	}	
	/**
	 * Checks, if query and queried structure are exactly equivalent.
	 * @return Boolean, true if exact match, false if not exact match.
	 */
	public Boolean isExactMatch () throws SearchEngineException{
		try {
			this.match();
		} catch (GlycoVisitorException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} catch (GlycoconjugateException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} catch (SearchEngineException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		GlycoVisitorCountNodeType g_count = new GlycoVisitorCountNodeType();
		try {
			g_count.start(this.query);
		} catch (GlycoVisitorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Integer count = g_count.getMonosaccharideCount()+
		g_count.getNonMonosaccharideCount()+
		g_count.getSubstituentCount()+
		g_count.getUnvalidatedNodeCount();

		if (this.getMax().equals(count)){
			return (true);
		}
		else {
			return (false);
		}
	}
	/**
	 * Returns biggest number of matching residues.
	 * @return Number of matching residues.
	 */
	public Integer getScore (){
		return this.getMax();		
	}
	
	
	public float getResultSize (){
		return this.v_query.size() / (float) this.v_queried.size();		
	}


	/**
	 * Switch for reducing ends.
	 *
	 */
	public void restrictToReducingEnds (){
		this.OnlyReducingEnd=true;
	}


	public void match () throws GlycoVisitorException, GlycoconjugateException, SearchEngineException{
		if (this.queried==null || this.query==null){			
			throw new SearchEngineException ("You forgot to add sugars");
		}

		// traverse graphs and vector with MDO "flat tree" data structure		
		SearchVisitor sv = new SearchVisitor ();
		// Vector queried
		sv.start(queried);
		this.v_queried=sv.getVector();

		SearchVisitor sv1 = new SearchVisitor ();
		// Vector query
		sv1.start(query);
		this.v_query=sv1.getVector();


		// Perform Matrix initialisation
		this.m_aMatrix = new int[v_queried.size()][v_query.size()];

		// iterate over all matrix entries and start recursion		
		for (int i = 0; i < v_queried.size(); i++) {
			for (int q = 0; q < v_query.size(); q++) {				
				// zuruecksetzen
				this.score=0;
				for (MatrixDataObject mdo : this.v_queried){
					mdo.visited=false;
				}
				for (MatrixDataObject mdo : this.v_query){
					mdo.visited=false;
				}			
				this.m_aMatrix[i][q] = recursive (v_queried.get(i),v_query.get(q));
			}
		}


	}
	private int recursive(MatrixDataObject MDO_queried, MatrixDataObject MDO_query) throws GlycoconjugateException {

		GlycoNode queriedNode = MDO_queried.getNode();
		GlycoNode queryNode = MDO_query.getNode();

		if (this.NodeComparator.compare(queriedNode, queryNode)==0){
			// identity
			this.score++;		

			// iterate over all children
			for (GlycoEdge t_childEdge_queried : MDO_queried.getChildren()){
				for (GlycoEdge t_childEdge_query : MDO_query.getChildren()){

					// for identical children, descend
					if (this.EdgeComparator.compare(t_childEdge_queried, t_childEdge_query) == 0 &&
							this.NodeComparator.compare(t_childEdge_queried.getChild(),t_childEdge_query.getChild()) == 0){

						// iterate over vector 
						for (MatrixDataObject a : v_queried){
							for (MatrixDataObject b : v_query){

								// recursion
								if (t_childEdge_queried.getChild()==a.getNode() 
										&& 	t_childEdge_query.getChild()==b.getNode()  
										&& b.visited==false 
										&& this.score<v_query.size()
								){
									// block current node								
									block(b);									
									recursive (a,b);
								}								
							}
						}
					}
				}
			}
		}		
		return score;

	}

	private void block(MatrixDataObject mdo_queried) {
		for (MatrixDataObject mdo : this.v_queried){
			if (mdo_queried==mdo){
				mdo.visited=true;
			}
		}
		for (MatrixDataObject mdo : this.v_query){
			if (mdo_queried==mdo){
				mdo.visited=true;
			}
		}
	}
	public void plotMatrix(){
		SimpleGetNameVisitor snv = new SimpleGetNameVisitor ();
		for (int i = 0; i < v_queried.size(); i++) {
			GlycoNode g1 = v_queried.get(i).fragment;
			try {
				g1.accept(snv);
			} catch (GlycoVisitorException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.print(snv.getName().trim()+"\t");
			for (int q = 0; q < v_query.size(); q++) {

				System.out.print(this.m_aMatrix[i][q]+" ");	
			}	
			System.out.print("\n");
		}	
	}	

	private Integer getMax() {
		this.score=0;
		if (this.OnlyReducingEnd){
			Integer temp=0;			
			for (int t_counterG1 = 0; t_counterG1 < this.v_queried.size(); t_counterG1++) {			
				for (int t_counterG2 = 0; t_counterG2 < this.v_query.size(); t_counterG2++){							
					if (this.m_aMatrix[t_counterG1][t_counterG2]>temp){	
						if (v_queried.get(t_counterG1).reducingEnd==true){
							temp=this.m_aMatrix[t_counterG1][t_counterG2];
							this.score=temp;
							this.queriedMax=v_queried.get(t_counterG1);
							this.queryMax=v_query.get(t_counterG2);
						}					
					}					
				}				
			}


		}

		else {
			//	get max score from matrix regardless of reducing end
			Integer temp=0;			
			for (int t_counterG1 = 0; t_counterG1 < this.v_queried.size(); t_counterG1++) {			
				for (int t_counterG2 = 0; t_counterG2 < this.v_query.size(); t_counterG2++){							
					if (this.m_aMatrix[t_counterG1][t_counterG2]>temp){	


						temp=this.m_aMatrix[t_counterG1][t_counterG2];
						this.score=temp;
						this.queriedMax=v_queried.get(t_counterG1);
						this.queryMax=v_query.get(t_counterG2);
					}					
				}				
			}

		}		
		return this.score;
	}

	public Sugar getMaximumCommonSubstructure () {

		// trigger Maximum Finder
		this.getMax();

		// if at least one node hit
		if (this.score>0){
			try {
				// use root node
				result.addNode(this.queryMax.getNode().copy());
			} catch (GlycoconjugateException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			try {
				// reset MDO
				for (MatrixDataObject mdo : this.v_queried){
					mdo.visited=false;
				}
				for (MatrixDataObject mdo : this.v_query){
					mdo.visited=false;
				}			

				// recursive Buildup of Sugar			
				SugarBuild(this.queriedMax, this.queryMax);

			} catch (GlycoconjugateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}		
		}
		return result;
	}

	private void SugarBuild(MatrixDataObject MDO_queried, MatrixDataObject MDO_query) throws GlycoconjugateException {

		GlycoNode queriedNode = MDO_queried.getNode();
		GlycoNode queryNode = MDO_query.getNode();

		if (this.NodeComparator.compare(queriedNode, queryNode)==0){
			// identity

			// Iterate over all children
			for (GlycoEdge t_childEdge_queried : MDO_queried.getChildren()){
				for (GlycoEdge t_childEdge_query : MDO_query.getChildren()){

					// for identical children, descend
					if (this.EdgeComparator.compare(t_childEdge_queried, t_childEdge_query) == 0 &&
							this.NodeComparator.compare(t_childEdge_queried.getChild(),t_childEdge_query.getChild()) == 0){

						// descend to ALL putative children
						for (MatrixDataObject a : v_queried){
							for (MatrixDataObject b : v_query){
								// Rekursionsbedingungen
								if (t_childEdge_queried.getChild()==a.getNode() && 
										t_childEdge_query.getChild()==b.getNode()  &&
										MDO_queried.visited==false &&
										this.score<v_query.size()){
									// Sperren der aktuellen Knoten									
									block(b);
									result.addEdge(queryNode, t_childEdge_query.getChild(), t_childEdge_query);
									SugarBuild (a,b);
								}								
							}
						}

					}

				}
			}

		}		


	}
	
	/**
	 * @return the onlyReducingEnd
	 */
	public Boolean getOnlyReducingEnd() {
		return OnlyReducingEnd;
	}
	/**
	 * @param onlyReducingEnd the onlyReducingEnd to set
	 */
	public void setOnlyReducingEnd(Boolean onlyReducingEnd) {
		OnlyReducingEnd = onlyReducingEnd;
	}


}
