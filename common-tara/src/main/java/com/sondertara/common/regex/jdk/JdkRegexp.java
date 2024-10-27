package com.sondertara.common.regex.jdk;

import com.sondertara.common.base.Emptys;
import com.sondertara.common.collection.Lists;
import com.sondertara.common.comparator.OrderedComparator;
import com.sondertara.common.logging.Loggers;
import com.sondertara.common.reflect.ReflectUtils;
import com.sondertara.common.regex.Groups;
import com.sondertara.common.regex.Option;
import com.sondertara.common.regex.Regexp;
import com.sondertara.common.regex.RegexpMatcher;
import com.sondertara.common.struct.Holder;
import org.slf4j.Logger;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.regex.Pattern;

/**
 *
 * @author huangxiaohu.1ih*/
public class JdkRegexp implements Regexp {
    private static final Logger logger = Loggers.getLogger(JdkRegexp.class);
    private final Pattern pattern;
    private Option option;
    private Holder<List<String>> namedGroups;

    public JdkRegexp(Pattern pattern) {
        this.pattern = pattern;
        this.option = Option.buildOption(pattern.flags());
    }

    public JdkRegexp(String pattern) {
        this.pattern = Pattern.compile(pattern, 0);
    }

    public JdkRegexp(String pattern, Option option) {
        this(pattern, option.toFlags());
    }

    public JdkRegexp(String pattern, int flags) {
        this.pattern = Pattern.compile(pattern, flags);
        this.option = Option.buildOption(flags);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Option getOption() {
        return option;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getPattern() {
        return this.pattern.pattern();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RegexpMatcher matcher(CharSequence input) {
        return new JdkMatcher(this, this.pattern.matcher(input));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String[] split(CharSequence input) {
        return this.split(input, 0);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String[] split(CharSequence input, int limit) {
        return this.pattern.split(input, limit);
    }

    @Override
    public String toString() {
        return getPattern();
    }

    private static final Method NAMED_GROUP_MAP_METHOD;

    static {
        NAMED_GROUP_MAP_METHOD = ReflectUtils.getDeclaredMethod(Pattern.class, "namedGroups", Emptys.EMPTY_CLASSES);
    }

    @Override
    public List<String> getNamedGroups() {
        if (namedGroups == null) {
            List<String> groups;
            // key: group name, value: index
            Map<String, Integer> _map = null;
            try {
                // 优先使用 JDK 中 方式获取组
                if (NAMED_GROUP_MAP_METHOD != null) {
                    _map = ReflectUtils.invoke(NAMED_GROUP_MAP_METHOD, this.pattern, Emptys.EMPTY_OBJECTS, true);
                }
            } catch (Throwable e) {
                // ignore it
            }
            final Map<String, Integer> groupInfoByReflect = _map;
            if (groupInfoByReflect != null) {
                TreeSet<String> set = new TreeSet<String>((OrderedComparator<String>) groupInfoByReflect::get);
                set.addAll(groupInfoByReflect.keySet());
                groups = Lists.immutableList(set);
            } else {
                // 通过自己的扫描来获取组信息
                Map<String, List<Groups.GroupCoordinate>> scannedGroupInfo = Groups.extractGroupInfo(this.pattern.pattern());
                groups = Lists.newArrayList(scannedGroupInfo.keySet());
            }
            namedGroups = new Holder<List<String>>(groups);
        }
        return namedGroups.get();
    }
}
