package benchmark;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.convert.Convert;
import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;
import com.sondertara.common.bean.model.same.BaitSameEntity;
import com.sondertara.common.bean.model.same.BaitSameVo;
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

import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author sondertara
 * @since 2022/4/29 下午 03:29
 */


@BenchmarkMode(Mode.AverageTime)
@State(Scope.Thread)
@Fork(1)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class CopySameFieldTest {
    public static Mapper mapper = DozerBeanMapperBuilder.create().build();
    public static int COUNT = 50;

    public static final String DEFAULT_DIR = "E:\\workspace\\java\\tara\\example\\result";
    public static String filename = "";

    static {
        String property = System.getProperty("copy.count");
        if (null != property) {
            System.out.println("Copy count:" + property);
            CopySameFieldTest.COUNT = Integer.parseInt(property);
        }

        Path result = Paths.get(DEFAULT_DIR, "same-benchmark-" + CopySameFieldTest.COUNT + ".csv");
        filename = result.toString();
    }

    public static void main(String[] args) throws RunnerException {
        Options build = new OptionsBuilder().include(CopySameFieldTest.class.getSimpleName()).resultFormat(ResultFormatType.CSV).result(filename).build();
        new Runner(build).run();
    }


    @Benchmark
    @Warmup(iterations = 3, time = 5)
    @Measurement(iterations = 5, time = 5)
    @Test
    public void localTest() {
        // 90ms 93ms 106ms 101ms 100ms
        BaitSameVo BaitSameVo = getBaitSameVo();
        for (int i = 0; i < COUNT; i++) {
            BaitSameEntity entity = new BaitSameEntity();
            com.sondertara.common.util.BeanUtils.copyProperties(BaitSameVo, entity);

        }
    }

    @Benchmark
    @Warmup(iterations = 3, time = 5)
    @Measurement(iterations = 5, time = 5)
    public void nativeTest() {
        BaitSameVo BaitSameVo = getBaitSameVo();
        for (int i = 0; i < COUNT; i++) {
            convertToBaitSameEntity(BaitSameVo);
        }
    }

    @Benchmark
    @Warmup(iterations = 3, time = 5)
    @Measurement(iterations = 5, time = 5)
    public void springBeanUtilsTest() {
        BaitSameVo baitSameVo = getBaitSameVo();
        for (int i = 0; i < COUNT; i++) {
            BaitSameEntity baitSameEntity = new BaitSameEntity();
            org.springframework.beans.BeanUtils.copyProperties(baitSameVo, baitSameEntity);
        }
    }

    @Benchmark
    @Warmup(iterations = 3, time = 5)
    @Measurement(iterations = 5, time = 5)
    public void apacheBeanUtilsTest() throws InvocationTargetException, IllegalAccessException {
        BaitSameVo BaitSameVo = getBaitSameVo();
        for (int i = 0; i < COUNT; i++) {
            BaitSameEntity baitSameEntity = new BaitSameEntity();
            org.apache.commons.beanutils.BeanUtils.copyProperties(baitSameEntity, BaitSameVo);
        }
    }

    @Benchmark
    @Warmup(iterations = 3, time = 5)
    @Measurement(iterations = 5, time = 5)
    public void hutoolBeanUtilTest() {
        BaitSameVo baitSameVo = getBaitSameVo();
        for (int i = 0; i < COUNT; i++) {
            BeanUtil.copyProperties(baitSameVo, BaitSameEntity.class);
        }
    }

    @Benchmark
    @Warmup(iterations = 3, time = 5)
    @Measurement(iterations = 5, time = 5)
    public void hutoolConvertTest() {

        BaitSameVo baitSameVo = getBaitSameVo();
        for (int i = 0; i < COUNT; i++) {
            Convert.convert(BaitSameEntity.class, baitSameVo);
        }
    }

    @Benchmark
    @Warmup(iterations = 3, time = 5)
    @Measurement(iterations = 5, time = 5)
    public void dozerTest() {
        BaitSameVo baitSameVo = getBaitSameVo();
        for (int i = 0; i < COUNT; i++) {
            BaitSameEntity baitSameEntity = new BaitSameEntity();
            mapper.map(baitSameVo, baitSameEntity);
        }
    }


    private BaitSameVo getBaitSameVo() {
        BaitSameVo baitSameVo = new BaitSameVo();
        baitSameVo.setName("123");
        baitSameVo.setOsType("windows");
        baitSameVo.setId(123456L);
        baitSameVo.setUsers(new ArrayList<String>() {{
            add("Jones");
            add("John");
            add("Tom");
        }});
        Map<String, String> apps = new HashMap<>();
        apps.put("Spring boot", "V2.6");
        apps.put("Nginx", "V1.2.1");
        baitSameVo.setApps(apps);
        return baitSameVo;
    }


    public static BaitSameEntity convertToBaitSameEntity(BaitSameVo item) {
        if (item == null) {
            return null;
        }
        BaitSameEntity result = new BaitSameEntity();
        result.setId(item.getId().intValue());
        result.setName(item.getName());
        result.setOsType(item.getOsType());
        List<String> users = item.getUsers();
        result.setUsers(users);
        result.setApps(item.getApps());
        return result;
    }
}