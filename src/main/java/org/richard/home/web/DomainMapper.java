package org.richard.home.web;

public interface DomainMapper<T,U> {
    T mapFromDomain(U DTO);
}
