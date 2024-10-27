/*
 * Copyright (C) 2012 The Guava Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sondertara.common.collection;

import com.sondertara.common.base.Assert;
import org.jspecify.annotations.Nullable;

import java.util.Iterator;
import java.util.function.Function;

/**
 * An iterator that transforms a backing iterator; for internal use. This avoids the object overhead
 * of constructing a {@link Function Function} for internal methods.
 *
 * @author Louis Wasserman
 */

public abstract class TransformedIterator<F extends @Nullable Object, T extends @Nullable Object>
    implements Iterator<T> {
  final Iterator<? extends F> backingIterator;

 public TransformedIterator(Iterator<? extends F> backingIterator) {
    this.backingIterator = Assert.notNull(backingIterator);
  }

  
  abstract T transform( F from);

  @Override
  public final boolean hasNext() {
    return backingIterator.hasNext();
  }

  @Override
  
  public final T next() {
    return transform(backingIterator.next());
  }

  @Override
  public final void remove() {
    backingIterator.remove();
  }
}
