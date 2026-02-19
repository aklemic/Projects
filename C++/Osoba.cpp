#include "Osoba.h"

Osoba::Osoba(const std::string& ime) : ime(ime) {}

std::string Osoba::getIme() const { return ime; }

bool Osoba::operator==(const Osoba& other) const {
    return ime == other.ime;
}