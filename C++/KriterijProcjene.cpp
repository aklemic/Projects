#include "KriterijProcjene.h"
#include "Iznimka.h"

const Sudionik* KriterijNajboljiKomentar::odaberiPobjednika(const Kolekcija<Sudionik>& sudionici) const {
    if (sudionici.prazna()) throw PraznaKolekcijaIznimka();
    
    const Sudionik* najbolji = &sudionici[0];
    for (size_t i = 1; i < sudionici.velicina(); ++i) {
        if (sudionici[i].getKomentar().length() > najbolji->getKomentar().length()) {
            najbolji = &sudionici[i];
        }
    }
    return najbolji;
}