

package com.sondertara.common.bean.copier;

import com.sondertara.common.bean.copier.BeanCopierRegistry;
import com.sondertara.common.bean.copier.BeanCopy;
import com.sondertara.common.bean.copier.ConverterRegistry;
import com.sondertara.common.bean.model.Gender;
import com.sondertara.common.bean.model.Mono;
import com.sondertara.common.bean.model.Site;
import com.sondertara.common.bean.model.SiteView;
import com.sondertara.common.bean.model.User;
import com.sondertara.common.bean.model.UserView;
import com.sondertara.common.bean.model.WrongMapA;
import com.sondertara.common.bean.model.WrongMapB;
import com.sondertara.common.convert.TypeConverter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static java.util.Collections.singletonList;

public class BeanCopyTest {
    @Test
    public void testCopySingle() {
        Site site = new Site("MySite", new LinkedHashMap<>());
        User admin = new User("Admin", null);
        site.setAdmin(admin);
        site.getUsers().put("CEO", new User("CEO", singletonList(admin)));

        // If there is a converter of Site->SiteView, it works
        SiteView siteView = BeanCopy.copy(site, SiteView.class);

        // If there is a converter of Site->SiteView, it doesn't work
        SiteView siteView0 = new SiteView(null, null);
        BeanCopy.copyTo(site, siteView0);

        Assertions.assertEquals("SiteView{name='MySite', admin=UserView{name='Admin', underHands=null}, users={CEO=UserView{name='CEO', underHands=[UserView{name='Admin', underHands=null}]}}}", siteView.toString());
        Assertions.assertEquals(siteView.toString(), siteView0.toString());
    }

    @Test
    public void testCopyCollection() {
        Site site = new Site("MySite", new HashMap<String, User>());
        User admin = new User("Admin", null);
        site.setAdmin(admin);
        site.getUsers().put("CEO", new User("CEO", singletonList(admin)));
        Collection<Site> sites = Arrays.asList(site, site);
        Collection<SiteView> siteViews = new HashSet<>();
        BeanCopy.copy(sites, siteViews, SiteView.class);

        Assertions.assertEquals("[SiteView{name='MySite', admin=UserView{name='Admin', underHands=null}, users={CEO=UserView{name='CEO', underHands=[UserView{name='Admin', underHands=null}]}}}, SiteView{name='MySite', admin=UserView{name='Admin', underHands=null}, users={CEO=UserView{name='CEO', underHands=[UserView{name='Admin', underHands=null}]}}}]", siteViews.toString());
    }

    @Test
    public void testCopyMap() {
        Site site = new Site("MySite", new HashMap<String, User>());
        User admin = new User("Admin", null);
        site.setAdmin(admin);
        site.getUsers().put("CEO", new User("CEO", singletonList(admin)));
        Map<String, Site> sites = new HashMap<>();
        sites.put("Site1", site);
        sites.put("Site2", site);
        Map<String, SiteView> siteViews = new TreeMap<>();
        BeanCopy.copy(sites, siteViews, Site.class, SiteView.class);

        Assertions.assertEquals("{Site1=SiteView{name='MySite', admin=UserView{name='Admin', underHands=null}, users={CEO=UserView{name='CEO', underHands=[UserView{name='Admin', underHands=null}]}}}, Site2=SiteView{name='MySite', admin=UserView{name='Admin', underHands=null}, users={CEO=UserView{name='CEO', underHands=[UserView{name='Admin', underHands=null}]}}}}", siteViews.toString());
    }

    @Test
    public void testConverter() {
        BeanCopierRegistry.clear();
        ConverterRegistry.clear();

        // Add converters before generating any copier, otherwise converters won't take effect.
        ConverterRegistry.put(User.class.getName(), UserView.class.getName(), (value, defaultValue) -> {
            User user = (User) value;
            return new UserView(user.getName() + "-View", new ArrayList<>());
        });
        // This converter handles Mono<T>. Without this, the analyzer will complain it cannot handle custom generic type.
        ConverterRegistry.put(Mono.class.getName(), Mono.class.getName(), (value, defaultValue) -> {

            Mono mono = (Mono) value;
            if (mono.get().getClass().equals(Integer.class)) {
                return new Mono<>("num:" + mono.get());
            } else {
                return null;
            }
        });

        Assertions.assertNotNull(ConverterRegistry.find(User.class.getName(), UserView.class.getName()));

        Site site = new Site("MySite", new HashMap<>());
        User admin = new User("Admin", null);
        site.setAdmin(admin);
        site.getUsers().put("CEO", new User("CEO", singletonList(admin)));
        SiteView siteView = BeanCopy.copy(site, SiteView.class);

        Assertions.assertEquals("SiteView{name='MySite', admin=UserView{name='Admin-View', underHands=[]}, users={CEO=UserView{name='CEO-View', underHands=[]}}}", siteView.toString());

        Mono<Integer> monoInt = new Mono<>(42);
        Mono<String> monoStr = BeanCopy.copy(monoInt, Mono.class);
        Assertions.assertEquals("Mono{t=num:42}", monoStr.toString());

        BeanCopierRegistry.clear();
        ConverterRegistry.clear();
    }

    @Test
    public void testEnumConverter() {
        ConverterRegistry.put(Gender.class.getName(), Integer.class.getName(), new TypeConverter<Integer>() {
            @Override
            public Integer convert(Object value, Integer defaultValue) {
                return ((Enum) value).ordinal();
            }
        });

        List<Integer> ints = new ArrayList<>();
        BeanCopy.copy(Arrays.asList(Gender.UNKNOWN, Gender.MALE, Gender.FEMALE), ints, Integer.class);

        Assertions.assertEquals(Arrays.asList(0, 1, 2), ints);
    }

    /**
     * This feature is friendly to Continuous Integration (early test correctness in CI).
     */
    @Test
    public void testEarlyTestSuccess() {
        // Use such calls in unit tests to pre-test correctness.
        BeanCopierRegistry.prepare(Site.class, SiteView.class);
        // Unfortunately this call is successful because Site and User have at least a common field `String name`
        BeanCopierRegistry.prepare(Site.class, User.class);
    }

    public void testEarlyTestFailure1() {
        // Object has no copyable field
        BeanCopierRegistry.prepare(Object.class, Object.class);
    }

    public void testEarlyTestFailure2() {
        // Site and Mono have no common fields to copy
        BeanCopierRegistry.prepare(Site.class, Mono.class);
    }

    public void testEarlyTestFailure3() {
        // For Map<K1, V> and Map<K2, V> , K1 should be the same or subclass of K2
        BeanCopierRegistry.prepare(WrongMapA.class, WrongMapB.class);
    }
}
