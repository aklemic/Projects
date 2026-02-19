#ifndef KRITERIJNAJBOLJIKOMENTAR_H
#define KRITERIJNAJBOLJIKOMENTAR_H

#include "KriterijProcjene.h"

class KriterijNajboljiKomentar : public KriterijProcjene {
public:
    const Sudionik* odaberiPobjednika(const Kolekcija<Sudionik>& sudionici) const override;
};

#endif