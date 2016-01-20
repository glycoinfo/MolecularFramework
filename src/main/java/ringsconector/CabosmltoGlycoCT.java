package ringsconector;

import java.io.IOException;

import org.eurocarbdb.MolecularFramework.io.SugarImporter;
import org.eurocarbdb.MolecularFramework.io.SugarImporterException;
import org.eurocarbdb.MolecularFramework.io.GlycoCT.SugarExporterGlycoCTCondensed;
import org.eurocarbdb.MolecularFramework.io.cabosml.SugarImporterCabosML;
import org.eurocarbdb.MolecularFramework.io.namespace.GlycoVisitorToGlycoCTforKCF;
import org.eurocarbdb.MolecularFramework.sugar.Sugar;
import org.eurocarbdb.MolecularFramework.util.visitor.GlycoVisitorException;
import org.eurocarbdb.resourcesdb.Config;
import org.eurocarbdb.resourcesdb.ResourcesDbException;
import org.eurocarbdb.resourcesdb.io.MonosaccharideConverter;

public class CabosmltoGlycoCT {
	
	public static void main(String[] args) throws ResourcesDbException, SugarImporterException, GlycoVisitorException, IOException {
		SugarImporter t_objImporter = new SugarImporterCabosML();
		Config t_objConf = new Config();
		//g1
		//
		String t_strCode = "<jcggdb:Glyco><jcggdb:Carb_ID>JCGG-STR000001"
				+ "</jcggdb:Carb_ID><jcggdb:Carb_structure><jcggdb:MS SUBCLASS=\"HEX\" ct_name=\"x-dglc-HEX-x:x\" name=\"Glc\">"
				+ "<jcggdb:MOD ct_name=\"n-acetyl\" name=\"NAc\" node=\"15\" pos2=\"1\"/>"
				+ "<jcggdb:MS SUBCLASS=\"HEX\" anom=\"b\" clink1=\"1\" ct_name=\"b-dglc-HEX-1:5\" name=\"Glc\" node=\"2\" plink6=\"1\"> 			"
				+ "</jcggdb:MS></jcggdb:MS></jcggdb:Carb_structure>"
				+ "</jcggdb:Glyco> ";
				//+ "GlcNAc b1-2 Man a1-6 (Gal b1-4 GlcNAc b1-2 (Gal b1-4 GlcNAc b1-4)Man a1-3)Man b1-4 GlcNAc b1-4(Fuc a1-6)GlcNAc";
		
		//String t_strCode = t_strCodes[0];
		//
		
		 /*String t_strCode = "Galb4(Fuca3)GlcNAcb2Mana-\n"
		 +  "start 		::= residue { linkageposition { subbranch } residue } \"-\"\n"
		 +  "linkageposition ::=	number | \"?\"\n"
		 +  "resiude		::= symbol { symbol }\n"
		 +  "subbranch	::= \"(\" fullresidue { { subbranch } fullresidue } \")\"\n"
		 +  "fullresidue ::= residue linkageposition\n" 
		 +  "symbol		::= character\n"
		 +  "Galb4(Fuca3)GlcNAcb2Mana-\n";
		 */
		 
		
		MonosaccharideConverter t_objTrans = new MonosaccharideConverter(t_objConf);
		Sugar g1 = t_objImporter.parse(t_strCode);
		
		
		GlycoVisitorToGlycoCTforKCF t_objTo = new GlycoVisitorToGlycoCTforKCF(t_objTrans);
		t_objTo.start(g1);
		g1 = t_objTo.getNormalizedSugar();
		
		
		SugarExporterGlycoCTCondensed exp = new SugarExporterGlycoCTCondensed();
		exp.start(g1);
		
		System.out.print(exp.getHashCode()+"\n");
	}

}
