/**
 * Main JavaScript for Online Shop
 * Modern ES6+ with best practices
 */

// Wait for DOM to be fully loaded
document.addEventListener('DOMContentLoaded', () => {
    initializeApp();
});

/**
 * Initialize all app functionality
 */
function initializeApp() {
    initializeSearch();
    initializeFilters();
    initializeNotifications();
    initializeLazyLoading();
    initializeAnimations();
}

/**
 * Search functionality
 */
function initializeSearch() {
    const searchForm = document.querySelector('.search-form');
    const searchInput = document.querySelector('.search-input');

    if (!searchForm || !searchInput) return;

    // Add search suggestions (debounced)
    let searchTimeout;
    searchInput.addEventListener('input', (e) => {
        clearTimeout(searchTimeout);
        searchTimeout = setTimeout(() => {
            const query = e.target.value.trim();
            if (query.length >= 2) {
                // TODO: Fetch search suggestions
                console.log('Search query:', query);
            }
        }, 300);
    });

    // Submit handler
    searchForm.addEventListener('submit', (e) => {
        const query = searchInput.value.trim();
        if (!query) {
            e.preventDefault();
            showNotification('Введите поисковый запрос', 'warning');
        }
    });
}

/**
 * Cart functionality
 */
function initializeCart() {
    const addToCartButtons = document.querySelectorAll('.btn-add-to-cart');

    addToCartButtons.forEach(button => {
        button.addEventListener('click', async (e) => {
            e.preventDefault();
            const link = e.currentTarget.getAttribute('href') || e.currentTarget.closest('a').getAttribute('href');

            // Add loading state
            const originalContent = button.innerHTML;
            button.disabled = true;
            button.innerHTML = '<span class="spinner"></span> Добавление...';

            try {
                // Send request
                const response = await fetch(link, {
                    method: 'GET',
                    headers: {
                        'X-Requested-With': 'XMLHttpRequest'
                    }
                });

                if (response.ok) {
                    // Success animation
                    button.innerHTML = '✓ Добавлено';
                    button.classList.add('btn-success');

                    // Update cart badge
                    updateCartCount();

                    // Show notification
                    showNotification('Товар добавлен в корзину', 'success');

                    // Reset button after 2 seconds
                    setTimeout(() => {
                        button.innerHTML = originalContent;
                        button.disabled = false;
                        button.classList.remove('btn-success');
                    }, 2000);

                    // Navigate to cart page if needed
                    if (response.redirected) {
                        window.location.href = response.url;
                    }
                } else {
                    throw new Error('Failed to add to cart');
                }
            } catch (error) {
                console.error('Error adding to cart:', error);
                button.innerHTML = originalContent;
                button.disabled = false;
                showNotification('Ошибка при добавлении в корзину', 'error');
            }
        });
    });
}

/**
 * Update cart count badge
 */
async function updateCartCount() {
    try {
        const response = await fetch('/cart/count');
        if (response.ok) {
            const count = await response.json();
            const badge = document.querySelector('.cart-badge');
            if (badge) {
                badge.textContent = count;
                badge.classList.add('pulse');
                setTimeout(() => badge.classList.remove('pulse'), 600);
            }
        }
    } catch (error) {
        console.error('Error updating cart count:', error);
    }
}

/**
 * Filters functionality
 */
function initializeFilters() {
    const categorySelect = document.querySelector('.category-select');

    if (categorySelect) {
        // Smooth navigation instead of location.href
        categorySelect.addEventListener('change', (e) => {
            const categoryId = e.target.value;
            if (categoryId) {
                navigateToCategory(categoryId);
            }
        });
    }

    // Price range filter
    const priceFilter = document.querySelector('.price-filter');
    if (priceFilter) {
        priceFilter.addEventListener('input', debounce((e) => {
            applyFilters();
        }, 500));
    }
}

/**
 * Navigate to category with smooth transition
 */
function navigateToCategory(categoryId) {
    // Add loading overlay
    showLoadingOverlay();

    // Navigate
    window.location.href = `/products/main/category/${categoryId}`;
}

/**
 * Notification system
 */
function initializeNotifications() {
    // Create notification container if it doesn't exist
    if (!document.querySelector('.notification-container')) {
        const container = document.createElement('div');
        container.className = 'notification-container';
        document.body.appendChild(container);
    }
}

/**
 * Show notification
 */
function showNotification(message, type = 'info') {
    const container = document.querySelector('.notification-container');
    if (!container) return;

    const notification = document.createElement('div');
    notification.className = `notification notification-${type} notification-enter`;

    const icons = {
        success: '✓',
        error: '✗',
        warning: '⚠',
        info: 'ℹ'
    };

    notification.innerHTML = `
        <div class="notification-icon">${icons[type] || icons.info}</div>
        <div class="notification-message">${message}</div>
        <button class="notification-close" aria-label="Закрыть">×</button>
    `;

    container.appendChild(notification);

    // Animate in
    setTimeout(() => notification.classList.add('notification-show'), 10);

    // Close button
    const closeBtn = notification.querySelector('.notification-close');
    closeBtn.addEventListener('click', () => removeNotification(notification));

    // Auto remove after 5 seconds
    setTimeout(() => removeNotification(notification), 5000);
}

/**
 * Remove notification
 */
function removeNotification(notification) {
    notification.classList.remove('notification-show');
    notification.classList.add('notification-exit');
    setTimeout(() => notification.remove(), 300);
}

/**
 * Lazy loading for images
 */
function initializeLazyLoading() {
    const images = document.querySelectorAll('img[data-src]');

    if ('IntersectionObserver' in window) {
        const imageObserver = new IntersectionObserver((entries, observer) => {
            entries.forEach(entry => {
                if (entry.isIntersecting) {
                    const img = entry.target;
                    img.src = img.dataset.src;
                    img.removeAttribute('data-src');
                    img.classList.add('loaded');
                    observer.unobserve(img);
                }
            });
        }, {
            rootMargin: '50px'
        });

        images.forEach(img => imageObserver.observe(img));
    } else {
        // Fallback for older browsers
        images.forEach(img => {
            img.src = img.dataset.src;
            img.removeAttribute('data-src');
        });
    }
}

/**
 * Scroll animations
 */
function initializeAnimations() {
    const animatedElements = document.querySelectorAll('.animate-on-scroll');

    if ('IntersectionObserver' in window && animatedElements.length > 0) {
        const animationObserver = new IntersectionObserver((entries) => {
            entries.forEach(entry => {
                if (entry.isIntersecting) {
                    entry.target.classList.add('animated');
                }
            });
        }, {
            threshold: 0.1
        });

        animatedElements.forEach(el => animationObserver.observe(el));
    }
}

/**
 * Show loading overlay
 */
function showLoadingOverlay() {
    const overlay = document.createElement('div');
    overlay.className = 'loading-overlay';
    overlay.innerHTML = '<div class="spinner-large"></div>';
    document.body.appendChild(overlay);
}

/**
 * Utility: Debounce function
 */
function debounce(func, wait) {
    let timeout;
    return function executedFunction(...args) {
        const later = () => {
            clearTimeout(timeout);
            func(...args);
        };
        clearTimeout(timeout);
        timeout = setTimeout(later, wait);
    };
}

/**
 * Utility: Throttle function
 */
function throttle(func, limit) {
    let inThrottle;
    return function(...args) {
        if (!inThrottle) {
            func.apply(this, args);
            inThrottle = true;
            setTimeout(() => inThrottle = false, limit);
        }
    };
}

/**
 * Form validation
 */
function validateForm(form) {
    const inputs = form.querySelectorAll('input[required], select[required], textarea[required]');
    let isValid = true;

    inputs.forEach(input => {
        if (!input.value.trim()) {
            isValid = false;
            input.classList.add('input-error');
            showFieldError(input, 'Это поле обязательно для заполнения');
        } else {
            input.classList.remove('input-error');
            hideFieldError(input);
        }
    });

    return isValid;
}

/**
 * Show field error
 */
function showFieldError(input, message) {
    let errorElement = input.parentElement.querySelector('.field-error');
    if (!errorElement) {
        errorElement = document.createElement('div');
        errorElement.className = 'field-error';
        input.parentElement.appendChild(errorElement);
    }
    errorElement.textContent = message;
}

/**
 * Hide field error
 */
function hideFieldError(input) {
    const errorElement = input.parentElement.querySelector('.field-error');
    if (errorElement) {
        errorElement.remove();
    }
}

// Export for use in other scripts
window.ShopApp = {
    showNotification,
    updateCartCount,
    validateForm
};
