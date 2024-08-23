package org.richard.home.service;

public interface DomainMapper<T,U> {
    T mapFromDomain(U DTO);
}
