#include "NagradniNatjecaj.h"
#include <iostream>
#include <fstream>
#include <sstream>
#include <algorithm>
#include <ctime>

int NagradniNatjecaj::brojNatjecaja = 0;

// Privatne metode
void NagradniNatjecaj::azurirajStatus() {
    time_t t = time(0);
    tm* sada = localtime(&t);
    Datum danas(sada->tm_mday, sada->tm_mon + 1, sada->tm_year + 1900);

    if (danas < pocetakNatjecaja) {
        status = StatusNatjecanja::Planirano;
    } else if (danas > krajNatjecaja) {
        status = StatusNatjecanja::Zavrseno;
    } else {
        status = StatusNatjecanja::UTijeku;
    }
}

// Konstruktori
NagradniNatjecaj::NagradniNatjecaj() 
    : naziv(""), opis(""), nagrada(""), 
      pocetakNatjecaja(1,1,2023), krajNatjecaja(1,1,2023),
      organizator(""), status(StatusNatjecanja::Planirano) {
    brojNatjecaja++;
}

NagradniNatjecaj::NagradniNatjecaj(const std::string& naziv, const std::string& opis, 
                                  const std::string& nagrada, const Datum& pocetak, 
                                  const Datum& kraj, const Organizator& org)
    : naziv(naziv), opis(opis), nagrada(nagrada),
      pocetakNatjecaja(pocetak), krajNatjecaja(kraj),
      organizator(org) {
    if (krajNatjecaja < pocetakNatjecaja) {
        throw Iznimka("Kraj natjecaja ne može biti prije početka!");
    }
    azurirajStatus();
    brojNatjecaja++;
}

NagradniNatjecaj::NagradniNatjecaj(const NagradniNatjecaj& other)
    : naziv(other.naziv), opis(other.opis), nagrada(other.nagrada),
      pocetakNatjecaja(other.pocetakNatjecaja), krajNatjecaja(other.krajNatjecaja),
      organizator(other.organizator), sudionici(other.sudionici),
      status(other.status) {
    brojNatjecaja++;
}

// Destruktor
NagradniNatjecaj::~NagradniNatjecaj() {
    brojNatjecaja--;
}

// Metode za sudionike
void NagradniNatjecaj::dodajSudionika(const Sudionik& s) {
    if (status == StatusNatjecanja::Zavrseno) {
        throw Iznimka("Natječaj je završen, ne možete dodavati sudionike!");
    }

    for (size_t i = 0; i < sudionici.velicina(); ++i) {
        if (sudionici[i].getIme() == s.getIme()) {
            throw Iznimka("Sudionik s tim imenom već postoji!");
        }
    }
    
    sudionici.dodaj(s);
}

void NagradniNatjecaj::oznaciKaoPobjednika(int indeks) {
    if (indeks < 0 || indeks >= sudionici.velicina()) 
        throw NeispravanIndeksIznimka();
    sudionici[indeks].postaviKaoPobjednika(true);
}

void NagradniNatjecaj::ukloniPobjednika(int indeks) {
    if (indeks < 0 || indeks >= sudionici.velicina())
        throw NeispravanIndeksIznimka();
    sudionici[indeks].postaviKaoPobjednika(false);
}

// Ispisi
void NagradniNatjecaj::ispisiSveSudionike() const {
    std::cout << "\n--- Svi sudionici ---\n";
    for (size_t i = 0; i < sudionici.velicina(); ++i) {
        sudionici[i].ispisiDetalje();
        std::cout << "-------------------\n";
    }
}

void NagradniNatjecaj::ispisiPobjednike() const {
    Kolekcija<Sudionik> pobjednici;
    for (size_t i = 0; i < sudionici.velicina(); ++i) {
        if (sudionici[i].jePobjednik()) pobjednici.dodaj(sudionici[i]);
    }
    
    std::cout << "\n--- Pobjednici ---\n";
    for (size_t i = 0; i < pobjednici.velicina(); ++i) {
        pobjednici[i].ispisiDetalje();
        std::cout << "-------------------\n";
    }
}

void NagradniNatjecaj::ispisiStatistiku() const {
    int brojPobjednika = 0;
    for (size_t i = 0; i < sudionici.velicina(); ++i) {
        if (sudionici[i].jePobjednik()) brojPobjednika++;
    }
    
    std::cout << "\n--- Statistika ---\n"
              << "Status: ";
    switch(status) {
        case StatusNatjecanja::Planirano: std::cout << "Planirano"; break;
        case StatusNatjecanja::UTijeku: std::cout << "U tijeku"; break;
        case StatusNatjecanja::Zavrseno: std::cout << "Završeno"; break;
    }
    std::cout << "\nPeriod: " << pocetakNatjecaja << " - " << krajNatjecaja
              << "\nNagrada: " << nagrada
              << "\nSudionici: " << sudionici.velicina()
              << "\nPobjednici: " << brojPobjednika << "\n";
}

// Ostale metode
void NagradniNatjecaj::odrediPobjednika(const KriterijProcjene& kriterij) {
    const Sudionik* pobjednik = kriterij.odaberiPobjednika(sudionici);
    if (pobjednik) {
        for (size_t i = 0; i < sudionici.velicina(); ++i) {
            sudionici[i].postaviKaoPobjednika(false);
        }
        const_cast<Sudionik*>(pobjednik)->postaviKaoPobjednika(true);
    }
}

// Datoteke
void NagradniNatjecaj::spremiUDatoteku(const std::string& datoteka) const {
    std::ofstream izlaz(datoteka);
    if (!izlaz) throw DatotekaIznimka();

    izlaz << "NAGRADNI_NATJECAJ_v2\n"
          << organizator.getIme() << "\n"
          << naziv << "\n"
          << opis << "\n"
          << pocetakNatjecaja.toString() << "\n"
          << krajNatjecaja.toString() << "\n"
          << nagrada << "\n";
    
    for (size_t i = 0; i < sudionici.velicina(); ++i) {
        izlaz << sudionici[i].getIme() << ";;" 
              << sudionici[i].getKomentar() << ";;"
              << (sudionici[i].jePobjednik() ? "1" : "0") << "\n";
    }
}

void NagradniNatjecaj::ucitajIzDatoteke(const std::string& datoteka) {
    std::ifstream ulaz(datoteka);
    if (!ulaz) throw DatotekaIznimka();

    std::string linija;
    std::getline(ulaz, linija);
    if (linija != "NAGRADNI_NATJECAJ_v2") throw Iznimka("Neispravan format datoteke!");

    // Organizator
    std::getline(ulaz, linija);
    organizator = Organizator(linija);

    // Naziv
    std::getline(ulaz, naziv);

    // Opis
    opis = "";
    while (std::getline(ulaz, linija) && linija.find('/') == std::string::npos) {
        opis += linija + "\n";
    }
    if (!opis.empty()) opis.pop_back();

    // Datumi
    try {
        pocetakNatjecaja = Datum::parsirajDatum(linija);
        std::getline(ulaz, linija);
        krajNatjecaja = Datum::parsirajDatum(linija);
    } catch (...) {
        throw Iznimka("Neispravan format datuma u datoteci!");
    }

    // Nagrada
    std::getline(ulaz, nagrada);

    // Sudionici
    sudionici.ocisti();
    while (std::getline(ulaz, linija)) {
        size_t pos1 = linija.find(";;");
        size_t pos2 = linija.find(";;", pos1 + 2);
        
        if (pos1 == std::string::npos || pos2 == std::string::npos) continue;
        
        Sudionik s(
            linija.substr(0, pos1),
            linija.substr(pos1 + 2, pos2 - pos1 - 2)
        );
        s.postaviKaoPobjednika(linija.substr(pos2 + 2) == "1");
        sudionici.dodaj(s);
    }

    azurirajStatus();
}

// Operator dodjele
NagradniNatjecaj& NagradniNatjecaj::operator=(const NagradniNatjecaj& other) {
    if (this != &other) {
        naziv = other.naziv;
        opis = other.opis;
        nagrada = other.nagrada;
        pocetakNatjecaja = other.pocetakNatjecaja;
        krajNatjecaja = other.krajNatjecaja;
        organizator = other.organizator;
        sudionici = other.sudionici;
        status = other.status;
    }
    return *this;
}