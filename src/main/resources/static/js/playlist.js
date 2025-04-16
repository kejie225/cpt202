document.addEventListener('DOMContentLoaded', () => {
    const token = localStorage.getItem('token');
    const username = localStorage.getItem('username');
    if (!token || !username) {
        window.location.href = '/login.html';
        return;
    }
    document.getElementById('usernameDisplay').textContent = username;
    document.getElementById('logoutBtn').addEventListener('click', () => {
        localStorage.removeItem('token');
        localStorage.removeItem('username');
        window.location.href = '/login.html';
    });
    loadHistory();
    loadFavorites();
    loadMyPlaylists();

    // 创建歌单按钮
    document.getElementById('createPlaylistBtn').addEventListener('click', () => {
        const modal = new bootstrap.Modal(document.getElementById('createPlaylistModal'));
        modal.show();
    });

    // 创建歌单表单提交
    document.getElementById('createPlaylistForm').addEventListener('submit', async (e) => {
        e.preventDefault();
        const name = document.getElementById('playlistName').value.trim();
        if (!name) return;
        try {
            const res = await fetch('/api/playlist/create', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                },
                body: JSON.stringify({ name, username })
            });
            if (res.ok) {
                loadMyPlaylists();
                bootstrap.Modal.getInstance(document.getElementById('createPlaylistModal')).hide();
                showToast('Playlist created successfully', 'success');
            } else {
                showToast('Failed to create playlist', 'error');
            }
        } catch {
            showToast('Error creating playlist', 'error');
        }
    });
});

function renderMusicList(list, containerId) {
    const container = document.getElementById(containerId);
    if (!list || list.length === 0) {
        container.innerHTML = '<p class="text-muted">No data available.</p>';
        return;
    }
    container.innerHTML = list.map(m => `
        <div class="d-flex justify-content-between align-items-center mb-2">
            <div>
                <b>${m.title}</b> <span class="text-secondary">- ${m.artist}</span>
            </div>
            <button class="btn btn-sm btn-outline-primary" onclick="playMusic(${m.id})">Play</button>
        </div>
    `).join('');
}

function playMusic(id) {
    window.location.href = `/music.html?musicId=${id}`;
}

async function loadHistory() {
    try {
        const res = await fetch('/api/music/history', {
            headers: { 'Authorization': `Bearer ${localStorage.getItem('token')}` }
        });
        if (res.ok) {
            const list = await res.json();
            renderMusicList(list, 'historyList');
        } else {
            document.getElementById('historyList').innerHTML = '<p class="text-danger">Failed to load history.</p>';
        }
    } catch {
        document.getElementById('historyList').innerHTML = '<p class="text-danger">Error loading history.</p>';
    }
}

async function loadFavorites() {
    try {
        const res = await fetch('/api/music/favorites', {
            headers: { 'Authorization': `Bearer ${localStorage.getItem('token')}` }
        });
        if (res.ok) {
            const list = await res.json();
            renderMusicList(list, 'favoriteList');
        } else {
            document.getElementById('favoriteList').innerHTML = '<p class="text-danger">Failed to load favorites.</p>';
        }
    } catch {
        document.getElementById('favoriteList').innerHTML = '<p class="text-danger">Error loading favorites.</p>';
    }
}

async function loadMyPlaylists() {
    try {
        const res = await fetch(`/api/playlist/my?username=${encodeURIComponent(localStorage.getItem('username'))}`, {
            headers: { 'Authorization': `Bearer ${localStorage.getItem('token')}` }
        });
        if (res.ok) {
            const list = await res.json();
            const container = document.getElementById('myPlaylists');
            if (!list || list.length === 0) {
                container.innerHTML = '<p class="text-muted">No playlists yet.</p>';
            } else {
                container.innerHTML = list.map(p => `
                    <div class="d-flex justify-content-between align-items-center mb-2">
                        <div><b>${p.name}</b></div>
                        <div>
                            <button class="btn btn-sm btn-outline-info me-2" onclick="viewPlaylist(${p.id})">View</button>
                            <button class="btn btn-sm btn-outline-danger" onclick="deletePlaylist(${p.id})">Delete</button>
                        </div>
                    </div>
                `).join('');
            }
        } else {
            document.getElementById('myPlaylists').innerHTML = '<p class="text-danger">Failed to load playlists.</p>';
        }
    } catch {
        document.getElementById('myPlaylists').innerHTML = '<p class="text-danger">Error loading playlists.</p>';
    }
}

function viewPlaylist(id) {
    window.location.href = `/playlist_detail.html?playlistId=${id}`;
}

async function deletePlaylist(id) {
    if (!confirm('Are you sure you want to delete this playlist?')) return;
    try {
        const res = await fetch(`/api/playlist/${id}`, {
            method: 'DELETE',
            headers: { 'Authorization': `Bearer ${localStorage.getItem('token')}` }
        });
        if (res.ok) {
            loadMyPlaylists();
            showToast('Playlist deleted successfully', 'success');
        } else {
            showToast('Failed to delete playlist', 'error');
        }
    } catch {
        showToast('Error deleting playlist', 'error');
    }
}

function showToast(message, type = 'info') {
    const toast = document.getElementById('toast');
    const toastMessage = document.getElementById('toastMessage');
    
    toastMessage.textContent = message;
    toast.classList.remove('bg-success', 'bg-danger', 'bg-info');
    
    switch (type) {
        case 'success':
            toast.classList.add('bg-success');
            break;
        case 'error':
            toast.classList.add('bg-danger');
            break;
        default:
            toast.classList.add('bg-info');
    }
    
    bootstrap.Toast.getInstance(toast).show();
} 