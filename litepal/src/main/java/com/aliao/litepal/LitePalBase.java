package com.aliao.litepal;

import com.aliao.litepal.crud.model.AssociationsInfo;
import com.aliao.litepal.exceptions.DatabaseGenerateException;
import com.aliao.litepal.model.AssociationsModel;
import com.aliao.litepal.parser.LitePalAttr;
import com.aliao.litepal.util.BaseUtility;
import com.aliao.litepal.util.Const;
import com.aliao.litepal.util.DBUtility;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by 丽双 on 2015/6/16.
 */
public abstract class LitePalBase {


    /**
     * The collection contains all association models.
     */
    private Collection<AssociationsModel> mAssociationModels;

    /**
     * The collection contains all association info.
     */
    private Collection<AssociationsInfo> mAssociationInfos;


    /**
     * Action to get associations.
     */
    private static final int GET_ASSOCIATIONS_ACTION = 1;

    /**
     * Action to get association info.
     */
    private static final int GET_ASSOCIATION_INFO_ACTION = 2;


    protected List<Field> getSupportedFields(String className){
        List<Field> supportedFields = new ArrayList<>();
        Class<?> dynamicClass = null;
        try {
            dynamicClass = Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new DatabaseGenerateException(DatabaseGenerateException.CLASS_NOT_FOUND + className);
        }
        Field[] fields = dynamicClass.getDeclaredFields();
        for (Field field : fields) {
            int modifiers = field.getModifiers();
            if (Modifier.isPrivate(modifiers) && !Modifier.isStatic(modifiers)) {
                Class<?> fieldTypeClass = field.getType();
                String fieldType = fieldTypeClass.getName();
                if (BaseUtility.isFieldTypeSupported(fieldType)) {
                    supportedFields.add(field);
                }
            }
        }
        return supportedFields;
    }



    protected Collection<AssociationsInfo> getAssociationInfo(String className) {
        if (mAssociationInfos == null){
            mAssociationInfos = new HashSet<>();
        }
        mAssociationInfos.clear();
        analyzeClassFields(className, GET_ASSOCIATION_INFO_ACTION);
        return null;
    }

    private void analyzeClassFields(String className, int action){
        Class<?> dynamicClass = null;
        try {
            dynamicClass = Class.forName(className);
            Field[] fields = dynamicClass.getDeclaredFields();
            for (Field field:fields){
                if (isPrivateAndNonPrimitive(field)){
                    oneToAnyConditions(className, field, action);
                    manyToAnyConditions(className, field, action);
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void oneToAnyConditions(String className, Field field, int action) throws ClassNotFoundException {
        Class<?> fieldTypeClass = field.getType();
        //判断该字段的类名是否是映射表里包含的类名之一
        if (LitePalAttr.getIntance().getClassNames().contains(fieldTypeClass.getName())){
            Class<?> reverseDynamicClass = Class.forName(fieldTypeClass.getName());
            Field[] reverseFields = reverseDynamicClass.getDeclaredFields();
            // Look up if there's a reverse association
            // definition in the reverse class.
            boolean reverseAssociations = false;
            for (int i = 0; i<reverseFields.length; i++){
                Field reverseField = reverseFields[i];
                if (Modifier.isPrivate(reverseField.getModifiers())){
                    Class<?> reverseFieldTypeClass = reverseField.getType();
                    //双向的1对1关联关系
                    if (className.equals(reverseFieldTypeClass.getName())){
                        if (action == GET_ASSOCIATIONS_ACTION){
                            addIntoAssociationModelCollection(className, fieldTypeClass.getName(),fieldTypeClass.getName(), Const.Model.ONE_TO_ONE);

                        }else if (action == GET_ASSOCIATION_INFO_ACTION){
                            addIntoAssociationModelCollection(className, fieldTypeClass.getName(), fieldTypeClass.getName(), field, reverseField, Const.Model.ONE_TO_ONE);
                        }
                        reverseAssociations = true;
                    }
                    //一对多
                    else if(isCollection(reverseFieldTypeClass)){
                        //获取类类型
                        String genericTypeName = getGenericTypeName(reverseField);
                        if (className.equals(genericTypeName)) {
                            if (action == GET_ASSOCIATIONS_ACTION) {
                                addIntoAssociationModelCollection(className, fieldTypeClass.getName(),
                                        className, Const.Model.MANY_TO_ONE);
                            } else if (action == GET_ASSOCIATION_INFO_ACTION) {
                                addIntoAssociationInfoCollection(className, fieldTypeClass.getName(),
                                        className, field, reverseField, Const.Model.MANY_TO_ONE);
                            }
                            reverseAssociations = true;
                        }
                    }
                    //单向的1对1关联关系
                    if ((i == reverseFields.length - 1) && !reverseAssociations){
                        if (action == GET_ASSOCIATIONS_ACTION) {
                            addIntoAssociationModelCollection(className, fieldTypeClass.getName(),
                                    fieldTypeClass.getName(), Const.Model.ONE_TO_ONE);
                        } else if (action == GET_ASSOCIATION_INFO_ACTION) {
                            addIntoAssociationInfoCollection(className, fieldTypeClass.getName(),
                                    fieldTypeClass.getName(), field, null, Const.Model.ONE_TO_ONE);
                        }
                    }
                }
            }
        }
    }

    private void manyToAnyConditions(String className, Field field, int action) throws ClassNotFoundException {
        if (isCollection(field.getType())){
            //获取到泛型类型的泛型参数
            String genericTypeName = getGenericTypeName(field);
            //获取到genericType类类型，并遍历该类中的字段
            if (LitePalAttr.getIntance().getClassNames().contains(genericTypeName)) {
                Class<?> reverseDynamicClass = Class.forName(genericTypeName);
                Field[] reverseFields = reverseDynamicClass.getDeclaredFields();
                // Look up if there's a reverse association
                // definition in the reverse class.
                boolean reverseAssociations = false;
                for (int i=0; i<reverseFields.length; i++){
                    Field reverseField = reverseFields[i];
                    if (Modifier.isPrivate(reverseField.getModifiers())){

                        Class<?> reverseFieldTypeClass = reverseField.getType();

                        //多对一双向
                        if (className.equals(reverseFieldTypeClass.getName())){
                            if (action == GET_ASSOCIATIONS_ACTION) {
                                addIntoAssociationModelCollection(className, genericTypeName, genericTypeName, Const.Model.MANY_TO_ONE);
                            } else if (action == GET_ASSOCIATION_INFO_ACTION) {
                                addIntoAssociationInfoCollection(className, genericTypeName, genericTypeName,
                                        field, reverseField, Const.Model.MANY_TO_ONE);
                            }

                            reverseAssociations = true;
                        }
                        //多对多
                        else if(isCollection(reverseFieldTypeClass)){

                            String reverseGenericTypeName = getGenericTypeName(reverseField);

                            if (className.equals(reverseGenericTypeName)){

                                if (action == GET_ASSOCIATIONS_ACTION){
                                    addIntoAssociationModelCollection(className, genericTypeName, null, Const.Model.MANY_TO_MANY);

                                } else if (action == GET_ASSOCIATION_INFO_ACTION) {
                                    addIntoAssociationInfoCollection(className, genericTypeName, null, field,
                                            reverseField, Const.Model.MANY_TO_MANY);
                                }
                                reverseAssociations = true;

                            }
                        }
                        //单项多对一
                        if ((i == reverseFields.length - 1) && !reverseAssociations){
                            if (action == GET_ASSOCIATIONS_ACTION) {
                                addIntoAssociationModelCollection(className, genericTypeName,
                                        genericTypeName, Const.Model.MANY_TO_ONE);
                            } else if (action == GET_ASSOCIATION_INFO_ACTION) {
                                addIntoAssociationInfoCollection(className, genericTypeName, genericTypeName,
                                        field, null, Const.Model.MANY_TO_ONE);
                            }                        }
                    }
                }
            }
        }
    }

    private boolean isCollection(Class<?> fieldType) {
        return isList(fieldType) || isSet(fieldType);
    }



    /**
     * 判断字段类型是否是List
     * isAssignableFrom 是用来判断一个类Class1和另一个类Class2是否相同或是另一个类的超类或接口。
     * 通常调用格式是
     * Class1.isAssignableFrom (Class2)
     * 调用者和参数都是   java.lang.Class   类型。
     * @param fieldType
     * @return
     */
    protected boolean isList(Class<?> fieldType){
        return List.class.isAssignableFrom(fieldType);
    }

    protected boolean isSet(Class<?> fieldType){
        return Set.class.isAssignableFrom(fieldType);
    }


    /**
     * 获取到List或者Set里的泛型参数
     * @return
     * @param field
     */
    private String getGenericTypeName(Field field) {
        Type genericType = field.getGenericType();
        if (genericType != null){
            if (genericType instanceof ParameterizedType){
                //执行强制类型转换
                ParameterizedType parameterizedType = (ParameterizedType) genericType;
                //获取泛型类型的泛型参数
                Class<?> geneticArg = (Class<?>) parameterizedType.getActualTypeArguments()[0];
                return geneticArg.getName();
            }
        }
        return null;
    }

    private void addIntoAssociationModelCollection(String selfClassName, String associatedClassName,
                                                   String classHoldsForeignKey, Field associateOtherModelFromSelf,
                                                   Field associateSelfFromOtherModel, int associationType) {

        AssociationsInfo associationInfo = new AssociationsInfo();
        associationInfo.setSelfClassName(selfClassName);
        associationInfo.setAssociatedClassName(associatedClassName);
        associationInfo.setClassHoldsForeignKey(classHoldsForeignKey);
        associationInfo.setAssociateOtherModelFromSelf(associateOtherModelFromSelf);
        associationInfo.setAssociateSelfFromOtherModel(associateSelfFromOtherModel);
        associationInfo.setAssociationType(associationType);
        mAssociationInfos.add(associationInfo);
    }

    /**
     * 构建关联关系模型，并添加到mAssociationModels里
     * @param className
     * @param associatedClassName
     * @param classHoldsForeignKey
     * @param associationType
     */
    private void addIntoAssociationModelCollection(String className, String associatedClassName,
                                                   String classHoldsForeignKey, int associationType) {
        AssociationsModel associationModel = new AssociationsModel();
        associationModel.setTableName(DBUtility.getTableNameByClassName(className));
        associationModel.setAssociatedTableName(DBUtility.getTableNameByClassName(associatedClassName));
        associationModel.setTableHoldsForeignKey(DBUtility.getTableNameByClassName(classHoldsForeignKey));
        associationModel.setAssociationType(associationType);
        mAssociationModels.add(associationModel);
    }

    private void addIntoAssociationInfoCollection(String selfClassName, String associatedClassName,
                                                  String classHoldsForeignKey, Field associateOtherModelFromSelf,
                                                  Field associateSelfFromOtherModel, int associationType) {
        AssociationsInfo associationInfo = new AssociationsInfo();
        associationInfo.setSelfClassName(selfClassName);
        associationInfo.setAssociatedClassName(associatedClassName);
        associationInfo.setClassHoldsForeignKey(classHoldsForeignKey);
        associationInfo.setAssociateOtherModelFromSelf(associateOtherModelFromSelf);
        associationInfo.setAssociateSelfFromOtherModel(associateSelfFromOtherModel);
        associationInfo.setAssociationType(associationType);
        mAssociationInfos.add(associationInfo);
    }



    /**
     * 判断字段是否是private以及字段类型是否是基本类型
     * @param field
     * @return
     */
    private boolean isPrivateAndNonPrimitive(Field field) {
        return Modifier.isPrivate(field.getModifiers()) && !field.getType().isPrimitive();//字段类型是否是基本类型
    }

    /**
     * 判断传入的列是否是id列。列名为id或者_id都当做id列
     * @param columnName
     * @return
     */
    protected boolean isIdColumn(String columnName) {
        return "_id".equalsIgnoreCase(columnName) || "id".equalsIgnoreCase(columnName);
    }
}
