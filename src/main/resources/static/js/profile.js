document.addEventListener('DOMContentLoaded', function() {
    // Load user profile
    loadUserProfile();

    // Handle avatar change
    document.getElementById('changeAvatarBtn').addEventListener('click', function() {
        document.getElementById('avatarInput').click();
    });

    document.getElementById('avatarInput').addEventListener('change', function(e) {
        if (e.target.files.length > 0) {
            const formData = new FormData();
            formData.append('file', e.target.files[0]);

            fetch('/api/profile/avatar', {
                method: 'POST',
                body: formData
            })
            .then(response => response.json())
            .then(data => {
                document.getElementById('avatar').src = data.avatarUrl;
                showToast('Avatar updated successfully');
            })
            .catch(error => {
                console.error('Error:', error);
                showToast('Failed to update avatar', 'error');
            });
        }
    });

    // Handle profile form submission
    document.getElementById('profileForm').addEventListener('submit', function(e) {
        e.preventDefault();

        const profileData = {
            username: document.getElementById('username').value,
            email: document.getElementById('email').value,
            phone: document.getElementById('phone').value,
            gender: document.getElementById('gender').value,
            birthday: document.getElementById('birthday').value,
            region: document.getElementById('region').value,
            signature: document.getElementById('signature').value
        };

        fetch('/api/profile', {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(profileData)
        })
        .then(response => response.json())
        .then(data => {
            showToast('Profile updated successfully');
            loadUserProfile();
        })
        .catch(error => {
            console.error('Error:', error);
            showToast('Failed to update profile', 'error');
        });
    });
});

function loadUserProfile() {
    fetch('/api/profile')
        .then(response => response.json())
        .then(userData => {
            document.getElementById('username').textContent = userData.username;
            document.getElementById('signature').textContent = userData.signature || '';
            document.getElementById('avatar').src = userData.avatarUrl || '/images/default-avatar.png';

            // Fill form fields
            document.getElementById('username').value = userData.username || '';
            document.getElementById('email').value = userData.email || '';
            document.getElementById('phone').value = userData.phone || '';
            document.getElementById('gender').value = userData.gender || '男';
            document.getElementById('birthday').value = userData.birthday || '';
            document.getElementById('region').value = userData.region || '';
            document.getElementById('signature').value = userData.signature || '';
        })
        .catch(error => {
            console.error('Error:', error);
            showToast('Failed to load profile', 'error');
        });
}

function showToast(message, type = 'success') {
    const toast = document.createElement('div');
    toast.className = `toast align-items-center text-white bg-${type === 'success' ? 'success' : 'danger'} border-0`;
    toast.setAttribute('role', 'alert');
    toast.setAttribute('aria-live', 'assertive');
    toast.setAttribute('aria-atomic', 'true');

    toast.innerHTML = `
        <div class="d-flex">
            <div class="toast-body">
                ${message}
            </div>
            <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast" aria-label="Close"></button>
        </div>
    `;

    const container = document.createElement('div');
    container.className = 'toast-container position-fixed bottom-0 end-0 p-3';
    container.appendChild(toast);
    document.body.appendChild(container);

    const bsToast = new bootstrap.Toast(toast);
    bsToast.show();

    toast.addEventListener('hidden.bs.toast', function() {
        container.remove();
    });
} 