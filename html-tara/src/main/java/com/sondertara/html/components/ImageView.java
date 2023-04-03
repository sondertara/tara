package com.sondertara.html.components;

import com.sondertara.common.exception.IORuntimeException;
import com.sondertara.common.io.IoUtils;
import com.sondertara.common.regex.PatternPool;
import com.sondertara.common.util.RegexUtils;
import j2html.tags.DomContent;
import j2html.tags.specialized.DivTag;
import j2html.tags.specialized.ImgTag;
import org.apache.commons.codec.binary.Base64;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URI;
import java.nio.file.Paths;

import static j2html.TagCreator.attrs;
import static j2html.TagCreator.div;
import static j2html.TagCreator.img;

/**
 * @author huangxiaohu
 */

public class ImageView extends BaseComponent {

    private String path;
    private String name;


    @Override
    public DomContent build() {
        ImgTag imgTag;
        boolean match = RegexUtils.isMatch(PatternPool.URL_HTTP, path);
        if (match) {
            imgTag = img().withSrc(path);
        } else {
            try {
                FileInputStream stream = new FileInputStream(path);
                byte[] bytes = IoUtils.copyToByteArray(stream);
                String encode = Base64.encodeBase64String(bytes);
                imgTag = img().withSrc("data:image/png;base64," + encode);
            } catch (FileNotFoundException e) {
                throw new IORuntimeException(e);
            }
        }
        DivTag divTag = div(attrs(".image-view"), imgTag);
        return divTag;
    }

    ImageView(String path, String name) {
        this.path = path;
        this.name = name;
        init();
    }

    public static ImageViewBuilder builder() {
        return new ImageViewBuilder();
    }

    public static class ImageViewBuilder {
        private String path;
        private String name;

        ImageViewBuilder() {
        }

        public ImageViewBuilder path(String path) {
            this.path = path;
            return this;
        }

        public ImageViewBuilder name(String name) {
            this.name = name;
            return this;
        }

        public ImageView build() {
            return new ImageView(this.path, this.name);
        }

        @Override
        public String toString() {
            return "ImageView.ImageViewBuilder(path=" + this.path + ", name=" + this.name + ")";
        }
    }

    public static void main(String[] args) {
        URI uri = Paths.get(System.getProperty("user.dir"), ".tara_html").toUri();
        System.out.println(uri.toString());
        System.out.println(uri.getPath());
    }
}
