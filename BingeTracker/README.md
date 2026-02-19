Cilj aplikacije:

BingeTracker je jednostavna Android aplikacija za praćenje filmova i serija koje korisnik gleda, planira gledati ili je već pogledao.

Cilj je omogućiti brz unos naslova, spremanje podataka lokalno na uređaj i pregled popisa uz osnovne informacije poput vrste sadržaja, statusa gledanja i ocjene.

Implementirane funkcionalnosti:

- Početni ekran s opisom aplikacije i navigacijom na listu naslova i postavke.

- Spremanje naslova u lokalnu bazu koristeći Room (entitet Title s punim CRUD‐om: dodavanje, uređivanje, brisanje).

- Prikaz naslova u RecyclerViewu s klikom za uređivanje i long‐clickom za brisanje uz potvrdu.

- Forma za dodavanje/uređivanje naslova (naziv, tip: film/serija, status gledanja, opcionalna ocjena 1–10).

- Poruka kada je lista prazna (“Još nema naslova. Dodaj prvi!”).

- Ekran Postavke s opisom aplikacije i odabirom zadanog filtera liste (svi naslovi, trenutačno gledam, planiram gledati).

- Primjena odabranog filtera na listu naslova putem pohranjenih postavki.

- Lokalizacija korisničkog sučelja na hrvatski i engleski jezik (promjena ovisi o jeziku sustava).