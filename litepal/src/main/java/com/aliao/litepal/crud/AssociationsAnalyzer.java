package com.aliao.litepal.crud;

import com.aliao.litepal.crud.model.AssociationsInfo;

/**
 * Created by 丽双 on 2015/6/23.
 */
public class AssociationsAnalyzer extends DataHandler {


    protected void buildBidirectionalAssociations(DataSupport baseObj, DataSupport associatedModel, AssociationsInfo associationInfo) {
        putSetMethodValueByField(associatedModel, associationInfo.getAssociateSelfFromOtherModel(),
                baseObj);
    }
}
