package org.haijun.study.documentation.excel;

import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.metadata.BaseRowModel;
import com.alibaba.excel.metadata.Sheet;
import com.alibaba.excel.metadata.Table;
import com.alibaba.excel.read.context.AnalysisContext;
import com.alibaba.excel.read.event.AnalysisEventListener;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.excel.write.exception.ExcelGenerateException;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * excel 工具获取
 */
public class ExcelUtils {

    private static final int XLS_MAX_ROW_COUNT = 65536;

    private static final int XLSX_MAX_ROW_COUNT = 1048576;
    /**
     * 指定返回数据类型读取,返回List<List<String>>
     * @param file 文件
     * @param typeEnum 文件类型
     * @return
     * @throws IOException
     */
    public static  List<List<String>> readNoDataType(File file, ExcelTypeEnum typeEnum)throws IOException{
        return readNoDataType(file, typeEnum,0,false);
    }

    /**
     * 指定返回数据类型读取,返回List<List<String>>
     * @param file 文件
     * @param typeEnum 文件类型
     * @param headLineNum 头部信息行数
     * @return
     * @throws IOException
     */
    public static  List<List<String>> readNoDataType(File file, ExcelTypeEnum typeEnum,int headLineNum)throws IOException{
        return readNoDataType(file, typeEnum, headLineNum, false);
    }
    /**
     * 不指定返回数据类型读取,返回List<List<String>>
     * @param file 文件
     * @param typeEnum 文件类型
     * @param headLineNum 头部行数
     * @param headLineNumAllSheet  全部的sheet是否都要跳过头部信息行数
     * @return List<List<String>>
     * @throws IOException
     */
    public static  List<List<String>> readNoDataType(File file, ExcelTypeEnum typeEnum,int headLineNum,boolean headLineNumAllSheet)throws IOException{
        try(
                InputStream inputStream = new FileInputStream(file);

        ){
            List<List<String>> retData = new ArrayList<>();
            ExcelReader reader = new ExcelReader(inputStream, typeEnum, retData,
                    new AnalysisEventListener<List<String>>() {
                        @Override
                        public void invoke(List<String> object, AnalysisContext context) {
                            retData.add(object);
                        }
                        // 解析结束销毁不用的资源
                        @Override
                        public void doAfterAllAnalysed(AnalysisContext context) {
                            //System.out.println("读取完毕");
                        }
                    }
            );
            // 获取全部的sheet页
            List<Sheet> sheets = reader.getSheets();
            boolean flg = true;
            for(Sheet sheet : sheets){
                if(flg){
                    sheet.setHeadLineMun(headLineNum); // 设定表头行数
                }
                flg = headLineNumAllSheet;
                reader.read(sheet);

            }
            return retData;
        }
    }

    /**
     * 根据BaseRowModel 读取excel文件（没有头部信息的数据，直接读取）
     * @param file 文件全路径
     * @param typeEnum excel文件类型
     * @param dataType 返回数据类型，需要继承BaseRowModel，可以参考DemoRowModel
     * @return List<T>
     */
    public static <T extends BaseRowModel> List<T> read(File file, ExcelTypeEnum typeEnum,Class<T> dataType)throws IOException{
        return read(file, typeEnum, dataType, 0,false);
    }

    /**
     * 根据BaseRowModel 读取excel文件（默认第一个sheet页会跳过头部行数）
     * @param file 文件全路径
     * @param typeEnum excel文件类型
     * @param dataType 返回数据类型，需要继承BaseRowModel，可以参考DemoRowModel
     * @param headLineNum 标题头部信息行数（不会读取到数据行），没有设置成0;
     * @return List<T>
     */
    public static <T extends BaseRowModel> List<T> read(File file, ExcelTypeEnum typeEnum,Class<T> dataType,int headLineNum)throws IOException{
        return read(file, typeEnum, dataType, headLineNum,false);
    }

    /**
     * 根据BaseRowModel 读取excel文件
     * @param file 文件全路径
     * @param typeEnum excel文件类型
     * @param dataType 返回数据类型，需要继承BaseRowModel，可以参考DemoRowModel
     * @param headLineNum 标题头部信息行数（不会读取到数据行），没有设置成0
     * @param headLineNumAllSheet 跳过头部信息是否针对全部的sheets，如果为true，每个sheet读取是都会跳过头部信息的行数
     * @throws IOException
     * @return List<T>
     */
    public static <T extends BaseRowModel> List<T> read(File file, ExcelTypeEnum typeEnum,Class<T> dataType,
                                                        int headLineNum,boolean headLineNumAllSheet) throws IOException{
        try(
                InputStream inputStream = new FileInputStream(file)

            ){
            List<T> retData = new ArrayList<>();
            ExcelReader reader = new ExcelReader(inputStream, typeEnum, retData,
                    new AnalysisEventListener<T>() {
                        @Override
                        public void invoke(T object, AnalysisContext context) {
                            //System.out.println(
                                    //"当前sheet:" + context.getCurrentSheet().getSheetNo() + " 当前行：" + context.getCurrentRowNum() + " data:" + object);
                            /*if(object instanceof BaseRowModel){
                                System.out.println("是数据模型");
                            }*/
                            retData.add(object);
                            //System.out.println(retData);
                        }

                        // //解析结束销毁不用的资源
                        @Override
                        public void doAfterAllAnalysed(AnalysisContext context) {
                            //System.out.println("读取完毕");
                        }
                    }
            );
            // 获取全部的sheet页
            List<Sheet> sheets = reader.getSheets();
            boolean flg = true;
            for(Sheet sheet : sheets){
                if(flg){
                    sheet.setHeadLineMun(headLineNum); // 设定表头行数
                }
                flg = headLineNumAllSheet;
                sheet.setClazz(dataType);
                reader.read(sheet);
            }
            return retData;
        }
    }


    /**
     * 写excel数据返回二进制数组
     * @param data 写入的数组， 泛型类型为BaseRowModel
     * @param sheetName sheet名称
     * @param typeEnum 生成的文档类型
     * @param needHead 是否需要表头
     * @param table 表头（可以设置表头样式）
     * @return
     * @throws IOException
     */
    public static byte[] writeToByteArray(List<? extends BaseRowModel> data, String sheetName,
                                          ExcelTypeEnum typeEnum, boolean needHead, Table table) throws IOException{
        try(
                ByteArrayOutputStream out = new ByteArrayOutputStream()
        ) {
            commWrite(data, typeEnum, needHead, table, out ,sheetName);
            return out.toByteArray();
        }

    }

    /**
     * 写入到文件
     * @param file
     * @param data
     * @param sheetName
     * @param typeEnum
     * @param needHead
     * @param table
     * @throws IOException
     */
    public static void writeToFile(File file, List<? extends BaseRowModel> data, String sheetName,
                                   ExcelTypeEnum typeEnum, boolean needHead, Table table) throws IOException{
        try(
                OutputStream out = new FileOutputStream(file)
        ){
            commWrite(data, typeEnum, needHead, table, out, sheetName);
        }
    }

    /**
     * 写入字符串数组到文件
     * @param file
     * @param data
     * @param sheetName
     * @param typeEnum
     * @param needHead
     * @param table
     * @throws IOException
     */
    public static void writeListStrToFile(File file, List<List<String>> data, String sheetName,
                                   ExcelTypeEnum typeEnum, boolean needHead, Table table) throws IOException{
        try(
                OutputStream out = new FileOutputStream(file)
        ){
            commWrite0(data, typeEnum, needHead, table, out, sheetName);
        }
    }


    /**
     * 公共写入方法
     * @param data
     * @param typeEnum
     * @param needHead
     * @param table
     * @param out
     */
    private static void commWrite(List<? extends BaseRowModel> data, ExcelTypeEnum typeEnum, boolean needHead,
                                  Table table, OutputStream out, String sheetName) {
        ExcelWriter writer = new ExcelWriter(out, typeEnum ,needHead);
        int startIndex = 0;
        int size = data.size();
        int maxRowNum = XLS_MAX_ROW_COUNT;
        if(typeEnum.getValue().equals(ExcelTypeEnum.XLSX.getValue())){
            maxRowNum = XLSX_MAX_ROW_COUNT;
        }
        int endIndex = ((startIndex+1)*maxRowNum)-1;
        do{
            endIndex++;
            Sheet sheet = new Sheet(0);
            sheet.setSheetName(sheetName);
            if(table == null){
                // 如果没有设置Table，就通过Excel基础模型对象填充头部信息
                sheet.setClazz(data.get(0).getClass());
                writer.write(data.stream().skip(startIndex).limit(endIndex).collect(Collectors.toList()), sheet);
            } else {
                writer.write(data.stream().skip(startIndex).limit(endIndex).collect(Collectors.toList()), sheet, table);
            }
            endIndex = ((startIndex+1)*maxRowNum)-1;
        }while (endIndex<size);
        writer.finish();
    }
    // List<String> 数据写入
    private static void commWrite0(List<List<String>> data, ExcelTypeEnum typeEnum, boolean needHead,
                                   Table table, OutputStream out, String sheetName) {
        ExcelWriter writer = new ExcelWriter(out, typeEnum ,needHead);
        int startIndex = 0;
        int size = data.size();
        int maxRowNum = XLS_MAX_ROW_COUNT;
        if(typeEnum.getValue().equals(ExcelTypeEnum.XLSX.getValue())){
            maxRowNum = XLSX_MAX_ROW_COUNT;
        }
        int endIndex = ((startIndex+1)*maxRowNum)-1;
        int count=0;
        do{
            endIndex++;
            Sheet sheet = new Sheet(0);
            if(startIndex>0){
                sheet.setSheetName(sheetName+"_"+count);
            }else {
                sheet.setSheetName(sheetName);
            }
            if(table == null){
                writer.write0(data.stream().skip(startIndex).limit(endIndex).collect(Collectors.toList()), sheet);
            } else {
                writer.write0(data.stream().skip(startIndex).limit(endIndex).collect(Collectors.toList()), sheet, table);
                // 同一个sheet 创建多张表
                /*Table table3 = new Table(4);
                writer.write0(data.stream().skip(startIndex).limit(endIndex).collect(Collectors.toList()), sheet, table3);*/
            }

            endIndex = ((startIndex+1)*maxRowNum)-1;
            count++;
        }while (endIndex<size);

        writer.finish();
    }


    private List<List<String>> list;

    private Map<String, Integer> map;

    /**
     * 反编译获取字段对象类型
     * @param object
     * @throws ExcelGenerateException
     */
    private static void writeDataTypeCheck(Object object) throws ExcelGenerateException {

        Field listField = null;
        Field mapField = null;
        try {
            listField = ExcelUtils.class.getDeclaredField("list");
            mapField = ExcelUtils.class.getDeclaredField("map");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        //对比 Field 类的 getType() 和 getGenericType()
        System.out.println(listField.getType());        // interface java.util.List
        System.out.println(listField.getGenericType()); // java.util.List<java.lang.Character>
        System.out.println(mapField.getType());         // interface java.util.Map
        System.out.println(mapField.getGenericType());  // java.util.Map<java.lang.String, java.lang.Integer>

        //获取 list 字段的泛型参数
        ParameterizedType listGenericType = (ParameterizedType) listField.getGenericType();
        Type[] listActualTypeArguments = listGenericType.getActualTypeArguments();
        for (int i = 0; i < listActualTypeArguments.length; i++) {
            System.out.println(listActualTypeArguments[i]);
        }
        // class java.lang.Character

        //获取 map 字段的泛型参数
        ParameterizedType mapGenericType = (ParameterizedType) mapField.getGenericType();
        Type[] mapActualTypeArguments = mapGenericType.getActualTypeArguments();
        for (int i = 0; i < mapActualTypeArguments.length; i++) {
            System.out.println(mapActualTypeArguments[i]);
        }

        if(true){
            try {
                //Method[] test= object.getClass().getMethods();
                Method method = object.getClass().getMethod("get",int.class);
                //method.getGenericReturnType();
                ParameterizedType mapGenericType1 = (ParameterizedType) method.getGenericReturnType();
                Type[] mapActualTypeArguments1 = mapGenericType1.getActualTypeArguments();
                for (int i = 0; i < mapActualTypeArguments1.length; i++) {
                    System.out.println(mapActualTypeArguments1[i]);
                }
                System.out.println();
                //method.getReturnType().isInstance(BaseRowModel.class);
            } catch (Exception e) {
                throw new ExcelGenerateException(e.getMessage());
            }
        }

    }

}
