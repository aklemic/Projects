using Microsoft.EntityFrameworkCore;
using System;

namespace SportResultsApp.Models
{
    public class SportsContext : DbContext
    {
        public DbSet<FootballMatch> FootballMatches { get; set; }

        protected override void OnConfiguring(DbContextOptionsBuilder optionsBuilder)
        {
            if (!optionsBuilder.IsConfigured)
            {
                // Koristi apsolutni put do sports.db u folderu gdje se pokreće aplikacija
                string dbPath = System.IO.Path.Combine(AppDomain.CurrentDomain.BaseDirectory, "sports.db");
                optionsBuilder.UseSqlite($"Data Source={dbPath}");

                System.Diagnostics.Debug.WriteLine($"Using SQLite DB at: {dbPath}");
            }
        }
    }
}
