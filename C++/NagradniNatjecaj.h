#ifndef NAGRADNINATJECAJ_H
#define NAGRADNINATJECAJ_H

#include "Datum.h"
#include "Kolekcija.h"
#include "Sudionik.h"
#include "Organizator.h"
#include "Iznimka.h"
#include "KriterijProcjene.h"

enum class StatusNatjecanja {
    Planirano,
    UTijeku,
    Zavrseno
};

class NagradniNatjecaj {
private:
    std::string naziv;
    std::string opis;
    std::string nagrada;
    Datum pocetakNatjecaja;
    Datum krajNatjecaja;
    Organizator organizator;
    Kolekcija<Sudionik> sudionici;
    StatusNatjecanja status;
    static int brojNatjecaja;

    void azurirajStatus();

public:
    NagradniNatjecaj();
    NagradniNatjecaj(const std::string& naziv, const std::string& opis, 
                    const std::string& nagrada, const Datum& pocetak, 
                    const Datum& kraj, const Organizator& org);
    NagradniNatjecaj(const NagradniNatjecaj& other);
    ~NagradniNatjecaj();

    void dodajSudionika(const Sudionik& s);
    void oznaciKaoPobjednika(int indeks);
    void ukloniPobjednika(int indeks);
    void ispisiSveSudionike() const;
    void ispisiPobjednike() const;
    void ispisiStatistiku() const;
    void odrediPobjednika(const KriterijProcjene& kriterij);
    
    void spremiUDatoteku(const std::string& datoteka) const;
    void ucitajIzDatoteke(const std::string& datoteka);

    std::string getNaziv() const { return naziv; }
    std::string getOpis() const { return opis; }
    std::string getNagrada() const { return nagrada; }
    Datum getPocetak() const { return pocetakNatjecaja; }
    Datum getKraj() const { return krajNatjecaja; }
    StatusNatjecanja getStatus() const { return status; }
    void ispisiOrganizatora() const { organizator.ispisiDetalje(); }

    static int getBrojNatjecaja() { return brojNatjecaja; }
    NagradniNatjecaj& operator=(const NagradniNatjecaj& other);
};

#endif