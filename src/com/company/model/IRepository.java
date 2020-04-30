package com.company.model;

import java.util.List;

public interface IRepository <T> {
    List<T> findAll();
    T findOne(int id);
    boolean create(T entity);
}
