// API Configuration
const API_BASE_URL = 'http://localhost:8081/api';

// Global variables
let currentTab = 'media';
let editingIndex = -1;
let currentBulkType = '';
let selectedFile = null;

// Pagination variables
let paginationData = {
    media: { currentPage: 1, pageSize: 25, totalItems: 0, allData: [] },
    content: { currentPage: 1, pageSize: 25, totalItems: 0, allData: [] },
    upload: { currentPage: 1, pageSize: 25, totalItems: 0, allData: [] },
    states: { currentPage: 1, pageSize: 25, totalItems: 0, allData: [] },
    errors: { currentPage: 1, pageSize: 25, totalItems: 0, allData: [] }
};

// API endpoints
const endpoints = {
    media: `${API_BASE_URL}/media`,
    content: `${API_BASE_URL}/content`,
    upload: `${API_BASE_URL}/upload`,
    states: `${API_BASE_URL}/states`,
    errors: `${API_BASE_URL}/bulk-errors`
};

// Initialize the application
document.addEventListener('DOMContentLoaded', function() {
    loadData('media');
    updateDashboard();
    initializeCharacterCounters();
    
    // Set up event listeners for search functionality
    const mediaSearch = document.getElementById('mediaSearch');
    if (mediaSearch) {
        // Add debounce to search input to improve performance
        let searchTimeout;
        mediaSearch.addEventListener('input', function() {
            clearTimeout(searchTimeout);
            searchTimeout = setTimeout(filterMediaTable, 300);
        });
    }
    
    const contentSearch = document.getElementById('contentSearch');
    if (contentSearch) {
        // Add debounce to search input to improve performance
        let contentSearchTimeout;
        contentSearch.addEventListener('input', function() {
            clearTimeout(contentSearchTimeout);
            contentSearchTimeout = setTimeout(filterContentTable, 300);
        });
    }
    
    const uploadSearch = document.getElementById('uploadSearch');
    if (uploadSearch) {
        // Add debounce to search input to improve performance
        let uploadSearchTimeout;
        uploadSearch.addEventListener('input', function() {
            clearTimeout(uploadSearchTimeout);
            uploadSearchTimeout = setTimeout(filterUploadTable, 300);
        });
    }
});

// Initialize character counters for metadata fields
function initializeCharacterCounters() {
    const metadataTextarea = document.querySelector('textarea[name="metadata"]');
    const metadataCounter = document.getElementById('metadata-char-count');
    
    if (metadataTextarea && metadataCounter) {
        // Set initial count
        updateCharacterCount(metadataTextarea, metadataCounter, 9000);
        
        // Add event listeners for real-time updates
        metadataTextarea.addEventListener('input', function() {
            updateCharacterCount(this, metadataCounter, 9000);
        });
        
        metadataTextarea.addEventListener('keyup', function() {
            updateCharacterCount(this, metadataCounter, 9000);
        });
    }
}

// Update character count display
function updateCharacterCount(textarea, counterElement, maxLength) {
    const currentLength = textarea.value.length;
    const remaining = maxLength - currentLength;
    
    counterElement.textContent = `${currentLength}/${maxLength}`;
    
    // Update color based on remaining characters
    if (remaining < 100) {
        counterElement.className = 'text-sm text-red-500 mt-1';
    } else if (remaining < 500) {
        counterElement.className = 'text-sm text-yellow-500 mt-1';
    } else {
        counterElement.className = 'text-sm text-gray-500 mt-1';
    }
}

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
        
        // Update pagination data and render paginated table
        updatePaginationData(type, data);
        renderPaginatedTable(type);
        updatePaginationControls(type);
    } catch (error) {
        console.error(`Error loading ${type} data:`, error);
        showErrorMessage(type, `Failed to load ${type} data: ${error.message}`);
    }
}

// Show loading message
function showLoadingMessage(type) {
    const tbody = document.getElementById(`${type}-table`);
    let colspan = '6';
    if (type === 'content') {
        colspan = '8';
    } else if (type === 'errors') {
        colspan = '8';
    } else if (type === 'states') {
        colspan = '6';
    }
    tbody.innerHTML = `<tr><td colspan="${colspan}" class="px-6 py-4 text-center text-gray-500">Loading...</td></tr>`;
}

// Show error message
function showErrorMessage(type, message) {
    const tbody = document.getElementById(`${type}-table`);
    let colspan = '6';
    if (type === 'content') {
        colspan = '8';
    } else if (type === 'errors') {
        colspan = '8';
    } else if (type === 'states') {
        colspan = '6';
    }
    tbody.innerHTML = `<tr><td colspan="${colspan}" class="px-6 py-4 text-center text-red-500">${message}</td></tr>`;
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
        let colspan = '6';
        if (type === 'content') {
            colspan = '8';
        } else if (type === 'errors') {
            colspan = '8';
        } else if (type === 'states') {
            colspan = '6';
        }
        tbody.innerHTML = `<tr><td colspan="${colspan}" class="px-6 py-4 text-center text-gray-500">No ${type} records found</td></tr>`;
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
            // Handle multiple media names as a list
            let mediaDisplay = '';
            if (item.media_names_list && Array.isArray(item.media_names_list) && item.media_names_list.length > 0) {
                if (item.media_names_list.length === 1) {
                    mediaDisplay = item.media_names_list[0];
                } else {
                    const displayItems = item.media_names_list.slice(0, 2);
                    const remaining = item.media_names_list.length - 2;
                    mediaDisplay = displayItems.join(', ');
                    if (remaining > 0) {
                        mediaDisplay += ` +${remaining} more`;
                    }
                }
            } else {
                mediaDisplay = item.media_name || '';
            }
            
            cells = `
                <td class="px-6 py-4 text-sm text-blue-600 truncate max-w-xs"><a href="${item.link}" target="_blank" title="${item.link}">${item.link || ''}</a></td>
                <td class="px-6 py-4 text-sm text-gray-500">${item.content_type || ''}</td>
                <td class="px-6 py-4 text-sm text-gray-500">${item.media_type || ''}</td>
                <td class="px-6 py-4 text-sm text-gray-500" title="${item.media_names_list ? item.media_names_list.join(', ') : (item.media_name || '')}">${mediaDisplay}</td>
                <td class="px-6 py-4 text-sm ${getStatusColor(item.status)}">${item.status || ''}</td>
                <td class="px-6 py-4 text-sm ${getPriorityColor(item.priority)}">${item.priority || ''}</td>
                <td class="px-6 py-4 text-sm ${getStatusColor(item.local_status)}">${item.local_status || ''}</td>
            `;
            break;
        case 'upload':
            // Handle multiple media names as a list like content
            let uploadMediaDisplay = '';
            if (item.media_names_list && Array.isArray(item.media_names_list) && item.media_names_list.length > 0) {
                if (item.media_names_list.length === 1) {
                    uploadMediaDisplay = item.media_names_list[0];
                } else {
                    const displayItems = item.media_names_list.slice(0, 2);
                    const remaining = item.media_names_list.length - 2;
                    uploadMediaDisplay = displayItems.join(', ');
                    if (remaining > 0) {
                        uploadMediaDisplay += ` +${remaining} more`;
                    }
                }
            } else {
                uploadMediaDisplay = item.media_name || 'No media specified';
            }
            
            cells = `
                <td class="px-6 py-4 text-sm text-blue-600 truncate max-w-xs"><a href="${item.source_link}" target="_blank" title="${item.source_link}">${item.source_link || ''}</a></td>
                <td class="px-6 py-4 text-sm text-gray-500" title="${item.media_names_list ? item.media_names_list.join(', ') : (item.media_name || 'No media specified')}">${uploadMediaDisplay}</td>
                <td class="px-6 py-4 text-sm text-gray-500 truncate max-w-xs" title="${item.source_data}">${item.source_data || 'No data'}</td>
                <td class="px-6 py-4 text-sm ${getStatusColor(item.status)}">${item.status || ''}</td>
                <td class="px-6 py-4 text-sm text-gray-500">${item.media_type || 'N/A'}</td>
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
        case 'errors':
            const formattedDate = item.created_at ? new Date(item.created_at).toLocaleString() : '';
            const statusBadge = item.is_resolved ? 
                '<span class="px-2 py-1 text-xs font-semibold bg-green-100 text-green-800 rounded-full">Resolved</span>' : 
                '<span class="px-2 py-1 text-xs font-semibold bg-red-100 text-red-800 rounded-full">Unresolved</span>';
            
            cells = `
                <td class="px-6 py-4 text-sm text-gray-900">
                    <span class="px-2 py-1 text-xs font-semibold bg-blue-100 text-blue-800 rounded-full">${item.upload_type || ''}</span>
                </td>
                <td class="px-6 py-4 text-sm text-gray-500 font-mono">${item.batch_id || ''}</td>
                <td class="px-6 py-4 text-sm text-gray-500">${item.row_number || ''}</td>
                <td class="px-6 py-4 text-sm text-gray-500">
                    <span class="px-2 py-1 text-xs font-semibold bg-yellow-100 text-yellow-800 rounded-full">${item.error_type || ''}</span>
                </td>
                <td class="px-6 py-4 text-sm text-gray-500 max-w-xs truncate" title="${item.error_message || ''}">${item.error_message || ''}</td>
                <td class="px-6 py-4 text-sm">${statusBadge}</td>
                <td class="px-6 py-4 text-sm text-gray-500">${formattedDate}</td>
            `;
            break;
    }

    let actions = '';
    if (type === 'errors') {
        if (item.is_resolved) {
            actions = `
                <td class="px-6 py-4 text-sm space-x-2">
                    <button onclick="viewErrorDetails(${item.id})" class="text-blue-600 hover:text-blue-800">View</button>
                    <button onclick="deleteError(${item.id})" class="text-red-600 hover:text-red-800">Delete</button>
                </td>
            `;
        } else {
            actions = `
                <td class="px-6 py-4 text-sm space-x-2">
                    <button onclick="viewErrorDetails(${item.id})" class="text-blue-600 hover:text-blue-800">View</button>
                    <button onclick="resolveError(${item.id})" class="text-green-600 hover:text-green-800">Resolve</button>
                    <button onclick="deleteError(${item.id})" class="text-red-600 hover:text-red-800">Delete</button>
                </td>
            `;
        }
    } else {
        actions = `
            <td class="px-6 py-4 text-sm space-x-2">
                <button onclick="editItem('${type}', ${item.id})" class="text-blue-600 hover:text-blue-800">Edit</button>
                <button onclick="deleteItem('${type}', ${item.id})" class="text-red-600 hover:text-red-800">Delete</button>
            </td>
        `;
    }
    
    return `
        ${cells}
        ${actions}
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
    editingIndex = itemId !== null ? itemId : -1;
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
        showErrorMessage(`Failed to load ${type} data for editing`);
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
        if (editingIndex !== -1 && editingIndex !== null) {
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
            // Try to get error message from response
            try {
                const errorData = await response.json();
                if (errorData.error) {
                    throw new Error(errorData.error);
                }
            } catch (jsonError) {
                // If response isn't JSON, fall back to status error
            }
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        closeModal(type);
        loadData(type);
        updateDashboard();
        
        const action = (editingIndex !== -1 && editingIndex !== null) ? 'updated' : 'created';
        showSuccessMessage(`${type.charAt(0).toUpperCase() + type.slice(1)} ${action} successfully!`);
    } catch (error) {
        console.error(`Error saving ${type}:`, error);
        showErrorMessage(`Failed to save ${type}: ${error.message}`);
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
        showErrorMessage(`Failed to delete ${type}: ${error.message}`);
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
        if (document.body.contains(toast)) {
            document.body.removeChild(toast);
        }
    }, 3000);
}

function showErrorMessage(message) {
    // Create a simple error toast notification
    const toast = document.createElement('div');
    toast.className = 'fixed top-4 right-4 bg-red-500 text-white px-6 py-3 rounded-lg shadow-lg z-50 max-w-md';
    toast.innerHTML = `
        <div class="flex items-center">
            <svg class="w-5 h-5 mr-2" fill="currentColor" viewBox="0 0 20 20">
                <path fill-rule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7 4a1 1 0 11-2 0 1 1 0 012 0zm-1-9a1 1 0 00-1 1v4a1 1 0 102 0V6a1 1 0 00-1-1z" clip-rule="evenodd"></path>
            </svg>
            <span class="flex-1">${message}</span>
            <button onclick="this.parentElement.parentElement.remove()" class="ml-2 text-red-200 hover:text-white">
                <svg class="w-4 h-4" fill="currentColor" viewBox="0 0 20 20">
                    <path fill-rule="evenodd" d="M4.293 4.293a1 1 0 011.414 0L10 8.586l4.293-4.293a1 1 0 111.414 1.414L11.414 10l4.293 4.293a1 1 0 01-1.414 1.414L10 11.414l-4.293 4.293a1 1 0 01-1.414-1.414L8.586 10 4.293 5.707a1 1 0 010-1.414z" clip-rule="evenodd"></path>
                </svg>
            </button>
        </div>
    `;
    document.body.appendChild(toast);
    
    // Auto-remove after 5 seconds (longer than success message)
    setTimeout(() => {
        if (document.body.contains(toast)) {
            document.body.removeChild(toast);
        }
    }, 5000);
}

// CSV format templates for each tab
const csvFormats = {
    media: 'media_name,media_type,language,main_genres,sub_genres,is_downloaded,download_path,available_on\n"Movie Title","Movie","English","Action","Superhero","Yes","/path/to/file","Netflix"',
    content: 'link,content_type,content_metadata,media_type,media_name,status,priority,local_status,local_file_path\n"https://example.com/video","Video","High-quality movie content with excellent metadata","Movie","The Matrix","new","high","downloaded","/path/file"',
    upload: 'source_link,source_data,status,media_data\n"https://source.com","Metadata info","completed","HD Video, 2GB"',
    states: 'date,total_views,subscribers,interaction,content,page\n"2024-01-15","15420","1250","850","Content desc","cine.mitr"'
};

// CSV format templates are now defined in the csvFormats object above

// Bulk upload functions
function triggerBulkUpload(type) {
    currentBulkType = type;
    document.getElementById('bulk-upload-title').textContent = `Bulk Upload - ${type.charAt(0).toUpperCase() + type.slice(1)}`;
    document.getElementById('csv-format').textContent = csvFormats[type];
    document.getElementById('bulk-upload-modal').classList.add('active');
    resetBulkUpload();
}

function closeBulkUpload() {
    document.getElementById('bulk-upload-modal').classList.remove('active');
    resetBulkUpload();
}

function resetBulkUpload() {
    selectedFile = null;
    document.getElementById('file-info').classList.add('hidden');
    document.getElementById('upload-progress').classList.add('hidden');
    document.getElementById('process-btn').disabled = true;
    document.getElementById('bulk-file-input').value = '';
    document.getElementById('progress-bar').style.width = '0%';
    document.getElementById('progress-text').textContent = '0%';
}

function handleFileSelect(event) {
    const file = event.target.files[0];
    if (!file) return;

    selectedFile = file;

    // Show file info
    document.getElementById('file-name').textContent = file.name;
    document.getElementById('file-size').textContent = `(${(file.size / 1024 / 1024).toFixed(2)} MB)`;
    document.getElementById('file-info').classList.remove('hidden');
    document.getElementById('process-btn').disabled = false;
}

function processBulkUpload() {
    if (!selectedFile) return;

    document.getElementById('upload-progress').classList.remove('hidden');
    document.getElementById('process-btn').disabled = true;

    // Simulate file processing
    let progress = 0;
    const interval = setInterval(() => {
        progress += Math.random() * 20;
        if (progress >= 100) {
            progress = 100;
            clearInterval(interval);

            // Process the file
            processCSVFile(selectedFile);
        }

        document.getElementById('progress-bar').style.width = progress + '%';
        document.getElementById('progress-text').textContent = Math.round(progress) + '%';
    }, 200);
}

async function processCSVFile(file) {
    const batchId = await generateBatchId();
    
    try {
        const text = await readFileAsText(file);
        const rows = parseCSV(text);

        if (rows.length > 0) {
            const headers = rows[0];
            const dataRows = rows.slice(1);

            // Convert rows to objects
            const newItems = dataRows.map((row, index) => {
                const item = {};
                headers.forEach((header, headerIndex) => {
                    item[header.trim()] = row[headerIndex] ? row[headerIndex].trim() : '';
                });
                item._rowNumber = index + 2; // +2 because index starts at 0 and we skip header row
                item._rawData = row.join(',');
                return item;
            });

            // Send each item to the backend API with error tracking
            let successCount = 0;
            let errorCount = 0;
            const errorDetails = [];

            for (let i = 0; i < newItems.length; i++) {
                const item = newItems[i];
                const rowNumber = item._rowNumber;
                const rawData = item._rawData;
                
                // Remove metadata before sending to API
                delete item._rowNumber;
                delete item._rawData;

                try {
                    const response = await fetch(endpoints[currentBulkType], {
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/json',
                        },
                        body: JSON.stringify(item)
                    });

                    if (response.ok) {
                        successCount++;
                    } else {
                        errorCount++;
                        const errorText = await response.text();
                        
                        // Log error to backend error tracking system
                        logBulkUploadError(currentBulkType, batchId, rowNumber, rawData, errorText);
                        
                        errorDetails.push({
                            row: rowNumber,
                            error: errorText,
                            data: rawData
                        });
                        
                        console.error(`Failed to create ${currentBulkType} at row ${rowNumber}:`, errorText);
                    }
                } catch (error) {
                    errorCount++;
                    const errorMessage = error.message || 'Unknown error occurred';
                    
                    // Log error to backend error tracking system
                    logBulkUploadError(currentBulkType, batchId, rowNumber, rawData, errorMessage);
                    
                    errorDetails.push({
                        row: rowNumber,
                        error: errorMessage,
                        data: rawData
                    });
                    
                    console.error(`Error creating ${currentBulkType} at row ${rowNumber}:`, error);
                }
            }

            // Refresh the current data and update dashboard
            loadData(currentBulkType);
            updateDashboard();

            setTimeout(() => {
                closeBulkUpload();
                if (errorCount === 0) {
                    showSuccessMessage(`Successfully imported ${successCount} records!`);
                } else {
                    const message = `Imported ${successCount} records. ${errorCount} failed. <a href="javascript:void(0)" onclick="showBulkUploadErrors('${batchId}')" class="underline text-blue-600 hover:text-blue-800">View Details</a>`;
                    showSuccessMessage(message);
                }
            }, 500);
        } else {
            showErrorMessage('No data found in the file.');
            document.getElementById('process-btn').disabled = false;
        }
    } catch (error) {
        console.error('Error processing file:', error);
        logBulkUploadError(currentBulkType, batchId, 1, 'File processing error', error.message);
        showErrorMessage('Error processing file: ' + error.message);
        document.getElementById('process-btn').disabled = false;
    }
}

// Generate batch ID for error tracking
async function generateBatchId() {
    return 'BATCH_' + Date.now().toString(36) + Math.random().toString(36).substr(2, 5);
}

// Log bulk upload error to backend
async function logBulkUploadError(uploadType, batchId, rowNumber, rawData, errorMessage) {
    try {
        const response = await fetch('/api/bulk-errors', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                upload_type: uploadType.toUpperCase(),
                batch_id: batchId,
                row_number: rowNumber,
                raw_data: rawData,
                error_type: 'PROCESSING_ERROR',
                error_message: errorMessage,
                field_name: null,
                attempted_value: null,
                suggestions: null,
                is_resolved: false
            })
        });
        
        if (!response.ok) {
            console.error('Failed to log bulk upload error:', response.status, response.statusText);
        }
    } catch (error) {
        console.error('Failed to log bulk upload error:', error);
    }
}

// Show bulk upload errors modal
function showBulkUploadErrors(batchId) {
    // This will be implemented when we add the UI components
    fetch(`/api/bulk-errors/batch/${batchId}`)
        .then(response => response.json())
        .then(errors => {
            displayBulkUploadErrorsModal(errors);
        })
        .catch(error => {
            console.error('Error fetching bulk upload errors:', error);
            showErrorMessage('Failed to load error details');
        });
}

function readFileAsText(file) {
    return new Promise((resolve, reject) => {
        const reader = new FileReader();
        reader.onload = function (e) {
            resolve(e.target.result);
        };
        reader.onerror = function (e) {
            reject(e.target.error);
        };
        reader.readAsText(file);
    });
}

function parseCSV(text) {
    const rows = [];
    const lines = text.split('\n');

    for (let line of lines) {
        if (line.trim()) {
            const row = [];
            let current = '';
            let inQuotes = false;

            for (let i = 0; i < line.length; i++) {
                const char = line[i];

                if (char === '"') {
                    inQuotes = !inQuotes;
                } else if (char === ',' && !inQuotes) {
                    row.push(current);
                    current = '';
                } else {
                    current += char;
                }
            }
            row.push(current);
            rows.push(row);
        }
    }

    return rows;
}

// Download template function
function downloadTemplate() {
    if (!currentBulkType || !csvFormats[currentBulkType]) {
        console.error('No bulk type selected or template not found');
        return;
    }
    
    // Get the CSV content from the csvFormats object
    const csvContent = csvFormats[currentBulkType];
    
    // Create and download the file
    const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
    const link = document.createElement('a');
    
    if (link.download !== undefined) {
        const url = URL.createObjectURL(blob);
        link.setAttribute('href', url);
        link.setAttribute('download', `${currentBulkType}_template.csv`);
        link.style.visibility = 'hidden';
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
        URL.revokeObjectURL(url); // Clean up the URL object
    } else {
        // Fallback for older browsers
        window.open('data:text/csv;charset=utf-8,' + encodeURIComponent(csvContent));
    }
    
    console.log(`Downloaded ${currentBulkType} template`);
}

// Search and Filter Functions for Media Management
function filterMediaTable() {
    const searchInput = document.getElementById('mediaSearch');
    const typeFilter = document.getElementById('mediaTypeFilter');
    const languageFilter = document.getElementById('mediaLanguageFilter');
    const tbody = document.getElementById('media-table');
    const rows = tbody.querySelectorAll('tr');
    const resultsElement = document.getElementById('mediaSearchResults');
    
    if (!searchInput || !tbody || !rows.length) return;
    
    const searchText = searchInput.value.toLowerCase().trim();
    const typeValue = typeFilter ? typeFilter.value.toLowerCase() : '';
    const languageValue = languageFilter ? languageFilter.value.toLowerCase() : '';
    
    let visibleCount = 0;
    
    rows.forEach(row => {
        if (row.classList.contains('no-results-row')) {
            row.remove();
            return;
        }
        
        const cells = row.querySelectorAll('td');
        if (cells.length === 0) return;
        
        let shouldShow = true;
        
        // Apply search text filter (searches across all visible text in the row)
        if (searchText) {
            const rowText = Array.from(cells)
                .slice(0, -1) // Exclude the actions column
                .map(cell => cell.textContent.toLowerCase())
                .join(' ');
            
            if (!rowText.includes(searchText)) {
                shouldShow = false;
            }
        }
        
        // Apply type filter (column 1 - Type)
        if (shouldShow && typeValue && cells.length > 1) {
            const typeText = cells[1].textContent.toLowerCase();
            if (!typeText.includes(typeValue)) {
                shouldShow = false;
            }
        }
        
        // Apply language filter (column 2 - Language)
        if (shouldShow && languageValue && cells.length > 2) {
            const languageText = cells[2].textContent.toLowerCase();
            if (!languageText.includes(languageValue)) {
                shouldShow = false;
            }
        }
        
        // Show/hide row
        if (shouldShow) {
            row.style.display = '';
            visibleCount++;
        } else {
            row.style.display = 'none';
        }
    });
    
    // Update results counter
    if (resultsElement) {
        const totalRows = rows.length;
        const hasFilters = searchText || typeValue || languageValue;
        
        if (hasFilters) {
            resultsElement.textContent = `Showing ${visibleCount} of ${totalRows} results`;
            
            // Show "no results" message if needed
            if (visibleCount === 0 && !document.querySelector('.no-results-row')) {
                showNoResultsMessage(tbody);
            }
        } else {
            resultsElement.textContent = `Showing all ${totalRows} results`;
        }
    }
}

function clearMediaFilters() {
    // Clear all filter inputs
    const searchInput = document.getElementById('mediaSearch');
    const typeFilter = document.getElementById('mediaTypeFilter');
    const languageFilter = document.getElementById('mediaLanguageFilter');
    
    if (searchInput) searchInput.value = '';
    if (typeFilter) typeFilter.value = '';
    if (languageFilter) languageFilter.value = '';
    
    // Remove any "no results" rows
    const noResultsRows = document.querySelectorAll('.no-results-row');
    noResultsRows.forEach(row => row.remove());
    
    // Re-filter the table (which will show all rows)
    filterMediaTable();
}

function showNoResultsMessage(tbody) {
    // Remove any existing "no results" row
    const existingNoResults = tbody.querySelector('.no-results-row');
    if (existingNoResults) {
        existingNoResults.remove();
    }
    
    // Create and add "no results" row
    const noResultsRow = document.createElement('tr');
    noResultsRow.className = 'no-results-row';
    noResultsRow.innerHTML = `
        <td colspan="6" class="px-6 py-12 text-center">
            <div class="flex flex-col items-center justify-center">
                <svg class="w-12 h-12 text-gray-400 mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"></path>
                </svg>
                <h6 class="text-lg font-medium text-gray-900 mb-2">No results found</h6>
                <p class="text-gray-500 mb-4">Try adjusting your search terms or filters</p>
                <button class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 focus:ring-2 focus:ring-blue-500" onclick="clearMediaFilters()">
                    Clear All Filters
                </button>
            </div>
        </td>
    `;
    tbody.appendChild(noResultsRow);
}

// Search and Filter Functions for Content Management
function filterContentTable() {
    const searchInput = document.getElementById('contentSearch');
    const typeFilter = document.getElementById('contentTypeFilter');
    const statusFilter = document.getElementById('contentStatusFilter');
    const priorityFilter = document.getElementById('contentPriorityFilter');
    const localStatusFilter = document.getElementById('contentLocalStatusFilter');
    const tbody = document.getElementById('content-table');
    const rows = tbody.querySelectorAll('tr');
    const resultsElement = document.getElementById('contentSearchResults');
    
    if (!searchInput || !tbody || !rows.length) return;
    
    const searchText = searchInput.value.toLowerCase().trim();
    const typeValue = typeFilter ? typeFilter.value.toLowerCase() : '';
    const statusValue = statusFilter ? statusFilter.value.toLowerCase() : '';
    const priorityValue = priorityFilter ? priorityFilter.value.toLowerCase() : '';
    const localStatusValue = localStatusFilter ? localStatusFilter.value.toLowerCase() : '';
    
    let visibleCount = 0;
    
    rows.forEach(row => {
        if (row.classList.contains('no-results-row')) {
            row.remove();
            return;
        }
        
        const cells = row.querySelectorAll('td');
        if (cells.length === 0) return;
        
        let shouldShow = true;
        
        // Apply search text filter (searches across all visible text in the row)
        if (searchText) {
            const rowText = Array.from(cells)
                .slice(0, -1) // Exclude the actions column
                .map(cell => cell.textContent.toLowerCase())
                .join(' ');
            
            if (!rowText.includes(searchText)) {
                shouldShow = false;
            }
        }
        
        // Apply type filter (column 1 - Media Type)
        if (shouldShow && typeValue && cells.length > 1) {
            const typeText = cells[1].textContent.toLowerCase();
            if (!typeText.includes(typeValue)) {
                shouldShow = false;
            }
        }
        
        // Apply status filter (column 3 - Status)
        if (shouldShow && statusValue && cells.length > 3) {
            const statusText = cells[3].textContent.toLowerCase();
            if (!statusText.includes(statusValue)) {
                shouldShow = false;
            }
        }
        
        // Apply priority filter (column 4 - Priority)
        if (shouldShow && priorityValue && cells.length > 4) {
            const priorityText = cells[4].textContent.toLowerCase();
            if (!priorityText.includes(priorityValue)) {
                shouldShow = false;
            }
        }
        
        // Apply local status filter (column 5 - Local Status)
        if (shouldShow && localStatusValue && cells.length > 5) {
            const localStatusText = cells[5].textContent.toLowerCase();
            if (!localStatusText.includes(localStatusValue)) {
                shouldShow = false;
            }
        }
        
        // Show/hide row
        if (shouldShow) {
            row.style.display = '';
            visibleCount++;
        } else {
            row.style.display = 'none';
        }
    });
    
    // Update results counter
    if (resultsElement) {
        const totalRows = rows.length;
        const hasFilters = searchText || typeValue || statusValue || priorityValue || localStatusValue;
        
        if (hasFilters) {
            resultsElement.textContent = `Showing ${visibleCount} of ${totalRows} results`;
            
            // Show "no results" message if needed
            if (visibleCount === 0 && !document.querySelector('.no-results-row')) {
                showNoContentResultsMessage(tbody);
            }
        } else {
            resultsElement.textContent = `Showing all ${totalRows} results`;
        }
    }
}

function clearContentFilters() {
    // Clear all filter inputs
    const searchInput = document.getElementById('contentSearch');
    const typeFilter = document.getElementById('contentTypeFilter');
    const statusFilter = document.getElementById('contentStatusFilter');
    const priorityFilter = document.getElementById('contentPriorityFilter');
    const localStatusFilter = document.getElementById('contentLocalStatusFilter');
    
    if (searchInput) searchInput.value = '';
    if (typeFilter) typeFilter.value = '';
    if (statusFilter) statusFilter.value = '';
    if (priorityFilter) priorityFilter.value = '';
    if (localStatusFilter) localStatusFilter.value = '';
    
    // Remove any "no results" rows
    const noResultsRows = document.querySelectorAll('.no-results-row');
    noResultsRows.forEach(row => row.remove());
    
    // Re-filter the table (which will show all rows)
    filterContentTable();
}

function showNoContentResultsMessage(tbody) {
    // Remove any existing "no results" row
    const existingNoResults = tbody.querySelector('.no-results-row');
    if (existingNoResults) {
        existingNoResults.remove();
    }
    
    // Create and add "no results" row
    const noResultsRow = document.createElement('tr');
    noResultsRow.className = 'no-results-row';
    noResultsRow.innerHTML = `
        <td colspan="8" class="px-6 py-12 text-center">
            <div class="flex flex-col items-center justify-center">
                <svg class="w-12 h-12 text-gray-400 mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"></path>
                </svg>
                <h6 class="text-lg font-medium text-gray-900 mb-2">No content found</h6>
                <p class="text-gray-500 mb-4">Try adjusting your search terms or filters</p>
                <button class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 focus:ring-2 focus:ring-blue-500" onclick="clearContentFilters()">
                    Clear All Filters
                </button>
            </div>
        </td>
    `;
    tbody.appendChild(noResultsRow);
}

// Search and Filter Functions for Upload Management
function filterUploadTable() {
    const searchInput = document.getElementById('uploadSearch');
    const statusFilter = document.getElementById('uploadStatusFilter');
    const mediaTypeFilter = document.getElementById('uploadMediaTypeFilter');
    const mediaFormatFilter = document.getElementById('uploadMediaFormatFilter');
    const tbody = document.getElementById('upload-table');
    const rows = tbody.querySelectorAll('tr');
    const resultsElement = document.getElementById('uploadSearchResults');
    
    if (!searchInput || !tbody || !rows.length) return;
    
    const searchText = searchInput.value.toLowerCase().trim();
    const statusValue = statusFilter ? statusFilter.value.toLowerCase() : '';
    const mediaTypeValue = mediaTypeFilter ? mediaTypeFilter.value.toLowerCase() : '';
    const mediaFormatValue = mediaFormatFilter ? mediaFormatFilter.value.toLowerCase() : '';
    
    let visibleCount = 0;
    
    rows.forEach(row => {
        if (row.classList.contains('no-results-row')) {
            row.remove();
            return;
        }
        
        const cells = row.querySelectorAll('td');
        if (cells.length === 0) return;
        
        let shouldShow = true;
        
        // Apply search text filter (searches across all visible text in the row)
        if (searchText) {
            const rowText = Array.from(cells)
                .slice(0, -1) // Exclude the actions column
                .map(cell => cell.textContent.toLowerCase())
                .join(' ');
            
            if (!rowText.includes(searchText)) {
                shouldShow = false;
            }
        }
        
        // Apply status filter (column 3 - Status)
        if (shouldShow && statusValue && cells.length > 3) {
            const statusText = cells[3].textContent.toLowerCase();
            if (!statusText.includes(statusValue)) {
                shouldShow = false;
            }
        }
        
        // Apply media type filter (look in media name column and source data for type info)
        if (shouldShow && mediaTypeValue) {
            const mediaNameText = cells.length > 1 ? cells[1].textContent.toLowerCase() : '';
            const sourceDataText = cells.length > 2 ? cells[2].textContent.toLowerCase() : '';
            const rowText = `${mediaNameText} ${sourceDataText}`;
            
            if (!rowText.includes(mediaTypeValue)) {
                shouldShow = false;
            }
        }
        
        // Apply media format filter (column 4 - Media Data)
        if (shouldShow && mediaFormatValue && cells.length > 4) {
            const mediaDataText = cells[4].textContent.toLowerCase();
            if (!mediaDataText.includes(mediaFormatValue)) {
                shouldShow = false;
            }
        }
        
        // Show/hide row
        if (shouldShow) {
            row.style.display = '';
            visibleCount++;
        } else {
            row.style.display = 'none';
        }
    });
    
    // Update results counter
    if (resultsElement) {
        const totalRows = rows.length;
        const hasFilters = searchText || statusValue || mediaTypeValue || mediaFormatValue;
        
        if (hasFilters) {
            resultsElement.textContent = `Showing ${visibleCount} of ${totalRows} results`;
            
            // Show "no results" message if needed
            if (visibleCount === 0 && !document.querySelector('.no-results-row')) {
                showNoUploadResultsMessage(tbody);
            }
        } else {
            resultsElement.textContent = `Showing all ${totalRows} results`;
        }
    }
}

function clearUploadFilters() {
    // Clear all filter inputs
    const searchInput = document.getElementById('uploadSearch');
    const statusFilter = document.getElementById('uploadStatusFilter');
    const mediaTypeFilter = document.getElementById('uploadMediaTypeFilter');
    const mediaFormatFilter = document.getElementById('uploadMediaFormatFilter');
    
    if (searchInput) searchInput.value = '';
    if (statusFilter) statusFilter.value = '';
    if (mediaTypeFilter) mediaTypeFilter.value = '';
    if (mediaFormatFilter) mediaFormatFilter.value = '';
    
    // Remove any "no results" rows
    const noResultsRows = document.querySelectorAll('.no-results-row');
    noResultsRows.forEach(row => row.remove());
    
    // Re-filter the table (which will show all rows)
    filterUploadTable();
}

function showNoUploadResultsMessage(tbody) {
    // Remove any existing "no results" row
    const existingNoResults = tbody.querySelector('.no-results-row');
    if (existingNoResults) {
        existingNoResults.remove();
    }
    
    // Create and add "no results" row
    const noResultsRow = document.createElement('tr');
    noResultsRow.className = 'no-results-row';
    noResultsRow.innerHTML = `
        <td colspan="6" class="px-6 py-12 text-center">
            <div class="flex flex-col items-center justify-center">
                <svg class="w-12 h-12 text-gray-400 mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"></path>
                </svg>
                <h6 class="text-lg font-medium text-gray-900 mb-2">No uploads found</h6>
                <p class="text-gray-500 mb-4">Try adjusting your search terms or filters</p>
                <button class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 focus:ring-2 focus:ring-blue-500" onclick="clearUploadFilters()">
                    Clear All Filters
                </button>
            </div>
        </td>
    `;
    tbody.appendChild(noResultsRow);
}

// ==================== PAGINATION FUNCTIONALITY ====================

// Update pagination data when data is loaded
function updatePaginationData(type, data) {
    paginationData[type].allData = data || [];
    paginationData[type].totalItems = data ? data.length : 0;
    paginationData[type].currentPage = 1; // Reset to first page when data is reloaded
}

// Get paginated data for current page
function getPaginatedData(type) {
    const pageData = paginationData[type];
    const startIndex = (pageData.currentPage - 1) * pageData.pageSize;
    const endIndex = startIndex + pageData.pageSize;
    return pageData.allData.slice(startIndex, endIndex);
}

// Update pagination controls UI
function updatePaginationControls(type) {
    const pageData = paginationData[type];
    const totalPages = Math.ceil(pageData.totalItems / pageData.pageSize);
    
    // Update pagination info text
    const startItem = pageData.totalItems === 0 ? 0 : ((pageData.currentPage - 1) * pageData.pageSize) + 1;
    const endItem = Math.min(pageData.currentPage * pageData.pageSize, pageData.totalItems);
    document.getElementById(`${type}-pagination-info`).textContent = 
        `Showing ${startItem} to ${endItem} of ${pageData.totalItems} entries`;
    
    // Update button states
    const firstBtn = document.getElementById(`${type}-first-btn`);
    const prevBtn = document.getElementById(`${type}-prev-btn`);
    const nextBtn = document.getElementById(`${type}-next-btn`);
    const lastBtn = document.getElementById(`${type}-last-btn`);
    
    if (firstBtn && prevBtn && nextBtn && lastBtn) {
        firstBtn.disabled = pageData.currentPage === 1;
        prevBtn.disabled = pageData.currentPage === 1;
        nextBtn.disabled = pageData.currentPage === totalPages || totalPages === 0;
        lastBtn.disabled = pageData.currentPage === totalPages || totalPages === 0;
    }
    
    // Update page numbers
    updatePageNumbers(type, totalPages);
}

// Update page number buttons
function updatePageNumbers(type, totalPages) {
    const pageNumbersContainer = document.getElementById(`${type}-page-numbers`);
    if (!pageNumbersContainer) return;
    
    pageNumbersContainer.innerHTML = '';
    
    if (totalPages <= 1) return;
    
    const currentPage = paginationData[type].currentPage;
    const maxVisiblePages = 5;
    
    let startPage = Math.max(1, currentPage - Math.floor(maxVisiblePages / 2));
    let endPage = Math.min(totalPages, startPage + maxVisiblePages - 1);
    
    // Adjust start page if we're near the end
    if (endPage - startPage < maxVisiblePages - 1) {
        startPage = Math.max(1, endPage - maxVisiblePages + 1);
    }
    
    for (let i = startPage; i <= endPage; i++) {
        const pageBtn = document.createElement('button');
        pageBtn.textContent = i;
        pageBtn.className = i === currentPage 
            ? 'px-3 py-1 text-sm bg-blue-600 text-white border border-blue-600 rounded'
            : 'px-3 py-1 text-sm border border-gray-300 rounded hover:bg-gray-50';
        pageBtn.onclick = () => goToPage(type, i);
        pageNumbersContainer.appendChild(pageBtn);
    }
}

// Navigation functions
function goToPage(type, page) {
    const totalPages = Math.ceil(paginationData[type].totalItems / paginationData[type].pageSize);
    
    if (page === 'last') {
        page = totalPages;
    }
    
    if (page >= 1 && page <= totalPages) {
        paginationData[type].currentPage = page;
        renderPaginatedTable(type);
        updatePaginationControls(type);
    }
}

function previousPage(type) {
    if (paginationData[type].currentPage > 1) {
        paginationData[type].currentPage--;
        renderPaginatedTable(type);
        updatePaginationControls(type);
    }
}

function nextPage(type) {
    const totalPages = Math.ceil(paginationData[type].totalItems / paginationData[type].pageSize);
    if (paginationData[type].currentPage < totalPages) {
        paginationData[type].currentPage++;
        renderPaginatedTable(type);
        updatePaginationControls(type);
    }
}

function changePageSize(type) {
    const pageSizeSelect = document.getElementById(`${type}-page-size`);
    if (pageSizeSelect) {
        paginationData[type].pageSize = parseInt(pageSizeSelect.value);
        paginationData[type].currentPage = 1; // Reset to first page
        renderPaginatedTable(type);
        updatePaginationControls(type);
    }
}

// Render table with pagination
function renderPaginatedTable(type) {
    const paginatedData = getPaginatedData(type);
    renderTable(type, paginatedData);
}

// ==================== RESPONSIVE DESIGN ENHANCEMENTS ====================

// Handle window resize for responsive design
window.addEventListener('resize', function() {
    // Update pagination controls layout on resize
    updateAllPaginationControls();
});

function updateAllPaginationControls() {
    ['media', 'content', 'upload', 'states', 'errors'].forEach(type => {
        if (paginationData[type].totalItems > 0) {
            updatePaginationControls(type);
        }
    });
}

// ==================== BULK UPLOAD ERROR FUNCTIONS ====================

// View error details
function viewErrorDetails(errorId) {
    fetch(`/api/bulk-errors/${errorId}`)
        .then(response => response.json())
        .then(error => {
            displayErrorDetailsModal(error);
        })
        .catch(err => {
            console.error('Error fetching error details:', err);
            showErrorMessage('Failed to load error details');
        });
}

// Resolve an error
function resolveError(errorId) {
    const resolutionNotes = prompt('Enter resolution notes (optional):');
    if (resolutionNotes !== null) { // User didn't cancel
        fetch(`/api/bulk-errors/${errorId}/resolve`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                resolution_notes: resolutionNotes || 'Marked as resolved'
            })
        })
        .then(response => response.json())
        .then(() => {
            showSuccessMessage('Error marked as resolved');
            loadData('errors');
        })
        .catch(err => {
            console.error('Error resolving error:', err);
            showErrorMessage('Failed to resolve error');
        });
    }
}

// Delete an error
function deleteError(errorId) {
    if (confirm('Are you sure you want to delete this error record?')) {
        fetch(`/api/bulk-errors/${errorId}`, {
            method: 'DELETE'
        })
        .then(() => {
            showSuccessMessage('Error deleted successfully');
            loadData('errors');
        })
        .catch(err => {
            console.error('Error deleting error:', err);
            showErrorMessage('Failed to delete error');
        });
    }
}

// Display error details modal
function displayErrorDetailsModal(error) {
    const modal = document.createElement('div');
    modal.className = 'modal active fixed inset-0 bg-gray-600 bg-opacity-50 overflow-y-auto h-full w-full z-50';
    modal.innerHTML = `
        <div class="relative top-20 mx-auto p-5 border w-11/12 md:w-3/4 lg:w-1/2 shadow-lg rounded-md bg-white">
            <div class="mt-3">
                <div class="flex items-center justify-between mb-4">
                    <h3 class="text-lg font-medium text-gray-900">Error Details</h3>
                    <button onclick="closeErrorModal()" class="text-gray-400 hover:text-gray-600">
                        <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path>
                        </svg>
                    </button>
                </div>
                
                <div class="space-y-4">
                    <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                        <div>
                            <label class="block text-sm font-medium text-gray-700">Upload Type</label>
                            <p class="mt-1 text-sm text-gray-900">${error.upload_type || 'N/A'}</p>
                        </div>
                        <div>
                            <label class="block text-sm font-medium text-gray-700">Batch ID</label>
                            <p class="mt-1 text-sm text-gray-900 font-mono">${error.batch_id || 'N/A'}</p>
                        </div>
                        <div>
                            <label class="block text-sm font-medium text-gray-700">Row Number</label>
                            <p class="mt-1 text-sm text-gray-900">${error.row_number || 'N/A'}</p>
                        </div>
                        <div>
                            <label class="block text-sm font-medium text-gray-700">Error Type</label>
                            <p class="mt-1 text-sm text-gray-900">${error.error_type || 'N/A'}</p>
                        </div>
                    </div>
                    
                    <div>
                        <label class="block text-sm font-medium text-gray-700">Error Message</label>
                        <p class="mt-1 text-sm text-gray-900 bg-red-50 p-3 rounded-lg border border-red-200">${error.error_message || 'N/A'}</p>
                    </div>
                    
                    ${error.field_name ? `
                        <div>
                            <label class="block text-sm font-medium text-gray-700">Field Name</label>
                            <p class="mt-1 text-sm text-gray-900">${error.field_name}</p>
                        </div>
                    ` : ''}
                    
                    ${error.attempted_value ? `
                        <div>
                            <label class="block text-sm font-medium text-gray-700">Attempted Value</label>
                            <p class="mt-1 text-sm text-gray-900 bg-gray-50 p-2 rounded border">${error.attempted_value}</p>
                        </div>
                    ` : ''}
                    
                    <div>
                        <label class="block text-sm font-medium text-gray-700">Raw Data</label>
                        <pre class="mt-1 text-sm text-gray-900 bg-gray-50 p-3 rounded-lg border overflow-x-auto">${error.raw_data || 'N/A'}</pre>
                    </div>
                    
                    ${error.suggestions ? `
                        <div>
                            <label class="block text-sm font-medium text-gray-700">Suggestions</label>
                            <p class="mt-1 text-sm text-gray-900 bg-blue-50 p-3 rounded-lg border border-blue-200">${error.suggestions}</p>
                        </div>
                    ` : ''}
                    
                    <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                        <div>
                            <label class="block text-sm font-medium text-gray-700">Status</label>
                            <p class="mt-1">
                                ${error.is_resolved ? 
                                    '<span class="px-2 py-1 text-xs font-semibold bg-green-100 text-green-800 rounded-full">Resolved</span>' : 
                                    '<span class="px-2 py-1 text-xs font-semibold bg-red-100 text-red-800 rounded-full">Unresolved</span>'
                                }
                            </p>
                        </div>
                        <div>
                            <label class="block text-sm font-medium text-gray-700">Created At</label>
                            <p class="mt-1 text-sm text-gray-900">${error.created_at ? new Date(error.created_at).toLocaleString() : 'N/A'}</p>
                        </div>
                    </div>
                    
                    ${error.resolution_notes ? `
                        <div>
                            <label class="block text-sm font-medium text-gray-700">Resolution Notes</label>
                            <p class="mt-1 text-sm text-gray-900 bg-green-50 p-3 rounded-lg border border-green-200">${error.resolution_notes}</p>
                        </div>
                    ` : ''}
                </div>
                
                <div class="flex justify-end space-x-3 mt-6">
                    ${!error.is_resolved ? `
                        <button onclick="resolveError(${error.id}); closeErrorModal();" 
                                class="bg-green-600 hover:bg-green-700 text-white px-4 py-2 rounded-lg font-medium">
                            Mark as Resolved
                        </button>
                    ` : ''}
                    <button onclick="closeErrorModal()" 
                            class="bg-gray-600 hover:bg-gray-700 text-white px-4 py-2 rounded-lg font-medium">
                        Close
                    </button>
                </div>
            </div>
        </div>
    `;
    
    document.body.appendChild(modal);
}

// Close error modal
function closeErrorModal() {
    const modal = document.querySelector('.modal.active');
    if (modal) {
        document.body.removeChild(modal);
    }
}

// Show bulk upload errors modal from batch
function displayBulkUploadErrorsModal(errors) {
    if (!errors || errors.length === 0) {
        showErrorMessage('No errors found for this batch');
        return;
    }

    const modal = document.createElement('div');
    modal.className = 'modal active fixed inset-0 bg-gray-600 bg-opacity-50 overflow-y-auto h-full w-full z-50';
    
    const errorRows = errors.map(error => `
        <tr class="border-b">
            <td class="px-4 py-3 text-sm">${error.row_number}</td>
            <td class="px-4 py-3 text-sm">${error.error_type}</td>
            <td class="px-4 py-3 text-sm max-w-xs truncate" title="${error.error_message}">${error.error_message}</td>
            <td class="px-4 py-3 text-sm">
                <button onclick="viewErrorDetails(${error.id})" class="text-blue-600 hover:text-blue-800">View</button>
            </td>
        </tr>
    `).join('');

    modal.innerHTML = `
        <div class="relative top-10 mx-auto p-5 border w-11/12 md:w-4/5 lg:w-3/4 shadow-lg rounded-md bg-white max-h-screen overflow-y-auto">
            <div class="mt-3">
                <div class="flex items-center justify-between mb-4">
                    <h3 class="text-lg font-medium text-gray-900">Bulk Upload Errors - Batch: ${errors[0].batch_id}</h3>
                    <button onclick="closeErrorModal()" class="text-gray-400 hover:text-gray-600">
                        <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path>
                        </svg>
                    </button>
                </div>
                
                <div class="bg-red-50 border border-red-200 rounded-lg p-4 mb-4">
                    <h4 class="text-red-800 font-medium">Found ${errors.length} error(s) in this batch</h4>
                    <p class="text-red-700 text-sm mt-1">Review each error and take appropriate action.</p>
                </div>
                
                <div class="overflow-x-auto">
                    <table class="min-w-full divide-y divide-gray-200">
                        <thead class="bg-gray-50">
                            <tr>
                                <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">Row #</th>
                                <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">Error Type</th>
                                <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">Error Message</th>
                                <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">Actions</th>
                            </tr>
                        </thead>
                        <tbody class="bg-white divide-y divide-gray-200">
                            ${errorRows}
                        </tbody>
                    </table>
                </div>
                
                <div class="flex justify-end mt-6">
                    <button onclick="closeErrorModal()" 
                            class="bg-gray-600 hover:bg-gray-700 text-white px-4 py-2 rounded-lg font-medium">
                        Close
                    </button>
                </div>
            </div>
        </div>
    `;
    
    document.body.appendChild(modal);
}

// Show error statistics
function showErrorStatistics() {
    fetch('/api/bulk-errors/statistics')
        .then(response => response.json())
        .then(stats => {
            displayErrorStatisticsModal(stats);
        })
        .catch(err => {
            console.error('Error fetching statistics:', err);
            showErrorMessage('Failed to load error statistics');
        });
}

// Display error statistics modal
function displayErrorStatisticsModal(stats) {
    const modal = document.createElement('div');
    modal.className = 'modal active fixed inset-0 bg-gray-600 bg-opacity-50 overflow-y-auto h-full w-full z-50';
    modal.innerHTML = `
        <div class="relative top-20 mx-auto p-5 border w-11/12 md:w-2/3 lg:w-1/2 shadow-lg rounded-md bg-white">
            <div class="mt-3">
                <div class="flex items-center justify-between mb-4">
                    <h3 class="text-lg font-medium text-gray-900">Bulk Upload Error Statistics</h3>
                    <button onclick="closeErrorModal()" class="text-gray-400 hover:text-gray-600">
                        <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path>
                        </svg>
                    </button>
                </div>
                
                <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
                    <div class="bg-blue-50 p-4 rounded-lg">
                        <h4 class="text-blue-800 font-medium mb-3">Overall Statistics</h4>
                        <div class="space-y-2">
                            <div class="flex justify-between">
                                <span class="text-blue-700">Total Errors:</span>
                                <span class="font-medium">${stats.totalErrors || 0}</span>
                            </div>
                            <div class="flex justify-between">
                                <span class="text-red-700">Unresolved:</span>
                                <span class="font-medium text-red-600">${stats.unresolvedErrors || 0}</span>
                            </div>
                        </div>
                    </div>
                    
                    <div class="bg-green-50 p-4 rounded-lg">
                        <h4 class="text-green-800 font-medium mb-3">By Upload Type</h4>
                        <div class="space-y-2">
                            <div class="flex justify-between">
                                <span class="text-green-700">Media Errors:</span>
                                <span class="font-medium">${stats.mediaErrors || 0} (${stats.unresolvedMediaErrors || 0} unresolved)</span>
                            </div>
                            <div class="flex justify-between">
                                <span class="text-green-700">Content Errors:</span>
                                <span class="font-medium">${stats.contentErrors || 0} (${stats.unresolvedContentErrors || 0} unresolved)</span>
                            </div>
                            <div class="flex justify-between">
                                <span class="text-green-700">Upload Errors:</span>
                                <span class="font-medium">${stats.uploadErrors || 0} (${stats.unresolvedUploadErrors || 0} unresolved)</span>
                            </div>
                        </div>
                    </div>
                </div>
                
                <div class="flex justify-end mt-6">
                    <button onclick="closeErrorModal()" 
                            class="bg-gray-600 hover:bg-gray-700 text-white px-4 py-2 rounded-lg font-medium">
                        Close
                    </button>
                </div>
            </div>
        </div>
    `;
    
    document.body.appendChild(modal);
}

// Clear resolved errors
function clearResolvedErrors() {
    if (confirm('Are you sure you want to delete all resolved errors? This action cannot be undone.')) {
        // Since we don't have a bulk delete endpoint, we'll need to add one or delete individually
        showErrorMessage('Bulk delete feature not yet implemented');
    }
}

// Filter error table (placeholder - you can implement filtering logic)
function filterErrorTable() {
    // Implement filtering logic similar to other tables
    console.log('Error table filtering not yet implemented');
}

// Clear error filters (placeholder)
function clearErrorFilters() {
    document.getElementById('errorUploadTypeFilter').value = '';
    document.getElementById('errorTypeFilter').value = '';
    document.getElementById('errorStatusFilter').value = '';
    document.getElementById('errorBatchFilter').value = '';
    
    // Reload data
    loadData('errors');
}

