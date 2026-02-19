#ifndef IZNIMKA_H
#define IZNIMKA_H

#include <stdexcept>
#include <string>

class Iznimka : public std::exception {
protected:
    std::string poruka;
public:
    explicit Iznimka(const std::string& msg) : poruka(msg) {}
    virtual ~Iznimka() noexcept = default;
    const char* what() const noexcept override { return poruka.c_str(); }
};

class NeispravanIndeksIznimka : public Iznimka {
public:
    NeispravanIndeksIznimka() : Iznimka("Neispravan indeks!") {}
};

class PraznaKolekcijaIznimka : public Iznimka {
public:
    PraznaKolekcijaIznimka() : Iznimka("Prazna kolekcija!") {}
};

class DatotekaIznimka : public Iznimka {
public:
    DatotekaIznimka() : Iznimka("Greška s datotekom!") {}
};

#endif