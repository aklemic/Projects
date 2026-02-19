<?php
session_start();
require 'connect.php';

$error = '';
$success = '';

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $first_name = trim($_POST['first_name'] ?? '');
    $last_name = trim($_POST['last_name'] ?? '');
    $email = trim($_POST['email'] ?? '');
    $password = $_POST['password'] ?? '';
    $password_repeat = $_POST['password_repeat'] ?? '';

    if ($password !== $password_repeat) {
        $error = 'Lozinke se ne podudaraju.';
    } elseif (!filter_var($email, FILTER_VALIDATE_EMAIL)) {
        $error = 'Neispravan email.';
    } elseif (empty($first_name) || empty($last_name)) {
        $error = 'Ime i prezime su obavezni.';
    } else {
        $stmt = $conn->prepare("SELECT user_id FROM users WHERE email = ?");
        $stmt->bind_param('s', $email);
        $stmt->execute();
        $stmt->store_result();

        if ($stmt->num_rows > 0) {
            $error = 'Email već postoji.';
        } else {
            $stmt = $conn->prepare("INSERT INTO users (first_name, last_name, email, password) VALUES (?, ?, ?, ?)");
            $stmt->bind_param('ssss', $first_name, $last_name, $email, $password);

            if ($stmt->execute()) {
                $success = 'Uspješno ste se registrirali. Možete se prijaviti.';
            } else {
                $error = 'Greška prilikom registracije. Pokušajte ponovno.';
            }
        }
        $stmt->close();
    }
}
?>

<!DOCTYPE html>
<html lang="hr">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <title>Retrokamera - Registracija</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.6/dist/css/bootstrap.min.css" rel="stylesheet" />
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.6/dist/js/bootstrap.bundle.min.js"></script>
    <link href="https://cdn.jsdelivr.net/npm/sweetalert2@11.22.0/dist/sweetalert2.min.css" rel="stylesheet" />
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11.22.0/dist/sweetalert2.all.min.js"></script>
    <link rel="stylesheet" href="bootstrapStyle.css" />
</head>
<body>

<div class="container d-flex align-items-center justify-content-center vh-100">
    <div class="border border-3 rounded p-3 mt-3 form-width" style="min-width: 350px; max-width: 400px;">
        <form method="POST" action="">
            <img src="img/logo.png" alt="RetroKamera Logo" class="d-block mx-auto my-3" style="max-width:100px; max-height:100px;" />
            <h3 class="text-center mb-3">Registriraj se za RetroKameru</h3>

            <?php if ($error): ?>
                <div class="alert alert-danger"><?= htmlspecialchars($error) ?></div>
            <?php elseif ($success): ?>
                <div class="alert alert-success"><?= htmlspecialchars($success) ?></div>
            <?php endif; ?>

            <div class="mb-3">
                <label for="first-name" class="form-label">Ime</label>
                <input type="text" name="first_name" id="first-name" class="form-control" required value="<?= htmlspecialchars($_POST['first_name'] ?? '') ?>" />
            </div>

            <div class="mb-3">
                <label for="last-name" class="form-label">Prezime</label>
                <input type="text" name="last_name" id="last-name" class="form-control" required value="<?= htmlspecialchars($_POST['last_name'] ?? '') ?>" />
            </div>

            <div class="mb-3">
                <label for="register-email" class="form-label">Email</label>
                <input type="email" name="email" id="register-email" class="form-control" required value="<?= htmlspecialchars($_POST['email'] ?? '') ?>" />
            </div>

            <div class="mb-3">
                <label for="register-password" class="form-label">Lozinka</label>
                <input type="password" name="password" id="register-password" class="form-control" required />
            </div>

            <div class="mb-3">
                <label for="register-password-repeat" class="form-label">Ponovite lozinku</label>
                <input type="password" name="password_repeat" id="register-password-repeat" class="form-control" required />
            </div>

            <div class="text-end">
                <button type="submit" class="btn btn-primary">Registrirajte se</button>
            </div>

            <div class="text-center mt-2">
                <span>Već ste registrirani? <a href="login.php">Prijavite se</a></span>
            </div>
        </form>
    </div>
</div>

</body>
</html>