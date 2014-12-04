/**
 * 
 */
package org.eurocarbdb.MolecularFramework.io;

import java.io.IOException;
import java.util.ArrayList;

import org.eurocarbdb.MolecularFramework.io.GlycoCT.SugarExporterGlycoCT;
import org.eurocarbdb.MolecularFramework.io.GlycoCT.SugarExporterGlycoCTCondensed;
import org.eurocarbdb.MolecularFramework.io.GlycoCT.SugarImporterGlycoCT;
import org.eurocarbdb.MolecularFramework.io.GlycoCT.SugarImporterGlycoCTCondensed;
import org.eurocarbdb.MolecularFramework.io.Glyde.SugarExporterGlydeII;
import org.eurocarbdb.MolecularFramework.io.Glyde.SugarImporterGlydeII;
import org.eurocarbdb.MolecularFramework.io.Linucs.SugarExporterLinucs;
import org.eurocarbdb.MolecularFramework.io.Linucs.SugarImporterLinucs;
import org.eurocarbdb.MolecularFramework.io.OGBI.SugarImporterOgbi;
import org.eurocarbdb.MolecularFramework.io.bcsdb.SugarImporterBCSDB;
import org.eurocarbdb.MolecularFramework.io.bcsdb.SugarImporterBCSDB3;
import org.eurocarbdb.MolecularFramework.io.carbbank.SugarImporterCarbbank;
import org.eurocarbdb.MolecularFramework.io.cfg.SugarImporterCFG;
import org.eurocarbdb.MolecularFramework.io.glycobase.SugarImporterGlycobase;
import org.eurocarbdb.MolecularFramework.io.kcf.SugarImporterKCF;
import org.eurocarbdb.MolecularFramework.io.namespace.GlycoVisitorFromGlycoCT;
import org.eurocarbdb.MolecularFramework.io.namespace.GlycoVisitorToGlycoCT;
import org.eurocarbdb.MolecularFramework.sugar.Sugar;
import org.eurocarbdb.MolecularFramework.util.validation.GlycoVisitorSugarGraph;
import org.eurocarbdb.MolecularFramework.util.validation.SugarGraphInformation;
import org.eurocarbdb.MolecularFramework.util.visitor.GlycoVisitorException;
import org.eurocarbdb.resourcesdb.GlycanNamescheme;
import org.eurocarbdb.resourcesdb.io.MonosaccharideConversion;

/**
 * @author Logan
 *
 */
public class SequenceTranslator 
{
	public Sugar loadLinucs(String a_strSequence) throws SugarImporterException
	{
		SugarImporterLinucs t_objImporter = new SugarImporterLinucs();
		return t_objImporter.parse(a_strSequence);		
	}
	
	public Sugar loadBcsdb(String a_strSequence) throws SugarImporterException
	{
		SugarImporterBCSDB t_objImporter = new SugarImporterBCSDB();
		return t_objImporter.parse(a_strSequence);		
	}
	
	public Sugar loadBcsdb3(String a_strSequence) throws SugarImporterException
	{
		SugarImporterBCSDB3 t_objImporter = new SugarImporterBCSDB3();
		return t_objImporter.parse(a_strSequence);		
	}

	public Sugar loadCarbbank(String a_strSequence) throws SugarImporterException
	{
		SugarImporterCarbbank t_objImporter = new SugarImporterCarbbank();
		return t_objImporter.parse(a_strSequence);		
	}
	
	public Sugar loadLinearcode(String a_strSequence) throws SugarImporterException
	{
		SugarImporterCFG t_objImporter = new SugarImporterCFG();
		return t_objImporter.parse(a_strSequence);		
	}
	
	public Sugar loadGlycobaseLille(String a_strTRP, ArrayList<String> a_aIncrement) throws SugarImporterException
	{
		SugarImporterGlycobase t_objImporter = new SugarImporterGlycobase();
		t_objImporter.setReducingType(a_strTRP);
		t_objImporter.setIncrements(a_aIncrement);
		return t_objImporter.parse();
	}
	
	public Sugar loadGlycoCTcondensed(String a_strSequence) throws SugarImporterException
	{
		SugarImporterGlycoCTCondensed t_objImporter = new SugarImporterGlycoCTCondensed();
		return t_objImporter.parse(a_strSequence);		
	}
	
	public Sugar loadGlycoCTxml(String a_strSequence) throws SugarImporterException
	{
		SugarImporterGlycoCT t_objImporter = new SugarImporterGlycoCT();
		return t_objImporter.parse(a_strSequence);		
	}
	
	public Sugar loadGlydeII(String a_strSequence) throws SugarImporterException
	{
		SugarImporterGlydeII t_objImporter = new SugarImporterGlydeII();
		return t_objImporter.parse(a_strSequence);		
	}
	
	public Sugar loadKcf(String a_strSequence) throws SugarImporterException
	{
		SugarImporterKCF t_objImporter = new SugarImporterKCF();
		return t_objImporter.parse(a_strSequence);		
	}

	public Sugar loadOgbi(String a_strSequence) throws SugarImporterException
	{
		SugarImporterOgbi t_objImporter = new SugarImporterOgbi();
		return t_objImporter.parse(a_strSequence);		
	}
	
	public String saveLinucs(Sugar a_objSugar) throws SugarExporterException
	{
		SugarExporterLinucs t_objExporter = new SugarExporterLinucs();
		return t_objExporter.export(a_objSugar);
	}

	public String saveGlycoCTcondensed(Sugar a_objSugar) throws GlycoVisitorException
	{
		SugarExporterGlycoCTCondensed t_objExporter = new SugarExporterGlycoCTCondensed();
		t_objExporter.setStrict(false);
		t_objExporter.start(a_objSugar);
		return t_objExporter.getHashCode();
	}

	public String saveGlycoCTxml(Sugar a_objSugar) throws GlycoVisitorException, IOException
	{
		SugarExporterGlycoCT t_objExporter = new SugarExporterGlycoCT();
		t_objExporter.start(a_objSugar);
		return t_objExporter.getXMLCode();
	}
	
	public String saveGlydeII(Sugar a_objSugar) throws GlycoVisitorException, IOException
	{
		SugarExporterGlydeII t_objExporter = new SugarExporterGlydeII();
		t_objExporter.start(a_objSugar);
		return t_objExporter.getXMLCode();
	}

	public Sugar translateToGlycoCT(Sugar a_objSugar,MonosaccharideConversion a_objConverter,GlycanNamescheme a_objNamespace,boolean a_bStrict) throws GlycoVisitorException
	{
        GlycoVisitorToGlycoCT t_objVisitorGlycoCT = new GlycoVisitorToGlycoCT(a_objConverter,a_objNamespace);
        t_objVisitorGlycoCT.setUseStrict(a_bStrict);
        t_objVisitorGlycoCT.setUseSubstPosition(true);
        t_objVisitorGlycoCT.setUseFusion(true);
        if ( a_objNamespace == GlycanNamescheme.BCSDB )
        {
        	t_objVisitorGlycoCT.getFusionVisitor().setSzenarioFive(true);
        }
        t_objVisitorGlycoCT.start( a_objSugar );
        return t_objVisitorGlycoCT.getNormalizedSugar();
	}
	
	public Sugar translateFromGlycoCT(Sugar a_objSugar,MonosaccharideConversion a_objConverter,GlycanNamescheme a_objNamespace) throws GlycoVisitorException
	{
		GlycoVisitorFromGlycoCT t_objVisitorFromGlycoCT = new GlycoVisitorFromGlycoCT(a_objConverter);
		t_objVisitorFromGlycoCT.setNameScheme(a_objNamespace);
		t_objVisitorFromGlycoCT.start(a_objSugar);
		return t_objVisitorFromGlycoCT.getNormalizedSugar();
	}
	
	public ArrayList<SugarGraphInformation> releaseAglycon(Sugar a_objSugar) throws GlycoVisitorException
	{
        GlycoVisitorSugarGraph t_objSugarGraphVisitor = new GlycoVisitorSugarGraph();
        t_objSugarGraphVisitor.start(a_objSugar);
        ArrayList<SugarGraphInformation> t_aSugarGraph = t_objSugarGraphVisitor.getSugarGraphs();
        if ( t_aSugarGraph.size() == 0 )
        {
        	throw new GlycoVisitorException("Empty Sugar : Sequence does not contain a sugar part.");
        }
        return t_aSugarGraph;
	}
	
	public GlycanNamescheme getNameSchemaForFormat(SequenceFormat a_objSchema) throws SequenceFormatException
	{
		if ( a_objSchema == SequenceFormat.BCSDB )
		{
			return GlycanNamescheme.BCSDB;
		}
		if ( a_objSchema == SequenceFormat.BCSDB3 )
		{
			return GlycanNamescheme.BCSDB;
		}
		if ( a_objSchema == SequenceFormat.CARBBANK )
		{
			return GlycanNamescheme.CARBBANK;
		}
		if ( a_objSchema == SequenceFormat.GLYCOBASE_DUBLIN )
		{
			return GlycanNamescheme.GLYCOCT;
		}
		if ( a_objSchema == SequenceFormat.GLYCOBASE_LILLE )
		{
			return GlycanNamescheme.AUTO;
		}
		if ( a_objSchema == SequenceFormat.GLYCOCT_CONDENSED )
		{
			return GlycanNamescheme.GLYCOCT;
		}
		if ( a_objSchema == SequenceFormat.GLYOCCT_XML )
		{
			return GlycanNamescheme.GLYCOCT;
		}
		if ( a_objSchema == SequenceFormat.GLYDEII )
		{
			return GlycanNamescheme.GLYCOCT;
		}
		if ( a_objSchema == SequenceFormat.KCF )
		{
			return GlycanNamescheme.KEGG;
		}
		if ( a_objSchema == SequenceFormat.KCF_SOKA )
		{
		    return GlycanNamescheme.CARBBANK;
		}
		if ( a_objSchema == SequenceFormat.LINEARCODE )
		{
			return GlycanNamescheme.CFG;
		}
		if ( a_objSchema == SequenceFormat.LINUCS )
		{
			return GlycanNamescheme.GLYCOSCIENCES;
		}
		throw new SequenceFormatException("No namspace known for sequence format : " + a_objSchema.getName() );
	}
	
	public Sugar loadSequence(String a_strSequence, SequenceFormat a_objSchema) throws SugarImporterException, SequenceFormatException
	{
		if ( a_objSchema == SequenceFormat.BCSDB )
		{
			return this.loadBcsdb(a_strSequence);
		}
		if ( a_objSchema == SequenceFormat.BCSDB3 )
		{
			return this.loadBcsdb3(a_strSequence);
		}
		if ( a_objSchema == SequenceFormat.CARBBANK )
		{
			return this.loadCarbbank(a_strSequence);
		}
		if ( a_objSchema == SequenceFormat.GLYCOBASE_DUBLIN )
		{
			return this.loadOgbi(a_strSequence);
		}
		if ( a_objSchema == SequenceFormat.GLYCOBASE_LILLE )
		{
			int t_iPos = a_strSequence.indexOf("|");
			if ( t_iPos == -1 )
			{
				throw new SequenceFormatException("Missing TRP type before '|'.");
			}
			String[] t_aStrings = a_strSequence.substring(t_iPos+1).split("\n");
			ArrayList<String> t_aIncrement = new ArrayList<String>();
			for (int t_iCounter = 0; t_iCounter < t_aStrings.length; t_iCounter++) 
			{
				t_aIncrement.add(t_aStrings[t_iCounter]);
			}			
			return this.loadGlycobaseLille(a_strSequence.substring(0, t_iPos), t_aIncrement);
		}
		if ( a_objSchema == SequenceFormat.GLYCOCT_CONDENSED )
		{
			return this.loadGlycoCTcondensed(a_strSequence);
		}
		if ( a_objSchema == SequenceFormat.GLYOCCT_XML )
		{
			return this.loadGlycoCTxml(a_strSequence);
		}
		if ( a_objSchema == SequenceFormat.GLYDEII )
		{
			return this.loadGlydeII(a_strSequence);
		}
		if ( a_objSchema == SequenceFormat.KCF )
		{
			return this.loadKcf(a_strSequence);
		}
		if ( a_objSchema == SequenceFormat.KCF_SOKA )
        {
            return this.loadKcf(a_strSequence);
        }
		if ( a_objSchema == SequenceFormat.LINEARCODE )
		{
			return this.loadLinearcode(a_strSequence);
		}
		if ( a_objSchema == SequenceFormat.LINUCS )
		{
			return this.loadLinucs(a_strSequence);
		}
		throw new SequenceFormatException("Load of sequence format not supported : " + a_objSchema.getName() );		
	}
	
	public String saveSequence(Sugar a_objSugar,SequenceFormat a_objSchema) throws SequenceFormatException, SugarExporterException, GlycoVisitorException, IOException
	{
		if ( a_objSchema == SequenceFormat.GLYCOCT_CONDENSED )
		{
			return this.saveGlycoCTcondensed(a_objSugar);
		}
		if ( a_objSchema == SequenceFormat.GLYOCCT_XML )
		{
			return this.saveGlycoCTxml(a_objSugar);
		}
		if ( a_objSchema == SequenceFormat.GLYDEII )
		{
			return this.saveGlydeII(a_objSugar);
		}
		if ( a_objSchema == SequenceFormat.LINUCS )
		{
			return this.saveLinucs(a_objSugar);
		}
		throw new SequenceFormatException("Save to sequence format not supported : " + a_objSchema.getName() );		
	}
}

