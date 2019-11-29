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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * generate csv excel file
 *
 * @param <P> query param
 * @param <T> query result
 * @author huangxiaohu
 */
public class ExcelGenerateTask<P, T> implements ExcelRunnable {
    private static final Logger logger = LoggerFactory.getLogger(ExcelGenerateTask.class);

    private AtomicBoolean flag = new AtomicBoolean(true);
    private AtomicInteger page = new AtomicInteger(1);

    private P param;

    private ExportFunction<P, T> exportFunction;

    private BlockingQueue<ExcelQueryEntity<T>> queue;
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
                super.isDone = true;
                logger.warn("producer thread exit");
                return;
            }
            logger.info("start query pageSize[{}]", helper.getPageSize());

            final int queryPage = page.getAndIncrement();
            if (queryPage > helper.getPageEnd()) {
                logger.warn("page query end!");
                super.isDone = true;
                flag.set(false);
                return;
            }
            logger.info("start query page[{}]...", queryPage);
            List<T> data = exportFunction.pageQuery(param, queryPage, helper.getPageSize());
            logger.info("end query page[{}]...", queryPage);
            if (data == null || data.isEmpty()) {
                logger.warn("query data is empty,query exit !");
                super.isDone = true;
                flag.set(false);
                return;
            }
            ExcelQueryEntity<T> entity = new ExcelQueryEntity<>();
            entity.setData(data);
            entity.setPage(queryPage);
            queue.put(entity);
            if (data.size() < helper.getPageSize()) {
                logger.warn("current data  size is [{}],less than pageSize[{}],is the last page,query exit!", data.size(), helper.getPageSize());
                super.isDone = true;
                flag.set(false);
            }
        }
    }

    private class ExcelQueryDataConsumer extends AbstractConsumer {
        @Override
        public void consume() throws InterruptedException {

            if (!flag.get() && queue.isEmpty()) {
                super.isDone = true;
                logger.warn("consumer[{}] exit...", Thread.currentThread().getName());
                return;
            }
            ExcelQueryEntity<T> excelQueryEntity = queue.poll(3000, TimeUnit.MILLISECONDS);
            if (null == excelQueryEntity) {
                return;
            }

            logger.info("current dir is {}", helper.getWorkspace());

            logger.info("start handle data of page[{}]  ...", excelQueryEntity.getPage());
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
                logger.info("end handle data of page[{}]...", excelQueryEntity.getPage());
            } catch (Exception e) {
                logger.error("write into file error:", e);
            }


        }

    }

    /**
     * build data row except first row in excel.
     *
     * @param entity      data
     * @param excelEntity excel entity via {@link com.sondertara.excel.annotation.ExportField}
     */
    private List<String> buildRow(Object entity, ExcelEntity excelEntity) throws ExecutionException, IllegalAccessException {


        List<ExcelPropertyEntity> propertyList = excelEntity.getPropertyList();
        List<String> list = new ArrayList<>(propertyList.size());
        for (ExcelPropertyEntity property : propertyList) {
            String cell;
            Field field = property.getFieldEntity();
            Object cellValue = field.get(entity);

            if (cellValue == null) {
                cell = "";
            } else if (cellValue instanceof BigDecimal) {
                cell = (((BigDecimal) cellValue).setScale(property.getScale(), property.getRoundingMode())).toString();
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
