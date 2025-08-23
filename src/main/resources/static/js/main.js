// Main JavaScript file for Cine Mitr

$(document).ready(function() {
    // Initialize tooltips
    var tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    var tooltipList = tooltipTriggerList.map(function (tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl);
    });

    // Auto-hide alerts after 5 seconds
    $('.alert:not(.alert-permanent)').delay(5000).fadeOut('slow');

    // Form validation
    $('.needs-validation').on('submit', function(e) {
        if (!this.checkValidity()) {
            e.preventDefault();
            e.stopPropagation();
        }
        $(this).addClass('was-validated');
    });

    // Movie search with debouncing
    let searchTimeout;
    $('#movieSearch').on('keyup', function() {
        clearTimeout(searchTimeout);
        const searchTerm = $(this).val();
        
        searchTimeout = setTimeout(function() {
            if (searchTerm.length >= 2 || searchTerm.length === 0) {
                performMovieSearch(searchTerm);
            }
        }, 300);
    });

    // Showtime selection
    $('.showtime-card').on('click', function() {
        $('.showtime-card').removeClass('selected');
        $(this).addClass('selected');
        $('#selectedShowtime').val($(this).data('showtime-id'));
        $('#bookingForm input[type="submit"]').prop('disabled', false);
    });

    // Seat selection
    $('.seat-selector input[type="number"]').on('change', function() {
        const seats = parseInt($(this).val());
        const price = parseFloat($(this).data('price'));
        const total = seats * price;
        
        $('#totalAmount').text('$' + total.toFixed(2));
        $('#bookingTotal').val(total);
    });

    // Booking confirmation
    $('#bookingForm').on('submit', function(e) {
        const seats = parseInt($('#numberOfSeats').val());
        const movie = $('#movieTitle').text();
        const showtime = $('.showtime-card.selected .showtime-time').text();
        
        const confirmMessage = `Confirm booking for ${seats} seat(s) for "${movie}" at ${showtime}?`;
        
        if (!confirm(confirmMessage)) {
            e.preventDefault();
        }
    });

    // Loading states
    $('form').on('submit', function() {
        const submitBtn = $(this).find('input[type="submit"], button[type="submit"]');
        const originalText = submitBtn.text();
        
        submitBtn.prop('disabled', true);
        submitBtn.html('<span class="spinner-border spinner-border-sm me-2"></span>Loading...');
        
        // Re-enable after 10 seconds as fallback
        setTimeout(function() {
            submitBtn.prop('disabled', false);
            submitBtn.text(originalText);
        }, 10000);
    });

    // Smooth scrolling for anchor links
    $('a[href^="#"]').on('click', function(e) {
        e.preventDefault();
        const target = $($(this).attr('href'));
        
        if (target.length) {
            $('html, body').animate({
                scrollTop: target.offset().top - 70
            }, 500);
        }
    });

    // Image lazy loading fallback
    $('img').on('error', function() {
        $(this).attr('src', '/images/default-poster.jpg');
    });

    // Genre filter buttons
    $('.genre-btn').on('click', function(e) {
        e.preventDefault();
        const genre = $(this).data('genre');
        
        $('.genre-btn').removeClass('btn-primary').addClass('btn-outline-primary');
        $(this).removeClass('btn-outline-primary').addClass('btn-primary');
        
        window.location.href = `/movies?genre=${encodeURIComponent(genre)}`;
    });

    // Responsive navbar
    $('.navbar-toggler').on('click', function() {
        setTimeout(function() {
            if ($('.navbar-collapse').hasClass('show')) {
                $('body').addClass('navbar-open');
            } else {
                $('body').removeClass('navbar-open');
            }
        }, 100);
    });

    // Fade in animation for cards
    $('.card').each(function(index) {
        $(this).delay(100 * index).queue(function(next) {
            $(this).addClass('fade-in');
            next();
        });
    });
});

// Utility functions
function performMovieSearch(searchTerm) {
    // This would typically make an AJAX call to search movies
    // For now, we'll just redirect to the search page
    if (searchTerm.trim() !== '') {
        window.location.href = `/movies?search=${encodeURIComponent(searchTerm)}`;
    }
}

function formatCurrency(amount) {
    return new Intl.NumberFormat('en-US', {
        style: 'currency',
        currency: 'USD'
    }).format(amount);
}

function showNotification(message, type = 'info') {
    const alertClass = `alert-${type}`;
    const icon = type === 'success' ? 'fas fa-check-circle' : 
                 type === 'error' ? 'fas fa-exclamation-circle' : 
                 'fas fa-info-circle';
    
    const alertHtml = `
        <div class="alert ${alertClass} alert-dismissible fade show" role="alert">
            <i class="${icon} me-2"></i>
            ${message}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    `;
    
    $('#notifications').html(alertHtml);
    $('.alert').delay(5000).fadeOut('slow');
}

// Export functions for global use
window.CineMitr = {
    showNotification,
    formatCurrency
};