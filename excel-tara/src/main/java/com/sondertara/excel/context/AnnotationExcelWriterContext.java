package com.sondertara.excel.context;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.sondertara.excel.entity.PageQueryParam;
import com.sondertara.excel.executor.ExcelWriterExecutor;
import com.sondertara.excel.executor.TaraExcelExecutor;
import com.sondertara.excel.function.ExportFunction;
import com.sondertara.excel.meta.annotation.ExcelExport;
import com.sondertara.excel.meta.model.AnnotationExcelWriterSheetDefinition;
import com.sondertara.excel.meta.model.AnnotationSheet;
import com.sondertara.excel.utils.ListUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;

/**
 * @author huangxiaohu
 */
@Slf4j
public class AnnotationExcelWriterContext implements ExcelWriterContext {

    private final List<AnnotationSheet> sheetDefinitions;
    private final Map<Integer, AnnotationSheet> sheetDefinitionMap;
    private final TaraExcelExecutor<Workbook> excelExecutor;
    private final LoadingCache<String, Integer> sheetNameCache;

    public AnnotationExcelWriterContext() {
        this.sheetDefinitions = new ArrayList<>();
        this.sheetDefinitionMap = new TreeMap<>();

        this.sheetNameCache = CacheBuilder.newBuilder().maximumSize(10).build(new CacheLoader<String, Integer>() {
            @Override
            public Integer load(String key) {
                return 1;
            }
        });
        this.excelExecutor = new ExcelWriterExecutor(this);
    }

    @Override
    public Map<Integer, AnnotationSheet> getSheetDefinitions() {
        return this.sheetDefinitionMap;
    }

    @Override
    public TaraExcelExecutor<Workbook> getExecutor() {
        return this.excelExecutor;
    }

    @Override
    public void addMapper(Class<?> excelClass, ExportFunction<?> function, PageQueryParam queryParam) {
        sheetDefinitions.add(new AnnotationExcelWriterSheetDefinition<>(excelClass, function, queryParam));
    }

    @Override
    public void addData(List<?>... dataList) {
        this.addData(Arrays.asList(dataList));
    }

    @Override
    public void addData(List<List<?>> dataList) {
        for (List<?> data : dataList) {
            sheetDefinitions.add(new AnnotationExcelWriterSheetDefinition<>(ListUtils.getGenericClass(data), data));
        }
        Collections.sort(sheetDefinitions);

        buildSheetDefinitionMap();
    }

    @Override
    public void addModel(Class<?>... clazzs) {
        for (Class<?> clazz : clazzs) {
            sheetDefinitions.add(new AnnotationExcelWriterSheetDefinition<>(clazz, Collections.emptyList()));
        }
        Collections.sort(sheetDefinitions);

        buildSheetDefinitionMap();
    }

    @Override
    public void removeSheet(int index) {
        sheetDefinitions.remove(index);
    }

    /**
     * 获取定义的SheetName
     *
     * @param sheetDefinition the sheet definition
     * @return the sheet name
     */
    private String getOriginalSheetName(AnnotationSheet sheetDefinition) {
        ExcelExport excelExport = sheetDefinition.getAnnotation(ExcelExport.class);
        return excelExport.sheetName();
    }

    /**
     * 生成唯一性的sheetName
     *
     * @param sheetName the sheet name
     * @return the unique sheet name
     */
    private String buildUniqueSheetName(String sheetName) {
        try {
            int cnt = this.sheetNameCache.get(sheetName);
            this.sheetNameCache.put(sheetName, cnt + 1);
            if (cnt > 1) {
                return sheetName + "_" + cnt;
            } else {
                return sheetName;
            }
        } catch (ExecutionException ex) {
            log.error("", ex);
            return "数据";
        }
    }

    private void buildSheetDefinitionMap() {
        sheetDefinitionMap.clear();
        sheetNameCache.invalidateAll();

        for (int i = 0; i < sheetDefinitions.size(); i++) {
            AnnotationSheet tSheetDefinition = sheetDefinitions.get(i);
            String originalSheetName = getOriginalSheetName(tSheetDefinition);
            tSheetDefinition.setName(buildUniqueSheetName(originalSheetName));
            this.sheetDefinitionMap.put(i, tSheetDefinition);
        }
    }
}
