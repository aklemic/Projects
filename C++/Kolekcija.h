#ifndef KOLEKCIJA_H
#define KOLEKCIJA_H

#include <vector>
#include <algorithm>
#include "Iznimka.h"

template <typename T>
class Kolekcija {
private:
    std::vector<T> elementi;

public:
    Kolekcija() = default;
    Kolekcija(const Kolekcija& other) : elementi(other.elementi) {}
    
    void dodaj(const T& element) { elementi.push_back(element); }
    void ukloni(int indeks);
    void ocisti() { elementi.clear(); }
    
    T& operator[](int indeks);
    const T& operator[](int indeks) const;
    
    size_t velicina() const { return elementi.size(); }
    bool prazna() const { return elementi.empty(); }

    class Iterator {
    private:
        typename std::vector<T>::iterator it;
    public:
        Iterator(typename std::vector<T>::iterator it) : it(it) {}
        Iterator& operator++() { ++it; return *this; }
        bool operator!=(const Iterator& other) const { return it != other.it; }
        T& operator*() { return *it; }
    };

    class ConstIterator {
    private:
        typename std::vector<T>::const_iterator it;
    public:
        ConstIterator(typename std::vector<T>::const_iterator it) : it(it) {}
        ConstIterator& operator++() { ++it; return *this; }
        bool operator!=(const ConstIterator& other) const { return it != other.it; }
        const T& operator*() const { return *it; }
    };

    Iterator begin() { return Iterator(elementi.begin()); }
    Iterator end() { return Iterator(elementi.end()); }
    ConstIterator begin() const { return ConstIterator(elementi.begin()); }
    ConstIterator end() const { return ConstIterator(elementi.end()); }
};

template <typename T>
void Kolekcija<T>::ukloni(int indeks) {
    if (indeks < 0 || indeks >= elementi.size()) 
        throw NeispravanIndeksIznimka();
    elementi.erase(elementi.begin() + indeks);
}

template <typename T>
T& Kolekcija<T>::operator[](int indeks) {
    if (indeks < 0 || indeks >= elementi.size())
        throw NeispravanIndeksIznimka();
    return elementi[indeks];
}

template <typename T>
const T& Kolekcija<T>::operator[](int indeks) const {
    if (indeks < 0 || indeks >= elementi.size())
        throw NeispravanIndeksIznimka();
    return elementi[indeks];
}

#endif