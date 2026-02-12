<?php
require 'head.php';
require 'connect.php';

$sql = "
    SELECT 
        p.product_id, p.name, p.price, c.name AS category_name
    FROM 
        products p
    JOIN 
        categories c ON p.category_id = c.category_id
    ORDER BY p.created_at DESC
";

$result = $conn->query($sql);

if (!$result) {
    die("Database error: " . $conn->error);
}
?>

<div class="container">
    <div style="margin-bottom: 20px; text-align: right;">
        <a href="index.php" class="btn-view" style="display: inline-block; padding: 8px 14px; border: 1.5px solid white; color: white; background: black; text-decoration: none; cursor: pointer; font-weight: bold; font-size: 1em; transition: all 0.3s;">
            Nazad na početnu
        </a>
    </div>

    <main class="products-main">
        <div class="cards-container">
            <?php if ($result->num_rows === 0): ?>
                <p style="color: white; text-align: center; width: 100%; margin-top: 40px; font-size: 1.5em;">
                    Trenutno nema proizvoda u ponudi.
                </p>
            <?php else: ?>
                <?php while ($product = $result->fetch_assoc()): ?>
                    <div class="card" style="border: 3px solid white; background: black; padding: 15px; margin-bottom: 20px; color: white;">
                        <div class="card-body" style="font-family: 'Roboto', sans-serif;">
                            <div class="card-type" style="font-size: 0.9em; margin-bottom: 5px; color: #aaa;">
                                <?= htmlspecialchars($product['category_name']) ?>
                            </div>
                            <div class="card-title" style="font-weight: bold; font-size: 1.2em; margin-bottom: 8px;">
                                <?= htmlspecialchars($product['name']) ?>
                            </div>
                            <div class="card-price" style="font-size: 1em; margin-bottom: 10px;">
                                <?= number_format($product['price'], 2) ?>€
                            </div>
                            <a href="#" class="btn-view" style="color: white; border: 3px solid white; padding: 8px 12px; text-decoration: none; font-weight: bold; display: inline-block; transition: all 0.3s;">
                                DODAJ U KOŠARICU
                            </a>
                        </div>
                    </div>
                <?php endwhile; ?>
            <?php endif; ?>
        </div>
    </main>
</div>
