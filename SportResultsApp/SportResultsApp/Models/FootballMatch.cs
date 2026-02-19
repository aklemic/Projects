using System;
using System.ComponentModel.DataAnnotations;

namespace SportResultsApp.Models
{
    public class FootballMatch : Match
    {
        [Key]
        public int Id { get; set; } // Primarni ključ za bazu EF Core

        public int HomeYellowCards { get; set; }
        public int AwayYellowCards { get; set; }

        // Prazan konstruktor potreban EF Core
        public FootballMatch() : base("", "", DateTime.MinValue, 0, 0)
        {
        }

        // Konstruktor koji poziva bazni konstruktor s odgovarajućim parametrima
        public FootballMatch(string homeTeam, string awayTeam, DateTime date, int homeScore, int awayScore, int homeYellowCards, int awayYellowCards)
            : base(homeTeam, awayTeam, date, homeScore, awayScore)
        {
            HomeYellowCards = homeYellowCards;
            AwayYellowCards = awayYellowCards;
        }

        public override string GetWinner()
        {
            string winner = base.GetWinner();
            if (winner == "Neriješeno")
                return "Neriješeno, ali utakmica je puna kartona";
            return winner;
        }

        public int GetTotalCards()
        {
            return HomeYellowCards + AwayYellowCards;
        }

        public override string ToString()
        {
            return $"{HomeTeam} - {AwayTeam} | {Date:yyyy-MM-dd} | {HomeScore}:{AwayScore} | Kartoni: {HomeYellowCards}-{AwayYellowCards}";
        }
    }
}
