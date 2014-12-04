package org.eurocarbdb.MolecularFramework.io.carbbank;

import java.util.Comparator;
import org.eurocarbdb.MolecularFramework.sugar.GlycoNode;

class ComparatorChild implements Comparator<GlycoNode> {

    public ComparatorChild() {
    }

//    @Override
    public int compare(GlycoNode node1, GlycoNode node2) {
        if(node1.getChildNodes().size() >= node2.getChildNodes().size()) {
            return -1;
        }
        else {
            return 1;
        }
    }
    
}
