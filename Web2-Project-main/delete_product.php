<?php
session_start();
require 'connect.php';
require 'head.php';
require 'header.php';

if (!isset($_SESSION['user_id'])) {
    header("Location: login.php");
    exit();
}

$message = "";

if ($_SERVER['REQUEST_METHOD'] === 'POST' && isset($_POST['delete_product'])) {
    $id = intval($_POST['product_id']);
    if ($id > 0) {
        $stmt = $conn->prepare("DELETE FROM products WHERE product_id = ?");
        $stmt->bind_param("i", $id);
        if ($stmt->execute()) {
            $message = "Proizvod je uspješno obrisan!";
        } else {
            $message = "Greška prilikom brisanja proizvoda: " . $stmt->error;
        }
        $stmt->close();
    }
}

$products = $conn->query("SELECT * FROM products");
$categories = [];
$res = $conn->query("SELECT category_id, name FROM categories");
while ($row = $res->fetch_assoc()) {
    $categories[$row['category_id']] = $row['name'];
}
?>

<style>
    body {
        background: black;
        color: white;
        font-family: 'Roboto', sans-serif;
        margin: 0;
        padding: 20px;
    }
    .container {
        max-width: 960px;
        margin: 40px auto;
        border: 3px solid white;
        padding: 20px;
        background-color: black;
    }
    h2 {
        text-align: center;
        font-weight: bold;
        border-bottom: 3px solid white;
        padding-bottom: 10px;
        margin-bottom: 25px;
    }
    .message {
        font-weight: bold;
        border-left: 5px solid;
        padding: 10px;
        margin-bottom: 20px;
        max-width: 960px;
        margin-left: auto;
        margin-right: auto;
    }
    .message.success {
        border-color: #2ecc71;
        background-color: #113311;
        color: #aaffaa;
    }
    .message.error {
        border-color: #e74c3c;
        background-color: #331111;
        color: #ffaaaa;
    }
    table {
        width: 100%;
        border-collapse: collapse;
    }
    th, td {
        padding: 10px;
        border: 3px solid white;
        vertical-align: middle;
        text-align: left;
    }
    th {
        background-color: #222;
        font-weight: bold;
    }
    button.delete-btn {
        background-color: #e74c3c;
        color: white;
        border: none;
        padding: 6px 12px;
        cursor: pointer;
        font-weight: bold;
        border-radius: 3px;
        transition: background-color 0.3s ease;
    }
    button.delete-btn:hover {
        background-color: #c0392b;
    }
</style>

<div class="container">
    <h2>Obriši proizvod</h2>

    <?php if (!empty($message)): ?>
        <div class="message <?= strpos($message, 'uspješno') !== false ? 'success' : 'error' ?>">
            <?= htmlspecialchars($message) ?>
        </div>
    <?php endif; ?>

    <table>
        <thead>
            <tr>
                <th>ID</th>
                <th>Naziv</th>
                <th>Opis</th>
                <th>Cijena</th>
                <th>Kategorija</th>
                <th>Obriši</th>
            </tr>
        </thead>
        <tbody>
        <?php while ($p = $products->fetch_assoc()): ?>
            <tr>
                <td><?= $p['product_id'] ?></td>
                <td><?= htmlspecialchars($p['name']) ?></td>
                <td><?= htmlspecialchars($p['description']) ?></td>
                <td><?= number_format($p['price'], 2) ?> €</td>
                <td><?= htmlspecialchars($categories[$p['category_id']] ?? 'Nepoznato') ?></td>
                <td>
                    <form method="POST" onsubmit="return confirm('Jeste li sigurni da želite obrisati ovaj proizvod?');" style="margin:0;">
                        <input type="hidden" name="product_id" value="<?= $p['product_id'] ?>" />
                        <button type="submit" name="delete_product" class="delete-btn">Obriši</button>
                    </form>
                </td>
            </tr>
        <?php endwhile; ?>
        </tbody>
    </table>
</div>