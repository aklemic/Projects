#ifndef SUDIONIK_H
#define SUDIONIK_H

#include "Osoba.h"
#include <string>

class Sudionik : public Osoba {
private:
    std::string komentar;
    bool pobjednik;
public:
    Sudionik(const std::string& ime = "", const std::string& komentar = "");
    Sudionik(const Sudionik& other);
    Sudionik() : Sudionik("", "") {} // Delegirajući konstruktor
    
    void postaviKaoPobjednika(bool status);
    std::string getKomentar() const;
    bool jePobjednik() const;
    void ispisiDetalje() const override;
    
    Sudionik& operator=(const Sudionik& other);
    friend std::ostream& operator<<(std::ostream& os, const Sudionik& s);
};

#endif