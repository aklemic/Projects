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

if ($_SERVER['REQUEST_METHOD'] === 'POST' && isset($_POST['edit_product'])) {
    $id = intval($_POST['product_id']);
    $name = trim($_POST['name'] ?? '');
    $price = floatval($_POST['price'] ?? 0);
    $category_id = intval($_POST['category'] ?? 0);
    $description = trim($_POST['description'] ?? '');

    if ($name !== '' && $price > 0 && $category_id > 0) {
        $stmt = $conn->prepare("UPDATE products SET name = ?, description = ?, price = ?, category_id = ? WHERE product_id = ?");
        $stmt->bind_param("ssdii", $name, $description, $price, $category_id, $id);
        if ($stmt->execute()) {
            $message = "Proizvod je uspješno azuriran!";
        } else {
            $message = "Greška prilikom ažuriranja proizvoda: " . $stmt->error;
        }
        $stmt->close();
    } else {
        $message = "Molimo unesite ispravne podatke.";
    }
}

$products = $conn->query("SELECT * FROM products");
$categories = [];
$res = $conn->query("SELECT category_id, name FROM categories");
while ($row = $res->fetch_assoc()) {
    $categories[$row['category_id']] = $row['name'];
}
?>

<!DOCTYPE html>
<html lang="hr">
<head>
<meta charset="UTF-8" />
<title>Uredi proizvode</title>
<style>
    body {
        background: black;
        color: white;
        font-family: 'Roboto', sans-serif;
        margin: 0; padding: 20px;
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
    }
    th {
        background-color: #222;
        font-weight: bold;
        text-align: left;
    }
    input, select, textarea {
        width: 100%;
        box-sizing: border-box;
        padding: 8px;
        background: black;
        color: white;
        border: 3px solid white;
        font-family: 'Roboto', sans-serif;
        font-size: 1em;
        resize: vertical;
    }
    textarea {
        min-height: 50px;
    }
    button {
        background-color: black;
        color: white;
        border: 3px solid white;
        padding: 8px 20px;
        font-weight: bold;
        cursor: pointer;
        transition: background-color 0.3s ease, color 0.3s ease;
        width: 100%;
    }
    button:hover {
        background-color: white;
        color: black;
    }
    .price-field {
        -moz-appearance: textfield;
        -webkit-appearance: none;
        appearance: none;
    }
</style>
</head>
<body>
<div class="container">
<h2>Uredi proizvode</h2>

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
            <th>Cijena (€)</th>
            <th>Kategorija</th>
            <th>Akcije</th>
        </tr>
    </thead>
    <tbody>
        <?php while ($p = $products->fetch_assoc()): ?>
            <tr>
                <form method="POST">
                    <td>
                        <?= $p['product_id'] ?>
                        <input type="hidden" name="product_id" value="<?= $p['product_id'] ?>">
                    </td>
                    <td><input type="text" name="name" required value="<?= htmlspecialchars($p['name']) ?>" /></td>
                    <td><textarea name="description"><?= htmlspecialchars($p['description']) ?></textarea></td>
                    <td><input type="text" step="0.01" min="0" name="price" required value="<?= htmlspecialchars($p['price']) ?>" /></td>
                    <td>
                        <select name="category" required>
                            <?php foreach ($categories as $id => $cat_name): ?>
                                <option value="<?= $id ?>" <?= ($p['category_id'] == $id ? 'selected' : '') ?>>
                                    <?= htmlspecialchars($cat_name) ?>
                                </option>
                            <?php endforeach; ?>
                        </select>
                    </td>
                    <td><button type="submit" name="edit_product">Spremi</button></td>
                </form>
            </tr>
        <?php endwhile; ?>
    </tbody>
</table>
</div>
</body>
</html>