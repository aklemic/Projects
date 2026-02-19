#ifndef KRITERIJPROCJENE_H
#define KRITERIJPROCJENE_H

#include "Sudionik.h"
#include "Kolekcija.h"

class KriterijProcjene {
public:
    virtual ~KriterijProcjene() = default;
    virtual const Sudionik* odaberiPobjednika(const Kolekcija<Sudionik>& sudionici) const = 0;
};

class KriterijNajboljiKomentar : public KriterijProcjene {
public:
    const Sudionik* odaberiPobjednika(const Kolekcija<Sudionik>& sudionici) const override;
};

#endif