// API Configuration
const API_BASE_URL = 'http://localhost:8080/api';

// Global variables
let currentTab = 'media';
let editingIndex = -1;

// API endpoints
const endpoints = {
    media: `${API_BASE_URL}/media`,
    content: `${API_BASE_URL}/content`,
    upload: `${API_BASE_URL}/upload`,
    states: `${API_BASE_URL}/states`
};

// Initialize the application
document.addEventListener('DOMContentLoaded', function() {
    loadData('media');
    updateDashboard();
});

// Tab switching
function switchTab(tabName) {
    // Hide all tab contents
    document.querySelectorAll('.tab-content').forEach(content => {
        content.classList.remove('active');
    });

    // Remove active class from all tab buttons
    document.querySelectorAll('.tab-btn').forEach(btn => {
        btn.classList.remove('border-blue-500', 'text-blue-600');
        btn.classList.add('border-transparent', 'text-gray-500');
    });

    // Show selected tab content
    document.getElementById(tabName).classList.add('active');

    // Add active class to selected tab button
    const activeBtn = document.querySelector(`[data-tab="${tabName}"]`);
    activeBtn.classList.remove('border-transparent', 'text-gray-500');
    activeBtn.classList.add('border-blue-500', 'text-blue-600');

    currentTab = tabName;
    
    // Load data for the current tab
    if (tabName !== 'dashboard') {
        loadData(tabName);
    } else {
        updateDashboard();
    }
}

// Load data from API
async function loadData(type) {
    try {
        showLoadingMessage(type);
        const response = await fetch(endpoints[type]);
        
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        
        const data = await response.json();
        renderTable(type, data);
    } catch (error) {
        console.error(`Error loading ${type} data:`, error);
        showErrorMessage(type, `Failed to load ${type} data: ${error.message}`);
    }
}

// Show loading message
function showLoadingMessage(type) {
    const tbody = document.getElementById(`${type}-table`);
    tbody.innerHTML = '<tr><td colspan="6" class="px-6 py-4 text-center text-gray-500">Loading...</td></tr>';
}

// Show error message
function showErrorMessage(type, message) {
    const tbody = document.getElementById(`${type}-table`);
    tbody.innerHTML = `<tr><td colspan="6" class="px-6 py-4 text-center text-red-500">${message}</td></tr>`;
}

// Refresh data
function refreshData(type) {
    loadData(type);
}

// Render table with data
function renderTable(type, data) {
    const tbody = document.getElementById(`${type}-table`);
    tbody.innerHTML = '';

    if (!data || data.length === 0) {
        tbody.innerHTML = `<tr><td colspan="6" class="px-6 py-4 text-center text-gray-500">No ${type} records found</td></tr>`;
        return;
    }

    data.forEach((item, index) => {
        const row = document.createElement('tr');
        row.innerHTML = generateTableRow(type, item, index);
        tbody.appendChild(row);
    });
}

// Generate table row HTML based on type
function generateTableRow(type, item, index) {
    let cells = '';
    
    switch(type) {
        case 'media':
            cells = `
                <td class="px-6 py-4 text-sm text-gray-900">${item.media_name || ''}</td>
                <td class="px-6 py-4 text-sm text-gray-500">${item.media_type || ''}</td>
                <td class="px-6 py-4 text-sm text-gray-500">${item.language || ''}</td>
                <td class="px-6 py-4 text-sm text-gray-500">${item.main_genres || ''}</td>
                <td class="px-6 py-4 text-sm ${item.is_downloaded === 'Yes' ? 'text-green-600' : 'text-red-600'}">${item.is_downloaded || 'No'}</td>
            `;
            break;
        case 'content':
            cells = `
                <td class="px-6 py-4 text-sm text-blue-600 truncate max-w-xs"><a href="${item.link}" target="_blank" title="${item.link}">${item.link || ''}</a></td>
                <td class="px-6 py-4 text-sm text-gray-500">${item.media_type || ''}</td>
                <td class="px-6 py-4 text-sm ${getStatusColor(item.status)}">${item.status || ''}</td>
                <td class="px-6 py-4 text-sm ${getPriorityColor(item.priority)}">${item.priority || ''}</td>
                <td class="px-6 py-4 text-sm ${getStatusColor(item.local_status)}">${item.local_status || ''}</td>
            `;
            break;
        case 'upload':
            cells = `
                <td class="px-6 py-4 text-sm text-blue-600 truncate max-w-xs"><a href="${item.source_link}" target="_blank" title="${item.source_link}">${item.source_link || ''}</a></td>
                <td class="px-6 py-4 text-sm text-gray-500 truncate max-w-xs" title="${item.source_data}">${item.source_data || ''}</td>
                <td class="px-6 py-4 text-sm ${getStatusColor(item.status)}">${item.status || ''}</td>
                <td class="px-6 py-4 text-sm text-gray-500 truncate max-w-xs" title="${item.media_data}">${item.media_data || ''}</td>
            `;
            break;
        case 'states':
            cells = `
                <td class="px-6 py-4 text-sm text-gray-900">${item.date || ''}</td>
                <td class="px-6 py-4 text-sm text-gray-500">${item.total_views ? parseInt(item.total_views).toLocaleString() : ''}</td>
                <td class="px-6 py-4 text-sm text-gray-500">${item.subscribers ? parseInt(item.subscribers).toLocaleString() : ''}</td>
                <td class="px-6 py-4 text-sm text-gray-500">${item.interaction ? parseInt(item.interaction).toLocaleString() : ''}</td>
                <td class="px-6 py-4 text-sm text-gray-500">${item.page || ''}</td>
            `;
            break;
    }

    return `
        ${cells}
        <td class="px-6 py-4 text-sm space-x-2">
            <button onclick="editItem('${type}', ${item.id})" class="text-blue-600 hover:text-blue-800">Edit</button>
            <button onclick="deleteItem('${type}', ${item.id})" class="text-red-600 hover:text-red-800">Delete</button>
        </td>
    `;
}

// Get color classes for status
function getStatusColor(status) {
    if (!status) return 'text-gray-500';
    
    const lowerStatus = status.toLowerCase();
    if (lowerStatus.includes('downloaded') || lowerStatus.includes('completed')) {
        return 'text-green-600';
    } else if (lowerStatus.includes('error') || lowerStatus.includes('blocked')) {
        return 'text-red-600';
    } else if (lowerStatus.includes('progress') || lowerStatus.includes('new')) {
        return 'text-yellow-600';
    }
    return 'text-gray-500';
}

// Get color classes for priority
function getPriorityColor(priority) {
    if (!priority) return 'text-gray-500';
    
    const lowerPriority = priority.toLowerCase();
    if (lowerPriority === 'high') {
        return 'text-red-600';
    } else if (lowerPriority === 'medium') {
        return 'text-yellow-600';
    } else if (lowerPriority === 'low') {
        return 'text-green-600';
    }
    return 'text-gray-500';
}

// Modal functions
function openModal(type, itemId = null) {
    editingIndex = itemId;
    const modal = document.getElementById(`${type}-modal`);
    const form = document.getElementById(`${type}-form`);
    const title = document.getElementById(`${type}-modal-title`);

    if (itemId !== null) {
        // Edit mode
        title.textContent = `Edit ${type.charAt(0).toUpperCase() + type.slice(1)}`;
        loadItemForEdit(type, itemId);
    } else {
        // Add mode
        title.textContent = `Add ${type.charAt(0).toUpperCase() + type.slice(1)}`;
        form.reset();
    }

    modal.classList.add('active');
}

// Load item data for editing
async function loadItemForEdit(type, itemId) {
    try {
        const response = await fetch(`${endpoints[type]}/${itemId}`);
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        
        const item = await response.json();
        const form = document.getElementById(`${type}-form`);
        
        // Populate form fields
        Object.keys(item).forEach(key => {
            const field = form.querySelector(`[name="${key}"]`);
            if (field) {
                field.value = item[key] || '';
            }
        });
    } catch (error) {
        console.error(`Error loading ${type} item:`, error);
        alert(`Failed to load ${type} data for editing`);
    }
}

function closeModal(type) {
    document.getElementById(`${type}-modal`).classList.remove('active');
    editingIndex = -1;
}

// Form submission
async function submitForm(event, type) {
    event.preventDefault();
    const form = event.target;
    const formData = new FormData(form);
    const item = {};

    for (let [key, value] of formData.entries()) {
        item[key] = value;
    }

    try {
        let response;
        if (editingIndex !== -1) {
            // Update existing item
            response = await fetch(`${endpoints[type]}/${editingIndex}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(item)
            });
        } else {
            // Create new item
            response = await fetch(endpoints[type], {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(item)
            });
        }

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        closeModal(type);
        loadData(type);
        updateDashboard();
        
        const action = editingIndex !== -1 ? 'updated' : 'created';
        showSuccessMessage(`${type.charAt(0).toUpperCase() + type.slice(1)} ${action} successfully!`);
    } catch (error) {
        console.error(`Error saving ${type}:`, error);
        alert(`Failed to save ${type}: ${error.message}`);
    }
}

// Edit item
function editItem(type, itemId) {
    openModal(type, itemId);
}

// Delete item
async function deleteItem(type, itemId) {
    if (!confirm('Are you sure you want to delete this item?')) {
        return;
    }

    try {
        const response = await fetch(`${endpoints[type]}/${itemId}`, {
            method: 'DELETE'
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        loadData(type);
        updateDashboard();
        showSuccessMessage(`${type.charAt(0).toUpperCase() + type.slice(1)} deleted successfully!`);
    } catch (error) {
        console.error(`Error deleting ${type}:`, error);
        alert(`Failed to delete ${type}: ${error.message}`);
    }
}

// Update dashboard counts
async function updateDashboard() {
    const types = ['media', 'content', 'upload', 'states'];
    
    for (const type of types) {
        try {
            const response = await fetch(`${endpoints[type]}/count`);
            if (response.ok) {
                const count = await response.json();
                const countElement = document.getElementById(`${type}-count`);
                if (countElement) {
                    countElement.textContent = count;
                }
            }
        } catch (error) {
            console.error(`Error loading ${type} count:`, error);
            const countElement = document.getElementById(`${type}-count`);
            if (countElement) {
                countElement.textContent = 'Error';
            }
        }
    }
}

// Show success message
function showSuccessMessage(message) {
    // Create a simple toast notification
    const toast = document.createElement('div');
    toast.className = 'fixed top-4 right-4 bg-green-500 text-white px-6 py-3 rounded-lg shadow-lg z-50';
    toast.textContent = message;
    document.body.appendChild(toast);
    
    setTimeout(() => {
        document.body.removeChild(toast);
    }, 3000);
}