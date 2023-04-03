package benchmark;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.convert.Convert;
import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;
import com.sondertara.common.bean.model.differ.ArpBaitEntity;
import com.sondertara.common.bean.model.differ.ArpBaitVo;
import com.sondertara.common.bean.model.differ.BaitTemplateEntity;
import com.sondertara.common.bean.model.differ.BaitTemplateVo;
import com.sondertara.common.bean.model.differ.FileBaitEntity;
import com.sondertara.common.bean.model.differ.FileBaitVo;
import com.sondertara.common.bean.model.differ.ProcessBaitEntity;
import com.sondertara.common.bean.model.differ.ProcessBaitVo;
import org.junit.jupiter.api.Test;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.springframework.beans.BeanUtils;

import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author sondertara
 * @since 2022/4/29 下午 03:29
 */


@BenchmarkMode(Mode.AverageTime)
@State(Scope.Thread)
@Fork(1)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class CopyDifferFieldTest {

    public static final int COUNT = 100;

    public static Mapper mapper = DozerBeanMapperBuilder.create().build();

    public static final String DEFAULT_DIR = "E:\\workspace\\java\\tara\\example\\result";
    public static String filename = "";

    static {
        String property = System.getProperty("copy.count");
        if (null != property) {
            System.out.println("Copy count:" + property);
            CopySameFieldTest.COUNT = Integer.parseInt(property);
        }

        Path result = Paths.get(DEFAULT_DIR, "differ-benchmark-" + CopySameFieldTest.COUNT + ".csv");
        filename = result.toString();
    }
    @Benchmark
    @Warmup(iterations = 3, time = 5)
    @Measurement(iterations = 10, time = 10)
    @Test
    public void localTest() {
        // 90ms 93ms 106ms 101ms 100ms
        BaitTemplateVo baitTemplateVo = getBaitTemplateVo();
        for (int i = 0; i < COUNT; i++) {
            BaitTemplateEntity entity = new BaitTemplateEntity();
            com.sondertara.common.util.BeanUtils.copyProperties(baitTemplateVo, entity);
            for (ArpBaitEntity bait : entity.getArpBaitList()) {
                System.out.println(bait.getIp());
            }
        }
    }

    @Benchmark
    @Warmup(iterations = 3, time = 5)
    @Measurement(iterations = 10, time = 10)
    public void nativeTest() {
        BaitTemplateVo baitTemplateVo = getBaitTemplateVo();
        for (int i = 0; i < COUNT; i++) {
            convertToBaitTemplateEntity(baitTemplateVo);
        }
    }

    @Benchmark
    @Warmup(iterations = 3, time = 5)
    @Measurement(iterations = 10, time = 10)
    public void springBeanUtilsTest() {
        BaitTemplateVo baitTemplateVo = getBaitTemplateVo();
        for (int i = 0; i < COUNT; i++) {
            BaitTemplateEntity baitTemplateEntity = new BaitTemplateEntity();
            BeanUtils.copyProperties(baitTemplateVo, baitTemplateEntity);
        }
    }

    /**
     * cant copy if the fields type is different
     *
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    //@Benchmark
    public void apacheBeanUtilsTest() throws InvocationTargetException, IllegalAccessException {
        BaitTemplateVo baitTemplateVo = getBaitTemplateVo();
        for (int i = 0; i < COUNT; i++) {
            BaitTemplateEntity baitTemplateEntity = new BaitTemplateEntity();
            org.apache.commons.beanutils.BeanUtils.copyProperties(baitTemplateEntity, baitTemplateVo);
        }
    }

    @Benchmark
    @Warmup(iterations = 3, time = 5)
    @Measurement(iterations = 10, time = 10)
    public void hutoolBeanUtilTest() {
        BaitTemplateVo baitTemplateVo = getBaitTemplateVo();
        for (int i = 0; i < COUNT; i++) {
            BeanUtil.copyProperties(baitTemplateVo, BaitTemplateEntity.class);

        }
    }

    @Benchmark
    @Warmup(iterations = 3, time = 5)
    @Measurement(iterations = 10, time = 10)
    public void hutoolConvertTest() {

        BaitTemplateVo baitTemplateVo = getBaitTemplateVo();
        for (int i = 0; i < COUNT; i++) {
            Convert.convert(BaitTemplateEntity.class, baitTemplateVo);
        }
    }

    @Benchmark
    @Warmup(iterations = 3, time = 5)
    @Measurement(iterations = 10, time = 10)
    public void dozerTest() {
        BaitTemplateVo baitTemplateVo = getBaitTemplateVo();
        for (int i = 0; i < COUNT; i++) {
            BaitTemplateEntity baitTemplateEntity = new BaitTemplateEntity();
            mapper.map(baitTemplateVo, baitTemplateEntity);
        }
    }

    public static void main(String[] args) throws RunnerException {
        Options build = new OptionsBuilder().include(CopyDifferFieldTest.class.getSimpleName()).resultFormat(ResultFormatType.CSV).result(filename).build();
        new Runner(build).run();
    }

    private BaitTemplateVo getBaitTemplateVo() {
        BaitTemplateVo baitTemplateVo = new BaitTemplateVo();
        baitTemplateVo.setName("123");
        baitTemplateVo.setOsType("windows");
        baitTemplateVo.setId(123456L);
        baitTemplateVo.setArpBaitList(new ArrayList<ArpBaitVo>() {{
            ArpBaitVo arpBaitVo1 = new ArpBaitVo();
            arpBaitVo1.setId(123);
            arpBaitVo1.setIp("1.2.3.4");
            arpBaitVo1.setMac("dd-dd-dd-dd-dd-dd");
            add(arpBaitVo1);
            ArpBaitVo arpBaitVo2 = new ArpBaitVo();
            arpBaitVo2.setId(1234);
            arpBaitVo2.setIp("1.2.3.4");
            arpBaitVo2.setMac("dd-dd-dd-dd-dd-dd");
            add(arpBaitVo2);
        }});
        baitTemplateVo.setFileBaitList(new ArrayList<FileBaitVo>() {{
            FileBaitVo fileBaitVo1 = new FileBaitVo();
            fileBaitVo1.setId(123);
            fileBaitVo1.setPath("/test");
            add(fileBaitVo1);
            FileBaitVo fileBaitVo2 = new FileBaitVo();
            fileBaitVo2.setId(1234);
            fileBaitVo2.setPath("/test");
            add(fileBaitVo2);
        }});
        baitTemplateVo.setProcessBaitList(new ArrayList<ProcessBaitVo>() {{
            ProcessBaitVo processBaitVo1 = new ProcessBaitVo();
            processBaitVo1.setId(123);
            processBaitVo1.setCommand("df -h");
            add(processBaitVo1);
            ProcessBaitVo processBaitVo2 = new ProcessBaitVo();
            processBaitVo2.setId(123);
            processBaitVo2.setCommand("df -h");
            add(processBaitVo2);
        }});
        return baitTemplateVo;
    }


    public static BaitTemplateEntity convertToBaitTemplateEntity(BaitTemplateVo item) {
        if (item == null) {
            return null;
        }
        BaitTemplateEntity result = new BaitTemplateEntity();
        result.setId(item.getId().intValue());
        result.setName(item.getName());
        result.setOsType(item.getOsType());
        List<FileBaitVo> fileBaitList = item.getFileBaitList();
        if (fileBaitList == null) {
            result.setFileBaitList(null);
        } else {
            result.setFileBaitList(fileBaitList.stream().map(CopyDifferFieldTest::convertToFileBaitEntity).collect(Collectors.toList()));
        }
        List<ArpBaitVo> arpBaitList = item.getArpBaitList();
        if (arpBaitList == null) {
            result.setArpBaitList(null);
        } else {
            result.setArpBaitList(arpBaitList.stream().map(CopyDifferFieldTest::convertToArpBaitEntity).collect(Collectors.toList()));
        }
        List<ProcessBaitVo> processBaitList = item.getProcessBaitList();
        if (processBaitList == null) {
            result.setProcessBaitList(null);
        } else {
            result.setProcessBaitList(processBaitList.stream().map(CopyDifferFieldTest::convertToProcessBaitEntity).collect(Collectors.toList()));
        }
        return result;
    }

    public static ProcessBaitEntity convertToProcessBaitEntity(ProcessBaitVo item) {
        if (item == null) {
            return null;
        }
        ProcessBaitEntity result = new ProcessBaitEntity();
        result.setId(item.getId());
        result.setCommand(item.getCommand());
        return result;
    }

    public static ArpBaitEntity convertToArpBaitEntity(ArpBaitVo item) {
        if (item == null) {
            return null;
        }
        ArpBaitEntity result = new ArpBaitEntity();
        result.setId(item.getId());
        result.setIp(item.getIp());
        result.setMac(item.getMac());
        return result;
    }

    public static FileBaitEntity convertToFileBaitEntity(FileBaitVo item) {
        if (item == null) {
            return null;
        }
        FileBaitEntity result = new FileBaitEntity();
        result.setId(item.getId());
        result.setPath(item.getPath());
        return result;
    }
}