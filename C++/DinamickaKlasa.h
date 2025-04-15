#ifndef DINAMICKAKLASA_H
#define DINAMICKAKLASA_H

class DinamickaKlasa {
private:
    int* podatak;
public:
    DinamickaKlasa(int v = 0);
    DinamickaKlasa(const DinamickaKlasa& other);
    ~DinamickaKlasa();
    DinamickaKlasa& operator=(const DinamickaKlasa& other);
};

#endif