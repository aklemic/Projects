#include "Datum.h"

Datum::Datum(int d, int m, int g) : dan(d), mjesec(m), godina(g) {
    if (d < 1 || d > 31 || m < 1 || m > 12 || g < 2000)
        throw std::invalid_argument("Neispravan datum!");
}

Datum Datum::parsirajDatum(const std::string& str) {
    std::istringstream iss(str);
    char delimiter;
    int d, m, g;
    if (!(iss >> d >> delimiter >> m >> delimiter >> g))
        throw std::invalid_argument("Neispravan format datuma!");
    return Datum(d, m, g);
}

void Datum::postavi(int d, int m, int g) {
    if (d < 1 || d > 31) throw std::invalid_argument("Neispravan dan!");
    dan = d;
    mjesec = m;
    godina = g;
}

std::string Datum::toString() const {
    std::ostringstream oss;
    oss << *this; // Koristi operator <<
    return oss.str();
}

bool Datum::operator<(const Datum& other) const {
    if (godina != other.godina) return godina < other.godina;
    if (mjesec != other.mjesec) return mjesec < other.mjesec;
    return dan < other.dan;
}

bool Datum::operator>(const Datum& other) const {
    return other < *this;
}

bool Datum::operator==(const Datum& other) const {
    return dan == other.dan && mjesec == other.mjesec && godina == other.godina;
}

std::ostream& operator<<(std::ostream& os, const Datum& datum) {
    os << std::setw(2) << std::setfill('0') << datum.dan << "/"
       << std::setw(2) << std::setfill('0') << datum.mjesec << "/"
       << datum.godina;
    return os;
}