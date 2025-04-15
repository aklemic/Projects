#include "DinamickaKlasa.h"

DinamickaKlasa::DinamickaKlasa(int v) : podatak(new int(v)) {}

DinamickaKlasa::DinamickaKlasa(const DinamickaKlasa& other) 
    : podatak(new int(*other.podatak)) {}

DinamickaKlasa::~DinamickaKlasa() { 
    delete podatak;
}

DinamickaKlasa& DinamickaKlasa::operator=(const DinamickaKlasa& other) {
    if (this != &other) {
        *podatak = *other.podatak;
    }
    return *this;
}