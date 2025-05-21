// script.js
document.getElementById('profile-update-form').addEventListener('submit', function (e) {
    e.preventDefault();

    let valid = true;

    // Reset error messages
    document.getElementById('avatar-error').textContent = '';
    document.getElementById('email-error').textContent = '';
    document.getElementById('gender-error').textContent = '';

    const avatarFile = document.getElementById('avatar').files[0];
    const email = document.getElementById('email').value;
    const gender = document.getElementById('gender').value;

    // 1. Avatar validation (JPG/PNG, ≤ 2MB)
    if (avatarFile) {
        const fileSize = avatarFile.size / 1024 / 1024; // Convert size to MB
        const fileType = avatarFile.type;
        if (fileSize > 2) {
            document.getElementById('avatar-error').textContent = 'File size must be less than 2MB.';
            valid = false;
        } else if (fileType !== 'image/jpeg' && fileType !== 'image/png') {
            document.getElementById('avatar-error').textContent = 'Only JPG/PNG formats are supported.';
            valid = false;
        }
    }

    // 2. Email validation
    const emailPattern = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
    if (!emailPattern.test(email)) {
        document.getElementById('email-error').textContent = 'Mailbox format does not meet the specification.';
        valid = false;
    }

    // 3. Gender validation
    if (!gender) {
        document.getElementById('gender-error').textContent = 'Please select a valid option from the list.';
        valid = false;
    }

    // 4. Check if email is already registered (simulate backend check)
    const registeredEmails = ['existingemail@example.com', 'anotheremail@example.com']; // Example list of existing emails
    if (registeredEmails.includes(email)) {
        document.getElementById('email-error').textContent = 'This email has been registered, please change another address.';
        valid = false;
    }

    // If all validations pass
    if (valid) {
        // Simulate backend request
        document.getElementById('toast-message').textContent = 'Data has been successfully updated';

        // Simulate saving the changes and resetting the form
        document.getElementById('profile-update-form').reset();
        setTimeout(() => {
            document.getElementById('toast-message').textContent = '';
        }, 3000); // Hide the toast message after 3 seconds
    } else {
        // Focus on the first error field
        document.querySelector('.error-message').scrollIntoView({ behavior: 'smooth' });
    }
});