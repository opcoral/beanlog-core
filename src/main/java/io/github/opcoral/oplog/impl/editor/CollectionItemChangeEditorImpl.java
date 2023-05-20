package io.github.opcoral.oplog.impl.editor;

import io.github.opcoral.oplog.core.OpLogParamEditor;
import io.github.opcoral.oplog.entity.param.OpLogParam;
import io.github.opcoral.oplog.util.OpLogConvertUtil;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Collection的列表项变化比较器editor<br>
 * 当type为Collection时，在p中填充如下字段：<br>
 * <ul>
 *     <li>addItemSet: after比before多的项，HashSet类型</li>
 *     <li>removeItemSet: after比before少的项，HashSet类型</li>
 *     <li>commonItemSet：共有项，HashSet类型</li>
 *     <li>addItem：addItemSet根据config生成的字符串</li>
 *     <li>removeItem：removeItemSet根据config生成的字符串</li>
 *     <li>commonItem：commonItemSet根据config生成的字符串</li>
 * </ul>
 *
 * @author GuanZH
 * @since 2023-5-17 10:53
 */
public class CollectionItemChangeEditorImpl implements OpLogParamEditor {
    @Override
    public void edit(OpLogParam opLogParam) {
        if(Collection.class.isAssignableFrom(opLogParam.getType())) {
            // 如果是集合，开始修改
            Collection<?> before = (Collection<?>) opLogParam.getBeforeObj();
            Collection<?> after = (Collection<?>) opLogParam.getAfterObj();
            // 1. 新建set, 先认为after全是新增，before全是删除
            Set<Object> addItemSet = new HashSet<>(after);
            Set<Object> removeItemSet = new HashSet<>(before);
            Set<Object> commonItemSet = new HashSet<>();
            // 2. 遍历
            for(Object afterItem : new HashSet<>(addItemSet)) {
                if(removeItemSet.contains(afterItem)) {
                    // 如果发现添加/删除列表里都有，共同列表添加，添加删除列表删除
                    commonItemSet.add(afterItem);
                    addItemSet.remove(afterItem);
                    removeItemSet.remove(afterItem);
                }
            }
            // 3. 生成字符串
            String addItem = OpLogConvertUtil.convertObj(opLogParam.getConfig().getConverterMap(), addItemSet, opLogParam.getParseField());
            String removeItem = OpLogConvertUtil.convertObj(opLogParam.getConfig().getConverterMap(), removeItemSet, opLogParam.getParseField());
            String commonItem = OpLogConvertUtil.convertObj(opLogParam.getConfig().getConverterMap(), commonItemSet, opLogParam.getParseField());
            // 4. 写入
            Map<String, Object> p = opLogParam.getP();
            p.put("addItemSet", addItemSet);
            p.put("removeItemSet", removeItemSet);
            p.put("commonItemSet", commonItemSet);
            p.put("addItem", addItem);
            p.put("removeItem", removeItem);
            p.put("commonItem", commonItem);
        }
    }
}
