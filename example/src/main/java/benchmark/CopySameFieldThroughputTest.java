package benchmark;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.convert.Convert;
import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;
import com.sondertara.common.bean.model.same.BaitSameEntity;
import com.sondertara.common.bean.model.same.BaitSameVo;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Author: wangy
 * @Date: 2022/1/15 23:17
 * @Description:
 */
public class CopySameFieldThroughputTest {
//    public static Mapper mapper = DozerBeanMapperBuilder.create().build();
    private static BaitSameVo baitSameVo = getBaitSameVo();
    public static String filename = "";

    static {
        Path result = Paths.get(System.getProperty("user.dir"),"example/result", "same-benchmark-thrpt.json");
        filename = result.toString();
    }


    @Benchmark
    public void localTest(Blackhole bh) {
        BaitSameEntity baitSameEntity = new BaitSameEntity();
        com.sondertara.common.util.BeanUtils.copyProperties(baitSameVo,baitSameEntity);
        bh.consume(baitSameEntity);
    }

    @Benchmark
    public void nativeTest(Blackhole bh) {
        bh.consume(convertToBaitSameEntity(baitSameVo));
    }

    @Benchmark
    public void springBeanUtilsTest(Blackhole bh) {
        BaitSameEntity baitSameEntity = new BaitSameEntity();
        org.springframework.beans.BeanUtils.copyProperties(baitSameVo, baitSameEntity);
        bh.consume(baitSameEntity);
    }

    @Benchmark
    public void apacheBeanUtilsTest(Blackhole bh) throws Exception {
        BaitSameEntity baitSameEntity = new BaitSameEntity();
        org.apache.commons.beanutils.BeanUtils.copyProperties(baitSameEntity, baitSameVo);
        bh.consume(baitSameEntity);
    }


    @Benchmark
    public void hutoolBeanUtilTest(Blackhole bh) throws Exception {
        bh.consume( BeanUtil.copyProperties(baitSameVo, BaitSameEntity.class));
    }


    @Benchmark
    public void hutoolConvertTest(Blackhole bh) throws Exception {
        bh.consume( Convert.convert(BaitSameEntity.class, baitSameVo));
    }

//    @Benchmark
//    public void dozerTest(Blackhole bh) throws Exception {
//        BaitSameEntity baitSameEntity = new BaitSameEntity();
//        mapper.map(baitSameVo, baitSameEntity);
//        bh.consume(baitSameEntity);
//    }




    private static BaitSameVo getBaitSameVo() {
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

    public static void main(String[] args) throws RunnerException {
        Options options = new OptionsBuilder()
                .include(CopySameFieldThroughputTest.class.getName())
                .mode(Mode.Throughput)
                .resultFormat(ResultFormatType.JSON).result(filename)
                .timeUnit(TimeUnit.MILLISECONDS)
                .forks(1)
                .build();
        new Runner(options).run();

    }

}