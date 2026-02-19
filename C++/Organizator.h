#ifndef ORGANIZATOR_H
#define ORGANIZATOR_H

#include "Osoba.h"
#include <string>

class Organizator : public Osoba {
public:
    Organizator(const std::string& ime = "");
    void ispisiDetalje() const override;
};

#endif