#ifndef OSOBA_H
#define OSOBA_H

#include <string>
#include <iostream>

class Osoba {
protected:
    std::string ime;
public:
    Osoba(const std::string& ime = "");
    virtual ~Osoba() = default;
    
    virtual void ispisiDetalje() const = 0;
    std::string getIme() const;
    bool operator==(const Osoba& other) const;
};

#endif