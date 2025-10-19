#include <iostream>
#include <limits>
#include "NagradniNatjecaj.h"
#include "KriterijProcjene.h"
#include "Organizator.h"
#ifdef _WIN32
#include <Windows.h>
#endif

void ocistiBuffer() {
    std::cin.clear();
    std::cin.ignore(std::numeric_limits<std::streamsize>::max(), '\n');
}

void prikaziMeni() {
    std::cout << "\n════════════════ MENI ════════════════\n"
              << "1. Kreiraj novi natječaj\n"
              << "2. Dodaj sudionika\n"
              << "3. Oznaci pobjednika\n"
              << "4. Pregled svih sudionika\n"
              << "5. Prikazi pobjednike\n"
              << "6. Prikazi statistiku\n"
              << "7. Odredi pobjednika automatski\n"
              << "8. Spremi podatke\n"
              << "9. Ucitaj podatke\n"
              << "0. Izlaz\n"
              << "═══════════════════════════════════════\n"
              << "Odabir: ";
}

int main() {
    #ifdef _WIN32
    SetConsoleOutputCP(65001);
    #endif

    NagradniNatjecaj* natjecaj = nullptr;
    int izbor;

    do {
        prikaziMeni();
        std::cin >> izbor;
        ocistiBuffer();

        try {
            switch (izbor) {
                case 1: {
                    std::string naziv, opis, orgIme, nagrada, line;
                    
                    std::cout << "Organizator: ";
                    std::getline(std::cin, orgIme);
                    
                    std::cout << "Naziv natječaja: ";
                    std::getline(std::cin, naziv);
                    
                    std::cout << "Opis (unesite '---' za kraj):\n";
                    opis = "";
                    while (std::getline(std::cin, line) && line != "---") {
                        opis += line + "\n";
                    }
                    
                    std::cout << "Nagrada: ";
                    std::getline(std::cin, nagrada);
                    
                    Datum pocetak, kraj;
                    bool datumOK = false;
                    while (!datumOK) {
                        try {
                            std::cout << "Početak natjecaja (DD/MM/GGGG): ";
                            std::getline(std::cin, line);
                            pocetak = Datum::parsirajDatum(line);
                            
                            std::cout << "Kraj natjecaja (DD/MM/GGGG): ";
                            std::getline(std::cin, line);
                            kraj = Datum::parsirajDatum(line);
                            
                            if (kraj < pocetak) {
                                throw Iznimka("Kraj ne može biti prije početka!");
                            }
                            datumOK = true;
                        } catch (const std::exception& e) {
                            std::cout << "Greška: " << e.what() << "\nPokušajte ponovno!\n";
                        }
                    }
                    
                    if (natjecaj) delete natjecaj;
                    natjecaj = new NagradniNatjecaj(naziv, opis, nagrada, pocetak, kraj, Organizator(orgIme));
                    break;
                }
                case 2: {
                    if (!natjecaj) throw Iznimka("Prvo kreirajte natječaj!");
                    std::string ime, komentar, line;
                    
                    std::cout << "Ime sudionika: ";
                    std::getline(std::cin, ime);
                    
                    std::cout << "Komentar (unesite '---' za kraj):\n";
                    komentar = "";
                    while (std::getline(std::cin, line) && line != "---") {
                        komentar += line + "\n";
                    }
                    
                    natjecaj->dodajSudionika(Sudionik(ime, komentar));
                    std::cout << "Sudionik dodan!\n";
                    break;
                }
                case 3: {
                    if (!natjecaj) throw Iznimka("Natječaj nije kreiran!");
                    int indeks;
                    std::cout << "Indeks sudionika: ";
                    std::cin >> indeks;
                    natjecaj->oznaciKaoPobjednika(indeks - 1);
                    std::cout << "Status pobjednika ažuriran!\n";
                    ocistiBuffer();
                    break;
                }
                case 4: 
                    if (natjecaj) {
                        std::cout << "\n=== Detalji natječaja ===\n";
                        std::cout << "Organizator: ";
                        natjecaj->ispisiOrganizatora();
                        natjecaj->ispisiSveSudionike();
                    }
                    else std::cout << "Natječaj nije kreiran!\n";
                    break;
                case 5:
                    if (natjecaj) natjecaj->ispisiPobjednike();
                    else std::cout << "Natječaj nije kreiran!\n";
                    break;
                case 6:
                    if (natjecaj) natjecaj->ispisiStatistiku();
                    else std::cout << "Natječaj nije kreiran!\n";
                    break;
                case 7: {
                    if (!natjecaj) throw Iznimka("Natječaj nije kreiran!");
                    KriterijNajboljiKomentar kriterij;
                    natjecaj->odrediPobjednika(kriterij);
                    std::cout << "Pobjednik automatski odabran!\n";
                    break;
                }
                case 8: {
                    if (!natjecaj) throw Iznimka("Natječaj nije kreiran!");
                    std::string datoteka;
                    std::cout << "Naziv datoteke: ";
                    std::getline(std::cin, datoteka);
                    natjecaj->spremiUDatoteku(datoteka);
                    std::cout << "Podaci spremljeni!\n";
                    break;
                }
                case 9: {
                    std::string datoteka;
                    std::cout << "Naziv datoteke: ";
                    std::getline(std::cin, datoteka);
                    if (natjecaj) delete natjecaj;
                    natjecaj = new NagradniNatjecaj();
                    natjecaj->ucitajIzDatoteke(datoteka);
                    std::cout << "Podaci učitani!\n";
                    break;
                }
                case 0:
                    std::cout << "Izlaz iz programa...\n";
                    break;
                default:
                    std::cout << "Neispravan odabir!\n";
            }
        } catch (const Iznimka& e) {
            std::cerr << "❗ Greška: " << e.what() << "\n";
        } catch (const std::exception& e) {
            std::cerr << "❗ Standardna greška: " << e.what() << "\n";
        }
    } while (izbor != 0);

    delete natjecaj;
    return 0;
}