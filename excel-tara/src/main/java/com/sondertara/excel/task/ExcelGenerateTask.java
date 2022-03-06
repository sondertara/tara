package com.sondertara.excel.task;

import com.sondertara.common.util.LocalDateTimeUtils;
import com.sondertara.excel.annotation.ExcelImportFiled;
import com.sondertara.excel.common.Constant;
import com.sondertara.excel.entity.ExcelEntity;
import com.sondertara.excel.entity.ExcelHelper;
import com.sondertara.excel.entity.ExcelPropertyEntity;
import com.sondertara.excel.entity.ExcelQueryEntity;
import com.sondertara.excel.entity.PageQueryParam;
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
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * generate csv excel file
 *
 * @param <T> query param
 * @param <R> query result
 * @author huangxiaohu
 */
public class ExcelGenerateTask<T extends PageQueryParam, R> implements ExcelRunnable {
    private static final Logger logger = LoggerFactory.getLogger(ExcelGenerateTask.class);

    private final AtomicBoolean flag = new AtomicBoolean(true);
    private final AtomicInteger page = new AtomicInteger(1);


    private final T param;

    private final ExportFunction<T, R> exportFunction;

    private final BlockingQueue<ExcelQueryEntity<R>> queue;
    private final ExcelEntity excelEntity;

    private final String fileName;


    public ExcelGenerateTask(T param, ExportFunction<T, R> exportFunction, ExcelEntity e, String fileName) {
        this.param = param;
        this.exportFunction = exportFunction;
        this.excelEntity = e;
        queue = new LinkedBlockingQueue<>(8);
        page.set(param.getPageStart());
        this.fileName = fileName;
    }

    @Override
    public Runnable newRunnableConsumer(CyclicBarrier cyclicBarrier) {
        return new ExcelQueryDataConsumer(cyclicBarrier);
    }

    @Override
    public Runnable newRunnableProducer(CyclicBarrier cyclicBarrier) {
        return new ExcelQueryDataProducer(cyclicBarrier);
    }

    private class ExcelQueryDataProducer extends AbstractProducer {

        private final CyclicBarrier cyclicBarrier;

        private ExcelQueryDataProducer(CyclicBarrier cyclicBarrier) {
            this.cyclicBarrier = cyclicBarrier;
        }

        @Override
        public void produce() throws InterruptedException {
            if (!flag.get()) {
                super.isDone = true;
                logger.warn("producer thread exit");
                await();
                return;
            }
            logger.info("start query pageSize[{}]", param.getPageSize());
            final int queryPage = page.getAndIncrement();
            if (queryPage > param.getPageEnd()) {
                logger.warn("page query end!");
                super.isDone = true;
                flag.set(false);
                await();
                return;
            }
            logger.info("start query page[{}]...", queryPage);
            List<R> data = exportFunction.pageQuery(param, queryPage);
            logger.info("end query page[{}]...", queryPage);
            if (data == null || data.isEmpty()) {
                logger.warn("query data is empty,query exit !");
                super.isDone = true;
                flag.set(false);
                await();
                return;
            }
            ExcelQueryEntity<R> entity = new ExcelQueryEntity<>();
            entity.setData(data);
            entity.setPage(queryPage);
            queue.put(entity);
            if (data.size() < param.getPageSize()) {
                logger.warn("current data  size is [{}],less than pageSize[{}],is the last page,query exit!", data.size(), param.getPageSize());
                super.isDone = true;
                flag.set(false);
                await();
            }
        }

        private void await() {
            try {
                cyclicBarrier.await();
            } catch (InterruptedException | BrokenBarrierException e) {
                e.printStackTrace();
            }
        }
    }

    private class ExcelQueryDataConsumer extends AbstractConsumer {

        private final CyclicBarrier cyclicBarrier;

        private ExcelQueryDataConsumer(CyclicBarrier cyclicBarrier) {
            this.cyclicBarrier = cyclicBarrier;
        }

        @Override
        public void consume() throws InterruptedException {

            if (!flag.get() && queue.isEmpty()) {
                super.isDone = true;
                logger.warn("consumer[{}] exit...", Thread.currentThread().getName());
                await();
                return;
            }
            ExcelQueryEntity<R> excelQueryEntity = queue.poll(3000, TimeUnit.MILLISECONDS);
            if (null == excelQueryEntity) {
                await();
                return;
            }

            logger.info("Data of page[{}] processing  is starting ......", excelQueryEntity.getPage());
            try {
                final String workPath = Constant.FILE_PATH + File.separator + fileName + File.separator;
                File file = new File(workPath);
                if (!file.exists()) {
                    file.mkdirs();
                }
                Appendable printWriter = new PrintWriter(workPath + excelQueryEntity.getPage() + ".csv", Constant.CHARSET);
                CSVPrinter csvPrinter = CSVFormat.EXCEL.print(printWriter);

                final List<R> list = excelQueryEntity.getData();
                for (R data : list) {
                    Object o = exportFunction.convert(data);
                    List<String> row = buildRow(o, excelEntity);
                    csvPrinter.printRecord(row);
                }

                csvPrinter.flush();
                csvPrinter.close();
                logger.info("Data of page[{}] processing has been completed...", excelQueryEntity.getPage());
            } catch (Exception e) {
                logger.error("write into file error:", e);
            }
            await();
        }

        private void await() {
            try {
                cyclicBarrier.await();
            } catch (InterruptedException | BrokenBarrierException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * build data row except first row in excel.
     *
     * @param entity      data
     * @param excelEntity excel entity via {@link ExcelImportFiled}
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
                cell = LocalDateTimeUtils.format((Date) cellValue, property.getDateFormat());
            } else {
                cell = cellValue.toString();
            }
            list.add(cell);
        }
        return list;

    }
}
