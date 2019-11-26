package com.sondertara.excel.task;

import com.sondertara.common.util.DateUtil;
import com.sondertara.excel.common.Constant;
import com.sondertara.excel.entity.ExcelEntity;
import com.sondertara.excel.entity.ExcelHelper;
import com.sondertara.excel.entity.ExcelPropertyEntity;
import com.sondertara.excel.entity.ExcelQueryEntity;
import com.sondertara.excel.function.ExportFunction;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 生成excel任务
 *
 * @param <P> 查询参数
 * @param <T> 结果数据
 * @author huangxiaohu
 */
public class ExcelGenerateTask<P, T> implements ExcelRunnable {
    private static final Logger log = LoggerFactory.getLogger(ExcelGenerateTask.class);

    private AtomicBoolean flag = new AtomicBoolean(true);
    private AtomicInteger page = new AtomicInteger(1);

    private P param;

    private ExportFunction<P, T> exportFunction;

    private BlockingQueue<ExcelQueryEntity> queue;
    private ExcelEntity excelEntity;

    private ExcelHelper helper;

    public ExcelGenerateTask(P param, ExportFunction<P, T> exportFunction, ExcelEntity e, final ExcelHelper helper) {
        this.param = param;
        this.exportFunction = exportFunction;
        this.excelEntity = e;
        this.helper = helper;
        queue = new LinkedBlockingQueue<>(8);
        page.set(helper.getPageStart());
    }

    @Override
    public Runnable newRunnableConsumer() {
        return new ExcelQueryDataConsumer();
    }

    @Override
    public Runnable newRunnableProducer() {
        return new ExcelQueryDataProducer();
    }

    private class ExcelQueryDataProducer extends AbstractProducer {

        @Override
        public void produce() throws InterruptedException {
            if (!flag.get()) {
                log.warn("结束完毕");
                super.isDone = true;
                return;
            }
            log.info("开始查询 pageSize[{}]", helper.getPageSize());

            final int queryPage = page.getAndIncrement();
            if (queryPage > helper.getPageEnd()) {
                log.warn("分页查询结束");
                super.isDone = true;
                flag.set(false);
                return;
            }
            log.info("开始查询第[{}]页", queryPage);
            List<T> data = exportFunction.pageQuery(param, queryPage, helper.getPageSize());
            log.info("第[{}]页查询结束", queryPage);
            if (data == null || data.isEmpty()) {
                log.warn("查询结果为空,结束查询!");
                super.isDone = true;
                flag.set(false);
                return;
            }
            ExcelQueryEntity<T> entity = new ExcelQueryEntity<>();
            entity.setData(data);
            entity.setPage(queryPage);
            queue.put(entity);
            if (data.size() < helper.getPageSize()) {
                log.warn("查询结果数量小于pageSize,为最后一页，结束查询!");
                super.isDone = true;
                flag.set(false);
                return;
            }
        }
    }

    private class ExcelQueryDataConsumer extends AbstractConsumer {
        @Override
        public void consume() throws InterruptedException {

            if (!flag.get() && queue.isEmpty()) {
                super.isDone = true;
                log.warn("结束消费[{}]", Thread.currentThread().getName());
                return;
            }
            ExcelQueryEntity excelQueryEntity = queue.poll(3000, TimeUnit.MILLISECONDS);
            if (null == excelQueryEntity) {
                return;
            }

            log.info("current dir is {}", helper.getWorkspace());

            log.info("处理第{}页", excelQueryEntity.getPage());
            try {
                File file = new File(helper.getWorkspace());
                if (!file.exists()) {
                    file.mkdirs();
                }
                Appendable printWriter = new PrintWriter(helper.getWorkspace() + excelQueryEntity.getPage() + ".csv", Constant.CHARSET);
                CSVPrinter csvPrinter = CSVFormat.EXCEL.print(printWriter);

                final List<T> list = excelQueryEntity.getData();
                for (T data : list) {
                    Object o = exportFunction.convert(data);
                    List<String> row = buildRow(o, excelEntity);
                    csvPrinter.printRecord(row);
                }

                csvPrinter.flush();
                csvPrinter.close();
                log.info("第{}页处理完毕", excelQueryEntity.getPage());
            } catch (Exception e) {
                log.error("write into file error:", e);
            }


        }

    }

    /**
     * 构造 除第一行以外的其他行的列值
     *
     * @param entity      数据
     * @param excelEntity 导出字段属性
     */
    private List<String> buildRow(Object entity, ExcelEntity excelEntity) throws Exception {


        List<ExcelPropertyEntity> propertyList = excelEntity.getPropertyList();
        List<String> list = new ArrayList<>(propertyList.size());
        for (ExcelPropertyEntity property : propertyList) {
            String cell;
            Field field = property.getFieldEntity();
            Object cellValue = field.get(entity);

            if (cellValue == null) {
                cell = "";
            } else if (cellValue instanceof BigDecimal) {
                if (-1 == property.getScale()) {
                    cell = cellValue.toString();
                } else {
                    cell = (((BigDecimal) cellValue).setScale(property.getScale(), property.getRoundingMode())).toString();
                }
            } else if (cellValue instanceof Date) {
                cell = DateUtil.formatDate((Date) cellValue, property.getDateFormat());
            } else {
                cell = cellValue.toString();
            }
            list.add(cell);
        }
        return list;

    }
}
