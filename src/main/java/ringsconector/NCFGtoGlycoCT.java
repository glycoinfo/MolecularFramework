package ringsconector;

import java.io.IOException;

import org.eurocarbdb.MolecularFramework.io.SugarImporter;
import org.eurocarbdb.MolecularFramework.io.SugarImporterException;
import org.eurocarbdb.MolecularFramework.io.GlycoCT.SugarExporterGlycoCTCondensed;
import org.eurocarbdb.MolecularFramework.io.namespace.GlycoVisitorToGlycoCT;
import org.eurocarbdb.MolecularFramework.io.ncfg.SugarImporterNCFG;
import org.eurocarbdb.MolecularFramework.sugar.Sugar;
import org.eurocarbdb.MolecularFramework.util.visitor.GlycoVisitorException;
import org.eurocarbdb.resourcesdb.Config;
import org.eurocarbdb.resourcesdb.ResourcesDbException;
import org.eurocarbdb.resourcesdb.io.MonosaccharideConverter;

public class NCFGtoGlycoCT {

	public static void main(String[] args) throws ResourcesDbException, SugarImporterException, GlycoVisitorException, IOException {
		SugarImporter t_objImporter = new SugarImporterNCFG();
		Config t_objConf = new Config();
		//g1
		//
		String t_strCode = "Galb4(Fuca3)GlcNAcb2Man";
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


		GlycoVisitorToGlycoCT t_objTo = new GlycoVisitorToGlycoCT(t_objTrans);
		t_objTo.start(g1);
		g1 = t_objTo.getNormalizedSugar();


		SugarExporterGlycoCTCondensed exp = new SugarExporterGlycoCTCondensed();
		exp.start(g1);

		System.out.print(exp.getHashCode()+"\n");
	}

}
