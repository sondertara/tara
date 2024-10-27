package com.sondertara.common.io.stream.obj;

import com.sondertara.common.function.Functions;
import com.sondertara.common.text.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *  */
public class SecureObjectInputStream extends ObjectInputStream {

    private Predicate<ObjectStreamClass> predicate;

    public SecureObjectInputStream(InputStream inputStream) throws IOException {
        this(inputStream, new SecureObjectClassPredicate[0]);
    }

    public SecureObjectInputStream(InputStream inputStream, SecureObjectClassPredicate... predicates) throws IOException {
        super(inputStream);
        initPredicate(predicates);
    }

    private void initPredicate(SecureObjectClassPredicate... predicates) {
        List<Predicate<ObjectStreamClass>> ps = Stream.of(predicates).filter(Objects::nonNull).collect(Collectors.toList());
        if (ps.size() == 0) {
            predicate = new DefaultSecureObjectClassPredicate();
        } else {
            predicate = Functions.allPredicate(ps);
        }
    }

    protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
        String name = desc.getName();
        if (!isSafeClass(desc)) {
            throw new SecurityException(StringUtils.format("Illegal class name: {}", name));
        }
        return super.resolveClass(desc);
    }

    private boolean isSafeClass(ObjectStreamClass desc) {
        return predicate.test(desc);
    }

    @Override
    protected Class<?> resolveProxyClass(String[] interfaces) throws IOException, ClassNotFoundException {
        return super.resolveProxyClass(interfaces);
    }


}
