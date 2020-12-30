package com.duangframework.mvc.core;

import com.duangframework.exception.MvcException;
import com.duangframework.kit.ObjectKit;
import com.duangframework.kit.ToolsKit;
import org.objectweb.asm.*;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

import static org.objectweb.asm.Opcodes.ASM5;

/**
 * 方法参数名称发现类
 * 通过ASM字节码技术，取得该类下的所有公共方法里包含了参数，则返回参数名数组，如果没有包含参数，则返回null
 *
 * @author Created by laotang
 * @date createed in 2018/6/28.
 */
public class MethodParameterNameDiscoverer {

   // public static final int ASM5 = 5 << 16 | 0 << 8 | 0;
    /**
     * 要过滤的方法(Object自带方法及BaseController里的方法)
     */
    private static final Set<String> excludedMethodName = ObjectKit.buildExcludedMethodName(BaseController.class);
    private static final Map<String, String[]> parameterNamePool = new ConcurrentHashMap<>();

    public static String[]  getParameterNames(Class<?> clazz, Method method) throws Exception{
        Parameter[] actionParams = method.getParameters();
        if (ToolsKit.isEmpty(actionParams)) {
            return  null;
        }
        String key = buildParameterNamePoolKey(clazz, method);
        String[] parameters = parameterNamePool.get(key);
        if(ToolsKit.isEmpty(parameters)) {
            Method[] methodArray = clazz.getMethods();
            for (Method itemMethod : methodArray) {
                if (!ObjectKit.isNormalApiMethod(itemMethod.getModifiers()) ||
                        excludedMethodName.contains(itemMethod.getName())) {
                    continue;
                }
                String parameterKey = buildParameterNamePoolKey(clazz, itemMethod);
                String[] paramNameArray = getMethodParamNames(itemMethod);
                if (ToolsKit.isNotEmpty(paramNameArray)) {
                    parameterNamePool.put(parameterKey, paramNameArray);
                }
            }
        }
        if(!parameterNamePool.containsKey(key)) {
            throw new MvcException("取方法参数体时异常，参数获取失败!");
        }
        return parameterNamePool.get(key);
    }

    /**
     * 创建缓存KEY， 类全路径+方法名，来标识唯一
     * 因为请求API是唯一的，所以在Controller里应不要出一致的方法名，所以就没做进一步的唯一性确定处理
     * 如果需要加强唯一性，可以将方法体里的参数类型取出，再拼接字符串后MD5
     * @param clazz
     * @param method
     * @return
     */
    private static String buildParameterNamePoolKey(Class<?> clazz, Method method) {
        return clazz.getName() + "." + method.getName();
    }

    /**
     * 比较参数类型是否一致
     * @param types
     *            asm的类型({@link Type})
     * @param clazzes
     *            java 类型({@link Class})
     * @return
     */
    private static boolean sameType(Type[] types, Class<?>[] clazzes) {
        // 个数不同
        if (types.length != clazzes.length) {
            return false;
        }

        for (int i = 0; i < types.length; i++) {
            if (!Type.getType(clazzes[i]).equals(types[i])) {
                return false;
            }
        }
        return true;
    }

    /**
     * 获取方法的参数名
     只取所需的参数值, 因为所取得的index是无序的，到目前简单测试，得出的排序规则是如果出现了this(一般对应的index是0)后，
     后面顺延的索引位对应的值就是参数的名称，但这个顺延的索引数据有可能并不是按正序排序的，所以先存在TreeMap里排好序
     再将排序后的位置去掉第一位元素后，再方法参数的总长度，从第二位元素顺开始顺延取出参数名。
     如：
             Controller里的方法save(String id, String name, String address)
             ASM后，取出的内容可能是
             name          index
             e                 8
             value           7
             this            0
             id              1
             name         2
             address     3
             null           5
     TreeMap排序后就是0123578，将0去掉，往后再取3位(方法参数长度是3)元素的值就是方法体参数对应的参数名
     * @param m
     * @return
     */
    public static String[] getMethodParamNames(final Method m) {
        final String[] paramNames = new String[m.getParameterTypes().length];
        final String n = m.getDeclaringClass().getName();
        ClassReader cr = null;
        try {
            cr = new ClassReader(n);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        TreeMap<Integer, String> itemVisitorNameMap = new TreeMap<>();

        cr.accept(new ClassVisitor(ASM5) {
            @Override
            public MethodVisitor visitMethod(final int access,
                                             final String name, final String desc,
                                             final String signature, final String[] exceptions) {
                final Type[] args = Type.getArgumentTypes(desc);
                // 方法名相同并且参数个数相同
                if (!name.equals(m.getName())
                        || !sameType(args, m.getParameterTypes())) {
                    return super.visitMethod(access, name, desc, signature, exceptions);
                }

                MethodVisitor v = super.visitMethod(access, name, desc, signature, exceptions);
                return new MethodVisitor(ASM5, v) {
                    @Override
                    public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index) {
                        itemVisitorNameMap.put(index, name);
                        super.visitLocalVariable(name, desc, signature, start, end, index);
                    }
                };
            }
        }, 0);
        Collection<String> nameList = itemVisitorNameMap.values();
        String[] filertParamNames = nameList.toArray(new String[]{});
        // i+1 : 如果是非静态方法，第一位的值就是this, 要将第一位元素过滤掉，由于在上层已经控制只遍历public的非静态，非private的方法才会取出参数名称
        for(int i=0; i<paramNames.length; i++){
            paramNames[i] =  filertParamNames[i+1];
        }
        return paramNames;
    }

}
