/**
 * 
 */
package org.eurocarbdb.MolecularFramework.util.similiarity.PairSimiliarity;


import java.util.ArrayList;
import org.eurocarbdb.MolecularFramework.sugar.Sugar;
import org.eurocarbdb.MolecularFramework.util.analytical.disaccharide.Disaccharide;
import org.eurocarbdb.MolecularFramework.util.analytical.disaccharide.GlycoVisitorDisaccharide;
import org.eurocarbdb.MolecularFramework.util.similiarity.SearchEngine.EdgeComparator;
import org.eurocarbdb.MolecularFramework.util.similiarity.SearchEngine.NodeComparator;
import org.eurocarbdb.MolecularFramework.util.similiarity.SearchEngine.StandardEdgeComparator;
import org.eurocarbdb.MolecularFramework.util.similiarity.SearchEngine.StandardNodeComparator;
import org.eurocarbdb.MolecularFramework.util.visitor.GlycoVisitorException;



/**
 * @author sherget
 *
 */
public class PairSimiliarity {	

	private ArrayList <Disaccharide> m_aG1Pairs = new ArrayList <Disaccharide> ();
	private ArrayList <Disaccharide> m_aG2Pairs = new ArrayList <Disaccharide> ();
	private ArrayList <Disaccharide> m_aResult = new ArrayList <Disaccharide> ();
	private int score;
	private int size;

	private NodeComparator t_oNodeComparator = new StandardNodeComparator ();
	private EdgeComparator t_oCompEdge = new StandardEdgeComparator ();

	public void calculate (Sugar g1, Sugar g2){
		this.clear();
		// Decomposition
		try {
			GlycoVisitorDisaccharide o_vis = new GlycoVisitorDisaccharide ();
			o_vis.start(g1);
			this.m_aG1Pairs = o_vis.getDisaccharide();
		} catch (GlycoVisitorException e) {

			e.printStackTrace();
		}
		try {
			GlycoVisitorDisaccharide o_vis = new GlycoVisitorDisaccharide ();
			o_vis.start(g2);
			this.m_aG2Pairs = o_vis.getDisaccharide();
		} catch (GlycoVisitorException e) {

			e.printStackTrace();
		}
		//Size comparison
		if (this.m_aG1Pairs.size()>=this.m_aG2Pairs.size()){
			this.size = this.m_aG1Pairs.size();
		}
		else {this.size = this.m_aG2Pairs.size();}

		// compare arrays and compose result		
		for (Disaccharide p1 : this.m_aG1Pairs){
			for (Disaccharide p2 : this.m_aG2Pairs){

				//GlycoVisitorNodeType t_oCompNodeType = new GlycoVisitorNodeType();

				if (this.t_oNodeComparator.compare(p1.getParent(),p2.getParent())==0 &&
						this.t_oNodeComparator.compare(p1.getChild(),p2.getChild())==0 &&
						this.t_oCompEdge.compare(p1.getLinkage(),p2.getLinkage())==0 &&
						p1.getTouched().equals(false) &&
						p2.getTouched().equals(false)){							
					this.m_aResult.add(p2);
					this.score++;		
					p1.setTouched(true);
					p2.setTouched(true);
				}				
			}
		}	
	}
	public Integer getScore (){
		return this.score;
	}

	public Float getNormalizedScore (){
		Float f_temp;
		f_temp = this.score /Float.valueOf(this.size);
		return (f_temp);
	}
	public ArrayList <Disaccharide> getPairs (){
		return this.m_aResult;
	}	
	private void clear(){
		score=0;
		size=0;
		m_aG1Pairs.clear();
		m_aG2Pairs.clear();
		m_aResult.clear();
	}
	public void setComparatorEdge(EdgeComparator compEdge) {
		t_oCompEdge = compEdge;
	}
	public void setNodeComparator(NodeComparator nodeComparator) {
		t_oNodeComparator = nodeComparator;
	}	
}
