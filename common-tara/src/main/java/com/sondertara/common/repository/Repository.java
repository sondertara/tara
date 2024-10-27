package com.sondertara.common.repository;

public interface Repository<E, ID> {
    E getById(ID id);

    void add(E entity);

    void update(E entity);

    void removeById(ID id);

}
