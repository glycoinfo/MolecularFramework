/**
 * 
 */
package org.eurocarbdb.MolecularFramework.io;

import java.util.ArrayList;

import org.eurocarbdb.MolecularFramework.io.GlycoCT.SugarImporterGlycoCT;
import org.eurocarbdb.MolecularFramework.io.GlycoCT.SugarImporterGlycoCTCondensed;
import org.eurocarbdb.MolecularFramework.io.Glyde.SugarImporterGlydeII;
import org.eurocarbdb.MolecularFramework.io.Linucs.SugarImporterLinucs;
import org.eurocarbdb.MolecularFramework.io.OGBI.SugarImporterOgbi;
import org.eurocarbdb.MolecularFramework.io.bcsdb.SugarImporterBCSDB;
import org.eurocarbdb.MolecularFramework.io.cabosml.SugarImporterCabosML;
import org.eurocarbdb.MolecularFramework.io.carbbank.SugarImporterCarbbank;
import org.eurocarbdb.MolecularFramework.io.cfg.SugarImporterCFG;
import org.eurocarbdb.MolecularFramework.io.glycosuite.SugarImporterGlycoSuite;
import org.eurocarbdb.MolecularFramework.io.iupac.SugarImporterIupacCondenced;
import org.eurocarbdb.MolecularFramework.io.iupac.SugarImporterIupacShortV1;
import org.eurocarbdb.MolecularFramework.io.iupac.SugarImporterIupacShortV2;
import org.eurocarbdb.MolecularFramework.io.kcf.SugarImporterKCF;
import org.eurocarbdb.MolecularFramework.io.namespace.GlycoVisitorToGlycoCT;
import org.eurocarbdb.MolecularFramework.io.simglycan.SugarImporterSimGlycan;
import org.eurocarbdb.MolecularFramework.sugar.Sugar;
import org.eurocarbdb.resourcesdb.Config;
import org.eurocarbdb.resourcesdb.GlycanNamescheme;
import org.eurocarbdb.resourcesdb.io.MonosaccharideConversion;
import org.eurocarbdb.resourcesdb.io.MonosaccharideConverter;

/**
 * @author Logan
 *
 */
public class SugarImporterFactory 
{
    public static SugarImporter getImporter(CarbohydrateSequenceEncoding a_enumEncoding) throws Exception
    {
        SugarImporter t_objImporter;
        if ( a_enumEncoding == CarbohydrateSequenceEncoding.carbbank )
        {
            t_objImporter = new SugarImporterCarbbank();
        }
        else if ( a_enumEncoding == CarbohydrateSequenceEncoding.linucs )
        {
            t_objImporter = new SugarImporterLinucs();
        }
        else if ( a_enumEncoding == CarbohydrateSequenceEncoding.ogbi )
        {
            t_objImporter = new SugarImporterOgbi();
        }
        else if ( a_enumEncoding == CarbohydrateSequenceEncoding.glycoct_xml )
        {
            t_objImporter = new SugarImporterGlycoCT();
        }
        else if ( a_enumEncoding == CarbohydrateSequenceEncoding.glycoct_condensed )
        {
            t_objImporter = new SugarImporterGlycoCTCondensed();
        }
        else if ( a_enumEncoding == CarbohydrateSequenceEncoding.bcsdb )
        {
            t_objImporter = new SugarImporterBCSDB();
        }
        else if ( a_enumEncoding == CarbohydrateSequenceEncoding.cfg )
        {
            t_objImporter = new SugarImporterCFG();
        }
        else if ( a_enumEncoding == CarbohydrateSequenceEncoding.iupac_condenced )
        {
            t_objImporter = new SugarImporterIupacCondenced();
        }
        else if ( a_enumEncoding == CarbohydrateSequenceEncoding.iupac_short_v1 )
        {
            t_objImporter = new SugarImporterIupacShortV1();
        }
        else if ( a_enumEncoding == CarbohydrateSequenceEncoding.iupac_short_v2 )
        {
            t_objImporter = new SugarImporterIupacShortV2();
        }
        else if ( a_enumEncoding == CarbohydrateSequenceEncoding.kcf)
        {
            t_objImporter = new SugarImporterKCF();
        }
        else if ( a_enumEncoding == CarbohydrateSequenceEncoding.simglycan)
        {
            t_objImporter = new SugarImporterSimGlycan();
        }
        else if ( a_enumEncoding == CarbohydrateSequenceEncoding.cabosml)
        {
            t_objImporter = new SugarImporterCabosML();
        }
        else if ( a_enumEncoding == CarbohydrateSequenceEncoding.glycosuite)
        {
            t_objImporter = new SugarImporterGlycoSuite();
        }
        else if ( a_enumEncoding == CarbohydrateSequenceEncoding.glyde)
        {
            t_objImporter = new SugarImporterGlydeII();
        }

        else
        {
            throw new Exception("Invalid CarbohydrateSequenceEncoding for Importer.");
        }
        return t_objImporter;
    }

    public static ArrayList<CarbohydrateSequenceEncoding> getSupportedEncodings()
    {
        ArrayList<CarbohydrateSequenceEncoding> t_aList = new ArrayList<CarbohydrateSequenceEncoding>();
        t_aList.add(CarbohydrateSequenceEncoding.carbbank);
        t_aList.add(CarbohydrateSequenceEncoding.linucs);
        t_aList.add(CarbohydrateSequenceEncoding.ogbi);
        t_aList.add(CarbohydrateSequenceEncoding.bcsdb);
        t_aList.add(CarbohydrateSequenceEncoding.cfg);
        t_aList.add(CarbohydrateSequenceEncoding.iupac_condenced);
        t_aList.add(CarbohydrateSequenceEncoding.iupac_short_v1);
        t_aList.add(CarbohydrateSequenceEncoding.iupac_short_v2);
        t_aList.add(CarbohydrateSequenceEncoding.glycoct_xml);
        t_aList.add(CarbohydrateSequenceEncoding.glycoct_condensed);	
        t_aList.add(CarbohydrateSequenceEncoding.kcf);	
        t_aList.add(CarbohydrateSequenceEncoding.glycobase);	
        t_aList.add(CarbohydrateSequenceEncoding.glycosuite);	
        t_aList.add(CarbohydrateSequenceEncoding.cabosml);	
        t_aList.add(CarbohydrateSequenceEncoding.simglycan);	
        t_aList.add(CarbohydrateSequenceEncoding.glyde);
        return t_aList;
    }

    public static Sugar importSugar(String a_strSequence,CarbohydrateSequenceEncoding a_enumEncoding) throws Exception
    {
        Config t_objConf = new Config();
        MonosaccharideConverter t_objTrans = new MonosaccharideConverter(t_objConf);
        return SugarImporterFactory.importSugar(a_strSequence, a_enumEncoding, t_objTrans);
    }

    public static Sugar importSugar(String a_strSequence,CarbohydrateSequenceEncoding a_enumEncoding, MonosaccharideConversion a_converter) throws Exception
    {
        SugarImporter t_objImporter = SugarImporterFactory.getImporter(a_enumEncoding);
        Sugar t_objSugar = t_objImporter.parse(a_strSequence); 
        // translate to validated sugar
        GlycoVisitorToGlycoCT t_objTo = new GlycoVisitorToGlycoCT(a_converter);
        t_objTo.setUseSubstPosition(true);
        t_objTo.setUseFusion(true);
        if ( a_enumEncoding == CarbohydrateSequenceEncoding.carbbank || 
                a_enumEncoding == CarbohydrateSequenceEncoding.cabosml)
        {

            t_objTo.setNameScheme(GlycanNamescheme.CARBBANK);
            t_objTo.start(t_objSugar);
            t_objSugar = t_objTo.getNormalizedSugar();          
        }
        else if ( a_enumEncoding == CarbohydrateSequenceEncoding.linucs )
        {
            t_objTo.setNameScheme(GlycanNamescheme.GLYCOSCIENCES);
            t_objTo.start(t_objSugar);
            t_objSugar = t_objTo.getNormalizedSugar();          
        }
        else if ( a_enumEncoding == CarbohydrateSequenceEncoding.bcsdb )
        {
            t_objTo.getFusionVisitor().setSzenarioFive(true);
            t_objTo.setNameScheme(GlycanNamescheme.BCSDB);
            t_objTo.start(t_objSugar);
            t_objSugar = t_objTo.getNormalizedSugar();          
        }
        else if ( a_enumEncoding == CarbohydrateSequenceEncoding.cfg )
        {
            t_objTo.setNameScheme(GlycanNamescheme.CFG);
            t_objTo.start(t_objSugar);
            t_objSugar = t_objTo.getNormalizedSugar();          
        }
        else if ( a_enumEncoding == CarbohydrateSequenceEncoding.iupac_condenced ||  
                a_enumEncoding == CarbohydrateSequenceEncoding.iupac_short_v1 ||
                a_enumEncoding == CarbohydrateSequenceEncoding.iupac_short_v2 ||
                a_enumEncoding == CarbohydrateSequenceEncoding.simglycan ||
                a_enumEncoding == CarbohydrateSequenceEncoding.glycosuite)
        {
            t_objTo.setNameScheme(GlycanNamescheme.IUPAC);
            t_objTo.start(t_objSugar);
            t_objSugar = t_objTo.getNormalizedSugar();          
        }
        else if ( a_enumEncoding == CarbohydrateSequenceEncoding.kcf )
        {
            t_objTo.setNameScheme(GlycanNamescheme.KEGG);
            t_objTo.start(t_objSugar);
            t_objSugar = t_objTo.getNormalizedSugar();          
        }
        return t_objSugar;
    }

}
