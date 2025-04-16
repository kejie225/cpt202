let currentPlaylist = [];
let currentTrackIndex = -1;
let audioPlayer = new Audio();
let isPlaying = false;
let isRepeat = false;
let isShuffle = false;

// Check authentication
document.addEventListener('DOMContentLoaded', () => {
    const token = localStorage.getItem('token');
    const username = localStorage.getItem('username');
    
    if (!token || !username) {
        window.location.href = '/login.html';
        return;
    }

    document.getElementById('usernameDisplay').textContent = username;
    loadPlaylist();
    setupEventListeners();
});

// Setup event listeners
function setupEventListeners() {
    // Play/Pause button
    document.getElementById('playBtn').addEventListener('click', togglePlay);
    
    // Previous button
    document.getElementById('prevBtn').addEventListener('click', playPrevious);
    
    // Next button
    document.getElementById('nextBtn').addEventListener('click', playNext);
    
    // Repeat button
    document.getElementById('repeatBtn').addEventListener('click', toggleRepeat);
    
    // Shuffle button
    document.getElementById('shuffleBtn').addEventListener('click', toggleShuffle);
    
    // Progress bar click
    document.querySelector('.progress').addEventListener('click', seek);
    
    // Audio events
    audioPlayer.addEventListener('timeupdate', updateProgress);
    audioPlayer.addEventListener('ended', handleTrackEnd);
    audioPlayer.addEventListener('loadedmetadata', updateDuration);
}

// Load playlist from server
async function loadPlaylist() {
    try {
        const response = await fetch('/api/music', {
            headers: {
                'Authorization': `Bearer ${localStorage.getItem('token')}`
            }
        });

        if (response.ok) {
            currentPlaylist = await response.json();
            displayPlaylist();
        } else {
            throw new Error('Failed to load playlist');
        }
    } catch (error) {
        console.error('Error:', error);
        showError('Failed to load playlist');
    }
}

// Display playlist
function displayPlaylist() {
    const playlistElement = document.getElementById('playlist');
    playlistElement.innerHTML = '';

    currentPlaylist.forEach((music, index) => {
        const item = document.createElement('div');
        item.className = 'list-group-item';
        item.innerHTML = `
            <div class="d-flex justify-content-between align-items-center">
                <div>
                    <h6 class="mb-0">${music.title}</h6>
                    <small class="text-muted">${music.artist}</small>
                </div>
                <div class="d-flex align-items-center">
                    <small class="me-3">${music.duration}</small>
                    <button class="btn btn-sm btn-outline-primary">
                        <i class="fas fa-heart"></i>
                    </button>
                </div>
            </div>
        `;
        item.addEventListener('click', () => playTrack(index));
        playlistElement.appendChild(item);
    });
}

// Play track
function playTrack(index) {
    if (index < 0 || index >= currentPlaylist.length) return;

    currentTrackIndex = index;
    const track = currentPlaylist[index];
    
    document.getElementById('songTitle').textContent = track.title;
    document.getElementById('artistName').textContent = track.artist;
    
    audioPlayer.src = track.filePath;
    audioPlayer.play();
    isPlaying = true;
    updatePlayButton();
    
    // Update active track in playlist
    document.querySelectorAll('.list-group-item').forEach((item, i) => {
        item.classList.toggle('active', i === index);
    });
}

// Toggle play/pause
function togglePlay() {
    if (currentTrackIndex === -1 && currentPlaylist.length > 0) {
        playTrack(0);
    } else if (isPlaying) {
        audioPlayer.pause();
        isPlaying = false;
    } else {
        audioPlayer.play();
        isPlaying = true;
    }
    updatePlayButton();
}

// Play previous track
function playPrevious() {
    if (currentTrackIndex > 0) {
        playTrack(currentTrackIndex - 1);
    }
}

// Play next track
function playNext() {
    if (isShuffle) {
        playRandomTrack();
    } else if (currentTrackIndex < currentPlaylist.length - 1) {
        playTrack(currentTrackIndex + 1);
    } else if (isRepeat) {
        playTrack(0);
    }
}

// Play random track
function playRandomTrack() {
    const randomIndex = Math.floor(Math.random() * currentPlaylist.length);
    playTrack(randomIndex);
}

// Toggle repeat
function toggleRepeat() {
    isRepeat = !isRepeat;
    document.getElementById('repeatBtn').classList.toggle('active', isRepeat);
}

// Toggle shuffle
function toggleShuffle() {
    isShuffle = !isShuffle;
    document.getElementById('shuffleBtn').classList.toggle('active', isShuffle);
}

// Update progress bar
function updateProgress() {
    const progress = (audioPlayer.currentTime / audioPlayer.duration) * 100;
    document.getElementById('progressBar').style.width = `${progress}%`;
    document.getElementById('currentTime').textContent = formatTime(audioPlayer.currentTime);
}

// Update duration
function updateDuration() {
    document.getElementById('totalTime').textContent = formatTime(audioPlayer.duration);
}

// Format time
function formatTime(seconds) {
    const minutes = Math.floor(seconds / 60);
    const remainingSeconds = Math.floor(seconds % 60);
    return `${minutes}:${remainingSeconds.toString().padStart(2, '0')}`;
}

// Seek in track
function seek(e) {
    const progress = e.target;
    const rect = progress.getBoundingClientRect();
    const x = e.clientX - rect.left;
    const percentage = x / rect.width;
    audioPlayer.currentTime = percentage * audioPlayer.duration;
}

// Handle track end
function handleTrackEnd() {
    if (isRepeat) {
        playTrack(currentTrackIndex);
    } else {
        playNext();
    }
}

// Update play button icon
function updatePlayButton() {
    const playBtn = document.getElementById('playBtn');
    const icon = playBtn.querySelector('i');
    icon.className = isPlaying ? 'fas fa-pause' : 'fas fa-play';
}

// Show error message
function showError(message) {
    // You can implement a more sophisticated error display
    alert(message);
}

// Logout
document.getElementById('logoutBtn').addEventListener('click', () => {
    localStorage.removeItem('token');
    localStorage.removeItem('username');
    window.location.href = '/login.html';
}); 