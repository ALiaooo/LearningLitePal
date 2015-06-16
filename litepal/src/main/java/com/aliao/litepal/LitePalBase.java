package com.aliao.litepal;

import com.aliao.litepal.exceptions.DatabaseGenerateException;
import com.aliao.litepal.util.BaseUtility;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 丽双 on 2015/6/16.
 */
public abstract class LitePalBase {

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
}
