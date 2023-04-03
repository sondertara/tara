import com.sondertara.common.lang.id.MeteorId;
import com.sondertara.common.lang.unit.DataSizeUtils;
import com.sondertara.common.util.LocalDateTimeUtils;
import j2html.tags.specialized.BodyTag;

import java.io.File;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalField;

import static j2html.TagCreator.body;
import static j2html.TagCreator.table;
import static j2html.TagCreator.th;

public class DataSizeTest {

    public static void main(String[] args) {
        long second = LocalDateTimeUtils.parseLocalDate("2022-01-01").atStartOfDay(ZoneOffset.systemDefault()).toInstant().getEpochSecond();
        System.out.println(second);
        System.out.println(System.currentTimeMillis() / 1000);
        System.out.println(256 << 22);
        System.out.println(256 << 20);
        String format = DataSizeUtils.format(new File("F:\\workspace\\java\\tara\\pom.xml").length());
        System.out.println(format);

        long nextId = MeteorId.nextId();
        System.out.println(nextId);

        BodyTag bodyTag = body().with(table(th("Name"), th("Size"), th("LastModifyTime")));
        String formatted = bodyTag.renderFormatted();
        System.out.println(formatted);
    }
}
