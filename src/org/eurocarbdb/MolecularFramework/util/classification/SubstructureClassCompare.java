/**
 * 
 */
package org.eurocarbdb.MolecularFramework.util.classification;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jdom.*;
import org.eurocarbdb.MolecularFramework.io.SugarImporterException;
import org.eurocarbdb.MolecularFramework.io.GlycoCT.SugarImporterGlycoCT;
import org.eurocarbdb.MolecularFramework.sugar.Sugar;
import org.eurocarbdb.MolecularFramework.util.similiarity.SearchEngine.SearchEngine;
import org.eurocarbdb.MolecularFramework.util.similiarity.SearchEngine.SearchEngineException;
import org.eurocarbdb.MolecularFramework.util.visitor.GlycoVisitorException;

import org.jdom.Document;
import org.jdom.input.SAXBuilder;

/**
 * @author sherget
 * 
 */
public class SubstructureClassCompare {
	private List m_Lmotifs;
	private Sugar m_oSugar = null;
	private ArrayList<Integer> m_aIDsFound = new ArrayList<Integer>();
	private ArrayList<ResultsPerMotif> m_aResultsPerSequence = new ArrayList<ResultsPerMotif>();

	public SubstructureClassCompare() throws JDOMException, IOException {
		
		String location = "./src/org/eurocarbdb/MolecularFramework/util/classification/substructureBased/motifs.xml";
		File f = new File(location);
		if (f.exists()){
		this.readXMLDefinitions(location);
		}
	}

	public SubstructureClassCompare(String a_strFileName) throws JDOMException,
			IOException {
		this.readXMLDefinitions(a_strFileName);
	}

	public void scanForMotif(Sugar a_oSugar) throws GlycoVisitorException, SearchEngineException {
		this.clear();
		this.m_oSugar = a_oSugar;

		for (Iterator t_iterElements = this.m_Lmotifs.iterator(); t_iterElements
				.hasNext();) {
			Element motif = (Element) t_iterElements.next();

			List sequences = motif.getChildren("sequence");

			for (Iterator t_iterElementSequences = sequences.iterator(); t_iterElementSequences
					.hasNext();) {
				Element sequence = (Element) t_iterElementSequences.next();
				Sugar t_oSug = new Sugar();

				try {
					SugarImporterGlycoCT t_oCTimporter = new SugarImporterGlycoCT();
					t_oSug = t_oCTimporter.parse(sequence.getChild("sugar"));
				} catch (SugarImporterException e) {
					e.printStackTrace();
				}

				
					SearchEngine t_comp = new SearchEngine();
					t_comp.setQueriedStructure(this.m_oSugar);
					t_comp.setQueryStructure(t_oSug);
					
					//	set some flags depending upon XML - configuration						
					if (sequence.getAttribute("reducing_end").getValue().equals(
					"true")) {
						t_comp.restrictToReducingEnds();
					}					
					
					// store positive Results in ArrayList m_aIDsFound
					if (t_comp.isExactMatch()) {
						try {
							ResultsPerMotif t_oRPM = new ResultsPerMotif();
							t_oRPM.motifID = motif.getAttribute("id")
									.getIntValue();
							t_oRPM.sequenceID = sequence.getAttribute("id").getIntValue();

							this.m_aResultsPerSequence.add(t_oRPM);
							if (!this.m_aIDsFound.contains(motif.getAttribute(
									"id").getIntValue())) {
								this.m_aIDsFound.add(motif.getAttribute("id")
										.getIntValue());
							}
						} catch (DataConversionException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}				
			}
		}
	}

	/**
	 * 
	 */
	private void clear() {
		this.m_aIDsFound.clear();
		this.m_aResultsPerSequence.clear();
	}

	public Boolean isMotifIDinSugar(Integer id) {

		if (this.m_aIDsFound.contains(id)) {
			return true;
		} else {
			return false;
		}
	}
	
	public Boolean isMotifPrimaryAssignment (Integer idInQuestion){
		
		Boolean result=true;
		
		//N-Glycan		
		if (this.m_aIDsFound.contains(idInQuestion) &&
			idInQuestion>0 &&
			idInQuestion<100){
			
			for (Integer foundId:this.m_aIDsFound){
				if (foundId>0 && idInQuestion<100){
					if (foundId<idInQuestion){
						result=false;
					}
				}
			}
			
		}
		
		
		// Glycosphingolipid
		if (this.m_aIDsFound.contains(idInQuestion) &&
				idInQuestion>=500 &&
				idInQuestion<599){
				
				for (Integer foundId:this.m_aIDsFound){
					if (foundId>=500 && idInQuestion<599){
						if (foundId<idInQuestion){
							result=false;
						}
					}
				}
				
			}
		// O-Glycan
		if (this.m_aIDsFound.contains(idInQuestion) &&
				idInQuestion>=100 &&
				idInQuestion<200){
				
				for (Integer foundId:this.m_aIDsFound){
					if (foundId>=100 && idInQuestion<200){
						if (foundId<idInQuestion){
							result=false;
						}
					}
				}
				
			}		
		return result;
	}

	public ArrayList<String> getMotifNames() {
		ArrayList<String> result = new ArrayList<String>();
		for (Iterator t_iterElements = this.m_Lmotifs.iterator(); t_iterElements
				.hasNext();) {
			Element motif = (Element) t_iterElements.next();
			try {
				if (this.m_aIDsFound.contains(motif.getAttribute("id")
						.getIntValue())) {
					result.add(motif.getAttribute("name").getValue());
				}
			} catch (DataConversionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return result;
	}
	
	public ArrayList<String> getCategoryNames() {
		ArrayList<String> result = new ArrayList<String>();
		for (Iterator t_iterElements = this.m_Lmotifs.iterator(); t_iterElements
				.hasNext();) {
			Element motif = (Element) t_iterElements.next();
			try {
				if (this.m_aIDsFound.contains(motif.getAttribute("id")
						.getIntValue())) {
					result.add(motif.getAttribute("category").getValue());
				}
			} catch (DataConversionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return result;
	}
	public ArrayList<String> getClassNames() {
		ArrayList<String> result = new ArrayList<String>();
		for (Iterator t_iterElements = this.m_Lmotifs.iterator(); t_iterElements
				.hasNext();) {
			Element motif = (Element) t_iterElements.next();
			try {
				if (this.m_aIDsFound.contains(motif.getAttribute("id")
						.getIntValue())) {
					result.add(motif.getAttribute("class").getValue());
				}
			} catch (DataConversionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return result;
	}
	
	public ArrayList<Integer> getMotifIDs() {
		return m_aIDsFound;
	}
	
	

	public ArrayList<String> getMotifExplaination() {
		ArrayList<String> result = new ArrayList<String>();
		for (Iterator t_iterElements = this.m_Lmotifs.iterator(); t_iterElements
				.hasNext();) {
			Element motif = (Element) t_iterElements.next();
			try {
				if (this.m_aIDsFound.contains(motif.getAttribute("id")
						.getIntValue())) {
					result.add(motif.getAttribute("comment").getValue());
				}
			} catch (DataConversionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return result;
	}

	public ArrayList<ResultsPerMotif> getMotifStructures() {
		return this.m_aResultsPerSequence;
	}

	private void readXMLDefinitions(String a_strFileName) throws JDOMException,
			IOException {
		Document doc = null;
		doc = new SAXBuilder().build(new File(a_strFileName));
		Element rootElement = doc.getRootElement();
		this.m_Lmotifs = rootElement.getChildren();
	}

	public ArrayList<String> getDefinedMotifNames() {
		ArrayList<String> result = new ArrayList<String>();
		for (Iterator t_iterElements = this.m_Lmotifs.iterator(); t_iterElements
				.hasNext();) {
			Element motif = (Element) t_iterElements.next();
			result.add(motif.getAttribute("name").getValue());
		}
		return result;
	}
	
	

	public void submitNewMotifFile(String path) throws JDOMException,
			IOException {
		Document doc = null;
		doc = new SAXBuilder().build(new File(path));
		Element rootElement = doc.getRootElement();
		this.m_Lmotifs = rootElement.getChildren();
	}	
}
