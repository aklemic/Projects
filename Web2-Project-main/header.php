<?php
session_start();
?>

<div class="header">
    <div id="logo-container">
        <a href="index.php"><img src="img/logo.png" id="logo" alt="RetroKamera Logo" /></a>
    </div>
    <div id="icons-container">
        <?php if (isset($_SESSION['user_id'])): ?>
            <a href="logout.php" class="header-link"><i class="fa-solid fa-sign-out-alt"></i> Odjava</a>
        <?php else: ?>
            <a href="login.php" class="header-link"><i class="fa-solid fa-user"></i> Prijava</a>
        <?php endif; ?>
    </div>
</div>

<div class="navbar">
    <ul class="navbar-list">
        <li class="navbar-item"><a href="products.php">Svi proizvodi</a></li>
        <?php if (isset($_SESSION['user_id'])): ?>
            <li class="navbar-item"><a href="add_product.php">Dodaj proizvod</a></li>
            <li class="navbar-item"><a href="edit_product.php">Uredi proizvod</a></li>
            <li class="navbar-item"><a href="delete_product.php">Obriši proizvod</a></li>
        <?php endif; ?>
    </ul>
</div>