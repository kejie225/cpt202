document.addEventListener('DOMContentLoaded', () => {
    // Check authentication
    const token = localStorage.getItem('token');
    const username = localStorage.getItem('username');
    
    if (!token || !username) {
        window.location.href = '/login.html';
        return;
    }

    // Display username
    document.getElementById('usernameDisplay').textContent = username;

    // Load recent activity
    loadRecentActivity();

    // 新增：加载推荐音乐、我的收藏、公告
    loadRecommendedMusic();
    loadFavoriteMusic();
    loadAnnouncements();

    // 新增：上传音乐表单提交
    const uploadForm = document.getElementById('uploadMusicForm');
    if (uploadForm) {
        uploadForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            const formData = new FormData(uploadForm);
            document.getElementById('uploadStatus').textContent = 'Uploading...';
            try {
                const response = await fetch('/api/music/upload', {
                    method: 'POST',
                    headers: {
                        'Authorization': `Bearer ${localStorage.getItem('token')}`
                    },
                    body: formData
                });
                if (response.ok) {
                    document.getElementById('uploadStatus').textContent = 'Upload successful!';
                    uploadForm.reset();
                } else {
                    document.getElementById('uploadStatus').textContent = 'Upload failed!';
                }
            } catch (error) {
                document.getElementById('uploadStatus').textContent = 'Error uploading!';
            }
        });
    }

    // Setup logout button
    document.getElementById('logoutBtn').addEventListener('click', () => {
        localStorage.removeItem('token');
        localStorage.removeItem('username');
        window.location.href = '/login.html';
    });
});

// Load recent activity
async function loadRecentActivity() {
    try {
        const response = await fetch('/api/music/recent', {
            headers: {
                'Authorization': `Bearer ${localStorage.getItem('token')}`
            }
        });

        if (response.ok) {
            const activities = await response.json();
            displayActivities(activities);
        } else {
            throw new Error('Failed to load recent activity');
        }
    } catch (error) {
        console.error('Error:', error);
        document.getElementById('recentActivity').innerHTML = `
            <div class="list-group-item">
                <div class="d-flex w-100 justify-content-between">
                    <h6 class="mb-1">No recent activity</h6>
                </div>
            </div>
        `;
    }
}

// Display activities
function displayActivities(activities) {
    const activityList = document.getElementById('recentActivity');
    activityList.innerHTML = '';

    if (activities.length === 0) {
        activityList.innerHTML = `
            <div class="list-group-item">
                <div class="d-flex w-100 justify-content-between">
                    <h6 class="mb-1">No recent activity</h6>
                </div>
            </div>
        `;
        return;
    }

    activities.forEach(activity => {
        const item = document.createElement('div');
        item.className = 'list-group-item';
        item.innerHTML = `
            <div class="d-flex w-100 justify-content-between">
                <h6 class="mb-1">${activity.title}</h6>
                <small class="text-muted">${formatDate(activity.timestamp)}</small>
            </div>
            <p class="mb-1">${activity.description}</p>
        `;
        activityList.appendChild(item);
    });
}

// Format date
function formatDate(timestamp) {
    const date = new Date(timestamp);
    return date.toLocaleDateString() + ' ' + date.toLocaleTimeString();
}

// 新增：加载推荐音乐
async function loadRecommendedMusic() {
    try {
        const response = await fetch('/api/music/recommend', {
            headers: {
                'Authorization': `Bearer ${localStorage.getItem('token')}`
            }
        });
        const container = document.getElementById('recommendedMusic');
        if (response.ok) {
            const musicList = await response.json();
            if (musicList.length === 0) {
                container.innerHTML = '<p>No recommendations yet.</p>';
            } else {
                container.innerHTML = musicList.map(m => `<div><b>${m.title}</b> - ${m.artist}</div>`).join('');
            }
        } else {
            container.innerHTML = '<p>Failed to load recommendations.</p>';
        }
    } catch {
        document.getElementById('recommendedMusic').innerHTML = '<p>Error loading recommendations.</p>';
    }
}

// 新增：加载我的收藏
async function loadFavoriteMusic() {
    try {
        const response = await fetch('/api/music/favorites', {
            headers: {
                'Authorization': `Bearer ${localStorage.getItem('token')}`
            }
        });
        const container = document.getElementById('favoriteMusic');
        if (response.ok) {
            const musicList = await response.json();
            if (musicList.length === 0) {
                container.innerHTML = '<p>No favorites yet.</p>';
            } else {
                container.innerHTML = musicList.map(m => `<div><b>${m.title}</b> - ${m.artist}</div>`).join('');
            }
        } else {
            container.innerHTML = '<p>Failed to load favorites.</p>';
        }
    } catch {
        document.getElementById('favoriteMusic').innerHTML = '<p>Error loading favorites.</p>';
    }
}

// 新增：加载公告
async function loadAnnouncements() {
    try {
        const response = await fetch('/api/announcements', {
            headers: {
                'Authorization': `Bearer ${localStorage.getItem('token')}`
            }
        });
        const container = document.getElementById('announcements');
        if (response.ok) {
            const list = await response.json();
            if (list.length === 0) {
                container.innerHTML = '<p>No announcements.</p>';
            } else {
                container.innerHTML = list.map(a => `<div><b>${a.title}</b><br><small>${formatDate(a.date)}</small><p>${a.content}</p></div>`).join('<hr>');
            }
        } else {
            container.innerHTML = '<p>Failed to load announcements.</p>';
        }
    } catch {
        document.getElementById('announcements').innerHTML = '<p>Error loading announcements.</p>';
    }
} 