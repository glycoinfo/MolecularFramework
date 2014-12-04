package org.eurocarbdb.MolecularFramework.util.similiarity.SearchEngine;

import java.util.ArrayList;

import org.eurocarbdb.MolecularFramework.sugar.GlycoEdge;
import org.eurocarbdb.MolecularFramework.sugar.GlycoNode;
import org.eurocarbdb.MolecularFramework.sugar.GlycoconjugateException;


public class MatrixDataObject {
	GlycoNode fragment;
	Boolean visited=false;	
	Boolean reducingEnd=false;	
	ArrayList <GlycoEdge> children = new ArrayList<GlycoEdge> ();
	
	public void setChildren (ArrayList <GlycoEdge> a_children)	{
		this.children=a_children;
	}
	
	public void addChild (GlycoEdge g){
		this.children.add(g);
	}
	
	public ArrayList <GlycoEdge> getChildren ()	{
		return (this.children);
	}


	public GlycoNode getNode() {
		return fragment;
	}

	public void setNode(GlycoNode a_oGraph) {
		this.fragment = a_oGraph;
	}

	public Boolean getVisited() {
		return visited;
	}

	public void setVisited(Boolean visited) {
		this.visited = visited;
	}	
	
	public MatrixDataObject copy (){
		MatrixDataObject res = new MatrixDataObject();
	
		res.visited=this.visited;
		res.reducingEnd=this.reducingEnd;
		
		
			res.fragment=this.getNode();
			ArrayList <GlycoEdge> a_g = new ArrayList<GlycoEdge> ();
			for (GlycoEdge g:this.children){
				a_g.add(g);
			}
			res.children=a_g;			
			
			
		
		return res;
		
		
	}

	public Boolean getReducingEnd() {
		return reducingEnd;
	}

	public void setReducingEnd(Boolean reducingEnd) {
		this.reducingEnd = reducingEnd;
	}
	
}
