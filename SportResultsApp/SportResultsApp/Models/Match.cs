using System;

namespace SportResultsApp.Models
{
    public class Match
    {
        public string HomeTeam { get; set; }
        public string AwayTeam { get; set; }
        public DateTime Date { get; set; }
        public int HomeScore { get; set; }
        public int AwayScore { get; set; }

        public Match(string homeTeam, string awayTeam, DateTime date, int homeScore, int awayScore)
        {
            HomeTeam = homeTeam;
            AwayTeam = awayTeam;
            Date = date;
            HomeScore = homeScore;
            AwayScore = awayScore;
        }

        public override string ToString()
        {
            return $"{HomeTeam} - {AwayTeam} | {Date:yyyy-MM-dd} | {HomeScore}:{AwayScore}";
        }

        public virtual string GetWinner()
        {
            if (HomeScore > AwayScore)
                return HomeTeam;
            else if (AwayScore > HomeScore)
                return AwayTeam;
            else
                return "Neriješeno";
        }
    }
}
