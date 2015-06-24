package com.aliao.litepal.crud;

import com.aliao.litepal.crud.model.AssociationsInfo;

import java.util.Collection;

/**
 * Created by 丽双 on 2015/6/23.
 */
public class Many2OneAnalyzer extends AssociationsAnalyzer {

    void analyze(DataSupport baseObj, AssociationsInfo associationInfo){
        if (baseObj.getClassName().equals(associationInfo.getClassHoldsForeignKey())){
            analyzeManySide(baseObj, associationInfo);
        }else {
            analyzeOneSide(baseObj, associationInfo);
        }
    }

    private void analyzeOneSide(DataSupport baseObj, AssociationsInfo associationInfo) {

        Collection<DataSupport> associatedModels = getAssociatedModels(baseObj, associationInfo);

        for (DataSupport associatedModel : associatedModels){
            buildBidirectionalAssociations(baseObj, associatedModel, associationInfo);
        }
    }


    private void analyzeManySide(DataSupport baseObj, AssociationsInfo associationInfo) {

    }
}
