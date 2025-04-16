document.getElementById('loginForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    
    const username = document.getElementById('username').value;
    const password = document.getElementById('password').value;

    try {
        const response = await fetch('/api/login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                username: username,
                password: password
            })
        });

        if (response.ok) {
            const data = await response.json();
            // Store user info in localStorage
            localStorage.setItem('token', data.token);
            localStorage.setItem('username', data.username);
            // Redirect to home page
            window.location.href = '/home.html';
        } else {
            const errorData = await response.json();
            alert('Login failed: ' + (errorData.message || 'Unknown error'));
        }
    } catch (error) {
        console.error('Error:', error);
        alert('An error occurred during login. Please try again.');
    }
}); 