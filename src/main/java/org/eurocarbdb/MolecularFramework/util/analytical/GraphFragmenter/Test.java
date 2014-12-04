package org.eurocarbdb.MolecularFramework.util.analytical.GraphFragmenter;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String[] elements = {"a", "b", "c","d","e" };
		
		//for (int a = 0; a<elements.length;a++){
		int[] indices;
		CombinationGenerator x = new CombinationGenerator (elements.length, 4);
		StringBuffer combination;
		while (x.hasMore ()) {
			
		  combination = new StringBuffer ();
		  indices = x.getNext ();
		  for (int i = 0; i < indices.length; i++) {
		    combination.append (elements[indices[i]]);
		  }
		  System.out.println (combination.toString ());
		}

	}

}
