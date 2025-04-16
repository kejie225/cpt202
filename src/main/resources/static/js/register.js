document.getElementById('signupForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    
    // Clear previous error messages
    document.getElementById('username-error').textContent = '';
    document.getElementById('email-error').textContent = '';
    document.getElementById('confirm-error').textContent = '';
    document.getElementById('terms-error').textContent = '';
    
    const username = document.getElementById('username').value;
    const email = document.getElementById('email').value;
    const password = document.getElementById('password').value;
    const confirmPassword = document.getElementById('confirmPassword').value;
    
    // Validate username
    if (!username || username.trim() === '') {
        document.getElementById('username-error').textContent = 'Username cannot be empty';
        return;
    }
    
    // Validate email
    if (!email || email.trim() === '') {
        document.getElementById('email-error').textContent = 'Email cannot be empty';
        return;
    }
    
    // Validate email format
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(email)) {
        document.getElementById('email-error').textContent = 'Please enter a valid email address';
        return;
    }
    
    // Validate password match
    if (password !== confirmPassword) {
        document.getElementById('confirm-error').textContent = 'Passwords do not match';
        return;
    }
    
    // Validate terms acceptance
    if (!document.getElementById('terms').checked) {
        document.getElementById('terms-error').textContent = 'You must accept the terms and conditions';
        return;
    }
    
    try {
        const response = await fetch('/api/register', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                username: username,
                email: email,
                password: password
            })
        });
        
        if (response.ok) {
            alert('Registration successful! Please login.');
            window.location.href = '/login.html';
        } else {
            const error = await response.text();
            document.getElementById('username-error').textContent = error;
        }
    } catch (error) {
        console.error('Error:', error);
        document.getElementById('username-error').textContent = 'An error occurred during registration';
    }
});

// Password strength indicator
document.getElementById('password').addEventListener('input', function(e) {
    const password = e.target.value;
    const strengthDiv = document.getElementById('password-strength');
    
    // Reset strength indicator
    strengthDiv.textContent = '';
    strengthDiv.className = 'password-strength';
    
    if (password.length === 0) return;
    
    let strength = 0;
    if (password.length >= 8) strength++;
    if (password.match(/[a-z]/)) strength++;
    if (password.match(/[A-Z]/)) strength++;
    if (password.match(/[0-9]/)) strength++;
    if (password.match(/[^a-zA-Z0-9]/)) strength++;
    
    const strengthText = ['Very Weak', 'Weak', 'Medium', 'Strong', 'Very Strong'];
    const strengthClass = ['very-weak', 'weak', 'medium', 'strong', 'very-strong'];
    
    strengthDiv.textContent = `Password Strength: ${strengthText[strength - 1]}`;
    strengthDiv.classList.add(strengthClass[strength - 1]);
}); 