//add to cart
function addToCart(name, price) {
    let cart = JSON.parse(sessionStorage.getItem('cart') || '[]');
    let found = cart.find(item => item.name === name);
    if (found != null) {
        found.qty += 1;
    } else {
        cart.push({ name, price, qty: 1 });
    }

    sessionStorage.setItem('cart', JSON.stringify(cart));
    updateCartCount();

    Swal.fire({
        icon: 'success',
        title: 'Dodano u košaricu!',
        showConfirmButton: false,
        timer: 1000
    }).then(() => {
        window.location.href = 'cart.html';
    });
}


function renderCart() {
    let cart = JSON.parse(sessionStorage.getItem('cart') || '[]');
    let itemsDiv = document.getElementById('cart-items');
    let totalSpan = document.getElementById('cart-total');
    let totalCartPrice = 0;

    if (!itemsDiv || !totalSpan) {
        return;
    }

    if (cart.length === 0) {
        itemsDiv.innerHTML = '<p class="cart-item-name" style="color:white;">Košarica je prazna.</p>';
        totalSpan.textContent = '0.00€';
        return;
    }

    let html = '';

    cart.forEach((object, index) => {
        let objectTotalPrice = object.price * object.qty;

        totalCartPrice += objectTotalPrice;

        html += `
            <div class="cart-item">
                <div>
                    <span class="cart-item-name">${object.name}</span>
                    <span class="cart-item-qty"> ${object.qty} kom</span>
                </div>
                <div class="cart-item-price">${objectTotalPrice.toFixed(2)}€</div>
                <button class="btn-buy cart-remove" onclick="removeFromCart(${index})">Ukloni</button>
            </div>
        `;
    });

    itemsDiv.innerHTML = html;
    totalSpan.textContent = totalCartPrice.toFixed(2) + '€';
}


function removeFromCart(index) {
    let cart = JSON.parse(sessionStorage.getItem('cart') || '[]');

    if (cart[index].qty > 1) {
        cart[index].qty -= 1;
    } else {
        cart.splice(index, 1);
    }

    sessionStorage.setItem('cart', JSON.stringify(cart));
    updateCartCount();
    renderCart();

    Swal.fire({
        icon: 'error',
        title: 'Uklonjeno iz košarice!',
        showConfirmButton: false,
        timer: 1000
    });
}

if (document.getElementById('cart-items')) {
    renderCart();
    updateCartCount();
}


//filters for products
function filterProducts() {
    let categoryFilters = Array.from(document.querySelectorAll('input[name="category"]:checked')).map(checkbox => checkbox.value);
    let brandFilters = Array.from(document.querySelectorAll('input[name="brand"]:checked')).map(checkbox => checkbox.value);

    let cards = document.querySelectorAll('.card');

    cards.forEach(card => {
        let lensTypeDiv = card.querySelector('.lens-type');

        let categoryDiv;
        if (lensTypeDiv != null) {
            categoryDiv = lensTypeDiv;
        } else {
            categoryDiv = card.querySelector('.card-type');
        }

        let categoryText;
        if (categoryDiv != null) {
            categoryText = categoryDiv.textContent.trim();
        } else {
            categoryText = '';
        }

        let cardTitle;
        if (card.querySelector('.card-title') != null) {
            cardTitle = card.querySelector('.card-title').textContent.trim();
        } else {
            cardTitle = '';
        }

        let isLensPage;
        if (lensTypeDiv != null) {
            isLensPage = true;
        } else {
            isLensPage = false;
        }

        let categoryMatch;
        if (categoryFilters.length === 0) {
            categoryMatch = true;
        } else if (isLensPage  === true) {
            categoryMatch = categoryFilters.every(filter => categoryText.includes(filter));
        } else {
            categoryMatch = categoryFilters.some(filter => categoryText === filter);
        }

        let brandMatch = brandFilters.length === 0 || brandFilters.some(brand => cardTitle.toLowerCase().includes(brand.toLowerCase()));

        if (categoryMatch && brandMatch) {
            card.style.display = '';
        } else {
            card.style.display = 'none';
        }
    });
}


//graph
function bestsellerChart() {
    const canvas = document.getElementById('bestsellerChart');

    if (canvas != null) {
        const ctx = canvas.getContext('2d');
        const bestsellerChart = new Chart(ctx, {
            type: 'bar',
            data: {
                labels: ['MINOLTA SRT 101', 'MINOLTA XE1', 'Olympus XA2', 'Polaroid Supercolor 1000', 'Canon AE1'],
                datasets: [{
                    label: ' Broj prodanih komada',
                    data: [150, 78, 62, 49, 6],
                    backgroundColor: [
                        'rgba(52, 102, 204, 0.7)',
                        'rgba(38, 166, 91, 0.7)',
                        'rgba(255, 152, 0, 0.7)',
                        'rgba(156, 39, 176, 0.7)',
                        'rgba(229, 57, 53, 0.7)'
                    ],
                    borderColor: [
                        'rgba(52, 102, 204, 1)',
                        'rgba(38, 166, 91, 1)',
                        'rgba(255, 152, 0, 1)',
                        'rgba(156, 39, 176, 1)',
                        'rgba(229, 57, 53, 1)'
                    ],
                    borderWidth: 3
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    title: {
                        display: true,
                        text: 'Najprodavaniji fotoaparati na stranici',
                        font: {
                            size: 18
                        },
                        color: 'white',
                    },
                    legend: { display: false }
                },
                scales: {
                    x: {
                        ticks: {
                            color: 'white'
                        }
                    },
                    y: {
                        beginAtZero: true,
                        ticks: {
                            color: 'white'
                        }
                    }
                }
            }
        });
    }
}


document.addEventListener('DOMContentLoaded', function() {
    updateCartCount();
    bestsellerChart();
});


//pay button
const payBtn = document.getElementById('pay-btn');

if (payBtn != null) {
    payBtn.addEventListener('click', function(e) {
        e.preventDefault();
        Swal.fire({
            icon: 'question',
            title: 'Želite li platiti?',
            showConfirmButton: true,
            confirmButtonText: 'Da',
            showCancelButton: true,
            cancelButtonText: 'Ne',
            confirmButtonColor: '#27ae60',
            cancelButtonColor: '#e74c3c'
        }).then((result) => {
            if (result.isConfirmed) {
                Swal.fire({
                    icon: 'success',
                    title: 'Plaćanje je bilo uspješno!',
                    showConfirmButton: true,
                    confirmButtonColor: '#27ae60',
                }).then(() => {
                    sessionStorage.removeItem('cart');
                    updateCartCount();
                    window.location.href = 'index.html';
                });
            } else if (result.dismiss === Swal.DismissReason.cancel) {
                Swal.fire({
                    icon: 'error',
                    title: 'Plaćanje je bilo otkazano',
                    showConfirmButton: false,
                    timer: 1000
                });
            }
        });
    });
}

//funkcija za prikaz broja stavki u košarici u headeru
function updateCartCount() {
    let cart = JSON.parse(sessionStorage.getItem('cart') || '[]');
    let totalQty = cart.reduce((sum, item) => sum + item.qty, 0);
    let countElem = document.getElementById('cart-count');
    if (countElem) {
        countElem.textContent = totalQty;
    }
}
