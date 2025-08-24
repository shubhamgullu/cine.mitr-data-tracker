/**
 * Dashboard JavaScript Functions for Movie & Instagram Dashboard
 * Handles modal interactions, form validation, and UI enhancements
 */

// Initialize dashboard functionality when DOM is loaded
document.addEventListener('DOMContentLoaded', function() {
    initializeDashboard();
    fixFormInputVisibility();
});

function initializeDashboard() {
    // Initialize form validation
    initializeFormValidation();
    
    // Initialize tooltips if Bootstrap is available
    if (typeof bootstrap !== 'undefined') {
        var tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
        tooltipTriggerList.map(function (tooltipTriggerEl) {
            return new bootstrap.Tooltip(tooltipTriggerEl);
        });
    }
}

// Form validation for Instagram links
function initializeFormValidation() {
    const forms = document.querySelectorAll('form');
    forms.forEach(form => {
        form.addEventListener('submit', function(event) {
            if (!form.checkValidity()) {
                event.preventDefault();
                event.stopPropagation();
            }
            form.classList.add('was-validated');
        });
    });
}

// Edit link functionality
function editLink(button) {
    const id = button.getAttribute('data-id');
    const movie = button.getAttribute('data-movie');
    const category = button.getAttribute('data-category');
    const link = button.getAttribute('data-link');
    const description = button.getAttribute('data-description');

    // Populate modal form
    document.getElementById('editMovieName').value = movie || '';
    document.getElementById('editCategory').value = category || '';
    document.getElementById('editInstagramLink').value = link || '';
    document.getElementById('editDescription').value = description || '';
    
    // Set form action
    document.getElementById('editForm').action = '/dashboard/link/' + id + '/edit';
    
    // Show modal
    if (typeof bootstrap !== 'undefined') {
        const editModal = new bootstrap.Modal(document.getElementById('editModal'));
        editModal.show();
    }
}

// Track Instagram link clicks
function trackClick(linkId) {
    if (!linkId) return;
    
    fetch('/dashboard/link/' + linkId + '/click', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Accept': 'application/json'
        },
    })
    .then(response => response.text())
    .then(data => {
        console.log('Click tracked successfully:', data);
    })
    .catch(error => {
        console.error('Error tracking click:', error);
    });
}

// Show add entry tab
function showAddEntry() {
    const addEntryBtn = document.querySelector('[data-tab="add-entry"]');
    if (addEntryBtn) {
        addEntryBtn.click();
    }
}

// Tab switching functionality with smooth transitions
function initializeTabSwitching() {
    document.querySelectorAll('.tab-btn').forEach(button => {
        button.addEventListener('click', function() {
            const tabName = this.getAttribute('data-tab');
            
            // Remove active class from all buttons
            document.querySelectorAll('.tab-btn').forEach(btn => {
                btn.classList.remove('active');
            });
            
            // Add active class to clicked button
            this.classList.add('active');
            
            // Hide all tab content with fade out
            document.querySelectorAll('.tab-content').forEach(content => {
                content.classList.remove('active');
            });
            
            // Show selected tab content with fade in
            const targetTab = document.getElementById(tabName);
            if (targetTab) {
                setTimeout(() => {
                    targetTab.classList.add('active');
                    // Load data for the newly activated tab
                    loadTabData(tabName);
                }, 50);
            }
        });
    });
}

// Load data when tabs are activated
function loadTabData(tabName) {
    switch (tabName) {
        case 'movies':
            // Media catalog tab - refresh recent media records
            if (typeof window.safeRefreshRecentMedia === 'function') {
                window.safeRefreshRecentMedia();
            } else if (typeof window.refreshRecentMedia === 'function') {
                window.refreshRecentMedia();
            }
            break;
            
        case 'content-status':
            // Content catalog tab - refresh recent content records
            if (typeof window.safeRefreshRecentContent === 'function') {
                window.safeRefreshRecentContent();
            } else if (typeof window.refreshRecentContent === 'function') {
                window.refreshRecentContent();
            }
            break;
            
        case 'add-entry':
            // Upload catalog tab - refresh recent upload records
            if (typeof refreshRecentUpload === 'function') {
                refreshRecentUpload();
            }
            break;
            
        case 'states-catalog':
            // States catalog tab - could refresh states data if needed
            console.log('States catalog tab activated');
            break;
            
        case 'dashboard':
            // Dashboard tab - could refresh dashboard data if needed
            console.log('Dashboard tab activated');
            break;
    }
}

// Form submission with loading state
function handleFormSubmission() {
    const forms = document.querySelectorAll('form');
    forms.forEach(form => {
        form.addEventListener('submit', function() {
            const submitBtn = form.querySelector('button[type="submit"]');
            if (submitBtn) {
                submitBtn.classList.add('loading');
                submitBtn.disabled = true;
                
                // Re-enable button after 3 seconds as fallback
                setTimeout(() => {
                    submitBtn.classList.remove('loading');
                    submitBtn.disabled = false;
                }, 3000);
            }
        });
    });
}

// Initialize all functionality
initializeTabSwitching();
handleFormSubmission();

// Utility function to validate Instagram URLs
function isValidInstagramUrl(url) {
    const instagramRegex = /^https?:\/\/(www\.)?instagram\.com\/.+/i;
    return instagramRegex.test(url);
}

// Enhanced form validation for Instagram links
function validateInstagramForm(form) {
    const instagramInput = form.querySelector('input[type="url"]');
    if (instagramInput && !isValidInstagramUrl(instagramInput.value)) {
        alert('Please enter a valid Instagram URL');
        return false;
    }
    return true;
}

// Auto-resize textareas
document.querySelectorAll('textarea').forEach(textarea => {
    textarea.addEventListener('input', function() {
        this.style.height = 'auto';
        this.style.height = (this.scrollHeight) + 'px';
    });
});

// Smooth scroll to top when switching tabs
function scrollToTop() {
    window.scrollTo({
        top: 0,
        behavior: 'smooth'
    });
}

// Fix form input visibility for dark theme
function fixFormInputVisibility() {
    const formInputs = document.querySelectorAll('input, select, textarea');
    formInputs.forEach(input => {
        // Ensure dark theme styles are applied
        input.style.color = '#ffffff';
        input.style.backgroundColor = '#2d3139';
        input.style.borderColor = '#3a3f4b';
        
        // Add event listeners to maintain visibility
        input.addEventListener('focus', function() {
            this.style.color = '#ffffff';
            this.style.backgroundColor = '#1a1d23';
        });
        
        input.addEventListener('blur', function() {
            this.style.color = '#ffffff';
            this.style.backgroundColor = '#2d3139';
        });
        
        input.addEventListener('input', function() {
            this.style.color = '#ffffff';
        });
    });
}

// Export functions for global access
window.editLink = editLink;
window.trackClick = trackClick;
window.showAddEntry = showAddEntry;
window.scrollToTop = scrollToTop;
window.fixFormInputVisibility = fixFormInputVisibility;