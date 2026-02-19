<?php
session_start();
include 'head.php';
include 'header.php';
?>

<div class="main" style="text-align: center; padding-top: 50px;">
    <div class="cards-container" style="justify-content: center;">
        <div class="card" style="max-width: 400px;">
            <img src="img/cameras.png" alt="Fotoaparati" class="card-img" />
            <div class="card-body">
                <h2 class="card-title">Asortiman proizvoda</h2>
                <a href="products.php" class="btn-view" style="width: 100%; display: inline-block; box-sizing: border-box;">Pogledaj proizvode</a>
            </div>
        </div>
    </div>
</div>

<?php
include 'footer.php';
?>