import com.openhtmltopdf.css.parser.property.PageSize;
import com.sondertara.common.io.FileUtils;
import com.sondertara.html.generator.PdfGenerator;

import java.nio.charset.StandardCharsets;

public class DataSizeTest {

    public static void main(String[] args) {
        String document = FileUtils.readString("O:\\workspace\\java\\tara\\20230725_SR23032100011_云管理平台系统审核专题会汇报材料 (1).html", StandardCharsets.UTF_8);
        PdfGenerator.create(document, "1.pdf", PageSize.B3);

    }
}
