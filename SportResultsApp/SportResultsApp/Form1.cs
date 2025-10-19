using SportResultsApp.Models;
using Microsoft.EntityFrameworkCore;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading;
using System.Windows.Forms;

namespace SportResultsApp
{
    public partial class Form1 : Form
    {
        private readonly SportsContext _dbContext = new SportsContext();
        private List<FootballMatch> matches = new List<FootballMatch>();
        private int editingIndex = -1;

        public delegate void MatchAddedEventHandler(object sender, FootballMatch match);
        public event MatchAddedEventHandler MatchAdded;

        private readonly object lockObj = new object();

        public Form1()
        {
            InitializeComponent();

            btnAddMatch.Click += BtnAddMatch_Click;
            btnDeleteMatch.Click += BtnDeleteMatch_Click;
            btnTopMatches.Click += BtnTopMatches_Click;
            btnShowStats.Click += BtnShowStats_Click;
            btnExportCsv.Click += BtnExportCsv_Click;
            btnLargestDiff.Click += BtnLargestDiff_Click;
            btnClearFilter.Click += BtnClearFilter_Click;
            btnShowWins.Click += BtnShowWins_Click;

            lstMatches.SelectedIndexChanged += LstMatches_SelectedIndexChanged;
            txtSearch.TextChanged += TxtSearch_TextChanged;

            MatchAdded += Form1_MatchAdded;

            LoadMatchesFromDb();
        }

        private void LoadMatchesFromDb()
        {
            matches = _dbContext.FootballMatches.OrderBy(m => m.Date).ToList();
            UpdateMatchList();
        }

        private void SaveMatchToDb(FootballMatch match)
        {
            lock (lockObj)
            {
                if (match.Id == 0)
                {
                    _dbContext.FootballMatches.Add(match);
                }
                else
                {
                    var existingMatch = _dbContext.FootballMatches.Find(match.Id);
                    if (existingMatch != null)
                    {
                        existingMatch.HomeTeam = match.HomeTeam;
                        existingMatch.AwayTeam = match.AwayTeam;
                        existingMatch.Date = match.Date;
                        existingMatch.HomeScore = match.HomeScore;
                        existingMatch.AwayScore = match.AwayScore;
                        existingMatch.HomeYellowCards = match.HomeYellowCards;
                        existingMatch.AwayYellowCards = match.AwayYellowCards;
                    }
                    else
                    {
                        _dbContext.FootballMatches.Add(match);
                    }
                }
                _dbContext.SaveChanges();
            }
        }

        private void BtnAddMatch_Click(object sender, EventArgs e)
        {
            try
            {
                string home = txtHomeTeam.Text.Trim();
                string away = txtAwayTeam.Text.Trim();
                DateTime date = dtpMatchDate.Value.Date;

                if (!int.TryParse(txtHomeScore.Text, out int homeScore) || homeScore < 0)
                    throw new Exception("Pogrešan rezultat domaćih");
                if (!int.TryParse(txtAwayScore.Text, out int awayScore) || awayScore < 0)
                    throw new Exception("Pogrešan rezultat gostiju");

                int homeYellowCards = (int)nudHomeYellowCards.Value;
                int awayYellowCards = (int)nudAwayYellowCards.Value;

                FootballMatch match = new FootballMatch(home, away, date, homeScore, awayScore, homeYellowCards, awayYellowCards);

                if (editingIndex == -1)
                {
                    AddMatchInBackground(match);
                }
                else
                {
                    lock (lockObj)
                    {
                        var oldMatch = matches[editingIndex];
                        match.Id = oldMatch.Id;
                        matches[editingIndex] = match;
                        editingIndex = -1;
                    }
                    SaveMatchToDb(match);
                    UpdateMatchList();
                    ClearInputs();
                    btnAddMatch.Text = "Dodaj utakmicu";
                    lstMatches.ClearSelected();
                }
            }
            catch (Exception ex)
            {
                MessageBox.Show("Greška: " + ex.Message);
            }
        }

        private void AddMatchInBackground(FootballMatch match)
        {
            Thread thread = new Thread(() =>
            {
                lock (lockObj)
                    matches.Add(match);
                SaveMatchToDb(match);
                MatchAdded?.Invoke(this, match);
            });
            thread.Start();
        }

        private void Form1_MatchAdded(object sender, FootballMatch match)
        {
            if (InvokeRequired)
            {
                Invoke(new Action(() =>
                {
                    UpdateMatchList();
                    ClearInputs();
                }));
            }
            else
            {
                UpdateMatchList();
                ClearInputs();
            }
        }

        private void UpdateMatchList()
        {
            lstMatches.Items.Clear();
            lock (lockObj)
            {
                foreach (var m in matches)
                    lstMatches.Items.Add(m.ToString());
            }
        }

        private void BtnDeleteMatch_Click(object sender, EventArgs e)
        {
            int selectedIndex = lstMatches.SelectedIndex;
            if (selectedIndex >= 0 && selectedIndex < matches.Count)
            {
                lock (lockObj)
                    matches.RemoveAt(selectedIndex);
                _dbContext.SaveChanges();
                UpdateMatchList();
                ClearInputs();
                editingIndex = -1;
                btnAddMatch.Text = "Dodaj utakmicu";
            }
            else
            {
                MessageBox.Show("Odaberite utakmicu za brisanje!");
            }
        }

        private void BtnTopMatches_Click(object sender, EventArgs e)
        {
            List<FootballMatch> topMatches;
            lock (lockObj)
                topMatches = matches.OrderByDescending(m => m.HomeScore + m.AwayScore).Take(5).ToList();

            lstMatches.Items.Clear();
            foreach (var m in topMatches)
                lstMatches.Items.Add(m.ToString());
        }

        private void BtnShowStats_Click(object sender, EventArgs e)
        {
            int totalMatches;
            int totalGoals;
            lock (lockObj)
            {
                totalMatches = matches.Count;
                totalGoals = matches.Sum(m => m.HomeScore + m.AwayScore);
            }
            double avgGoals = totalMatches > 0 ? (double)totalGoals / totalMatches : 0;
            MessageBox.Show($"Ukupan broj utakmica: {totalMatches}\nUkupno golova: {totalGoals}\nProsjek golova po utakmici: {avgGoals:F2}", "Statistika utakmica");
        }

        private void BtnExportCsv_Click(object sender, EventArgs e)
        {
            string exportFile = "matches_export.csv";
            try
            {
                using (System.IO.StreamWriter writer = new System.IO.StreamWriter(exportFile))
                {
                    writer.WriteLine("HomeTeam,AwayTeam,MatchDate,HomeScore,AwayScore");
                    lock (lockObj)
                    {
                        foreach (var match in matches)
                            writer.WriteLine($"{match.HomeTeam},{match.AwayTeam},{match.Date:yyyy-MM-dd},{match.HomeScore},{match.AwayScore}");
                    }
                }
                MessageBox.Show($"Uspješno eksportirano u {exportFile}");
            }
            catch (Exception ex)
            {
                MessageBox.Show("Greška pri izvozu podataka: " + ex.Message);
            }
        }

        private void BtnLargestDiff_Click(object sender, EventArgs e)
        {
            List<FootballMatch> diffMatches;
            lock (lockObj)
                diffMatches = matches.OrderByDescending(m => Math.Abs(m.HomeScore - m.AwayScore)).Take(5).ToList();

            lstMatches.Items.Clear();
            foreach (var m in diffMatches)
                lstMatches.Items.Add(m.ToString());
        }

        private void BtnClearFilter_Click(object sender, EventArgs e)
        {
            txtSearch.Text = string.Empty;
            UpdateMatchList();
        }

        private void BtnShowWins_Click(object sender, EventArgs e)
        {
            Dictionary<string, int> winsCount = new Dictionary<string, int>();
            lock (lockObj)
            {
                foreach (var m in matches)
                {
                    string winner = m.GetWinner();
                    if (!winner.StartsWith("Neriješeno"))
                    {
                        if (!winsCount.ContainsKey(winner))
                            winsCount[winner] = 0;
                        winsCount[winner]++;
                    }
                }
            }
            if (winsCount.Count == 0)
            {
                MessageBox.Show("Nema registriranih pobjeda.");
                return;
            }

            var result = string.Join("\n", winsCount.OrderByDescending(kvp => kvp.Value).Select(kvp => $"{kvp.Key}: {kvp.Value} pobjeda"));
            MessageBox.Show(result, "Statistika pobjeda");
        }

        private void LstMatches_SelectedIndexChanged(object sender, EventArgs e)
        {
            int index = lstMatches.SelectedIndex;
            if (index >= 0 && index < matches.Count)
            {
                FootballMatch m;
                lock (lockObj)
                    m = matches[index];

                txtHomeTeam.Text = m.HomeTeam;
                txtAwayTeam.Text = m.AwayTeam;
                dtpMatchDate.Value = m.Date;
                txtHomeScore.Text = m.HomeScore.ToString();
                txtAwayScore.Text = m.AwayScore.ToString();
                nudHomeYellowCards.Value = m.HomeYellowCards;
                nudAwayYellowCards.Value = m.AwayYellowCards;

                editingIndex = index;
                btnAddMatch.Text = "Spremi promjene";
            }
        }

        private void listBox1_SelectedIndexChanged(object sender, EventArgs e)
        {
            LstMatches_SelectedIndexChanged(sender, e);
        }

        private void TxtSearch_TextChanged(object sender, EventArgs e)
        {
            string filter = txtSearch.Text.Trim().ToLower();
            List<FootballMatch> filteredMatches;

            lock (lockObj)
                filteredMatches = matches.Where(m => m.HomeTeam.ToLower().Contains(filter) || m.AwayTeam.ToLower().Contains(filter)).ToList();

            lstMatches.Items.Clear();
            foreach (var m in filteredMatches)
                lstMatches.Items.Add(m.ToString());
        }

        private void SortAndRefreshList()
        {
            lock (lockObj)
                matches = matches.OrderBy(m => m.Date).ToList();

            UpdateMatchList();
        }

        private void ClearInputs()
        {
            txtHomeTeam.Clear();
            txtAwayTeam.Clear();
            txtHomeScore.Clear();
            txtAwayScore.Clear();
            nudHomeYellowCards.Value = 0;
            nudAwayYellowCards.Value = 0;
        }

        // Prazne event handler metode zbog designer veza
        private void textBox1_TextChanged(object sender, EventArgs e) { }
        private void textBox2_TextChanged(object sender, EventArgs e) { }
        private void textBox4_TextChanged(object sender, EventArgs e) { }
        private void textBox5_TextChanged(object sender, EventArgs e) { }
        private void button1_Click(object sender, EventArgs e) { }
    }
}
