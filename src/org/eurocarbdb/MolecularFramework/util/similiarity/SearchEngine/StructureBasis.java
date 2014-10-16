package org.eurocarbdb.MolecularFramework.util.similiarity.SearchEngine;

import java.util.ArrayList;

import org.eurocarbdb.MolecularFramework.io.SugarImporterException;
import org.eurocarbdb.MolecularFramework.io.GlycoCT.SugarImporterGlycoCTCondensed;
import org.eurocarbdb.MolecularFramework.sugar.GlycoconjugateException;
import org.eurocarbdb.MolecularFramework.sugar.Sugar;
import org.eurocarbdb.MolecularFramework.util.visitor.GlycoVisitorException;

public class StructureBasis {

	ArrayList <String> CondensedCodes = new ArrayList<String> ();

	String query = 
		"RES\n" +
		"1b:a-dglc-HEX-1:5\n" +
		"2b:b-dglc-HEX-1:5\n" +
		"3b:a-dglc-HEX-1:5\n" +
		"LIN\n" +
		"1:1o(4+1)2d"+
		"2:2o(4+1)3d";

	/**
	 * @param args
	 */

	public void structureInit () {
		
//		this.CondensedCodes.add(
//				"RES\n" +
//				"1r:r1\n" +
//				"REP\n" +
//				"REP1:2o(4+1)2d=-1--1\n" +
//				"RES\n" +
//				"2b:b-dglc-HEX-1:5\n");
//		
//	
//	this.CondensedCodes.add(
//			"RES\n" +
//			"1b:a-dglc-HEX-1:5\n" +
//			"2r:r1\n" +
//			"LIN\n" +
//			"1:1o(4+1)2n" +
//			"REP\n" +
//			"REP1:3o(4+1)3d=-1--1\n" +
//			"RES\n" +
//			"3b:b-dglc-HEX-1:5\n");
	

	this.CondensedCodes.add(
			"RES\n" +
			"1b:a-dglc-HEX-1:5\n" +
			"2r:r1\n" +
			"3b:a-dglc-HEX-1:5\n" +
			"LIN\n" +
			"1:1o(4+1)2n" +
			"2:2n(4+1)3d" +
			"REP\n" +
			"REP1:4o(4+1)4d=-1--1\n" +
			"RES\n" +
			"4b:b-dglc-HEX-1:5\n");
	
}
	

	public void calculate (){

		try {
			SugarImporterGlycoCTCondensed g_in = new SugarImporterGlycoCTCondensed ();
			SearchEngine SE = new SearchEngine ();
			Sugar o_query = g_in.parse(this.query);
			SE.setQueryStructure(o_query);

			for (String code : this.CondensedCodes ){

				Sugar t_sug =g_in.parse(code);

				SE.setQueriedStructure(t_sug);

				SE.match();

				if(SE.isExactMatch()){
					System.out.println("NO."+ this.CondensedCodes.indexOf(code)+" is hit!");
					SE.plotMatrix();
					System.out.println("\n");
				}
			} 

		}catch (SugarImporterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (GlycoVisitorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (GlycoconjugateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SearchEngineException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}



	public static void main(String[] args) {
		StructureBasis SB = new StructureBasis ();
		SB.structureInit();
		SB.calculate();

	}




}
