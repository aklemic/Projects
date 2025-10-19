#include "Organizator.h"
#include <iostream>

Organizator::Organizator(const std::string& ime) : Osoba(ime) {}

void Organizator::ispisiDetalje() const {
    std::cout << "Organizator: " << ime << "\n";
}