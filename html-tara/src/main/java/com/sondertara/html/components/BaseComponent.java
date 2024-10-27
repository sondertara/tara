package com.sondertara.html.components;

import j2html.rendering.HtmlBuilder;
import j2html.tags.DomContent;

import java.io.IOException;

/**
 * 自定义组件基础类,为了让组件提前解析,早发现错误
 * @author huangxiaohu
 */
public abstract class BaseComponent extends DomContent {

    private DomContent target;

    public DomContent getTarget() {
        return target;
    }

    public void setTarget(DomContent target) {
        this.target = target;
    }

    @Override
    public <T extends Appendable> T render(HtmlBuilder<T> builder, Object model) throws IOException {
        if (target == null) {
            target = build();
        }
        return getTarget().render(builder, model);
    }

    public void init() {
        this.target = build();
    }

    public abstract DomContent build();
}
