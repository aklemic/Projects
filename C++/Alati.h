#ifndef ALATI_H
#define ALATI_H

#include "Kolekcija.h"
#include "Iznimka.h"

template <typename T>
T prviElement(const Kolekcija<T>& kolekcija) {
    if (kolekcija.prazna()) throw PraznaKolekcijaIznimka();
    return kolekcija[0];
}

template <typename T, typename U>
auto kombinacija(const T& a, const U& b) -> decltype(a + b) {
    return a + b;
}

#endif