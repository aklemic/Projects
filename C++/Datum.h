#ifndef DATUM_H
#define DATUM_H

#include <string>
#include <iostream>
#include <iomanip>
#include <sstream>
#include <stdexcept>

struct Datum {
    int dan, mjesec, godina;
    
    Datum(int d = 1, int m = 1, int g = 2000);
    static Datum parsirajDatum(const std::string& str);
    
    void postavi(int d, int m, int g);
    std::string toString() const;
    bool operator<(const Datum& other) const;
    bool operator>(const Datum& other) const;
    bool operator==(const Datum& other) const;
    friend std::ostream& operator<<(std::ostream& os, const Datum& datum);
};

#endif