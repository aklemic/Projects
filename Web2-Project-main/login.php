<?php
session_start();
require 'connect.php';

$error = '';

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $email = $_POST['email'] ?? '';
    $password = $_POST['password'] ?? '';

    $stmt = $conn->prepare("SELECT user_id, email, password FROM users WHERE email = ?");
    $stmt->bind_param('s', $email);
    
    $stmt->execute();
    $res = $stmt->get_result();

    if ($res && $res->num_rows === 1) {
        $row = $res->fetch_assoc();
        if ($row['password'] === $password) {
            $_SESSION['user_id'] = $row['user_id'];
            $_SESSION['username'] = $row['email'];
            header("Location: index.php");
            exit();
        }
    }
    $error = "Pogrešan email ili lozinka.";
}
?>

<!DOCTYPE html>
<html lang="hr">
<head>
    <meta charset="UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1"/>
    <title>Retrokamera - Prijava</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.6/dist/css/bootstrap.min.css" rel="stylesheet"/>
    <link href="https://cdn.jsdelivr.net/npm/sweetalert2@11.22.0/dist/sweetalert2.min.css" rel="stylesheet"/>
    <link rel="stylesheet" href="bootstrapStyle.css"/>
</head>
<body>
<div class="container d-flex align-items-center justify-content-center vh-100">
    <div class="border border-3 rounded p-3 mt-3 form-width" style="min-width: 350px; max-width: 400px;">
        <form method="POST" action="">
            <img src="img/logo.png" alt="RetroKamera Logo" class="d-block mx-auto my-3" style="max-width:100px; max-height:100px;"/>
            <h3 class="text-center mb-3">Prijavite se u RetroKameru</h3>

            <?php if ($error): ?>
                <div class="alert alert-danger" role="alert"><?= htmlspecialchars($error)?></div>
            <?php endif; ?>

            <div class="mb-3">
                <label for="login-email" class="form-label">Email</label>
                <input type="email" name="email" id="login-email" class="form-control" required autofocus/>
            </div>

            <div class="mb-3">
                <label for="login-password" class="form-label">Lozinka</label>
                <input type="password" name="password" id="login-password" class="form-control" required/>
            </div>

            <div class="text-end">
                <button type="submit" class="btn btn-primary">Prijavite se</button>
            </div>

            <div class="text-center mt-3">
                <span>Nemate račun? <a href="register.php">Registrirajte se</a></span>
            </div>
        </form>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.6/dist/js/bootstrap.bundle.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/sweetalert2@11.22.0/dist/sweetalert2.all.min.js"></script>
</body>
</html>