#include "Sudionik.h"
#include <iostream>

Sudionik::Sudionik(const std::string& ime, const std::string& komentar) 
    : Osoba(ime), komentar(komentar), pobjednik(false) {}

Sudionik::Sudionik(const Sudionik& other) 
    : Osoba(other), komentar(other.komentar), pobjednik(other.pobjednik) {}

void Sudionik::postaviKaoPobjednika(bool status) { pobjednik = status; }
std::string Sudionik::getKomentar() const { return komentar; }
bool Sudionik::jePobjednik() const { return pobjednik; }

void Sudionik::ispisiDetalje() const {
    std::cout << "Sudionik: " << ime << "\nKomentar: " << komentar 
              << "\nStatus: " << (pobjednik ? "Pobjednik" : "Nije pobjednik") << "\n";
}

Sudionik& Sudionik::operator=(const Sudionik& other) {
    if (this != &other) {
        ime = other.ime;
        komentar = other.komentar;
        pobjednik = other.pobjednik;
    }
    return *this;
}

std::ostream& operator<<(std::ostream& os, const Sudionik& s) {
    os << s.ime << "|" << s.komentar << "|" << (s.pobjednik ? "1" : "0");
    return os;
}