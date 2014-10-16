package org.eurocarbdb.MolecularFramework.util.similiarity.SearchEngine;

import java.util.ArrayList;
import java.util.Collections;

import org.eurocarbdb.MolecularFramework.io.GlycoCT.GlycoCTLinkageComparator;
import org.eurocarbdb.MolecularFramework.sugar.GlycoEdge;
import org.eurocarbdb.MolecularFramework.sugar.Linkage;

public class StandardEdgeComparator implements EdgeComparator {

	public int compare(GlycoEdge arg0, GlycoEdge arg1) {

		ArrayList <Linkage> t_aLinkages0 = arg0.getGlycosidicLinkages();
		ArrayList <Linkage> t_aLinkages1 = arg1.getGlycosidicLinkages();		

		for (Linkage lin : t_aLinkages0){
			for (Integer pos : lin.getChildLinkages()){
				if (pos==Linkage.UNKNOWN_POSITION){
					return 0;
				}		
			}
		}
		for (Linkage lin : t_aLinkages1){
			for (Integer pos : lin.getChildLinkages()){
				if (pos==Linkage.UNKNOWN_POSITION){
					return 0;
				}		
			}
		}
		for (Linkage lin : t_aLinkages0){
			for (Integer pos : lin.getParentLinkages()){
				if (pos==Linkage.UNKNOWN_POSITION){
					return 0;
				}		
			}
		}
		for (Linkage lin : t_aLinkages1){
			for (Integer pos : lin.getParentLinkages()){
				if (pos==Linkage.UNKNOWN_POSITION){
					return 0;
				}		
			}
		}
			// sort LinkageList
			GlycoCTLinkageComparator t_oLinComp = new GlycoCTLinkageComparator ();
			Collections.sort(t_aLinkages0,t_oLinComp);
			Collections.sort(t_aLinkages1,t_oLinComp);
			
			Integer result = 0;	
			
			// if of equal size, compare linkages inside GlycoEdges	via GlycoCT Comparator	
			// Linkage comparison		
			
			if (t_aLinkages0.size()==t_aLinkages1.size())
			{
				for (int i = 0; i < t_aLinkages0.size(); i++) {
					if (t_oLinComp.compare(t_aLinkages0.get(i),t_aLinkages1.get(i))!=0){
						result=t_oLinComp.compare(t_aLinkages0.get(i),t_aLinkages1.get(i));
					}

				}

			}
			// non-equal size of linkages list
			else {
				
				// Parent Linkages Integer Comparison
				ArrayList<Integer> Parents0 = new ArrayList<Integer>();				
				for (int i = 0; i < t_aLinkages0.size(); i++) {
					Linkage tempLin = t_aLinkages0.get(i);					
					for (Integer tempInt : tempLin.getParentLinkages()){						
						Parents0.add(tempInt);						
					}					
				}				
				Collections.sort(Parents0);
				
				ArrayList<Integer> Parents1 = new ArrayList<Integer>();				
				for (int i = 0; i < t_aLinkages1.size(); i++) {
					Linkage tempLin = t_aLinkages1.get(i);					
					for (Integer tempInt : tempLin.getParentLinkages()){						
						Parents1.add(tempInt);						
					}					
				}				
				Collections.sort(Parents1);				
				
				if (Parents0.size()>Parents1.size()){					
					for (Integer t : Parents1){
						if (!Parents0.contains(t)){
							result=1;
						}
					}
					
				}
				else {
					for (Integer t : Parents0){
						if (!Parents1.contains(t)){
							result=1;
						}
					}
				}
				// Child Linkages Integer Comparison
				ArrayList<Integer> Child0 = new ArrayList<Integer>();				
				for (int i = 0; i < t_aLinkages0.size(); i++) {
					Linkage tempLin = t_aLinkages0.get(i);					
					for (Integer tempInt : tempLin.getChildLinkages()){						
						Child0.add(tempInt);						
					}					
				}				
				Collections.sort(Child0);
				
				ArrayList<Integer> Child1 = new ArrayList<Integer>();				
				for (int i = 0; i < t_aLinkages1.size(); i++) {
					Linkage tempLin = t_aLinkages1.get(i);					
					for (Integer tempInt : tempLin.getChildLinkages()){						
						Child1.add(tempInt);						
					}					
				}				
				Collections.sort(Child1);				
				
				if (Child0.size()>Child1.size()){					
					for (Integer t : Child1){
						if (!Child0.contains(t)){
							result=1;
						}
					}
					
				}
				else {
					for (Integer t : Child0){
						if (!Child1.contains(t)){
							result=1;
						}
					}
				}
				
				
			}

			return result;
		}

	}
