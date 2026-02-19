namespace SportResultsApp
{
    partial class Form1
    {
        /// <summary>
        ///  Required designer variable.
        /// </summary>
        private System.ComponentModel.IContainer components = null;

        /// <summary>
        ///  Clean up any resources being used.
        /// </summary>
        /// <param name="disposing">true if managed resources should be disposed; otherwise, false.</param>
        protected override void Dispose(bool disposing)
        {
            if (disposing && (components != null))
            {
                components.Dispose();
            }
            base.Dispose(disposing);
        }

        #region Windows Form Designer generated code

        /// <summary>
        ///  Required method for Designer support - do not modify
        ///  the contents of this method with the code editor.
        /// </summary>
        private void InitializeComponent()
        {
            txtHomeTeam = new TextBox();
            txtAwayTeam = new TextBox();
            txtHomeScore = new TextBox();
            txtAwayScore = new TextBox();
            btnAddMatch = new Button();
            lstMatches = new ListBox();
            btnDeleteMatch = new Button();
            txtSearch = new TextBox();
            btnTopMatches = new Button();
            btnShowStats = new Button();
            btnExportCsv = new Button();
            dtpMatchDate = new DateTimePicker();
            btnLargestDiff = new Button();
            btnClearFilter = new Button();
            btnShowWins = new Button();
            lblHomeYellowCards = new Label();
            nudHomeYellowCards = new NumericUpDown();
            lblAwayYellowCards = new Label();
            nudAwayYellowCards = new NumericUpDown();
            ((System.ComponentModel.ISupportInitialize)nudHomeYellowCards).BeginInit();
            ((System.ComponentModel.ISupportInitialize)nudAwayYellowCards).BeginInit();
            SuspendLayout();
            // 
            // txtHomeTeam
            // 
            txtHomeTeam.AccessibleName = "txtHomeTeam";
            txtHomeTeam.Location = new Point(118, 27);
            txtHomeTeam.Name = "txtHomeTeam";
            txtHomeTeam.Size = new Size(125, 27);
            txtHomeTeam.TabIndex = 0;
            txtHomeTeam.Text = "Domaća ekipa";
            txtHomeTeam.TextAlign = HorizontalAlignment.Center;
            txtHomeTeam.TextChanged += textBox1_TextChanged;
            // 
            // txtAwayTeam
            // 
            txtAwayTeam.AccessibleName = "txtAwayTeam";
            txtAwayTeam.Location = new Point(594, 27);
            txtAwayTeam.Name = "txtAwayTeam";
            txtAwayTeam.Size = new Size(125, 27);
            txtAwayTeam.TabIndex = 1;
            txtAwayTeam.Text = "Gostujuća ekipa";
            txtAwayTeam.TextAlign = HorizontalAlignment.Center;
            txtAwayTeam.TextChanged += textBox2_TextChanged;
            // 
            // txtHomeScore
            // 
            txtHomeScore.AccessibleName = "txtHomeScore";
            txtHomeScore.Location = new Point(118, 222);
            txtHomeScore.Name = "txtHomeScore";
            txtHomeScore.Size = new Size(125, 27);
            txtHomeScore.TabIndex = 3;
            txtHomeScore.Text = "Rezultat domaćih";
            txtHomeScore.TextAlign = HorizontalAlignment.Center;
            txtHomeScore.TextChanged += textBox4_TextChanged;
            // 
            // txtAwayScore
            // 
            txtAwayScore.AccessibleName = "txtAwayScore";
            txtAwayScore.Location = new Point(594, 222);
            txtAwayScore.Name = "txtAwayScore";
            txtAwayScore.Size = new Size(125, 27);
            txtAwayScore.TabIndex = 4;
            txtAwayScore.Text = "Rezultat gostiju";
            txtAwayScore.TextAlign = HorizontalAlignment.Center;
            txtAwayScore.TextChanged += textBox5_TextChanged;
            // 
            // btnAddMatch
            // 
            btnAddMatch.AccessibleName = "btnAddMatch";
            btnAddMatch.Location = new Point(371, 268);
            btnAddMatch.Name = "btnAddMatch";
            btnAddMatch.Size = new Size(94, 29);
            btnAddMatch.TabIndex = 5;
            btnAddMatch.Text = "Dodaj utakmicu";
            btnAddMatch.UseVisualStyleBackColor = true;
            btnAddMatch.Click += button1_Click;
            // 
            // lstMatches
            // 
            lstMatches.AccessibleName = "lstMatches";
            lstMatches.FormattingEnabled = true;
            lstMatches.Location = new Point(594, 389);
            lstMatches.Name = "lstMatches";
            lstMatches.Size = new Size(268, 164);
            lstMatches.TabIndex = 6;
            lstMatches.SelectedIndexChanged += listBox1_SelectedIndexChanged;
            // 
            // btnDeleteMatch
            // 
            btnDeleteMatch.Location = new Point(371, 303);
            btnDeleteMatch.Name = "btnDeleteMatch";
            btnDeleteMatch.Size = new Size(94, 29);
            btnDeleteMatch.TabIndex = 7;
            btnDeleteMatch.Text = "Obriši utakmicu";
            btnDeleteMatch.UseVisualStyleBackColor = true;
            // 
            // txtSearch
            // 
            txtSearch.Location = new Point(118, 389);
            txtSearch.Name = "txtSearch";
            txtSearch.Size = new Size(125, 27);
            txtSearch.TabIndex = 8;
            // 
            // btnTopMatches
            // 
            btnTopMatches.Location = new Point(371, 338);
            btnTopMatches.Name = "btnTopMatches";
            btnTopMatches.Size = new Size(94, 29);
            btnTopMatches.TabIndex = 9;
            btnTopMatches.Text = "Najbolje utakmice";
            btnTopMatches.UseVisualStyleBackColor = true;
            // 
            // btnShowStats
            // 
            btnShowStats.Location = new Point(371, 373);
            btnShowStats.Name = "btnShowStats";
            btnShowStats.Size = new Size(94, 29);
            btnShowStats.TabIndex = 10;
            btnShowStats.Text = "Prikaži statistiku";
            btnShowStats.UseVisualStyleBackColor = true;
            // 
            // btnExportCsv
            // 
            btnExportCsv.Location = new Point(371, 408);
            btnExportCsv.Name = "btnExportCsv";
            btnExportCsv.Size = new Size(94, 29);
            btnExportCsv.TabIndex = 11;
            btnExportCsv.Text = "Export CSV";
            btnExportCsv.UseVisualStyleBackColor = true;
            // 
            // dtpMatchDate
            // 
            dtpMatchDate.Location = new Point(311, 129);
            dtpMatchDate.Name = "dtpMatchDate";
            dtpMatchDate.Size = new Size(250, 27);
            dtpMatchDate.TabIndex = 12;
            // 
            // btnLargestDiff
            // 
            btnLargestDiff.Location = new Point(371, 443);
            btnLargestDiff.Name = "btnLargestDiff";
            btnLargestDiff.Size = new Size(94, 29);
            btnLargestDiff.TabIndex = 13;
            btnLargestDiff.Text = "Najveće razlike";
            btnLargestDiff.UseVisualStyleBackColor = true;
            // 
            // btnClearFilter
            // 
            btnClearFilter.Location = new Point(371, 478);
            btnClearFilter.Name = "btnClearFilter";
            btnClearFilter.Size = new Size(94, 29);
            btnClearFilter.TabIndex = 14;
            btnClearFilter.Text = "Očisti pretraživanje";
            btnClearFilter.UseVisualStyleBackColor = true;
            // 
            // btnShowWins
            // 
            btnShowWins.Location = new Point(371, 513);
            btnShowWins.Name = "btnShowWins";
            btnShowWins.Size = new Size(94, 29);
            btnShowWins.TabIndex = 15;
            btnShowWins.Text = "Prikaži pobjednike";
            btnShowWins.UseVisualStyleBackColor = true;
            // 
            // lblHomeYellowCards
            // 
            lblHomeYellowCards.AutoSize = true;
            lblHomeYellowCards.Location = new Point(118, 272);
            lblHomeYellowCards.Name = "lblHomeYellowCards";
            lblHomeYellowCards.Size = new Size(147, 20);
            lblHomeYellowCards.TabIndex = 16;
            lblHomeYellowCards.Text = "Žuti kartoni domaćih";
            // 
            // nudHomeYellowCards
            // 
            nudHomeYellowCards.Location = new Point(118, 303);
            nudHomeYellowCards.Maximum = new decimal(new int[] { 20, 0, 0, 0 });
            nudHomeYellowCards.Name = "nudHomeYellowCards";
            nudHomeYellowCards.Size = new Size(150, 27);
            nudHomeYellowCards.TabIndex = 17;
            // 
            // lblAwayYellowCards
            // 
            lblAwayYellowCards.AutoSize = true;
            lblAwayYellowCards.Location = new Point(594, 272);
            lblAwayYellowCards.Name = "lblAwayYellowCards";
            lblAwayYellowCards.Size = new Size(134, 20);
            lblAwayYellowCards.TabIndex = 18;
            lblAwayYellowCards.Text = "Žuti kartoni gostiju";
            // 
            // nudAwayYellowCards
            // 
            nudAwayYellowCards.Location = new Point(594, 303);
            nudAwayYellowCards.Maximum = new decimal(new int[] { 20, 0, 0, 0 });
            nudAwayYellowCards.Name = "nudAwayYellowCards";
            nudAwayYellowCards.Size = new Size(150, 27);
            nudAwayYellowCards.TabIndex = 19;
            // 
            // Form1
            // 
            AutoScaleDimensions = new SizeF(8F, 20F);
            AutoScaleMode = AutoScaleMode.Font;
            ClientSize = new Size(893, 628);
            Controls.Add(nudAwayYellowCards);
            Controls.Add(lblAwayYellowCards);
            Controls.Add(nudHomeYellowCards);
            Controls.Add(lblHomeYellowCards);
            Controls.Add(btnShowWins);
            Controls.Add(btnClearFilter);
            Controls.Add(btnLargestDiff);
            Controls.Add(dtpMatchDate);
            Controls.Add(btnExportCsv);
            Controls.Add(btnShowStats);
            Controls.Add(btnTopMatches);
            Controls.Add(txtSearch);
            Controls.Add(btnDeleteMatch);
            Controls.Add(lstMatches);
            Controls.Add(btnAddMatch);
            Controls.Add(txtAwayScore);
            Controls.Add(txtHomeScore);
            Controls.Add(txtAwayTeam);
            Controls.Add(txtHomeTeam);
            Name = "Form1";
            Text = "Form1";
            ((System.ComponentModel.ISupportInitialize)nudHomeYellowCards).EndInit();
            ((System.ComponentModel.ISupportInitialize)nudAwayYellowCards).EndInit();
            ResumeLayout(false);
            PerformLayout();
        }

        #endregion

        private TextBox txtHomeTeam;
        private TextBox txtAwayTeam;
        private TextBox txtHomeScore;
        private TextBox txtAwayScore;
        private Button btnAddMatch;
        private ListBox lstMatches;
        private Button btnDeleteMatch;
        private TextBox txtSearch;
        private Button btnTopMatches;
        private Button btnShowStats;
        private Button btnExportCsv;
        private DateTimePicker dtpMatchDate;
        private Button btnLargestDiff;
        private Button btnClearFilter;
        private Button btnShowWins;
        private Label lblHomeYellowCards;
        private NumericUpDown nudHomeYellowCards;
        private Label lblAwayYellowCards;
        private NumericUpDown nudAwayYellowCards;
    }
}
