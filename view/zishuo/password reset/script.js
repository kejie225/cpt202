// script.js
document.getElementById('reset-request-form').addEventListener('submit', function(e) {
    e.preventDefault();
    const email = document.getElementById('email').value;

    if (email) {
        // 调用后端API
        fetch('http://localhost:8080/api/reset-password', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ email: email }),
            credentials: 'include' // 允许发送cookies
        })
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.json();
        })
        .then(data => {
            document.getElementById('response-message').textContent = 'A reset link has been sent to your email!';
        })
        .catch(error => {
            console.error('Error:', error);
            document.getElementById('response-message').textContent = 'Failed to send reset link. Please try again.';
        });
    } else {
        document.getElementById('response-message').textContent = 'Please enter a valid email address.';
    }
});

document.getElementById('password-reset-form').addEventListener('submit', function(e) {
    e.preventDefault();

    const newPassword = document.getElementById('new-password').value;
    const confirmPassword = document.getElementById('confirm-password').value;
    const errorMessage = document.getElementById('error-message');

    // Check for password complexity
    const passwordPattern = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)[A-Za-z\d]{8,}$/;

    if (newPassword !== confirmPassword) {
        errorMessage.textContent = 'Passwords do not match.';
    } else if (!passwordPattern.test(newPassword)) {
        errorMessage.textContent = 'Password must be at least 8 characters, including uppercase, lowercase, and a number.';
    } else {
        errorMessage.textContent = '';
        
        // 调用后端API重置密码
        fetch('http://localhost:8080/api/update-password', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ 
                newPassword: newPassword,
                confirmPassword: confirmPassword
            }),
            credentials: 'include'
        })
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.json();
        })
        .then(data => {
            alert('Password updated successfully!');
            // 可以在这里添加重定向逻辑
            // window.location.href = '/login';
        })
        .catch(error => {
            console.error('Error:', error);
            errorMessage.textContent = 'Failed to update password. Please try again.';
        });
    }
});