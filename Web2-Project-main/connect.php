<?php
$servername = "localhost";
$username = "root";
$password = "";
$dbname = "web2";

$conn = new mysqli($servername, $username, $password, $dbname);
if ($conn->connect_error) {
    die("Greška: " . $conn->connect_error);
}