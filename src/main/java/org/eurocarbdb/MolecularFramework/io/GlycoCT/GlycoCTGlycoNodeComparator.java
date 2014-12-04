/**
 * 
 */
package org.eurocarbdb.MolecularFramework.io.GlycoCT;

import java.util.Comparator;


import org.eurocarbdb.MolecularFramework.sugar.GlycoNode;
import org.eurocarbdb.MolecularFramework.util.analytical.misc.GlycoVisitorCountNodeType;
import org.eurocarbdb.MolecularFramework.util.visitor.GlycoVisitorCountBranchingPoints;
import org.eurocarbdb.MolecularFramework.util.visitor.GlycoVisitorCountLongestBranch;
import org.eurocarbdb.MolecularFramework.util.visitor.GlycoVisitorCountResidueTerminal;
import org.eurocarbdb.MolecularFramework.util.visitor.GlycoVisitorException;

public class GlycoCTGlycoNodeComparator implements  Comparator<GlycoNode> {


	public int compare(GlycoNode r1, GlycoNode r2) {

		// First criterion: RESIDUE COUNT
		if (ResidueCount(r1,r2)!=0)
		{
			return (ResidueCount(r1,r2));
		} 			

		// Second criterion: LONGEST BRANCH
		if (LongestBranch(r1,r2)!=0){
			return (LongestBranch(r1,r2));
		}	
		// Third criterion: TERMINAL RESIDUE COUNT
		if (TerminalResidue(r1,r2)!=0){
			return (TerminalResidue(r1,r2));
		} 
		// Fourth criterion: BRANCHING POINTS COUNT
		if (BranchingCount(r1,r2)!=0){
			return (BranchingCount(r1,r2));
		} 		
		//Last criterion: ALPHANUM SORT OF REMAINING GLYCO - CT
		if (AlphaNum(r1,r2)!=0){
			return (AlphaNum(r1,r2));
		} 

		// Not able to discrimate subgraphs
		return 0;
	}

	private int BranchingCount(GlycoNode r1, GlycoNode r2) {
		int t_g1LongestBranch=0;
		int t_g2LongestBranch=0;

		GlycoVisitorCountBranchingPoints t_oLongest = new  GlycoVisitorCountBranchingPoints();
		try {
			t_oLongest.start(r1);
			t_g1LongestBranch = t_oLongest.getBranchingPointsCountResidue();
			t_oLongest.clear();
			t_oLongest.start(r2);
			t_g2LongestBranch = t_oLongest.getBranchingPointsCountResidue();

			if (t_g1LongestBranch < t_g2LongestBranch){
				return 1;
			}
			else if (t_g1LongestBranch > t_g2LongestBranch){
				return -1;
			}			
		} catch (GlycoVisitorException e) {
			e.printStackTrace();
		}		
		return 0;
	}

	private int TerminalResidue(GlycoNode r1, GlycoNode r2) {
		int t_g1LongestBranch=0;
		int t_g2LongestBranch=0;

		GlycoVisitorCountResidueTerminal t_oLongest = new  GlycoVisitorCountResidueTerminal();
		try {
			t_oLongest.start(r1);
			t_g1LongestBranch = t_oLongest.getTerminalCountResidue();

			t_oLongest.start(r2);
			t_g2LongestBranch = t_oLongest.getTerminalCountResidue();

			if (t_g1LongestBranch < t_g2LongestBranch){
				return 1;
			}
			else if (t_g1LongestBranch > t_g2LongestBranch){
				return -1;
			}			
		} catch (GlycoVisitorException e) {
			e.printStackTrace();
		}		
		return 0;
	}

	private int AlphaNum(GlycoNode r1, GlycoNode r2) {
		String t_g1StringGlycoCT="";
		String t_g2StringGlycoCT="";

		SugarExporterGlycoCTCondensed t_objGlycoCTExporter = new SugarExporterGlycoCTCondensed();
		try {
			t_objGlycoCTExporter.start(r1);
		} catch (GlycoVisitorException e) {
			e.printStackTrace();
		}				
		t_g1StringGlycoCT=t_objGlycoCTExporter.getHashCode();
		t_objGlycoCTExporter.clear();
		try {
			t_objGlycoCTExporter.start(r2);
		} catch (GlycoVisitorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}				
		t_g2StringGlycoCT=t_objGlycoCTExporter.getHashCode();	

		return (t_g2StringGlycoCT.compareTo(t_g1StringGlycoCT) * -1);
	}

	private int LongestBranch (GlycoNode r1, GlycoNode r2) {
		int t_g1LongestBranch=0;
		int t_g2LongestBranch=0;

		GlycoVisitorCountLongestBranch t_oLongest = new  GlycoVisitorCountLongestBranch();
		try {
			t_oLongest.start(r1);
			t_g1LongestBranch = t_oLongest.getLongestBranchResidue();

			t_oLongest.start(r2);
			t_g2LongestBranch = t_oLongest.getLongestBranchResidue();

			if (t_g1LongestBranch < t_g2LongestBranch){
				return 1;
			}
			else if (t_g1LongestBranch > t_g2LongestBranch){
				return -1;
			}			
		} catch (GlycoVisitorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		return 0;

	}
	private int ResidueCount (GlycoNode r1, GlycoNode r2) 
	{
		int t_g1LongestBranch=0;
		int t_g2LongestBranch=0;

		GlycoVisitorCountNodeType t_oLongest = new  GlycoVisitorCountNodeType();
		try {
			t_oLongest.start(r1);
			t_g1LongestBranch = 
				t_oLongest.getMonosaccharideCount()+
				t_oLongest.getNonMonosaccharideCount()+
				t_oLongest.getSubstituentCount()+
				t_oLongest.getAlternativeNodeCount();
			t_oLongest.clear();

			t_oLongest.start(r2);			
			t_g2LongestBranch = t_oLongest.getMonosaccharideCount()+
			t_oLongest.getNonMonosaccharideCount()+
			t_oLongest.getSubstituentCount()+
			t_oLongest.getAlternativeNodeCount();

			if (t_g1LongestBranch < t_g2LongestBranch){
				return 1;
			}
			else if (t_g1LongestBranch > t_g2LongestBranch){
				return -1;
			}			
		} catch (GlycoVisitorException e) {
			e.printStackTrace();
		}		
		return 0;

	}


}
